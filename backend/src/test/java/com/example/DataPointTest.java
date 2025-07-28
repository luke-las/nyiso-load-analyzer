package com.example;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DataPointTest {

    @Test
    void constructorAndGetters_ShouldInitializeCorrectly() {
        String zone = "NYC";
        LocalDateTime timestamp = LocalDateTime.of(2023, 5, 1, 12, 0);
        double load = 1234.56;

        DataPoint dataPoint = new DataPoint(zone, timestamp, load);

        assertEquals(zone, dataPoint.getZone());
        assertEquals(timestamp, dataPoint.getTimestamp());
        assertEquals(load, dataPoint.getLoad(), 0.0001);
    }

    @Test
    void getZone_ShouldReturnCorrectZone() {
        DataPoint dataPoint = new DataPoint("WEST", LocalDateTime.now(), 800.0);
        assertEquals("WEST", dataPoint.getZone());
    }

    @Test
    void getTimestamp_ShouldReturnCorrectTimestamp() {
        LocalDateTime now = LocalDateTime.now();
        DataPoint dataPoint = new DataPoint("EAST", now, 200.0);
        assertEquals(now, dataPoint.getTimestamp());
    }

    @Test
    void getLoad_ShouldReturnCorrectLoad() {
        DataPoint dataPoint = new DataPoint("NORTH", LocalDateTime.now(), 99.99);
        assertEquals(99.99, dataPoint.getLoad(), 0.0001);
    }
}
