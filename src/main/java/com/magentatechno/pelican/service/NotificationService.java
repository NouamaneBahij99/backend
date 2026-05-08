package com.magentatechno.pelican.service;

import com.magentatechno.pelican.dto.NotificationDto;
import com.magentatechno.pelican.entity.Courrier;
import com.magentatechno.pelican.entity.Notification;
import com.magentatechno.pelican.entity.User;
import com.magentatechno.pelican.exception.ResourceNotFoundException;
import com.magentatechno.pelican.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public Page<NotificationDto.Response> findByUserId(Long userId, Pageable pageable) {
        return notificationRepository.findByUserId(userId, pageable)
                .map(this::mapToResponse);
    }

    public Page<NotificationDto.Response> findUnreadByUserId(Long userId, Pageable pageable) {
        return notificationRepository.findByUserIdAndIsReadFalse(userId, pageable)
                .map(this::mapToResponse);
    }

    public long countUnreadByUserId(Long userId) {
        return notificationRepository.countUnreadByUserId(userId);
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification non trouvée"));
        notification.setRead(true);
        notification.setReadAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    @Transactional
    public void notifyAssignment(User user, Courrier courrier) {
        Notification notification = Notification.builder()
                .user(user)
                .titre("Nouveau courrier assigné")
                .message("Un courrier vous a été assigné: " + courrier.getObjet())
                .lien("/courriers/" + courrier.getId())
                .type(Notification.TypeNotification.COURRIER_ASSIGNE)
                .isRead(false)
                .build();
        notificationRepository.save(notification);
        log.info("Notification envoyée à: {}", user.getEmail());
    }

    @Transactional
    public void notifyValidation(User user, Courrier courrier) {
        Notification notification = Notification.builder()
                .user(user)
                .titre("Courrier validé")
                .message("Votre courrier a été validé: " + courrier.getObjet())
                .lien("/courriers/" + courrier.getId())
                .type(Notification.TypeNotification.COURRIER_VALIDE)
                .isRead(false)
                .build();
        notificationRepository.save(notification);
    }

    @Transactional
    public void notifyRejection(User user, Courrier courrier, String reason) {
        Notification notification = Notification.builder()
                .user(user)
                .titre("Courrier rejeté")
                .message("Votre courrier a été rejeté: " + reason)
                .lien("/courriers/" + courrier.getId())
                .type(Notification.TypeNotification.COURRIER_REJETE)
                .isRead(false)
                .build();
        notificationRepository.save(notification);
    }

    private NotificationDto.Response mapToResponse(Notification n) {
        return NotificationDto.Response.builder()
                .id(n.getId())
                .titre(n.getTitre())
                .message(n.getMessage())
                .lien(n.getLien())
                .type(n.getType() != null ? n.getType().name() : null)
                .isRead(n.isRead())
                .createdAt(n.getCreatedAt())
                .readAt(n.getReadAt())
                .build();
    }
}
