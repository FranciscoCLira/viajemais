package com.viajemais.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.viajemais.entities.Destino;

import java.util.List;

public interface DestinoRepository extends JpaRepository<Destino, Long> {
	
    // para autocomplete de nome
    List<Destino> findByLocalStartingWithIgnoreCase(String prefix);
	
    // percorre o objeto: campoCategoria.nome
    List<Destino> findByCategoriaNomeIgnoreCase(String nomeCategoria);
    
    List<Destino> findByLocalContainingIgnoreCase(String substring);    
}