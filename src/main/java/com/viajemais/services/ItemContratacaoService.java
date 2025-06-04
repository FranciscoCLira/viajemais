package com.viajemais.services;

import com.viajemais.entities.ItemContratacao;
import com.viajemais.repositories.ItemContratacaoRepository;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ItemContratacaoService {

    private final ItemContratacaoRepository itemContratacaoRepository;

    public ItemContratacaoService(ItemContratacaoRepository itemContratacaoRepository) {
        this.itemContratacaoRepository = itemContratacaoRepository;
    }

    public void salvar(ItemContratacao item) {
        itemContratacaoRepository.save(item);
    }
    
    public List<ItemContratacao> buscarItensPorContratacao(Long contratacaoId) {
        return itemContratacaoRepository.findByContratacaoId(contratacaoId);
    }

}
