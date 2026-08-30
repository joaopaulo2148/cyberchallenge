package com.cyberchallenge.dto.dashboard;

public record IndicadoresGeraisDTO(
    long totalParticipantes,
    long totalPartidas,
    double mediaPontuacao,
    double mediaAcertos,
    double taxaGeralAcertoPercentual,
    double tempoMedioPartidasSegundos
) {}
