package com.magentatechno.pelican.service;

import com.magentatechno.pelican.entity.Courrier;
import com.magentatechno.pelican.entity.HistoriqueCourrier;
import com.magentatechno.pelican.entity.User;
import com.magentatechno.pelican.repository.HistoriqueCourrierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class HistoriqueService {

    private final HistoriqueCourrierRepository historiqueRepository;

    public void log(
            Courrier courrier,
            User user,
            HistoriqueCourrier.Action action,
            String commentaire
    ) {
        if (courrier == null || action == null) {
            return;
        }

        HistoriqueCourrier historique = new HistoriqueCourrier();
        historique.setCourrier(courrier);
        historique.setUser(user);
        historique.setAction(action);
        historique.setCommentaire(commentaire);
        historique.setDate(LocalDateTime.now());

        historiqueRepository.save(historique);
    }

    public void log(
            Courrier courrier,
            User user,
            String action,
            String commentaire
    ) {
        if (action == null) {
            return;
        }

        HistoriqueCourrier.Action enumAction = normalizeAction(action);
        log(courrier, user, enumAction, commentaire);
    }

    private HistoriqueCourrier.Action normalizeAction(String action) {
        String a = action.trim().toUpperCase();

        return switch (a) {
            case "CREATION", "CREER", "CREATE" ->
                    HistoriqueCourrier.Action.CREATION;

            case "AFFECTATION", "AFFECTER", "AFFECTE" ->
                    HistoriqueCourrier.Action.AFFECTATION;

            case "TRANSFERT", "TRANSFERER", "TRANSFERE" ->
                    HistoriqueCourrier.Action.TRANSFERT;

            case "VALIDATION", "VALIDER", "VALIDE" ->
                    HistoriqueCourrier.Action.VALIDATION;

            case "REJET", "REJETER", "REJETE" ->
                    HistoriqueCourrier.Action.REJET;

            case "ARCHIVAGE", "ARCHIVER", "ARCHIVE" ->
                    HistoriqueCourrier.Action.ARCHIVAGE;

            case "MODIFICATION", "MODIFIER", "MODIFIE" ->
                    HistoriqueCourrier.Action.MODIFICATION;

            default -> throw new IllegalArgumentException("Action historique invalide : " + action);
        };
    }
}
