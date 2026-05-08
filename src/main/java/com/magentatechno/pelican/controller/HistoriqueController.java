package com.magentatechno.pelican.controller;

import com.magentatechno.pelican.dto.response.HistoriqueCourrierResponse;
import com.magentatechno.pelican.repository.HistoriqueCourrierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/courriers")
@RequiredArgsConstructor
public class HistoriqueController {

    private final HistoriqueCourrierRepository historiqueRepository;

    @GetMapping("/{id}/historique")
    public ResponseEntity<List<HistoriqueCourrierResponse>> getHistorique(
            @PathVariable Long id
    ) {
        List<HistoriqueCourrierResponse> result =
                historiqueRepository.findByCourrierIdOrderByDateDesc(id)
                        .stream()
                        .map(HistoriqueCourrierResponse::fromEntity)
                        .toList();

        return ResponseEntity.ok(result);
    }
}
