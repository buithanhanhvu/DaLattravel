package com.example.DaLattravel.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "completed_trips")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompletedTrip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private int vehicleId;

    @Column(nullable = false, length = 100)
    private String driverName;

    @Column(length = 20)
    private String driverPhone;

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

    private LocalDateTime actualDepartureTime;
    private LocalDateTime actualArrivalTime;

    private int totalSeats;
    private int occupiedSeats;

    @Column(length = 50)
    private String vehicleType;

    @Column(precision = 18, scale = 2)
    private BigDecimal totalCost;

    @Column(precision = 18, scale = 2)
    private BigDecimal costPerSeat;

    @Column(columnDefinition = "TEXT")
    private String routePolyline;

    @Enumerated(EnumType.ORDINAL)
    @Builder.Default
    private TripStatus status = TripStatus.COMPLETED;

    @Builder.Default
    private LocalDateTime completedAt = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "completedTrip", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<CompletedTripPassenger> passengers = new ArrayList<>();
}
