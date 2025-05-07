package com.lohika.morning.ml.api.service;

import com.lohika.morning.ml.spark.driver.service.lyrics.GenrePrediction;
import com.lohika.morning.ml.spark.driver.service.lyrics.pipeline.LogisticRegressionPipeline;

import java.util.Map;
import javax.annotation.Resource;
import org.apache.spark.ml.tuning.TrainValidationSplitModel;
import org.springframework.stereotype.Component;

@Component
public class LyricsService {

    @Resource(name = "${lyrics.pipeline}")
    private LogisticRegressionPipeline pipeline;

    public Map<String, Object> classifyLyrics() {
        TrainValidationSplitModel model = pipeline.classify();
        return pipeline.getModelStatistics(model);
    }

    public GenrePrediction predictGenre(final String unknownLyrics, String dataset) {
        return pipeline.predict(unknownLyrics);
    }

}
