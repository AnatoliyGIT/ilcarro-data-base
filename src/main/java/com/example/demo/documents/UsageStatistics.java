package com.example.demo.documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@Builder
public class UsageStatistics {
    @Id
    private String email;
    private ObjectUserStatistics objectUserStatistics;
    private ObjectGeneralStatistics objectGeneralStatistics;
}
