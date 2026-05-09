// ===== OrganisationDto.java =====
package com.magentatechno.pelican.dto;

import com.magentatechno.pelican.entity.NoeudOrganisation;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public class OrganisationDto {

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class CreateRequest {
        @NotBlank(message = "Le nom est requis")
        private String nom;
        private String description;
        private String type;
        private Long parentId;
        private Integer ordre;
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class UpdateRequest {
        private String nom;
        private String description;
        private String type;
        private Long parentId;
        private Integer ordre;
        private Boolean actif;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Response {
        private Long id;
        private String nom;
        private String description;
        private NoeudOrganisation.TypeNoeud type;
        private Long parentId;
        private String parentNom;
        private Integer ordre;
        private boolean actif;
        private List<Response> enfants;
        private LocalDateTime createdAt;
    }
}
