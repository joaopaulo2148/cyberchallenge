package com.cyberchallenge.dto;

import com.cyberchallenge.model.Participante;

public record RankingDTO(
    String nome,
    Integer pontuacao,
    Double tempoTotal,
    Double tempoMedio,
    Double notaFinal,
    Integer nivel
) {
    public static RankingDTO fromEntity(Participante p) {
        return new RankingDTO(p.getNome(), p.getPontuacao(), p.getTempoTotal(), p.getTempoMedio(), p.getNotaFinal(), p.getNivel());
    }
}
