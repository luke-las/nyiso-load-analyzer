package com.example;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class DataAnalyzer {

    // Group all data points by zone
    public static Map<String, List<DataPoint>> groupByZone(List<DataPoint> data) {
        return data.stream()
                .collect(Collectors.groupingBy(DataPoint::getZone));
    }
    
    // Filter by zone (case-insensitive)
    public static List<DataPoint> filterByZone(List<DataPoint> data, String zone) {
        return data.stream()
                .filter(dp -> dp.getZone().equalsIgnoreCase(zone))
                .collect(Collectors.toList());
    }

    // Filter by timestamp range (inclusive)
    public static List<DataPoint> filterByTimeRange(List<DataPoint> data, LocalDateTime start, LocalDateTime end) {
        return data.stream()
                .filter(dp -> !dp.getTimestamp().isBefore(start) && !dp.getTimestamp().isAfter(end))
                .collect(Collectors.toList());
    }
}
