package com.viajemais.services;

import com.viajemais.entities.Destino;
import com.viajemais.repositories.DestinoRepository;

import jakarta.transaction.Transactional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DestinoService {

    private final DestinoRepository repo;
    private final ItemContratacaoService itemRepo;

    public DestinoService(DestinoRepository repo,
                    ItemContratacaoService itemRepo) {
        this.repo = repo;
        this.itemRepo = itemRepo;
    }


    public List<String> sugerirNomesList(String prefix) {
    	  return repo.findByLocalContainingIgnoreCase(prefix)
    	             .stream().map(Destino::getLocal).toList();
    }
    
    
    /** Busca geral com filtros opcionais */
    public List<Destino> buscarComFiltro(String nome,
                                         String categoria,
                                         Double precoMax) {
        return repo.findAll().stream()
            .filter(d -> nome == null || nome.isBlank()
                         || d.getLocal()
                             .toLowerCase()
                             .startsWith(nome.toLowerCase()))
            .filter(d -> categoria == null || categoria.isBlank()
                         || d.getCategoria().getNome().equals(categoria))
            .filter(d -> precoMax == null
                         || d.getPreco() <= precoMax)
            .toList();
    }

    /** Lista só os nomes para autocomplete */
    public List<String> sugerirNomes(String prefix) {
        return repo.findByLocalStartingWithIgnoreCase(prefix).stream()
                   .map(Destino::getLocal)
                   .toList();
    }
    
    
    /** Só pode excluir se não houver nenhum item de contratação deste destino */
    public boolean podeExcluir(Long destinoId) {
        // nota: existsByDestinoId retorna true quando HÁ referências,
        // então invertemos o resultado
        return !itemRepo.existsByDestinoId(destinoId);
    }
    
    /** Exclui ou lança exceção se não puder */
    public void excluir(Long id) {
        if (!podeExcluir(id)) {
            throw new DataIntegrityViolationException(
              "Este destino está em pelo menos uma contratação e não pode ser excluído.");
        }
        repo.deleteById(id);
    }
    
    public List<Destino> buscarPorIds(List<Long> ids) {
        return repo.findAllById(ids);
    }

    public Destino buscarPorId(Long id) {
        return repo.findById(id).orElseThrow();
    }

    
    /**
     * Busca destinos por uma lista de IDs.
     */
    public Optional<Destino> buscarPorId1(Long id) {
        return repo.findById(id);
    }

    /**
     * Retorna todos os destinos.
     */
    public List<Destino> listarTodos() {
        return repo.findAll();
    }

    /**
     * Cria ou atualiza um destino.
     */
    public Destino salvar(Destino destino) {
        return repo.save(destino);
    }

    @Transactional 
    public void excluir2(Long id) {
        // 1) Remove todos os itens vinculados a este destino
        itemRepo.removerPorDestino(id);

        // 2) Só então exclui o destino
        repo.deleteById(id);
    }
    
    /**
     * Exclui um destino por ID.
     */
    @Transactional
    public void excluir1(Long id) {
        try {
            repo.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            // opcional: logar ou tratar caso o ID não exista
            throw new IllegalArgumentException("Não foi possível excluir. Destino não encontrado: " + id);
        }
    }
}
