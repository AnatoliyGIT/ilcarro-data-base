package com.example.demo.documents;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Builder
@Document(collection = "cars")
public class Car {
    @Id
    private String serial_number;
    private String make;
    private String model;
    private String year;
    private String engine;
    private String fuel;
    private String gear;
    private String wheels_drive;
    private int horse_power;
    private int torque;
    private int doors;
    private int seats;
    private String car_class;
    private float fuel_consumption;
    private ArrayList<String> features;
    private double price_per_day;
    //private Price price;
    private double distance_included;
    private String about;
    private PickUpPlace pick_up_place;
    private ArrayList<String> image_url;
    private Owner owner;
    private ArrayList<BookedPeriod> booked_periods;
    private Statistics statistics;
    private ArrayList<Comment> comments;
}
