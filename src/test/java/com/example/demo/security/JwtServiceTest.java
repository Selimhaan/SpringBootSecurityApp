package com.example.demo.security;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JwtServiceTest {

    private JwtService jwtService;
    private CustomUserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 86400000L); // 1 day

        Role role = new Role();
        role.setName("EMPLOYEE");
        
        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("password");
        user.setRole(role);
        
        userDetails = new CustomUserDetails(user);
    }

    @Test
    void testGenerateAndValidateToken() {
        String token = jwtService.generateToken(userDetails);
        
        assertTrue(token != null && !token.isEmpty());
        
        String username = jwtService.extractUsername(token);
        assertEquals("test@test.com", username);
        
        assertTrue(jwtService.isTokenValid(token, userDetails));
    }
}
