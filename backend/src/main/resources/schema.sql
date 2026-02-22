-- This file is for reference only
-- Tables are created automatically by Hibernate using JPA entities
-- Database: PostgreSQL

-- Part Master Table
-- CREATE TABLE part_master (
--     part_number VARCHAR(255) PRIMARY KEY,
--     part_name VARCHAR(255) NOT NULL,
--     make_or_buy VARCHAR(50) NOT NULL,
--     category VARCHAR(100) NOT NULL,
--     subcategory VARCHAR(100),
--     unit_of_measure VARCHAR(50) NOT NULL,
--     drawing_number VARCHAR(255),
--     revision_number VARCHAR(50),
--     revised_date DATE,
--     location_id BIGINT,
--     FOREIGN KEY (location_id) REFERENCES location(location_id)
-- );

-- CREATE INDEX idx_part_number ON part_master(part_number);
-- CREATE INDEX idx_category ON part_master(category);

-- Stock Table
-- CREATE TABLE stock (
--     stock_id BIGSERIAL PRIMARY KEY,
--     part_number VARCHAR(255) NOT NULL,
--     stock_quantity DOUBLE PRECISION NOT NULL,
--     bin_number VARCHAR(100),
--     rack_number VARCHAR(100),
--     last_updated TIMESTAMP NOT NULL,
--     FOREIGN KEY (part_number) REFERENCES part_master(part_number)
-- );

-- CREATE INDEX idx_part_number_stock ON stock(part_number);
-- CREATE INDEX idx_bin_number ON stock(bin_number);
-- CREATE INDEX idx_rack_number ON stock(rack_number);

-- Location Table
-- CREATE TABLE location (
--     location_id BIGSERIAL PRIMARY KEY,
--     location_name VARCHAR(255) UNIQUE NOT NULL,
--     description TEXT
-- );

