package com.magentatechno.pelican.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "etapes_workflow")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EtapeWorkflow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom est requis")
    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private Integer ordre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_id", nullable = false)
    private WorkflowConfig workflow;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "noeud_id")
    private NoeudOrganisation noeud;

    @Enumerated(EnumType.STRING)
    private Role roleRequis;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Builder.Default
    private boolean obligatoire = true;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
