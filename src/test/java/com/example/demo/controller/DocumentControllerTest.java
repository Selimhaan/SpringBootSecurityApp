package com.example.demo.controller;

import com.example.demo.dto.DocumentResponseDto;
import com.example.demo.entity.User;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.service.DocumentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DocumentControllerTest {

    @Mock
    private DocumentService documentService;

    @InjectMocks
    private DocumentController documentController;

    private User mockUser;
    private CustomUserDetails userDetails;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        userDetails = new CustomUserDetails(mockUser);
    }

    @Test
    void testUploadDocument() {
        MultipartFile mockFile = mock(MultipartFile.class);
        DocumentResponseDto mockResponse = DocumentResponseDto.builder().name("test.pdf").build();

        when(documentService.uploadDocument(mockFile, mockUser)).thenReturn(mockResponse);

        ResponseEntity<DocumentResponseDto> result = documentController.uploadDocument(mockFile, userDetails);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("test.pdf", result.getBody().getName());
    }

    @Test
    void testGetDocuments() {
        when(documentService.getDocuments(mockUser)).thenReturn(List.of(DocumentResponseDto.builder().name("doc").build()));

        ResponseEntity<List<DocumentResponseDto>> result = documentController.getDocuments(userDetails);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
    }

    @Test
    void testDownloadDocument() {
        when(documentService.downloadDocument(1L, mockUser)).thenReturn(new byte[]{1, 2, 3});

        ResponseEntity<byte[]> result = documentController.downloadDocument(1L, userDetails);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(3, result.getBody().length);
    }

    @Test
    void testDeleteDocument() {
        doNothing().when(documentService).deleteDocument(1L, mockUser);

        ResponseEntity<String> result = documentController.deleteDocument(1L, userDetails);

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }
}
