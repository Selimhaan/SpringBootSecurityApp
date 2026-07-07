package com.example.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    private final String uploadDir = "uploads";

    public FileStorageService() {
        try {
            Files.createDirectories(Paths.get(uploadDir));
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory!");
        }
    }

    public String storeFile(MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            // Generate unique file name to avoid collisions
            String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
            Path filePath = Paths.get(uploadDir, uniqueFileName);
            
            Files.copy(file.getInputStream(), filePath);
            return filePath.toString();
        } catch (IOException e) {
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
    }

    public void deleteFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException("Could not delete the file. Error: " + e.getMessage());
        }
    }

    public byte[] readFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new RuntimeException("Could not read the file. Error: " + e.getMessage());
        }
    }
}
