package com.example.demo.security;

import com.example.demo.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (user.getRole() == null) {
            return Collections.emptyList();
        }
        
        String roleName = user.getRole().getName();
        if (!roleName.startsWith("ROLE_")) {
            roleName = "ROLE_" + roleName;
        }
        return Collections.singleton(new SimpleGrantedAuthority(roleName));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail(); // Kullanıcı adı olarak email kullanıyoruz
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}


// Spring Security kendi içinde güvenlik bağlamında User değil,
// UserDetails adında bir interface (arayüz) ile konuşur. Biz de kendi
// Veritabanı Entity'mizi (User.java) alıp Spring Security'nin diline çevirdik
// (Adaptör pattern). Kullanıcının e-postasını username olarak, Rolünü ise
// GrantedAuthority olarak Spring'e bildirdik.


// Entity User.java sınıfımıza doğrudan UserDetails interface'i implement edilebilirdi
// (böylece CustomUserDetails oluşturmaya gerek kalmazdı). Ancak Katmanlı mimarilerde
// Veritabanı sınıfını, Güvenlik kütüphanelerine (Spring Security) bağımlı yapmamak
// "Clean Architecture" prensiplerine daha uygundur. O yüzden bu şekilde ayırdık.