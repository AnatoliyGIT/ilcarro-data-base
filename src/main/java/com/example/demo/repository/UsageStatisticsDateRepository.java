package com.example.demo.repository;

import com.example.demo.documents.UsageStatisticsYesterday;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UsageStatisticsDateRepository extends MongoRepository<UsageStatisticsYesterday, String> {
}
