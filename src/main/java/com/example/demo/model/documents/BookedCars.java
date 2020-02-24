package com.example.demo.model.documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder

public class BookedCars {
    private String serial_number;
    private BookedPeriod booked_period;
}