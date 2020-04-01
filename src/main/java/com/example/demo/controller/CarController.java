package com.example.demo.controller;

import com.example.demo.documents.*;
import com.example.demo.CarComparatorBookedPeriod;
import com.example.demo.repository.CarRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

@RestController
@RequestMapping("/car")

public class CarController {

    CarRepository carRepository;
    UserRepository userRepository;

    @Autowired
    public CarController(CarRepository carRepository, UserRepository userRepository) {
        this.carRepository = carRepository;
        this.userRepository = userRepository;
    }


    @GetMapping("find_owner_by_car")
    public String findOwnerByCar(@RequestParam String serial_number) {
        Car car = carRepository.findById(serial_number)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND
                        , "Car with SN: " + serial_number + " not found"));
        User owner = userRepository.findById(car.getOwner().getEmail()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Owner not found"));
        return "Car: " + serial_number + " (" + car.getMake() + " " + car.getModel()
                + ") Owner: " + owner.getEmail() + " (" + owner.getFirstName() + ")";
    }

    @GetMapping
    public TreeMap<String, String> findOwners() {
        TreeMap<String, String> carMap = new TreeMap<>();
        List<Car> cars = carRepository.findAll();
        List<String> numbers = new ArrayList<>();
        List<String> models = new ArrayList<>();
        for (Car car : cars) {
            numbers.add(car.getSerial_number());
            models.add(car.getMake() + " " + car.getModel());
        }
        numbers.sort(comparator);
        models.sort(comparator);
        int numbersCounts = numbers.get(numbers.size() - 1).length() + 1;
        int modelsCounts = models.get(models.size() - 1).length() + 3;
        for (Car car : cars) {
            StringBuilder str1 = new StringBuilder();
            StringBuilder str2 = new StringBuilder();
            for (int i = 0; i < numbersCounts - car.getSerial_number().length(); i++) {
                str1.append(" ");
            }
            for (int i = 0; i < modelsCounts - car.getModel().length() - car.getMake().length(); i++) {
                str2.append("-");
            }
            String serial_number = "(" + car.getSerial_number() +  ") " + str1
                    + car.getMake() + " " + car.getModel() + " " + str2 + " ";
            String owner = car.getOwner().getEmail();
            carMap.put(serial_number, owner);
        }
        return carMap;
    }

    @GetMapping(value = "/geo/all/")
    public TreeMap<String, String> findGeoCars() {
        TreeMap<String, String> carMap = new TreeMap<>();
        List<Car> cars = carRepository.findAll();
        List<String> numbers = new ArrayList<>();
        List<String> models = new ArrayList<>();
        for (Car car : cars) {
            numbers.add(car.getSerial_number());
            models.add(car.getMake() + " " + car.getModel());
        }
        numbers.sort(comparator);
        models.sort(comparator);
        int numbersCounts = numbers.get(numbers.size() - 1).length() + 3;
        int modelsCounts = models.get(models.size() - 1).length() + 3;
        for (Car car : cars) {
            StringBuilder str1 = new StringBuilder();
            StringBuilder str2 = new StringBuilder();
            for (int i = 0; i < numbersCounts - car.getSerial_number().length(); i++) {
                str1.append(" ");
            }
            for (int i = 0; i < modelsCounts - car.getModel().length() - car.getMake().length(); i++) {
                str2.append("-");
            }
            String carNumber = car.getSerial_number() + str1
                    + car.getMake() + " " + car.getModel();
            carMap.put(carNumber, " " + str2 + " " + car.getPick_up_place().getGeolocation().toString());
        }
        return carMap;
    }

    @GetMapping(value = "/booked_pay/")
    public TreeMap<String, String> findBookedPay(@RequestParam String serial_number) {
        Car car = carRepository.findById(serial_number)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Car note found"));
        List<User> users = userRepository.findAll();
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
                    String end_date_time = idBooked.get(key).getEnd_date_time()
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                    mapResponse.put(start_date_time + " - " + end_date_time, mapUser.get(keyB));
                }
            }
        }
        if(mapResponse.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Booked periods not founds");
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
                for (User user : users) {
                    for (BookedCars bookedCars : user.getBookedCars()) {
                        if (bookedCars.getSerial_number().equals(car.getSerial_number())) {
                            usersCar.add(user);
                        }
                    }
                }
                User userCar = null;
                for (User user : usersCar) {
                    for (BookedCars bookedCars : user.getBookedCars()) {
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
                String email;
                if (userCar == null) {
                    email = "............";
                } else {
                    email = userCar.getEmail();
                }
                list.add(bookedPeriod.getOrder_id() + " -> paid: " + bookedPeriod.isPaid()
                        + " / trip: " + trip
                        + " / " + bookedPeriod.getPerson_who_booked().getFirst_name()
                        + " / " + email + " / " + bookedPeriod.getAmount());

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
    public Car getCar(@RequestParam String serial_number) {
        return carRepository.findById(serial_number)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Car not found"));
    }

    @GetMapping("/get_statistics/")
    public List<String> getStatistics() {
        List<Car> list = carRepository.findAll();
        list.sort(new CarComparatorBookedPeriod());
        List<String> stringList = new ArrayList<>();
        List<String> numbers = new ArrayList<>();
        for (Car car : list) {
            numbers.add(car.getSerial_number());
        }
        numbers.sort(comparator);
        int numbersCounts = numbers.get(numbers.size() - 1).length() + 3;
        for (Car car : list) {
            StringBuilder str = new StringBuilder();
            for (int i = 0; i < numbersCounts - car.getSerial_number().length(); i++) {
                str.append("-");
            }
            stringList.add(car.getSerial_number() + " " + str + " bookeds: " + car.getBooked_periods().size()
                    + "; trips: " + car.getStatistics().getTrips());
        }
        return stringList;
    }

    @DeleteMapping("/del_trips_by_car")
    public String delTrips(@RequestHeader("Authorization") String tokenAdmin, @RequestParam String serial_number) {
        if (!tokenAdmin.equals("YW5hdG9seUBtYWlsLmNvbTpBbmF0b2x5MjAyMDIw"))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin unauthorized");
        String str;
        Car car = carRepository.findById(serial_number)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Car not found"));
        str = car.getStatistics().getRating().toString();
        car.getStatistics().setTrips(0);
        User owner = userRepository.findById(car.getOwner().getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Owner " + car.getOwner().getEmail() + " not found"));
        Car carOwner = owner.getOwnerCars().stream().filter(c -> c.getSerial_number()
                .equals(car.getSerial_number())).findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Car not found"));
        carOwner.getStatistics().setTrips(0);
        owner.getOwnerCars().removeIf(c -> c.getSerial_number().equals(car.getSerial_number()));
        owner.getOwnerCars().add(carOwner);
        userRepository.save(owner);
        carRepository.save(car);
        return str + " -> " + car.getStatistics().getRating().toString();
    }

    @DeleteMapping("delete_bookedPeriod_by_bookedId_User")
    public void delBookedPeriod(@RequestHeader String tokenAdmin, @RequestParam String email,
                                @RequestParam String bookedId, @RequestParam String serial_number) {
        if (!tokenAdmin.equals("YW5hdG9seUBtYWlsLmNvbTpBbmF0b2x5MjAyMDIw"))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin unauthorized");
        User user = userRepository.findById(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        ArrayList<Car> cars = user.getOwnerCars();
        Car car = cars.stream().filter(c -> c.getSerial_number()
                .equals(serial_number)).findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Car not found"));
        ArrayList<BookedPeriod> bookedPeriodList = car.getBooked_periods();
        BookedPeriod bookedPeriod = bookedPeriodList.stream().filter(per -> per.getOrder_id().equals(bookedId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "BookedPeriod not found"));
        bookedPeriodList.removeIf(per -> per.getOrder_id().equals(bookedId));
        car.setBooked_periods(bookedPeriodList);
        cars.removeIf(c -> c.getSerial_number().equals(serial_number));
        cars.add(car);
        user.setOwnerCars(cars);
        userRepository.save(user);
    }

    @GetMapping(value = "/tree_cars/")
    public List<Car> findTreePopularCars() {
        return carRepository.getThreePopularsCar();
    }

    @GetMapping(value = "/time/")
    public List<String> findTimesForCar() {
        List<Car> cars = carRepository.findAll();
        List<String> list = new ArrayList<>();
        List<String> numbers = new ArrayList<>();
        for (Car car : cars) {
            numbers.add(car.getSerial_number());
        }
        numbers.sort(comparator);
        int numbersCounts = numbers.get(numbers.size() - 1).length() + 3;
        for (Car car : cars) {
            StringBuilder str = new StringBuilder();
            for (int i = 0; i < numbersCounts - car.getSerial_number().length(); i++) {
                str.append("-");
            }
            list.add(car.getSerial_number() + " " + str + " " + LocalDateTime.now()
                    .plusHours(correctionTimeZone(car.getPick_up_place()
                            .getGeolocation().getLongitude()) + 1).format(DateTimeFormatter
                            .ofPattern("HH:mm dd.MM.yyyy")));
        }
        return list;
    }

    @DeleteMapping(value = "delete_period_false")
    public void deletePeriodFalse(@RequestHeader("Authorization") String tokenAdmin
            , @RequestParam String serial_number_auto, @RequestParam String bookedId) {
        if (!tokenAdmin.equals("YW5hdG9seUBtYWlsLmNvbTpBbmF0b2x5MjAyMDIw"))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin unauthorized");
        Car car = carRepository.findById(serial_number_auto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Car not found"));
        ArrayList<BookedPeriod> bookedPeriodList = car.getBooked_periods();
        bookedPeriodList.removeIf(bookedPeriod -> bookedPeriod.getOrder_id().equals(bookedId));
        car.setBooked_periods(bookedPeriodList);
        carRepository.save(car);

    }

    @GetMapping(value = "find_booked_periods")
    public List<BookedPeriod> findBookedPeriods(@RequestParam String serial_number) {
        Car car = carRepository.findById(serial_number)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Car not found"));
        return car.getBooked_periods();
    }

    private int correctionTimeZone(Double l) {
        int longitude = (int) Math.floor(l);
        return Math.floorDiv(longitude, 15);
    }

    Comparator<String> comparator = Comparator.comparingInt(String::length);
}
