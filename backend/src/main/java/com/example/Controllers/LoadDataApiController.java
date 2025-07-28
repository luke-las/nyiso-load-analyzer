package com.example.Controllers;

import com.example.AggregatedDataPoint;
import com.example.DataPoint;
import com.example.LoadDataRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class LoadDataApiController {

    private final LoadDataRepository loadDataRepository;

    public LoadDataApiController(LoadDataRepository loadDataRepository) {
        this.loadDataRepository = loadDataRepository;
    }

    @GetMapping("/api/load-data")
    public List<DataPoint> getLoadData(
            @RequestParam String start,
            @RequestParam String end,
            @RequestParam(required = false, defaultValue = "ALL") String zone) {

        LocalDateTime startDate = LocalDateTime.parse(start + "T00:00:00");
        LocalDateTime endDate = LocalDateTime.parse(end + "T23:59:59");

        return loadDataRepository.findLoadData(startDate, endDate, zone);
    }

    @GetMapping("/api/load-data/comparison")
    public Map<String, List<AggregatedDataPoint>> getLoadComparison(
            @RequestParam String start,
            @RequestParam String end,
            @RequestParam(required = false, defaultValue = "ALL") String zone) {

        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);

        return loadDataRepository.findCurrentAndLastYearLoadData(startDate, endDate, zone);
    }
}

