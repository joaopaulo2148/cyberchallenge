package com.cyberchallenge.model; // BUG CORRIGIDO: pacote estava "cyberchallenge.model" (faltava o "com.")

import jakarta.persistence.*;

@Entity
@Table(name = "perguntas")
public class Pergunta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String texto;

    @Column(nullable = false)
    private String tema;

    @Column(name = "resposta_correta", nullable = false)
    private Boolean respostaCorreta;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String explicacao;

    @Column(nullable = false)
    private Boolean ativa = true;

    public Pergunta() {}

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
    public String getTema() { return tema; }
    public void setTema(String tema) { this.tema = tema; }
    public Boolean getRespostaCorreta() { return respostaCorreta; }
    public void setRespostaCorreta(Boolean respostaCorreta) { this.respostaCorreta = respostaCorreta; }
    public String getExplicacao() { return explicacao; }
    public void setExplicacao(String explicacao) { this.explicacao = explicacao; }
    public Boolean getAtiva() { return ativa; }
    public void setAtiva(Boolean ativa) { this.ativa = ativa; }
}
