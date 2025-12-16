-- Скрипт для полного пересоздания базы данных
-- ВНИМАНИЕ: Этот скрипт удалит все данные!

-- Удаление всех таблиц (в правильном порядке из-за внешних ключей)
DROP TABLE IF EXISTS refresh_tokens CASCADE;
DROP TABLE IF EXISTS reviews CASCADE;
DROP TABLE IF EXISTS payments CASCADE;
DROP TABLE IF EXISTS rentals CASCADE;
DROP TABLE IF EXISTS vehicles CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS roles CASCADE;

-- Удаление последовательностей
DROP SEQUENCE IF EXISTS roles_id_seq CASCADE;
DROP SEQUENCE IF EXISTS users_id_seq CASCADE;
DROP SEQUENCE IF EXISTS vehicles_id_seq CASCADE;
DROP SEQUENCE IF EXISTS rentals_id_seq CASCADE;
DROP SEQUENCE IF EXISTS payments_id_seq CASCADE;
DROP SEQUENCE IF EXISTS reviews_id_seq CASCADE;
DROP SEQUENCE IF EXISTS refresh_tokens_id_seq CASCADE;

-- ==========================================
-- СОЗДАНИЕ ТАБЛИЦ
-- ==========================================

-- Roles
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    role_name VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Users
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    role_id BIGINT NOT NULL REFERENCES roles(id) ON DELETE RESTRICT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_role_id ON users(role_id);

-- Vehicles
CREATE TABLE vehicles (
    id BIGSERIAL PRIMARY KEY,
    license_plate VARCHAR(20) UNIQUE NOT NULL,
    manufacturer VARCHAR(100) NOT NULL,
    model VARCHAR(100) NOT NULL,
    year INTEGER NOT NULL,
    daily_price DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) DEFAULT 'AVAILABLE',
    color VARCHAR(50),
    mileage DECIMAL(10,2) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT valid_status CHECK (status IN ('AVAILABLE','RENTED','MAINTENANCE')),
    CONSTRAINT valid_price CHECK (daily_price > 0)
);

CREATE INDEX idx_vehicles_status ON vehicles(status);
CREATE INDEX idx_vehicles_price ON vehicles(daily_price);

-- Rentals
CREATE TABLE rentals (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    vehicle_id BIGINT NOT NULL REFERENCES vehicles(id) ON DELETE RESTRICT,
    manager_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT rental_status CHECK (status IN ('ACTIVE','COMPLETED','CANCELLED')),
    CONSTRAINT rental_dates CHECK (end_date >= start_date)
);

CREATE INDEX idx_rentals_user ON rentals(user_id);
CREATE INDEX idx_rentals_vehicle ON rentals(vehicle_id);
CREATE INDEX idx_rentals_status ON rentals(status);

-- Payments
CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    rental_id BIGINT NOT NULL REFERENCES rentals(id) ON DELETE CASCADE,
    amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING' NOT NULL,
    method VARCHAR(50),
    paid_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT payment_status CHECK (status IN ('PENDING','PAID','FAILED','REFUNDED'))
);

CREATE INDEX idx_payments_rental ON payments(rental_id);
CREATE INDEX idx_payments_status ON payments(status);

-- Reviews
CREATE TABLE reviews (
    id BIGSERIAL PRIMARY KEY,
    rental_id BIGINT NOT NULL REFERENCES rentals(id) ON DELETE CASCADE,
    rating INTEGER NOT NULL,
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT valid_rating CHECK (rating BETWEEN 1 AND 5)
);

CREATE INDEX idx_reviews_rental ON reviews(rental_id);

-- Refresh tokens
CREATE TABLE refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token VARCHAR(512) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    revoked BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_refresh_user ON refresh_tokens(user_id);

-- ==========================================
-- ТЕСТОВЫЕ ДАННЫЕ
-- ==========================================

-- Роли
INSERT INTO roles (id, role_name, description) VALUES
(1, 'ADMIN', 'Полный доступ, управление системой'),
(2, 'MANAGER', 'Управление арендами и платежами'),
(3, 'CLIENT', 'Просмотр каталога и создание аренд');

