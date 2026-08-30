package com.cyberchallenge.controller;

import com.cyberchallenge.dto.admin.PerguntaAdminDTO;
import com.cyberchallenge.dto.admin.PerguntaFormDTO;
import com.cyberchallenge.service.PerguntaAdminService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Area administrativa - CRUD do banco de perguntas (secao 13 do briefing).
 * Em um cenario real, isso ficaria atras de autenticacao (Spring Security);
 * como o projeto e para uma atividade extensionista controlada, isso fica
 * como proximo passo recomendado e nao foi adicionado aqui para nao mudar
 * o escopo sem combinar antes.
 */
@RestController
@RequestMapping("/api/admin/perguntas")
public class AdminPerguntaController {

    private final PerguntaAdminService perguntaAdminService;

    public AdminPerguntaController(PerguntaAdminService perguntaAdminService) {
        this.perguntaAdminService = perguntaAdminService;
    }

    @GetMapping
    public ResponseEntity<List<PerguntaAdminDTO>> listarTodas() {
        return ResponseEntity.ok(perguntaAdminService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PerguntaAdminDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(perguntaAdminService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<PerguntaAdminDTO> criar(@Valid @RequestBody PerguntaFormDTO form) {
        return ResponseEntity.status(201).body(perguntaAdminService.criar(form));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PerguntaAdminDTO> atualizar(@PathVariable Long id, @Valid @RequestBody PerguntaFormDTO form) {
        return ResponseEntity.ok(perguntaAdminService.atualizar(id, form));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        perguntaAdminService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    // PATCH /api/admin/perguntas/5/status  { "ativa": false }
    @PatchMapping("/{id}/status")
    public ResponseEntity<PerguntaAdminDTO> alterarStatus(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        Boolean ativa = body.get("ativa");
        if (ativa == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(perguntaAdminService.alterarStatus(id, ativa));
    }
}
