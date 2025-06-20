package com.viajemais.controllers;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
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
    
     // 1) LISTAR TODOS OS CLIENTES
    @GetMapping
    public String listar(Model model) {
        List<Cliente> clientes = clienteService.listarTodos();

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

        model.addAttribute("listaDeClientes",  clientes);
        model.addAttribute("podeEditarMap", podeEditar);
        model.addAttribute("podeExcluirMap",podeExcluir);
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
}
