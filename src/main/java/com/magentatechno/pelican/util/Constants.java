package com.magentatechno.pelican.util;

/**
 * Constantes globales de l'application Pelican
 */
public final class Constants {

    private Constants() {
        // Empêche l'instanciation
    }

    // Application
    public static final String APP_NAME = "Pelican by MagentaTechno";
    public static final String APP_VERSION = "1.0.0";

    // Numérotation des courriers
    public static final String PREFIX_COURRIER_ENTRANT = "CE";
    public static final String PREFIX_COURRIER_SORTANT = "CS";
    public static final String DATE_FORMAT = "yyyyMMdd";

    // Sécurité
    public static final int MAX_LOGIN_ATTEMPTS = 5;
    public static final int LOCK_DURATION_MINUTES = 30;
    public static final int BCRYPT_STRENGTH = 12;

    // Pagination
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;

    // Fichiers
    public static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 MB
    public static final String[] ALLOWED_EXTENSIONS = {
        "pdf", "doc", "docx", "xls", "xlsx", 
        "jpg", "jpeg", "png", "txt"
    };

    // JWT
    public static final long JWT_EXPIRATION = 900000; // 15 min
    public static final long JWT_REFRESH_EXPIRATION = 604800000; // 7 jours

    // Rate Limiting
    public static final int RATE_LIMIT_PER_MINUTE = 100;

    // Backups
    public static final String BACKUP_DIR = "./backups";
    public static final int BACKUP_RETENTION_DAYS = 7;
}
