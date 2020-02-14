package com.example.demo.repository;

import com.example.demo.model.documents.Car;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CarRepository extends MongoRepository<Car,String> {
}
