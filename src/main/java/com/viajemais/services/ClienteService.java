package com.viajemais.services;

import com.viajemais.entities.Cliente;
import com.viajemais.repositories.ClienteRepository;
import com.viajemais.repositories.ContratacaoRepository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ContratacaoRepository contratacaoRepository;

    public ClienteService(ClienteRepository clienteRepo,
            ContratacaoRepository contratacaoRepo) {
			this.clienteRepository      = clienteRepo;
			this.contratacaoRepository  = contratacaoRepo;
	}

    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }

    public Cliente salvar(Cliente cliente) {
        if (cliente.getId() == null) {
            // Criação: Novo cliente: verifica unicidade de nome
            if (clienteRepository.existsByNomeCliente(cliente.getNomeCliente())) {
                throw new DataIntegrityViolationException("Já existe um cliente com este nome");
            }
            // Auto incremento codCliente
            Long maiorCodigo = clienteRepository.findMaxCodCliente();
            cliente.setCodCliente((maiorCodigo != null ? maiorCodigo : 0) + 1);
            cliente.setDataCadastro(LocalDate.now());
	    } else {
	        // Edição: atualização: se o nome mudou, verifica duplicado excluindo este id
	        if (clienteRepository.existsByNomeClienteAndIdNot(
	                cliente.getNomeCliente(), cliente.getId())) {
	            throw new DataIntegrityViolationException("Já existe um cliente com este nome");
	        }
	        // NÃO altera dataCadastro nem codCliente
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
    
    /** Retorna true se houver outro cliente com o mesmo nome e id diferente */
    public boolean existsByNomeClienteAndIdNot(String nomeCliente, Long id) {
        return clienteRepository.existsByNomeClienteAndIdNot(nomeCliente, id);
    }
    
    /** Retorna true se já existir um cliente com este nome */
    public boolean existsByNomeCliente(String nomeCliente) {
        return clienteRepository.existsByNomeCliente(nomeCliente);
    }

    // método que busca por nome usando o clienteRepository
    public Optional<Cliente> buscarPorNome(String nome) {
        return clienteRepository.findByNomeCliente(nome);
    }
    
    
    /** Retorna true se existirem contratações para esse nome de cliente */
    // existências de viagens para o cliente
    public boolean clientePossuiContratacoes(String nomeCliente) {
      return contratacaoRepository.existsByNomeCliente(nomeCliente);
    }
}
