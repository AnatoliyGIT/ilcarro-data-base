package com.example.demo.documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@Builder
public class UsageStatisticsDate {
    @Id
    private String date;
    private UsageStatisticsYesterday usageStatisticsYesterday;
}
