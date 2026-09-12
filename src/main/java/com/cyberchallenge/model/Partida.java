package com.cyberchallenge.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Uma jogada completa (10 perguntas) de um Participante em um nivel.
 *
 * MELHORIA (ajuste de banco de dados): esta entidade existia antes com o
 * nome "Participante" e misturava identidade (nickname/idade/autoavaliacao)
 * com o resultado da partida em si. Agora ela guarda apenas o resultado, e
 * referencia a identidade por chave estrangeira (participante_id) -- um
 * mesmo Participante poderia, em tese, ter mais de uma Partida.
 */
@Entity
@Table(name = "partidas")
public class Partida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "participante_id", nullable = false)
    private Participante participante;

    @Column(name = "data_partida", nullable = false)
    private LocalDateTime dataPartida;

    // Nivel de dificuldade desta partida (1 a 4)
    @Column(nullable = false)
    private Integer nivel;

    // MELHORIA (simplificacao do sistema de pontuacao): 1 ponto por
    // acerto, numa partida de exatamente 10 perguntas -- pontuacao,
    // acertos e nota final passam a ser, na pratica, o mesmo numero
    // (0 a 10). Mantidos como campos separados por clareza semantica e
    // para nao depender de recalculo em toda leitura.
    @Column(nullable = false)
    private Integer pontuacao;

    @Column(nullable = false)
    private Integer acertos;

    @Column(nullable = false)
    private Integer erros;

    @Column(name = "tempo_total")
    private Double tempoTotal;

    @Column(name = "tempo_medio")
    private Double tempoMedio;

    @Column(name = "nota_final", nullable = false)
    private Double notaFinal;

    @OneToMany(mappedBy = "partida", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Resposta> respostas = new ArrayList<>();

    public Partida() {}

    @PrePersist
    protected void onCreate() {
        this.dataPartida = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Participante getParticipante() { return participante; }
    public void setParticipante(Participante participante) { this.participante = participante; }
    public LocalDateTime getDataPartida() { return dataPartida; }
    public void setDataPartida(LocalDateTime dataPartida) { this.dataPartida = dataPartida; }
    public Integer getNivel() { return nivel; }
    public void setNivel(Integer nivel) { this.nivel = nivel; }
    public Integer getPontuacao() { return pontuacao; }
    public void setPontuacao(Integer pontuacao) { this.pontuacao = pontuacao; }
    public Integer getAcertos() { return acertos; }
    public void setAcertos(Integer acertos) { this.acertos = acertos; }
    public Integer getErros() { return erros; }
    public void setErros(Integer erros) { this.erros = erros; }
    public Double getTempoTotal() { return tempoTotal; }
    public void setTempoTotal(Double tempoTotal) { this.tempoTotal = tempoTotal; }
    public Double getTempoMedio() { return tempoMedio; }
    public void setTempoMedio(Double tempoMedio) { this.tempoMedio = tempoMedio; }
    public Double getNotaFinal() { return notaFinal; }
    public void setNotaFinal(Double notaFinal) { this.notaFinal = notaFinal; }
    public List<Resposta> getRespostas() { return respostas; }
    public void setRespostas(List<Resposta> respostas) { this.respostas = respostas; }
}
