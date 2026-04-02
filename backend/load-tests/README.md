# Load Tests for UManitobaGuessr

This folder contains the current k6 load test for backend performance and reliability checks.

## Test Script

The active test is userJourneyTest.js.

It models one integrated user flow:

1. Signup and login once per virtual user
2. Create a game
3. Submit a guess
4. Advance to next round
5. Read player stats

This keeps feature dependencies realistic under concurrent load instead of testing endpoints in isolation.

## Prerequisites

1. k6 installed
2. Backend stack running
3. Base URL passed with `K6_BASE_URL` (required by the script)
4. Start Docker Compose in detached mode (`up -d`) so the terminal is available for k6, or run k6 in a second/split terminal.

## Run Commands

Run from this folder: `backend/load-tests`

Step by step workflow:

1. Start one target stack in detached mode (`up -d`):

```bash
# Dev stack
docker-compose -f docker-compose.dev.yaml -p umg_dev up -d --build

# Users stack
docker-compose -f docker-compose.users.yaml -p umg_users up -d --pull always

# Prod stack
docker-compose -f docker-compose.prod.yaml -p umg_prod up -d --build
```

2. Verify services are running:

```bash
docker ps --filter name=umg
```

3. Run the load test:

```bash
# Dev compose path (frontend proxy on port 3000)
K6_BASE_URL=http://localhost:3000/api/v1 k6 run userJourneyTest.js

# Users or prod compose path (nginx/proxy on port 7000)
K6_BASE_URL=http://localhost:7000/api/v1 k6 run userJourneyTest.js
```

4. Stop the selected stack when done:

```bash
# Dev stack
docker-compose -f docker-compose.dev.yaml -p umg_dev down

# Users stack
docker-compose -f docker-compose.users.yaml -p umg_users down

# Prod stack
docker-compose -f docker-compose.prod.yaml -p umg_prod down
```

## Current Load Profile

Configured directly in userJourneyTest.js (located in backend/load-tests):

1. Ramp up to 40 virtual users over 15 seconds
2. Hold 40 virtual users for 1 minute 30 seconds
3. Ramp down to 0 virtual users over 15 seconds

To change concurrency, update `VU_COUNT` in userJourneyTest.js.

## Current Thresholds

Configured in userJourneyTest.js:

1. `http_req_duration`: `p(95)<200`
2. `http_req_failed`: `rate<0.05`
3. `http_reqs`: `rate>3.33` (200 requests/minute target)

## Notes

1. Each virtual user creates a unique account on first iteration, then reuses its token.
2. The test uses seeded coordinates and a fixed guess offset for gameplay requests.
3. Current script checks for HTTP 200 and required response fields (token, gameId).
