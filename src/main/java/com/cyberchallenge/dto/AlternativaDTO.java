package com.cyberchallenge.dto;

import com.cyberchallenge.model.Alternativa;

// Mesma logica de design documentada em PerguntaDTO: "correta" e exposta de
// proposito, pois o front-end avalia a resposta localmente (atividade
// supervisionada, sem round-trip por pergunta ao servidor).
public record AlternativaDTO(
    Long id,
    String texto,
    Boolean correta
) {
    public static AlternativaDTO fromEntity(Alternativa a) {
        return new AlternativaDTO(a.getId(), a.getTexto(), a.getCorreta());
    }
}
