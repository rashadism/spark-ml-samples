package com.lohika.morning.ml.spark.driver.service.lyrics;

import java.util.Map;

public class GenrePrediction {

    private String genre;
    private Map<String, Double> genreProbabilities;

    // Constructor that takes a genre and a map of probabilities
    public GenrePrediction(String genre, Map<String, Double> genreProbabilities) {
        this.genre = genre;
        this.genreProbabilities = genreProbabilities;
    }

    // Constructor that takes only a genre if no probabilities are needed
    public GenrePrediction(String genre) {
        this.genre = genre;
    }

    public String getGenre() {
        return genre;
    }

    public Map<String, Double> getGenreProbabilities() {
        return genreProbabilities;
    }

    // Add a method to get probability for a specific genre
    public Double getProbabilityForGenre(String genreName) {
        return genreProbabilities.getOrDefault(genreName, null);
    }
}
