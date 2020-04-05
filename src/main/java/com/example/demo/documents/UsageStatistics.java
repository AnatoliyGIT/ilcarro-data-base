package com.example.demo.documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@Builder
@Document(collection = "UsageStatisticsToday")
public class UsageStatistics {
    @Id
    private String name;
    private ObjectUserStatistics objectUserStatistics;
    private ObjectGeneralStatistics objectGeneralStatistics;
}
