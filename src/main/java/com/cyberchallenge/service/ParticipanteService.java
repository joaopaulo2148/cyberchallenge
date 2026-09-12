package com.cyberchallenge.service;

import com.cyberchallenge.dto.ParticipanteCadastroDTO;
import com.cyberchallenge.dto.ParticipanteDTO;
import com.cyberchallenge.exception.ConflitoException;
import com.cyberchallenge.model.Participante;
import com.cyberchallenge.repository.ParticipanteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ParticipanteService {

    private final ParticipanteRepository participanteRepository;

    public ParticipanteService(ParticipanteRepository participanteRepository) {
        this.participanteRepository = participanteRepository;
    }

    /**
     * Item 3 do briefing: nickname deve ser unico. A verificacao aqui e a
     * que realmente garante a regra (o front-end so da feedback rapido,
     * nunca decide sozinho) -- e a coluna "nickname" tambem tem uma
     * restricao UNIQUE no banco (item 6), como uma terceira camada de
     * protecao contra condicoes de corrida.
     */
    @Transactional
    public ParticipanteDTO cadastrar(ParticipanteCadastroDTO dto) {
        String nickname = dto.nickname().trim();

        if (participanteRepository.existsByNickname(nickname)) {
            throw new ConflitoException("Este nickname ja esta em uso. Escolha outro.");
        }

        Participante participante = new Participante();
        participante.setNickname(nickname);
        participante.setIdade(dto.idade());
        participante.setAutoavaliacao(dto.autoavaliacao());

        try {
            participante = participanteRepository.save(participante);
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            // Ultima linha de defesa: se duas requisicoes simultaneas
            // tentarem cadastrar o mesmo nickname ao mesmo tempo, a
            // restricao UNIQUE do banco rejeita a segunda, e ela cai aqui
            // em vez de gerar um erro 500 generico.
            throw new ConflitoException("Este nickname ja esta em uso. Escolha outro.");
        }

        return ParticipanteDTO.fromEntity(participante);
    }

    /** Usado pelo front-end para dar feedback imediato ao digitar o nickname. */
    public boolean nicknameDisponivel(String nickname) {
        if (nickname == null || nickname.isBlank()) {
            return false;
        }
        return !participanteRepository.existsByNickname(nickname.trim());
    }
}
