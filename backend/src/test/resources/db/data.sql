-- Data script for H2 database

-- Clear existing data in the correct order to maintain referential integrity
DELETE FROM "GUESS";
DELETE FROM "ROUND";
DELETE FROM "GAME";
DELETE FROM "LOCATION";
DELETE FROM "User";

-- Insert test data for User with explicit userId values
INSERT INTO "User" ("username", "email", "passwordHash", "profileImageUrl") VALUES
('testuser1', 'testuser1@example.com', 'hashedpassword1', 'https://example.com/profile1.png'),
('testuser2', 'testuser2@example.com', 'hashedpassword2', 'https://example.com/profile2.png')
ON CONFLICT ("username") DO NOTHING;

-- Insert test data for Location
INSERT INTO "LOCATION" ("name", "imageUrl", "corX", "corY") VALUES
('Location A', 'https://example.com/locationA.png', 49.8951, -97.1384),
('Location B', 'https://example.com/locationB.png', 40.7128, -74.0060),
('Location C', 'https://example.com/locationC.png', 34.0522, -118.2437),
('Location D', 'https://example.com/locationD.png', 51.5074, -0.1278),
('Location E', 'https://example.com/locationE.png', 35.6895, 139.6917),
('Location F', 'https://example.com/locationF.png', -33.8688, 151.2093),
('Location G', 'https://example.com/locationG.png', 48.8566, 2.3522);

-- -- Insert test data for Game
-- INSERT INTO "GAME" ("userId", "maxTimerSeconds", "totalRounds", "isCompleted", "gameState", "currentRoundNumber", "score") VALUES
-- (1, 60, 5, FALSE, 'GUESS', 1, 0);
