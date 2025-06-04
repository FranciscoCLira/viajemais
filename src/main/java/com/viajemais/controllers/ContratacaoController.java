package com.viajemais.controllers;

import com.viajemais.entities.Contratacao;
import com.viajemais.entities.Destino;
import com.viajemais.entities.ItemContratacao;
import com.viajemais.services.ContratacaoService;
import com.viajemais.services.DestinoService;
import com.viajemais.services.ItemContratacaoService;

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
    private final ItemContratacaoService itemContratacaoService;

    public ContratacaoController(DestinoService destinoService,
                                 ContratacaoService contratacaoService,
                                 ItemContratacaoService itemContratacaoService) {
        this.destinoService = destinoService;
        this.contratacaoService = contratacaoService;
        this.itemContratacaoService = itemContratacaoService;
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
            @RequestParam int quantidadePessoas,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodoInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodoFim) {

        Contratacao contratacao = new Contratacao();
        contratacao.setNomeCliente(nomeCliente);
        contratacao.setPeriodoInicio(periodoInicio);
        contratacao.setPeriodoFim(periodoFim);
        contratacao.setQuantidadePessoas(quantidadePessoas);
        contratacao.setData(LocalDate.now());

        contratacao = contratacaoService.salvar(contratacao);

        for (Long id : destinos) {
            Destino destino = destinoService.buscarPorId(id);
            if (destino != null) {
                ItemContratacao item = new ItemContratacao();
                item.setContratacao(contratacao);
                item.setDestino(destino);
                item.setPrecoUnitario(destino.getPreco());
                itemContratacaoService.salvar(item);
            }
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

        for (Contratacao c : contratacoes) {
            List<ItemContratacao> itens = itemContratacaoService.buscarItensPorContratacao(c.getId());
            c.setItens(itens); // ✅ requer getItens/setItens em Contratacao
        }

        model.addAttribute("contratacoes", contratacoes);
        return "contratacao-lista";
    }
}
