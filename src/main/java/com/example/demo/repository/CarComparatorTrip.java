package com.example.demo.repository;

import com.example.demo.documents.Car;

import java.util.Comparator;

public class CarComparatorTrip implements Comparator<Car> {
    @Override
    public int compare(Car car1, Car car2) {
        return car2.getStatistics().getTrips() - car1.getStatistics().getTrips();
    }
}