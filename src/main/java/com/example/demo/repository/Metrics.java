package com.example.demo.repository;

import org.springframework.data.geo.Metric;

public enum Metrics implements Metric {
    KILOMETERS(6378.137, "km"), METERS(6378137, "m");

    private final double multiplier;
    private final String abbreviation;

    Metrics(double multiplier, String abbreviation) {

        this.multiplier = multiplier;
        this.abbreviation = abbreviation;
    }

    public double getMultiplier() {
        return multiplier;
    }

    @Override
    public String getAbbreviation() {
        return abbreviation;
    }
}
