package com.viajemais.services;

import com.viajemais.entities.Contratacao;
import com.viajemais.repositories.ContratacaoRepository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ContratacaoService {

    private final ContratacaoRepository contratacaoRepository;

    public ContratacaoService(ContratacaoRepository contratacaoRepository) {
        this.contratacaoRepository = contratacaoRepository;
    }

    public Contratacao salvar(Contratacao c) {
        // regra de duração, validações…
        return contratacaoRepository.save(c);
    }

    public List<Contratacao> listarTodas() {
        return contratacaoRepository.findAll();
    }

    /** retorna Optional para usar orElseThrow / orElse */
    public Optional<Contratacao> buscarPorId(Long id) {
        return contratacaoRepository.findById(id);
    }

    /** Verdadeiro se a data de início for estritamente futura */
    public boolean canExcluir(Long id) {
        return buscarPorId(id)
               .map(c -> c.getPeriodoInicio().isAfter(LocalDate.now()))
               .orElse(false);
    }

    public void excluir(Long id) {
        if (!canExcluir(id)) {
            throw new DataIntegrityViolationException(
              "Só é possível excluir viagens cujo início seja futuro");
        }
        contratacaoRepository.deleteById(id);
    }    
}
