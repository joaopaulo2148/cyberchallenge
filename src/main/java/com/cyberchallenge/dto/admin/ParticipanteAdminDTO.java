package com.cyberchallenge.dto.admin;

import com.cyberchallenge.model.Participante;

import java.time.LocalDateTime;

public record ParticipanteAdminDTO(
    Long id,
    String nome,
    LocalDateTime dataParticipacao,
    Integer pontuacao,
    Double tempoTotal,
    Double tempoMedio
) {
    public static ParticipanteAdminDTO fromEntity(Participante p) {
        return new ParticipanteAdminDTO(
            p.getId(), p.getNome(), p.getDataParticipacao(),
            p.getPontuacao(), p.getTempoTotal(), p.getTempoMedio()
        );
    }
}
