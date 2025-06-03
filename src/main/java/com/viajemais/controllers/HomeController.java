package com.viajemais.controllers;
// listar destinos e pagina inicial

import com.viajemais.services.DestinoService;
import com.viajemais.entities.Destino;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    private final DestinoService destinoService;

    public HomeController(DestinoService destinoService) {
        this.destinoService = destinoService;
    }

    @GetMapping("/")
    public String home(Model model) {
        List<Destino> destinos = destinoService.listarTodos();
        model.addAttribute("destinos", destinos);
        return "index"; // index.html no templates
    }
}
