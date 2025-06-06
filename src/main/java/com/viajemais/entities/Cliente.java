package com.viajemais.entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long codCliente; // código de identificação visível para o cliente

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 50, message = "Máximo de 50 caracteres")
    @Pattern(
        regexp = "^(?=.*[A-Za-z])[A-Za-z0-9 ]+$",
        message = "Use apenas letras, números e espaços, com ao menos uma letra"
    )
    @Column(name = "nome_cliente", unique = true, nullable = false, length = 50)
    private String nomeCliente;

    private String situacaoCliente; // A, I, P ou C

    private LocalDate dataCadastro;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCodCliente() {
        return codCliente;
    }

    public void setCodCliente(Long codCliente) {
        this.codCliente = codCliente;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public String getSituacaoCliente() {
        return situacaoCliente;
    }

    public void setSituacaoCliente(String situacaoCliente) {
        this.situacaoCliente = situacaoCliente;
    }

    public LocalDate getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDate dataCadastro) {
        this.dataCadastro = dataCadastro;
    }
}
