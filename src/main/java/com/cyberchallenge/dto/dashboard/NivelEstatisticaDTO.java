package com.cyberchallenge.dto.dashboard;

// MELHORIA (reformulacao, item 13): distribuicao e desempenho por nivel de dificuldade.
public record NivelEstatisticaDTO(
    Integer nivel,
    String nomeNivel,
    long totalPartidas,
    double mediaPontuacao,
    double mediaNotaFinal,
    double percentualAcerto
) {}
