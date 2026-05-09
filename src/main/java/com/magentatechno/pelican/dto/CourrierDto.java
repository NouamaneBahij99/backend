package com.magentatechno.pelican.dto;

import com.magentatechno.pelican.entity.Courrier;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public class CourrierDto {

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class CreateRequest {
        @NotBlank(message = "Objet requis")
        private String objet;
        private String contenu;
        @NotBlank(message = "Expéditeur requis")
        private String expediteur;
        @NotBlank(message = "Destinataire requis")
        private String destinataire;
        @NotBlank(message = "Type requis")
        private String type;
        private String priorite;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Response {
        private Long id;
        private String numero;
        private String objet;
        private String contenu;
        private String expediteur;
        private String destinataire;
        private Courrier.TypeCourrier type;
        private Courrier.StatutCourrier statut;
        private Courrier.Priorite priorite;
        private String fichierNom;
        private String fichierPath;
        private String createurNom;
        private String assigneANom;
        private String etapeCouranteNom;
        private String workflowNom;
        private boolean archive;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private List<HistoriqueDto> historiques;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class HistoriqueDto {
        private Long id;
        private String userNom;
        private String action;
        private String commentaire;
        private LocalDateTime date;
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class UpdateRequest {
        private String objet;
        private String contenu;
        private String expediteur;
        private String destinataire;
        private String priorite;
    }
}
