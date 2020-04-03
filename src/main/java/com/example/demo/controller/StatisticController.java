package com.example.demo.controller;

import com.example.demo.documents.ObjectStatistics;
import com.example.demo.documents.UsageStatistics;
import com.example.demo.repository.UsageStatisticsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

@RestController
@RequestMapping("/statistics")
public class StatisticController {

    UsageStatisticsRepository repository;

    @Autowired
    public StatisticController(UsageStatisticsRepository repository) {
        this.repository = repository;
    }

    @GetMapping("find_statistics_for_user")
    public ObjectStatistics findStatisticsForUser(@RequestParam String email) {
        UsageStatistics usageStatistics = repository.findById(email).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Statistics not found"));
        return usageStatistics.getObjectStatistics();
    }

    @GetMapping("get_all_statistics")
    public TreeMap<String, ObjectStatistics> getAllStatistics() {
        List<UsageStatistics> usageStatisticsList = repository.findAll();
        TreeMap<String, ObjectStatistics> map = new TreeMap<>();
        for (UsageStatistics usageStatistics : usageStatisticsList) {
            map.put(usageStatistics.getEmail(), usageStatistics.getObjectStatistics());
        }
        return map;
    }

    @DeleteMapping("delete_all_statistics")
    public void deleteAllStatistics() {
        repository.deleteAll();
    }
}
