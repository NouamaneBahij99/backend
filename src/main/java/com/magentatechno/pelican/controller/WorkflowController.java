package com.magentatechno.pelican.controller;

import com.magentatechno.pelican.dto.WorkflowDto;
import com.magentatechno.pelican.service.WorkflowService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/workflows")
@RequiredArgsConstructor
public class WorkflowController {

    private final WorkflowService workflowService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WorkflowDto.Response> create(
            @Valid @RequestBody WorkflowDto.CreateRequest request) {
        return ResponseEntity.ok(workflowService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<WorkflowDto.Response>> findAll() {
        return ResponseEntity.ok(workflowService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkflowDto.Response> findById(@PathVariable Long id) {
        return ResponseEntity.ok(workflowService.findById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WorkflowDto.Response> update(
            @PathVariable Long id,
            @RequestBody WorkflowDto.UpdateRequest request) {
        return ResponseEntity.ok(workflowService.update(id, request));
    }

    @PostMapping("/{id}/etapes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WorkflowDto.Response> ajouterEtape(
            @PathVariable Long id,
            @Valid @RequestBody WorkflowDto.EtapeRequest request) {
        return ResponseEntity.ok(workflowService.ajouterEtape(id, request));
    }

    @DeleteMapping("/{id}/etapes/{etapeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WorkflowDto.Response> supprimerEtape(
            @PathVariable Long id,
            @PathVariable Long etapeId) {
        return ResponseEntity.ok(workflowService.supprimerEtape(id, etapeId));
    }

    @PutMapping("/{id}/etapes/reorganiser")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WorkflowDto.Response> reorganiser(
            @PathVariable Long id,
            @RequestBody List<Long> etapeIds) {
        return ResponseEntity.ok(workflowService.reorganiserEtapes(id, etapeIds));
    }
}
