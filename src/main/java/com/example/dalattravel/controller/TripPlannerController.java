package com.example.dalattravel.controller;

import com.example.dalattravel.dto.TripPlanResult;
import com.example.dalattravel.dto.TripPlannerViewModel;
import com.example.dalattravel.model.*;
import com.example.dalattravel.repository.*;
import com.example.dalattravel.service.TripPlannerService;
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
    private final TripPlannerService tripPlannerService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TripPlannerController(
            CategoryRepository categoryRepository,
            TouristPlaceRepository touristPlaceRepository,
            TransportOptionRepository transportOptionRepository,
            HotelRepository hotelRepository,
            RestaurantRepository restaurantRepository,
            TripPlannerService tripPlannerService) {
        this.categoryRepository = categoryRepository;
        this.touristPlaceRepository = touristPlaceRepository;
        this.transportOptionRepository = transportOptionRepository;
        this.hotelRepository = hotelRepository;
        this.restaurantRepository = restaurantRepository;
        this.tripPlannerService = tripPlannerService;
    }

    @GetMapping
    public String index(Model model) {
        List<Category> categories = categoryRepository.findAll();
        List<TouristPlace> touristPlaces = touristPlaceRepository.findAll();
        List<Hotel> hotels = hotelRepository.findAll();
        List<Restaurant> restaurants = restaurantRepository.findAll();
        List<TransportOption> transportOptions = transportOptionRepository.findAll();

        TripPlannerViewModel viewModel = TripPlannerViewModel.builder()
                .categories(categories)
                .touristPlaces(touristPlaces)
                .hotels(hotels)
                .restaurants(restaurants)
                .transportOptions(transportOptions)
                .numberOfDays(3)
                .budget(BigDecimal.valueOf(5000000))
                .startLatitude(11.9404)
                .startLongitude(108.4583)
                .startLocation("Trung tâm TP. Đà Lạt")
                .build();

        model.addAttribute("model", viewModel);

        // Build rich selectable JSON list combining Places, Hotels, and Restaurants
        List<Map<String, Object>> combinedSelectableList = new ArrayList<>();

        // 1. Tourist Places (Category ID 3)
        for (TouristPlace tp : touristPlaces) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", tp.getId());
            map.put("name", tp.getName());
            map.put("latitude", tp.getLatitude());
            map.put("longitude", tp.getLongitude());
            map.put("categoryId", tp.getCategoryId() != null ? tp.getCategoryId() : 3);
            map.put("categoryName", tp.getCategory() != null ? tp.getCategory().getName() : "Địa điểm du lịch");
            map.put("type", "ATTRACTION");
            map.put("description", tp.getDescription());
            map.put("rating", tp.getRating());
            map.put("ticketPrice", tp.getTicketPrice() != null ? tp.getTicketPrice() : 0);
            combinedSelectableList.add(map);
        }

        // 2. Hotels (Category ID 1)
        for (Hotel h : hotels) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", "H_" + h.getId());
            map.put("name", h.getName());
            map.put("latitude", h.getLatitude() != 0 ? h.getLatitude() : 11.9365);
            map.put("longitude", h.getLongitude() != 0 ? h.getLongitude() : 108.4412);
            map.put("categoryId", 1);
            map.put("categoryName", "Khách sạn");
            map.put("type", "HOTEL");
            map.put("description", h.getAddress() + " - Giá từ " + (h.getPricePerNight() != null ? h.getPricePerNight() : 0) + " VNĐ/đêm");
            map.put("rating", 5);
            map.put("ticketPrice", 0);
            combinedSelectableList.add(map);
        }

        // 3. Restaurants (Category ID 2)
        for (Restaurant r : restaurants) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", "R_" + r.getId());
            map.put("name", r.getName());
            map.put("latitude", r.getLatitude() != 0 ? r.getLatitude() : 11.9404);
            map.put("longitude", r.getLongitude() != 0 ? r.getLongitude() : 108.4383);
            map.put("categoryId", 2);
            map.put("categoryName", "Nhà hàng/Quán ăn");
            map.put("type", "RESTAURANT");
            map.put("description", r.getAddress() + " - Giá trung bình " + (r.getAveragePricePerPerson() != null ? r.getAveragePricePerPerson() : 0) + " VNĐ/người");
            map.put("rating", 5);
            map.put("ticketPrice", 0);
            combinedSelectableList.add(map);
        }

        try {
            model.addAttribute("touristPlacesJson", objectMapper.writeValueAsString(combinedSelectableList));
        } catch (Exception e) {
            model.addAttribute("touristPlacesJson", "[]");
        }

        return "trip-planner/index";
    }

    @PostMapping
    public String planTrip(@ModelAttribute("model") TripPlannerViewModel inputModel, Model model) {
        List<Category> categories = categoryRepository.findAll();
        List<TouristPlace> allPlaces = touristPlaceRepository.findAll();
        List<Hotel> hotels = hotelRepository.findAll();
        List<Restaurant> restaurants = restaurantRepository.findAll();
        List<TransportOption> transportOptions = transportOptionRepository.findAll();

        inputModel.setCategories(categories);
        inputModel.setTouristPlaces(allPlaces);
        inputModel.setHotels(hotels);
        inputModel.setRestaurants(restaurants);
        inputModel.setTransportOptions(transportOptions);

        if (inputModel.getBudget() == null || inputModel.getBudget().compareTo(BigDecimal.valueOf(100000)) < 0) {
            model.addAttribute("errorMessage", "Vui lòng nhập ngân sách dự kiến hợp lệ (Tối thiểu 100,000 VNĐ).");
            return index(model);
        }

        List<TouristPlace> selectedPlaces = new ArrayList<>();
        if (inputModel.getSelectedTouristPlaceIds() != null && !inputModel.getSelectedTouristPlaceIds().isEmpty()) {
            for (String id : inputModel.getSelectedTouristPlaceIds()) {
                if (id.startsWith("H_")) {
                    try {
                        int hId = Integer.parseInt(id.substring(2));
                        Optional<Hotel> hOpt = hotelRepository.findById(hId);
                        if (hOpt.isPresent()) {
                            Hotel h = hOpt.get();
                            selectedPlaces.add(TouristPlace.builder()
                                    .id("H_" + h.getId())
                                    .name(h.getName())
                                    .latitude(h.getLatitude() != 0 ? h.getLatitude() : 11.9365)
                                    .longitude(h.getLongitude() != 0 ? h.getLongitude() : 108.4412)
                                    .description(h.getAddress())
                                    .rating(5)
                                    .build());
                        }
                    } catch (Exception ignored) {}
                } else if (id.startsWith("R_")) {
                    try {
                        int rId = Integer.parseInt(id.substring(2));
                        Optional<Restaurant> rOpt = restaurantRepository.findById(rId);
                        if (rOpt.isPresent()) {
                            Restaurant r = rOpt.get();
                            selectedPlaces.add(TouristPlace.builder()
                                    .id("R_" + r.getId())
                                    .name(r.getName())
                                    .latitude(r.getLatitude() != 0 ? r.getLatitude() : 11.9404)
                                    .longitude(r.getLongitude() != 0 ? r.getLongitude() : 108.4383)
                                    .description(r.getAddress())
                                    .rating(5)
                                    .build());
                        }
                    } catch (Exception ignored) {}
                } else {
                    for (TouristPlace tp : allPlaces) {
                        if (tp.getId().equals(id)) {
                            selectedPlaces.add(tp);
                            break;
                        }
                    }
                }
            }
        }

        if (selectedPlaces.isEmpty()) {
            selectedPlaces = allPlaces.stream().limit(5).collect(Collectors.toList());
        }

        double startLat = (inputModel.getStartLatitude() != null) ? inputModel.getStartLatitude() : 11.9404;
        double startLng = (inputModel.getStartLongitude() != null) ? inputModel.getStartLongitude() : 108.4583;
        String startName = (inputModel.getStartLocation() != null && !inputModel.getStartLocation().trim().isEmpty())
                ? inputModel.getStartLocation() : "Vị trí xuất phát Đà Lạt";

        TripPlanResult planResult = tripPlannerService.generatePlans(
                inputModel.getBudget(),
                inputModel.getNumberOfDays() > 0 ? inputModel.getNumberOfDays() : 3,
                inputModel.getTransportType(),
                startLat,
                startLng,
                startName,
                selectedPlaces);

        model.addAttribute("planResult", planResult);

        try {
            model.addAttribute("planResultJson", objectMapper.writeValueAsString(planResult));
        } catch (Exception e) {
            model.addAttribute("planResultJson", "{}");
        }

        return "trip-planner/result";
    }
}
