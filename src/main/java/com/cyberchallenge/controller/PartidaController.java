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
    // -> Retorna o JSON com 10 perguntas do nivel escolhido (secoes 1 e 2).
    // "excluir" e opcional: lista de IDs de perguntas usadas na partida
    // anterior do mesmo nivel, para dar mais variedade ao jogar novamente.
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
