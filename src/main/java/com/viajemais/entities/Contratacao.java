package com.viajemais.entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Contratacao {
    @Id @GeneratedValue
    private Long id;

    private LocalDate data;

    @OneToMany(mappedBy = "contratacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemContratacao> itens = new ArrayList<>();

    
    // Getters e setters

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDate getData() {
		return data;
	}

	public void setData(LocalDate data) {
		this.data = data;
	}

	public List<ItemContratacao> getItens() {
		return itens;
	}

	public void setItens(List<ItemContratacao> itens) {
		this.itens = itens;
	}
    
}
