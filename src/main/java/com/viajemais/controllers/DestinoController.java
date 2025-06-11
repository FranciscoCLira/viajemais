package com.viajemais.controllers;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;

import java.beans.PropertyEditorSupport;

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
        Destino d = new Destino();
        d.setPreco(0.0);               // evita null no campo preco
        model.addAttribute("destino", d);
        model.addAttribute("editar", false);
        return "destino-form";
    }
    
    @PostMapping
    public String criar(
            @Valid @ModelAttribute("destino") Destino destino,
            BindingResult binding,
            Model model) {

        if (binding.hasErrors()) {
            model.addAttribute("editar", false);
            return "destino-form";
        }

        destinoService.salvar(destino);
        return "redirect:/destinos";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Destino d = destinoService.buscarPorId(id);
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

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        // binder para preço em formato "1.234,56"
        binder.registerCustomEditor(Double.class, "preco", new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                if (text == null || text.isBlank()) {
                    setValue(null);
                } else {
                    // remove pontos de milhar e troca vírgula por ponto decimal
                    String normalized = text.replace(".", "").replace(",", ".");
                    setValue(Double.parseDouble(normalized));
                }
            }
        });
    }

}
