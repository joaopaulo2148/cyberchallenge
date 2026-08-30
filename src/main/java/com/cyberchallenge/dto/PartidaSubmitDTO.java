package com.cyberchallenge.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

// BUG CORRIGIDO: nao havia validacao de nome vazio nem da quantidade de
// respostas (regra 6/16 do briefing: a partida tem exatamente 5 perguntas).
public record PartidaSubmitDTO(

    @NotBlank(message = "nomeParticipante e obrigatorio")
    String nomeParticipante,

    @NotEmpty(message = "respostas e obrigatorio")
    @Size(min = 5, max = 5, message = "a partida deve conter exatamente 5 respostas")
    @Valid
    List<RespostaSubmitDTO> respostas
) {}
