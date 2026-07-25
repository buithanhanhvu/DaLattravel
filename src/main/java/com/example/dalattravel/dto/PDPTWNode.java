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
public class PDPTWNode {

    private double latitude;
    private double longitude;
    private String address;
    private String type; // "pickup" or "dropoff"
    private Integer passengerId;
    private LocalDateTime earliestTime;
    private LocalDateTime latestTime;

    @Builder.Default
    private int serviceTime = 5;
}
