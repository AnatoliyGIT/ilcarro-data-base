package com.example.demo.repository;

import com.example.demo.documents.UsageStatisticsDate;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UsageStatisticsDateRepository extends MongoRepository<UsageStatisticsDate, String> {
    UsageStatisticsDate findByDate(String date);
}
