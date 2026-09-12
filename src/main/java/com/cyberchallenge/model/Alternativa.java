package com.cyberchallenge.model;

import jakarta.persistence.*;

/** Uma opcao de resposta de uma pergunta de multipla escolha (item 8). */
@Entity
@Table(name = "alternativas")
public class Alternativa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pergunta_id", nullable = false)
    private Pergunta pergunta;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String texto;

    @Column(nullable = false)
    private Boolean correta = false;

    public Alternativa() {}

    public Alternativa(String texto, Boolean correta) {
        this.texto = texto;
        this.correta = correta;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Pergunta getPergunta() { return pergunta; }
    public void setPergunta(Pergunta pergunta) { this.pergunta = pergunta; }
    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
    public Boolean getCorreta() { return correta; }
    public void setCorreta(Boolean correta) { this.correta = correta; }
}
