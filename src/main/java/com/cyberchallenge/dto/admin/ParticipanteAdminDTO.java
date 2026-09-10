package com.cyberchallenge.dto.admin;

import com.cyberchallenge.model.Participante;

import java.time.LocalDateTime;

public record ParticipanteAdminDTO(
    Long id,
    String nome,
    Integer idade,
    Integer autoavaliacao,
    Integer nivel,
    LocalDateTime dataParticipacao,
    Integer pontuacao,
    Double tempoTotal,
    Double tempoMedio,
    Double notaFinal
) {
    public static ParticipanteAdminDTO fromEntity(Participante p) {
        return new ParticipanteAdminDTO(
            p.getId(), p.getNome(), p.getIdade(), p.getAutoavaliacao(), p.getNivel(), p.getDataParticipacao(),
            p.getPontuacao(), p.getTempoTotal(), p.getTempoMedio(), p.getNotaFinal()
        );
    }
}
