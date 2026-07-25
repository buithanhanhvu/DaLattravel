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
public class CarpoolViewModel {

    private String passengerName;
    private String phoneNumber;

    private String pickupAddress;
    private double pickupLatitude;
    private double pickupLongitude;

    private String dropoffAddress;
    private double dropoffLatitude;
    private double dropoffLongitude;

    @Builder.Default
    private LocalDateTime preferredDepartureTime = LocalDateTime.now().plusHours(1);

    @Builder.Default
    private int numberOfPassengers = 1;

    @Builder.Default
    private int requestedVehicleSeats = 4;

    @Builder.Default
    private List<Integer> seatOptions = List.of(4, 7, 9);

    private boolean isGroup;
    private boolean privateGroup;

    private String statusMessage;
    private CarpoolTripInfo assignedTrip;

    @Builder.Default
    private List<CarpoolTripInfo> assignedTrips = new ArrayList<>();

    @Builder.Default
    private List<CarpoolTripInfo> openTrips = new ArrayList<>();

    public boolean isHasAssignment() {
        return assignedTrip != null || (assignedTrips != null && !assignedTrips.isEmpty());
    }
}
