package com.magentatechno.pelican.util;

import java.util.regex.Pattern;

/**
 * Validateur de mot de passe selon les règles de sécurité
 */
public class PasswordValidator {

    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$"
    );

    /**
     * Vérifie si le mot de passe respecte les règles:
     * - Au moins 8 caractères
     * - Au moins 1 majuscule
     * - Au moins 1 minuscule
     * - Au moins 1 chiffre
     * - Au moins 1 caractère spécial
     */
    public static boolean isValid(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    /**
     * Retourne un message d'erreur si le mot de passe est invalide
     */
    public static String getValidationMessage(String password) {
        if (password == null || password.isEmpty()) {
            return "Le mot de passe est requis";
        }
        if (password.length() < 8) {
            return "Le mot de passe doit contenir au moins 8 caractères";
        }
        if (!password.matches(".*[A-Z].*")) {
            return "Le mot de passe doit contenir au moins une majuscule";
        }
        if (!password.matches(".*[a-z].*")) {
            return "Le mot de passe doit contenir au moins une minuscule";
        }
        if (!password.matches(".*\\d.*")) {
            return "Le mot de passe doit contenir au moins un chiffre";
        }
        if (!password.matches(".*[@$!%*?&].*")) {
            return "Le mot de passe doit contenir au moins un caractère spécial (@$!%*?&)";
        }
        return "Mot de passe valide";
    }
}
