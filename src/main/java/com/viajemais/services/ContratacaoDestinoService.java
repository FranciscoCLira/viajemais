package com.viajemais.services;

import com.viajemais.entities.ContratacaoDestino;
import com.viajemais.repositories.ContratacaoDestinoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContratacaoDestinoService {

    private final ContratacaoDestinoRepository contratacaoDestinoRepository;

    public ContratacaoDestinoService(ContratacaoDestinoRepository contratacaoDestinoRepository) {
        this.contratacaoDestinoRepository = contratacaoDestinoRepository;
    }

    public List<ContratacaoDestino> listarTodos() {
        return contratacaoDestinoRepository.findAll();
    }

    public ContratacaoDestino salvar(ContratacaoDestino contratacaoDestino) {
        return contratacaoDestinoRepository.save(contratacaoDestino);
    }
}
