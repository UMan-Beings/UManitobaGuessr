/** 
* Virtual users follow this flow: signup (once) -> create game -> make guess -> check stats -> advance round (repeated)
* This integrated test validates the system under realistic 40 (changeable) user concurrent load generating 200+ requests per minute
* Endpoints: POST /api/v1/auth/signup, POST /api/v1/auth/login, POST /api/v1/games, 
*           POST /api/v1/games/{gameId}/guess, GET /api/v1/users/me/stats, POST /api/v1/games/{gameId}/next 
*/

import http from 'k6/http';
import { check, sleep } from 'k6';
import { textSummary } from 'https://jslib.k6.io/k6-summary/0.1.0/index.js';

const VU_COUNT = 40;  // concurrent virtual users
const BASE_URL = __ENV.K6_BASE_URL;
const RESULTS_FILE = 'backend/load-tests/userJourney-results.txt';

// Seeded test location (from backend test data)
const SEEDED_LOCATION_X = 49.8951;
const SEEDED_LOCATION_Y = -97.1384;

// Each virtual user keeps its own auth token and current game id.
let authToken = '';
let currentGameId = null;

export const options = {
  stages: [
    { duration: '15s', target: VU_COUNT },     // ramp up to the target user count
    { duration: '1m30s', target: VU_COUNT },   // hold steady load at the target user count
    { duration: '15s', target: 0 }             // ramp back down to zero users
  ],
  thresholds: {
    http_req_duration: ['p(95)<200'],       // 95% of requests should complete under 200ms
    http_req_failed: ['rate<0.05'],         // keep the overall failure rate below 5%
    http_reqs: ['rate>3.33']                // at least 200 successful requests per minute (200/60 = 3.33 per second)
  },
};

export default function userJourney() {
  // Phase 1: Signup and login.
  // On the first iteration for each virtual user, create a unique account and store its token.
  if (__ITER === 0) {
    const uniqueId = `vu${__VU}_${Date.now()}`;
    const email = `${uniqueId}@example.com`;
    const password = 'TestPassword123!';

    // Create a unique test account for this virtual user.
    const signupResponse = http.post(
      `${BASE_URL}/auth/signup`,
      JSON.stringify({
        username: uniqueId,
        email,
        password
      }),
      {
        headers: { 'Content-Type': 'application/json' }
      }
    );
    
    // Check that the signup request succeeded (status 200)
    check(signupResponse, {
      'signup 200': response => response.status === 200
    });

    // Log in with the newly created account and capture the auth token.
    const loginResponse = http.post(
      `${BASE_URL}/auth/login`,
      JSON.stringify({ email, password }),
      {
        headers: { 'Content-Type': 'application/json' }
      }
    );

    // check that the login request succeeded and that a token was returned
    check(loginResponse, {
      'login 200': response => response.status === 200,
      'token present': response => response.json('token') != null
    });

    authToken = loginResponse.json('token');
    sleep(0.5);
  }

  // Phase 2: Create a game.
  const gameResponse = http.post(
    `${BASE_URL}/games`,
    JSON.stringify({
      totalRounds: 5,
      maxTimerSeconds: 30
    }),
    {
      headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${authToken}` }
    }
  );

  // Check if game creation succeeded and that a gameId was returned
  check(gameResponse, {
    'game 200': response => response.status === 200,
    'gameId present': response => response.json('gameId') != null
  });

  currentGameId = gameResponse.json('gameId');
  sleep(0.5);

  // Phase 3: Submit a guess.
  const guessResponse = http.post(
    `${BASE_URL}/games/${currentGameId}/guess`,
    JSON.stringify({
      corX: SEEDED_LOCATION_X + 100,
      corY: SEEDED_LOCATION_Y,
      guessTimeSeconds: 10
    }),
    {
      headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${authToken}` }
    }
  );

  // Check if guess submission succeeded
  check(guessResponse, {
    'guess 200': response => response.status === 200
  });
  sleep(0.5);

  // Phase 4: Advance to the next round.
  const nextResponse = http.post(
    `${BASE_URL}/games/${currentGameId}/next`,
    '{}',
    {
      headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${authToken}` }
    }
  );

  // Check if next round advancement succeeded
  check(nextResponse, {
    'next round 200': response => response.status === 200
  });
  sleep(0.5);

  // Phase 5: Fetch player statistics.
  const statsResponse = http.get(
    `${BASE_URL}/users/me/stats`,
    {
      headers: { 'Authorization': `Bearer ${authToken}` }
    }
  );

  // Check if stats fetch succeeded
  check(statsResponse, {
    'stats 200': response => response.status === 200
  });
  sleep(1);
}

// Summary handler
export function handleSummary(data) {
  return {
    // Save a plain-text file without colors for readability
    [RESULTS_FILE]: textSummary(data, { indent: ' ', enableColors: false }),

    // Print a terminal summary with colors
    stdout: textSummary(data, { indent: ' ', enableColors: true }),
  };
}