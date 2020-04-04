package com.example.demo.controller;

import com.example.demo.documents.ObjectGeneralStatistics;
import com.example.demo.documents.ObjectUserStatistics;
import com.example.demo.documents.UsageStatistics;
import com.example.demo.documents.UsageStatisticsDate;
import com.example.demo.repository.UsageStatisticsDateRepository;
import com.example.demo.repository.UsageStatisticsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/statistics")
public class StatisticController {

    UsageStatisticsRepository statisticsRepository;
    UsageStatisticsDateRepository dateRepository;

    @Autowired
    public StatisticController(UsageStatisticsRepository statisticsRepository
            , UsageStatisticsDateRepository dateRepository) {
        this.statisticsRepository = statisticsRepository;
        this.dateRepository = dateRepository;
    }

    @GetMapping("find_statistics_for_user")
    public ObjectUserStatistics findStatisticsForUser(@RequestParam String email) {
        UsageStatistics usageStatistics = statisticsRepository.findById(email).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Statistics not found"));
        return usageStatistics.getObjectUserStatistics();
    }

    @GetMapping("get_statistics_today")
    public TreeMap<String, List<String>> getStatisticsToday() throws IllegalAccessException {
        List<ObjectGeneralStatistics> objectGeneralStatisticsList = new ArrayList<>();
        List<ObjectUserStatistics> objectUserStatisticsList = new ArrayList<>();
        TreeMap<String, List<String>> mapList = new TreeMap<>();
        List<UsageStatistics> statisticsList = statisticsRepository.findAll();

        for (UsageStatistics usageStatistics : statisticsList) {
            if (objectGeneralStatisticsList.isEmpty() && usageStatistics.getObjectGeneralStatistics() != null) {
                objectGeneralStatisticsList.add(usageStatistics.getObjectGeneralStatistics());
            }
            if (usageStatistics.getObjectUserStatistics() != null) {
                objectUserStatisticsList.add(usageStatistics.getObjectUserStatistics());
            }
            if (usageStatistics.getName().equals("General")) {
                for (ObjectGeneralStatistics objectGeneralStatistics : objectGeneralStatisticsList) {
                    serializerField(mapList, usageStatistics, objectGeneralStatistics, null);
                }
            }
            for (ObjectUserStatistics objectUserStatistics : objectUserStatisticsList) {
                serializerField(mapList, usageStatistics, null, objectUserStatistics);
            }
        }
        return mapList;
    }

    @GetMapping("find_all_statistics")
    public TreeMap<LocalDate, TreeMap<String, List<String>>> findAllStatistics() throws IllegalAccessException {
        TreeMap<LocalDate, TreeMap<String, List<String>>> returnMap = new TreeMap<>();
        TreeMap<String, List<String>> treeMap = new TreeMap<>();
        for (UsageStatisticsDate usd : dateRepository.findAll()) {
            for (UsageStatistics us : usd.getUsageStatisticsList()) {
                serializerField(treeMap,us,us.getObjectGeneralStatistics(),us.getObjectUserStatistics());
            }
            returnMap.put(usd.getDate(), treeMap);
        }
        return returnMap;
    }

    @DeleteMapping("delete_all_statistics")
    public void deleteAllStatistics(@RequestHeader("Authorization") String tokenAdmin) {
        if (!tokenAdmin.equals("YW5hdG9seUBtYWlsLmNvbTpBbmF0b2x5MjAyMDIw"))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin unauthorized");
        statisticsRepository.deleteAll();
    }

    private static void serializerField(TreeMap<String, List<String>> mapList
            , UsageStatistics usageStatistics
            , ObjectGeneralStatistics objectGeneralStatistics
            , ObjectUserStatistics objectUserStatistics) throws IllegalAccessException {
        List<String> list = new ArrayList<>();
        TreeMap<String, Integer> generalMap = new TreeMap<>();
        Field[] fields = new Field[0];
        if (objectGeneralStatistics == null) {
            fields = objectUserStatistics.getClass().getDeclaredFields();
        }
        if (objectUserStatistics == null) {
            fields = objectGeneralStatistics.getClass().getDeclaredFields();
        }
        for (Field field : fields) {
            int value = 0;
            field.setAccessible(true);
            if (objectGeneralStatistics == null) {
                value = field.getInt(objectUserStatistics);
            }
            if (objectUserStatistics == null) {
                value = field.getInt(objectGeneralStatistics);
            }
            generalMap.put(field.getName(), value);
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
        mapList.put(usageStatistics.getName(), list);
    }
}