-- Сброс sequence для roles
SELECT setval('roles_id_seq', (SELECT MAX(id) FROM roles));

-- Пользователи
-- Пароль для всех: "password" (BCrypt хэш, strength=10)
-- ВАЖНО: Если этот хэш не работает, сгенерируйте новый через приложение
-- или используйте DataInitializer, который создаст админа автоматически
INSERT INTO users (id, username, email, password, first_name, last_name, role_id, is_active) VALUES
(1, 'admin', 'admin@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Admin', 'User', 1, TRUE),
(2, 'manager', 'manager@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Мария', 'Менеджер', 2, TRUE),
(3, 'client1', 'client1@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Иван', 'Клиентов', 3, TRUE),
(4, 'client2', 'client2@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Анна', 'Покупатель', 3, TRUE);

-- ПРИМЕЧАНИЕ: Если пароль "password" не работает, попробуйте:
-- 1. Удалить всех пользователей: DELETE FROM users;
-- 2. Перезапустить приложение - DataInitializer создаст админа с правильным хэшем
-- 3. Или обновить пароль вручную через UPDATE users SET password = 'новый_хэш' WHERE username = 'admin';

-- Сброс sequence для users
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));

-- Автомобили
INSERT INTO vehicles (id, license_plate, manufacturer, model, year, daily_price, status, color, mileage) VALUES
(1, 'A111AA', 'Toyota', 'Camry', 2022, 70.00, 'AVAILABLE', 'Белый', 15000.00),
(2, 'B222BB', 'Tesla', 'Model 3', 2023, 120.00, 'AVAILABLE', 'Серый', 5000.00),
(3, 'C333CC', 'BMW', 'X3', 2021, 110.00, 'MAINTENANCE', 'Черный', 32000.00),
(4, 'D444DD', 'Mercedes', 'C-Class', 2023, 130.00, 'AVAILABLE', 'Серебристый', 8000.00),
(5, 'E555EE', 'Audi', 'A4', 2022, 115.00, 'AVAILABLE', 'Синий', 12000.00);

-- Сброс sequence для vehicles
SELECT setval('vehicles_id_seq', (SELECT MAX(id) FROM vehicles));

-- Аренды
INSERT INTO rentals (id, user_id, vehicle_id, manager_id, status, start_date, end_date, total_price) VALUES
(1, 3, 1, 2, 'ACTIVE', '2025-12-10', '2025-12-15', 420.00),
(2, 4, 2, 2, 'COMPLETED', '2025-11-01', '2025-11-05', 600.00),
(3, 3, 4, 2, 'ACTIVE', '2025-12-12', '2025-12-20', 1040.00);

-- Сброс sequence для rentals
SELECT setval('rentals_id_seq', (SELECT MAX(id) FROM rentals));

-- Платежи
INSERT INTO payments (id, rental_id, amount, status, method, paid_at) VALUES
(1, 1, 420.00, 'PENDING', 'CARD', NULL),
(2, 2, 600.00, 'PAID', 'CARD', '2025-11-01 10:30:00'),
(3, 3, 1040.00, 'PAID', 'CARD', '2025-12-12 14:20:00');

-- Сброс sequence для payments
SELECT setval('payments_id_seq', (SELECT MAX(id) FROM payments));

-- Отзывы
INSERT INTO reviews (id, rental_id, rating, comment) VALUES
(1, 2, 5, 'Отличный авто и обслуживание! Всё было на высшем уровне.');

-- Сброс sequence для reviews
SELECT setval('reviews_id_seq', (SELECT MAX(id) FROM reviews));

-- ==========================================
-- ИНФОРМАЦИЯ О ТЕСТОВЫХ ПОЛЬЗОВАТЕЛЯХ
-- ==========================================
-- Админ:     admin / password
-- Менеджер:  manager / password
-- Клиент 1:  client1 / password
-- Клиент 2:  client2 / password
