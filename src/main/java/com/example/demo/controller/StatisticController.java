package com.example.demo.controller;

import com.example.demo.documents.*;
import com.example.demo.repository.UsageStatisticsDateRepository;
import com.example.demo.repository.UsageStatisticsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

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
    public TreeMap<String, List<String>> findStatisticsForUser(@RequestParam String email) throws IllegalAccessException {
        UsageStatistics usageStatistics = statisticsRepository.findById(email).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Statistics not found"));
        TreeMap<String, List<String>> mapList = new TreeMap<>();
        serializerField(mapList,usageStatistics,usageStatistics.getObjectGeneralStatistics()
                ,usageStatistics.getObjectUserStatistics());
        return mapList;
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
    public TreeMap<String, TreeMap<String, List<String>>> findAllStatistics() throws IllegalAccessException {
        TreeMap<String, TreeMap<String, List<String>>> returnMap = new TreeMap<>();
        for (UsageStatisticsYesterday usd : dateRepository.findAll()) {
            TreeMap<String, List<String>> treeMap = new TreeMap<>();
            List<UsageStatistics> usageStatisticsList = new ArrayList<>(usd.getUsageStatisticsList());
            for (UsageStatistics us : usageStatisticsList) {
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
        dateRepository.deleteAll();
    }

//    @GetMapping("findAll")
//    public List<UsageStatisticsYesterday> findAllStat() {
//        return dateRepository.findAll();
//    }

    private static void serializerField(TreeMap<String, List<String>> mapList
            , UsageStatistics usageStatistics
            , ObjectGeneralStatistics objectGeneralStatistics
            , ObjectUserStatistics objectUserStatistics) throws IllegalAccessException {
        List<String> list = new ArrayList<>();
        TreeMap<String, Integer> map = new TreeMap<>();
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
            map.put(field.getName(), value);
            field.setAccessible(false);
            StringBuilder str = new StringBuilder();
            for (int i = field.getName().length(); i < 32; i++) {
                str.append("-");
            }
            if (map.get(field.getName()) != 0) {
                list.add(map.firstKey() + " " + str + " " + map.get(field.getName()));
            }
            map.remove(field.getName());
        }
        mapList.put(usageStatistics.getName(), list);
    }
}
