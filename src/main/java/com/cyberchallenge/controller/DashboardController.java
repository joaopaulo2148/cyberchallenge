package com.cyberchallenge.controller;

import com.cyberchallenge.dto.dashboard.DashboardDTO;
import com.cyberchallenge.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Estatisticas Gerais do Cyber Challenge (reformulacao, item 13).
 *
 * Publico, sem nenhuma restricao de acesso -- nao existe mais area
 * administrativa neste projeto.
 */
@RestController
@RequestMapping("/api/estatisticas")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public ResponseEntity<DashboardDTO> obterEstatisticas() {
        return ResponseEntity.ok(dashboardService.gerarDashboard());
    }
}
