package com.example.demo.service;

import com.example.demo.dto.AuthRequest;
import com.example.demo.dto.AuthResponse;
import com.example.demo.security.CustomUserDetailsService;
import com.example.demo.security.JwtService;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.entity.RefreshToken;
import com.example.demo.dto.TokenRefreshRequest;
import com.example.demo.dto.TokenRefreshResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;

    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
                
        String jwtToken = jwtService.generateToken(extraClaims, userDetails);
        
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
                
        // Eğer varsa eskisini silip yeni üretmek iyi bir strateji olabilir.
        refreshTokenService.deleteByUserId(user.getId());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());
        
        return AuthResponse.builder()
                .token(jwtToken)
                .refreshToken(refreshToken.getToken())
                .build();
    }
    
    public TokenRefreshResponse refreshToken(TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
                    
                    Map<String, Object> extraClaims = new HashMap<>();
                    extraClaims.put("roles", userDetails.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.toList()));
                            
                    String token = jwtService.generateToken(extraClaims, userDetails);
                    return new TokenRefreshResponse(token, requestRefreshToken);
                })
                .orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
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