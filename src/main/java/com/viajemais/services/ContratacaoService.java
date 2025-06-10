package com.viajemais.services;

import com.viajemais.entities.Contratacao;
import com.viajemais.repositories.ContratacaoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.time.temporal.ChronoUnit;

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

    public void excluir(Long id) {
        contratacaoRepository.deleteById(id);
    }
}
