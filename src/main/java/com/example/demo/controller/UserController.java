package com.example.demo.controller;

import com.example.demo.documents.*;
import com.example.demo.repository.CarRepository;
import com.example.demo.repository.UserRepository;
import org.bson.internal.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

@RestController
@RequestMapping("/user")
public class UserController {

    UserRepository userRepository;
    CarRepository carRepository;
    TreeMap<Integer, String> map = new TreeMap<>();

    @Autowired
    public UserController(UserRepository userRepository, CarRepository carRepository) {
        this.userRepository = userRepository;
        this.carRepository = carRepository;
    }

    @GetMapping(value = "/find_tokens/")
    public TreeMap<Integer, String> findTokens(@RequestHeader("Authorization") String tokenAdmin) {
        if (!tokenAdmin.equals("YW5hdG9seUBtYWlsLmNvbTpBbmF0b2x5MjAyMDIw"))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin unauthorized");
        List<User> users = userRepository.findAll();
        users.sort(emailComparator);
        List<String> emails = new ArrayList<>();
        users.forEach(email -> emails.add(email.getEmail()));
        emails.sort(lengthComparator);
        int symbolsCounts = emails.get(emails.size() - 1).length() + 3;
        int count = 0;
        for (User user : users) {
            String strS = "";
            String email = user.getEmail();
            String password = user.getPassword();
            byte[] base = Base64.decode(password);
            String str = new String(base);
            str = email + ":" + str;
            String token = Base64.encode(str.getBytes());
            count++;
            if (count < 10) {
                strS = " ";
            }
            map.put(count, strS + "(" + user.getRegistrationDate() + ") " + email
                    + " ".repeat(Math.max(0, symbolsCounts - user.getEmail().length())) + token);
        }
        return map;
    }

    @GetMapping(value = "/find_passwords/")
    public TreeMap<Integer, String> findPasswords(@RequestHeader("Authorization") String tokenAdmin) {
        if (!tokenAdmin.equals("YW5hdG9seUBtYWlsLmNvbTpBbmF0b2x5MjAyMDIw"))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin unauthorized");
        List<User> users = userRepository.findAll();
        users.sort(emailComparator);
        List<String> emails = new ArrayList<>();
        users.forEach(email -> emails.add(email.getEmail()));
        emails.sort(lengthComparator);
        int symbolsCounts = emails.get(emails.size() - 1).length() + 3;
        int count = 0;
        for (User user : users) {
            String strS = "";
            String email = user.getEmail();
            String password = user.getPassword();
            byte[] base = Base64.decode(password);
            String str = new String(base);
            count++;
            if (count < 10) {
                strS = " ";
            }
            map.put(count, strS + "(" + user.getRegistrationDate() + ") " + email
                    + " ".repeat(Math.max(0, symbolsCounts - user.getEmail().length())) + str);
        }
        return map;
    }

    @DeleteMapping(value = "/delete_all_lists_from_user/")
    public void deleteAllLists(@RequestHeader("Authorization") String tokenAdmin, String email) {
        if (!tokenAdmin.equals("YW5hdG9seUBtYWlsLmNvbTpBbmF0b2x5MjAyMDIw"))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin unauthorized");
        User user = userRepository.findById(email).orElse(null);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found");
        }
        user.setComments(new ArrayList<>());
        user.setHistory(new ArrayList<>());
        user.setBookedCars(new ArrayList<>());
        ArrayList<Car> cars = user.getOwnerCars();
        for (Car car : cars) {
            car.setBooked_periods(new ArrayList<>());
            car.setStatistics(Statistics.builder()
                    .rating(0)
                    .trips(0)
                    .build());
            Car carFromRepository = carRepository.findById(car.getSerial_number()).orElse(null);
            if (carFromRepository == null) {
                cars.remove(car);
                continue;
            }
            carFromRepository.setBooked_periods(new ArrayList<>());
            carFromRepository.setStatistics(Statistics.builder()
                    .trips(0)
                    .rating(0)
                    .build());
            carFromRepository.setComments(new ArrayList<>());
            carRepository.save(carFromRepository);
        }
        user.setOwnerCars(cars);
        userRepository.save(user);
    }

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
                    list.add("Car: " + history.getSerial_number()
                            + " - /" + history.getHistory().getPerson_who_booked().getFirst_name()
                            + "/ (" + history.getHistory().getStart_date_time().toLocalDate() + " "
                            + history.getHistory().getStart_date_time().toLocalTime() + "..."
                            + history.getHistory().getEnd_date_time().toLocalDate() + " "
                            + history.getHistory().getEnd_date_time().toLocalTime()
                            + ") -> " + history.getHistory().getAmount() + " $");
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

    @DeleteMapping(value = "/delete_user_with_cars")
    public void deleteUserByEmail(@RequestHeader("Authorization") String tokenAdmin, String email) {
        if (!tokenAdmin.equals("YW5hdG9seUBtYWlsLmNvbTpBbmF0b2x5MjAyMDIw"))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin unauthorized");
        User user = userRepository.findById(email).orElse(null);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        List<Car> carsUser = carRepository.findCarsByOwnerEmail(email);
        for (Car car : carsUser) {
            carRepository.deleteById(car.getSerial_number());
        }
        userRepository.delete(user);
    }

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

    @DeleteMapping("/delHistory")
    public void delHistory(@RequestHeader("Authorization") String tokenAdmin, String email) {
        if (!tokenAdmin.equals("YW5hdG9seUBtYWlsLmNvbTpBbmF0b2x5MjAyMDIw"))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin unauthorized");
        User user = userRepository.findById(email).orElse(null);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        user.setHistory(new ArrayList<>());
        userRepository.save(user);
    }

    @PostMapping("activate_user")
    public void activateUser(@RequestHeader("Authorization") String tokenAdmin, @RequestParam String email) {
        if (!tokenAdmin.equals("YW5hdG9seUBtYWlsLmNvbTpBbmF0b2x5MjAyMDIw"))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin unauthorized");
        User user = userRepository.findById(email).orElse(null);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        user.setActive(true);
        userRepository.save(user);
    }

    Comparator<String> lengthComparator = Comparator.comparingInt(String::length);
    Comparator<User> emailComparator = Comparator.comparing(User::getEmail);
}
