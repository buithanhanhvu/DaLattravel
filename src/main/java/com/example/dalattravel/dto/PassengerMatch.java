package com.example.dalattravel.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PassengerMatch {

    private int passengerId;
    private String passengerName;
    private String pickupAddress;
    private String dropoffAddress;
    private LocalDateTime pickupTime;
    private LocalDateTime dropoffTime;
    private BigDecimal cost;
    private int sequenceOrder;
}
