# UManitobaGuessr (UMG!)

# Table of Contents
- [1. Team](#1-team)
  - [1.1. Name](#11-name)
  - [1.2. Members](#12-members)
- [2. Summary and Vision](#2-summary-and-vision)
- [3. Features](#3-features)
  - [3.1. Core features](#31-core-features)
  - [3.2. Optional features](#32-optional-features)
- [4. User Stories](#4-user-stories)
  - [4.1. Game configuration](#41-game-configuration)
    - [4.1.1. Adjusting Number of Rounds](#411-adjusting-number-of-rounds)
    - [4.1.2. Enable/Disable Timer](#412-enabledisable-timer)
    - [4.1.3. Adjusting Timer Duration](#413-adjusting-timer-duration)
  - [4.2. Location guessing](#42-location-guessing)
    - [4.2.1. Image View](#421-image-view)
    - [4.2.2. Making a Guess](#422-making-a-guess)
    - [4.2.3. Image/Map Zoom](#423-imagemap-zoom)
    - [4.2.4. Reveal Correct Answer](#424-reveal-correct-answer)
    - [4.2.5. Confirm and Cancel Selection](#425-confirm-and-cancel-selection)
    - [4.2.6. Round Counter](#426-round-counter)
    - [4.2.7. Receiving Score After Game](#427-receiving-score-after-game)
  - [4.3. Accounts](#43-accounts)
    - [4.3.1. Play From Different Devices](#431-play-from-different-devices)
    - [4.3.2. Account Security for Competitive Play](#432-account-security-for-competitive-play)
    - [4.3.3. Quick Sign-In for Short Play Sessions](#433-quick-sign-in-for-short-play-sessions)
    - [4.3.4. Password Recovery](#434-password-recovery)
    - [4.3.5. Account Deletion for Privacy](#435-account-deletion-for-privacy)
  - [4.4. Player Statistics](#44-player-statistics)
    - [4.4.1. Display Total Guesses](#441-display-total-guesses)
    - [4.4.2. Display Total Score](#442-display-total-score)
    - [4.4.3. Display Average Score](#443-display-average-score)
    - [4.4.4. Display Average Time to Guess](#444-display-average-time-to-guess)
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
- Game configuration
- Location guessing
- Account management
- Player statistics

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
This section contains user stories for our planned core features. The user stories for optional features will be written if we have time to implement them.

### 4.1. Game configuration

#### 4.1.1. Adjusting Number of Rounds  
As a: player
I want to: be able to adjust the number of rounds
So that I can: play for certain amounts of time

Acceptance Criteria:
- Given I am at the configuration page, I should have buttons to adjust the number of rounds.

#### 4.1.2. Enable/Disable Timer
As a: player
I want to: be able to turn on the timer
So that I can: play with a time limit for extra difficulty

Acceptance Criteria:
- Given I am at the configuration page, when I click on "Timer" button, I should be able to enable/disable the timer.

#### 4.1.3. Adjusting Timer Duration
As a: player
I want to: be able to adjust the timer duration
So that I can: choose the challenge level I want

Acceptance Criteria:
- Given I am at the configuration page, I should be able to set a time length and play with the duration setting.

### 4.2. Location guessing

#### 4.2.1. Image View
As a: player
I want to: be able to view the picture
So that I can: gather information about the location

Acceptance Criteria:
- Given a new round of game, I can view the picture of the current location.

#### 4.2.2. Making a Guess
As a: player
I want to: be able to click on the map
so that I can: guess the location of the picture

Acceptance Criteria:
- Given I am in the game, when I hover over the map, the map should maximize for better view.
- Given the map is maximized, when I click a location, it should be marked as the current guess.

#### 4.2.3. Image/Map Zoom
As a: player
I want to: be able to zoom in and out
So that I can: check details of pictures or make precise guesses

Acceptance Criteria:
- Given a picture or a map, I can zoom in and out on the cursor position when I scroll up or down accordingly.

#### 4.2.4. Reveal Correct Answer
As a: player
I want to: know the correct answer after guessing
So that I can: know my performance

Acceptance Criteria:
- Given a guess, after I submit the answer, I should be shown the correct location of the picture.

#### 4.2.5. Confirm and Cancel Selection
As a: player
I want to: be able to verify and cancel my guess
So that I can: make changes when I misclick

Acceptance Criteria:
- Given a location was selected, I can choose another location by clicking another location on the map.
- Given a location was selected, when I click "confirm", the location should be submitted as the final answer.

#### 4.2.6. Round Counter
As a: player
I want to: be able to check the game progress
So that I can: determine the number of remaining round

Acceptance Criteria:
- Given a game, I can check how many rounds are left with a counter on screen.

#### 4.2.7. Receiving Score After Game
As a: player
I want to: be able to receive a score after the game
So that I can: track my performance

Acceptance criteria:
- Given that a guess was submitted, I should receive a score based on precision of my answer and time taken.

### 4.3. Accounts

#### 4.3.1. Play From Different Devices
As a: registered user  
I want to: log into my account from different devices  
So that I can: continue my game progress wherever I am  

Acceptance Criteria:
- Given that the user has an account, the user can log in to the same account on multiple devices.
- Given that the user logs in with the same account, game progress and high scores are synchronized across devices.
- Given that the user logs in with the same account, updates made on one device are reflected on other devices after login or refresh.
- Given that the user has an account, the user does not need to create new accounts for different devices.

#### 4.3.2. Account Security for Competitive Play
As a: competitive user  
I want to: restrict access to my account  
So that I can: ensure no one else can play on my account or affect my high score  

Acceptance Criteria:
- Given that an account is present, the system requires authentication to access an account.
- Given that an account is present, only the authenticated user can play games that affect their high score.
- Given that an account is present, logging out prevents further changes to the user’s account data.

#### 4.3.3. Quick Sign-In for Short Play Sessions
As a: user with limited time  
I want to: sign into my account quickly  
So that I can: start playing short games without unnecessary delay  

Acceptance Criteria:
- Given the login page, the login process consists of minimal required steps.
- Given the login page, Returning users can sign in within a few seconds.
- Given a login attemp failed, clear error messages are shown.
- Given a login attemp was successful, the user is taken directly to the game homepage.

#### 4.3.4. Password Recovery
As a: forgetful user  
I want to: recover my password using a verification method (e.g., email)  
So that I can: regain access to my account if I forget my password  

Acceptance Criteria:
- Given the login page, a password recovery option is available on the login screen.
- Given the recovery page, the system verifies the user’s identity (e.g., via email).
- Given the verification of email, the user can receive an email containing a link for resetting the password.
- Given the resetting page, the user can enter a new password.
- Given a reset of password, the new password works immediately.

#### 4.3.5. Account Deletion for Privacy
As a: privacy-conscious user  
I want to: delete my account and associated data  
So that I can: permanently remove my information from the system when I stop playing  

Acceptance Criteria:
- Given the account management page, the user can request account deletion by clicking "Delete account".
- Given the deletion page, the system asks for confirmation before deleting the account.
- Given confirmation of deletion, all user-related data, including high scores, is permanently removed.
- Given confirmation of deletion, the deleted account can no longer be used to log in.

### 4.4. Player Statistics

#### 4.4.1. Display Total Guesses
As a: registered user  
I want to: see the total number of guesses I have made    
So that I can: track my overall game activity    

Acceptance Criteria:
- Given the statisics page, the total number of guesses is displayed.
- Given the statisics page, the total number of guesses updates automatically after each completed game.
- Given the statisics page, the display format is clear and readable.

#### 4.4.2. Display Total Score
As a: registered user  
I want to: view my cumulative score across all games  
So that I can: monitor my overall performance and progress  

Acceptance Criteria:
- Given the statisics page, the total score is displayed on the user's profile or dashboard.
- Given the statisics page, the score updates immediately after each game ends.
- Given the statisics page, the user can distinguish total score from individual game scores.

#### 4.4.3. Display Average Score
As a: registered user  
I want to: see my average score per game  
So that I can: evaluate my performance consistency  

Acceptance Criteria:
- Given the statisics page, the average score is calculated based on all completed games.
- Given the statisics page, the average updates automatically as new games are completed.
- Given the statisics page, the calculation method is transparent and consistent.

#### 4.4.4. Display Average Time to Guess
As a: registered user   
I want to: view my average time taken to guess correctly  
So that I can: track my speed and improve my performance  

Acceptance Criteria:
- Given the statisics page, the average time is displayed on the user's profile or dashboard.
- Given the statisics page, the average updates automatically after each game.
- Given the statisics page, the time is displayed in a clear format (e.g., seconds or minutes:seconds).

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





