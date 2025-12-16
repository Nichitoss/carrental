-- Скрипт для исправления пароля админа
-- Используйте этот скрипт, если пароль admin не работает

-- Вариант 1: Удалить админа и позволить DataInitializer создать его заново
DELETE FROM users WHERE username = 'admin';

-- Затем перезапустите приложение - DataInitializer создаст админа с паролем из application.yml
-- По умолчанию: admin / admin123

-- Вариант 2: Обновить пароль вручную (нужно сгенерировать хэш)
-- Сначала получите хэш через GET /api/auth/generate-hash?password=ваш_пароль
-- Затем выполните:
-- UPDATE users SET password = 'полученный_хэш' WHERE username = 'admin';

-- Вариант 3: Использовать стандартный хэш для "password"
-- UPDATE users SET password = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy' WHERE username = 'admin';
-- Пароль будет: password

-- Проверка текущего админа
SELECT id, username, email, role_id, is_active FROM users WHERE username = 'admin';


