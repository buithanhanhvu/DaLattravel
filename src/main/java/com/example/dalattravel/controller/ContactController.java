package com.example.dalattravel.controller;

import com.example.dalattravel.model.Contact;
import com.example.dalattravel.repository.ContactRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/contact")
public class ContactController {

    private final ContactRepository contactRepository;

    public ContactController(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("contact", new Contact());
        return "contact/index";
    }

    @PostMapping
    public String submitContact(@ModelAttribute("contact") Contact contact, Model model) {
        contactRepository.save(contact);
        model.addAttribute("successMessage", "Cảm ơn bạn đã liên hệ! Chúng tôi sẽ phản hồi sớm nhất.");
        return "contact/index";
    }
}
