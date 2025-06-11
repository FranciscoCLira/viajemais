package com.viajemais.services;

import com.viajemais.entities.Destino;
import com.viajemais.repositories.DestinoRepository;

import jakarta.transaction.Transactional;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DestinoService {

    private final DestinoRepository destinoRepository;
    private final ItemContratacaoService itemService;

    public DestinoService(DestinoRepository destinoRepository,
                    ItemContratacaoService itemService) {
        this.destinoRepository = destinoRepository;
        this.itemService = itemService;
    }

    public List<Destino> buscarPorIds(List<Long> ids) {
        return destinoRepository.findAllById(ids);
    }

    public Destino buscarPorId(Long id) {
        return destinoRepository.findById(id).orElseThrow();
    }

    
    /**
     * Busca destinos por uma lista de IDs.
     */
    public Optional<Destino> buscarPorId1(Long id) {
        return destinoRepository.findById(id);
    }

    /**
     * Retorna todos os destinos.
     */
    public List<Destino> listarTodos() {
        return destinoRepository.findAll();
    }

    /**
     * Cria ou atualiza um destino.
     */
    public Destino salvar(Destino destino) {
        return destinoRepository.save(destino);
    }

    @Transactional 
    public void excluir(Long id) {
        // 1) Remove todos os itens vinculados a este destino
        itemService.removerPorDestino(id);

        // 2) Só então exclui o destino
        destinoRepository.deleteById(id);
    }
    
    /**
     * Exclui um destino por ID.
     */
    @Transactional
    public void excluir1(Long id) {
        try {
            destinoRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            // opcional: logar ou tratar caso o ID não exista
            throw new IllegalArgumentException("Não foi possível excluir. Destino não encontrado: " + id);
        }
    }
}
