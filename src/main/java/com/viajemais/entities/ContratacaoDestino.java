package com.viajemais.entities;

import jakarta.persistence.*;

@Entity
public class ContratacaoDestino {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "contratacao_id")
    private Contratacao contratacao;

    @ManyToOne
    @JoinColumn(name = "destino_id")
    private Destino destino;

    // Futuro: campo extra como quantidade, data personalizada, etc.
    // private int quantidade;

    public ContratacaoDestino() {
    }

    public ContratacaoDestino(Contratacao contratacao, Destino destino) {
        this.contratacao = contratacao;
        this.destino = destino;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public Contratacao getContratacao() {
        return contratacao;
    }

    public void setContratacao(Contratacao contratacao) {
        this.contratacao = contratacao;
    }

    public Destino getDestino() {
        return destino;
    }

    public void setDestino(Destino destino) {
        this.destino = destino;
    }
}
