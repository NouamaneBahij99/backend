package com.magentatechno.pelican.service;

import com.magentatechno.pelican.dto.WorkflowDto;
import com.magentatechno.pelican.entity.*;
import com.magentatechno.pelican.exception.BusinessException;
import com.magentatechno.pelican.exception.ResourceNotFoundException;
import com.magentatechno.pelican.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkflowService {

    private final WorkflowConfigRepository workflowRepository;
    private final NoeudOrganisationRepository noeudRepository;
    private final EtapeCourrierRepository etapeCourrierRepository;

    @Transactional
    public WorkflowDto.Response create(WorkflowDto.CreateRequest request) {
        if (workflowRepository.existsByNom(request.getNom())) {
            throw new BusinessException("Un workflow avec ce nom existe deja");
        }

        if (request.isDefaut()) {
            workflowRepository.findByDefautTrueAndActifTrue()
                    .ifPresent(w -> { w.setDefaut(false); workflowRepository.save(w); });
        }

        WorkflowConfig workflow = WorkflowConfig.builder()
                .nom(request.getNom())
                .description(request.getDescription())
                .typeCourrier(request.getTypeCourrier() != null ?
                        Courrier.TypeCourrier.valueOf(request.getTypeCourrier()) : null)
                .actif(true)
                .defaut(request.isDefaut())
                .etapes(new ArrayList<>())
                .build();

        WorkflowConfig saved = workflowRepository.save(workflow);

        if (request.getEtapes() != null) {
            List<EtapeWorkflow> etapes = new ArrayList<>();
            for (int i = 0; i < request.getEtapes().size(); i++) {
                WorkflowDto.EtapeRequest etapeReq = request.getEtapes().get(i);
                EtapeWorkflow etape = buildEtape(etapeReq, saved, i + 1);
                etapes.add(etape);
            }
            saved.setEtapes(etapes);
            saved = workflowRepository.save(saved);
        }

        log.info("Workflow cree: {}", saved.getNom());
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<WorkflowDto.Response> findAll() {
        return workflowRepository.findByActifTrue().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public WorkflowDto.Response findById(Long id) {
        return mapToResponse(workflowRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Workflow non trouve")));
    }

    @Transactional
    public WorkflowDto.Response update(Long id, WorkflowDto.UpdateRequest request) {
        WorkflowConfig workflow = workflowRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Workflow non trouve"));

        if (request.getNom() != null) workflow.setNom(request.getNom());
        if (request.getDescription() != null) workflow.setDescription(request.getDescription());
        if (request.getTypeCourrier() != null)
            workflow.setTypeCourrier(Courrier.TypeCourrier.valueOf(request.getTypeCourrier()));
        workflow.setActif(request.isActif());

        if (request.isDefaut()) {
            workflowRepository.findByDefautTrueAndActifTrue()
                    .ifPresent(w -> { w.setDefaut(false); workflowRepository.save(w); });
            workflow.setDefaut(true);
        }

        return mapToResponse(workflowRepository.save(workflow));
    }

    @Transactional
    public WorkflowDto.Response ajouterEtape(Long workflowId, WorkflowDto.EtapeRequest request) {
        WorkflowConfig workflow = workflowRepository.findById(workflowId)
                .orElseThrow(() -> new ResourceNotFoundException("Workflow non trouve"));

        int ordre = request.getOrdre() != null ? request.getOrdre() :
                workflow.getEtapes().size() + 1;

        // Décaler les étapes existantes si besoin
        workflow.getEtapes().stream()
                .filter(e -> e.getOrdre() >= ordre)
                .forEach(e -> e.setOrdre(e.getOrdre() + 1));

        EtapeWorkflow etape = buildEtape(request, workflow, ordre);
        workflow.getEtapes().add(etape);

        return mapToResponse(workflowRepository.save(workflow));
    }

    @Transactional
    public WorkflowDto.Response supprimerEtape(Long workflowId, Long etapeId) {
        WorkflowConfig workflow = workflowRepository.findById(workflowId)
                .orElseThrow(() -> new ResourceNotFoundException("Workflow non trouve"));

        workflow.getEtapes().removeIf(e -> e.getId().equals(etapeId));

        // Réordonner
        List<EtapeWorkflow> etapes = workflow.getEtapes();
        for (int i = 0; i < etapes.size(); i++) {
            etapes.get(i).setOrdre(i + 1);
        }

        return mapToResponse(workflowRepository.save(workflow));
    }

    @Transactional
    public WorkflowDto.Response reorganiserEtapes(Long workflowId, List<Long> etapeIds) {
        WorkflowConfig workflow = workflowRepository.findById(workflowId)
                .orElseThrow(() -> new ResourceNotFoundException("Workflow non trouve"));

        for (int i = 0; i < etapeIds.size(); i++) {
            final int ordre = i + 1;
            final Long etapeId = etapeIds.get(i);
            workflow.getEtapes().stream()
                    .filter(e -> e.getId().equals(etapeId))
                    .findFirst()
                    .ifPresent(e -> e.setOrdre(ordre));
        }

        return mapToResponse(workflowRepository.save(workflow));
    }

    public WorkflowConfig trouverWorkflowApplicable(Courrier.TypeCourrier type) {
        return workflowRepository.findByTypeCourrierAndActifTrue(type)
                .orElseGet(() -> workflowRepository.findByDefautTrueAndActifTrue()
                        .orElseThrow(() -> new BusinessException(
                                "Aucun workflow configure. Veuillez creer un workflow dans les parametres.")));
    }

    public List<WorkflowDto.CircuitCourrierDto> getCircuitCourrier(Long courrierId) {
        return etapeCourrierRepository.findByCourrierIdOrderByEtapeOrdreAsc(courrierId).stream()
                .map(ec -> WorkflowDto.CircuitCourrierDto.builder()
                        .etapeId(ec.getEtape().getId())
                        .etapeNom(ec.getEtape().getNom())
                        .ordre(ec.getEtape().getOrdre())
                        .noeudNom(ec.getEtape().getNoeud() != null ? ec.getEtape().getNoeud().getNom() : null)
                        .statut(ec.getStatut())
                        .responsableNom(ec.getResponsable() != null ?
                                ec.getResponsable().getNom() + " " + ec.getResponsable().getPrenom() : null)
                        .commentaire(ec.getCommentaire())
                        .dateTraitement(ec.getDateTraitement())
                        .courante(ec.getStatut() == EtapeCourrier.StatutEtape.EN_COURS)
                        .build())
                .collect(Collectors.toList());
    }

    private EtapeWorkflow buildEtape(WorkflowDto.EtapeRequest req, WorkflowConfig workflow, int ordre) {
        EtapeWorkflow etape = new EtapeWorkflow();
        etape.setNom(req.getNom());
        etape.setOrdre(req.getOrdre() != null ? req.getOrdre() : ordre);
        etape.setWorkflow(workflow);
        etape.setDescription(req.getDescription());
        etape.setObligatoire(req.isObligatoire());

        if (req.getNoeudId() != null) {
            noeudRepository.findById(req.getNoeudId())
                    .ifPresent(etape::setNoeud);
        }

        if (req.getRoleRequis() != null) {
            etape.setRoleRequis(Role.valueOf(req.getRoleRequis()));
        }

        return etape;
    }

    public WorkflowDto.Response mapToResponse(WorkflowConfig w) {
        List<WorkflowDto.EtapeResponse> etapes = w.getEtapes() != null ?
                w.getEtapes().stream()
                        .sorted((a, b) -> Integer.compare(a.getOrdre(), b.getOrdre()))
                        .map(e -> WorkflowDto.EtapeResponse.builder()
                                .id(e.getId())
                                .nom(e.getNom())
                                .ordre(e.getOrdre())
                                .noeudId(e.getNoeud() != null ? e.getNoeud().getId() : null)
                                .noeudNom(e.getNoeud() != null ? e.getNoeud().getNom() : null)
                                .roleRequis(e.getRoleRequis())
                                .description(e.getDescription())
                                .obligatoire(e.isObligatoire())
                                .build())
                        .collect(Collectors.toList()) : new ArrayList<>();

        return WorkflowDto.Response.builder()
                .id(w.getId())
                .nom(w.getNom())
                .description(w.getDescription())
                .typeCourrier(w.getTypeCourrier())
                .actif(w.isActif())
                .defaut(w.isDefaut())
                .etapes(etapes)
                .createdAt(w.getCreatedAt())
                .build();
    }
}
