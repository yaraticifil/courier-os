-- CREATIVE ELEPHANT // COURIER OS
-- DATABASE INITIALIZATION & SPATIAL SCHEMA SETUP

-- 1. Enable Spatial Extensions
CREATE EXTENSION IF NOT EXISTS postgis;

-- 2. Clean Existing Tables
DROP TABLE IF EXISTS orders CASCADE;
DROP TABLE IF EXISTS couriers CASCADE;
DROP TABLE IF EXISTS telemetry_logs CASCADE;

-- 3. Create Couriers Spatial Table
CREATE TABLE couriers (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    zone VARCHAR(50) NOT NULL,
    is_busy BOOLEAN DEFAULT FALSE,
    geom GEOMETRY(Point, 4326), -- Spatial Point Coordinate
    delivery_count INTEGER DEFAULT 0,
    total_earnings NUMERIC(12, 2) DEFAULT 0.00,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create Spatial Index for high-performance geospatial queries (Redis GEO backup)
CREATE INDEX idx_couriers_geom ON couriers USING GIST(geom);

-- 4. Create Orders Spatial Table
CREATE TABLE orders (
    id VARCHAR(50) PRIMARY KEY,
    pickup_zone VARCHAR(50) NOT NULL,
    dropoff_zone VARCHAR(50) NOT NULL,
    pickup_geom GEOMETRY(Point, 4326) NOT NULL,
    dropoff_geom GEOMETRY(Point, 4326) NOT NULL,
    assigned_courier_id VARCHAR(50) REFERENCES couriers(id) ON DELETE SET NULL,
    status VARCHAR(20) DEFAULT 'pending' CHECK (status IN ('pending', 'transit', 'delivered')),
    eta INTEGER DEFAULT 0,
    base_payout NUMERIC(10, 2) DEFAULT 50.00,
    multiplier NUMERIC(4, 2) DEFAULT 1.00,
    total_payout NUMERIC(10, 2) DEFAULT 50.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    assigned_at TIMESTAMP,
    delivered_at TIMESTAMP
);

-- Create Spatial Indexes for pickups and dropoffs
CREATE INDEX idx_orders_pickup_geom ON orders USING GIST(pickup_geom);
CREATE INDEX idx_orders_dropoff_geom ON orders USING GIST(dropoff_geom);

-- 5. Create Telemetry Logs Table
CREATE TABLE telemetry_logs (
    id SERIAL PRIMARY KEY,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ticks INTEGER NOT NULL,
    avg_delivery_time NUMERIC(8, 2),
    courier_idle_ticks INTEGER,
    zone_imbalance_index NUMERIC(5, 2),
    delay_rate NUMERIC(5, 2),
    metrics JSONB NOT NULL
);

-- 6. Populate Initial Courier Nodes (Matching System State)
INSERT INTO couriers (id, name, zone, is_busy, geom, delivery_count, total_earnings) VALUES
('C10', 'Mert Kaya', 'Kadıköy', FALSE, ST_SetSRID(ST_MakePoint(29.027, 40.991), 4326), 0, 0.00),
('C11', 'Ahmet Yurt', 'Kadıköy', FALSE, ST_SetSRID(ST_MakePoint(29.029, 40.992), 4326), 0, 0.00),
('C12', 'Selin Kılıç', 'Beşiktaş', FALSE, ST_SetSRID(ST_MakePoint(29.008, 41.042), 4326), 0, 0.00),
('C13', 'Caner Demir', 'Beşiktaş', FALSE, ST_SetSRID(ST_MakePoint(29.006, 41.041), 4326), 0, 0.00),
('C14', 'Burak Tunç', 'Üsküdar', FALSE, ST_SetSRID(ST_MakePoint(29.015, 40.988), 4326), 0, 0.00),
('C15', 'Hale Gümüş', 'Üsküdar', FALSE, ST_SetSRID(ST_MakePoint(29.016, 40.989), 4326), 0, 0.00);

-- 7. Add Verification Sample Query Function
-- Finds the closest available courier to a given coordinates (e.g., Beşiktaş pickup)
-- SELECT id, name, ST_Distance(geom, ST_SetSRID(ST_MakePoint(29.008, 41.042), 4326)::geography) as distance_meters FROM couriers WHERE is_busy = FALSE ORDER BY distance_meters LIMIT 1;
