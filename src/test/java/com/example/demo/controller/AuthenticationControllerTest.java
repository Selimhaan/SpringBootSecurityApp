package com.example.demo.controller;

import com.example.demo.dto.AuthRequest;
import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.TokenRefreshRequest;
import com.example.demo.dto.TokenRefreshResponse;
import com.example.demo.service.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthenticationControllerTest {

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthenticationController authenticationController;

    @Test
    void testAuthenticate() {
        AuthRequest request = new AuthRequest("test@test.com", "password");
        AuthResponse response = new AuthResponse("token", "refresh-token");
        
        when(authenticationService.authenticate(request)).thenReturn(response);

        ResponseEntity<AuthResponse> result = authenticationController.authenticate(request);

        assertEquals(200, result.getStatusCode().value());
        assertEquals("token", result.getBody().getToken());
    }

    @Test
    void testRefreshToken() {
        TokenRefreshRequest request = new TokenRefreshRequest("refresh-token");
        TokenRefreshResponse response = new TokenRefreshResponse("new-token", "refresh-token");

        when(authenticationService.refreshToken(request)).thenReturn(response);

        ResponseEntity<TokenRefreshResponse> result = authenticationController.refreshtoken(request);

        assertEquals(200, result.getStatusCode().value());
        assertEquals("new-token", result.getBody().getAccessToken());
    }
}
