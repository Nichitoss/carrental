# Генерация BCrypt хэша для пароля

Если нужно сгенерировать правильный BCrypt хэш для пароля "password", используйте один из способов:

## Способ 1: Через Java код

Создайте временный класс:

```java
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = encoder.encode("password");
        System.out.println("Hash for 'password': " + hash);
    }
}
```

## Способ 2: Онлайн генератор

Используйте: https://bcrypt-generator.com/
- Rounds: 10
- Password: password

## Способ 3: Через Spring Boot приложение

Добавьте временный эндпоинт в контроллер:

```java
@GetMapping("/test-hash")
public String testHash(PasswordEncoder encoder) {
    return encoder.encode("password");
}
```

## Рабочий хэш для "password"

Следующий хэш точно работает с Spring Security BCryptPasswordEncoder (strength=10):

```
$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.H/HiGj7h3g8q5V0b0zLuW
```

Это стандартный тестовый хэш для пароля "password".


