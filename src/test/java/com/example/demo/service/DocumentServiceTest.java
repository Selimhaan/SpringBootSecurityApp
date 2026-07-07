package com.example.demo.service;

import com.example.demo.dto.DocumentResponseDto;
import com.example.demo.entity.Document;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.DocumentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DocumentServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private DocumentService documentService;

    private User user;
    private Document document;

    @BeforeEach
    void setUp() {
        Role role = new Role();
        role.setName("EMPLOYEE");
        
        user = new User();
        user.setId(1L);
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail("user@test.com");
        user.setRole(role);

        document = new Document();
        document.setId(1L);
        document.setName("test.pdf");
        document.setOwner(user);
    }

    @Test
    void testUploadDocument() {
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn("test.pdf");
        when(mockFile.getContentType()).thenReturn("application/pdf");
        when(mockFile.getSize()).thenReturn(1024L);

        when(fileStorageService.storeFile(mockFile)).thenReturn("/uploads/test.pdf");
        when(documentRepository.save(any(Document.class))).thenReturn(document);

        DocumentResponseDto savedDoc = documentService.uploadDocument(mockFile, user);

        assertEquals("test.pdf", savedDoc.getName());
        verify(documentRepository).save(any(Document.class));
    }

    @Test
    void testGetDocumentsForEmployee() {
        when(documentRepository.findByOwner(user)).thenReturn(List.of(document));

        List<DocumentResponseDto> docs = documentService.getDocuments(user);

        assertEquals(1, docs.size());
        assertEquals("test.pdf", docs.get(0).getName());
    }

    @Test
    void testGetDocumentEntityById() {
        when(documentRepository.findById(1L)).thenReturn(Optional.of(document));

        Document doc = documentService.getDocumentEntityById(1L);

        assertEquals("test.pdf", doc.getName());
    }
}
