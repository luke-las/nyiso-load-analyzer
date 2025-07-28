package com.example;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AggregatedDataPointTest {

    @Test
    void testConstructorAndGetters() {
        LocalDateTime date = LocalDateTime.of(2025, 7, 28, 0, 0);
        AggregatedDataPoint point = new AggregatedDataPoint("Zone1", date, 123.45);

        assertEquals("Zone1", point.getZone());
        assertEquals(date, point.getDay());
        assertEquals(123.45, point.getAvgLoad());
    }

    @Test
    void testSetters() {
        AggregatedDataPoint point = new AggregatedDataPoint("Zone1", LocalDateTime.now(), 0.0);

        point.setZone("Zone2");
        point.setDay(LocalDateTime.of(2024, 12, 25, 10, 0));
        point.setAvgLoad(987.65);

        assertEquals("Zone2", point.getZone());
        assertEquals(LocalDateTime.of(2024, 12, 25, 10, 0), point.getDay());
        assertEquals(987.65, point.getAvgLoad());
    }

    @Test
    void testNullZone() {
        AggregatedDataPoint point = new AggregatedDataPoint(null, LocalDateTime.now(), 10.0);
        assertNull(point.getZone());
    }

    @Test
    void testNegativeAvgLoad() {
        AggregatedDataPoint point = new AggregatedDataPoint("ZoneX", LocalDateTime.now(), -50.0);
        assertEquals(-50.0, point.getAvgLoad());
    }
}
