package com.example.demo.service;

import com.example.demo.entity.RefreshToken;
import com.example.demo.repository.RefreshTokenRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.CacheEvict;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    // verifyException metodumuz @CacheEvict anotasyonuna sahip deleteToken fonksiyonunu
    // çağırıyordu normal şartlar altında anotasyon içeren bir servisimiz başka bir servis
    // tarafından çağrıldığında araya proxy (vekil) girer ve anotasyonda belirtilen görevi
    // yerine getirir. Fakat anotasyonlu fonksiyon aynı class yapısı içerisinde bulunan başka
    // bir fonksiyon/servis tarafından çağırılırsa proxy devreye girmez çünkü proxy sınıf bazlıdır
    // bu sorunu çözmek için sınıf içerisine lazy parametresi yazılır. Böylece proxy sınıf içi
    // olarakta çalışabilir.


    // Araya Vekil'in (Proxy) girmesini zorunlu kılmak için, kendi servisimizin bir vekil
    // kopyasını kendi içimize enjekte ettik: @Lazy private RefreshTokenService self;
    // Böylece kodu self.deleteToken(token) diye değiştirdik. Artık çağrı "kendi içinden"
    // değil de, sanki "dışarıdaki o vekil kopyadan" yapılmış gibi davranıyor ve
    // @CacheEvict devreye giriyor. (Not: @Lazy demeseydik "Dairesel
    // Bağımlılık - Circular Dependency" hatası alırdık çünkü sistem kendini oluşturmak
    // için henüz oluşmamış kendini bekleyecekti).

    @org.springframework.context.annotation.Lazy
    @org.springframework.beans.factory.annotation.Autowired
    // Burası
    private RefreshTokenService self;

    // Default to 7 days
    @Value("${jwt.refresh.expiration:604800000}")
    private Long refreshTokenDurationMs;

    @Cacheable(value = "refreshTokens", key = "#token", unless = "#result == null")
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Transactional
    @CachePut(value = "refreshTokens", key = "#result.token")
    public RefreshToken createRefreshToken(Long userId) {
        RefreshToken refreshToken = new RefreshToken();
        
        refreshToken.setUser(userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found")));
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString());

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            self.deleteToken(token);
            throw new RuntimeException("Refresh token was expired. Please make a new signin request");
        }
        return token;
    }

    @CacheEvict(value = "refreshTokens", key = "#token.token")
    public void deleteToken(RefreshToken token) {
        refreshTokenRepository.delete(token);
    }

    @Transactional
    public int deleteByUserId(Long userId) {
        java.util.List<RefreshToken> tokens = refreshTokenRepository.findAllByUser_Id(userId);
        for (RefreshToken token : tokens) {
            self.deleteToken(token);
        }
        return tokens.size();
    }
}
