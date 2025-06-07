package com.viajemais.controllers;

import java.util.List;
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
    public String listarClientes(Model model) {
        List<Cliente> lista = clienteService.listarTodos();
        model.addAttribute("listaDeClientes", lista);
        return "clientes"; // renderiza: src/main/resources/templates/clientes.html
    }

    // 2) FORMULÁRIO “NOVO CLIENTE”
    @GetMapping("/novo")
    public String novoCliente(Model model) {
        model.addAttribute("cliente", new Cliente());
        model.addAttribute("editar", false);
        return "cliente-form"; // renderiza: src/main/resources/templates/cliente-form.html
    }

    // 3) FORMULÁRIO “EDITAR CLIENTE” (edição completa)
    @GetMapping("/editar/{id}")
    public String editarCliente(@PathVariable Long id, Model model) {
        Cliente cliente = clienteService.buscarPorId(id)
            .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado: " + id));
        model.addAttribute("cliente", cliente);
        model.addAttribute("editar", true);
        return "cliente-form";
    }

    // 4) SALVAR (criação ou edição completa via cliente-form.html)
    @PostMapping("/salvar")
    public String salvarCliente(
            @Valid @ModelAttribute("cliente") Cliente cliente,
            BindingResult result,
            @RequestParam(value = "editar", defaultValue = "false") boolean editar,
            Model model) {

        // Se houver erro de validação (nome, tamanho, pattern etc.), volta ao formulário
        if (result.hasErrors()) {
            model.addAttribute("editar", editar);
            return "cliente-form";
        }

        try {
            // O próprio ClienteService cuida de: 
            // • auto-increment de codCliente (se id == null)  
            // • colocar dataCadastro (dentro de salvar(...) quando id == null)  
            // • verificação de unicidade de nome (lança DataIntegrityViolationException)
            clienteService.salvar(cliente);
        } catch (DataIntegrityViolationException e) {
            model.addAttribute("erroNome", e.getMessage());
            model.addAttribute("editar", editar);
            return "cliente-form";
        }

        return "redirect:/clientes";
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
