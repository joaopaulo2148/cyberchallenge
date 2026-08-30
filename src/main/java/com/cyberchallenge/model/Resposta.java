package com.cyberchallenge.model; // BUG CORRIGIDO: pacote estava "cyberchallenge.model" (faltava o "com.")

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "respostas")
public class Resposta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relacionamento N para 1: Varias respostas pertencem a um Participante
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participante_id", nullable = false)
    private Participante participante;

    // Relacionamento N para 1: Varias respostas referenciam uma Pergunta
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pergunta_id", nullable = false)
    private Pergunta pergunta;

    @Column(nullable = false)
    private Boolean resposta;

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
    public Participante getParticipante() { return participante; }
    public void setParticipante(Participante participante) { this.participante = participante; }
    public Pergunta getPergunta() { return pergunta; }
    public void setPergunta(Pergunta pergunta) { this.pergunta = pergunta; }
    public Boolean getResposta() { return resposta; }
    public void setResposta(Boolean resposta) { this.resposta = resposta; }
    public Boolean getCorreta() { return correta; }
    public void setCorreta(Boolean correta) { this.correta = correta; }
    public Double getTempoResposta() { return tempoResposta; }
    public void setTempoResposta(Double tempoResposta) { this.tempoResposta = tempoResposta; }
    public LocalDateTime getDataResposta() { return dataResposta; }
    public void setDataResposta(LocalDateTime dataResposta) { this.dataResposta = dataResposta; }
}
