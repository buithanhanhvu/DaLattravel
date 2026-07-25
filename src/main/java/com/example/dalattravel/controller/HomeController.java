package com.example.DaLattravel.controller;

import com.example.DaLattravel.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final TouristPlaceRepository touristPlaceRepository;
    private final BlogPostRepository blogPostRepository;
    private final FestivalRepository festivalRepository;

    public HomeController(
            TouristPlaceRepository touristPlaceRepository,
            BlogPostRepository blogPostRepository,
            FestivalRepository festivalRepository) {
        this.touristPlaceRepository = touristPlaceRepository;
        this.blogPostRepository = blogPostRepository;
        this.festivalRepository = festivalRepository;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("touristPlaces", touristPlaceRepository.findAll());
        model.addAttribute("blogPosts", blogPostRepository.findAll());
        model.addAttribute("festivals", festivalRepository.findByActiveTrue());
        return "index";
    }
}
