package com.viajemais.controllers;

import java.beans.PropertyEditorSupport;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.viajemais.entities.Contratacao;
import com.viajemais.entities.Destino;
import com.viajemais.entities.ItemContratacao;
import com.viajemais.services.ClienteService;
import com.viajemais.services.ContratacaoService;

import com.viajemais.services.DestinoService;
import com.viajemais.services.ItemContratacaoService;

import jakarta.validation.Valid;

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
    public String selecionarDestinos(
    		@RequestParam(required = false) List<Long> destinosSelecionados,
            Model model,
            RedirectAttributes ra) {
    	
        if (destinosSelecionados == null || destinosSelecionados.isEmpty()) {
            // prepara flash-attribute para exibir o erro na Home
            ra.addFlashAttribute("erroSelecao", "Selecione ao menos um destino");
            return "redirect:/";
        }
        
        // caso haja seleção, segue normalmente
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
            Model model,
        	RedirectAttributes ra) {

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

        // Sem erros, cria a Contratacao - persiste a Contratacao e os Itens…
        Contratacao c = new Contratacao();
        c.setNomeCliente(nomeCliente);
        c.setPeriodoInicio(periodoInicio);
        c.setPeriodoFim(periodoFim);
        c.setQuantidadePessoas(quantidadePessoas);
        c.setData(LocalDate.now());
        contratacaoService.salvar(c);

        // Itens da contratação...  salva os itens 
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
        
        // **adiciona a mensagem de sucesso**  
        ra.addFlashAttribute("sucessoCriacao",
          String.format("Viagem contratada com sucesso: #%d – %s",
                        c.getId(),
                        c.getNomeCliente()));
        
        // Depois de salvar tudo, adiciona o nome do cliente como query-param:
        ra.addAttribute("filtroCliente", nomeCliente);
        return "redirect:/contratacao/historico";
    }
    
    /** Cancela a operação e volta para a home */
    @GetMapping("/cancelar")
    public String cancelar() {
        return "redirect:/";
    }

    /** Exibe o histórico de contratações */
    
    @GetMapping("/historico")
    public String historico(
            @RequestParam(required = false) String filtroCliente,
            @RequestParam(required = false)
               @DateTimeFormat(pattern="dd/MM/yyyy") LocalDate dataConfInicio,
            @RequestParam(required = false)
               @DateTimeFormat(pattern="dd/MM/yyyy") LocalDate dataConfFim,
            @RequestParam(required = false)
               @DateTimeFormat(pattern="dd/MM/yyyy") LocalDate perInicio,
            @RequestParam(required = false)
               @DateTimeFormat(pattern="dd/MM/yyyy") LocalDate perFim,
            Model model) {

        // 1) mantém os filtros no model
        model.addAttribute("filtroCliente",   filtroCliente);
        model.addAttribute("dataConfInicio",  dataConfInicio);
        model.addAttribute("dataConfFim",     dataConfFim);
        model.addAttribute("perInicio",       perInicio);
        model.addAttribute("perFim",          perFim);

        // 2) validação das datas (igual ao que você já fez)
        boolean hasErrors = false;
        if (dataConfInicio != null && dataConfFim != null
            && dataConfFim.isBefore(dataConfInicio)) {
            model.addAttribute("erroDataConf",
                "Data de término da confirmação não pode ser anterior à data de início");
            hasErrors = true;
        }
        if (perInicio != null && perFim != null
            && perFim.isBefore(perInicio)) {
            model.addAttribute("erroPeriodo",
                "Data de término do período não pode ser anterior à data de início");
            hasErrors = true;
        }

        // 3) se erro ou nenhum filtro → lista vazia + map vazio
        if (hasErrors ||
            ((filtroCliente == null || filtroCliente.isBlank()) &&
             dataConfInicio  == null &&
             dataConfFim     == null &&
             perInicio       == null &&
             perFim          == null)) {

            model.addAttribute("contratacoes", List.<Contratacao>of());
            model.addAttribute("podeExcluirMap", Map.<Long,Boolean>of());
            return "contratacao-lista";
        }

        // 4) busca filtrada
        List<Contratacao> lista = contratacaoService.buscarComFiltro(
            filtroCliente, dataConfInicio, dataConfFim, perInicio, perFim);

        // 5) **aqui reaplica todo o cálculo de diárias, itens e totais**
        for (Contratacao c : lista) {
            // quantas diárias
            long diff = ChronoUnit.DAYS.between(c.getPeriodoInicio(), c.getPeriodoFim());
            long diarias = diff == 0 ? 1 : diff;
            c.setQuantidadeDiarias(diarias);

            // busca itens e calcula valorDestino e totalViagem
            List<ItemContratacao> itens = itemContratacaoService
                                             .buscarItensPorContratacao(c.getId());
            double total = 0;
            for (ItemContratacao item : itens) {
                double valor = item.getPrecoUnitario() * diarias * c.getQuantidadePessoas();
                item.setValorDestino(valor);
                total += valor;
            }
            c.setItens(itens);
            c.setTotalViagem(total);
        }

        // 6) monta mapa de exclusão
        Map<Long,Boolean> podeExcluirMap = new HashMap<>();
        for (Contratacao c : lista) {
            podeExcluirMap.put(c.getId(), contratacaoService.canExcluir(c.getId()));
        }

        // 7) joga tudo no model
        model.addAttribute("contratacoes", lista);
        model.addAttribute("podeExcluirMap", podeExcluirMap);

        return "contratacao-lista";
    }

    
    @GetMapping("/novo")
    public String novoContratacao(Model model) {
        model.addAttribute("contratacao", new Contratacao());
        model.addAttribute("editar", false);
        return "contratacao-form";
    }    
    
    /** Exibe o formulário de edição de período e quantidade */
    
    @GetMapping("/editar/{id}")
    public String editarContratacao(
    		@PathVariable Long id,
    		@RequestParam(required = false) String filtroCliente,
    		Model model) {
    	
        Contratacao c = contratacaoService.buscarPorId(id)
            .orElseThrow(() -> new IllegalArgumentException("Não encontrado: " + id));
        model.addAttribute("contratacao", c);
        
        // flags de permissão (sua lógica atual) …
        model.addAttribute("editar", true);
        
        LocalDate hoje = LocalDate.now();
        boolean permit = !c.getPeriodoInicio().isBefore(hoje) 
                      && !c.getPeriodoFim().isBefore(hoje);
        model.addAttribute("permitirAlteracao", permit);
        model.addAttribute("permitirExclusao", permit);

        // **repassa o filtroCliente de volta**
        model.addAttribute("filtroCliente", filtroCliente);        

        return "contratacao-edit";
    }

    /** Salva alterações de período e quantidade */
    @PostMapping("/editar/salvar")
    public String salvarEdicao(
            @Valid @ModelAttribute("contratacao") Contratacao contratacao,
            BindingResult binding,
            Model model,
            RedirectAttributes ra)  {

        LocalDate hoje = LocalDate.now();

        if (contratacao.getPeriodoInicio() == null ||
            contratacao.getPeriodoInicio().isBefore(hoje)) {
            binding.rejectValue("periodoInicio", "error.periodoInicio",
                "Data de início deve ser igual ou maior que hoje");
        }
        if (contratacao.getPeriodoFim() == null ||
            contratacao.getPeriodoFim().isBefore(contratacao.getPeriodoInicio())) {
            binding.rejectValue("periodoFim", "error.periodoFim",
                "Data de término deve ser igual ou maior que a data de início");
        }
        // ... valida Qt. Pessoas ...

        if (binding.hasErrors()) {
            model.addAttribute("permitirAlteracao", true);
            model.addAttribute("permitirExclusao", true);
            return "contratacao-edit";
        }

        // Carrega existente e atualiza só o que pode mudar
        Contratacao existente = contratacaoService.buscarPorId(contratacao.getId())
            .orElseThrow(() -> new IllegalArgumentException("Não encontrado: " + contratacao.getId()));

        existente.setPeriodoInicio(contratacao.getPeriodoInicio());
        existente.setPeriodoFim(contratacao.getPeriodoFim());
        existente.setQuantidadePessoas(contratacao.getQuantidadePessoas());
        // nomeCliente, dataCadastro e itens permanecem inalterados

        contratacaoService.salvar(existente);
        ra.addFlashAttribute("sucessoAlteracao",
          String.format("Viagem alterada com sucesso. (#%d – %s)",
                        contratacao.getId(),
                        contratacao.getNomeCliente()));        
        
        // adiciona o filtroCliente ao redirect:
        ra.addAttribute("filtroCliente", contratacao.getNomeCliente());
        
        return "redirect:/contratacao/historico";
    }

    /** Exclui a contratação, só se permitida (datas ≥ hoje) */
    @GetMapping("/excluir/{id}")
    public String excluirContratacao(
            @PathVariable Long id,
            @RequestParam(required = false) String filtroCliente,
            RedirectAttributes ra) {

        Contratacao c = contratacaoService.buscarPorId(id).orElse(null);
        if (c != null) {
            LocalDate hoje = LocalDate.now();
            if (!c.getPeriodoInicio().isBefore(hoje) &&
                !c.getPeriodoFim().isBefore(hoje)) {
                contratacaoService.excluir(id);
                // inclui id e nomeCliente na mensagem:
                ra.addFlashAttribute("sucessoExclusao",
                  String.format("Viagem excluída com sucesso. (#%d – %s)", 
                                c.getId(), c.getNomeCliente()));
            } else {
                ra.addFlashAttribute("erroExclusao",
                    "Só é possível excluir viagens ainda não iniciadas.");
            }
        }
        // mantém o filtro do cliente
        ra.addAttribute("filtroCliente", filtroCliente);
        return "redirect:/contratacao/historico";
    }

    
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(LocalDate.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                if (text == null || text.isBlank()) {
                    setValue(null);
                    return;
                }
                // Se vier no formato ISO (yyyy-MM-dd), usa parse padrão
                if (text.contains("-")) {
                    setValue(LocalDate.parse(text));
                } else {
                    // Senão, assume dd/MM/yyyy
                    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    setValue(LocalDate.parse(text, fmt));
                }
            }
            @Override
            public String getAsText() {
                LocalDate ld = (LocalDate) getValue();
                return (ld == null ? "" : ld.toString()); // ISO, para datepicker
            }
        });
    }

}
