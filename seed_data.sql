-- ============================================================
-- EVENT MANAGEMENT SYSTEM - MYSQL SEED DATA SCRIPT
-- Database: event_management
-- Execute this script in MySQL Workbench or via MySQL CLI
-- ============================================================

USE event_management;

-- 1. Insert Initial Users
-- Passwords below are BCrypt encoded:
-- admin@eventpro.com / admin123
-- organizer@eventpro.com / org123
-- user@eventpro.com / user123
INSERT INTO users (id, name, email, password, role, phone, joined_at, avatar) VALUES
(1, 'System Administrator', 'admin@eventpro.com', '$2a$10$wO8/dO4e96iSm5EAGwA.x.kG1K5YtZ2gZ2.8vVzR4X9Oq9Oq9Oq9O', 'admin', '+91 98765 43210', '2025-01-10', NULL),
(2, 'Apex Event Management', 'organizer@eventpro.com', '$2a$10$wO8/dO4e96iSm5EAGwA.x.kG1K5YtZ2gZ2.8vVzR4X9Oq9Oq9Oq9O', 'organizer', '+91 98765 43211', '2025-02-01', NULL),
(3, 'Rahul Sharma', 'user@eventpro.com', '$2a$10$wO8/dO4e96iSm5EAGwA.x.kG1K5YtZ2gZ2.8vVzR4X9Oq9Oq9Oq9O', 'user', '+91 98765 43212', '2025-03-15', NULL)
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- 2. Insert Sample Events
INSERT INTO events (id, title, category, date, end_date, time, venue, location, description, capacity, registered, price, early_bird_price, early_bird_deadline, thumbnail, organizer_id, status, rating, reviews, is_past) VALUES
(1, 'Tech Summit 2026', 'Technology', '2026-11-15', '2026-11-17', '09:30:00', 'Bangalore International Exhibition Centre', 'Bangalore, India', 'The largest AI and Cloud Computing Summit bringing together tech innovators, developers, and industry leaders.', 5000, 1420, 1499.00, 999.00, '2026-11-05', '/images/technology.png', 2, 'upcoming', 4.8, 154, false),
(2, 'Sunburn Neon Music Fest', 'Music', '2026-12-01', '2026-12-02', '17:00:00', 'Jawaharlal Nehru Stadium', 'Delhi NCR, India', 'An unforgettable weekend of EDM, visual laser shows, and performances by top international DJs.', 12000, 8500, 2999.00, 1999.00, '2026-11-20', '/images/music.png', 2, 'upcoming', 4.9, 310, false),
(3, 'Global Startup Expo 2026', 'Business', '2026-12-10', '2026-12-11', '10:00:00', 'Jio World Convention Centre', 'Mumbai, India', 'Connect with venture capitalists, angel investors, and pitch your startup idea on stage.', 3000, 1200, 799.00, 499.00, '2026-11-30', '/images/business.png', 2, 'upcoming', 4.6, 88, false)
ON DUPLICATE KEY UPDATE title=VALUES(title);

-- 3. Insert Event Tags
INSERT INTO event_tags (event_id, tag) VALUES
(1, 'AI'), (1, 'Cloud'), (1, 'Software'), (1, 'Innovation'),
(2, 'EDM'), (2, 'Concert'), (2, 'Music'), (2, 'Festival'),
(3, 'Startups'), (3, 'Venture Capital'), (3, 'Networking')
ON DUPLICATE KEY UPDATE tag=VALUES(tag);

-- 4. Insert Ticket Types
INSERT INTO ticket_types (id, name, price, available, event_id) VALUES
(1, 'General Pass', 999.00, 3000, 1),
(2, 'VIP All Access', 2499.00, 500, 1),
(3, 'Fan Pit', 1999.00, 5000, 2),
(4, 'VVIP Lounge', 5999.00, 200, 2),
(5, 'Delegate Pass', 499.00, 2000, 3)
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- 5. Insert Sample Tickets
INSERT INTO tickets (id, ticket_number, event_id, user_id, ticket_type_id, price, purchased_at, status, qr_code, seats) VALUES
(1, 'EVP-2026-TK001', 1, 3, 1, 999.00, '2026-10-01', 'active', 'QR-EVP-SAMPLE-001', 1)
ON DUPLICATE KEY UPDATE ticket_number=VALUES(ticket_number);

-- Verification Query
SELECT 'Users Count:' AS label, COUNT(*) AS count FROM users
UNION ALL
SELECT 'Events Count:' AS label, COUNT(*) AS count FROM events
UNION ALL
SELECT 'Tickets Count:' AS label, COUNT(*) AS count FROM tickets;
