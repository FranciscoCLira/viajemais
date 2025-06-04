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

    private int quantidadePessoas;

    private LocalDate data;

    private LocalDate periodoInicio;

    private LocalDate periodoFim;

    @OneToMany(mappedBy = "contratacao", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ItemContratacao> itens;

    // ✅ Método calculado para o total da viagem
    @Transient
    public double getTotal() {
        if (itens == null || quantidadePessoas <= 0) return 0.0;

        return itens.stream()
                    .filter(item -> item.getPrecoUnitario() != null)
                    .mapToDouble(ItemContratacao::getPrecoUnitario)
                    .sum() * quantidadePessoas;
    }

    // Getters e Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
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

    public void setData(LocalDate data) {
        this.data = data;
    }

    public LocalDate getPeriodoInicio() {
        return periodoInicio;
    }

    public void setPeriodoInicio(LocalDate periodoInicio) {
        this.periodoInicio = periodoInicio;
    }

    public LocalDate getPeriodoFim() {
        return periodoFim;
    }

    public void setPeriodoFim(LocalDate periodoFim) {
        this.periodoFim = periodoFim;
    }

    public List<ItemContratacao> getItens() {
        return itens;
    }

    public void setItens(List<ItemContratacao> itens) {
        this.itens = itens;
    }
}
