package com.viajemais.entities;

import java.text.ParseException;

import org.springframework.context.i18n.LocaleContextHolder;

import com.viajemais.config.StrictDoubleFormatter;

import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@Table(name="DESTINO")
public class Destino {
	/* LOG PARA TESTES DO PREÇO  */
    private static final Logger log = LoggerFactory.getLogger(Destino.class);
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message="Local é obrigatório")
    @Size(min=4, max=100, message="Local deve ter entre 4 e 100 caracteres")
    @Pattern(
      regexp="(?=.*\\p{L})[\\p{L}0-9 ]+",
      message="Local deve ser alfanumérico com acentuação grafica, mínimo 4 caracteres, e ao menos uma letra"
    )
    private String local;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;
    
    // não precisa do campo IdCategoria separado

    @Column(name = "imagem_url")
    private String imagemUrl;

    // Trocado de primitive double para wrapper Double
    // Double preco (em vez de double) permite que bean validation capture null

    /**
     * Só para binding no form: recebe exatamente o que o usuário digitou.
     * Validamos formato e obrigatoriedade aqui.
     */
    @Transient
    @NotBlank(message="Preço é obrigatório")
    @Pattern(
      // aceita:
      //  - apenas dígitos:       "1280"
      //  - com milhar:           "1.280"
      //  - com decimais:         "1280,45"
      //  - milhar + decimais:    "1.280,45"
      regexp="^\\d+(?:\\.\\d{3})*(?:,\\d{2})?$",
      message="Formato inválido. Use 1234 ou 1234,56 ou 1.234,56"
    )
    private String precoStr;
    
    /**
     * Este é o valor que vai para o banco.
     * Nunca recebe diretament e a string do form.
     */
    @Column(nullable = false)
    private Double preco;    
    

    // Getters e Setters 

    public Long getId() {
        return id;
    }

    public String getLocal() {
        return local;
    }

    public Categoria getCategoria() {
        return categoria;
    }
    
    public String getImagemUrl() {
        return imagemUrl;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setLocal(String local) {
        this.local = (local != null ? local.toUpperCase() : null);
    }


    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }
    
    public void setImagemUrl(String imagemUrl) {
        this.imagemUrl = imagemUrl;
    }

    // getters/setters do preco—sem anotações de validação
    
    public Double getPreco() { return preco; }
    public void setPreco(Double preco) { this.preco = preco; }
    
    // getter do precoStr: já formata o valor numérico ao abrir o “Editar”
    public String getPrecoStr() {
      if (preco == null) return "";
      // formata em pt-BR automaticamente
      return new StrictDoubleFormatter()
               .print(preco, LocaleContextHolder.getLocale());
    }

    // setter do precoStr: dispara o @Pattern/@NotBlank
    public void setPrecoStr(String precoStr) {
      this.precoStr = precoStr;
      // se passou no @Pattern, vamos converter para Double
      try {
        Double v = new StrictDoubleFormatter()
                        .parse(precoStr, LocaleContextHolder.getLocale());
        this.preco = v;
      } catch (ParseException e) {
        // nada aqui: o padrão @Pattern já bloqueou formatos inválidos
      }    
    }   

    /**
     * Garante parse estrito de toda a string (não só parte dela).
     */
    @AssertTrue(message="Formato inválido. Use 1.234,56")
    public boolean isPrecoStrValido() {
    	
      /* LOG PARA TESTE DO PREÇO */
      log.debug("Validando precoStr='{}'", precoStr);	
    	
      if (precoStr == null || precoStr.isBlank()) {
        // o @NotBlank já vai reclamar se estiver vazio
        return true;
      }
      try {
        // usa seu StrictDoubleFormatter
        new StrictDoubleFormatter()
          .parse(precoStr, LocaleContextHolder.getLocale());
        return true;
      } catch (ParseException ex) {
        return false;
      }
    }    
    
    // Método para impressão, listar 
    @Override
    public String toString() {
        return local + " (" + categoria + ") - R$" + preco;
    }
}


