package com.example.demo.repository;

import com.example.demo.documents.Car;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CarCustomRepositoryImpl implements CarCustomRepository {
    MongoTemplate mongoTemplate;


    public CarCustomRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<Car> findCarsByOwnerEmail(String email) {
        Query query = new Query();
        query.addCriteria(Criteria.where("owner.email").is(email));
        return mongoTemplate.find(query, Car.class);
    }

    @Override
    public List<Car> getThreePopularsCar() {
        Query query = new Query();
        query.with(Sort.by(Sort.Order.desc("statistics.trips"))).limit(3);
        return mongoTemplate.find(query, Car.class);
    }
}
