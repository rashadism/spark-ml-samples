package com.lohika.morning.ml.spark.driver.service;

import java.io.IOException;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.clustering.KMeans;
import org.apache.spark.ml.clustering.KMeansModel;
import org.apache.spark.ml.regression.LinearRegression;
import org.apache.spark.ml.regression.LinearRegressionModel;
import org.apache.spark.ml.regression.LinearRegressionTrainingSummary;
import org.apache.spark.ml.tuning.TrainValidationSplitModel;
import org.apache.spark.ml.util.MLWritable;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import scala.Tuple2;

@Component
public class MLService {

    public <T extends MLWritable> void saveModel(T model, String modelOutputDirectory) {
        try {
            model.write().overwrite().save(modelOutputDirectory);

            System.out.println("\n------------------------------------------------");
            System.out.println("Saved model to " + modelOutputDirectory);
            System.out.println("------------------------------------------------\n");
        } catch (IOException e) {
            throw new RuntimeException(String.format("Exception occurred while saving the model to disk. Details: %s",
                    e.getMessage()));
        }
    }

    public TrainValidationSplitModel loadModel(String modelDirectory) {
        TrainValidationSplitModel model = TrainValidationSplitModel.load(modelDirectory);

        System.out.println("\n------------------------------------------------");
        System.out.println("Loaded cross validation model from " + modelDirectory);
        System.out.println("------------------------------------------------\n");
        return model;
    }

    public PipelineModel loadPipelineModel(String modelDirectory) {
        PipelineModel model = PipelineModel.load(modelDirectory);

        System.out.println("\n------------------------------------------------");
        System.out.println("Loaded pipeline model from " + modelDirectory);
        System.out.println("------------------------------------------------\n");

        return model;
    }

}
