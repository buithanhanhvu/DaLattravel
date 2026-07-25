package com.example.DaLattravel.dto;

import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OsrmLeg {

    private double distanceMeters;
    private double durationSeconds;

    @Builder.Default
    private List<List<Double>> steps = new ArrayList<>();
}
