package com.example.demo.service;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    // uygulamanın beynidir. Veri geldikten sonra veritabanına gitmeden önce
    // ne yapılması gerektiğine dair iş akışı burada kurulur

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    public User createUser(User user, String roleName) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already in use: " + user.getEmail());
        }

        if (roleName != null && !roleName.trim().isEmpty()) {
            Role role = roleService.findByName(roleName)
                    .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
            user.setRole(role);
        }
        
        // Şifreyi kaydetmeden önce BCrypt ile hashliyoruz
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
