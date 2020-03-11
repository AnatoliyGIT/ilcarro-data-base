package com.example.demo.documents;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class ReservedPeriod {
    private LocalDateTime start_date_time;
    private LocalDateTime end_date_time;
}
