package com.example.dalattravel.controller;

import com.example.dalattravel.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final TouristPlaceRepository touristPlaceRepository;
    private final HotelRepository hotelRepository;
    private final RestaurantRepository restaurantRepository;
    private final BlogPostRepository blogPostRepository;
    private final FestivalRepository festivalRepository;

    public HomeController(
            TouristPlaceRepository touristPlaceRepository,
            HotelRepository hotelRepository,
            RestaurantRepository restaurantRepository,
            BlogPostRepository blogPostRepository,
            FestivalRepository festivalRepository) {
        this.touristPlaceRepository = touristPlaceRepository;
        this.hotelRepository = hotelRepository;
        this.restaurantRepository = restaurantRepository;
        this.blogPostRepository = blogPostRepository;
        this.festivalRepository = festivalRepository;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("touristPlaces", touristPlaceRepository.findAll().stream().limit(6).toList());
        model.addAttribute("hotels", hotelRepository.findAll().stream().limit(6).toList());
        model.addAttribute("restaurants", restaurantRepository.findAll().stream().limit(6).toList());
        model.addAttribute("blogPosts", blogPostRepository.findAll().stream().limit(3).toList());
        model.addAttribute("festivals", festivalRepository.findByActiveTrue());
        return "index";
    }
}
