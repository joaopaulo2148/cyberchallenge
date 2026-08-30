package com.cyberchallenge.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "participantes")
public class Participante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(name = "data_participacao", nullable = false)
    private LocalDateTime dataParticipacao;

    private Integer pontuacao;

    @Column(name = "tempo_total")
    private Double tempoTotal;

    @Column(name = "tempo_medio")
    private Double tempoMedio;

    // Relacionamento 1 para N: Um participante possui varias respostas
    @OneToMany(mappedBy = "participante", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Resposta> respostas = new ArrayList<>();

    public Participante() {}

    @PrePersist
    protected void onCreate() {
        this.dataParticipacao = LocalDateTime.now();
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public LocalDateTime getDataParticipacao() { return dataParticipacao; }
    public void setDataParticipacao(LocalDateTime dataParticipacao) { this.dataParticipacao = dataParticipacao; }
    public Integer getPontuacao() { return pontuacao; }
    public void setPontuacao(Integer pontuacao) { this.pontuacao = pontuacao; }
    public Double getTempoTotal() { return tempoTotal; }
    public void setTempoTotal(Double tempoTotal) { this.tempoTotal = tempoTotal; }
    public Double getTempoMedio() { return tempoMedio; }
    public void setTempoMedio(Double tempoMedio) { this.tempoMedio = tempoMedio; }
    public List<Resposta> getRespostas() { return respostas; }
    public void setRespostas(List<Resposta> respostas) { this.respostas = respostas; }
}
