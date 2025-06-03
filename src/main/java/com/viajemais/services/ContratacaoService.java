package com.viajemais.services;

import com.viajemais.entities.Contratacao;
import com.viajemais.repositories.ContratacaoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContratacaoService {

    private final ContratacaoRepository contratacaoRepository;

    public ContratacaoService(ContratacaoRepository contratacaoRepository) {
        this.contratacaoRepository = contratacaoRepository;
    }

    public List<Contratacao> listarTodas() {
        return contratacaoRepository.findAll();
    }

    public Contratacao salvar(Contratacao contratacao) {
        return contratacaoRepository.save(contratacao);
    }

    public Contratacao buscarPorId(Long id) {
        return contratacaoRepository.findById(id).orElse(null);
    }
}
