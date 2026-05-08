package com.magentatechno.pelican.controller;

import com.magentatechno.pelican.service.BackupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/backup")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BackupController {

    private final BackupService backupService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> createBackup() {
        Map<String, String> response = new HashMap<>();
        try {
            String backupFile = backupService.createBackup();
            response.put("status", "success");
            response.put("message", "Sauvegarde créée avec succès");
            response.put("file", backupFile);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Erreur: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
