package com.cyberchallenge.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

// MELHORIA (reformulacao, item 8): agora aceita tanto perguntas
// Verdadeiro/Falso (respostaEscolhida) quanto de multipla escolha
// (alternativaEscolhidaId). Exatamente um dos dois deve vir preenchido,
// dependendo do tipo da pergunta -- essa validacao condicional e feita em
// PartidaService (nao da para expressar com anotacoes simples de bean
// validation).
public record RespostaSubmitDTO(

    @NotNull(message = "perguntaId e obrigatorio")
    Long perguntaId,

    Boolean respostaEscolhida,

    Long alternativaEscolhidaId,

    @NotNull(message = "tempoGasto e obrigatorio")
    @PositiveOrZero(message = "tempoGasto nao pode ser negativo")
    Double tempoGasto
) {}
