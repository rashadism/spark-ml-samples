package com.lohika.morning.ml.spark.driver.service.lyrics.pipeline;

import java.util.Map;

import org.apache.spark.ml.tuning.TrainValidationSplitModel;

import com.lohika.morning.ml.spark.driver.service.lyrics.GenrePrediction;

public interface LyricsPipeline {

    TrainValidationSplitModel classify();

    GenrePrediction predict(String unknownLyrics);

    Map<String, Object> getModelStatistics(TrainValidationSplitModel model);

}
