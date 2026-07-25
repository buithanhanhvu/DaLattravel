package com.example.dalattravel.dto;

import com.example.dalattravel.model.Vehicle;
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
