package com.example.demo.controller;

import com.example.demo.documents.ObjectGeneralStatistics;
import com.example.demo.documents.ObjectUserStatistics;
import com.example.demo.documents.UsageStatistics;
import com.example.demo.repository.UsageStatisticsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Field;
import java.util.*;

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
    public TreeMap<String, List<String>> getAllStatistics() throws IllegalAccessException {
        List<ObjectGeneralStatistics> objectGeneralStatisticsList = new ArrayList<>();
        List<ObjectUserStatistics> objectUserStatisticsList = new ArrayList<>();
        TreeMap<String, List<String>> mapList = new TreeMap<>();
        List<UsageStatistics> statisticsList = repository.findAll();

        for (UsageStatistics usageStatistics : statisticsList) {
            if (objectGeneralStatisticsList.isEmpty() && usageStatistics.getObjectGeneralStatistics() != null) {
                objectGeneralStatisticsList.add(usageStatistics.getObjectGeneralStatistics());
            }
            if (usageStatistics.getObjectUserStatistics() != null) {
                objectUserStatisticsList.add(usageStatistics.getObjectUserStatistics());
            }
            if (usageStatistics.getEmail().equals("General")) {
                for (ObjectGeneralStatistics objectGeneralStatistics : objectGeneralStatisticsList) {
                    List<String> list = new ArrayList<>();
                    TreeMap<String, Integer> generalMap = new TreeMap<>();
                    Field[] generalFields = objectGeneralStatistics.getClass().getDeclaredFields();
                    for (Field field : generalFields) {
                        field.setAccessible(true);
                        generalMap.put(field.getName(), field.getInt(objectGeneralStatistics));
                        field.setAccessible(false);
                        StringBuilder str = new StringBuilder();
                        for (int i = field.getName().length(); i < 32; i++) {
                            str.append("-");
                        }
                        if (generalMap.get(field.getName()) != 0) {
                            list.add(generalMap.firstKey() + " " + str + " " + generalMap.get(field.getName()));
                        }
                        generalMap.remove(field.getName());
                    }
                    mapList.put(usageStatistics.getEmail(), list);
                }
            }

            for (ObjectUserStatistics objectUserStatistics : objectUserStatisticsList) {
                List<String> list = new ArrayList<>();
                TreeMap<String, Integer> userMap = new TreeMap<>();//мап для данных
                Field[] userFields = objectUserStatistics.getClass().getDeclaredFields();
                for (Field field : userFields) {
                    field.setAccessible(true);
                    userMap.put(field.getName(), field.getInt(objectUserStatistics));
                    field.setAccessible(false);
                    StringBuilder str = new StringBuilder();
                    for (int i = field.getName().length(); i < 32; i++) {
                        str.append("-");
                    }
                    if (userMap.get(field.getName()) != 0) {
                        list.add(userMap.firstKey() + " " + str + " " + userMap.get(field.getName()));
                    }
                    userMap.remove(field.getName());
                }
                mapList.put(usageStatistics.getEmail(), list);
            }
        }
        return mapList;
    }


    @DeleteMapping("delete_all_statistics")
    public void deleteAllStatistics(@RequestHeader("Authorization") String tokenAdmin) {
        if (!tokenAdmin.equals("YW5hdG9seUBtYWlsLmNvbTpBbmF0b2x5MjAyMDIw"))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin unauthorized");
        repository.deleteAll();
    }
}
