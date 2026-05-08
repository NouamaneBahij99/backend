package com.magentatechno.pelican.service;

import com.magentatechno.pelican.exception.BusinessException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    private final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
        "pdf", "doc", "docx", "xls", "xlsx", "jpg", "jpeg", "png", "txt"
    );

    private final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 MB

    private Path uploadPath;

    @PostConstruct
    public void init() {
        try {
            uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);
            log.info("Upload directory initialized: {}", uploadPath);
        } catch (IOException e) {
            throw new RuntimeException("Impossible de créer le dossier d'upload", e);
        }
    }

    public String storeFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("Fichier vide");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException("Fichier trop volumineux (max 10MB)");
        }

        String originalName = sanitizeFileName(file.getOriginalFilename());
        String extension = getExtension(originalName);

        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new BusinessException("Type de fichier non autorisé: " + extension);
        }

        String fileName = UUID.randomUUID() + "." + extension;
        try {
            Path targetLocation = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            log.info("Fichier stocké: {}", fileName);
            return fileName;
        } catch (IOException e) {
            throw new BusinessException("Erreur lors du stockage du fichier", e);
        }
    }

    public void deleteFile(String fileName) {
        try {
            Path filePath = uploadPath.resolve(fileName);
            Files.deleteIfExists(filePath);
            log.info("Fichier supprimé: {}", fileName);
        } catch (IOException e) {
            log.error("Erreur lors de la suppression du fichier: {}", fileName);
        }
    }

    private String sanitizeFileName(String fileName) {
        return fileName.replaceAll("[\\\\/:*?\"<>|]", "_");
    }

    private String getExtension(String fileName) {
        int idx = fileName.lastIndexOf('.');
        return idx > 0 ? fileName.substring(idx + 1) : "";
    }
}
