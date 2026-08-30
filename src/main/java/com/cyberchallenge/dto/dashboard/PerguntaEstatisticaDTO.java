package com.cyberchallenge.dto.dashboard;

public record PerguntaEstatisticaDTO(
    Long perguntaId,
    String texto,
    String tema,
    long totalRespostas,
    long acertos,
    long erros,
    double percentualAcerto,
    double percentualErro,
    long quantidadeVerdadeiro,
    long quantidadeFalso,
    double tempoMedioSegundos
) {}
