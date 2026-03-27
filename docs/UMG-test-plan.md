# UManitobaGuessr Test Plan (Sprint 2)

## ChangeLog

| Version | Change Date | By             | Description                            |
|---------|-------------|----------------|----------------------------------------|
| 1.0     | 2026-03-05  | Jason Bilinsky | Initial Sprint 2 testing plan created. |
| 2.0     | 2026-03-26  | Jason Bilinsky | Updated functional coverage, CI/CD, etc|


## 1. Introduction

This document defines the testing strategy for UManitobaGuessr during Sprint 3. It may be updated during Sprint 4 if changes are made to the load testing plan.

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
- Non-functional requirements (Sprint 4 planning level):
  - Load and mutation testing strategy definition for later execution

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

Requirement for Future:

- Include at least two request types associated with every core feature

Sprint 2 decision:

- Load testing execution is deferred in Sprint 2 as allowed by course guidance.
- This section documents the test design so implementation can start quickly later.

Proposed load-testing plan:

- Tool: k6 or JMeter (undecided)
- Strategy:
  - Gradual ramp up to a steady anticipated volume 

Potential Scenarios:

- **Game Configuration**
  - Endpoints: `POST /api/v1/games`, `GET /api/v1/games/{gameId}`
  - Load: 200–300 users starting and checking games concurrently

- **Location Guessing**
  - Endpoints: `POST /api/v1/games/{gameId}/guess`, `POST /api/v1/games/{gameId}/next`, `GET /api/v1/games/{gameId}`
  - Load: 200–300 users submitting guesses and moving rounds concurrently

- **Player Statistics**
  - Endpoints: `GET /api/v1/users/me/stats`, `POST /api/v1/games/{gameId}/guess`
  - Load: 200–300 users retrieving stats while others play

- **Account Management**
  - Endpoints: `POST /api/v1/auth/login`, `POST /api/v1/auth/signup`
  - Load: 200–300 users logging in and fetching profiles

## 3. Terms and Acronyms

| Term/Acronym | Definition                        |
|--------------|-----------------------------------|
| API          | Application Programming Interface |
| CI           | Continuous Integration            |
| CD           | Continuous Delivery/Deployment    |
| PR           | Pull Request                      |
| UMG          | UManitobaGuessr                   |
