package com.example.springbootapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Home Controller
 * Handles the home page request
 */
@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "index";
    }
}
