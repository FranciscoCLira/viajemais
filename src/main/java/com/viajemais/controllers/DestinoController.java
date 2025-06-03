package com.viajemais.controllers;

import org.springframework.web.bind.annotation.*;

import com.viajemais.entities.Destino;
import com.viajemais.services.DestinoService;

import java.util.List;

@RestController
@RequestMapping("/destinos")
public class DestinoController {

    private final DestinoService destinoService;

    public DestinoController(DestinoService destinoService) {
        this.destinoService = destinoService;
    }

    @GetMapping
    public List<Destino> listar() {
        return destinoService.listarTodos();
    }

    @PostMapping
    public Destino adicionar(@RequestBody Destino destino) {
        return destinoService.salvar(destino);
    }
}
