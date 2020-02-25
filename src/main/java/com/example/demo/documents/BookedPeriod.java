package com.example.demo.documents;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Builder
public class BookedPeriod {
    private String order_id;
    private LocalDateTime start_date_time;
    private LocalDateTime end_date_time;
    private boolean paid;
    private double amount;
    private String booking_date;
    private PersonWhoBooked person_who_booked;
}
