package com.viajemais.controllers;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;

import jakarta.validation.Valid;

import com.viajemais.entities.Destino;
import com.viajemais.services.DestinoService;

@Controller
@RequestMapping("/destinos")
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
    
    // listagem
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("destinos", destinoService.listarTodos());
        return "destinos";
    }

    // novo
    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("destino", new Destino());
        model.addAttribute("editar", false);
        return "destino-form";
    }
    @PostMapping
    public String criar(@Valid @ModelAttribute Destino destino, BindingResult br) {
        if (br.hasErrors()) return "destino-form";
        destinoService.salvar(destino);
        return "redirect:/destinos";
    }

    // editar
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        var d = destinoService.buscarPorId1(id)
                  .orElseThrow(() -> new IllegalArgumentException("Não encontrado: "+id));
        model.addAttribute("destino", d);
        model.addAttribute("editar", true);
        return "destino-form";
    }
    @PostMapping("/editar")
    public String atualizar(@Valid @ModelAttribute Destino destino, BindingResult br) {
        if (br.hasErrors()) return "destino-form";
        destinoService.salvar(destino);
        return "redirect:/destinos";
    }

    // excluir
    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id) {
        destinoService.excluir(id);
        return "redirect:/destinos";
    }
    
}
