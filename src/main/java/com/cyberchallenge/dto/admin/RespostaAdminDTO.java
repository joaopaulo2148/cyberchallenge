package com.cyberchallenge.dto.admin;

import com.cyberchallenge.model.Resposta;

import java.time.LocalDateTime;

public record RespostaAdminDTO(
    Long id,
    Long perguntaId,
    String perguntaTexto,
    String tema,
    Boolean respostaEscolhida,
    Boolean correta,
    Double tempoResposta,
    LocalDateTime dataResposta
) {
    public static RespostaAdminDTO fromEntity(Resposta r) {
        return new RespostaAdminDTO(
            r.getId(),
            r.getPergunta().getId(),
            r.getPergunta().getTexto(),
            r.getPergunta().getTema(),
            r.getResposta(),
            r.getCorreta(),
            r.getTempoResposta(),
            r.getDataResposta()
        );
    }
}
