package com.example.demo.documents;

import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Builder
public class Owner {
    private String email;
    private String first_name;
    private String second_name;
    private LocalDate registration_date;
}
