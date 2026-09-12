package com.cyberchallenge.model;

/**
 * Formato da pergunta (item 8 da reformulacao: o jogo deixou de ser
 * exclusivamente Verdadeiro/Falso).
 *
 * VERDADEIRO_FALSO: usa o campo Pergunta.respostaCorreta; a lista de
 * alternativas fica vazia.
 * MULTIPLA_ESCOLHA: usa a lista Pergunta.alternativas (cada uma com seu
 * proprio texto e uma flag "correta"); Pergunta.respostaCorreta fica nulo.
 */
public enum TipoPergunta {
    VERDADEIRO_FALSO,
    MULTIPLA_ESCOLHA
}
