package com.example.demo.controller;

import com.example.demo.model.documents.Car;
import com.example.demo.model.documents.User;
import com.example.demo.repository.CarRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/car")

public class CarController {

    @Autowired
    CarRepository carRepository;
    @Autowired
    UserRepository userRepository;
    HashMap<String,List<String>> map = new HashMap<>();

    @GetMapping
    public HashMap<String, List<String>> findCars() {
        List<User> users = userRepository.findAll();
        List<Car> cars = carRepository.findAll();
        for (User user: users ) {
            List<Car> ownerCars = new ArrayList<>();
            List<String> numbers = new ArrayList<>();
            ownerCars.addAll(cars.stream().filter(car -> car.getOwner().getEmail().equals(user.getEmail())).collect(Collectors.toList()));
            ownerCars.forEach(car -> numbers.add(car.getSerial_number()));
            map.put(user.getEmail(),numbers);
        }




        return map;
    }
}
