package com.example.demo.controller;

import com.example.demo.dto.DocumentResponseDto;
import com.example.demo.entity.User;
import com.example.demo.service.DocumentService;
import lombok.RequiredArgsConstructor;
import com.example.demo.security.CustomUserDetails;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentResponseDto> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        return ResponseEntity.ok(documentService.uploadDocument(file, userDetails.getUser()));
    }

    @GetMapping
    public ResponseEntity<List<DocumentResponseDto>> getDocuments(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(documentService.getDocuments(userDetails.getUser()));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadDocument(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        byte[] fileData = documentService.downloadDocument(id, userDetails.getUser());
        
        // Return file as attachment (simplified content type, could be dynamic)
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"document\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(fileData);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDocument(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        documentService.deleteDocument(id, userDetails.getUser());
        return ResponseEntity.ok("Document deleted successfully");
    }
}
