package com.viajemais.services;

import com.viajemais.entities.Contratacao;
import com.viajemais.entities.ContratacaoDestino;
import com.viajemais.entities.Destino;
import com.viajemais.repositories.ContratacaoDestinoRepository;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ContratacaoDestinoService {

    private final ContratacaoDestinoRepository contratacaoDestinoRepository;

    public ContratacaoDestinoService(ContratacaoDestinoRepository contratacaoDestinoRepository) {
        this.contratacaoDestinoRepository = contratacaoDestinoRepository;
    }

    public ContratacaoDestino salvar(Contratacao contratacao, Destino destino) {
        ContratacaoDestino cd = new ContratacaoDestino();
        cd.setContratacao(contratacao);
        cd.setDestino(destino);
        return contratacaoDestinoRepository.save(cd);
    }

    // Adicione métodos adicionais conforme necessário (ex: listar por contratacao, etc.)

    public List<ContratacaoDestino> listarTodos() {
        return contratacaoDestinoRepository.findAll();
    }
}
