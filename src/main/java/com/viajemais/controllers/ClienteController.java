package com.viajemais.controllers;

import com.viajemais.entities.Cliente;
import com.viajemais.services.ClienteService;

import java.time.LocalDate;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    // Lista todos os clientes
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("clientes", clienteService.listarTodos());
        return "clientes";
    }

    // Abre o formulário de novo cliente
    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("cliente", new Cliente());  // garante que cliente não será null
        return "cliente-form";
    }

    // Edita cliente existente
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Cliente cliente = clienteService.buscarPorId(id)
                             .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado: " + id));
        model.addAttribute("cliente", cliente);
        return "cliente-form";
    }

    // Salva (criação ou atualização)
    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Cliente cliente) {
        if (cliente.getId() == null) {
            cliente.setDataCadastro(LocalDate.now());
        }
        clienteService.salvar(cliente);
        return "redirect:/clientes";
    }


    // Exclui cliente
    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id) {
        clienteService.excluir(id);
        return "redirect:/clientes";
    }
}
