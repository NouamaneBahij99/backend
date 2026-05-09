package com.magentatechno.pelican.controller;

import com.magentatechno.pelican.dto.CourrierDto;
import com.magentatechno.pelican.dto.WorkflowDto;
import com.magentatechno.pelican.service.CourrierService;
import com.magentatechno.pelican.service.PdfService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/courriers")
@RequiredArgsConstructor
public class CourrierController {

    private final CourrierService courrierService;
    private final PdfService pdfService;

    @PostMapping(consumes = "multipart/form-data")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    public ResponseEntity<CourrierDto.Response> create(
            @Valid @RequestPart("courrier") CourrierDto.CreateRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        return ResponseEntity.ok(courrierService.create(request, file));
    }

    @GetMapping
    public ResponseEntity<Page<CourrierDto.Response>> findAll(
            Pageable pageable,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String statut) {
        return ResponseEntity.ok(courrierService.findAll(pageable, search, type, statut));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourrierDto.Response> findById(@PathVariable Long id) {
        return ResponseEntity.ok(courrierService.findById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    public ResponseEntity<CourrierDto.Response> update(
            @PathVariable Long id,
            @Valid @RequestBody CourrierDto.UpdateRequest request) {
        return ResponseEntity.ok(courrierService.update(id, request));
    }

    @GetMapping("/{id}/circuit")
    public ResponseEntity<List<WorkflowDto.CircuitCourrierDto>> getCircuit(@PathVariable Long id) {
        return ResponseEntity.ok(courrierService.getCircuit(id));
    }

    @PutMapping("/{id}/affecter/{userId}")
    @PreAuthorize("hasAnyRole('AGENT', 'CHEF_SERVICE', 'ADMIN')")
    public ResponseEntity<CourrierDto.Response> affecter(
            @PathVariable Long id, @PathVariable Long userId) {
        return ResponseEntity.ok(courrierService.affecter(id, userId));
    }

    @PutMapping("/{id}/transferer/{userId}")
    @PreAuthorize("hasAnyRole('AGENT', 'CHEF_SERVICE', 'ADMIN', 'DIRECTEUR')")
    public ResponseEntity<CourrierDto.Response> transferer(
            @PathVariable Long id,
            @PathVariable Long userId,
            @RequestParam(required = false) String commentaire) {
        return ResponseEntity.ok(courrierService.transferer(id, userId, commentaire));
    }

    @PutMapping("/{id}/valider")
    @PreAuthorize("hasAnyRole('CHEF_SERVICE', 'DIRECTEUR', 'ADMIN')")
    public ResponseEntity<CourrierDto.Response> valider(
            @PathVariable Long id,
            @RequestParam(required = false) String commentaire) {
        return ResponseEntity.ok(courrierService.valider(id, commentaire));
    }

    @PutMapping("/{id}/rejeter")
    @PreAuthorize("hasAnyRole('CHEF_SERVICE', 'DIRECTEUR', 'ADMIN')")
    public ResponseEntity<CourrierDto.Response> rejeter(
            @PathVariable Long id,
            @RequestParam String motif) {
        return ResponseEntity.ok(courrierService.rejeter(id, motif));
    }

    @PutMapping("/{id}/archiver")
    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF_SERVICE')")
    public ResponseEntity<CourrierDto.Response> archiver(@PathVariable Long id) {
        return ResponseEntity.ok(courrierService.archiver(id));
    }


    @GetMapping("/{id}/pdf")
    public ResponseEntity<ByteArrayResource> generatePdf(@PathVariable Long id) {
        byte[] pdfBytes = pdfService.generateCourrierPdf(id);
        ByteArrayResource resource = new ByteArrayResource(pdfBytes);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"courrier-" + id + ".pdf\"")
                .contentLength(pdfBytes.length)
                .body(resource);
    }
}
