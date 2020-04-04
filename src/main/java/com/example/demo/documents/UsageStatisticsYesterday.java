package com.example.demo.documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class UsageStatisticsYesterday {
    @Id
    private String date;
    private List<UsageStatistics> usageStatisticsList;
}
