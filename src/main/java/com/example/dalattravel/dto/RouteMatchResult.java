package com.example.DaLattravel.dto;

import com.example.DaLattravel.model.Vehicle;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteMatchResult {

    private boolean canMatch;
    private Vehicle matchedVehicle;
    private Integer pickupOrder;
    private Integer dropoffOrder;
    private String reason;
}
