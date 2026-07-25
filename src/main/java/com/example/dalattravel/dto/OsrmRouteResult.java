package com.example.DaLattravel.dto;

import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OsrmRouteResult {

    private double distanceMeters;
    private double durationSeconds;

    @Builder.Default
    private List<List<Double>> geometry = new ArrayList<>(); // [[lon, lat], ...]

    @Builder.Default
    private List<OsrmLeg> legs = new ArrayList<>();
}
