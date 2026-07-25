package com.example.DaLattravel.controller;

import com.example.DaLattravel.dto.TransportPriceResult;
import com.example.DaLattravel.dto.TripPlannerViewModel;
import com.example.DaLattravel.model.*;
import com.example.DaLattravel.repository.*;
import com.example.DaLattravel.service.KMeansClusteringService;
import com.example.DaLattravel.service.TransportPriceCalculator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/trip-planner")
public class TripPlannerController {

    private final CategoryRepository categoryRepository;
    private final TouristPlaceRepository touristPlaceRepository;
    private final TransportOptionRepository transportOptionRepository;
    private final HotelRepository hotelRepository;
    private final RestaurantRepository restaurantRepository;
    private final AttractionRepository attractionRepository;
    private final TransportPriceCalculator priceCalculator;
    private final KMeansClusteringService kmeansService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TripPlannerController(
            CategoryRepository categoryRepository,
            TouristPlaceRepository touristPlaceRepository,
            TransportOptionRepository transportOptionRepository,
            HotelRepository hotelRepository,
            RestaurantRepository restaurantRepository,
            AttractionRepository attractionRepository,
            TransportPriceCalculator priceCalculator,
            KMeansClusteringService kmeansService) {
        this.categoryRepository = categoryRepository;
        this.touristPlaceRepository = touristPlaceRepository;
        this.transportOptionRepository = transportOptionRepository;
        this.hotelRepository = hotelRepository;
        this.restaurantRepository = restaurantRepository;
        this.attractionRepository = attractionRepository;
        this.priceCalculator = priceCalculator;
        this.kmeansService = kmeansService;
    }

    @GetMapping
    public String index(Model model) {
        List<Category> categories = categoryRepository.findAll();
        List<TouristPlace> touristPlaces = touristPlaceRepository.findAll();
        List<TransportOption> transportOptions = transportOptionRepository.findAll();

        TripPlannerViewModel viewModel = TripPlannerViewModel.builder()
                .categories(categories)
                .touristPlaces(touristPlaces)
                .transportOptions(transportOptions)
                .hotels(new ArrayList<>())
                .restaurants(new ArrayList<>())
                .attractions(new ArrayList<>())
                .suggestions(new ArrayList<>())
                .build();

        model.addAttribute("model", viewModel);

        try {
            List<Map<String, Object>> placeJsonList = touristPlaces.stream().map(tp -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", tp.getId());
                map.put("name", tp.getName());
                map.put("latitude", tp.getLatitude());
                map.put("longitude", tp.getLongitude());
                map.put("categoryId", tp.getCategoryId());
                return map;
            }).collect(Collectors.toList());
            model.addAttribute("touristPlacesJson", objectMapper.writeValueAsString(placeJsonList));
        } catch (Exception e) {
            model.addAttribute("touristPlacesJson", "[]");
        }

        return "trip-planner/index";
    }

    @PostMapping
    public String planTrip(@ModelAttribute("model") TripPlannerViewModel inputModel, Model model) {
        List<Category> categories = categoryRepository.findAll();
        List<TouristPlace> allPlaces = touristPlaceRepository.findAll();
        List<TransportOption> transportOptions = transportOptionRepository.findAll();

        inputModel.setCategories(categories);
        inputModel.setTouristPlaces(allPlaces);
        inputModel.setTransportOptions(transportOptions);

        if (inputModel.getBudget() == null || inputModel.getBudget().compareTo(BigDecimal.valueOf(100000)) < 0) {
            model.addAttribute("errorMessage", "Ngân sách tối thiểu 100,000 VNĐ cho chuyến đi Đà Lạt.");
            return index(model);
        }

        List<String> suggestions = new ArrayList<>();
        List<TouristPlace> selectedPlaces = new ArrayList<>();

        if (inputModel.getSelectedTouristPlaceIds() != null && !inputModel.getSelectedTouristPlaceIds().isEmpty()) {
            selectedPlaces = allPlaces.stream()
                    .filter(tp -> inputModel.getSelectedTouristPlaceIds().contains(tp.getId()))
                    .collect(Collectors.toList());
        }

        if (selectedPlaces.isEmpty()) {
            selectedPlaces = allPlaces.stream().limit(5).collect(Collectors.toList());
        }

        if (inputModel.getStartLatitude() != null && inputModel.getStartLongitude() != null && inputModel.getSelectedTransportId() != null) {
            TransportPriceResult priceResult = priceCalculator.getFinalPrice(
                    inputModel.getStartLatitude(), inputModel.getStartLongitude(), inputModel.getSelectedTransportId());
            suggestions.add(String.format("Chi phí di chuyển ước tính: %,d VNĐ (%s)", priceResult.getPrice().longValue(), priceResult.getNote()));
        }

        if (inputModel.getNumberOfDays() > 0 && !selectedPlaces.isEmpty()) {
            int k = Math.max(1, Math.min(inputModel.getNumberOfDays(), selectedPlaces.size()));
            List<Passenger> dummyPassengers = selectedPlaces.stream().map(p -> {
                Passenger pass = new Passenger();
                pass.setId(Math.abs(p.getId().hashCode()));
                pass.setPickupLatitude(p.getLatitude());
                pass.setPickupLongitude(p.getLongitude());
                pass.setName(p.getName());
                return pass;
            }).collect(Collectors.toList());

            List<KMeansClusteringService.Cluster> clusters = kmeansService.clusterPassengers(dummyPassengers, k);

            for (int i = 0; i < clusters.size(); i++) {
                KMeansClusteringService.Cluster cluster = clusters.get(i);
                String dayNames = cluster.getPoints().stream().map(pt -> pt.getPassenger().getName()).collect(Collectors.joining(", "));
                suggestions.add(String.format("Ngày %d: Khám phá các địa điểm gần nhau -> %s", i + 1, dayNames));
            }
        }

        inputModel.setSuggestions(suggestions);
        model.addAttribute("model", inputModel);

        try {
            model.addAttribute("touristPlacesJson", objectMapper.writeValueAsString(allPlaces));
        } catch (Exception ignored) {}

        return "trip-planner/result";
    }
}
