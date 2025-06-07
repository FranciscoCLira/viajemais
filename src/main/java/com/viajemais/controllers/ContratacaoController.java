package com.viajemais.controllers;

import com.viajemais.entities.Contratacao;
import com.viajemais.entities.Destino;
import com.viajemais.entities.ItemContratacao;
import com.viajemais.services.ClienteService;
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
    private final ClienteService clienteService;

    public ContratacaoController(DestinoService destinoService,
                                 ContratacaoService contratacaoService,
                                 ItemContratacaoService itemContratacaoService,
                                 ClienteService clienteService) {
        this.destinoService = destinoService;
        this.contratacaoService = contratacaoService;
        this.itemContratacaoService = itemContratacaoService;
        this.clienteService = clienteService;
    }

    @PostMapping("/selecionar")
    public String selecionarDestinos(@RequestParam(required = false) List<Long> destinosSelecionados,
                                     Model model) {
        if (destinosSelecionados == null || destinosSelecionados.isEmpty()) {
            return "redirect:/"; 
        }
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
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodoFim,
            Model model) {

        boolean hasErrors = false;

        // 1) Validar cliente existe
        if (!clienteService.existsByNomeCliente(nomeCliente)) {
            model.addAttribute("erroCliente", "Cliente '" + nomeCliente + "' não cadastrado");
            hasErrors = true;
        }

        // 2) Quantidade de pessoas <= 1000
        if (quantidadePessoas < 1 || quantidadePessoas > 1000) {
            model.addAttribute("erroQuantidade", "Quantidade deve ser entre 1 e 1000");
            hasErrors = true;
        }

        LocalDate hoje = LocalDate.now();
        // 3) Data de início >= hoje
        if (periodoInicio.isBefore(hoje)) {
            model.addAttribute("erroDataInicio", "Data de início não pode ser anterior a hoje");
            hasErrors = true;
        }
        // 4) Data de término >= data de início
        if (periodoFim.isBefore(periodoInicio)) {
            model.addAttribute("erroDataTermino", "Data de término não pode ser anterior à data de início");
            hasErrors = true;
        }

        if (hasErrors) {
            // repor destinos selecionados para reexibir no form
            List<Destino> destinosList = destinoService.buscarPorIds(destinos);
            model.addAttribute("destinosSelecionados", destinosList);
            // repor valores para manter preenchido
            model.addAttribute("nomeCliente", nomeCliente);
            model.addAttribute("quantidadePessoas", quantidadePessoas);
            model.addAttribute("periodoInicio", periodoInicio);
            model.addAttribute("periodoFim", periodoFim);
            return "contratacao-form";
        }

        // --- sem erros, prossegue com criação da contratação ---
        Contratacao contratacao = new Contratacao();
        contratacao.setNomeCliente(nomeCliente);
        contratacao.setPeriodoInicio(periodoInicio);
        contratacao.setPeriodoFim(periodoFim);
        contratacao.setQuantidadePessoas(quantidadePessoas);
        contratacao.setData(LocalDate.now());
        contratacao = contratacaoService.salvar(contratacao);

        for (Long id : destinos) {
            Destino dest = destinoService.buscarPorId(id);
            if (dest != null) {
                ItemContratacao item = new ItemContratacao();
                item.setContratacao(contratacao);
                item.setDestino(dest);
                item.setPrecoUnitario(dest.getPreco());
                itemContratacaoService.salvar(item);
            }
        }
        return "redirect:/contratacao/historico";
    }

    // ... demais métodos (cancelar, histórico) permanecem iguais ...
}
