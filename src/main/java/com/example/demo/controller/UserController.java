package com.example.demo.controller;

import com.example.demo.dto.UserCreateDto;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // @RequestBody gelen veriyi de-serialization eder.
    // Yani bizim oluşturduğumuz dto formatına getirir


    // controller uygulamanın dış dünyayla iletişim kurduğu kapıdır.

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody UserCreateDto dto) {
        User user = User.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .build();
                
        User createdUser = userService.createUser(user, dto.getRole());
        return ResponseEntity.ok(createdUser);
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
}


// Controller ile Service Ayrımı: DTO'yu Entity'ye çevirme işlemini Controller'da
// yaptık. Ancak bazı mimari standartlarda Controller sadece istek alır, dönüşümü
// Service katmanı yapar veya Facade adında yepyeni bir katman bu işi üstlenir.
// (Proje büyüklüğüne göre tercih edilir.)