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

        // Manager Kullanıcısını Oluştur
        try {
            User manager = User.builder()
                    .firstName("Manager")
                    .lastName("User")
                    .email("manager@staj.com")
                    .password("manager123")
                    .build();
            userService.createUser(manager, "MANAGER");
            System.out.println("Manager user created successfully.");
        } catch (RuntimeException e) {
            System.out.println("Manager user already exists or error: " + e.getMessage());
        }

        // Employee Kullanıcısını Oluştur
        try {
            User employee = User.builder()
                    .firstName("Employee")
                    .lastName("User")
                    .email("employee@staj.com")
                    .password("employee123")
                    .build();
            userService.createUser(employee, "EMPLOYEE");
            System.out.println("Employee user created successfully.");
        } catch (RuntimeException e) {
            System.out.println("Employee user already exists or error: " + e.getMessage());
        }
    }

    private void createRoleIfNotFound(String name) {
        if (roleService.findByName(name).isEmpty()) {
            roleService.createRole(name);
        }
    }
}
