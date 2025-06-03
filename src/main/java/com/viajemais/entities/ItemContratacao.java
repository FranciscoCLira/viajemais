package com.viajemais.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class ItemContratacao {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    private Contratacao contratacao;

    @ManyToOne
    private Destino destino;

    private int quantidade; // Ex: número de pessoas
    private double precoUnitario; // Pode ser capturado do destino na hora da contratação

    
    // Getters e setters
    
    public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	public int getQuantidade() {
		return quantidade;
	}
	public void setQuantidade(int quantidade) {
		this.quantidade = quantidade;
	}
	public double getPrecoUnitario() {
		return precoUnitario;
	}
	public void setPrecoUnitario(double precoUnitario) {
		this.precoUnitario = precoUnitario;
	}
}
