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

    private final ContratacaoRepository repo;

    public ContratacaoService(ContratacaoRepository repo) {
        this.repo = repo;
    }


    /**
     * Retorna todas as contratações que:
     * - nomeCliente contém (ignoreCase) filtroCliente (se não nulo);
     * - dataConfirmação entre dataConfInicio/dataConfFim (se ambos não nulos);
     * - períodoInicio/períodoFim engloba o filtroPeriodoInicio/filtroPeriodoFim (se ambos não nulos).
     *
     * Se nenhum critério for passado, devolve todas.
     */
    public List<Contratacao> buscarComFiltro(String filtroCliente,
                                             LocalDate dataConfInicio,
                                             LocalDate dataConfFim,
                                             LocalDate perInicio,
                                             LocalDate perFim) {
        return repo.findAll().stream()
            .filter(c -> {
                // filtro por cliente
                if (filtroCliente != null && !filtroCliente.isBlank()) {
                    if (!c.getNomeCliente()
                          .toLowerCase()
                          .contains(filtroCliente.toLowerCase())) {
                        return false;
                    }
                }
                // filtro por data de confirmação
                if (dataConfInicio != null && dataConfFim != null) {
                    if (c.getData().isBefore(dataConfInicio) ||
                        c.getData().isAfter (dataConfFim)) {
                        return false;
                    }
                }
                // filtro por período
                if (perInicio != null && perFim != null) {
                    if (c.getPeriodoInicio().isAfter(perFim) ||
                        c.getPeriodoFim()  .isBefore(perInicio)) {
                        return false;
                    }
                }
                return true;
            })
            .toList();
    }    
    
    
    public Contratacao salvar(Contratacao c) {
        // regra de duração, validações…
        return repo.save(c);
    }

    public List<Contratacao> listarTodas() {
        return repo.findAll();
    }

    /** retorna Optional para usar orElseThrow / orElse */
    public Optional<Contratacao> buscarPorId(Long id) {
        return repo.findById(id);
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
        repo.deleteById(id);
    }    
}
