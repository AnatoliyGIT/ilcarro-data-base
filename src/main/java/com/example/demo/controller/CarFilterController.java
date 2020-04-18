package com.example.demo.controller;

import com.example.demo.documents.Filter;
import com.example.demo.repository.CarFilterRepository;
import com.example.demo.repository.FilterUpdateRepository;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/filter")
public class CarFilterController {

    CarFilterRepository carFilterRepository;
    FilterUpdateRepository filterUpdateRepository;

    @Autowired
    public CarFilterController(CarFilterRepository carFilterRepository
            , FilterUpdateRepository filterUpdateRepository) {
        this.carFilterRepository = carFilterRepository;
        this.filterUpdateRepository = filterUpdateRepository;
    }

    @ApiOperation(value = "(УДАЛИТЬ весь фильтр)")
    @DeleteMapping("del_root")
    public void delRoot(@RequestHeader("Authorization") String tokenAdmin) {
        if (!tokenAdmin.equals("YW5hdG9seUBtYWlsLmNvbTpBbmF0b2x5MjAyMDIw"))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin unauthorized");
        filterUpdateRepository.findById("root").orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Root not found"));
        filterUpdateRepository.deleteById("root");
        throw new ResponseStatusException(HttpStatus.OK, "OK");
    }

    @GetMapping("getAllFilters")
    public List<Filter> filterUpdate() {
        return filterUpdateRepository.findAll();
    }
}
