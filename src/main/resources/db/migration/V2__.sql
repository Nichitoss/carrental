ALTER TABLE rentals
DROP
CONSTRAINT rentals_client_id_fkey;

ALTER TABLE reviews
DROP
CONSTRAINT reviews_client_id_fkey;

ALTER TABLE rentals
    ADD user_id BIGINT;

ALTER TABLE rentals
    ALTER COLUMN user_id SET NOT NULL;

ALTER TABLE rentals
    ADD CONSTRAINT FK_RENTALS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE rentals
DROP
COLUMN actual_return_date;

ALTER TABLE rentals
DROP
COLUMN userId;

ALTER TABLE rentals
DROP
COLUMN notes;

ALTER TABLE reviews
DROP
COLUMN client_id;

ALTER TABLE refresh_tokens
DROP
COLUMN revoked;

ALTER TABLE payments
ALTER
COLUMN amount TYPE DECIMAL(10, 2) USING (amount::DECIMAL(10, 2));

ALTER TABLE vehicles
DROP
COLUMN mileage;

ALTER TABLE vehicles
    ADD mileage DECIMAL(10, 2);

ALTER TABLE payments
    ALTER COLUMN status SET NOT NULL;

ALTER TABLE rentals
    ALTER COLUMN status SET NOT NULL;

ALTER TABLE vehicles
    ALTER COLUMN status SET NOT NULL;

ALTER TABLE rentals
ALTER
COLUMN total_price TYPE DECIMAL(10, 2) USING (total_price::DECIMAL(10, 2));

ALTER TABLE rentals
    ALTER COLUMN total_price SET NOT NULL;