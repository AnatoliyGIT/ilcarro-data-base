package com.example.demo.controller;

import com.example.demo.documents.Car;
import com.example.demo.documents.Comment;
import com.example.demo.repository.CarRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

@RestController
@RequestMapping("/comment")
public class CommentController {

    CarRepository carRepository;
    UserRepository userRepository;

    @Autowired
    public CommentController(CarRepository carRepository
            , UserRepository userRepository) {
        this.carRepository = carRepository;
        this.userRepository = userRepository;
    }

    @GetMapping(value = "/all_comments/")
    public List<Comment> findAllComments() {
        List<Comment> comments = new ArrayList<>();
        carRepository.findAll().forEach(car -> comments.addAll(car.getComments()));
        return comments;
    }

    @GetMapping(value = "/comments_by_car/")
    public List<Comment> findCommentsBySerialNumber(String serial_number) {
        Car car = carRepository.findById(serial_number)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Car not found"));
        return car.getComments();
    }

    @GetMapping(value = "/get_time")
    public String getTime() {
        String[] ids = TimeZone.getAvailableIDs();
        String a ="";
        for (String str:ids) {
            if (str.equals("Asia/Jerusalem")) {
                a = TimeZone.getDefault().getDisplayName();
                return str + "; " + a;
            }
        }
        return null;
    }
}
