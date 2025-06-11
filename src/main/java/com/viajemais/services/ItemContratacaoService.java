package com.viajemais.services;

import com.viajemais.entities.ItemContratacao;
import com.viajemais.repositories.ItemContratacaoRepository;

import jakarta.transaction.Transactional;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ItemContratacaoService {

    private final ItemContratacaoRepository itemRepo;

    public ItemContratacaoService(ItemContratacaoRepository itemRepo) {
        this.itemRepo = itemRepo;
    }

    /** Remove todos os itens de uma determinada contratação */
    @Transactional
    public void removerPorContratacao(Long contratacaoId) {
    	itemRepo.deleteByContratacaoId(contratacaoId);
    }

    /** Remove todos os itens de um destino */
    @Transactional
    public void removerPorDestino(Long destinoId) {
    	itemRepo.deleteByDestinoId(destinoId);
    }
    

    public void salvar(ItemContratacao item) {
        itemRepo.save(item);
    }
    
    public List<ItemContratacao> buscarItensPorContratacao(Long contratacaoId) {
        return itemRepo.findByContratacaoId(contratacaoId);
    }

}
