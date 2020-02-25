package com.example.demo.documents;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.concurrent.CopyOnWriteArrayList;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Builder
@Document(value = "history")
public class HistoryBookedCars {
    @Id
    private String serial_number;
    private CopyOnWriteArrayList<HistoryCars> history;
}
