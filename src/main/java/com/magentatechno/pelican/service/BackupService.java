package com.magentatechno.pelican.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class BackupService {

    private static final String BACKUP_DIR = "./backups";
    private static final String DB_NAME = "pelican_db";
    private static final String DB_USER = "pelican_user";
    private static final String DB_PASSWORD = "changeme_strong_password";

    // Sauvegarde automatique tous les jours à 2h00 du matin
    @Scheduled(cron = "0 0 2 * * *")
    public void scheduledBackup() {
        log.info("🦩 Démarrage de la sauvegarde automatique...");
        try {
            String backupFile = createBackup();
            log.info("✅ Sauvegarde automatique terminée: {}", backupFile);
        } catch (Exception e) {
            log.error("❌ Erreur sauvegarde automatique: {}", e.getMessage());
        }
    }

    public String createBackup() throws Exception {
        // Créer le dossier
        Path backupDir = Paths.get(BACKUP_DIR);
        Files.createDirectories(backupDir);

        // Nom du fichier
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String fileName = "pelican_backup_" + timestamp + ".sql";
        Path backupFile = backupDir.resolve(fileName);

        // Trouver le bon pg_dump
        String pgDumpPath = findPgDump();
        log.info("Utilisation de pg_dump: {}", pgDumpPath);

        // Commande pg_dump
        ProcessBuilder pb = new ProcessBuilder(
            pgDumpPath,
            "-U", DB_USER,
            "-h", "localhost",
            "-d", DB_NAME,
            "-f", backupFile.toAbsolutePath().toString()
        );
        
        pb.environment().put("PGPASSWORD", DB_PASSWORD);
        pb.redirectErrorStream(true);

        Process process = pb.start();
        
        // Lire la sortie pour debug
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        StringBuilder output = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
            log.debug(line);
        }

        int exitCode = process.waitFor();
        
        if (exitCode == 0) {
            log.info("✅ Backup créé: {}", backupFile);
            return backupFile.toString();
        } else {
            log.error("❌ pg_dump output: {}", output.toString());
            throw new Exception("pg_dump a échoué (code: " + exitCode + "): " + output.toString());
        }
    }

    private String findPgDump() {
        // Chemins possibles pour pg_dump (versions multiples)
        String[] possiblePaths = {
            "/opt/homebrew/opt/postgresql@15/bin/pg_dump",
            "/opt/homebrew/opt/postgresql@16/bin/pg_dump",
            "/opt/homebrew/opt/postgresql@17/bin/pg_dump",
            "/usr/local/opt/postgresql@15/bin/pg_dump",
            "/usr/local/opt/postgresql@16/bin/pg_dump",
            "/Library/PostgreSQL/15/bin/pg_dump",
            "/Library/PostgreSQL/16/bin/pg_dump"
        };

        for (String path : possiblePaths) {
            if (new File(path).exists()) {
                return path;
            }
        }

        // Fallback: utiliser pg_dump du PATH
        return "pg_dump";
    }
}
