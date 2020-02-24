package com.example.demo.controller;

import com.example.demo.model.documents.Car;
import com.example.demo.model.documents.User;
import com.example.demo.repository.CarRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    HashMap<String,String> carmap = new HashMap<>();

    @GetMapping
    public HashMap<String, List<String>> findOwners() {
        List<User> users = userRepository.findAll();
        List<Car> cars = carRepository.findAll();
        for (User user: users ) {
            List<Car> ownerCars = new ArrayList<>();
            List<String> numbers = new ArrayList<>();
            ownerCars.addAll(cars.stream().filter(car -> car.getOwner().getEmail()
                    .equals(user.getEmail())).collect(Collectors.toList()));
            ownerCars.forEach(car -> numbers.add(car.getSerial_number()));
            map.put(user.getEmail(),numbers);
        }
        return map;
    }

    @GetMapping(value = "/geo/all/")
    public HashMap<String, String> findGeoCars() {
        List<Car> cars = carRepository.findAll();
        for (Car car: cars ) {
            String str = "                  ";
            if (car.getMake().length() + car.getModel().length() >= 8) {
                str = "            ";
            }
            if (car.getMake().length() + car.getModel().length() >= 12) {
                str = "        ";
            }
            if (car.getMake().length() + car.getModel().length() >= 15) {
                str = "     ";
            }
            String carNumber = car.getSerial_number()
                    + " (" + car.getMake() + " " + car.getModel() + ")";
            carmap.put(carNumber,str + car.getPick_up_place().getGeolocation());
        }
        return carmap;
    }

    @GetMapping(value = "/booked/")
    public HashMap<String, String> findBookedCar(String serial_number) {
        List<Car> cars = carRepository.findAll();
        for (Car car: cars ) {
            String str = "                  ";
            if (car.getMake().length() + car.getModel().length() >= 8) {
                str = "            ";
            }
            if (car.getMake().length() + car.getModel().length() >= 12) {
                str = "        ";
            }
            if (car.getMake().length() + car.getModel().length() >= 15) {
                str = "     ";
            }
            String carNumber = car.getSerial_number()
                    + " (" + car.getMake() + " " + car.getModel() + ")";
            carmap.put(carNumber,str + car.getPick_up_place().getGeolocation());
        }
        return carmap;
    }
}
