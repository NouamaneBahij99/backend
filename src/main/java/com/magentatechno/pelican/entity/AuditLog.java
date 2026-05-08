package com.magentatechno.pelican.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs", indexes = {
    @Index(name = "idx_audit_user", columnList = "user_email"),
    @Index(name = "idx_audit_date", columnList = "created_at"),
    @Index(name = "idx_audit_action", columnList = "action")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userEmail;
    private String action;
    private String resource;
    private String ipAddress;
    private String userAgent;

    @Column(columnDefinition = "TEXT")
    private String details;

    @Builder.Default
    private boolean success = true;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
