package com.viajemais.repositories;

import com.viajemais.entities.ItemContratacao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemContratacaoRepository extends JpaRepository<ItemContratacao, Long> {
	List<ItemContratacao> findByContratacaoId(Long contratacaoId);

}
