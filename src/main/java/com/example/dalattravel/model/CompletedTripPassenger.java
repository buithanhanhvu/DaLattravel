package com.example.dalattravel.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "completed_trip_passengers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompletedTripPassenger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer completedTripId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "completedTripId", insertable = false, updatable = false)
    private CompletedTrip completedTrip;

    private int originalPassengerId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 20)
    private String phoneNumber;

    private double pickupLatitude;
    private double pickupLongitude;

    @Column(length = 200)
    private String pickupAddress;

    private double dropoffLatitude;
    private double dropoffLongitude;

    @Column(length = 200)
    private String dropoffAddress;

    private Integer pickupOrder;
    private Integer dropoffOrder;

    @Builder.Default
    private int requiredSeats = 1;

    @Column(precision = 18, scale = 2)
    private BigDecimal cost;

    private Integer groupId;

    @Column(length = 100)
    private String groupName;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
