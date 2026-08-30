package com.cyberchallenge.dto;

import com.cyberchallenge.model.Pergunta;

/**
 * DTO retornado em /api/partidas/iniciar.
 *
 * Observacao de design (nao e um bug, mas vale registrar): este DTO inclui
 * respostaCorreta e explicacao porque o front-end atual avalia a resposta
 * e mostra o feedback educativo localmente, sem round-trip ao servidor a
 * cada pergunta. Isso e aceitavel para uma atividade presencial e supervisionada
 * (o participante nao tem incentivo real para "inspecionar o JSON" durante
 * uma atividade de extensao). Se no futuro isso rodar publicamente na internet
 * e a pontuacao passar a ter valor competitivo, o ideal e criar um endpoint
 * POST /api/partidas/{id}/responder que avalia uma pergunta por vez no
 * servidor, sem nunca expor a resposta correta antes do envio.
 */
public record PerguntaDTO(
    Long id,
    String texto,
    String tema,
    Boolean respostaCorreta,
    String explicacao
) {
    public static PerguntaDTO fromEntity(Pergunta p) {
        return new PerguntaDTO(p.getId(), p.getTexto(), p.getTema(), p.getRespostaCorreta(), p.getExplicacao());
    }
}
