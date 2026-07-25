package com.example.dalattravel.controller;

import com.example.dalattravel.model.TouristPlace;
import com.example.dalattravel.repository.CategoryRepository;
import com.example.dalattravel.repository.TouristPlaceRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/tourist-places")
public class TouristPlaceController {

    private final TouristPlaceRepository touristPlaceRepository;
    private final CategoryRepository categoryRepository;

    public TouristPlaceController(TouristPlaceRepository touristPlaceRepository, CategoryRepository categoryRepository) {
        this.touristPlaceRepository = touristPlaceRepository;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public String index(@RequestParam(value = "categoryId", required = false) Integer categoryId, Model model) {
        List<TouristPlace> places;
        if (categoryId != null) {
            places = touristPlaceRepository.findByCategoryId(categoryId);
        } else {
            places = touristPlaceRepository.findAll();
        }
        model.addAttribute("places", places);
        model.addAttribute("categories", categoryRepository.findAll());
        return "tourist-places/index";
    }

    @GetMapping("/{id}")
    public String details(@PathVariable("id") String id, Model model) {
        Optional<TouristPlace> place = touristPlaceRepository.findById(id);
        if (place.isEmpty()) {
            return "redirect:/tourist-places";
        }
        model.addAttribute("place", place.get());
        return "tourist-places/details";
    }
}
