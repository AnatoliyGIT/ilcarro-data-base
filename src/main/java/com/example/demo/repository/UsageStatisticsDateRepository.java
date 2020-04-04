package com.example.demo.repository;

import com.example.demo.documents.UsageStatisticsDate;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;

public interface UsageStatisticsDateRepository extends MongoRepository<UsageStatisticsDate, String> {
    UsageStatisticsDate findByDate(LocalDate date);
}
