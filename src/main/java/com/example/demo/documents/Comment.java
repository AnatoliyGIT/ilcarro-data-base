package com.example.demo.documents;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Builder
@Document(collection = "comments")
public class Comment {
    private String email;
    private String serial_number;
    private String first_name;
    private String second_Name;
    private String photo;
    private String content;
    private LocalDate post_date;
}
