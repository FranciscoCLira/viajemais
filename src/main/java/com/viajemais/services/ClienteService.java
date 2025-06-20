package com.viajemais.services;

import com.viajemais.entities.Cliente;
import com.viajemais.repositories.ClienteRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }

    public Cliente salvar(Cliente cliente) {
        if (cliente.getId() == null) {
            // Novo cliente: verifica unicidade de nome
            if (clienteRepository.existsByNomeCliente(cliente.getNomeCliente())) {
                throw new DataIntegrityViolationException("Já existe um cliente com este nome");
            }
            // Auto incremento codCliente
            Long maiorCodigo = clienteRepository.findMaxCodCliente();
            cliente.setCodCliente((maiorCodigo != null ? maiorCodigo : 0) + 1);
            cliente.setDataCadastro(LocalDate.now());
        }
        // Em edição, nome não deve mudar (controlado no Controller)
        return clienteRepository.save(cliente);
    }

    public void excluir(Long id) {
        clienteRepository.deleteById(id);
    }
    
    /** Busca clientes cujo nome começa com prefix (case-insensitive) */
    public List<Cliente> buscarPorPrefixo(String prefix) {
        return clienteRepository.findByNomeClienteStartingWithIgnoreCase(prefix);
    }

    public boolean existePorCodigo(Integer codCliente) {
        return clienteRepository.existsByCodCliente(codCliente);
    }
    
    public boolean existsByNomeCliente(String nomeCliente) {
        return clienteRepository.existsByNomeCliente(nomeCliente);
    }

    public Optional<Cliente> buscarPorNome(String nome) {
        return clienteRepository.findByNomeCliente(nome);
    }
}
