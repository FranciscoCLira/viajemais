package com.viajemais.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.viajemais.repositories.DestinoRepository;

@Controller
public class HomeController {
    private final DestinoRepository destinoRepo;

    public HomeController(DestinoRepository destinoRepo) {
        this.destinoRepo = destinoRepo;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("destinos", destinoRepo.findAll());
        return "index";
    }
}