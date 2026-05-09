// ===== OrganisationController.java =====
package com.magentatechno.pelican.controller;

import com.magentatechno.pelican.dto.OrganisationDto;
import com.magentatechno.pelican.service.NoeudOrganisationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/organisation")
@RequiredArgsConstructor
public class OrganisationController {

    private final NoeudOrganisationService noeudService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrganisationDto.Response> create(
            @Valid @RequestBody OrganisationDto.CreateRequest request) {
        return ResponseEntity.ok(noeudService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<OrganisationDto.Response>> findAll() {
        return ResponseEntity.ok(noeudService.findAll());
    }

    @GetMapping("/racines")
    public ResponseEntity<List<OrganisationDto.Response>> findRacines() {
        return ResponseEntity.ok(noeudService.findRacines());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrganisationDto.Response> findById(@PathVariable Long id) {
        return ResponseEntity.ok(noeudService.findById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrganisationDto.Response> update(
            @PathVariable Long id,
            @RequestBody OrganisationDto.UpdateRequest request) {
        return ResponseEntity.ok(noeudService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        noeudService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
