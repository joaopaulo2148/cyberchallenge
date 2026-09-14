package com.cyberchallenge.service;

import com.cyberchallenge.dto.LoginDTO;
import com.cyberchallenge.dto.ParticipanteCadastroDTO;
import com.cyberchallenge.dto.ParticipanteDTO;
import com.cyberchallenge.exception.ConflitoException;
import com.cyberchallenge.exception.CredenciaisInvalidasException;
import com.cyberchallenge.model.Participante;
import com.cyberchallenge.repository.ParticipanteRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ParticipanteService {

    private final ParticipanteRepository participanteRepository;
    private final PasswordValidator passwordValidator;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public ParticipanteService(ParticipanteRepository participanteRepository, PasswordValidator passwordValidator) {
        this.participanteRepository = participanteRepository;
        this.passwordValidator = passwordValidator;
    }

    /**
     * Cadastro de conta: nickname unico + senha forte, com o hash
     * calculado aqui (BCrypt) antes de qualquer persistencia -- a senha em
     * texto puro nunca chega perto do banco de dados.
     */
    @Transactional
    public ParticipanteDTO cadastrar(ParticipanteCadastroDTO dto) {
        String nickname = dto.nickname().trim();

        if (participanteRepository.existsByNickname(nickname)) {
            throw new ConflitoException("Este nickname ja esta em uso. Escolha outro.");
        }

        passwordValidator.validar(dto.senha());

        Participante participante = new Participante();
        participante.setNickname(nickname);
        participante.setIdade(dto.idade());
        participante.setAutoavaliacao(dto.autoavaliacao());
        participante.setSenhaHash(passwordEncoder.encode(dto.senha()));

        try {
            participante = participanteRepository.save(participante);
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            // Ultima linha de defesa: duas requisicoes simultaneas tentando
            // cadastrar o mesmo nickname ao mesmo tempo caem aqui, gracas a
            // restricao UNIQUE do banco, em vez de um erro 500 generico.
            throw new ConflitoException("Este nickname ja esta em uso. Escolha outro.");
        }

        return ParticipanteDTO.fromEntity(participante);
    }

    /**
     * Login simples por nickname + senha. Mensagem de erro
     * deliberadamente generica (item 7): nao revela se o problema foi o
     * nickname inexistente ou a senha incorreta, para nao ajudar alguem a
     * descobrir quais nicknames existem por tentativa e erro.
     */
    public ParticipanteDTO login(LoginDTO dto) {
        Participante participante = participanteRepository.findByNickname(dto.nickname().trim())
                .orElseThrow(() -> new CredenciaisInvalidasException("Nickname ou senha incorretos."));

        if (!passwordEncoder.matches(dto.senha(), participante.getSenhaHash())) {
            throw new CredenciaisInvalidasException("Nickname ou senha incorretos.");
        }

        return ParticipanteDTO.fromEntity(participante);
    }

    /** Usado pelo front-end para dar feedback imediato ao digitar o nickname no cadastro. */
    public boolean nicknameDisponivel(String nickname) {
        if (nickname == null || nickname.isBlank()) {
            return false;
        }
        return !participanteRepository.existsByNickname(nickname.trim());
    }
}
