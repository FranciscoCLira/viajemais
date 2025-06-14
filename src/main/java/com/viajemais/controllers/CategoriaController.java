package com.viajemais.controllers;

import com.viajemais.entities.Categoria;
import com.viajemais.services.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/categorias")
public class CategoriaController {
    private final CategoriaService svc;
    public CategoriaController(CategoriaService svc) { this.svc=svc; }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("categorias", svc.listarTodas());
        return "categorias";
    }
    @GetMapping("/novo")
    public String novo(Model m) {
        m.addAttribute("categoria", new Categoria());
        m.addAttribute("editar", false);
        return "categoria-form";
    }
    @PostMapping
    public String criar(@Valid @ModelAttribute Categoria categoria,
                        BindingResult b, Model m) {
        if (b.hasErrors()) return "categoria-form";
        svc.salvar(categoria);
        return "redirect:/categorias";
    }
    
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model m) {
        Categoria c = svc.buscarPorId(id)
                         .orElseThrow();
        m.addAttribute("categoria", c);
        m.addAttribute("editar", true);
        return "categoria-form";
    }
    
    @PostMapping("/editar")
    public String atualizar(@Valid @ModelAttribute Categoria categoria,
                            BindingResult b) {
    	
        if (b.hasErrors()) return "categoria-form";
        svc.salvar(categoria);
        return "redirect:/categorias";
    }
    
    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id) {
        svc.excluir(id);
        return "redirect:/categorias";
    }
}
