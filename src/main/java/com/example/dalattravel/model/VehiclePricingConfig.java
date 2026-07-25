package com.example.DaLattravel.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "VehiclePricingConfigs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehiclePricingConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private int seatCapacity;

    @Column(nullable = false, length = 100)
    private String vehicleTypeName;

    @Column(precision = 18, scale = 2, nullable = false)
    private BigDecimal fuelPricePerKm;

    @Column(precision = 18, scale = 2, nullable = false)
    private BigDecimal driverSalaryPerTrip;

    @Column(precision = 18, scale = 2, nullable = false)
    private BigDecimal tollFee;

    @Column(precision = 5, scale = 2, nullable = false)
    private BigDecimal profitMargin;

    @Column(precision = 18, scale = 2, nullable = false)
    private BigDecimal minimumTripCost;

    @Builder.Default
    private boolean active = true;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
