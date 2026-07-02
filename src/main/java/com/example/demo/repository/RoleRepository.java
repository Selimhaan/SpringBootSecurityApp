package com.example.demo.repository;

import com.example.demo.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


// Spring Data JPAi JpaRepository'den kalıtım aldığımız için bizim için en temel
// CRUD metotlarını otomatik yazar

// isimden çıkarım yapar.

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
