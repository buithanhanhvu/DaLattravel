package com.example.DaLattravel.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoutePoint {

    private double latitude;
    private double longitude;
    private String address;
    private String type; // "pickup" or "dropoff"
    private Integer passengerId;
    private LocalDateTime time;
    private int sequence;
}
