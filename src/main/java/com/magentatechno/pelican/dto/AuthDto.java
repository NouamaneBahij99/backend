package com.magentatechno.pelican.dto;

import jakarta.validation.constraints.*;
import lombok.*;

public class AuthDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequest {
        @Email(message = "Email invalide")
        @NotBlank(message = "Email requis")
        private String email;

        @NotBlank(message = "Mot de passe requis")
        @Size(min = 8, message = "Au moins 8 caractères")
        private String password;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AuthResponse {
        private String accessToken;
        private String refreshToken;
        private String email;
        private String role;
        private String nom;
        private String prenom;
        private Long userId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterRequest {
        @NotBlank(message = "Nom requis")
        private String nom;

        @NotBlank(message = "Prénom requis")
        private String prenom;

        @Email(message = "Email invalide")
        @NotBlank(message = "Email requis")
        private String email;

        @NotBlank(message = "Mot de passe requis")
        @Size(min = 8, message = "Minimum 8 caractères")
        @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&]).+$",
                message = "Majuscule, minuscule, chiffre et caractère spécial requis")
        private String password;

        @NotBlank(message = "Rôle requis")
        private String role;

        private String service;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RefreshTokenRequest {
        @NotBlank(message = "Token requis")
        private String refreshToken;
    }
}
