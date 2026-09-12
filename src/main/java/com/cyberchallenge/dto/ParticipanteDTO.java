package com.cyberchallenge.dto;

import com.cyberchallenge.model.Participante;

public record ParticipanteDTO(
    Long id,
    String nickname,
    Integer idade,
    Integer autoavaliacao
) {
    public static ParticipanteDTO fromEntity(Participante p) {
        return new ParticipanteDTO(p.getId(), p.getNickname(), p.getIdade(), p.getAutoavaliacao());
    }
}
