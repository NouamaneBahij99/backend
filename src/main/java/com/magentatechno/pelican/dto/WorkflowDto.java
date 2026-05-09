// ===== WorkflowDto.java =====
package com.magentatechno.pelican.dto;

import com.magentatechno.pelican.entity.Courrier;
import com.magentatechno.pelican.entity.EtapeCourrier;
import com.magentatechno.pelican.entity.Role;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public class WorkflowDto {

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class CreateRequest {
        @NotBlank(message = "Le nom est requis")
        private String nom;
        private String description;
        private String typeCourrier;
        private boolean defaut;
        private List<EtapeRequest> etapes;
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class EtapeRequest {
        @NotBlank(message = "Le nom est requis")
        private String nom;
        private Integer ordre;
        private Long noeudId;
        private String roleRequis;
        private String description;
        private boolean obligatoire;
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class UpdateRequest {
        private String nom;
        private String description;
        private String typeCourrier;
        private boolean defaut;
        private boolean actif;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Response {
        private Long id;
        private String nom;
        private String description;
        private Courrier.TypeCourrier typeCourrier;
        private boolean actif;
        private boolean defaut;
        private List<EtapeResponse> etapes;
        private LocalDateTime createdAt;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class EtapeResponse {
        private Long id;
        private String nom;
        private Integer ordre;
        private Long noeudId;
        private String noeudNom;
        private Role roleRequis;
        private String description;
        private boolean obligatoire;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class CircuitCourrierDto {
        private Long etapeId;
        private String etapeNom;
        private Integer ordre;
        private String noeudNom;
        private EtapeCourrier.StatutEtape statut;
        private String responsableNom;
        private String commentaire;
        private LocalDateTime dateTraitement;
        private boolean courante;
    }
}
