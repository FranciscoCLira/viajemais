package com.viajemais.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.viajemais.entities.Destino;

import java.util.List;

public interface DestinoRepository extends JpaRepository<Destino, Long> {
    List<Destino> findByCategoria(String categoria);
}