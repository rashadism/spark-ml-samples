package com.lohika.morning.ml.spark.driver.service.lyrics;

public enum Genre {

    POP("Pop 🎧", 0D, "pop"),
    COUNTRY("Country 🤠", 1D, "country"),
    BLUES("Blues 🎶", 2D, "blues"),
    JAZZ("Jazz 🎷", 3D, "jazz"),
    REGGAE("Reggae 🌴", 4D, "reggae"),
    ROCK("Rock 🎸", 5D, "rock"),
    HIPHOP("Hip Hop 🎤", 6D, "hip hop"),
    GOSPEL("Gospel 🎹", 7D, "gospel"),
    UNKNOWN("Don't know :(", -1D, "unknown");

    private final String name;
    private final Double value;
    private final String code;

    Genre(final String name, final Double value, String code) {
        this.name = name;
        this.value = value;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public Double getValue() {
        return value;
    }

    public String getCode() {
        return code;
    }
}
