package com.example.demo.repository;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // karmaşık sorgular yazılacaksa ya da performansın çok kritik olduğu
    // bir projede çalışılınıyorsa Hibernate yerine
    // kendi SQL kodlarınızı yazdığınız MyBatis, JOOQ veya Spring'in
    // JdbcTemplate araçları tercih edilebilir.


    // existByEmail boolean döner ve kişinin varlığı kontrol edilir.
    // finByEmail ise kullanıcı bilgisi döner ve eğer kullanıcı yoksa
    // nullpointerException alarak uygulamanın çökmesine sebep olabilir.
    // İşte tamda bu sebepten ötürü optional parametresini kullanıyoruz.
    // Yani veri yoksa patlamamak için.
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
