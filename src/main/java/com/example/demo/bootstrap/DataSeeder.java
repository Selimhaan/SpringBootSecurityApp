package com.example.demo.bootstrap;

import com.example.demo.entity.User;
import com.example.demo.service.RoleService;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final RoleService roleService;
    private final UserService userService;

    @Override
    public void run(String... args) throws Exception {
        // Rolleri Oluştur
        createRoleIfNotFound("ADMIN");
        createRoleIfNotFound("MANAGER");
        createRoleIfNotFound("EMPLOYEE");

        // Admin Kullanıcısını Oluştur
        try {
            User admin = User.builder()
                    .firstName("Admin")
                    .lastName("User")
                    .email("admin@staj.com")
                    .password("admin123")
                    .build();
            userService.createUser(admin, "ADMIN");
            System.out.println("Admin user created successfully.");
        } catch (RuntimeException e) {
            System.out.println("Admin user already exists or error: " + e.getMessage());
        }
    }

    private void createRoleIfNotFound(String name) {
        if (roleService.findByName(name).isEmpty()) {
            roleService.createRole(name);
        }
    }
}
