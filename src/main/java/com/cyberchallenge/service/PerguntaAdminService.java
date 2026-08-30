package com.cyberchallenge.service;

import com.cyberchallenge.dto.admin.PerguntaAdminDTO;
import com.cyberchallenge.dto.admin.PerguntaFormDTO;
import com.cyberchallenge.exception.RecursoNaoEncontradoException;
import com.cyberchallenge.model.Pergunta;
import com.cyberchallenge.repository.PerguntaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PerguntaAdminService {

    private final PerguntaRepository perguntaRepository;

    public PerguntaAdminService(PerguntaRepository perguntaRepository) {
        this.perguntaRepository = perguntaRepository;
    }

    public List<PerguntaAdminDTO> listarTodas() {
        return perguntaRepository.findAll().stream()
                .map(PerguntaAdminDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public PerguntaAdminDTO buscarPorId(Long id) {
        return PerguntaAdminDTO.fromEntity(buscarEntidade(id));
    }

    @Transactional
    public PerguntaAdminDTO criar(PerguntaFormDTO form) {
        Pergunta pergunta = new Pergunta();
        aplicarForm(pergunta, form);
        return PerguntaAdminDTO.fromEntity(perguntaRepository.save(pergunta));
    }

    @Transactional
    public PerguntaAdminDTO atualizar(Long id, PerguntaFormDTO form) {
        Pergunta pergunta = buscarEntidade(id);
        aplicarForm(pergunta, form);
        return PerguntaAdminDTO.fromEntity(perguntaRepository.save(pergunta));
    }

    @Transactional
    public void excluir(Long id) {
        if (!perguntaRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Pergunta nao encontrada: " + id);
        }
        perguntaRepository.deleteById(id);
    }

    @Transactional
    public PerguntaAdminDTO alterarStatus(Long id, boolean ativa) {
        Pergunta pergunta = buscarEntidade(id);
        pergunta.setAtiva(ativa);
        return PerguntaAdminDTO.fromEntity(perguntaRepository.save(pergunta));
    }

    private Pergunta buscarEntidade(Long id) {
        return perguntaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pergunta nao encontrada: " + id));
    }

    private void aplicarForm(Pergunta pergunta, PerguntaFormDTO form) {
        pergunta.setTexto(form.texto());
        pergunta.setTema(form.tema());
        pergunta.setRespostaCorreta(form.respostaCorreta());
        pergunta.setExplicacao(form.explicacao());
        pergunta.setAtiva(form.ativa() == null ? true : form.ativa());
    }
}
