package com.viajemais.controllers;
// iniciar e confirmar contratação

import com.viajemais.entities.Contratacao;
import com.viajemais.entities.ContratacaoDestino;
import com.viajemais.entities.Destino;
import com.viajemais.services.ContratacaoDestinoService;
import com.viajemais.services.ContratacaoService;
import com.viajemais.services.DestinoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/contratacao")
public class ContratacaoController {

    private final ContratacaoService contratacaoService;
    private final DestinoService destinoService;
    private final ContratacaoDestinoService contratacaoDestinoService;

    public ContratacaoController(
            ContratacaoService contratacaoService,
            DestinoService destinoService,
            ContratacaoDestinoService contratacaoDestinoService) {
        this.contratacaoService = contratacaoService;
        this.destinoService = destinoService;
        this.contratacaoDestinoService = contratacaoDestinoService;
    }

    @GetMapping("/novo")
    public String novaContratacaoForm(@RequestParam List<Long> destinos, Model model) {
        List<Destino> selecionados = destinos.stream()
                .map(destinoService::buscarPorId)
                .toList();

        model.addAttribute("destinosSelecionados", selecionados);
        return "contratacao-form"; // contratacao-form.html
    }

    @PostMapping("/confirmar")
    public String confirmarContratacao(@RequestParam List<Long> destinos) {
        Contratacao contratacao = new Contratacao();
        contratacao.setData(LocalDate.now());
        contratacao = contratacaoService.salvar(contratacao);

        for (Long id : destinos) {
            Destino destino = destinoService.buscarPorId(id);
            ContratacaoDestino item = new ContratacaoDestino(contratacao, destino);
            contratacaoDestinoService.salvar(item);
        }

        return "redirect:/";
    }

    @PostMapping("/cancelar")
    public String cancelarContratacao() {
        return "redirect:/";
    }
}

