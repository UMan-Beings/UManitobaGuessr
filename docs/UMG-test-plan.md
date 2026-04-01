# UManitobaGuessr Test Plan (Sprint 2)

## ChangeLog

| Version | Change Date | By             | Description                            |
|---------|-------------|----------------|----------------------------------------|
| 1.0     | 2026-03-05  | Jason Bilinsky | Initial Sprint 2 testing plan created. |
| 2.0     | 2026-03-26  | Jason Bilinsky | Updated functional coverage, CI/CD, etc|
| 3.0     | 2026-04-01  | Jason Bilinsky | Updated Load testing section           |


## 1. Introduction

This document defines the testing strategy for UManitobaGuessr during Sprint 4.

### 1.1 Scope

The following software features and quality requirements are in scope for testing:

- Core features:
  - Game configuration
  - Location guessing
  - Player statistics
  - Account Management
- Functional requirements:
  - Correct API request/response behavior for implemented endpoints
  - Correct game state transitions (`GUESS -> REVEAL -> GUESS/FINISHED`)
  - Authentication and authorization behavior:
    - Protected endpoints require valid authentication
    - Game ownership checks return forbidden access when violated
  - Input validation and error handling (400/404/409/401/403 conditions)
  - Player statistics aggregation correctness (total score, average score, average guess time)
- Non-functional requirements:
  - Mutation testing is conducted to evaluate effectiveness of test cases
    - Included into CI pipeline when merging to main only
    - Implemented with Pitest
  - Load Testing is conducted and meets the requirements of at least 20 concurrent users generating 200 requests per minute
    - Plan to integrate into CI pipeline
    - Implemented with k6

### 1.2 Roles and Responsibilities

| Name            | Net ID           | GitHub username | Role                           |
|-----------------|------------------|-----------------|--------------------------------|
| Jason Bilinsky  | umbilin2         | JasonB26        | DevOps                         |
| Richard Marinas | marinarj         | marinarj        | Team Lead / Fullstack Developer|
| Kunhao Zhang    | zhangk4          | GilgameshACP    | Backend Developer              |
| Lucas McFarlane | mcfarl17         | lucasjmcfarlane | System Architect               |

Role expectations:

- DevOps Engineer:
  - Responsible for the CI/CD pipeline.
  - Docker containerization for frontend, backend, and database.
  - Deployment automation
  - Mutation tests and additional unit tests
  - Load testing
- Team Lead
  - Organize and run team meetings
  - Keep the team on track with deadlines and sprint goals
  - Coordinate task assignments and track progress
- Fullstack Developer
  - Develop frontend interface for features
  - Implement backend endpoints as needed for game features
  - Write unit tests for frontend and backend components
- System Architect:
  - Create system design documents and sequence diagrams
  - Planned software structure
  - Planned and coded APIs for frontend to backend communication
  - Setup database tables
- Backend Developer:
  - Setup up Spring Boot backend
  - Connected controller logic to service layers
  - Write integration tests
  - Setup Authorization
  - Construct service layer

## 2. Test Methodology

### 2.1 Automated Regression Testing

### 2.1.1 Test Levels

Course mandatory levels: unit, integration, acceptance, regression, and load testing.

Sprint 3 status summary:

- Implemented automated tests currently cover backend unit and backend integration paths.
- CI runs backend build/test, container build validation, SonarQube scan on PRs, and mutation testing on `main` PR/push.
- CD runs on release publication and pushes versioned and `latest` frontend/backend images to GHCR and Docker Hub.

Total unit tests (current): 82 

Total integration tests: 27

| Test Level | Scope and Requirement | Methodology (How will you do this?) |
|------------|-----------------------|-------------------------------------|
| Unit Testing | Minimum 10 tests per core feature. Current unit tests: 82 | We use JUnit 5 with Mockito to isolate and test service logic across game, auth, user, location, round, and JWT services. |
| Integration Testing | Minimum 10 tests total across core feature interactions. Current integration tests: 27 | We use Spring Boot integration tests with PostgreSQL backed CI services to validate controller to service flow, authentication behavior, and exception handling. |
| Acceptance Testing | End-user testing for every user story. | Team members will perform manual walkthroughs based on user story criteria. |
| Regression Testing | Unit + Integration tests must run on every push/PR. | GitHub Actions CI runs backend build/tests on every push and on PRs to `main`, and runs mutation testing for `main` PR/pushes |

