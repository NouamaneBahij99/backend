package com.magentatechno.pelican.dto;

import lombok.*;

import java.time.LocalDateTime;

public class UserDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String nom;
        private String prenom;
        private String email;
        private String role;
        private String service;
        private boolean enabled;
        private boolean accountNonLocked;
        private LocalDateTime lastLogin;
        private LocalDateTime createdAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        private String nom;
        private String prenom;
        private String service;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChangePasswordRequest {
        private String oldPassword;
        private String newPassword;
        private String confirmPassword;
    }
}
