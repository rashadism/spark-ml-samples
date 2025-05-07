package com.lohika.morning.ml.spark.driver.service.lyrics.pipeline;

import static com.lohika.morning.ml.spark.distributed.library.function.map.lyrics.Column.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.linalg.DenseVector;
import org.apache.spark.ml.tuning.TrainValidationSplitModel;
import org.apache.spark.sql.*;
import org.apache.spark.sql.api.java.UDF1;
import org.apache.spark.sql.types.DataTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.lohika.morning.ml.spark.driver.service.MLService;
import com.lohika.morning.ml.spark.driver.service.lyrics.Genre;
import com.lohika.morning.ml.spark.driver.service.lyrics.GenrePrediction;

public abstract class CommonLyricsPipeline {

    @Autowired
    protected SparkSession sparkSession;

    @Autowired
    private MLService mlService;

    @Value("${lyrics.training.set.directory.path}")
    private String lyricsTrainingSetDirectoryPath;

    @Value("${lyrics.model.directory.path}")
    private String lyricsModelDirectoryPath;

    public GenrePrediction predict(final String unknownLyrics, String dataset) {
        String lyrics[] = unknownLyrics.split("\\r?\\n");
        Dataset<String> lyricsDataset = sparkSession.createDataset(Arrays.asList(lyrics),
                Encoders.STRING());

        Dataset<Row> unknownLyricsDataset = lyricsDataset
                .withColumn(LABEL.getName(), functions.lit(Genre.UNKNOWN.getValue()))
                .withColumn(ID.getName(), functions.lit("unknown.txt"));

        Path modelPath = Paths.get(getModelDirectory(), dataset);
        TrainValidationSplitModel model = mlService.loadModel(modelPath.toString());
        getModelStatistics(model);

        PipelineModel bestModel = (PipelineModel) model.bestModel();

        // Get predictions from the model
        Dataset<Row> predictionsDataset = bestModel.transform(unknownLyricsDataset);
        Row predictionRow = predictionsDataset.first();

        System.out.println("\n------------------------------------------------");
        final Double prediction = predictionRow.getAs("prediction");
        System.out.println("Prediction: " + Double.toString(prediction));

        Map<String, Double> genreProbabilities = new HashMap<>();

        if (Arrays.asList(predictionsDataset.columns()).contains("probability")) {
            final DenseVector probability = predictionRow.getAs("probability");

            for (Genre genre : Genre.values()) {
                if (genre.getValue() < 0 || genre.getValue().intValue() >= probability.size()) {
                    continue;
                }
                genreProbabilities.put(genre.getCode(), probability.apply(genre.getValue().intValue()));
            }

            System.out.println("Probabilities: " + genreProbabilities);
            System.out.println("------------------------------------------------\n");

            return new GenrePrediction(getGenre(prediction).getName(), genreProbabilities);
        }

        System.out.println("------------------------------------------------\n");
        return new GenrePrediction(getGenre(prediction).getName());
    }

    Dataset<Row> readLyricsCSV(String dataset) {
        Path filePath = Paths.get(lyricsTrainingSetDirectoryPath, dataset + ".csv");
        Dataset<Row> csvData = sparkSession.read()
                .option("header", "true")
                .csv(filePath.toString());

        Dataset<Row> filtered = csvData
                .filter(functions.col("lyrics").isNotNull().and(functions.length(functions.col("lyrics")).gt(0)))
                .filter(functions.col("lyrics").contains(" "));

        Map<String, Double> genreToLabel = new HashMap<>();
        for (Genre genre : Genre.values()) {
            genreToLabel.put(genre.getCode(), genre.getValue());
        }

        UDF1<String, Double> mapGenreToLabel = (String genre) -> genreToLabel.getOrDefault(genre.toLowerCase(), -1.0);
        sparkSession.udf().register("mapGenreToLabel", mapGenreToLabel, DataTypes.DoubleType);

        Dataset<Row> processed = filtered
                .withColumn("value", functions.col("lyrics"))
                .withColumn("label", functions.callUDF("mapGenreToLabel", functions.col("genre")))
                .withColumn("id", functions.monotonically_increasing_id().cast(DataTypes.StringType))
                .select("value", "id", "label");

        processed = processed.coalesce(sparkSession.sparkContext().defaultMinPartitions()).cache();
        processed.count();
        System.out.println("hehehe");
        processed.groupBy("label").count().orderBy("label").show();

        return processed;
    }

    private Genre getGenre(Double value) {
        for (Genre genre : Genre.values()) {
            if (genre.getValue().equals(value)) {
                return genre;
            }
        }

        return Genre.UNKNOWN;
    }

    public Map<String, Object> getModelStatistics(TrainValidationSplitModel model) {
        Map<String, Object> modelStatistics = new HashMap<>();

        double[] validationMetrics = model.validationMetrics();
        Arrays.sort(validationMetrics);

        double bestMetric = validationMetrics[validationMetrics.length - 1];
        modelStatistics.put("Best model metrics", bestMetric);

        return modelStatistics;
    }

    void printModelStatistics(Map<String, Object> modelStatistics) {
        System.out.println("\n------------------------------------------------");
        System.out.println("Model statistics:");
        System.out.println(modelStatistics);
        System.out.println("------------------------------------------------\n");
    }

    void saveModel(TrainValidationSplitModel model, String modelOutputDirectory) {
        this.mlService.saveModel(model, modelOutputDirectory);
    }

    void saveModel(PipelineModel model, String modelOutputDirectory) {
        this.mlService.saveModel(model, modelOutputDirectory);
    }

    public void setLyricsTrainingSetDirectoryPath(String lyricsTrainingSetDirectoryPath) {
        this.lyricsTrainingSetDirectoryPath = lyricsTrainingSetDirectoryPath;
    }

    public void setLyricsModelDirectoryPath(String lyricsModelDirectoryPath) {
        this.lyricsModelDirectoryPath = lyricsModelDirectoryPath;
    }

    protected abstract String getModelDirectory();

    String getLyricsModelDirectoryPath() {
        return lyricsModelDirectoryPath;
    }

    public abstract TrainValidationSplitModel classify(String dataset);
}
