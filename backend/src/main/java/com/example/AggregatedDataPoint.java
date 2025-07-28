package com.example;

import java.time.LocalDateTime;

public class AggregatedDataPoint {
    private String zone;
    private LocalDateTime day;
    private double avgLoad;

    public AggregatedDataPoint(String zone, LocalDateTime day, double avgLoad) {
        this.zone = zone;
        this.day = day;
        this.avgLoad = avgLoad;
    }

    public String getZone() {
        return zone;
    }

    public LocalDateTime getDay() {
        return day;
    }

    public double getAvgLoad() {
        return avgLoad;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public void setDay(LocalDateTime day) {
        this.day = day;
    }

    public void setAvgLoad(double avgLoad) {
        this.avgLoad = avgLoad;
    }
}
