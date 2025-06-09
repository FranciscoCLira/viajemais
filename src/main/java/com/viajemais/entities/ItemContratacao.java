package com.viajemais.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;

@Entity
public class ItemContratacao {
    @Id @GeneratedValue
    private Long id;
 
    @ManyToOne
    private Contratacao contratacao;

    @ManyToOne
    private Destino destino;

    
    @Column(nullable = false)
    private Double precoUnitario;
    
    @Transient 
    private double valorDestino;
    
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
	
	public Double getPrecoUnitario() {
		return precoUnitario;
	}
	public void setPrecoUnitario(Double precoUnitario) {
		this.precoUnitario = precoUnitario;
	}
	
    public double getValorDestino() {
        return valorDestino;
    }
    public void setValorDestino(double valorDestino) {
        this.valorDestino = valorDestino;
    }

}
