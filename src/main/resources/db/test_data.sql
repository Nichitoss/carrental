-- Тестовые данные для Car Rental System
-- Пароль для всех пользователей: "password"
-- BCrypt хэш для "password": $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy

-- Сброс последовательностей (если данные уже есть)
SELECT setval('roles_id_seq', (SELECT MAX(id) FROM roles));
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));
SELECT setval('vehicles_id_seq', (SELECT MAX(id) FROM vehicles));
SELECT setval('rentals_id_seq', (SELECT MAX(id) FROM rentals));
SELECT setval('payments_id_seq', (SELECT MAX(id) FROM payments));
SELECT setval('reviews_id_seq', (SELECT MAX(id) FROM reviews));
SELECT setval('refresh_tokens_id_seq', (SELECT MAX(id) FROM refresh_tokens));

-- Тестовые пользователи (пароль: "password")
-- BCrypt хэш: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
INSERT INTO users (username, email, password, first_name, last_name, role_id, is_active) VALUES
('admin', 'admin@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Admin', 'User', 1, true),
('manager', 'manager@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Мария', 'Менеджер', 2, true),
('client1', 'client1@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Иван', 'Клиентов', 3, true),
('client2', 'client2@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Анна', 'Покупатель', 3, true)
ON CONFLICT (username) DO NOTHING;

-- Тестовые автомобили
INSERT INTO vehicles (license_plate, manufacturer, model, year, daily_price, status, color, mileage) VALUES
('A111AA', 'Toyota', 'Camry', 2022, 70.00, 'AVAILABLE', 'Белый', 15000),
('B222BB', 'Tesla', 'Model 3', 2023, 120.00, 'AVAILABLE', 'Серый', 5000),
('C333CC', 'BMW', 'X3', 2021, 110.00, 'MAINTENANCE', 'Черный', 32000),
('D444DD', 'Mercedes', 'C-Class', 2023, 130.00, 'AVAILABLE', 'Серебристый', 8000),
('E555EE', 'Audi', 'A4', 2022, 115.00, 'AVAILABLE', 'Синий', 12000)
ON CONFLICT (license_plate) DO NOTHING;

-- Тестовые аренды
INSERT INTO rentals (user_id, vehicle_id, manager_id, status, start_date, end_date, total_price) VALUES
(3, 1, 2, 'ACTIVE', '2025-12-10', '2025-12-15', 420.00),
(4, 2, 2, 'COMPLETED', '2025-11-01', '2025-11-05', 600.00),
(3, 4, 2, 'ACTIVE', '2025-12-12', '2025-12-20', 1040.00)
ON CONFLICT DO NOTHING;

-- Тестовые платежи
INSERT INTO payments (rental_id, amount, status, method, paid_at) VALUES
(1, 420.00, 'PENDING', 'CARD', NULL),
(2, 600.00, 'PAID', 'CARD', NOW() - INTERVAL '20 days'),
(3, 1040.00, 'PAID', 'CARD', NOW() - INTERVAL '1 day')
ON CONFLICT DO NOTHING;

-- Тестовые отзывы
INSERT INTO reviews (rental_id, rating, comment) VALUES
(2, 5, 'Отличный авто и обслуживание! Рекомендую!'),
(2, 4, 'Хороший сервис, но можно улучшить')
ON CONFLICT DO NOTHING;

-- Обновление последовательностей после вставки данных
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));
SELECT setval('vehicles_id_seq', (SELECT MAX(id) FROM vehicles));
SELECT setval('rentals_id_seq', (SELECT MAX(id) FROM rentals));
SELECT setval('payments_id_seq', (SELECT MAX(id) FROM payments));
SELECT setval('reviews_id_seq', (SELECT MAX(id) FROM reviews));

