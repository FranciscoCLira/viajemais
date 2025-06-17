package com.viajemais.repositories;

import com.viajemais.entities.ItemContratacao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemContratacaoRepository 
                 extends JpaRepository<ItemContratacao, Long> {
	
    /** Retorna true se existir ao menos um item vinculando este destino */
    boolean existsByDestinoId(Long destinoId);	
	        
	List<ItemContratacao> findByContratacaoId(Long contratacaoId);
	
    void deleteByContratacaoId(Long contratacaoId);
    void deleteByDestinoId(Long destinoId);

}
