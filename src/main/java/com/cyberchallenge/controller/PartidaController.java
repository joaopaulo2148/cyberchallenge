package com.cyberchallenge.controller;

import com.cyberchallenge.dto.PartidaSubmitDTO;
import com.cyberchallenge.dto.PerguntaDTO;
import com.cyberchallenge.dto.RankingDTO;
import com.cyberchallenge.service.PartidaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/partidas")
public class PartidaController {

    private final PartidaService partidaService;

    public PartidaController(PartidaService partidaService) {
        this.partidaService = partidaService;
    }

    // GET: /api/partidas/iniciar -> Retorna o JSON com 5 perguntas
    @GetMapping("/iniciar")
    public ResponseEntity<List<PerguntaDTO>> iniciarPartida() {
        List<PerguntaDTO> perguntas = partidaService.iniciarPartida();
        return ResponseEntity.ok(perguntas);
    }

    // POST: /api/partidas/finalizar -> Recebe o JSON com os dados jogados
    // BUG CORRIGIDO: faltava @Valid, entao o DTO validado (PartidaSubmitDTO)
    // nunca era realmente checado antes de chegar na regra de negocio.
    @PostMapping("/finalizar")
    public ResponseEntity<Void> finalizarPartida(@Valid @RequestBody PartidaSubmitDTO payload) {
        partidaService.processarFinalPartida(payload);
        return ResponseEntity.ok().build();
    }

    // GET: /api/partidas/ranking -> Retorna a lista do ranking atualizada
    @GetMapping("/ranking")
    public ResponseEntity<List<RankingDTO>> obterRanking() {
        List<RankingDTO> ranking = partidaService.obterRanking();
        return ResponseEntity.ok(ranking);
    }
}
