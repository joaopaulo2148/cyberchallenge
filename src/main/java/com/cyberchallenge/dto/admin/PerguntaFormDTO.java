package com.cyberchallenge.dto.admin;

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

    Boolean ativa
) {}
