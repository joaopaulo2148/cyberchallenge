package com.cyberchallenge.dto.dashboard;

public record IndicadoresGeraisDTO(
    long totalParticipantes,
    long totalPartidas,
    double mediaPontuacao,
    double mediaAcertos,
    double taxaGeralAcertoPercentual,
    double tempoMedioPartidasSegundos,
    // MELHORIA (reformulacao, item 13): media da autoavaliacao (1-10)
    // informada antes da partida e media da nota final (0-10) realmente
    // obtida -- permite comparar percepcao x desempenho real.
    double mediaAutoavaliacao,
    double mediaNotaFinal
) {}
