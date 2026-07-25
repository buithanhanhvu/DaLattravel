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
public class PDPTWRoute {

    @Builder.Default
    private List<PDPTWNode> nodes = new ArrayList<>();

    private double totalDistance;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal totalCost;
}
