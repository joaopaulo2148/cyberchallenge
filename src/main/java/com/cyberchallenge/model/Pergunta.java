package com.cyberchallenge.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

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

    // MELHORIA (reformulacao): so se aplica quando tipo = VERDADEIRO_FALSO;
    // para perguntas de multipla escolha esse campo fica nulo e a resposta
    // certa e a alternativa com correta = true (veja getAlternativas()).
    @Column(name = "resposta_correta")
    private Boolean respostaCorreta;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String explicacao;

    @Column(nullable = false)
    private Boolean ativa = true;

    // Nivel de dificuldade da pergunta (1 = Leigo, 2 = Usuario,
    // 3 = Intermediario, 4 = Especialista).
    @Column(nullable = false)
    private Integer nivel;

    // MELHORIA (reformulacao, item 8): formato da pergunta. As perguntas
    // antigas (seed original) continuam todas como VERDADEIRO_FALSO -- essa
    // coluna tem um valor padrao para nao quebrar dados existentes.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoPergunta tipo = TipoPergunta.VERDADEIRO_FALSO;

    // EAGER de proposito: cada partida carrega no maximo 10 perguntas, cada
    // uma com no maximo 4 alternativas -- volume irrelevante, e evita ter
    // que abrir uma transacao especifica so para ler essa lista mais tarde
    // (o mesmo tipo de problema de "LazyInitializationException" que
    // apareceu com outra relacao lazy neste projeto).
    @OneToMany(mappedBy = "pergunta", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Alternativa> alternativas = new ArrayList<>();

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
    public Integer getNivel() { return nivel; }
    public void setNivel(Integer nivel) { this.nivel = nivel; }
    public TipoPergunta getTipo() { return tipo; }
    public void setTipo(TipoPergunta tipo) { this.tipo = tipo; }
    public List<Alternativa> getAlternativas() { return alternativas; }
    public void setAlternativas(List<Alternativa> alternativas) { this.alternativas = alternativas; }
}
