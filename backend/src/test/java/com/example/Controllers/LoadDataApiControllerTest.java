package com.example.Controllers;

import com.example.AggregatedDataPoint;
import com.example.DataPoint;
import com.example.LoadDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


class LoadDataApiControllerTest {

    private MockMvc mockMvc;

    @Mock
    private LoadDataRepository loadDataRepository;

    @InjectMocks
    private LoadDataApiController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testGetLoadData() throws Exception {
        LocalDateTime expectedStart = LocalDateTime.parse("2025-07-01T00:00:00");
        LocalDateTime expectedEnd = LocalDateTime.parse("2025-07-31T23:59:59");

        List<DataPoint> mockData = List.of(
                new DataPoint("ZoneA", expectedStart.plusHours(1), 120.0),
                new DataPoint("ZoneB", expectedStart.plusHours(2), 150.0)
        );

        when(loadDataRepository.findLoadData(eq(expectedStart), eq(expectedEnd), eq("ZoneA")))
                .thenReturn(mockData);

        mockMvc.perform(get("/api/load-data")
                        .param("start", "2025-07-01")
                        .param("end", "2025-07-31")
                        .param("zone", "ZoneA")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].zone", is("ZoneA")))
                .andExpect(jsonPath("$[0].load", is(120.0)))
                .andExpect(jsonPath("$[1].zone", is("ZoneB")));

        verify(loadDataRepository, times(1))
                .findLoadData(eq(expectedStart), eq(expectedEnd), eq("ZoneA"));
    }

    @Test
    void testGetLoadDataDefaultZone() throws Exception {
        LocalDateTime expectedStart = LocalDateTime.parse("2025-07-01T00:00:00");
        LocalDateTime expectedEnd = LocalDateTime.parse("2025-07-31T23:59:59");

        when(loadDataRepository.findLoadData(eq(expectedStart), eq(expectedEnd), eq("ALL")))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/load-data")
                        .param("start", "2025-07-01")
                        .param("end", "2025-07-31")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(loadDataRepository, times(1))
                .findLoadData(eq(expectedStart), eq(expectedEnd), eq("ALL"));
    }

    @Test
    void testGetLoadComparison() throws Exception {
        LocalDate expectedStart = LocalDate.parse("2025-07-01");
        LocalDate expectedEnd = LocalDate.parse("2025-07-31");

        Map<String, List<AggregatedDataPoint>> mockResult = Map.of(
                "currentYear", List.of(new AggregatedDataPoint("ZoneA", expectedStart.atStartOfDay(), 123.0)),
                "lastYear", List.of(new AggregatedDataPoint("ZoneA", expectedStart.minusYears(1).atStartOfDay(), 110.0))
        );

        when(loadDataRepository.findCurrentAndLastYearLoadData(eq(expectedStart), eq(expectedEnd), eq("ALL")))
                .thenReturn(mockResult);

        mockMvc.perform(get("/api/load-data/comparison")
                        .param("start", "2025-07-01")
                        .param("end", "2025-07-31")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentYear", hasSize(1)))
                .andExpect(jsonPath("$.lastYear", hasSize(1)))
                .andExpect(jsonPath("$.currentYear[0].zone", is("ZoneA")))
                .andExpect(jsonPath("$.currentYear[0].avgLoad", is(123.0)));

        verify(loadDataRepository, times(1))
                .findCurrentAndLastYearLoadData(eq(expectedStart), eq(expectedEnd), eq("ALL"));
    }
}
