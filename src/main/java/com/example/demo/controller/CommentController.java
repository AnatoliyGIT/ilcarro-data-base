package com.example.demo.controller;

import com.example.demo.documents.BookedPeriod;
import com.example.demo.documents.Car;
import com.example.demo.documents.Comment;
import com.example.demo.repository.CarCustomRepository;
import com.example.demo.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    CarRepository carRepository;
    CarCustomRepository carCustomRepository;

    @GetMapping(value = "/all/")
    public List<Comment> findAllComments() {
        List<Comment> comments = new ArrayList<>();
        carRepository.findAll().forEach(car -> comments.addAll(car.getComments()));
        return comments;
    }

    @GetMapping(value = "/comments/")
    public List<Comment> findCommentsBySerialNumber(String serial_number) {
        Car car = carRepository.findById(serial_number)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Car not found"));
        return car.getComments();
    }

    @GetMapping(value = "/tree_cars/")
    public List<Car> findTreePopularCars() {
        return carRepository.getThreePopularsCar();
    }
}
