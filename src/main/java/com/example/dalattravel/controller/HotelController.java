package com.example.DaLattravel.controller;

import com.example.DaLattravel.model.Hotel;
import com.example.DaLattravel.repository.HotelRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/hotels")
public class HotelController {

    private final HotelRepository hotelRepository;

    public HotelController(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("hotels", hotelRepository.findAll());
        return "hotels/index";
    }
}
