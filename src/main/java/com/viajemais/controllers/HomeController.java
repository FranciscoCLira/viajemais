package com.viajemais.controllers;
// listar destinos e pagina inicial

import com.viajemais.entities.Destino;
import com.viajemais.services.CategoriaService;
import com.viajemais.services.DestinoService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class HomeController {

    private final DestinoService destinoService;
    private final CategoriaService categoriaService;

    public HomeController(DestinoService destinoService,
                          CategoriaService categoriaService) {
		this.destinoService   = destinoService;
		this.categoriaService = categoriaService;
    }


 // src/main/java/com/viajemais/controllers/HomeController.java
    @GetMapping("/")
    public String index(
            @RequestParam(required = false) String destinoNome,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) Double precoMax,
            Model model) {

        // 1) lista de categorias para o filtro
        model.addAttribute("categorias",
                           categoriaService.listarTodas());

        // 2) aplica todos (0–3) filtros opcionalmente
        List<Destino> destinosFiltrados =
             destinoService.buscarComFiltro(destinoNome,
                                            categoria,
                                            precoMax);
        model.addAttribute("destinos", destinosFiltrados);

        // 3) repõe os valores no form para não limpar enquanto não clicar em "Limpar"
        model.addAttribute("filtroDestino",   destinoNome);
        model.addAttribute("filtroCategoria", categoria);
        model.addAttribute("filtroPrecoMax",  precoMax);

        return "index";
    }

    
    /** Endpoint para autocomplete de destinos */
//    @GetMapping("/destinos/sugerir")
//    @ResponseBody
//    public List<String> sugerirDestino(@RequestParam String prefix) {
//        return destinoService.sugerirNomes(prefix);
//    }
}
