package com.magentatechno.pelican.service;

import com.magentatechno.pelican.dto.OrganisationDto;
import com.magentatechno.pelican.entity.NoeudOrganisation;
import com.magentatechno.pelican.exception.BusinessException;
import com.magentatechno.pelican.exception.ResourceNotFoundException;
import com.magentatechno.pelican.repository.NoeudOrganisationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NoeudOrganisationService {

    private final NoeudOrganisationRepository noeudRepository;

    @Transactional
    public OrganisationDto.Response create(OrganisationDto.CreateRequest request) {
        if (noeudRepository.existsByNom(request.getNom())) {
            throw new BusinessException("Un noeud avec ce nom existe deja");
        }

        NoeudOrganisation noeud = NoeudOrganisation.builder()
                .nom(request.getNom())
                .description(request.getDescription())
                .type(request.getType() != null ?
                        NoeudOrganisation.TypeNoeud.valueOf(request.getType()) :
                        NoeudOrganisation.TypeNoeud.SERVICE)
                .ordre(request.getOrdre() != null ? request.getOrdre() : 0)
                .actif(true)
                .build();

        if (request.getParentId() != null) {
            NoeudOrganisation parent = noeudRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Noeud parent non trouve"));
            noeud.setParent(parent);
        }

        NoeudOrganisation saved = noeudRepository.save(noeud);
        log.info("Noeud organisation cree: {}", saved.getNom());
        return mapToResponse(saved, true);
    }

    @Transactional(readOnly = true)
    public List<OrganisationDto.Response> findAll() {
        return noeudRepository.findAllActifs().stream()
                .map(n -> mapToResponse(n, true))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrganisationDto.Response> findRacines() {
        return noeudRepository.findByParentIsNullAndActifTrue().stream()
                .map(n -> mapToResponse(n, true))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrganisationDto.Response findById(Long id) {
        NoeudOrganisation noeud = noeudRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Noeud non trouve"));
        return mapToResponse(noeud, true);
    }

    @Transactional
    public OrganisationDto.Response update(Long id, OrganisationDto.UpdateRequest request) {
        NoeudOrganisation noeud = noeudRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Noeud non trouve"));

        if (request.getNom() != null) noeud.setNom(request.getNom());
        if (request.getDescription() != null) noeud.setDescription(request.getDescription());
        if (request.getType() != null) noeud.setType(NoeudOrganisation.TypeNoeud.valueOf(request.getType()));
        if (request.getOrdre() != null) noeud.setOrdre(request.getOrdre());
        if (request.getActif() != null) noeud.setActif(request.getActif());

        if (request.getParentId() != null) {
            NoeudOrganisation parent = noeudRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Noeud parent non trouve"));
            noeud.setParent(parent);
        }

        return mapToResponse(noeudRepository.save(noeud), true);
    }

    @Transactional
    public void delete(Long id) {
        NoeudOrganisation noeud = noeudRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Noeud non trouve"));
        noeud.setActif(false);
        noeudRepository.save(noeud);
        log.info("Noeud desactive: {}", noeud.getNom());
    }

    public OrganisationDto.Response mapToResponse(NoeudOrganisation n, boolean includeEnfants) {
        List<OrganisationDto.Response> enfants = null;
        if (includeEnfants && n.getEnfants() != null) {
            enfants = n.getEnfants().stream()
                    .filter(NoeudOrganisation::isActif)
                    .map(e -> mapToResponse(e, false))
                    .collect(Collectors.toList());
        }

        return OrganisationDto.Response.builder()
                .id(n.getId())
                .nom(n.getNom())
                .description(n.getDescription())
                .type(n.getType())
                .parentId(n.getParent() != null ? n.getParent().getId() : null)
                .parentNom(n.getParent() != null ? n.getParent().getNom() : null)
                .ordre(n.getOrdre())
                .actif(n.isActif())
                .enfants(enfants)
                .createdAt(n.getCreatedAt())
                .build();
    }
}
