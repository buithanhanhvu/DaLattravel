package com.example.DaLattravel.dto;

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
public class CarpoolTripInfo {

    private int tripId;
    private String pickupAddress;
    private String dropoffAddress;
    private LocalDateTime departureTime;
    private int totalSeats;
    private int availableSeats;
    private BigDecimal totalCost;
    private BigDecimal costPerSeat;
    private String driverName;
    private String driverPhone;
    private String licensePlate;
    private String vehicleType;
    private Double tripDistance;

    @Builder.Default
    private List<CarpoolPassengerInfo> passengers = new ArrayList<>();

    private Integer currentPassengerId;
    private BigDecimal currentPassengerCost;

    public boolean isFull() {
        return availableSeats <= 0;
    }

    public boolean isCanCancel() {
        return currentPassengerId != null;
    }
}
