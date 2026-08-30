package com.cyberchallenge.dto.admin;

import com.cyberchallenge.model.Pergunta;

/** Representacao completa de uma pergunta, usada apenas na area administrativa. */
public record PerguntaAdminDTO(
    Long id,
    String texto,
    String tema,
    Boolean respostaCorreta,
    String explicacao,
    Boolean ativa
) {
    public static PerguntaAdminDTO fromEntity(Pergunta p) {
        return new PerguntaAdminDTO(p.getId(), p.getTexto(), p.getTema(), p.getRespostaCorreta(), p.getExplicacao(), p.getAtiva());
    }
}
