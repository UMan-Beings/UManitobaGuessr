# UManitobaGuessr Backend API

## Overview

### Game phases
The server controls the current game phase. The client should only send actions that are valid for the phase returned in the latest response.
- **GUESS:** The user can submit a guess.
- **REVEAL:** The user can go to the next round.
- **FINISHED:** No more actions allowed.

### Endpoints
| Method | Endpoint                     | User action                      | Description                             | Send                               | Receive                                               |
|--------|------------------------------|----------------------------------|-----------------------------------------|------------------------------------|-------------------------------------------------------|
| POST   | /api/v1/games                | Clicks the 'Start game' button   | Start a new game                        | total rounds, time limit (seconds) | game ID, initial game phase (round 1 GUESS phase)     |
| GET    | /api/v1/games/{gameId}       | Loads or reloads the game page   | Get current game phase                  | -                                  | current game phase (GUESS, REVEAL, or FINISHED phase) |
| POST   | /api/v1/games/{gameId}/guess | Clicks the 'Submit guess' button | Submit a guess and reveal location      | lat/lng                            | REVEAL game phase                                     |
| POST   | /api/v1/games/{gameId}/next  | Clicks the 'Next round' button   | Go to the next round or finish the game | -                                  | GUESS or FINISHED game phase                          |

### Errors

#### 404 Not Found
- All endpoints that use {gameId} when that gameId does not exist

#### 409 Conflict
- POST .../guess while game phase is REVEAL or FINISHED
- POST .../next while game phase is GUESS or FINISHED

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
  "scoreReceived": 1
}
```

#### If finished
```
{
  "phase": "FINISHED",
  "round": 5,
  "totalRounds": 5,
  "score": 5432
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
  "guessLng": 65.4321
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
  "scoreReceived": 1
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
  "score": 5432
}
```
