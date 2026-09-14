package com.cyberchallenge.controller;

import com.cyberchallenge.dto.PartidaSubmitDTO;
import com.cyberchallenge.dto.PerguntaDTO;
import com.cyberchallenge.dto.RankingDTO;
import com.cyberchallenge.exception.RegraDeNegocioException;
import com.cyberchallenge.service.PartidaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/partidas")
public class PartidaController {

    private final PartidaService partidaService;

    public PartidaController(PartidaService partidaService) {
        this.partidaService = partidaService;
    }

    // GET: /api/partidas/iniciar?nivel=1&excluir=10,11,12
    // -> Retorna o JSON com 10 perguntas do nivel escolhido.
    @GetMapping("/iniciar")
    public ResponseEntity<List<PerguntaDTO>> iniciarPartida(
            @RequestParam Integer nivel,
            @RequestParam(required = false) List<Long> excluir) {
        if (nivel == null) {
            throw new RegraDeNegocioException("O parametro 'nivel' e obrigatorio (valores validos: 1 a 4).");
        }
        List<Long> idsExcluidos = excluir == null ? Collections.emptyList() : excluir;
        List<PerguntaDTO> perguntas = partidaService.iniciarPartida(nivel, idsExcluidos);
        return ResponseEntity.ok(perguntas);
    }

    // POST: /api/partidas/finalizar -> Recebe o JSON com os dados jogados
    @PostMapping("/finalizar")
    public ResponseEntity<Void> finalizarPartida(@Valid @RequestBody PartidaSubmitDTO payload) {
        partidaService.processarFinalPartida(payload);
        return ResponseEntity.ok().build();
    }

    // GET: /api/partidas/ranking?nivel=1 -> ranking de um unico nivel por vez
    // (item 2 da reformulacao: nunca misturar niveis diferentes).
    @GetMapping("/ranking")
    public ResponseEntity<List<RankingDTO>> obterRanking(@RequestParam Integer nivel) {
        if (nivel == null) {
            throw new RegraDeNegocioException("O parametro 'nivel' e obrigatorio (valores validos: 1 a 4).");
        }
        List<RankingDTO> ranking = partidaService.obterRanking(nivel);
        return ResponseEntity.ok(ranking);
    }
}
