package com.cyberchallenge.dto;

import com.cyberchallenge.model.Partida;

// MELHORIA (ranking separado por nivel): cada RankingDTO ja pertence a uma
// unica chamada filtrada por nivel (ver PartidaRepository), entao nao
// existe risco de misturar niveis diferentes na mesma lista.
public record RankingDTO(
    String nickname,
    Integer pontuacao,
    Integer acertos,
    Integer nivel,
    Double percentualAcerto,
    Double tempoMedio
) {
    public static RankingDTO fromEntity(Partida p) {
        double percentual = (p.getAcertos() * 100.0) / 10.0;
        return new RankingDTO(
            p.getParticipante().getNickname(), p.getPontuacao(), p.getAcertos(),
            p.getNivel(), percentual, p.getTempoMedio()
        );
    }
}
