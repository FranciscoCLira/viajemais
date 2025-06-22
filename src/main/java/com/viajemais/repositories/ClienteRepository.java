package com.viajemais.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.viajemais.entities.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    
    boolean existsByCodCliente(Integer codCliente);
    
    boolean existsByNomeCliente(String nomeCliente);
    boolean existsByNomeClienteAndIdNot(String nomeCliente, Long id);
     
    Optional<Cliente> findByNomeCliente(String nomeCliente);
    
    List<Cliente> findByNomeClienteStartingWithIgnoreCase(String prefix);
    
    
    // filtro por nome iniciando
    List<Cliente> findByNomeClienteStartingWithIgnoreCaseOrderByNomeCliente(String prefix);

    // filtro por código entre X e Y
    List<Cliente> findByCodClienteBetweenOrderByCodCliente(Integer inicio, Integer fim);

    // filtro por dataCadastro entre X e Y
    List<Cliente> findByDataCadastroBetweenOrderByDataCadastroAscNomeClienteAsc(
              LocalDate inicio, LocalDate fim);

    // filtro por situação, ordenado por nome
    List<Cliente> findBySituacaoClienteOrderByNomeCliente(String situacao);

    // listagem padrão
    List<Cliente> findAllByOrderByNomeCliente();    
    
    // filtro combinado por data e situação, ordenando por data e nome
    List<Cliente> findByDataCadastroBetweenAndSituacaoClienteOrderByDataCadastroAscNomeClienteAsc(
        LocalDate inicio,
        LocalDate fim,
        String situacao
    );

    
    
    @Query("SELECT MAX(c.codCliente) FROM Cliente c")
    Long findMaxCodCliente();
}
