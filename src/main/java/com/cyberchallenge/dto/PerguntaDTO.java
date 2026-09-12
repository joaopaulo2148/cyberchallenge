package com.cyberchallenge.dto;

import com.cyberchallenge.model.Pergunta;

import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO retornado em /api/partidas/iniciar.
 *
 * Observacao de design (nao e um bug, mas vale registrar): este DTO inclui
 * respostaCorreta/alternativas.correta e explicacao porque o front-end
 * avalia a resposta e mostra o feedback educativo localmente, sem
 * round-trip ao servidor a cada pergunta. Isso e aceitavel para uma
 * atividade presencial e supervisionada. Se no futuro isso rodar
 * publicamente com pontuacao competitiva, o ideal e criar um endpoint que
 * avalia uma pergunta por vez no servidor, sem expor a resposta certa
 * antes do envio.
 *
 * "tipo" indica se a pergunta e VERDADEIRO_FALSO (usa respostaCorreta) ou
 * MULTIPLA_ESCOLHA (usa a lista "alternativas", com respostaCorreta nulo).
 */
public record PerguntaDTO(
    Long id,
    String texto,
    String tema,
    String tipo,
    Boolean respostaCorreta,
    List<AlternativaDTO> alternativas,
    String explicacao,
    Integer nivel
) {
    public static PerguntaDTO fromEntity(Pergunta p) {
        List<AlternativaDTO> alternativas = p.getAlternativas().stream()
                .map(AlternativaDTO::fromEntity)
                .collect(Collectors.toList());
        return new PerguntaDTO(
            p.getId(), p.getTexto(), p.getTema(), p.getTipo().name(),
            p.getRespostaCorreta(), alternativas, p.getExplicacao(), p.getNivel()
        );
    }
}
