package com.example.demo.controller;

import com.example.demo.documents.ObjectGeneralStatistics;
import com.example.demo.documents.ObjectUserStatistics;
import com.example.demo.documents.UsageStatistics;
import com.example.demo.repository.UsageStatisticsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.function.ObjDoubleConsumer;

@RestController
@RequestMapping("/statistics")
public class StatisticController {

    UsageStatisticsRepository repository;

    @Autowired
    public StatisticController(UsageStatisticsRepository repository) {
        this.repository = repository;
    }

    @GetMapping("find_statistics_for_user")
    public ObjectUserStatistics findStatisticsForUser(@RequestParam String email) {
        UsageStatistics usageStatistics = repository.findById(email).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Statistics not found"));
        return usageStatistics.getObjectUserStatistics();
    }

    @GetMapping("get_all_statistics")
    public HashMap<String, ObjectUserStatistics> getAllStatistics() {
        List<UsageStatistics> usageStatisticsList = repository.findAll();
        HashMap<String, ObjectUserStatistics> map = new HashMap<>();
        for (UsageStatistics usageStatistics : usageStatisticsList) {
            if (!usageStatistics.getEmail().equals("General")) {
                map.put(usageStatistics.getEmail(), usageStatistics.getObjectUserStatistics());
            }
        }
        return map;
    }

    @GetMapping("get_general_statistics")
    public HashMap<String, ObjectGeneralStatistics> getGeneralStatistics() {
        HashMap<String, ObjectGeneralStatistics> map = new HashMap<>();
        UsageStatistics usageStatistics = repository.findById("General")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "General statistics not founds"));
        map.put(usageStatistics.getEmail(), usageStatistics.getObjectGeneralStatistics());
        return map;
    }


    @DeleteMapping("delete_all_statistics")
    public void deleteAllStatistics(@RequestHeader("Authorization") String tokenAdmin) {
        if (!tokenAdmin.equals("YW5hdG9seUBtYWlsLmNvbTpBbmF0b2x5MjAyMDIw"))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin unauthorized");
        repository.deleteAll();
    }
}
