package com.viajemais.services;

import com.viajemais.entities.Cliente;
import com.viajemais.repositories.ClienteRepository;
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
            // Cliente novo
            Long maiorCodigo = clienteRepository.findMaxCodCliente();
            cliente.setCodCliente((maiorCodigo != null ? maiorCodigo : 0) + 1);
            cliente.setDataCadastro(LocalDate.now());
        }

        return clienteRepository.save(cliente);
    }
 
    
    public void excluir(Long id) {
        clienteRepository.deleteById(id);
    }

    public boolean existePorCodigo(Integer codCliente) {
        return clienteRepository.existsByCodCliente(codCliente);
    }
}
