package com.viajemais.repositories;

import com.viajemais.entities.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    
    boolean existsByCodCliente(Integer codCliente);
    
    @Query("SELECT MAX(c.codCliente) FROM Cliente c")
    Long findMaxCodCliente();

}
