package com.viajemais.repositories;

import com.viajemais.entities.Contratacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ContratacaoRepository extends JpaRepository<Contratacao, Long> {

	@Query("SELECT c FROM Contratacao c LEFT JOIN FETCH c.itens i LEFT JOIN FETCH i.destino")
	List<Contratacao> buscarComDestinos();

    // para os filtros:  nada além de métodos padrão
}
