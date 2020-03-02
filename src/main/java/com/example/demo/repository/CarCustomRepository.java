package com.example.demo.repository;

import com.example.demo.documents.BookedPeriod;
import com.example.demo.documents.Car;

import java.util.List;

public interface CarCustomRepository {
    List<Car> findCarsByOwnerEmail(String email);
    List<Car> getThreePopularsCar();
}
