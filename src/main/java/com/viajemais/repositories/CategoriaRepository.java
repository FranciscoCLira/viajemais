package com.viajemais.repositories;

import com.viajemais.entities.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    List<Categoria> findByNomeStartingWithIgnoreCase(String prefix);
}
