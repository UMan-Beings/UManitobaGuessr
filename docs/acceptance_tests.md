# Acceptance Tests

## 4.1 Game Configuration

### 4.1.1 Game Configuration Overview

#### AC 2: Show current rounds/timer values on configuration page

- Given: I am on the main menu.
- When: I view the Game Configuration card.
- Then: I should see selected values for rounds and time limit (timer Off or specific duration).

#### AC 4: New game uses settings from configuration

- Given: I selected rounds and time limit in Game Configuration.
- When: I click Start Game.
- Then: The game should start with matching total rounds and timer limit.
- Verification:
  - The in-game round counter denominator matches selected rounds.
  - Timer behavior reflects selected time limit (Off means no auto-timeout; On means timeout can occur).

### 4.1.2 Adjusting Number of Rounds

#### AC 1: Buttons to adjust rounds

- Given: I am on the configuration interface.
- When: I inspect rounds controls.
- Then: I should see buttons for selectable round counts (5, 10, 15, 20).

### 4.1.3 Enable/Disable Timer

#### AC 1: Timer can be enabled/disabled from configuration

- Given: I am on the configuration interface.
- When: I choose Off, 30 sec, 1 min, 5 min, or 10 min in Time Limit.
- Then: Timer is effectively disabled (Off) or enabled (non-zero duration).

## 4.2 Location Guessing

### 4.2.1 Image View

#### AC 1: View current location picture each round

- Given: A new round starts in GUESS phase.
- When: The game screen renders.
- Then: The round image should be visible.

### 4.2.2 Making a Guess

#### AC 1: Map maximizes on hover

- Given: I am in GUESS phase.
- When: I hover over the mini-map.
- Then: Map width expands for a larger view.

#### AC 2: Click on maximized map marks current guess

- Given: Map is visible (mini or expanded).
- When: I click a location on the map.
- Then: A marker appears (or moves) at the clicked location as the current guess.

### 4.2.4 Reveal Correct Answer

#### AC 1: After submit, show correct location

- Given: I placed a guess marker.
- When: I click Submit guess.
- Then: Reveal phase shows actual location marker, and if guessed, my marker plus connecting line.

### 4.2.5 Confirm and Cancel Selection

#### AC 1: Selecting another location updates current selection

- Given: I already selected a location.
- When: I click a different location on the map.
- Then: The guess marker should move to the new location.

#### AC 2: Confirm submits final answer

- Given: I selected a location.
- When: I click Submit guess.
- Then: Guess is submitted and game transitions to reveal phase.

### 4.2.6 Round Counter

#### AC 1: Display rounds left/progress counter

- Given: A game is in progress.
- When: I view the game stats panel.
- Then: Round is shown in X/Y format.

### 4.2.7 Receiving Score After Game

#### AC 1: Receive score after submitting a guess based on precision

- Given: A guess was submitted.
- When: The round result is shown.
- Then: I should receive a score based on the precision of my answer.

## 4.3 Accounts

### 4.3.1 Play From Different Devices

#### AC 1: Same account login from multiple devices

- Given: A registered account exists.
- When: I log in from Device A and Device B.
- Then: Login succeeds on both devices with valid credentials.

#### AC 2: Playing on one device is reflected after login on another

- Given: I play and complete a game on Device A while using my account.
- When: I log in to the same account on Device B.
- Then: The updated account data (such as aggregate stats) should be reflected on Device B.

#### AC 3: Updates on one device reflected on other device after login/refresh

- Given: I am logged in on Device A and Device B.
- When: I finish a game on Device A and refresh/re-login on Device B.
- Then: Aggregate stats endpoint should return updated totals on Device B.

#### AC 4: No need to create multiple accounts for different devices

- Given: A user already has an account.
- When: User logs in from another device.
- Then: Existing account credentials are sufficient; no new account is required.

### 4.3.2 Account Security for Competitive Play

#### AC 1: Authentication required to access account

- Given: Account data endpoints.
- When: I call user account stats endpoint without JWT.
- Then: Request is rejected as unauthorized.

#### AC 2: Only authenticated user can play games that affect high score

- Given: A game is tied to an authenticated user.
- When: Another user or anonymous session accesses that game endpoint.
- Then: Access is forbidden.

#### AC 3: Logout prevents further changes to account data

- Given: I am logged in and then log out.
- When: I try to fetch account stats.
- Then: Request should be unauthorized unless I log in again.

### 4.3.3 Quick Sign-In for Short Play Sessions

#### AC 1: Login has minimal required steps

- Given: Login page.
- When: I inspect required inputs.
- Then: Only email and password are required to submit.

#### AC 2: Returning users can sign in within a few seconds

- Given: Valid credentials.
- When: I submit login form.
- Then: JWT is stored and redirect to homepage occurs quickly.

#### AC 3: Failed login shows clear error messages

- Given: Invalid login attempt.
- When: I submit login.
- Then: Error alert appears with backend-provided message.

#### AC 4: Successful login lands directly on game homepage

- Given: Valid login.
- When: Login succeeds.
- Then: User is redirected to root homepage.

## 4.4 Player Statistics

### 4.4.2 Display Total Score

#### AC 1: Total score displayed on profile/dashboard

- Given: Authenticated user on homepage.
- When: Stats load.
- Then: Total Score value is visible.

#### AC 2: Total score updates after each game ends

- Given: An authenticated user completes a game.
- When: I return/reload homepage stats.
- Then: Total Score reflects new cumulative total.

#### AC 3: Total score distinguishable from per-game scores

- Given: Stats panel and in-game score exist.
- When: I view both contexts.
- Then: Labels clearly distinguish Total Score from game score.

### 4.4.3 Display Average Score

#### AC 1: Average score calculated from completed games

- Given: One or more completed games.
- When: I request user stats.
- Then: Average score equals average of completed game scores.

#### AC 2: Average score auto-updates after new completed games

- Given: Average score displayed.
- When: I complete another game and refresh/reload stats.
- Then: Average score updates.

#### AC 3: Calculation method is transparent and consistent

- Given: Backend returns average score.
- When: I compare manual calculation from known game results.
- Then: Displayed value is consistent (rounded in UI to 1 decimal).

### 4.4.4 Display Average Time to Guess

#### AC 1: Average time shown on profile/dashboard

- Given: Authenticated user with completed games.
- When: Stats panel loads.
- Then: Average Guess Time is visible.

#### AC 2: Average time auto-updates after each game

- Given: Average Guess Time exists.
- When: I complete another game and refresh/reload stats.
- Then: Average Guess Time updates.

#### AC 3: Time displayed in clear format (seconds or mm:ss)

- Given: Average Guess Time is displayed.
- When: I inspect the value format.
- Then: It is clearly labeled in seconds.
