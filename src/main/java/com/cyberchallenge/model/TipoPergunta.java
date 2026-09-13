package com.cyberchallenge.model;

/**
 * Formato da pergunta.
 *
 * VERDADEIRO_FALSO: usa o campo Pergunta.respostaCorreta; a lista de
 * alternativas fica vazia.
 *
 * MULTIPLA_ESCOLHA e COMPLETAR_FRASE: ambas usam a lista
 * Pergunta.alternativas (cada uma com seu proprio texto e uma flag
 * "correta"); Pergunta.respostaCorreta fica nulo. A diferenca entre as
 * duas e apenas de apresentacao no front-end -- em "completar a frase" o
 * enunciado tem uma lacuna e as alternativas sao as opcoes para preenche-la,
 * mas tecnicamente o participante sempre escolhe uma alternativa (nunca
 * digita texto livre), entao o backend avalia as duas exatamente da mesma
 * forma (ver PartidaService.avaliarResposta).
 */
public enum TipoPergunta {
    VERDADEIRO_FALSO,
    MULTIPLA_ESCOLHA,
    COMPLETAR_FRASE
}
