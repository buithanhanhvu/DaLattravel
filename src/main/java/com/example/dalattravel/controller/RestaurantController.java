package com.example.DaLattravel.controller;

import com.example.DaLattravel.model.Restaurant;
import com.example.DaLattravel.repository.RestaurantRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/restaurants")
public class RestaurantController {

    private final RestaurantRepository restaurantRepository;

    public RestaurantController(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("restaurants", restaurantRepository.findAll());
        return "restaurants/index";
    }
}