Tools and environment used/planned:

- Backend: Java 21, Spring Boot, JUnit 5, Mockito, Gradle, JaCoCo
- Database for CI tests: PostgreSQL service container, H2
- CI/CD: GitHub Actions (`.github/workflows/ci.yaml`, `.github/workflows/cd.yaml`)

### 2.1.2 CI/CD Regression Workflow

Current automated workflow baseline:

- CI trigger conditions (`ci.yaml`):
  - Every push on all branches
  - Every pull request targeting `main`
  - Manual workflow dispatch
- CI actions (`ci.yaml`):
  - Start PostgreSQL service container for backend test jobs
  - Build backend with `./gradlew build` (runs tests and JaCoCo report generation)
  - Build frontend Docker image for validation
  - Build backend Docker image for validation
  - Run SonarQube Cloud scan on PR events
  - Run PIT mutation testing for PRs targeting `main` and direct pushes to `main`
- CD trigger conditions (`cd.yaml`):
  - Release event: `published`
- CD actions (`cd.yaml`):
  - Checkout repository at the release tag
  - Setup credentials for GHCR and Docker Hub
  - Build and push backend image tags
  - Build and push frontend image tags


### 2.2 Mutation Testing (Test Effectiveness)

Requirement reminder per core feature:

- Generate at least 10 non-equivalent mutants per feature
- Achieve 100% mutation score per feature (all mutants killed)
  - Achieved 98%
  - Two mutants survive as they are equivalent mutants

Proposed plan:

- Tool:
  - Backend Java: PIT (Pitest) Gradle plugin

- Game configuration
  - Primary classes/areas: `GameService.createNewGame`, request validation
  - Mutation type: Decision mutation, Value mutation
  - Operators:
    - `DEFAULTS` setting

- Location guessing
  - Primary classes/areas: `GuessService`, `GameService.submitGuess`, `timeout`, `nextRound`
  - Mutation type: Value mutation, Decision mutation, Statement mutation
  - Operators:
    - `DEFAULTS` setting

- Player statistics
  - Primary classes/areas: score and round aggregation logic in the game flow
  - Mutation type: Value mutation, Decision mutation
  - Operators:
    - `DEFAULTS` setting

- Account management (planned)
  - Primary classes/areas: authentication/user service 
  - Mutation type: Decision mutation, Object-oriented mutation
  - Operators:
    - `DEFAULTS` setting

The mutations that get injected follow the `DEFAULTS` setting found at https://pitest.org/quickstart/mutators/

### 2.3 Load Testing

Requirement:

- Include at least two API requests associated with every core feature.
- Include both GET and POST traffic across the full load test suite.
- System must handle at least 20 concurrent users, generating a total of 200 requests per minute.


Proposed load-testing plan:

- Tool:
  - k6

- Strategy:
  
  - Gradually ramp up to 20 users, and ramp up to 200 requests.
  - Each user will perform a series of feature-specific requests (GET and POST where supported by the API).
  - We will monitor response times and request failures

Feature Load Test Mapping:

| Feature            | Request Type 1               | Request Type 2                   | What is Being Tested                          |
|--------------------|------------------------------|----------------------------------|-----------------------------------------------|
| Game Configuration | GET /api/v1/games/{gameId}   | POST /api/v1/games               | Starting a new game and checking game state   |
| Location Guessing  | GET /api/v1/games/{gameId}   | POST /api/v1/games/{gameId}/guess| Submitting guesses and seeing updated results |
| Player Statistics  | GET /api/v1/users/me/stats   | POST /api/v1/games/{gameId}/next | Viewing player stats while progressing rounds |
| Account Management | POST /api/v1/auth/signup     | POST /api/v1/auth/login          | Registering a new user and logging in         |

Notes:
- Account Management exposes POST endpoints only. This is expected and reflected in the mapping.
- The full suite still includes both GET and POST traffic as required.

## 3. Terms and Acronyms

| Term/Acronym | Definition                        |
|--------------|-----------------------------------|
| API          | Application Programming Interface |
| CI           | Continuous Integration            |
| CD           | Continuous Delivery/Deployment    |
| PR           | Pull Request                      |
| UMG          | UManitobaGuessr                   |
