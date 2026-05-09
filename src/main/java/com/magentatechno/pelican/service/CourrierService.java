package com.magentatechno.pelican.service;

import com.magentatechno.pelican.dto.CourrierDto;
import com.magentatechno.pelican.entity.Courrier;
import com.magentatechno.pelican.entity.HistoriqueCourrier;
import com.magentatechno.pelican.entity.User;
import com.magentatechno.pelican.exception.ResourceNotFoundException;
import com.magentatechno.pelican.repository.CourrierRepository;
import com.magentatechno.pelican.repository.HistoriqueCourrierRepository;
import com.magentatechno.pelican.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourrierService {

    private final CourrierRepository courrierRepository;
    private final HistoriqueCourrierRepository historiqueCourrierRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    private final AuditService auditService;
    private final NotificationService notificationService;

    @Transactional
    public CourrierDto.Response create(CourrierDto.CreateRequest request, MultipartFile file) {
        log.info("Creation courrier: {}", request.getObjet());
        User currentUser = getCurrentUser();

        Courrier courrier = Courrier.builder()
                .numero(generateNumero(request.getType()))
                .objet(request.getObjet())
                .contenu(request.getContenu())
                .expediteur(request.getExpediteur())
                .destinataire(request.getDestinataire())
                .type(Courrier.TypeCourrier.valueOf(request.getType()))
                .statut(Courrier.StatutCourrier.NOUVEAU)
                .priorite(request.getPriorite() != null ?
                        Courrier.Priorite.valueOf(request.getPriorite()) : Courrier.Priorite.NORMALE)
                .createur(currentUser)
                .build();

        if (file != null && !file.isEmpty()) {
            String fileName = fileStorageService.storeFile(file);
            courrier.setFichierPath(fileName);
            courrier.setFichierNom(file.getOriginalFilename());
        }

        Courrier saved = courrierRepository.save(courrier);
        addHistorique(saved, currentUser, HistoriqueCourrier.Action.CREATION, "Courrier cree");
        saved = courrierRepository.save(saved);

        try {
            auditService.log(currentUser.getEmail(), "CREATE_COURRIER", "COURRIER", true, "Cree: " + saved.getNumero());
        } catch (Exception e) {
            log.warn("Audit error: {}", e.getMessage());
        }

        return mapToResponseSimple(saved);
    }

    @Transactional(readOnly = true)
    public Page<CourrierDto.Response> findAll(Pageable pageable, String search, String type, String statut) {
        Courrier.TypeCourrier typeEnum = (type != null && !type.isEmpty()) ? Courrier.TypeCourrier.valueOf(type) : null;
        Courrier.StatutCourrier statutEnum = (statut != null && !statut.isEmpty()) ? Courrier.StatutCourrier.valueOf(statut) : null;
        String searchParam = (search != null && !search.isEmpty()) ? search : null;

        return courrierRepository.searchAdvanced(searchParam, typeEnum, statutEnum, pageable)
                .map(this::mapToResponseSimple);
    }

    @Transactional(readOnly = true)
    public CourrierDto.Response findById(Long id) {
        Courrier courrier = courrierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Courrier non trouve"));
        return mapToResponse(courrier);
    }

    @Transactional
    public CourrierDto.Response affecter(Long id, Long userId) {
        Courrier courrier = courrierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Courrier non trouve"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User non trouve"));

        User currentUser = getCurrentUser();
        courrier.setAssigneA(user);
        courrier.setStatut(Courrier.StatutCourrier.EN_COURS);
        addHistorique(courrier, currentUser, HistoriqueCourrier.Action.AFFECTATION,
                "Affecte a " + user.getNom() + " " + user.getPrenom());

        Courrier updated = courrierRepository.save(courrier);

        try {
            notificationService.notifyAssignment(user, courrier);
        } catch (Exception e) {
            log.warn("Notif error: {}", e.getMessage());
        }

        return mapToResponseSimple(updated);
    }

    @Transactional
    public CourrierDto.Response transferer(Long id, Long userId, String commentaire) {
        Courrier courrier = courrierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Courrier non trouve"));
        User nouvelUtilisateur = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur destinataire non trouve"));

        User currentUser = getCurrentUser();
        User ancienUtilisateur = courrier.getAssigneA();

        courrier.setAssigneA(nouvelUtilisateur);
        courrier.setStatut(Courrier.StatutCourrier.EN_COURS);

        String message = "Transfere de " +
                (ancienUtilisateur != null ? ancienUtilisateur.getNom() + " " + ancienUtilisateur.getPrenom() : "Non assigne") +
                " vers " + nouvelUtilisateur.getNom() + " " + nouvelUtilisateur.getPrenom();
        if (commentaire != null && !commentaire.isEmpty()) {
            message += " - " + commentaire;
        }

        addHistorique(courrier, currentUser, HistoriqueCourrier.Action.TRANSFERT, message);
        Courrier updated = courrierRepository.save(courrier);

        try {
            notificationService.notifyAssignment(nouvelUtilisateur, courrier);
        } catch (Exception e) {
            log.warn("Notif error: {}", e.getMessage());
        }

        try {
            auditService.log(currentUser.getEmail(), "TRANSFERER_COURRIER", "COURRIER", true,
                    "Courrier " + courrier.getNumero() + " transfere");
        } catch (Exception e) {
            log.warn("Audit error: {}", e.getMessage());
        }

        log.info("Courrier {} transfere a {}", courrier.getNumero(), nouvelUtilisateur.getEmail());
        return mapToResponseSimple(updated);
    }

    @Transactional
    public CourrierDto.Response valider(Long id, String commentaire) {
        Courrier courrier = courrierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Courrier non trouve"));
        User currentUser = getCurrentUser();

        courrier.setStatut(Courrier.StatutCourrier.VALIDE);
        addHistorique(courrier, currentUser, HistoriqueCourrier.Action.VALIDATION,
                commentaire != null ? commentaire : "Valide");

        Courrier updated = courrierRepository.save(courrier);

        try {
            if (courrier.getCreateur() != null) {
                notificationService.notifyValidation(courrier.getCreateur(), courrier);
            }
        } catch (Exception e) {
            log.warn("Notif error: {}", e.getMessage());
        }

        return mapToResponseSimple(updated);
    }

    @Transactional
    public CourrierDto.Response rejeter(Long id, String motif) {
        Courrier courrier = courrierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Courrier non trouve"));
        User currentUser = getCurrentUser();

        courrier.setStatut(Courrier.StatutCourrier.REJETE);
        addHistorique(courrier, currentUser, HistoriqueCourrier.Action.REJET, motif);

        Courrier updated = courrierRepository.save(courrier);

        try {
            if (courrier.getCreateur() != null) {
                notificationService.notifyRejection(courrier.getCreateur(), courrier, motif);
            }
        } catch (Exception e) {
            log.warn("Notif error: {}", e.getMessage());
        }

        return mapToResponseSimple(updated);
    }

    @Transactional
    public CourrierDto.Response archiver(Long id) {
        Courrier courrier = courrierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Courrier non trouve"));
        User currentUser = getCurrentUser();

        courrier.setArchive(true);
        courrier.setStatut(Courrier.StatutCourrier.ARCHIVE);
        addHistorique(courrier, currentUser, HistoriqueCourrier.Action.ARCHIVAGE, "Archive");

        Courrier updated = courrierRepository.save(courrier);
        return mapToResponseSimple(updated);
    }

    @Transactional
    public CourrierDto.Response update(Long id, CourrierDto.UpdateRequest request) {
        Courrier courrier = courrierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Courrier non trouve"));
        User currentUser = getCurrentUser();

        if (request.getObjet() != null) courrier.setObjet(request.getObjet());
        if (request.getContenu() != null) courrier.setContenu(request.getContenu());
        if (request.getExpediteur() != null) courrier.setExpediteur(request.getExpediteur());
        if (request.getDestinataire() != null) courrier.setDestinataire(request.getDestinataire());
        if (request.getPriorite() != null) courrier.setPriorite(Courrier.Priorite.valueOf(request.getPriorite()));

        addHistorique(courrier, currentUser, HistoriqueCourrier.Action.MODIFICATION, "Courrier modifie");

        try {
            auditService.log(currentUser.getEmail(), "UPDATE_COURRIER", "COURRIER", true,
                    "Courrier " + courrier.getNumero() + " modifie");
        } catch (Exception e) {
            log.warn("Audit error: {}", e.getMessage());
        }

        return mapToResponseSimple(courrierRepository.save(courrier));
    }

    private void addHistorique(Courrier courrier, User user, HistoriqueCourrier.Action action, String comment) {
        HistoriqueCourrier hist = HistoriqueCourrier.builder()
                .courrier(courrier)
                .user(user)
                .action(action)
                .commentaire(comment)
                .build();
        if (courrier.getHistoriques() == null) {
            courrier.setHistoriques(new ArrayList<>());
        }
        courrier.getHistoriques().add(hist);
    }

    private String generateNumero(String type) {
        String prefix = type.equals("ENTRANT") ? "CE" : "CS";
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String unique = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return prefix + "-" + date + "-" + unique;
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User non trouve"));
    }

    private CourrierDto.Response mapToResponseSimple(Courrier c) {
        return CourrierDto.Response.builder()
                .id(c.getId())
                .numero(c.getNumero())
                .objet(c.getObjet())
                .contenu(c.getContenu())
                .expediteur(c.getExpediteur())
                .destinataire(c.getDestinataire())
                .type(c.getType())
                .statut(c.getStatut())
                .priorite(c.getPriorite())
                .fichierNom(c.getFichierNom())
                .fichierPath(c.getFichierPath())
                .createurNom(c.getCreateur() != null ?
                        c.getCreateur().getNom() + " " + c.getCreateur().getPrenom() : null)
                .assigneANom(c.getAssigneA() != null ?
                        c.getAssigneA().getNom() + " " + c.getAssigneA().getPrenom() : null)
                .archive(c.isArchive())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }

    private CourrierDto.Response mapToResponse(Courrier c) {
        List<CourrierDto.HistoriqueDto> historiques = new ArrayList<>();
        if (c.getHistoriques() != null) {
            historiques = c.getHistoriques().stream()
                    .map(h -> CourrierDto.HistoriqueDto.builder()
                            .id(h.getId())
                            .userNom(h.getUser() != null ? h.getUser().getNom() + " " + h.getUser().getPrenom() : "Systeme")
                            .action(h.getAction().name())
                            .commentaire(h.getCommentaire())
                            .date(h.getDate())
                            .build())
                    .collect(Collectors.toList());
        }

        return CourrierDto.Response.builder()
                .id(c.getId())
                .numero(c.getNumero())
                .objet(c.getObjet())
                .contenu(c.getContenu())
                .expediteur(c.getExpediteur())
                .destinataire(c.getDestinataire())
                .type(c.getType())
                .statut(c.getStatut())
                .priorite(c.getPriorite())
                .fichierNom(c.getFichierNom())
                .fichierPath(c.getFichierPath())
                .createurNom(c.getCreateur() != null ?
                        c.getCreateur().getNom() + " " + c.getCreateur().getPrenom() : null)
                .assigneANom(c.getAssigneA() != null ?
                        c.getAssigneA().getNom() + " " + c.getAssigneA().getPrenom() : null)
                .archive(c.isArchive())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .historiques(historiques)
                .build();
    }
}