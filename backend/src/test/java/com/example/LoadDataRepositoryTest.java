package com.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class LoadDataRepositoryTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private LoadDataRepository repository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindLoadDataWithoutZoneFilter() {
        LocalDateTime start = LocalDateTime.of(2024, 7, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 7, 31, 23, 59);

        List<DataPoint> mockResult = List.of(
                new DataPoint("ZoneA", start.plusHours(1), 100.0),
                new DataPoint("ZoneB", start.plusHours(2), 150.0)
        );

        when(jdbcTemplate.query(
                anyString(),
                any(Object[].class),
                ArgumentMatchers.<RowMapper<DataPoint>>any()))
            .thenReturn(mockResult);

        List<DataPoint> results = repository.findLoadData(start, end, "ALL");

        assertEquals(2, results.size());
        assertEquals("ZoneA", results.get(0).getZone());
        assertEquals(100.0, results.get(0).getLoad());

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(jdbcTemplate).query(sqlCaptor.capture(), any(Object[].class), any(RowMapper.class));
        assertTrue(sqlCaptor.getValue().contains("WHERE timestamp BETWEEN"));
        assertFalse(sqlCaptor.getValue().toLowerCase().contains("and zone = ?"));
    }

    @Test
    void testFindLoadDataWithZoneFilter() {
        LocalDateTime start = LocalDateTime.of(2024, 7, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 7, 31, 23, 59);
        String zone = "ZoneA";

        List<DataPoint> mockResult = List.of(
                new DataPoint(zone, start.plusHours(1), 120.0)
        );

        when(jdbcTemplate.query(
                anyString(),
                any(Object[].class),
                ArgumentMatchers.<RowMapper<DataPoint>>any()))
            .thenReturn(mockResult);

        List<DataPoint> results = repository.findLoadData(start, end, zone);

        assertEquals(1, results.size());
        assertEquals(zone, results.get(0).getZone());

        ArgumentCaptor<Object[]> paramsCaptor = ArgumentCaptor.forClass(Object[].class);
        verify(jdbcTemplate).query(anyString(), paramsCaptor.capture(), any(RowMapper.class));

        Object[] params = paramsCaptor.getValue();
        assertEquals(3, params.length);
        assertEquals(Timestamp.valueOf(start), params[0]);
        assertEquals(Timestamp.valueOf(end), params[1]);
        assertEquals(zone, params[2]);
    }

    @Test
    void testFindCurrentAndLastYearLoadDataWithoutZone() {
        LocalDate start = LocalDate.of(2024, 7, 1);
        LocalDate end = LocalDate.of(2024, 7, 31);

        List<AggregatedDataPoint> currentYearMock = List.of(
                new AggregatedDataPoint("ZoneA", start.atStartOfDay(), 110.0)
        );

        List<AggregatedDataPoint> lastYearMock = List.of(
                new AggregatedDataPoint("ZoneA", start.minusYears(1).atStartOfDay(), 105.0)
        );

        when(jdbcTemplate.query(
                contains("WHERE timestamp BETWEEN"),
                any(Object[].class),
                ArgumentMatchers.<RowMapper<AggregatedDataPoint>>any()))
            .thenReturn(currentYearMock)
            .thenReturn(lastYearMock);

        Map<String, List<AggregatedDataPoint>> results = repository.findCurrentAndLastYearLoadData(start, end, "ALL");

        assertNotNull(results);
        assertTrue(results.containsKey("currentYear"));
        assertTrue(results.containsKey("lastYear"));
        assertEquals(1, results.get("currentYear").size());
        assertEquals(1, results.get("lastYear").size());

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(jdbcTemplate, times(2)).query(sqlCaptor.capture(), any(Object[].class), any(RowMapper.class));
        String sql = sqlCaptor.getAllValues().get(0);
        assertFalse(sql.toLowerCase().contains("and zone = ?"));
    }

    @Test
    void testFindCurrentAndLastYearLoadDataWithZone() {
        LocalDate start = LocalDate.of(2024, 7, 1);
        LocalDate end = LocalDate.of(2024, 7, 31);
        String zone = "ZoneA";

        List<AggregatedDataPoint> currentYearMock = List.of(
                new AggregatedDataPoint(zone, start.atStartOfDay(), 120.0)
        );

        List<AggregatedDataPoint> lastYearMock = List.of(
                new AggregatedDataPoint(zone, start.minusYears(1).atStartOfDay(), 115.0)
        );

        when(jdbcTemplate.query(
                contains("WHERE timestamp BETWEEN"),
                any(Object[].class),
                ArgumentMatchers.<RowMapper<AggregatedDataPoint>>any()))
            .thenReturn(currentYearMock)
            .thenReturn(lastYearMock);

        Map<String, List<AggregatedDataPoint>> results = repository.findCurrentAndLastYearLoadData(start, end, zone);

        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals(1, results.get("currentYear").size());
        assertEquals(zone, results.get("currentYear").get(0).getZone());

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(jdbcTemplate, times(2)).query(sqlCaptor.capture(), any(Object[].class), any(RowMapper.class));
        String sql = sqlCaptor.getAllValues().get(0);
        assertTrue(sql.toLowerCase().contains("and zone = ?"));
    }
}
