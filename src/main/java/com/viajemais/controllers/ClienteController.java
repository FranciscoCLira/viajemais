package com.viajemais.controllers;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import com.viajemais.entities.Cliente;
import com.viajemais.services.ClienteService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;
    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }
    
     // 1) LISTAR OS CLIENTES POR FILTRO INFORMADO 
    @GetMapping
    public String listar(
	    @RequestParam(required = false) String nomeFiltro,
	    @RequestParam(required = false) Integer codInicio,
	    @RequestParam(required = false) Integer codFim,
	    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
	    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
	    @RequestParam(required = false) String situacaoFiltro,
	    @RequestParam(required = false, defaultValue = "false") boolean limpar,
    	Model model) {
    	
    	// List<Cliente> clientes;
        // String mensagem;

    	
        // Se for “Limpar”, renderiza tela vazia
        if (limpar) {
          model.addAttribute("clientes", List.of());
          // zera todos os campos de filtro
          model.addAttribute("nomeFiltro",     null);
          model.addAttribute("codInicio",      null);
          model.addAttribute("codFim",         null);
          model.addAttribute("dataInicio",     null);
          model.addAttribute("dataFim",        null);
          model.addAttribute("situacaoFiltro", null);
          return "clientes";
        }    	

        boolean hasNome  = nomeFiltro  != null && !nomeFiltro.isBlank();
        boolean hasCod   = codInicio   != null || codFim   != null;
        boolean hasDate  = dataInicio  != null || dataFim  != null;
        boolean hasSit   = situacaoFiltro != null && !situacaoFiltro.isBlank();
        
        List<Cliente> clientes; 
        
        
        // 1) Se usar Nome ou Código de cliente, deve ser o ÚNICO filtro
        if ((hasNome && (hasCod || hasDate || hasSit))
         || (hasCod  && (hasNome|| hasDate || hasSit))) {
          model.addAttribute("erroFiltro", "Informe apenas um filtro por vez (exceto Data+Situação).");
          clientes = List.of();
        }
        // 2) Se usar Data+Situação JUNTOS
        else if (hasDate && hasSit) {
          // valida intervalo de datas
          LocalDate di = dataInicio != null ? dataInicio : dataFim;
          LocalDate df = dataFim    != null ? dataFim    : dataInicio;
          if (df.isBefore(di)) {
            model.addAttribute("erroFiltro", "Intervalo de datas inválido");
            clientes = List.of();
          } else {
            clientes = clienteService.filtrarPorDataESituacao(di, df, situacaoFiltro);
            model.addAttribute("mensagemSucesso",
              clientes.isEmpty()
                ? "Não há clientes para a consulta informada"
                : "Consulta efetuada com sucesso"
            );
          }
        }
        // 3) Se usar só Data
        else if (hasDate) {
          LocalDate di = dataInicio != null ? dataInicio : dataFim;
          LocalDate df = dataFim    != null ? dataFim    : dataInicio;
          if (df.isBefore(di)) {
            model.addAttribute("erroFiltro", "Intervalo de datas inválido");
            clientes = List.of();
          } else {
            clientes = clienteService.filtrarPorData(di, df);
            model.addAttribute("mensagemSucesso",
              clientes.isEmpty()
                ? "Não há clientes para a consulta informada"
                : "Consulta efetuada com sucesso"
            );
          }
        }
        // 4) Se usar só Situação
        else if (hasSit) {
          clientes = clienteService.filtrarPorSituacao(situacaoFiltro);
          model.addAttribute("mensagemSucesso",
            clientes.isEmpty()
              ? "Não há clientes para a consulta informada"
              : "Consulta efetuada com sucesso"
          );
        }
        // 5) Se usar só Nome
        else if (hasNome) {
          clientes = clienteService.filtrarPorNome(nomeFiltro);
          model.addAttribute("mensagemSucesso",
            clientes.isEmpty()
              ? "Não há clientes para a consulta informada"
              : "Consulta efetuada com sucesso"
          );
        }
        // 6) Se usar só Código
        else if (hasCod) {
          Integer i = codInicio != null ? codInicio : codFim;
          Integer f = codFim    != null ? codFim    : codInicio;
          if (f < i) {
            model.addAttribute("erroFiltro", "Intervalo de código inválido");
            clientes = List.of();
          } else {
            clientes = clienteService.filtrarPorCodigo(i, f);
            model.addAttribute("mensagemSucesso",
              clientes.isEmpty()
                ? "Não há clientes para a consulta informada"
                : "Consulta efetuada com sucesso"
            );
          }
        }
        // 7) Sem filtro
        else {
          clientes = clienteService.listarTodosOrdenados();
          model.addAttribute("mensagemSucesso", "Consulta efetuada com sucesso de todos os clientes.");
        }
        

        Map<Long,Boolean> podeEditar  = new HashMap<>();
        Map<Long,Boolean> podeExcluir = new HashMap<>();

        for (Cliente c : clientes) {
            boolean temViagens = clienteService.clientePossuiContratacoes(c.getNomeCliente());
            // Editar só se NÃO tiver viagens
            podeEditar.put(c.getId(), !temViagens);
            // Excluir só se situação = 'C' e sem viagens
            podeExcluir.put(c.getId(),
                "C".equals(c.getSituacaoCliente()) && !temViagens);
        }

        
        // repõe dados no formulário
        model.addAttribute("listaDeClientes",  clientes);
        model.addAttribute("podeEditarMap", podeEditar);
        model.addAttribute("podeExcluirMap",podeExcluir);
        
        // também manter os valores de filtro para repor no form:
        model.addAttribute("nomeFiltro",      nomeFiltro);
        model.addAttribute("codInicio",       codInicio);
        model.addAttribute("codFim",          codFim);
        model.addAttribute("dataInicio",      dataInicio);
        model.addAttribute("dataFim",         dataFim);
        model.addAttribute("situacaoFiltro",  situacaoFiltro);        
        
        return "clientes";
    }
    
    
    // 2) FORMULÁRIO “NOVO CLIENTE”
    @GetMapping("/novo")
    public String novoCliente(Model model) {
        model.addAttribute("cliente", new Cliente());
        model.addAttribute("editar", false);
        return "cliente-form"; // renderiza: src/main/resources/templates/cliente-form.html
    }

    
    // 3) FORMULÁRIO “EDITAR CLIENTE” (edição completa)
    
    // 1) Form de edição (reaproveitando o mesmo template cliente-form.html)
    @GetMapping("/editar/{id}")
    public String editarCliente(@PathVariable Long id, Model model) {
        Cliente c = clienteService.buscarPorId(id)
            .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado: " + id));
        model.addAttribute("cliente", c);
        model.addAttribute("editar", true);
        return "cliente-form";
    }

    // 2) Salvar edição
    @PostMapping("/editar")
    public String salvarEdicao(
        @Valid @ModelAttribute("cliente") Cliente cliente,
        BindingResult binding,
        RedirectAttributes ra,
        Model model) {

      // 2.1) Bean-Validation de campos (ex: @NotBlank)
      if (binding.hasErrors()) {
        model.addAttribute("editar", true);
        return "cliente-form";
      }

      // 2.2) Regra de cancelamento: só se não houver contratações
      if ("C".equals(cliente.getSituacaoCliente())
          && clienteService.clientePossuiContratacoes(cliente.getNomeCliente())) {
        model.addAttribute("erroCancelamento",
            "Erro: Cliente não cancelado pois possui contratações de viagens.");
        model.addAttribute("editar", true);
        return "cliente-form";
      }

      try {
        clienteService.salvar(cliente);
        // 2.3) flash-message de sucesso
        ra.addFlashAttribute("sucessoEdicao",
            String.format("Cliente alterado com sucesso: #%d – %s",
                          cliente.getCodCliente(),
                          cliente.getNomeCliente()));
        return "redirect:/clientes";
      } catch (DataIntegrityViolationException ex) {
        // nome duplicado?
        model.addAttribute("erroDuplicado",
            String.format("Erro. Cliente já cadastrado: #%d – %s",
                          cliente.getCodCliente(),
                          cliente.getNomeCliente()));
        model.addAttribute("editar", true);
        return "cliente-form";
      } catch (Exception ex) {
        model.addAttribute("erroNaoPrevisto", "Erro não previsto pelo sistema.");
        model.addAttribute("editar", true);
        return "cliente-form";
      }
    }
  
    
    
    // 4) SALVAR (criação ou edição completa via cliente-form.html)
    @PostMapping("/salvar")
    public String salvar(
        @Valid @ModelAttribute("cliente") Cliente clienteForm,
        BindingResult binding,
        RedirectAttributes ra,
        Model model) {

    	System.out.println("***********************************************");  
	    System.out.printf("*** ClienteController: clienteForm.getId()=%d, " + 
	              "clienteForm.getNomeCliente()=%s, " + 
	              "clienteForm.getSituacaoCliente()=%s%n",
	               clienteForm.getId(),
	               clienteForm.getNomeCliente(),
	               clienteForm.getSituacaoCliente());   
    	System.out.println("***********************************************");  
	    
	    // 1) Detecta edição por ID
        boolean isEdicao = clienteForm.getId() != null;
 
        // Sempre reponha essa flag pra view saber se é edição
        model.addAttribute("editar", isEdicao);
         
        // 2) Bean validations (NotBlank, etc)
        if (binding.hasErrors()) {
          return "cliente-form";
        }
      
      
      // 3) Detecta “nenhuma alteração” em edição
      if (isEdicao) {
        Cliente original = clienteService.buscarPorId(clienteForm.getId())
                           .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
        boolean mudouNome  = !original.getNomeCliente().equals(clienteForm.getNomeCliente());
        boolean mudouSituacao = !original.getSituacaoCliente().equals(clienteForm.getSituacaoCliente());

        if (!mudouNome && !mudouSituacao) {
          model.addAttribute("erroSemAlteracao", "Entre com as alterações para Salvar");
          return "cliente-form";
        }
      }

      // 4) Validação de duplicidade ANTES do save
      if (isEdicao) {
        // edição: não pode existir outro com mesmo nome
        if (clienteService.existsByNomeClienteAndIdNot(
                clienteForm.getNomeCliente(), clienteForm.getId())) {
          model.addAttribute("erroDuplicado", "Já existe um cliente com este nome");
          return "cliente-form";
        }
      } else {
        // criação: não pode existir nenhum com esse nome
        if (clienteService.existsByNomeCliente(clienteForm.getNomeCliente())) {
          model.addAttribute("erroDuplicado", "Já existe um cliente com este nome");
          return "cliente-form";
        }
      }

      if (isEdicao) {
    	  Cliente original = clienteService.buscarPorId(clienteForm.getId()).orElseThrow();
    	  // garante que o form carregue o código já existente
    	  clienteForm.setCodCliente(original.getCodCliente());
    	  // (se quiser manter dataCadastro, 
    	  // também pode “clienteForm.setDataCadastro(original.getDataCadastro());”)
    	  clienteForm.setDataCadastro(LocalDate.now());
    	}      
      
      // 5) se passou, salva 
      Cliente salvo = clienteService.salvar(clienteForm);

      // 6) flash + redirect
      if (isEdicao) {
        ra.addFlashAttribute("sucessoEdicao",
          String.format("Cliente alterado com sucesso: #%d – %s",
                        salvo.getCodCliente(), salvo.getNomeCliente()));
        return "redirect:/clientes/editar/" + salvo.getId();
      } else {
        ra.addFlashAttribute("sucessoCadastro",
          String.format("Cliente cadastrado com sucesso: #%d – %s",
                        salvo.getCodCliente(), salvo.getNomeCliente()));
        return "redirect:/clientes/novo";
      }
    }

    
    // 5) EXCLUIR CLIENTE (só se não estiver ativo — condicional na view)
    @GetMapping("/excluir/{id}")
    public String excluirCliente(@PathVariable Long id) {
        clienteService.excluir(id);
        return "redirect:/clientes";
    }

    // ――― NOVO MÉTODO: ATUALIZAR SOMENTE A SITUAÇÃO (edição inline na lista) ―――
    @PostMapping("/atualizar-situacao")
    public String atualizarSituacao(
            @RequestParam("id") Long id,
            @RequestParam("situacaoCliente") String situacao) {

        Cliente existente = clienteService.buscarPorId(id)
            .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado: " + id));
        existente.setSituacaoCliente(situacao);
        // Neste caso, clienteService.salvar(existente) só atualiza situação, 
        // pois id != null e o service não altera codCliente nem dataCadastro.
        clienteService.salvar(existente);

        return "redirect:/clientes";
    }
    
    // Somente o método de autocomplete permanece gerando JSON:
    @GetMapping("/sugerir")
    @ResponseBody
    public List<String> sugerirNomes(@RequestParam("prefix") String prefix) {
        if (prefix == null || prefix.isBlank()) {
            return List.of();
        }
        return clienteService.buscarPorPrefixo(prefix)
                             .stream()
                             .map(Cliente::getNomeCliente)
                             .collect(Collectors.toList());
    }
    
    
    @PostMapping("/alterarSituacao")
    public String alterarSituacao(
            @RequestParam Long id,
            @RequestParam String situacaoCliente,
            @RequestParam(required=false) String nomeFiltro,
            @RequestParam(required=false) Integer codInicio,
            @RequestParam(required=false) Integer codFim,
            @RequestParam(required=false)
              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required=false)
              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(required=false) String situacaoFiltro,
            RedirectAttributes ra) {

        // 1) Atualiza a situação do cliente
        clienteService.alterarSituacao(id, situacaoCliente);
        ra.addFlashAttribute("mensagemSucesso",
          "Cliente #" + id + " agora com situação " + situacaoCliente);

        // 2) Reconstrói a URL de redirecionamento incluindo os filtros que não eram nulos
        UriComponentsBuilder uri = UriComponentsBuilder.fromPath("/clientes");
        if (nomeFiltro     != null) uri.queryParam("nomeFiltro", nomeFiltro);
        if (codInicio      != null) uri.queryParam("codInicio", codInicio);
        if (codFim         != null) uri.queryParam("codFim", codFim);
        if (dataInicio     != null) uri.queryParam("dataInicio", dataInicio);
        if (dataFim        != null) uri.queryParam("dataFim", dataFim);
        if (situacaoFiltro != null) uri.queryParam("situacaoFiltro", situacaoFiltro);

        return "redirect:" + uri.build().toUriString();
    }
    
    
}
