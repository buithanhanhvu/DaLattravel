package com.example.DaLattravel.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "passengers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Passenger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

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

    @Column(nullable = false)
    private LocalDateTime preferredDepartureTime;

    private LocalDateTime preferredArrivalTime;

    private Integer groupId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "groupId", insertable = false, updatable = false)
    private PassengerGroup group;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    private boolean matched = false;

    private Integer matchedVehicleId;

    private Integer pickupOrder;
    private Integer dropoffOrder;
}
