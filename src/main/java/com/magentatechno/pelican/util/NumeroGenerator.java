package com.magentatechno.pelican.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Générateur de numéros automatiques
 */
public class NumeroGenerator {

    private static final DateTimeFormatter DATE_FORMATTER = 
        DateTimeFormatter.ofPattern(Constants.DATE_FORMAT);

    /**
     * Génère un numéro de courrier au format: CE-YYYYMMDD-XXXXXX
     * ou CS-YYYYMMDD-XXXXXX selon le type
     */
    public static String generateCourrierNumero(String type) {
        String prefix = type.equals("ENTRANT") ? 
            Constants.PREFIX_COURRIER_ENTRANT : 
            Constants.PREFIX_COURRIER_SORTANT;
        
        String date = LocalDateTime.now().format(DATE_FORMATTER);
        String unique = UUID.randomUUID().toString()
            .substring(0, 6)
            .toUpperCase();
        
        return prefix + "-" + date + "-" + unique;
    }

    /**
     * Génère un identifiant unique court
     */
    public static String generateShortId() {
        return UUID.randomUUID().toString()
            .substring(0, 8)
            .toUpperCase();
    }

    /**
     * Génère un nom de fichier unique
     */
    public static String generateUniqueFileName(String originalName) {
        String extension = "";
        int idx = originalName.lastIndexOf('.');
        if (idx > 0) {
            extension = originalName.substring(idx);
        }
        return UUID.randomUUID().toString() + extension;
    }
}
