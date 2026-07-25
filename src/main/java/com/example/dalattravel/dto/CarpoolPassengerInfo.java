package com.example.DaLattravel.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarpoolPassengerInfo {

    private int passengerId;
    private String name;
    private int seats;
    private BigDecimal cost;
    private Double distanceKm;

    public String getDistanceDisplay() {
        return distanceKm != null ? String.format("%.1f km", distanceKm) : "Đang tính...";
    }
}
