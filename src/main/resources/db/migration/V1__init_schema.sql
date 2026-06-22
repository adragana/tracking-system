CREATE TABLE users (
       id BIGSERIAL PRIMARY KEY,
       name VARCHAR(150) NOT NULL,
       email VARCHAR(150) NOT NULL UNIQUE,
       phone VARCHAR(50),
       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE shipments (
       id BIGSERIAL PRIMARY KEY,
       tracking_number VARCHAR(50) NOT NULL UNIQUE,
       description VARCHAR(500) NOT NULL,
       status VARCHAR(30) NOT NULL DEFAULT 'CREATED',
       user_id BIGINT NOT NULL,
       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
       updated_at TIMESTAMP NOT NULL,
       FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE shipment_status_history (
       id BIGSERIAL PRIMARY KEY,
       shipment_id BIGINT NOT NULL,
       previous_status VARCHAR(30),
       new_status VARCHAR(30) NOT NULL,
       note VARCHAR(600),
       changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
       FOREIGN KEY (shipment_id) REFERENCES shipments(id)
);
