package com.cyberchallenge.dto.dashboard;

public record TemaEstatisticaDTO(
    String tema,
    long totalRespostas,
    long acertos,
    double percentualAcerto
) {}
