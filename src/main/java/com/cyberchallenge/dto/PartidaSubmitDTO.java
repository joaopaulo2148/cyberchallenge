package com.cyberchallenge.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

// MELHORIA (ajuste de banco de dados): nickname/idade/autoavaliacao nao
// vem mais inline aqui -- eles pertencem ao Participante, cadastrado uma
// vez via POST /api/participantes. Esta partida so referencia o
// participante ja existente pelo ID (participanteId), como uma FK de
// verdade, em vez de repetir os dados a cada partida.
public record PartidaSubmitDTO(

    @NotNull(message = "participanteId e obrigatorio")
    Long participanteId,

    @NotNull(message = "nivel e obrigatorio")
    @Min(value = 1, message = "nivel deve estar entre 1 e 4")
    @Max(value = 4, message = "nivel deve estar entre 1 e 4")
    Integer nivel,

    @NotEmpty(message = "respostas e obrigatorio")
    @Size(min = 10, max = 10, message = "a partida deve conter exatamente 10 respostas")
    @Valid
    List<RespostaSubmitDTO> respostas
) {}
