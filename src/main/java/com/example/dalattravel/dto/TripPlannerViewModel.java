package com.example.DaLattravel.dto;

import com.example.DaLattravel.model.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripPlannerViewModel {

    private BigDecimal budget;

    @Builder.Default
    private List<String> selectedTouristPlaceIds = new ArrayList<>();

    private int numberOfDays;
    private String transportType;

    @Builder.Default
    private List<TransportOption> transportOptions = new ArrayList<>();

    @Builder.Default
    private List<Hotel> hotels = new ArrayList<>();

    @Builder.Default
    private List<Restaurant> restaurants = new ArrayList<>();

    @Builder.Default
    private List<Attraction> attractions = new ArrayList<>();

    @Builder.Default
    private List<String> suggestions = new ArrayList<>();

    @Builder.Default
    private List<TouristPlace> touristPlaces = new ArrayList<>();

    private String startLocation;
    private Double startLatitude;
    private Double startLongitude;
    private double distanceKm;

    @Builder.Default
    private List<Category> categories = new ArrayList<>();

    private Integer selectedCategoryId;
    private Integer selectedTransportId;
    private Integer maxPlaces;
}
