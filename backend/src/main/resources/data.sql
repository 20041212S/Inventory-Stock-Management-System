-- Sample Location Data
INSERT INTO location (location_name, description) VALUES
('Warehouse A', 'Main warehouse facility'),
('Warehouse B', 'Secondary storage facility'),
('Production Floor', 'On-site production storage'),
('Distribution Center', 'Central distribution hub')
ON CONFLICT DO NOTHING;

