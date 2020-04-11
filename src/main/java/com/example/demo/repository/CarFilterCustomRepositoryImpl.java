package com.example.demo.repository;

import com.example.demo.documents.Car;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.support.PageableExecutionUtils;

import java.time.LocalDateTime;


public class CarFilterCustomRepositoryImpl implements CarFilterCustomRepository {
    private final MongoTemplate mongoTemplate;
    private FilterRepository filterRepository;

    @Autowired
    public CarFilterCustomRepositoryImpl(MongoTemplate mongoTemplate, FilterRepository filterRepository) {
        this.mongoTemplate = mongoTemplate;
        this.filterRepository = filterRepository;
    }

    @Override
    public Page getAllCarsByFilter(String make, String model, String year, String fuel
            , String gear, String wheels_drive, int items_on_page, int current_page) {
        Pageable pageable = PageRequest.of(current_page, items_on_page);
        Query query = new Query();
        if (make != null) query.addCriteria(Criteria.where("make").is(make));
        if (model != null) query.addCriteria(Criteria.where("model").is(model));
        if (year != null) query.addCriteria(Criteria.where("year").is(year));
        if (fuel != null) query.addCriteria(Criteria.where("fuel").is(fuel));
        if (gear != null) query.addCriteria(Criteria.where("gear").is(gear));
        if (wheels_drive != null) query.addCriteria(Criteria.where("wheels_drive").is(wheels_drive));
        query.with(pageable);
        Page<Car> page = PageableExecutionUtils.getPage(
                mongoTemplate.find(query, Car.class),
                pageable,
                () -> mongoTemplate.count(query, Car.class));
        return page;
    }

    @Override
    //@author Dmitry
    public Page getAllCarsByDateLocation(double radius, double latitude, double longitude
            , LocalDateTime dateStart, LocalDateTime dateEnd
            , double min_amount, double max_amount
            , boolean ascending
            , int items_on_page, int current_page) {
        Pageable pageable = PageRequest.of(current_page, items_on_page);
        Query query = new Query();
        Point point = new Point(longitude, latitude);
//        double radius = 1500;
        Distance distance = new Distance(radius, Metrics.METERS);
        Circle circle = new Circle(point, distance);
        query.addCriteria(Criteria.where("price_per_day").gte(min_amount).lte(max_amount));
        if (ascending) query.with(Sort.by(Sort.Order.asc("price_per_day")));
        if (!ascending) query.with(Sort.by(Sort.Order.desc("price_per_day")));
        query.addCriteria(Criteria.where("pick_up_place.geolocation").withinSphere(circle));
        query.with(pageable);
        Page<Car> page = PageableExecutionUtils.getPage(
                mongoTemplate.find(query, Car.class),
                pageable,
                () -> mongoTemplate.count(query, Car.class));
//        List list = new ArrayList();
//        int count = 0;
//        for (Car car : page.getContent()) {
//            boolean isAvailable = Utils.validationBookedPeriods(dateStart, dateEnd, car);
//            if (isAvailable) {
//                list.add(car);
//                count++;
//            }
//        }
        return new PageImpl(page.getContent(), pageable, page.getTotalElements());
    }

    @Override
    //@author Dmitry
    public Page getAllCarsByLocation(double latitude, double longitude, double radius
            , int items_on_page, int current_page) {
        Pageable pageable = PageRequest.of(current_page, items_on_page);
        Point point = new Point(longitude, latitude);
        Distance distance = new Distance(radius, Metrics.METERS);
        Circle circle = new Circle(point, distance);
        Query query = new Query();
        query.addCriteria(Criteria.where("pick_up_place.geolocation").withinSphere(circle)).with(pageable);
        Page<Car> page = PageableExecutionUtils.getPage(
                mongoTemplate.find(query, Car.class),
                pageable,
                () -> mongoTemplate.count(query, Car.class));
        return page;
    }

    @Override
    //@author Dmitry
    public Page getCarsByAllFilters(int current_page, int items_on_page, String make
            , String model, String wheels_drive, String year, String gear, String fuel
            , String engine, double latitude, double longitude, double radius
            , LocalDateTime date_start, LocalDateTime date_end, double min_amount, double max_amount
            , boolean ascending) {
        Pageable pageable = PageRequest.of(current_page, items_on_page);
        Query query = new Query();
        Point point = new Point(longitude, latitude);
        Distance distance = new Distance(radius, Metrics.METERS);
        Circle circle = new Circle(point, distance);
        if (make != null) query.addCriteria(Criteria.where("make").is(make));
        if (model != null) query.addCriteria(Criteria.where("model").is(model));
        if (year != null) query.addCriteria(Criteria.where("year").is(year));
        if (fuel != null) query.addCriteria(Criteria.where("fuel").is(fuel));
        if (gear != null) query.addCriteria(Criteria.where("gear").is(gear));
        if (wheels_drive != null) query.addCriteria(Criteria.where("wheels_drive").is(wheels_drive));
        query.addCriteria(Criteria.where("price_per_day").gte(min_amount).lte(max_amount));
        if (ascending) query.with(Sort.by(Sort.Order.asc("price_per_day")));
        if (!ascending) query.with(Sort.by(Sort.Order.desc("price_per_day")));
        query.addCriteria(Criteria.where("pick_up_place.geolocation").withinSphere(circle));
        query.with(pageable);
        Page<Car> page = PageableExecutionUtils.getPage(
                mongoTemplate.find(query, Car.class),
                pageable,
                () -> mongoTemplate.count(query, Car.class));
//        List list = new ArrayList();
//        int count = 0;
//        for (Car car : page.getContent()) {
//            boolean isAvailable = Utils.validationBookedPeriods(date_start, date_end, car);
//            if (isAvailable) {
//                list.add(car);
//                count++;
//            }
//        }
        return new PageImpl(page.getContent(), pageable, page.getTotalElements());
    }

    @Override
    public JsonNode getFilters() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(filterRepository.findById("root").get().getFilter_string());
    }
}
