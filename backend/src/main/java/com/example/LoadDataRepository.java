package com.example;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class LoadDataRepository {

    private final JdbcTemplate jdbcTemplate;

    public LoadDataRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<DataPoint> findLoadData(LocalDateTime start, LocalDateTime end, String zone) {
        String sql = "SELECT zone, timestamp, load_value FROM load_data WHERE timestamp BETWEEN ? AND ?";
        List<Object> params = new ArrayList<>();
        params.add(Timestamp.valueOf(start));
        params.add(Timestamp.valueOf(end));

        if (!"ALL".equalsIgnoreCase(zone)) {
            sql += " AND zone = ?";
            params.add(zone);
        }

        System.out.println("Executing SQL: " + sql);
        System.out.println("With parameters: " + params);

        List<DataPoint> results = jdbcTemplate.query(sql, params.toArray(), (rs, rowNum) ->
                new DataPoint(
                        rs.getString("zone"),
                        rs.getTimestamp("timestamp").toLocalDateTime(),
                        rs.getDouble("load_value")
                ));

        System.out.println("Retrieved " + results.size() + " records from the database.");
        return results; 
    }
    public Map<String, List<AggregatedDataPoint>> findCurrentAndLastYearLoadData(LocalDate start, LocalDate end, String zone) {
        LocalDate lastYearStart = start.minusYears(1);
        LocalDate lastYearEnd = end.minusYears(1);

        String sql = "SELECT zone, date_trunc('day', timestamp) AS day, AVG(load_value) AS avg_load " +
                    "FROM load_data WHERE timestamp BETWEEN ? AND ?";

        List<Object> paramsCurrent = new ArrayList<>(List.of(Timestamp.valueOf(start.atStartOfDay()), Timestamp.valueOf(end.atTime(23,59,59))));
        List<Object> paramsLastYear = new ArrayList<>(List.of(Timestamp.valueOf(lastYearStart.atStartOfDay()), Timestamp.valueOf(lastYearEnd.atTime(23,59,59))));

        if (!"ALL".equalsIgnoreCase(zone)) {
            sql += " AND zone = ?";
            paramsCurrent.add(zone);
            paramsLastYear.add(zone);
        }

        sql += " GROUP BY zone, day ORDER BY day";

        List<AggregatedDataPoint> currentYearData = jdbcTemplate.query(sql, paramsCurrent.toArray(), (rs, rowNum) -> 
            new AggregatedDataPoint(rs.getString("zone"), rs.getTimestamp("day").toLocalDateTime(), rs.getDouble("avg_load"))
        );

        List<AggregatedDataPoint> lastYearData = jdbcTemplate.query(sql, paramsLastYear.toArray(), (rs, rowNum) -> 
            new AggregatedDataPoint(rs.getString("zone"), rs.getTimestamp("day").toLocalDateTime(), rs.getDouble("avg_load"))
        );

        Map<String, List<AggregatedDataPoint>> result = new HashMap<>();
        result.put("currentYear", currentYearData);
        result.put("lastYear", lastYearData);

        return result;
    }

}
