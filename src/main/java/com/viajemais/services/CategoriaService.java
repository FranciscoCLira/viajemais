package com.viajemais.services;

import com.viajemais.entities.Categoria;
import com.viajemais.repositories.CategoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.dao.DataIntegrityViolationException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CategoriaService {
    private final CategoriaRepository repo;
    public CategoriaService(CategoriaRepository repo) { this.repo = repo; }

    public List<Categoria> listarTodas() { return repo.findAll(); }
    public Optional<Categoria> buscarPorId(Long id) { return repo.findById(id); }

    public Categoria salvar(Categoria c) {
        if (c.getId()==null) {
            c.setData(LocalDate.now());
            if (repo.findByNomeStartingWithIgnoreCase(c.getNome()).stream()
                    .anyMatch(x->x.getNome().equalsIgnoreCase(c.getNome()))) {
                throw new DataIntegrityViolationException("Nome deve ser único");
            }
        }
        return repo.save(c);
    }

    public void excluir(Long id) { repo.deleteById(id); }
}
