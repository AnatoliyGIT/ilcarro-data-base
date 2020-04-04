package com.example.demo.documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class UsageStatisticsDate {
    @Id
    private LocalDate date;
    private List<UsageStatistics> usageStatisticsList;
}
