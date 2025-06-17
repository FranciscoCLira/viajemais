package com.viajemais.services;

import com.viajemais.entities.ItemContratacao;
import com.viajemais.repositories.ItemContratacaoRepository;

import jakarta.transaction.Transactional;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ItemContratacaoService {

    private final ItemContratacaoRepository repo;

    public ItemContratacaoService(ItemContratacaoRepository repo) {
        this.repo = repo;
    }

    /** Retorna true se existir ao menos um item referenciando este destino */
    public boolean existsByDestinoId(Long destinoId) {
        return repo.existsByDestinoId(destinoId);
    }
    
    /** Remove todos os itens de uma determinada contratação */
    @Transactional
    public void removerPorContratacao(Long contratacaoId) {
    	repo.deleteByContratacaoId(contratacaoId);
    }

    /** Remove todos os itens de um destino */
    @Transactional
    public void removerPorDestino(Long destinoId) {
    	repo.deleteByDestinoId(destinoId);
    }
    

    public void salvar(ItemContratacao item) {
        repo.save(item);
    }
    
    public List<ItemContratacao> buscarItensPorContratacao(Long contratacaoId) {
        return repo.findByContratacaoId(contratacaoId);
    }

}
