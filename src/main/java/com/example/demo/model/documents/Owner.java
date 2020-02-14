package com.example.demo.model.documents;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Owner {
    private  String email;
    private  String first_name;
    private  String second_name;
    private LocalDate registration_date;
}
