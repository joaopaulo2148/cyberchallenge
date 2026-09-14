package com.cyberchallenge.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "respostas")
public class Resposta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relacionamento N para 1: Varias respostas pertencem a uma Partida
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partida_id", nullable = false)
    private Partida partida;

    // Relacionamento N para 1: Varias respostas referenciam uma Pergunta
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pergunta_id", nullable = false)
    private Pergunta pergunta;

    // Usado apenas quando a pergunta e do tipo VERDADEIRO_FALSO.
    private Boolean resposta;

    // MELHORIA (reformulacao, item 8): usado apenas quando a pergunta e do
    // tipo MULTIPLA_ESCOLHA -- guarda qual alternativa foi escolhida.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alternativa_escolhida_id")
    private Alternativa alternativaEscolhida;

    @Column(nullable = false)
    private Boolean correta;

    @Column(name = "tempo_resposta", nullable = false)
    private Double tempoResposta;

    @Column(name = "data_resposta", nullable = false)
    private LocalDateTime dataResposta;

    public Resposta() {}

    @PrePersist
    protected void onCreate() {
        this.dataResposta = LocalDateTime.now();
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Partida getPartida() { return partida; }
    public void setPartida(Partida partida) { this.partida = partida; }
    public Pergunta getPergunta() { return pergunta; }
    public void setPergunta(Pergunta pergunta) { this.pergunta = pergunta; }
    public Boolean getResposta() { return resposta; }
    public void setResposta(Boolean resposta) { this.resposta = resposta; }
    public Alternativa getAlternativaEscolhida() { return alternativaEscolhida; }
    public void setAlternativaEscolhida(Alternativa alternativaEscolhida) { this.alternativaEscolhida = alternativaEscolhida; }
    public Boolean getCorreta() { return correta; }
    public void setCorreta(Boolean correta) { this.correta = correta; }
    public Double getTempoResposta() { return tempoResposta; }
    public void setTempoResposta(Double tempoResposta) { this.tempoResposta = tempoResposta; }
    public LocalDateTime getDataResposta() { return dataResposta; }
    public void setDataResposta(LocalDateTime dataResposta) { this.dataResposta = dataResposta; }
}
