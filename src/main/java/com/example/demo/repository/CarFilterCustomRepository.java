package com.example.demo.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

public interface CarFilterCustomRepository {
    Page getAllCarsByFilter(String make, String model, String year, String fuel
            , String gear, String wheels_drive, int items_on_page, int current_page);

    Page getAllCarsByDateLocation(double radius, double latitude, double longitude, LocalDateTime dateStart
            , LocalDateTime dateEnd, double min_amount, double max_amount
            , boolean ascending, int items_on_page, int current_page);

    Page getAllCarsByLocation(double latitude, double longitude, double radius
            , int items_on_page, int current_page);

    Page getCarsByAllFilters(int current_page, int items_on_page, String make
            , String model, String wheels_drive
            , String year, String gear, String fuel
            , String engine, double latitude, double longitude
            , double radius, LocalDateTime date_start
            , LocalDateTime date_end, double min_amount, double max_amount
            , boolean ascending);

    JsonNode getFilters() throws JsonProcessingException;
}
