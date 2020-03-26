package com.example.demo.controller;

import com.example.demo.documents.BookedCars;
import com.example.demo.documents.Car;
import com.example.demo.documents.History;
import com.example.demo.documents.User;
import com.example.demo.repository.CarRepository;
import com.example.demo.repository.UserRepository;
import org.bson.internal.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

@RestController
@RequestMapping("/user")
public class UserController {

    UserRepository userRepository;
    CarRepository carRepository;
    TreeMap<String, String> map = new TreeMap<>();

    @Autowired
    public UserController(UserRepository userRepository, CarRepository carRepository) {
        this.userRepository = userRepository;
        this.carRepository = carRepository;
    }

    @GetMapping(value = "/find_tokens/")
    public TreeMap<String, String> findTokens() {

        List<User> users = userRepository.findAll();
        for (User user : users) {
            String email = user.getEmail();
            String password = user.getPassword();
            byte[] base = Base64.decode(password);
            String str = new String(base);
            str = email + ":" + str;
            String token = Base64.encode(str.getBytes());
            map.put("(" + user.getRegistrationDate() + ") " + email, "                    " + token);
        }
        return map;
    }

    @GetMapping(value = "/find_passwords/")
    public TreeMap<String, String> findPasswords() {

        List<User> users = userRepository.findAll();
        for (User user : users) {
            String email = user.getEmail();
            String password = user.getPassword();
            byte[] base = Base64.decode(password);
            String str = new String(base);
            map.put("(" + user.getRegistrationDate() + ") " + email, "                    " + str);
        }
        return map;
    }

//    @DeleteMapping(value = "/delete_all_lists/")
//    public void deleteAllLists(String email) {
//        User user = userRepository.findById(email).orElse(null);
//        if (user == null) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found");
//        }
//        user.setComments(new ArrayList<>());
//        user.setHistory(new ArrayList<>());
//        user.setBookedCars(new ArrayList<>());
//        ArrayList<Car> cars = user.getOwnerCars();
//        for (Car car : cars) {
//            car.setBooked_periods(new ArrayList<>());
//            car.setStatistics(Statistics.builder()
//                    .rating(0)
//                    .trips(0)
//                    .build());
//            Car carFromRepository = carRepository.findById(car.getSerial_number()).orElse(null);
//            if (carFromRepository == null) {
//                cars.remove(car);
//                continue;
//            }
//            carFromRepository.setBooked_periods(new ArrayList<>());
//            carFromRepository.setStatistics(Statistics.builder()
//                    .trips(0)
//                    .rating(0)
//                    .build());
//            carFromRepository.setComments(new ArrayList<>());
////            carRepository.save(carFromRepository);
//        }
//        user.setOwnerCars(cars);
////        userRepository.save(user);
//    }

    @GetMapping(value = "/find_booked_cars_all_users/")
    public TreeMap<String, List<String>> findBookedAllUsers() {
        TreeMap<String, List<String>> mapResponse = new TreeMap<>();
        for (User user : userRepository.findAll()) {
            List<String> list = new ArrayList<>();
            if (user.isActive()) {
                for (BookedCars bookedCars : user.getBookedCars()) {
                    list.add(bookedCars.getBooked_period().getOrder_id()
                            + " - " + bookedCars.getBooked_period().getPerson_who_booked().getFirst_name());
                }
                mapResponse.put(user.getEmail(), list);
            }
        }
        return mapResponse;
    }

    @GetMapping(value = "/find_histories_all_users/")
    public TreeMap<String, List<String>> findHistoryAllUsers() {
        TreeMap<String, List<String>> mapResponse = new TreeMap<>();
        for (User user : userRepository.findAll()) {
            if (user.isActive()) {
                List<String> list = new ArrayList<>();
                for (History history : user.getHistory()) {
                    list.add(history.getHistory().getOrder_id()
                            + " - " + history.getHistory().getPerson_who_booked().getFirst_name());
                }
                mapResponse.put(user.getEmail(), list);
            }
        }
        return mapResponse;
    }

    @GetMapping(value = "/find_deleted_users/")
    public List<String> findDeletedUsers() {
        List<String> list = new ArrayList<>();
        for (User user : userRepository.findAll()) {
            if (!user.isActive()) {
                list.add(user.getEmail());
            }
        }
        return list;
    }

//    @DeleteMapping(value = "/delete_user")
//    public void deleteUserWithEmail(String email) {
//        User user = userRepository.findById(email).orElse(null);
//        if (user == null) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
//        }
////        userRepository.delete(user);
//    }

    @GetMapping(value = "/find_counts_users_and_cars")
    public String[] findCounts() {
        String[] counts = new String[3];
        int countCars = carRepository.findAll().size();
        int countUsers = 0;
        int countDeletedUsers = 0;
        for (User user : userRepository.findAll()) {
            if (user.isActive()) {
                countUsers++;
            } else {
                countDeletedUsers++;
            }
        }
        counts[0] = "All cars in repository ->  " + countCars;
        counts[1] = "All users is active ->     " + countUsers;
        counts[2] = "All users is not active -> " + countDeletedUsers;
        return counts;
    }

    @GetMapping(value = "/find_cars_and_owners")
    public TreeMap<String, List<String>> findCars() {
        TreeMap<String, List<String>> map = new TreeMap<>();
        for (User user : userRepository.findAll()) {
            List<String> list = new ArrayList<>();
            if (user.isActive()) {
                for (Car car : user.getOwnerCars()) {
                    carRepository.findById(car.getSerial_number())
                            .ifPresent(carFromRepo -> list.add(car.getSerial_number()));
                }
                if (!list.isEmpty()) {
                    map.put(user.getEmail(), list);
                }
            }
        }
        return map;
    }

//    @DeleteMapping("/delHistory")
//    public void delHistory(String email) {
//        User user = userRepository.findById(email).orElse(null);
//        assert user != null;
//        user.setHistory(new ArrayList<>());
//        userRepository.save(user);
//    }
}
