# Инструкция по пересозданию базы данных

## Проблема
Если при регистрации возникает ошибка "повторяющееся значение ключа", это означает, что sequence (автоинкремент) не синхронизирован с реальными данными в таблице.

## Решение

### Вариант 1: Полное пересоздание БД (рекомендуется)

1. Подключитесь к PostgreSQL:
   ```bash
   psql -U postgres -d car_rental_1
   ```
   (или используйте pgAdmin / DBeaver)

2. Выполните скрипт пересоздания:
   ```sql
   \i src/main/resources/db/recreate_database.sql
   ```
   
   Или скопируйте содержимое файла `recreate_database.sql` и выполните в SQL-редакторе.

3. Перезапустите приложение.

### Вариант 2: Исправление sequence без удаления данных

Если нужно сохранить существующие данные, выполните:

```sql
-- Исправить sequence для всех таблиц
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));
SELECT setval('roles_id_seq', (SELECT MAX(id) FROM roles));
SELECT setval('vehicles_id_seq', (SELECT MAX(id) FROM vehicles));
SELECT setval('rentals_id_seq', (SELECT MAX(id) FROM rentals));
SELECT setval('payments_id_seq', (SELECT MAX(id) FROM payments));
SELECT setval('reviews_id_seq', (SELECT MAX(id) FROM reviews));
SELECT setval('refresh_tokens_id_seq', (SELECT MAX(id) FROM refresh_tokens));
```

## Тестовые пользователи

После выполнения скрипта будут созданы следующие пользователи:

| Логин | Пароль | Роль | Описание |
|-------|--------|------|----------|
| `admin` | `password` | ADMIN | Администратор системы |
| `manager` | `password` | MANAGER | Менеджер |
| `client1` | `password` | CLIENT | Клиент 1 |
| `client2` | `password` | CLIENT | Клиент 2 |

**Важно:** Пароль для всех тестовых пользователей: `password`

## Тестовые данные

Скрипт также создает:
- 5 автомобилей (Toyota, Tesla, BMW, Mercedes, Audi)
- 3 аренды (2 активные, 1 завершенная)
- 3 платежа
- 1 отзыв

## Проверка

После выполнения скрипта проверьте:

```sql
-- Проверить количество записей
SELECT 'users' as table_name, COUNT(*) as count FROM users
UNION ALL
SELECT 'vehicles', COUNT(*) FROM vehicles
UNION ALL
SELECT 'rentals', COUNT(*) FROM rentals
UNION ALL
SELECT 'payments', COUNT(*) FROM payments;

-- Проверить sequence
SELECT last_value FROM users_id_seq;
```


