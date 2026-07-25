package com.example.dalattravel.controller;

import com.example.dalattravel.repository.FestivalRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/festivals")
public class FestivalController {

    private final FestivalRepository festivalRepository;

    public FestivalController(FestivalRepository festivalRepository) {
        this.festivalRepository = festivalRepository;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("festivals", festivalRepository.findByActiveTrue());
        return "festivals/index";
    }
}
