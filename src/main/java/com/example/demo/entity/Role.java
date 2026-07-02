package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles")
// @Getter ve @Setter: Java'daki klasik getName(), setName() gibi metotları otomatik yazar.
@Getter
@Setter
// @NoArgsConstructor: Sınıfın boş (parametresiz) bir oluşturucusunu (Constructor) yazar.
// Hibernate'in çalışması için bu zorunludur.
@NoArgsConstructor
// @AllArgsConstructor: Tüm alanları içeren (ad, soyad, email vb.) bir constructor yazar.
@AllArgsConstructor
// @Builder: Nesneleri oluştururken new User(1, "Ali", ...) yerine
// User.builder().firstName("Ali").build() şeklinde çok daha okunaklı
// ve esnek nesneler oluşturmamızı sağlar. (Kodda UserController içinde
// bunu kullandık).
@Builder
public class Role {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String name;
}
