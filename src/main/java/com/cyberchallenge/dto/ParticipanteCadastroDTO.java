package com.cyberchallenge.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// Cadastro de conta: nickname, idade, autoavaliacao de 1 a 10 e senha. O
// nickname precisa ser unico -- ver ParticipanteService e ConflitoException.
// A forca da senha e verificada em PasswordValidator (mensagens de erro
// especificas por requisito nao atendido).
public record ParticipanteCadastroDTO(

    @NotBlank(message = "nickname e obrigatorio")
    @Size(min = 3, max = 30, message = "nickname deve ter entre 3 e 30 caracteres")
    String nickname,

    @NotNull(message = "idade e obrigatoria")
    @Min(value = 1, message = "idade deve ser maior que zero")
    @Max(value = 120, message = "idade informada parece invalida")
    Integer idade,

    @NotNull(message = "autoavaliacao e obrigatoria")
    @Min(value = 1, message = "autoavaliacao deve estar entre 1 e 10")
    @Max(value = 10, message = "autoavaliacao deve estar entre 1 e 10")
    Integer autoavaliacao,

    @NotBlank(message = "senha e obrigatoria")
    String senha
) {}
