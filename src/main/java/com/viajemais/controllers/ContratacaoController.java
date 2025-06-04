package com.viajemais.controllers;

import com.viajemais.entities.Contratacao;
import com.viajemais.entities.Destino;
import com.viajemais.services.ContratacaoDestinoService;
import com.viajemais.services.ContratacaoService;
import com.viajemais.services.DestinoService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/contratacao")
public class ContratacaoController {

    private final DestinoService destinoService;
    private final ContratacaoService contratacaoService;
    private final ContratacaoDestinoService contratacaoDestinoService;

    public ContratacaoController(DestinoService destinoService, ContratacaoService contratacaoService, ContratacaoDestinoService contratacaoDestinoService) {
        this.destinoService = destinoService;
        this.contratacaoService = contratacaoService;
        this.contratacaoDestinoService = contratacaoDestinoService;
    }

    @PostMapping("/selecionar")
    public String selecionarDestinos(@RequestParam List<Long> destinosSelecionados, Model model) {
        List<Destino> destinos = destinoService.buscarPorIds(destinosSelecionados);
        model.addAttribute("destinosSelecionados", destinos);
        return "contratacao-form";
    }

    @PostMapping("/confirmar")
    public String confirmarContratacao(
            @RequestParam List<Long> destinos,
            @RequestParam String nomeCliente,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodoInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodoFim) {

        Contratacao contratacao = new Contratacao();
        contratacao.setNomeCliente(nomeCliente);
        contratacao.setPeriodoInicio(periodoInicio);
        contratacao.setPeriodoFim(periodoFim);
        contratacao.setData(LocalDate.now());

        contratacao = contratacaoService.salvar(contratacao);

        for (Long id : destinos) {
            Destino destino = destinoService.buscarPorId(id);
            contratacaoDestinoService.salvar(contratacao, destino);
        }

        return "redirect:/contratacao/historico";
    }

    @PostMapping("/cancelar")
    public String cancelar() {
        return "redirect:/";
    }

    @GetMapping("/historico")
    public String historico(Model model) {
        List<Contratacao> contratacoes = contratacaoService.listarTodas();
        model.addAttribute("contratacoes", contratacoes);
        return "contratacao-lista";
    }
} 
