package com.cyberchallenge.controller;

import com.cyberchallenge.dto.ParticipanteCadastroDTO;
import com.cyberchallenge.dto.ParticipanteDTO;
import com.cyberchallenge.service.ParticipanteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/participantes")
public class ParticipanteController {

    private final ParticipanteService participanteService;

    public ParticipanteController(ParticipanteService participanteService) {
        this.participanteService = participanteService;
    }

    // POST /api/participantes -> cadastra nickname/idade/autoavaliacao
    // antes de iniciar a partida (secao 3). Retorna 409 se o nickname ja
    // estiver em uso (ver GlobalExceptionHandler/ConflitoException).
    @PostMapping
    public ResponseEntity<ParticipanteDTO> cadastrar(@Valid @RequestBody ParticipanteCadastroDTO dto) {
        ParticipanteDTO participante = participanteService.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(participante);
    }

    // GET /api/participantes/disponibilidade?nickname=xxx -> feedback
    // rapido no front-end, digitando o nickname (nao substitui a validacao
    // feita em POST /api/participantes).
    @GetMapping("/disponibilidade")
    public ResponseEntity<Map<String, Boolean>> verificarDisponibilidade(@RequestParam String nickname) {
        boolean disponivel = participanteService.nicknameDisponivel(nickname);
        return ResponseEntity.ok(Map.of("disponivel", disponivel));
    }
}
