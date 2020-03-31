package com.example.demo;

import com.example.demo.documents.Car;

import java.util.Comparator;

public class CarComparatorBookedPeriod implements Comparator<Car> {

    @Override
    public int compare(Car car1, Car car2) {
        if (car2.getStatistics().getTrips() - car1.getStatistics().getTrips() == 0) {
            return car2.getBooked_periods().size() - car1.getBooked_periods().size();
        }
        return car2.getStatistics().getTrips() - car1.getStatistics().getTrips();
    }
}