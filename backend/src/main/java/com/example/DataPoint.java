package com.example;

import java.time.LocalDateTime;

public class DataPoint {
    private final String zone;
    private final LocalDateTime timestamp;
    private final double load;

    public DataPoint(String zone, LocalDateTime timestamp, double load) {
        this.zone = zone;
        this.timestamp = timestamp;
        this.load = load;
    }

    public String getZone() {
        return zone;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public double getLoad() {
        return load;
    }
}
