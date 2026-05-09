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
@Table(name = "noeuds_organisation")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class NoeudOrganisation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom est requis")
    @Column(nullable = false)
    private String nom;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TypeNoeud type = TypeNoeud.SERVICE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private NoeudOrganisation parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<NoeudOrganisation> enfants = new ArrayList<>();

    @OneToMany(mappedBy = "noeud", fetch = FetchType.LAZY)
    @Builder.Default
    private List<User> utilisateurs = new ArrayList<>();

    @Builder.Default
    private boolean actif = true;

    @Column(nullable = false)
    @Builder.Default
    private Integer ordre = 0;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public enum TypeNoeud {
        SERVICE, POSTE, DIRECTION, DEPARTEMENT
    }
}
