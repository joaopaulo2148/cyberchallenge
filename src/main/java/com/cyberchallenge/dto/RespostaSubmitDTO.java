package com.cyberchallenge.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

// BUG CORRIGIDO: nao havia nenhuma validacao aqui. Um payload malformado
// (perguntaId nulo, respostaEscolhida nula, tempo negativo) passava direto
// para a regra de negocio e so quebrava mais tarde, de forma confusa.
public record RespostaSubmitDTO(

    @NotNull(message = "perguntaId e obrigatorio")
    Long perguntaId,

    @NotNull(message = "respostaEscolhida e obrigatoria")
    Boolean respostaEscolhida,

    @NotNull(message = "tempoGasto e obrigatorio")
    @PositiveOrZero(message = "tempoGasto nao pode ser negativo")
    Double tempoGasto
) {}
