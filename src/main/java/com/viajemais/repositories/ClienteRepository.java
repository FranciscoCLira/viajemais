package com.viajemais.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.viajemais.entities.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    
    boolean existsByCodCliente(Integer codCliente);
    
    boolean existsByNomeCliente(String nomeCliente);
    
    List<Cliente> findByNomeClienteStartingWithIgnoreCase(String prefix);

    @Query("SELECT MAX(c.codCliente) FROM Cliente c")
    Long findMaxCodCliente();
}
