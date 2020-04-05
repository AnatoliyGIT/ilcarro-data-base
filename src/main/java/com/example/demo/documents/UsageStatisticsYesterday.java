package com.example.demo.documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
@Document(collection = "UsageStatisticsDays")
public class UsageStatisticsYesterday {
    @Id
    private String date;
    private List<UsageStatistics> usageStatisticsList;
}