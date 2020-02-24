package com.example.demo.model.documents;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Builder
public class PickUpPlace {
    private String place_id;
    private Location geolocation;
}
