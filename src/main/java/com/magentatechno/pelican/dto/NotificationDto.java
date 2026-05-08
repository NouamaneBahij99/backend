package com.magentatechno.pelican.dto;

import lombok.*;

import java.time.LocalDateTime;

public class NotificationDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String titre;
        private String message;
        private String lien;
        private String type;
        private boolean isRead;
        private LocalDateTime createdAt;
        private LocalDateTime readAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MarkAsReadRequest {
        private Long notificationId;
    }
}
