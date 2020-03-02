package com.example.demo.repository;

import com.example.demo.documents.Car;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

public class CarCustomRepositoryImpl implements CarCustomRepository {
    MongoTemplate mongoTemplate;
    CarComparator carComparator;

    @Autowired
    public CarCustomRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<Car> findCarsByOwnerEmail(String email) {
        Query query = new Query();
        query.addCriteria(Criteria.where("owner.email").is(email));
        return mongoTemplate.find(query, Car.class);
    }

//    @Override
//    public List<Car> getThreePopularsCar() {
//        Query query = new Query();
//        query.with(Sort.by(Sort.Order.desc("statistics"))).limit(3);
//        return mongoTemplate.find(query, Car.class);
//    }

    @Override
    public List<Car> getThreePopularsCar() {
        Query queryBooked = new Query();
        queryBooked.with(Sort.by(Sort.Order.asc("booked_period")));
        Query queryCar = new Query();
        queryCar.with(Sort.by(Sort.Order.asc("booked_periods"))).limit(10);
        List<Car> cars = mongoTemplate.find(queryCar, Car.class);
        return cars;
    }
}
