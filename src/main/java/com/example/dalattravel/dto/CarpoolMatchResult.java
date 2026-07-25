package com.example.dalattravel.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarpoolMatchResult {

    private int vehicleId;
    private String driverName;
    private String licensePlate;
    private String vehicleType;
    private String driverPhone;
    private int totalSeats;
    private int availableSeats;
    private int occupiedSeats;

    @Builder.Default
    private List<PassengerMatch> matchedPassengers = new ArrayList<>();

    @Builder.Default
    private List<RoutePoint> optimizedRoute = new ArrayList<>();

    private BigDecimal totalCost;
    private BigDecimal costPerPassenger;
    private double totalDistance;
    private LocalDateTime estimatedDepartureTime;
    private LocalDateTime estimatedArrivalTime;
    private String pickupAddress;
    private String dropoffAddress;
}
