package com.example.DaLattravel.controller;

import com.example.DaLattravel.model.BlogPost;
import com.example.DaLattravel.repository.BlogPostRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/blog")
public class BlogController {

    private final BlogPostRepository blogPostRepository;

    public BlogController(BlogPostRepository blogPostRepository) {
        this.blogPostRepository = blogPostRepository;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("posts", blogPostRepository.findAll());
        return "blog/index";
    }

    @GetMapping("/{id}")
    public String details(@PathVariable("id") Integer id, Model model) {
        Optional<BlogPost> post = blogPostRepository.findById(id);
        if (post.isEmpty()) {
            return "redirect:/blog";
        }
        model.addAttribute("post", post.get());
        return "blog/details";
    }
}
