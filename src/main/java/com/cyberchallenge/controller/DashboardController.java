package com.cyberchallenge.controller;

import com.cyberchallenge.dto.dashboard.DashboardDTO;
import com.cyberchallenge.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Dashboard analitico descrito na secao 12 do briefing. */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public ResponseEntity<DashboardDTO> obterDashboard() {
        return ResponseEntity.ok(dashboardService.gerarDashboard());
    }
}
