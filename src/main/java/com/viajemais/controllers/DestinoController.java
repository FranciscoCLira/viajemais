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

import com.viajemais.entities.Categoria;
import com.viajemais.entities.Destino;
import com.viajemais.services.CategoriaService;
import com.viajemais.services.DestinoService;

@Controller
@RequestMapping("/destinos")
public class DestinoController {

    private final DestinoService destinoService;
    private final CategoriaService categoriaService;   

    public DestinoController(DestinoService destinoService,
                             CategoriaService categoriaService) {
        this.destinoService   = destinoService;
        this.categoriaService = categoriaService;
    }
    
    @GetMapping("/categorias")
    public String listarCategorias(Model model) {
        model.addAttribute("categorias", categoriaService.listarTodas());
        return "categorias";
    }
    
    /** Lista todos (GET  /destinos) */
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("destinos", destinoService.listarTodos());
        return "destinos";
    }
    
    /** Formulário de criação (GET /destinos/novo) */
    @GetMapping("/novo")
    public String novo(Model model) {
        // 1) objeto vazio para o formulário
        model.addAttribute("destino", new Destino());
        // 2) lista de categorias para o select (se houver)
        model.addAttribute("categorias", categoriaService.listarTodas());
        // 3) flag de edição = false
        model.addAttribute("editar", false);
        // 4) retorna a view destino-form.html
        return "destino-form";
    }    

    /** Grava novo destino (POST /destinos) */
    @PostMapping
    public String criar(
        @Valid @ModelAttribute("destino") Destino destino,
        BindingResult binding,
        Model model
    ) {
        // 1) validações de formato (Pattern + AssertTrue de parse) já rodaram
        // 2) se não houver erro de formato, valida o mínimo
        if (!binding.hasErrors()) {
            if (destino.getPreco() < 1.00) {
                binding.rejectValue(
                    "precoStr",
                    "precoStr.min",
                    "Preço mínimo é R$ 1,00"
                );
            }
        }

        // 3) se sobrou qualquer erro, volta ao form
        if (binding.hasErrors()) {
            model.addAttribute("categorias", categoriaService.listarTodas());
            model.addAttribute("editar", false);
            return "destino-form";
        }

    	
        // 4) Resolve o ID em Categoria de verdade  
        Long catId = destino.getCategoria().getId();
        Categoria cat = categoriaService.buscarPorId(catId).orElse(null);
        if (cat == null) {
            binding.rejectValue("categoria", "categoria.invalida",
                "Categoria selecionada não existe");
            model.addAttribute("categorias", categoriaService.listarTodas());
            model.addAttribute("editar", false); 
            return "destino-form";
        }
        destino.setCategoria(cat);
        
        // 4) salvar
        destinoService.salvar(destino);
        return "redirect:/destinos";
    }

    /** Salva edição (POST /destinos/editar) */
    @PostMapping("/editar")
    public String atualizar(
        @Valid @ModelAttribute("destino") Destino destino,
        BindingResult binding,
        Model model
    ) {
        if (!binding.hasErrors()) {
            if (destino.getPreco() < 1.00) {
                binding.rejectValue(
                    "precoStr",
                    "precoStr.min",
                    "Preço mínimo é R$ 1,00"
                );
            }
        }
        if (binding.hasErrors()) {
            model.addAttribute("categorias", categoriaService.listarTodas());
            model.addAttribute("editar", true);
            return "destino-form";
        }
    	
        /** Mesma resolução de Categoria para edição  */
        Long catId = destino.getCategoria().getId();
        Categoria cat = categoriaService.buscarPorId(catId).orElse(null);
        if (cat == null) {
            binding.rejectValue("categoria", "categoria.invalida",
                "Categoria selecionada não existe");
            model.addAttribute("categorias", categoriaService.listarTodas());
            model.addAttribute("editar", true); 
            return "destino-form";
        }
        destino.setCategoria(cat);
        
        destinoService.salvar(destino);
        return "redirect:/destinos";
    }
    
    /** Formulário de edição (GET /destinos/editar/{id}) */
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Destino d = destinoService.buscarPorId(id);
        model.addAttribute("destino", d);
        model.addAttribute("categorias", categoriaService.listarTodas());
        model.addAttribute("editar", true);
        return "destino-form";
    }
 
    /** Exclusão (GET /destinos/excluir/{id}) */
    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id) {
        destinoService.excluir(id);
        return "redirect:/destinos";
    }

	/*
	 * @InitBinder public void initBinder(WebDataBinder binder) { // binder para
	 * preço em formato "1.234,56" binder.registerCustomEditor(Double.class,
	 * "preco", new PropertyEditorSupport() {
	 * 
	 * @Override public void setAsText(String text) { if (text == null ||
	 * text.isBlank()) { setValue(null); } else { // remove pontos de milhar e troca
	 * vírgula por ponto decimal String normalized = text.replace(".",
	 * "").replace(",", "."); setValue(Double.parseDouble(normalized)); } } }); }
	 */
}
