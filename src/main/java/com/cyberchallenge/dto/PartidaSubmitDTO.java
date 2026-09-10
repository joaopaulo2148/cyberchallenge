package com.cyberchallenge.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

// BUG CORRIGIDO: nao havia validacao de nome vazio nem da quantidade de
// respostas. MELHORIA: a partida agora tem exatamente 10 perguntas (secao 1)
// e o cadastro do participante passou a incluir idade, autoavaliacao de
// conhecimento (1 a 10) e o nivel de dificuldade escolhido (secoes 2 e 3).
public record PartidaSubmitDTO(

    @NotBlank(message = "nomeParticipante e obrigatorio")
    String nomeParticipante,

    @NotNull(message = "idade e obrigatoria")
    @Min(value = 1, message = "idade deve ser maior que zero")
    @Max(value = 120, message = "idade informada parece invalida")
    Integer idade,

    @NotNull(message = "autoavaliacao e obrigatoria")
    @Min(value = 1, message = "autoavaliacao deve estar entre 1 e 10")
    @Max(value = 10, message = "autoavaliacao deve estar entre 1 e 10")
    Integer autoavaliacao,

    @NotNull(message = "nivel e obrigatorio")
    @Min(value = 1, message = "nivel deve estar entre 1 e 4")
    @Max(value = 4, message = "nivel deve estar entre 1 e 4")
    Integer nivel,

    @NotEmpty(message = "respostas e obrigatorio")
    @Size(min = 10, max = 10, message = "a partida deve conter exatamente 10 respostas")
    @Valid
    List<RespostaSubmitDTO> respostas
) {}
