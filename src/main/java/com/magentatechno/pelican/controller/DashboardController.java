package com.magentatechno.pelican.controller;

import com.magentatechno.pelican.dto.DashboardDto;
import com.magentatechno.pelican.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/statistics")
    public ResponseEntity<DashboardDto.Statistics> getStatistics() {
        return ResponseEntity.ok(dashboardService.getStatistics());
    }
}
