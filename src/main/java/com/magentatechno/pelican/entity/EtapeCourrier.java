package com.magentatechno.pelican.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "etapes_courriers")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EtapeCourrier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "courrier_id", nullable = false)
    private Courrier courrier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etape_id", nullable = false)
    private EtapeWorkflow etape;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsable_id")
    private User responsable;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatutEtape statut = StatutEtape.EN_ATTENTE;

    @Column(columnDefinition = "TEXT")
    private String commentaire;

    private LocalDateTime dateTraitement;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public enum StatutEtape {
        EN_ATTENTE, EN_COURS, VALIDE, REJETE, IGNORE
    }
}
