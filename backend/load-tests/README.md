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

## Run Commands

Run from this folder: `backend/load-tests`

Dev compose path (frontend proxy on port 3000):

```bash
K6_BASE_URL=http://localhost:3000/api/v1 k6 run userJourneyTest.js
```

Users or prod compose path (nginx/proxy on port 7000):

```bash
K6_BASE_URL=http://localhost:7000/api/v1 k6 run userJourneyTest.js
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
