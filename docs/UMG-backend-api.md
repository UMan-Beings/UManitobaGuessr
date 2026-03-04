# UManitobaGuessr Backend API

## Overview

### Game phases
The server controls the current game phase. The client should only send actions that are valid for the phase returned in the latest response.
- **GUESS:** The user can submit a guess.
- **REVEAL:** The user can go to the next round.
- **FINISHED:** No more actions allowed.

### Endpoints
| Method | Endpoint | Description | User action | Send | Receive |
|---|---|---|---|---|---|
| POST | /api/v1/games | Start a new game. | Clicks the 'Start game' button. | total rounds, time limit (seconds) | game ID, initial game state (round 1 GUESS phase) |
| GET | /api/v1/games/{gameId} | Get current game state. | Loads or reloads the game page | - | current game state (GUESS, REVEAL, or FINISHED phase) |
| POST | /api/v1/games/{gameId}/guess | Submit a guess and reveal location. | Clicks the 'Submit guess' button. | lat/lng, guess time (seconds) | REVEAL game state |
| POST | /api/v1/games/{gameId}/timeout | Reveal location without submitting a guess. | Reaches the configured time limit. | - | REVEAL game state |
| POST | /api/v1/games/{gameId}/next | Go to the next round or finish the game. | Clicks the 'Next round' button. | - | GUESS or FINISHED game state |

### Errors

#### 404 Not Found
- All endpoints that use {gameId} when that gameId does not exist

#### 409 Conflict
- POST .../guess while game phase is not GUESS
- POST .../timeout while game phase is not GUESS
- POST .../timeout when there is no time limit
- POST .../next while game phase is not REVEAL

#### 400 Bad Request
- All POST endpoints when there are missing or invalid fields in the request JSON body

#### Response
```
{ 
  "error": "InvalidPhase",
  "message": "Cannot submit guess during REVEAL phase."
}
```

## Start game

### Action
User clicks the "Start game" button.

### Request
POST /api/v1/games
```
{
  "totalRounds": 5,
  "timeLimitSeconds": 30
}
```

### Response
```
{
  "gameId": "id",
  "phase": "GUESS",
  "round": 1,
  "totalRounds": 5,
  "imageUrl": "example.jpeg",
  "score": 0,
  "timeLimitSeconds": 30
}
```

## Get current game phase

### Action
User loads or reloads the game page.

### Request
GET /api/v1/games/{gameId}

### Response

#### If guessing
```
{
  "phase": "GUESS",
  "round": 2,
  "totalRounds": 5,
  "imageUrl": "example.jpeg",
  "score": 1234,
  "timeLimitSeconds": 30
}
```

#### If revealed
```
{
  "phase": "REVEAL",
  "round": 2,
  "totalRounds": 5,
  "imageUrl": "example.jpeg",
  "score": 1235,
  "timeLimitSeconds": 30,
  "guessLat": 65.4321,
  "guessLng": 65.4321,
  "actualLat": 12.3456,
  "actualLng": 12.3456,
  "scoreReceived": 1,
  "guessTimeSeconds": 5
}
```

#### If finished
```
{
  "phase": "FINISHED",
  "round": 5,
  "totalRounds": 5,
  "score": 5432,
  "timeLimitSeconds": 30
}
```

## Submit guess

### Action
User clicks the "Submit guess" button.

### Request
POST /api/v1/games/{gameId}/guess
```
{
  "guessLat": 65.4321,
  "guessLng": 65.4321,
  "guessTimeSeconds": 5
}
```

### Response
```
{
  "phase": "REVEAL",
  "round": 2,
  "totalRounds": 5,
  "imageUrl": "example.jpeg",
  "score": 1235,
  "timeLimitSeconds": 30,
  "guessLat": 65.4321,
  "guessLng": 65.4321,
  "actualLat": 12.3456,
  "actualLng": 12.3456,
  "scoreReceived": 1,
  "guessTimeSeconds": 5
}
```

## Timeout

### Action
User runs out of time while guessing.

### Request
POST /api/v1/games/{gameId}/timeout

### Response
```
{
  "phase": "REVEAL",
  "round": 2,
  "totalRounds": 5,
  "imageUrl": "example.jpeg",
  "score": 1235,
  "timeLimitSeconds": 30,
  "actualLat": 12.3456,
  "actualLng": 12.3456,
  "scoreReceived": 1,
  "guessTimeSeconds": 30
}
```

## Next round

### Action
User clicks the "Next round" button.

### Request
POST /api/v1/games/{gameId}/next

### Response

#### If more rounds
```
{
  "phase": "GUESS",
  "round": 3,
  "totalRounds": 5,
  "imageUrl": "example.jpeg",
  "score": 1235,
  "timeLimitSeconds": 30
}
```

#### If finished
```
{
  "phase": "FINISHED",
  "round": 5,
  "totalRounds": 5,
  "score": 5432,
  "timeLimitSeconds": 30
}
```
