package com.example.demo.service;

import com.example.demo.dto.DocumentResponseDto;
import com.example.demo.entity.Document;
import com.example.demo.entity.User;
import com.example.demo.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final FileStorageService fileStorageService;

    public DocumentResponseDto uploadDocument(MultipartFile file, User owner) {
        String filePath = fileStorageService.storeFile(file);

        Document document = Document.builder()
                .name(file.getOriginalFilename())
                .filePath(filePath)
                .contentType(file.getContentType())
                .size(file.getSize())
                .owner(owner)
                .build();

        document = documentRepository.save(document);

        return mapToDto(document);
    }

    public List<DocumentResponseDto> getDocuments(User user) {
        List<Document> documents;

        String role = user.getRole().getName();
        if (role.equals("MANAGER") || role.equals("ADMIN")) {
            documents = documentRepository.findAll();
        } else {
            // ROLE_EMPLOYEE or others
            documents = documentRepository.findByOwner(user);
        }

        return documents.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public Document getDocumentEntityById(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found with id: " + id));
    }

    public byte[] downloadDocument(Long id, User user) {
        Document document = getDocumentEntityById(id);
        
        checkOwnershipOrManager(document, user, "download");

        return fileStorageService.readFile(document.getFilePath());
    }

    public void deleteDocument(Long id, User user) {
        Document document = getDocumentEntityById(id);
        
        checkOwnershipOrManager(document, user, "delete");

        // Delete from file system
        fileStorageService.deleteFile(document.getFilePath());
        
        // Delete from DB
        documentRepository.delete(document);
    }
    
    private void checkOwnershipOrManager(Document document, User user, String action) {
        String role = user.getRole().getName();
        boolean isOwner = document.getOwner().getId().equals(user.getId());
        boolean isManagerOrAdmin = role.equals("MANAGER") || role.equals("ADMIN");

        if (!isOwner && !isManagerOrAdmin) {
            throw new RuntimeException("You do not have permission to " + action + " this document");
        }
    }

    private DocumentResponseDto mapToDto(Document document) {
        return DocumentResponseDto.builder()
                .id(document.getId())
                .name(document.getName())
                .contentType(document.getContentType())
                .size(document.getSize())
                .ownerName(document.getOwner().getFirstName() + " " + document.getOwner().getLastName())
                .build();
    }
}
