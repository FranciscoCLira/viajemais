package com.viajemais.controllers;

import com.viajemais.services.DestinoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DestinoController {

    private final DestinoService destinoService;

    public DestinoController(DestinoService destinoService) {
        this.destinoService = destinoService;
    }

    @GetMapping("/destinos")
    public String listarDestinos(Model model) {
        model.addAttribute("destinos", destinoService.listarTodos());
        return "destinos";
    }
}
