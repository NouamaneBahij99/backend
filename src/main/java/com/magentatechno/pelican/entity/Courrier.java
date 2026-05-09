package com.magentatechno.pelican.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "courriers", indexes = {
        @Index(name = "idx_courrier_numero", columnList = "numero", unique = true),
        @Index(name = "idx_courrier_statut", columnList = "statut"),
        @Index(name = "idx_courrier_type", columnList = "type"),
        @Index(name = "idx_courrier_createur", columnList = "createur_id"),
        @Index(name = "idx_courrier_assigne", columnList = "assigne_a_id")
})
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Courrier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String numero;

    @NotBlank(message = "L'objet est requis")
    @Column(nullable = false)
    private String objet;

    @Column(columnDefinition = "TEXT")
    private String contenu;

    @NotBlank(message = "L'expéditeur est requis")
    private String expediteur;

    @NotBlank(message = "Le destinataire est requis")
    private String destinataire;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeCourrier type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatutCourrier statut = StatutCourrier.NOUVEAU;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Priorite priorite = Priorite.NORMALE;

    private String fichierPath;
    private String fichierNom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createur_id", nullable = false)
    private User createur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigne_a_id")
    private User assigneA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_id")
    private WorkflowConfig workflow;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etape_courante_id")
    private EtapeWorkflow etapeCourante;

    @OneToMany(mappedBy = "courrier", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<HistoriqueCourrier> historiques = new ArrayList<>();

    @OneToMany(mappedBy = "courrier", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("createdAt ASC")
    @Builder.Default
    private List<EtapeCourrier> etapesCourrier = new ArrayList<>();

    @Builder.Default
    private boolean archive = false;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public enum TypeCourrier { ENTRANT, SORTANT }
    public enum StatutCourrier { NOUVEAU, EN_COURS, VALIDE, REJETE, ARCHIVE }
    public enum Priorite { BASSE, NORMALE, HAUTE, URGENTE }
}