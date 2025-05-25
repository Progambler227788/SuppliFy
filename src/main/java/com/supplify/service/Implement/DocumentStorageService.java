package com.supplify.service.Implement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
public class DocumentStorageService {
    private static final Logger logger = LoggerFactory.getLogger(DocumentStorageService.class);
    private final Path fileStorageLocation;

    public DocumentStorageService() {
        // Determine base upload directory
        this.fileStorageLocation = Paths.get(System.getProperty("user.dir"), "documentuploads")
            .toAbsolutePath().normalize();
        
        try {
            // Create directories if they don't exist
            Files.createDirectories(this.fileStorageLocation);
            logger.info("Document storage location: {}", this.fileStorageLocation);
        } catch (Exception ex) {
            logger.error("Could not create upload directory", ex);
            throw new RuntimeException("Could not create the directory for uploaded files.", ex);
        }
    }

    /**
     * Generate a unique filename while preserving the original filename
     * @param multipartFile The uploaded file
     * @return Unique filename
     */
    public String generateUniqueFilename(MultipartFile multipartFile) {
        // Get original filename
        String originalFilename = multipartFile.getOriginalFilename();
        
        // Handle null or empty filename
        if (originalFilename == null || originalFilename.isEmpty()) {
            originalFilename = "unnamed_document";
        }
        
        // Clean filename (remove special characters)
        originalFilename = originalFilename.replaceAll("[^a-zA-Z0-9.-]", "_");
        
        // Generate unique prefix
        String prefix = "doc_" + System.currentTimeMillis() + "_";
        
        // Extract file extension
        String fileExtension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex > 0) {
            fileExtension = originalFilename.substring(dotIndex);
            originalFilename = originalFilename.substring(0, dotIndex);
        }
        
        // Truncate filename if too long
        int maxFilenameLength = 50;
        if (originalFilename.length() > maxFilenameLength) {
            originalFilename = originalFilename.substring(0, maxFilenameLength);
        }
        
        // Combine components
        return prefix + originalFilename + fileExtension;
    }

    /**
     * Save uploaded document
     * @param document Multipart file to save
     * @return Relative URL of saved document
     * @throws IOException If file saving fails
     */
    public String saveDocument(MultipartFile document) throws IOException {
        // Validate document
        validateDocument(document);

        // Generate unique filename
        String uniqueFilename = generateUniqueFilename(document);

        // Resolve full file path
        Path targetLocation = this.fileStorageLocation.resolve(uniqueFilename);

        try {
            // Copy file to target location
            Files.copy(document.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            
            logger.info("File saved: {}", targetLocation);
            
            // Return relative URL
            return "/documentuploads/" + uniqueFilename;
        } catch (IOException ex) {
            logger.error("Could not store file " + uniqueFilename, ex);
            throw new IOException("Could not store file " + uniqueFilename, ex);
        }
    }

    /**
     * Validate uploaded document
     * @param document Multipart file to validate
     */
    private void validateDocument(MultipartFile document) {
        // Check if document is empty
        if (document.isEmpty()) {
            throw new IllegalArgumentException("Document is empty");
        }

        // Validate file size (5MB max)
        long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
        if (document.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds maximum limit of 5MB");
        }

        // Validate file type
        String contentType = document.getContentType();
        Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg", 
            "image/png", 
            "image/gif", 
            "application/pdf"
        );

        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Invalid file type. Allowed types: JPEG, PNG, GIF, PDF");
        }
    }
}