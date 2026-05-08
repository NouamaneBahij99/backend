package com.magentatechno.pelican.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Utilitaires pour la manipulation des dates
 */
public class DateUtils {

    public static final DateTimeFormatter DATE_TIME_FORMATTER = 
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    
    public static final DateTimeFormatter DATE_FORMATTER = 
        DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    public static final DateTimeFormatter TIME_FORMATTER = 
        DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Formate une date en format français
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return "";
        return dateTime.format(DATE_TIME_FORMATTER);
    }

    /**
     * Formate uniquement la date
     */
    public static String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) return "";
        return dateTime.format(DATE_FORMATTER);
    }

    /**
     * Convertit Date en LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        if (date == null) return null;
        return date.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime();
    }

    /**
     * Convertit LocalDateTime en Date
     */
    public static Date toDate(LocalDateTime localDateTime) {
        if (localDateTime == null) return null;
        return Date.from(localDateTime
            .atZone(ZoneId.systemDefault())
            .toInstant());
    }

    /**
     * Calcule la différence en jours entre deux dates
     */
    public static long daysBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) return 0;
        return java.time.Duration.between(start, end).toDays();
    }

    /**
     * Vérifie si une date est dans le passé
     */
    public static boolean isPast(LocalDateTime dateTime) {
        if (dateTime == null) return false;
        return dateTime.isBefore(LocalDateTime.now());
    }
}
