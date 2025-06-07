package com.viajemais.services;

import com.viajemais.entities.Contratacao;
import com.viajemais.entities.ItemContratacao;
import com.viajemais.repositories.ContratacaoRepository;
import com.viajemais.repositories.ItemContratacaoRepository;

import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class ContratacaoService {

    private final ContratacaoRepository contratacaoRepository;
    private final ItemContratacaoRepository itemContratacaoRepository;

    public ContratacaoService(ContratacaoRepository contratacaoRepository,
                              ItemContratacaoRepository itemContratacaoRepository) {
        this.contratacaoRepository = contratacaoRepository;
        this.itemContratacaoRepository = itemContratacaoRepository;
    }

    public List<Contratacao> listarTodas() {
        return contratacaoRepository.buscarComDestinos(); // se estiver usando @Query personalizada
    }

    public Contratacao salvar(Contratacao c) {
        // Se ainda houver alguma data nula, não executamos a duração aqui
        if (c.getPeriodoInicio() != null && c.getPeriodoFim() != null) {
            long dias = ChronoUnit.DAYS.between(c.getPeriodoInicio(), c.getPeriodoFim());
            if (dias > 90) {
                throw new IllegalArgumentException("A duração da viagem não pode exceder 90 dias");
            }
        }
        return contratacaoRepository.save(c);
    }
    
    public Contratacao buscarPorId(Long id) {
        return contratacaoRepository.findById(id).orElse(null);
    }

    public List<ItemContratacao> listarItensPorContratacao(Long contratacaoId) {
        return itemContratacaoRepository.findByContratacaoId(contratacaoId);
    }
}
