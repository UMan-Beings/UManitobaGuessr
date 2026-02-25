-- Schema script for H2 database
DROP TABLE IF EXISTS "Guess";
DROP TABLE IF EXISTS "Round";
DROP TABLE IF EXISTS "Game";
DROP TABLE IF EXISTS "Location";
DROP TABLE IF EXISTS "User";

CREATE TABLE IF NOT EXISTS "User" (
    "userId" BIGINT AUTO_INCREMENT PRIMARY KEY,
    "username" VARCHAR(255) NOT NULL UNIQUE,
    "email" VARCHAR(255) NOT NULL UNIQUE,
    "passwordHash" VARCHAR(255) NOT NULL,
    "profileImageUrl" VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS "Location" (
    "locationId" BIGINT AUTO_INCREMENT PRIMARY KEY,
    "name" VARCHAR(255),
    "imageUrl" VARCHAR(255) NOT NULL,
    "corX" DECIMAL(9, 6) NOT NULL,
    "corY" DECIMAL(9, 6) NOT NULL
);

CREATE TABLE IF NOT EXISTS "Game" (
    "gameId" BIGINT AUTO_INCREMENT PRIMARY KEY,
    "userId" BIGINT NOT NULL,
    "maxTimerSeconds" INT NOT NULL,
    "totalRounds" INT NOT NULL,
    "isCompleted" BOOLEAN NOT NULL,
    "gameState" VARCHAR(255) NOT NULL,
    "currentRoundNumber" INT NOT NULL,
    "score" INT NOT NULL,
    FOREIGN KEY ("userId") REFERENCES "User"("userId")
);

CREATE TABLE IF NOT EXISTS "Round" (
    "roundId" BIGINT AUTO_INCREMENT PRIMARY KEY,
    "gameId" BIGINT NOT NULL,
    "locationId" BIGINT NOT NULL,
    "roundNumber" INT NOT NULL,
    FOREIGN KEY ("gameId") REFERENCES "Game"("gameId"),
    FOREIGN KEY ("locationId") REFERENCES "Location"("locationId")
);

CREATE TABLE IF NOT EXISTS "Guess" (
    "guessId" BIGINT AUTO_INCREMENT PRIMARY KEY,
    "roundId" BIGINT NOT NULL,
    "userId" BIGINT NOT NULL,
    "corX" DECIMAL(9, 6) NOT NULL,
    "corY" DECIMAL(9, 6) NOT NULL,
    "score" INT NOT NULL,
    FOREIGN KEY ("roundId") REFERENCES "Round"("roundId"),
    FOREIGN KEY ("userId") REFERENCES "User"("userId")
);