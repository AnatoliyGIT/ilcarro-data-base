package com.example.demo.repository;

import com.example.demo.documents.Car;

import java.util.Comparator;

public class CarComparator implements Comparator<Car> {
    @Override
    public int compare(Car car1, Car car2) {
        return car2.getBooked_periods().size() - car1.getBooked_periods().size();
    }
}