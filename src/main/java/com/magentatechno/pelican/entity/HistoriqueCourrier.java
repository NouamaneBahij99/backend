package com.magentatechno.pelican.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "historique_courriers", indexes = {
    @Index(name = "idx_hist_courrier", columnList = "courrier_id"),
    @Index(name = "idx_hist_user", columnList = "user_id"),
    @Index(name = "idx_hist_date", columnList = "date")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoriqueCourrier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "courrier_id", nullable = false)
    private Courrier courrier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Action action;

    @Column(columnDefinition = "TEXT")
    private String commentaire;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime date;

    public enum Action {
        CREATION, AFFECTATION, TRANSFERT, VALIDATION, REJET, ARCHIVAGE, MODIFICATION
    }
}
