package com.magentatechno.pelican.dto.response;

import com.magentatechno.pelican.entity.HistoriqueCourrier;

import java.time.format.DateTimeFormatter;

public record HistoriqueCourrierResponse(
        Long id,
        String action,
        String commentaire,
        String date,
        Long courrierId,
        Long userId,
        String userNom
) {
    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public static HistoriqueCourrierResponse fromEntity(HistoriqueCourrier h) {

        Long userId = null;
        String userNom = null;

        if (h.getUser() != null) {
            userId = h.getUser().getId();
            String prenom = h.getUser().getPrenom() != null ? h.getUser().getPrenom() : "";
            String nom    = h.getUser().getNom()    != null ? h.getUser().getNom()    : "";
            userNom = (prenom + " " + nom).trim();
        }

        Long courrierId = null;
        if (h.getCourrier() != null) {
            courrierId = h.getCourrier().getId();
        }

        String dateStr = null;
        if (h.getDate() != null) {
            dateStr = h.getDate().format(FMT);
        }

        String action = h.getAction() != null ? h.getAction().name() : null;

        return new HistoriqueCourrierResponse(
                h.getId(),
                action,
                h.getCommentaire(),
                dateStr,
                courrierId,
                userId,
                userNom
        );
    }
}
