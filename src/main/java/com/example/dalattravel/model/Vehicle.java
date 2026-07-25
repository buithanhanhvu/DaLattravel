package com.example.DaLattravel.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehicles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String driverName;

    @Column(length = 20)
    private String licensePlate;

    private double pickupLatitude;
    private double pickupLongitude;

    @Column(length = 200)
    private String pickupAddress;

    private double dropoffLatitude;
    private double dropoffLongitude;

    @Column(length = 200)
    private String dropoffAddress;

    @Column(nullable = false)
    private LocalDateTime departureTime;

    private int totalSeats;
    private int availableSeats;

    @Column(precision = 18, scale = 2)
    private BigDecimal costPerKm;

    @Column(precision = 18, scale = 2)
    private BigDecimal fixedPrice;

    @Column(length = 50)
    private String vehicleType;

    @Column(columnDefinition = "TEXT")
    private String routePolyline; // Polyline JSON Array of [lon, lat]

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    private boolean active = true;
}
