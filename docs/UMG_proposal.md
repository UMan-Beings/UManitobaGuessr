# UManitobaGuessr (UMG!)

## Table of Contents
- [1. Team](#1-team)
  - [1.1. Name](#11-name)
  - [1.2. Members](#12-members)
- [2. Summary and Vision](#2-summary-and-vision)
- [3. Features](#3-features)
  - [3.1. Core features](#31-core-features)
  - [3.2. Optional features](#32-optional-features)
- [4. User Stories](#4-user-stories)
  - [4.1. Core user stories](#41-core-user-stories)
    - [4.1.1. Accounts](#411-accounts)
    - [4.1.2. Game configuration](#412-game-configuration)
    - [4.1.3. Location guessing](#413-location-guessing)
    - [4.1.4. User-submitted locations](#414-user-submitted-locations)
  - [4.2. Optional user stories](#42-optional-user-stories)
- [5. Technology](#5-technology)
  - [5.1. Frontend](#51-frontend)
  - [5.2. Backend](#52-backend)
  - [5.3. Database](#53-database)

## 1. Team

### 1.1. Name
UMan Beings

### 1.2. Members
| Name                | Username        |
|---------------------|-----------------|
| Richard Marinas Jr. | marinarj        |
| Jason Bilinsky      | JasonB26        |
| Kunhao Zhang        | GilgameshACP    |
| Lucas McFarlane     | lucasjmcfarlane |

## 2. Summary and Vision

**UManitobaGuessr (UMG)** is an interactive geography-based game inspired by GeoGuessr, it challenges players to identify real-world locations on campus of the University of Manitoba based on clues given as pictures. Players can test their knowledge of Fort Garry Campus through scores they receive based on their performance, and learn places they do not know while playing the game.

Furthermore, **UMG** allows users to upload pictures to create custom challenges. Unlike traditional GeoGuessr games, **UManitobaGuessr** focuses on the university and it builds a community by building a community where everyone can participate to fill in new locations.

**UManitobaGuessr** aims to insipre curiosity about the campus and the University of Manitoba, and connect people through the exploration. By blending entertainment and knowledge, **UMG** aspires to make learning about our campus an engaging and rewarding experience.

(TA: Taufiqul Khan (Taufiq))

## 3. Features
This section contains the core and optional features for our application.

### 3.1. Core features
There are four core features that are fundamental for the game.
- Accounts
- Game configuration
- Location guessing
- User-submitted locations

### 3.2. Optional features
We might implement some of these features in Sprint 3 or 4.
- Challenge modes
- Rate locations
- Browse locations
- Game history
- Game saving
- Game sharing
- Leaderboards

## 4. User Stories
This section contains user stories for our planned features.

### 4.1. Core user stories

#### 4.1.1. Accounts

**Title: Play From Different Devices**  
As a: registered user  
I want to: log into my account from different devices  
So that I can: continue my game progress wherever I am  

Acceptance Criteria:
- The user can log in to the same account on multiple devices.
- Game progress and high scores are synchronized across devices.
- Updates made on one device are reflected on other devices after login or refresh.
- The user does not need to create a new account for each device.


**Title: Account Security for Competitive Play**  
As a: competitive user  
I want to: restrict access to my account  
So that I can: ensure no one else can play on my account or affect my high score  

Acceptance Criteria:
- The system requires authentication to access an account.
- Only the authenticated user can play games that affect their high score.
- Logging out prevents further changes to the user’s account data.

**Title: Quick Sign-In for Short Play Sessions**  
As a: user with limited time  
I want to: sign into my account quickly  
So that I can: start playing short games without unnecessary delay  

Acceptance Criteria:
- The login process consists of minimal required steps.
- Returning users can sign in within a few seconds.
- Clear error messages are shown if login fails.
- The user is taken directly to the game after successful login.

**Title: Password Recovery**  
As a: forgetful user  
I want to: recover my password using a verification method (e.g., email)  
So that I can: regain access to my account if I forget my password  

Acceptance Criteria:
- A password recovery option is available on the login screen.
- The system verifies the user’s identity (e.g., via email).
- The user can reset their password successfully.
- The new password works immediately for login.

**Title: Account Deletion for Privacy**  
As a: privacy-conscious user  
I want to: delete my account and associated data  
So that I can: permanently remove my information from the system when I stop playing  

Acceptance Criteria:
- The user can request account deletion from account settings.
- The system asks for confirmation before deleting the account.
- All user-related data, including high scores, is permanently removed.
- The deleted account can no longer be used to log in.


#### 4.1.2. Game configuration
-	As a player, I want to be able to adjust the number of rounds so that I can play for certain amounts of time.
-	As a player, I want to be able to turn on the timer so that I can play with a time limit for extra difficulty.
- As a player, I want to be able to adjust the timer duration so that I can choose the challenge level I want.

#### 4.1.3. Location guessing
-	As a player, I want to be able to view the picture so that I can gather information about the location.
-	As a player, I want to be able to click on the map so that I can guess the location of the picture.
-	As a player, I want to be able to zoom in and out so that I can check details of pictures or make precise guesses.
-	As a player, I want to know the correct answer after guessing so that I can know my performance.
-	As a player, I want to be able to verify and cancel my guess so that I can make changes when I misclick.
-	As a player, I want to be able to check the game progress so that I can determine the number of remaining rounds.
-	As a player, I want to be able to receive a score after the game so that I can track my performance.

#### 4.1.4. Player Statistics
**Title: Display Total Guesses**    
As a: registered user  
I want to: see the total number of guesses I have made    
So that I can: track my overall game activity    

Acceptance Criteria:
- The total number of guesses is displayed on the user's profile or dashboard.
- The number updates automatically after each completed game.
- The display format is clear and readable.


**Title: Display Total Score**  
As a: registered user  
I want to: view my cumulative score across all games  
So that I can: monitor my overall performance and progress  

Acceptance Criteria:
- The total score is displayed on the user's profile or dashboard.
- The score updates immediately after each game ends.
- The user can distinguish total score from individual game scores.


**Title: Display Average Score**  
As a: registered user  
I want to: see my average score per game  
So that I can: evaluate my performance consistency  

Acceptance Criteria:
- The average score is calculated based on all completed games.
- The average updates automatically as new games are completed.
- The calculation method is transparent and consistent.


**Title: Display Average Time to Guess**  
As a: registered user   
I want to: view my average time taken to guess correctly  
So that I can: track my speed and improve my performance  

Acceptance Criteria:
- The average time is displayed on the user's profile or dashboard.
- The average updates automatically after each game.
- The time is displayed in a clear format (e.g., seconds or minutes:seconds).

### 4.2. Optional user stories
We will update this section if we have time to implement optional features.

## 5. Technology

### 5.1. Frontend
- TypeScript
- VueJS
- Vuetify

### 5.2. Backend
- Java
- Spring Boot
- Spring Security
- Spring Data JPA

### 5.3. Database
- PostgreSQL







