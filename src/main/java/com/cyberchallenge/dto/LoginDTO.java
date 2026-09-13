package com.cyberchallenge.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginDTO(

    @NotBlank(message = "nickname e obrigatorio")
    String nickname,

    @NotBlank(message = "senha e obrigatoria")
    String senha
) {}
