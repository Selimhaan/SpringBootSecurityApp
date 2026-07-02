package com.example.demo.service;

import com.example.demo.dto.AuthRequest;
import com.example.demo.dto.AuthResponse;
import com.example.demo.security.CustomUserDetailsService;
import com.example.demo.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;

    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String jwtToken = jwtService.generateToken(userDetails);
        
        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }
}


// Neden Session Değil de JWT Seçtik?
//
//Stateless (Durumsuz): Sunucu, kullanıcıların oturum bilgilerini
// RAM'inde (Session) tutmak zorunda kalmaz. RAM tüketimi azalır.
//Ölçeklenebilirlik (Scalability): Projeyi Docker ile 5 farklı sunucuya
// kopyaladığımızda, kullanıcı hangi sunucuya giderse gitsin, token kendi
// kendini doğrulayabildiği için "Session bulunamadı" hatası almaz.
//Mobil Uyumluluk: Mobil uygulamalar (iOS/Android) Session Cookie mekanizması
// ile iyi çalışmazlar, JWT (Bearer Token) standarttır.