package com.cyberchallenge.dto.admin;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** Corpo de entrada para POST/PUT de pergunta na area administrativa. */
public record PerguntaFormDTO(

    @NotBlank(message = "texto e obrigatorio")
    String texto,

    @NotBlank(message = "tema e obrigatorio")
    String tema,

    @NotNull(message = "respostaCorreta e obrigatoria")
    Boolean respostaCorreta,

    @NotBlank(message = "explicacao e obrigatoria")
    String explicacao,

    Boolean ativa,

    // MELHORIA (secao 2): nivel de dificuldade da pergunta (1 a 4)
    @NotNull(message = "nivel e obrigatorio")
    @Min(value = 1, message = "nivel deve estar entre 1 e 4")
    @Max(value = 4, message = "nivel deve estar entre 1 e 4")
    Integer nivel
) {}
