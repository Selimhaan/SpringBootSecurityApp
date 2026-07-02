package com.example.demo.dto;

import lombok.Data;
import java.util.Set;

// entity yerine dto kullanımının temel sebebi güvenliktir. Veritabanıyla
// kullanıcı arasına ara bir bariyer olur ve kötü niyetli insanların veritabanımızı
// süistimal etmesi engellenir.

// yanlızca işlem yapılmak istenen yapıların aktarımı sağlanır, riskli verilere erişim
// kısıtlanır.

@Data
public class UserCreateDto {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String role;
}

//Soru: "Çalışma senaryosu ve işleyişi nedir?" Cevap:
//
//İstemci (React/Postman): Bize JSON formatında { "firstName": "Ali",
// "email": "ali@mail.com" } gönderir.
//Controller: İçindeki @RequestBody UserCreateDto dto kısmı sayesinde,
// dışarıdan gelen o JSON metnini alır ve içini açarak DTO objesine çevirir.
//Amaç: Neden direkt User (Entity) yapmıyoruz? Çünkü Entity'de id,
// password gibi alanlar var. Kullanıcı JSON içine "id": 1 yazıp gönderseydi,
// mevcut 1 numaralı kullanıcıyı ezebilirdi! DTO ile sadece "bana şu, şu,
// şu alanları yollayabilirsin" diyerek güvenli bir filtre oluşturuyoruz.