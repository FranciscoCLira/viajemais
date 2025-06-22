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
    private final ClienteRepository repo;
    private final ContratacaoRepository contratacaoRepo;

    public ClienteService(ClienteRepository repo,
            ContratacaoRepository contratacaoRepo) {
			this.repo      = repo;
			this.contratacaoRepo  = contratacaoRepo;
	}

    public List<Cliente> listarTodos() {
        return repo.findAll();
    }

    public Optional<Cliente> buscarPorId(Long id) {
        return repo.findById(id);
    }

    public Cliente salvar(Cliente cliente) {
        if (cliente.getId() == null) {
            // Criação: Novo cliente: verifica unicidade de nome
            if (repo.existsByNomeCliente(cliente.getNomeCliente())) {
                throw new DataIntegrityViolationException("Já existe um cliente com este nome");
            }
            // Auto incremento codCliente
            Long maiorCodigo = repo.findMaxCodCliente();
            cliente.setCodCliente((maiorCodigo != null ? maiorCodigo : 0) + 1);
            cliente.setDataCadastro(LocalDate.now());
	    } else {
	        // Edição: atualização: se o nome mudou, verifica duplicado excluindo este id
	        if (repo.existsByNomeClienteAndIdNot(
	                cliente.getNomeCliente(), cliente.getId())) {
	            throw new DataIntegrityViolationException("Já existe um cliente com este nome");
	        }
	        // NÃO altera dataCadastro nem codCliente
	    }        
        // Em edição, nome não deve mudar (controlado no Controller)
        return repo.save(cliente);
    }

    
    /**
     * Altera apenas a situação de um cliente.
     * Se for para 'C' (Cancelado), opcionalmente valida que não haja contratações ativas.
     */
    public void alterarSituacao(Long id, String novaSituacao) {
        Cliente c = repo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado: " + id));

        // se for cancelar, verifica integridade relacional
        if ("C".equals(novaSituacao)) {
            boolean temContratacoes = contratacaoRepo.existsByNomeCliente(c.getNomeCliente());
            if (temContratacoes) {
                throw new DataIntegrityViolationException(
                    "Erro, Cliente não cancelado pois possui contratações");
            }
        }

        c.setSituacaoCliente(novaSituacao);
        repo.save(c);
    }    
    
    
    public void excluir(Long id) {
        repo.deleteById(id);
    }
    
    /** Busca clientes cujo nome começa com prefix (case-insensitive) */
    public List<Cliente> buscarPorPrefixo(String prefix) {
        return repo.findByNomeClienteStartingWithIgnoreCase(prefix);
    }

    public boolean existePorCodigo(Integer codCliente) {
        return repo.existsByCodCliente(codCliente);
    }
    
    /** Retorna true se houver outro cliente com o mesmo nome e id diferente */
    public boolean existsByNomeClienteAndIdNot(String nomeCliente, Long id) {
        return repo.existsByNomeClienteAndIdNot(nomeCliente, id);
    }
    
    /** Retorna true se já existir um cliente com este nome */
    public boolean existsByNomeCliente(String nomeCliente) {
        return repo.existsByNomeCliente(nomeCliente);
    }

    // método que busca por nome usando o repo
    public Optional<Cliente> buscarPorNome(String nome) {
        return repo.findByNomeCliente(nome);
    }
    
    
    /** Retorna true se existirem contratações para esse nome de cliente */
    // existências de viagens para o cliente
    public boolean clientePossuiContratacoes(String nomeCliente) {
      return contratacaoRepo.existsByNomeCliente(nomeCliente);
    }
    
    
    /** FILTROS */
    
    public List<Cliente> filtrarPorNome(String prefix) {
        return repo.findByNomeClienteStartingWithIgnoreCaseOrderByNomeCliente(prefix);
    }

    public List<Cliente> filtrarPorCodigo(Integer inicio, Integer fim) {
        return repo.findByCodClienteBetweenOrderByCodCliente(inicio, fim);
    }

    public List<Cliente> filtrarPorData(LocalDate inicio, LocalDate fim) {
        return repo.findByDataCadastroBetweenOrderByDataCadastroAscNomeClienteAsc(inicio, fim);
    }

    public List<Cliente> filtrarPorSituacao(String sit) {
        return repo.findBySituacaoClienteOrderByNomeCliente(sit);
    }
    
    public List<Cliente> filtrarPorDataESituacao(LocalDate inicio, LocalDate fim, String sit) {
        return repo.findByDataCadastroBetweenAndSituacaoClienteOrderByDataCadastroAscNomeClienteAsc(
            inicio, fim, sit
        );
    }

    public List<Cliente> listarTodosOrdenados() {
        return repo.findAllByOrderByNomeCliente();
    }

    
}
