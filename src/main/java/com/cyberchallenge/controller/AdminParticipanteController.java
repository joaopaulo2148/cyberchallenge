package com.cyberchallenge.controller;

import com.cyberchallenge.dto.admin.ParticipanteAdminDTO;
import com.cyberchallenge.dto.admin.RespostaAdminDTO;
import com.cyberchallenge.service.ParticipanteAdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Area administrativa - visualizacao de participantes e respostas (secao 13). */
@RestController
@RequestMapping("/api/admin/participantes")
public class AdminParticipanteController {

    private final ParticipanteAdminService participanteAdminService;

    public AdminParticipanteController(ParticipanteAdminService participanteAdminService) {
        this.participanteAdminService = participanteAdminService;
    }

    @GetMapping
    public ResponseEntity<List<ParticipanteAdminDTO>> listarParticipantes() {
        return ResponseEntity.ok(participanteAdminService.listarParticipantes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParticipanteAdminDTO> buscarParticipante(@PathVariable Long id) {
        return ResponseEntity.ok(participanteAdminService.buscarParticipante(id));
    }

    @GetMapping("/{id}/respostas")
    public ResponseEntity<List<RespostaAdminDTO>> listarRespostas(@PathVariable Long id) {
        return ResponseEntity.ok(participanteAdminService.listarRespostasDoParticipante(id));
    }
}
