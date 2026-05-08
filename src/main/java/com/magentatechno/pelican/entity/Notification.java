package com.magentatechno.pelican.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications", indexes = {
        @Index(name = "idx_notif_user", columnList = "user_id"),
        @Index(name = "idx_notif_read", columnList = "is_read"),
        @Index(name = "idx_notif_date", columnList = "created_at")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String titre;

    @Column(columnDefinition = "TEXT")
    private String message;

    private String lien;

    @Enumerated(EnumType.STRING)
    private TypeNotification type;

    @Builder.Default
    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime readAt;

    public enum TypeNotification {
        COURRIER_ASSIGNE,
        COURRIER_VALIDE,
        COURRIER_REJETE,
        WORKFLOW,
        SYSTEME
    }
}
