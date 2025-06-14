package com.viajemais.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@Entity
@Table(name = "CATEGORIA", uniqueConstraints = @UniqueConstraint(columnNames = "nome"))
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 50)
    @Pattern(regexp = "^(?=.*[A-Za-z])[A-Za-z0-9 ]+$",
             message="Só alfanumérico e ao menos uma letra")
    @Column(name = "nome", nullable = false, unique = true)
    private String nome;

    @Column(name = "situacao", length = 1, nullable = false)
    private String situacaoCategoria; // A, I, P, C

    @Column(name = "data", nullable = false)
    private LocalDate data;
    

    // getters e setters…

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getSituacaoCategoria() {
		return situacaoCategoria;
	}

	public void setSituacaoCategoria(String situacaoCategoria) {
		this.situacaoCategoria = situacaoCategoria;
	}

	public LocalDate getData() {
		return data;
	}

	public void setData(LocalDate data) {
		this.data = data;
	}
	
    // Métodos
	
	public String getDescricaoSituacao() {
		  return switch (this.situacaoCategoria) {
		    case "A" -> "Ativa";
		    case "I" -> "Inativa";
		    case "C" -> "Cancelada";
		    case "P" -> "Pendente";
		    default -> "Desconhecida";
		  };
		}
}
