package com.example.demo.documents;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.ArrayList;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Builder
@Document(collection = "users")
public class User {
    @Id
    private String email;
    private String password;
    private String firstName;
    private String secondName;
    private String avatar;
    private boolean isActive;
    private String role;
    private String activationCode;
    private LocalDate registrationDate;
    private ArrayList<Comment> comments;
    private ArrayList<Car> ownerCars;
    private ArrayList<BookedCars> bookedCars;
    private ArrayList<History> history;
}