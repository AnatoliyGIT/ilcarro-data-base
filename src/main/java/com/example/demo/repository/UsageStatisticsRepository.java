package com.example.demo.repository;

import com.example.demo.documents.UsageStatistics;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UsageStatisticsRepository extends MongoRepository<UsageStatistics, String> {
}
