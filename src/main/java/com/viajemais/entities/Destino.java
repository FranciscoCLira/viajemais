package com.viajemais.entities;

import jakarta.persistence.*;

@Entity
public class Destino {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String local;
    private String categoria;

    @Column(name = "imagem_url")
    private String imagemUrl;

    private double preco;

    // Getters e Setters 

    public Long getId() {
        return id;
    }

    public String getLocal() {
        return local;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getImagemUrl() {
        return imagemUrl;
    }

    public double getPreco() {
        return preco;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public void setImagemUrl(String imagemUrl) {
        this.imagemUrl = imagemUrl;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    @Override
    public String toString() {
        return local + " (" + categoria + ") - R$" + preco;
    }
}


