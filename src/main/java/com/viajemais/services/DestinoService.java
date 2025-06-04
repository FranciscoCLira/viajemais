package com.viajemais.services;

import com.viajemais.entities.Destino;
import com.viajemais.repositories.DestinoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DestinoService {

    private final DestinoRepository destinoRepository;

    public DestinoService(DestinoRepository destinoRepository) {
        this.destinoRepository = destinoRepository;
    }

    public List<Destino> buscarPorIds(List<Long> ids) {
        return destinoRepository.findAllById(ids);
    }

    public Destino buscarPorId(Long id) {
        return destinoRepository.findById(id).orElseThrow();
    }

    public List<Destino> listarTodos() {
        return destinoRepository.findAll();
    }
} 
