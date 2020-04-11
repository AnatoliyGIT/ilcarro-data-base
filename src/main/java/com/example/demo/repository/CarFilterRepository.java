package com.example.demo.repository;

import com.example.demo.documents.Car;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CarFilterRepository extends MongoRepository<Car, String>, CarFilterCustomRepository {
}
