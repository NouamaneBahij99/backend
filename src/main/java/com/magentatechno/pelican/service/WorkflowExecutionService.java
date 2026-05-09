package com.magentatechno.pelican.service;

import com.magentatechno.pelican.entity.*;
import com.magentatechno.pelican.exception.BusinessException;
import com.magentatechno.pelican.exception.ResourceNotFoundException;
import com.magentatechno.pelican.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkflowExecutionService {

    private final CourrierRepository courrierRepository;
    private final EtapeCourrierRepository etapeCourrierRepository;
    private final WorkflowConfigRepository workflowRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final WorkflowService workflowService;

    @Transactional
    public void initialiserWorkflow(Courrier courrier) {
        WorkflowConfig workflow = workflowService.trouverWorkflowApplicable(courrier.getType());

        if (workflow.getEtapes() == null || workflow.getEtapes().isEmpty()) {
            log.warn("Workflow {} n'a pas d'etapes configurees", workflow.getNom());
            return;
        }

        courrier.setWorkflow(workflow);

        List<EtapeWorkflow> etapes = workflow.getEtapes().stream()
                .sorted((a, b) -> Integer.compare(a.getOrdre(), b.getOrdre()))
                .toList();

        for (EtapeWorkflow etape : etapes) {
            EtapeCourrier ec = EtapeCourrier.builder()
                    .courrier(courrier)
                    .etape(etape)
                    .statut(EtapeCourrier.StatutEtape.EN_ATTENTE)
                    .build();
            courrier.getEtapesCourrier().add(ec);
        }

        // Activer la première étape
        EtapeCourrier premiere = courrier.getEtapesCourrier().get(0);
        premiere.setStatut(EtapeCourrier.StatutEtape.EN_COURS);
        courrier.setEtapeCourante(premiere.getEtape());
        courrier.setStatut(Courrier.StatutCourrier.EN_COURS);

        // Assigner au responsable de la première étape
        User responsable = trouverResponsable(premiere.getEtape());
        if (responsable != null) {
            premiere.setResponsable(responsable);
            courrier.setAssigneA(responsable);
            notificationService.notifyNouvelleEtape(responsable, courrier, premiere.getEtape().getNom());
        }

        courrierRepository.save(courrier);
        log.info("Workflow '{}' initialise pour courrier {}", workflow.getNom(), courrier.getNumero());
    }

    @Transactional
    public Courrier avancerEtape(Long courrierId, String commentaire, User validateur) {
        Courrier courrier = courrierRepository.findById(courrierId)
                .orElseThrow(() -> new ResourceNotFoundException("Courrier non trouve"));

        if (courrier.getWorkflow() == null) {
            throw new BusinessException("Ce courrier n'a pas de workflow associe");
        }

        Optional<EtapeCourrier> etapeCouranteOpt =
                etapeCourrierRepository.findEtapeCouranteByCourrierID(courrierId);

        if (etapeCouranteOpt.isEmpty()) {
            throw new BusinessException("Aucune etape en cours pour ce courrier");
        }

        EtapeCourrier etapeCourante = etapeCouranteOpt.get();
        etapeCourante.setStatut(EtapeCourrier.StatutEtape.VALIDE);
        etapeCourante.setCommentaire(commentaire);
        etapeCourante.setDateTraitement(LocalDateTime.now());
        etapeCourrierRepository.save(etapeCourante);

        // Chercher l'étape suivante
        List<EtapeCourrier> etapes = etapeCourrierRepository
                .findByCourrierIdOrderByEtapeOrdreAsc(courrierId);

        EtapeCourrier prochaine = etapes.stream()
                .filter(e -> e.getStatut() == EtapeCourrier.StatutEtape.EN_ATTENTE)
                .findFirst()
                .orElse(null);

        if (prochaine != null) {
            prochaine.setStatut(EtapeCourrier.StatutEtape.EN_COURS);
            courrier.setEtapeCourante(prochaine.getEtape());

            User responsable = trouverResponsable(prochaine.getEtape());
            if (responsable != null) {
                prochaine.setResponsable(responsable);
                courrier.setAssigneA(responsable);
                notificationService.notifyNouvelleEtape(responsable, courrier, prochaine.getEtape().getNom());
            }

            etapeCourrierRepository.save(prochaine);
            log.info("Courrier {} avance a l'etape: {}", courrier.getNumero(), prochaine.getEtape().getNom());
        } else {
            // Toutes les étapes validées → courrier validé
            courrier.setStatut(Courrier.StatutCourrier.VALIDE);
            courrier.setEtapeCourante(null);
            if (courrier.getCreateur() != null) {
                notificationService.notifyValidation(courrier.getCreateur(), courrier);
            }
            log.info("Courrier {} completement valide - toutes les etapes franchies", courrier.getNumero());
        }

        return courrierRepository.save(courrier);
    }

    @Transactional
    public Courrier rejeterEtape(Long courrierId, String motif, User rejecteur) {
        Courrier courrier = courrierRepository.findById(courrierId)
                .orElseThrow(() -> new ResourceNotFoundException("Courrier non trouve"));

        etapeCourrierRepository.findEtapeCouranteByCourrierID(courrierId)
                .ifPresent(ec -> {
                    ec.setStatut(EtapeCourrier.StatutEtape.REJETE);
                    ec.setCommentaire(motif);
                    ec.setDateTraitement(LocalDateTime.now());
                    etapeCourrierRepository.save(ec);
                });

        courrier.setStatut(Courrier.StatutCourrier.REJETE);
        courrier.setEtapeCourante(null);

        if (courrier.getCreateur() != null) {
            notificationService.notifyRejection(courrier.getCreateur(), courrier, motif);
        }

        return courrierRepository.save(courrier);
    }

    private User trouverResponsable(EtapeWorkflow etape) {
        if (etape.getNoeud() != null && !etape.getNoeud().getUtilisateurs().isEmpty()) {
            return etape.getNoeud().getUtilisateurs().stream()
                    .filter(u -> u.isEnabled() && u.isAccountNonLocked())
                    .findFirst()
                    .orElse(null);
        }

        if (etape.getRoleRequis() != null) {
            return userRepository.findFirstByRoleAndEnabledTrue(etape.getRoleRequis())
                    .orElse(null);
        }

        return null;
    }
}
