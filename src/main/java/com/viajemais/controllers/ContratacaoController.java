package com.viajemais.controllers;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.viajemais.entities.Contratacao;
import com.viajemais.entities.Destino;
import com.viajemais.entities.ItemContratacao;
import com.viajemais.services.ClienteService;
import com.viajemais.services.ContratacaoService;
import com.viajemais.services.DestinoService;
import com.viajemais.services.ItemContratacaoService;

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

    /** Recebe os IDs de destinos selecionados e exibe o formulário */
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

    /** Processa o POST de confirmação, com validações de cliente, quantidade e datas */
    
    @PostMapping("/confirmar")
    public String confirmarContratacao(
            @RequestParam List<Long> destinos,
            @RequestParam String nomeCliente,
            @RequestParam int quantidadePessoas,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodoInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodoFim,
            Model model) {

        boolean hasErrors = false;
        LocalDate hoje = LocalDate.now();

        // 1) Cliente existe?
        if (!clienteService.existsByNomeCliente(nomeCliente)) {
            model.addAttribute("erroCliente", "Cliente '" + nomeCliente + "' não cadastrado");
            hasErrors = true;
        }

        // 2) Quantidade válida?
        if (quantidadePessoas < 1 || quantidadePessoas > 1000) {
            model.addAttribute("erroQuantidade", "Quantidade deve ser entre 1 e 1000");
            hasErrors = true;
        }

        // 3) Data de início
        if (periodoInicio == null) {
            model.addAttribute("erroDataInicio", "Data de início deve ser informada");
            hasErrors = true;
        } else if (periodoInicio.isBefore(hoje)) {
            model.addAttribute("erroDataInicio", "Data de início não pode ser anterior a hoje");
            hasErrors = true;
        }

        // 4) Data de término
        if (periodoFim == null) {
            model.addAttribute("erroDataTermino", "Data de término deve ser informada");
            hasErrors = true;
        } else if (periodoInicio != null && periodoFim.isBefore(periodoInicio)) {
            model.addAttribute("erroDataTermino", "Data de término não pode ser anterior à data de início");
            hasErrors = true;
        }

        // 5) Duração ≤ 90 dias (só se não houver erros acima)
        if (!hasErrors && periodoInicio != null && periodoFim != null) {
            long dias = ChronoUnit.DAYS.between(periodoInicio, periodoFim);
            if (dias > 90) {
                model.addAttribute("erroDuracao", "A duração da viagem não pode exceder 90 dias");
                hasErrors = true;
            }
        }

        if (hasErrors) {
            // Repor tudo no model e voltar ao formulário
            List<Destino> destinosList = destinoService.buscarPorIds(destinos);
            model.addAttribute("destinosSelecionados", destinosList);
            model.addAttribute("nomeCliente", nomeCliente);
            model.addAttribute("quantidadePessoas", quantidadePessoas);
            model.addAttribute("periodoInicio", periodoInicio);
            model.addAttribute("periodoFim", periodoFim);
            return "contratacao-form";
        }

        // Sem erros, cria a Contratacao
        Contratacao c = new Contratacao();
        c.setNomeCliente(nomeCliente);
        c.setPeriodoInicio(periodoInicio);
        c.setPeriodoFim(periodoFim);
        c.setQuantidadePessoas(quantidadePessoas);
        c.setData(LocalDate.now());
        contratacaoService.salvar(c);

        // Itens da contratação...
        for (Long id : destinos) {
            Destino dest = destinoService.buscarPorId(id);
            if (dest != null) {
                ItemContratacao item = new ItemContratacao();
                item.setContratacao(c);
                item.setDestino(dest);
                item.setPrecoUnitario(dest.getPreco());
                itemContratacaoService.salvar(item);
            }
        }
        return "redirect:/contratacao/historico";
    }
    
    /** Cancela a operação e volta para a home */
    @GetMapping("/cancelar")
    public String cancelar() {
        return "redirect:/";
    }

    /** Exibe o histórico de contratações */
    @GetMapping("/historico")
    public String historico(Model model) {
        List<Contratacao> contratacoes = contratacaoService.listarTodas();

        for (Contratacao c : contratacoes) {
            // diferença de dias entre início e fim
            long diff = ChronoUnit.DAYS.between(c.getPeriodoInicio(), c.getPeriodoFim());
            // se diff == 0 → 1 diária; caso contrário, diff diárias
            long diarias = (diff == 0) ? 1 : diff;
            c.setQuantidadeDiarias(diarias);

            // calcular valor de cada destino e total
            List<ItemContratacao> itens = itemContratacaoService.buscarItensPorContratacao(c.getId());
            double total = 0;
            for (ItemContratacao item : itens) {
                double valor = item.getPrecoUnitario() * diarias;
                item.setValorDestino(valor);
                total += valor;
            }
            c.setItens(itens);
            c.setTotalViagem(total);
        }

        model.addAttribute("contratacoes", contratacoes);
        return "contratacao-lista";
    }
}
