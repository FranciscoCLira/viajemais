package com.viajemais.entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
public class Contratacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomeCliente;
    
    @Column(nullable = false)
    private int quantidadePessoas;
    
    private LocalDate data;
    private LocalDate periodoInicio;
    private LocalDate periodoFim;

    @OneToMany(mappedBy = "contratacao", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ContratacaoDestino> destinos;
    
    @OneToMany(mappedBy = "contratacao", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ItemContratacao> itens;

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public int getQuantidadePessoas() {
        return quantidadePessoas;
    }

    public void setQuantidadePessoas(int quantidadePessoas) {
        this.quantidadePessoas = quantidadePessoas;
    }    
    
    public LocalDate getData() {
        return data;
    }

    public LocalDate getPeriodoInicio() {
        return periodoInicio;
    }

    public LocalDate getPeriodoFim() {
        return periodoFim;
    }

    public List<ContratacaoDestino> getDestinos() {
        return destinos;
    }
    
    public List<ItemContratacao> getItens() {
        return itens;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public void setPeriodoInicio(LocalDate periodoInicio) {
        this.periodoInicio = periodoInicio;
    }

    public void setPeriodoFim(LocalDate periodoFim) {
        this.periodoFim = periodoFim;
    }

    public void setDestinos(List<ContratacaoDestino> destinos) {
        this.destinos = destinos;
    }
    
    public void setItens(List<ItemContratacao> itens) {
        this.itens = itens;
    }
    
}
