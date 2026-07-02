package com.example.demo.service;

import com.example.demo.entity.Role;
import com.example.demo.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public Role createRole(String name) {
        if (roleRepository.findByName(name).isPresent()) {
            throw new RuntimeException("Role already exists: " + name);
        }
        Role role = Role.builder().name(name).build();
        return roleRepository.save(role);
    }

    public Optional<Role> findByName(String name) {
        return roleRepository.findByName(name);
    }
}


// Flyway veya Liquibase: Büyük ölçekli ve kurumsal şirket projelerinde, veritabanına
// ilk veriyi atmak veya tablo yapılarını güncellemek için Java kodu (CommandLineRunner)
// değil, Flyway veya Liquibase adında veritabanı versiyon kontrol sistemleri
// (Migration toolları) kullanılır. Bu sistemler, SQL dosyalarını sırayla okuyarak
// veritabanını günceller.