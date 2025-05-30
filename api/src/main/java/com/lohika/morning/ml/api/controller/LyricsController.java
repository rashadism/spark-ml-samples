package com.lohika.morning.ml.api.controller;

import com.lohika.morning.ml.api.service.LyricsService;
import com.lohika.morning.ml.spark.driver.service.lyrics.GenrePrediction;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/lyrics")
public class LyricsController {

    @Autowired
    private LyricsService lyricsService;

    @RequestMapping(value = "/train", method = RequestMethod.GET)
    ResponseEntity<Map<String, Object>> trainLyricsModel(
            @RequestParam(defaultValue = "Mendeley_mini.csv", required = false) String dataset) {
        Map<String, Object> trainStatistics = lyricsService.classifyLyrics(dataset);

        return new ResponseEntity<>(trainStatistics, HttpStatus.OK);
    }

    @RequestMapping(value = "/predict", method = RequestMethod.POST)
    ResponseEntity<GenrePrediction> predictGenre(
            @RequestBody String unknownLyrics,
            @RequestParam(defaultValue = "Mendeley_mini.csv", required = false) String dataset) {

        GenrePrediction genrePrediction = lyricsService.predictGenre(unknownLyrics, dataset);

        return new ResponseEntity<>(genrePrediction, HttpStatus.OK);
    }

}
