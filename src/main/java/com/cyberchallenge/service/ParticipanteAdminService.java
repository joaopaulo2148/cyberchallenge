package com.cyberchallenge.service;

import com.cyberchallenge.dto.admin.ParticipanteAdminDTO;
import com.cyberchallenge.dto.admin.RespostaAdminDTO;
import com.cyberchallenge.exception.RecursoNaoEncontradoException;
import com.cyberchallenge.model.Participante;
import com.cyberchallenge.repository.ParticipanteRepository;
import com.cyberchallenge.repository.RespostaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ParticipanteAdminService {

    private final ParticipanteRepository participanteRepository;
    private final RespostaRepository respostaRepository;

    public ParticipanteAdminService(ParticipanteRepository participanteRepository, RespostaRepository respostaRepository) {
        this.participanteRepository = participanteRepository;
        this.respostaRepository = respostaRepository;
    }

    public List<ParticipanteAdminDTO> listarParticipantes() {
        return participanteRepository.findAll().stream()
                .map(ParticipanteAdminDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public ParticipanteAdminDTO buscarParticipante(Long id) {
        return ParticipanteAdminDTO.fromEntity(buscarEntidade(id));
    }

    public List<RespostaAdminDTO> listarRespostasDoParticipante(Long participanteId) {
        buscarEntidade(participanteId); // valida que o participante existe
        return respostaRepository.findByParticipanteId(participanteId).stream()
                .map(RespostaAdminDTO::fromEntity)
                .collect(Collectors.toList());
    }

    private Participante buscarEntidade(Long id) {
        return participanteRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Participante nao encontrado: " + id));
    }
}
