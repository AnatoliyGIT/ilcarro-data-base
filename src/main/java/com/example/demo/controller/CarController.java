package com.example.demo.controller;

import com.example.demo.documents.*;
import com.example.demo.repository.CarComparatorBookedPeriod;
import com.example.demo.repository.CarRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/car")

public class CarController {

    CarRepository carRepository;
    UserRepository userRepository;
    TreeMap<String, List<String>> map = new TreeMap<>();
    TreeMap<String, String> carMap = new TreeMap<>();

    @Autowired
    public CarController(CarRepository carRepository, UserRepository userRepository) {
        this.carRepository = carRepository;
        this.userRepository = userRepository;
    }


    @GetMapping
    public TreeMap<String, List<String>> findOwners() {
        List<User> users = userRepository.findAll();
        List<Car> cars = carRepository.findAll();
        for (User user : users) {
            List<String> numbers = new ArrayList<>();
            List<Car> ownerCars = cars.stream().filter(car -> car.getOwner().getEmail()
                    .equals(user.getEmail())).collect(Collectors.toList());
            ownerCars.forEach(car -> numbers.add(car.getSerial_number()));
            map.put(user.getEmail(), numbers);
        }
        return map;
    }

    @GetMapping(value = "/geo/all/")
    public TreeMap<String, String> findGeoCars() {
        List<Car> cars = carRepository.findAll();
        for (Car car : cars) {
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
            carMap.put(carNumber, str + car.getPick_up_place().getGeolocation());
        }
        return carMap;
    }

    @GetMapping(value = "/booked_pay/")
    public TreeMap<String, String> findBookedCar(String serial_number) {
        Car car = carRepository.findById(serial_number).orElse(null);
        List<User> users = userRepository.findAll();
        if (car == null) throw new NullPointerException("note found");
        TreeMap<String, BookedPeriod> idBooked = new TreeMap<>();
        TreeMap<String, String> mapUser = new TreeMap<>();
        TreeMap<String, String> mapResponse = new TreeMap<>();
        for (BookedPeriod bookedPeriod : car.getBooked_periods()) {
            idBooked.put(bookedPeriod.getOrder_id(), bookedPeriod);
            for (User userId : users) {
                for (BookedCars bookedCars : userId.getBookedCars()) {
                    if (bookedPeriod.getOrder_id().equals(bookedCars.getBooked_period().getOrder_id())) {
                        mapUser.put(bookedPeriod.getOrder_id(), userId.getEmail());
                    }
                }
            }
        }
        for (String key : idBooked.keySet()) {
            for (String keyB : mapUser.keySet()) {
                if (key.equals(keyB)) {
                    String start_date_time = idBooked.get(key).getStart_date_time()
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                    mapResponse.put(start_date_time, mapUser.get(keyB));
                }
            }
        }
        return mapResponse;
    }

    @GetMapping(value = "/booked_all_cars/")
    public TreeMap<String, List<String>> findBookedAllCars() {
        TreeMap<String, List<String>> mapResponse = new TreeMap<>();
        List<User> users = userRepository.findAll();
        for (Car car : carRepository.findAll()) {
            List<String> list = new ArrayList<>();
            for (BookedPeriod bookedPeriod : car.getBooked_periods()) {
                List<User> usersCar = new ArrayList<>();
                for (User user:users) {
                    for (BookedCars bookedCars:user.getBookedCars()) {
                        if (bookedCars.getSerial_number().equals(car.getSerial_number())) {
                            usersCar.add(user);
                        }
                    }
                }
                User userCar = null;
                for (User user:usersCar) {
                    for (BookedCars bookedCars: user.getBookedCars()) {
                        if (bookedCars.getBooked_period().getOrder_id().equals(bookedPeriod.getOrder_id())) {
                            userCar = user;
                            break;
                        }
                    }
                }
                boolean flag = bookedPeriod.getEnd_date_time().isBefore(LocalDateTime.now());
                String trip;
                if (flag) {
                    trip = "YES";
                } else {
                    trip = "NO";
                }
                assert userCar != null;
                list.add(bookedPeriod.getOrder_id() + " -> paid: " + bookedPeriod.isPaid()
                        + " / trip: " + trip
                        + " / " + userCar.getFirstName() + " " + userCar.getSecondName()
                        + " / " + userCar.getEmail() + " / " + bookedPeriod.getAmount());

            }
            mapResponse.put(car.getSerial_number(), list);
        }
        return mapResponse;
    }

    @GetMapping(value = "/reserved_all_cars/")
    public TreeMap<String, List<String>> findReservedAllCars() {
        TreeMap<String, List<String>> mapResponse = new TreeMap<>();
        for (Car car : carRepository.findAll()) {
            List<String> list = new ArrayList<>();
            for (ReservedPeriod reservedPeriod : car.getReserved_periods()) {
                list.add(reservedPeriod.getStart_date_time().toLocalDate().toString() + " "
                        + reservedPeriod.getStart_date_time().toLocalTime().toString() + " - "
                        + reservedPeriod.getEnd_date_time().toLocalDate().toString() + " "
                        + reservedPeriod.getEnd_date_time().toLocalTime().toString());
            }
            mapResponse.put(car.getSerial_number(), list);
        }
        return mapResponse;
    }

    @GetMapping("/getCar/")
    public Car getCar(String serial_number) {
        return carRepository.findById(serial_number).orElse(null);
    }

    @GetMapping("/get_statistics/")
    public List<String> getStatistics() {
        List<Car> list = carRepository.findAll();
        list.sort(new CarComparatorBookedPeriod());
        List<String> stringList = new ArrayList<>();
        for (Car car:list) {
            stringList.add(car.getSerial_number() + " -> bookeds: " + car.getBooked_periods().size()
                    + "; trips: " + car.getStatistics().getTrips());
        }
        return stringList;
    }

    @PostMapping("/del_trip_by_car")
    public String delTrips(String serial_number) {
        String str = "";
        Car car = carRepository.findById(serial_number).orElseThrow();
        str = car.getStatistics().getRating().toString();
        car.getStatistics().setTrips(0);
        User owner = userRepository.findById(car.getOwner().getEmail()).orElseThrow();
        Car carOwner = owner.getOwnerCars().stream().filter(c -> c.getSerial_number()
                .equals(car.getSerial_number())).findFirst().orElseThrow();
        carOwner.getStatistics().setTrips(0);
        owner.getOwnerCars().removeIf(c -> c.getSerial_number().equals(car.getSerial_number()));
        owner.getOwnerCars().add(carOwner);
        userRepository.save(owner);
        carRepository.save(car);
        return str + " -> " + car.getStatistics().getRating().toString();
    }


    @GetMapping(value = "/tree_cars/")
    public List<Car> findTreePopularCars() {
        return carRepository.getThreePopularsCar();
    }

    @GetMapping(value = "/time/")
    public List<String> findTimesForCar() {
        List<Car> cars = carRepository.findAll();
        List<String> list = new ArrayList<>();
        for (Car car:cars) {
            list.add(car.getSerial_number() + " -> " + LocalDateTime.now()
                    .plusHours(correctionTimeZone(car.getPick_up_place()
                            .getGeolocation().getLongitude())).format(DateTimeFormatter
                            .ofPattern("dd.MM.yyyy HH:mm")));
        }
        return list;
    }

    private int correctionTimeZone(Double l) {
        int longitude = (int) Math.floor(l);
        return Math.floorDiv(longitude,15);
    }
}
