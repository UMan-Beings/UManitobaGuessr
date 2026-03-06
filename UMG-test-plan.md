# UManitobaGuessr Test Plan (Sprint 2)

## ChangeLog

| Version | Change Date | By             | Description                            |
|---------|-------------|----------------|----------------------------------------|
| 1.0     | 2026-03-05  | Jason Bilinsky | Initial Sprint 2 testing plan created. |

## 1. Introduction

This document defines the testing strategy for UManitobaGuessr during Sprint 2. The plan will be updated as topics covered in class allow us to have the knowledge to meet those criteria.

### 1.1 Scope

The following software features and quality requirements are in scope for testing:

- Core features:
  - Game configuration
  - Location guessing
  - Player statistics  (barely implemented implemented sprint 2)
- Functional requirements:
  - Correct API request/response behavior for implemented endpoints
  - Correct game state transitions (`GUESS -> REVEAL -> GUESS/FINISHED`)
  - Input validation and error handling (400/404/409 conditions)
- Non-functional requirements (Sprint 2 planning level):
  - Automated regression on each push and pull request through CI
  - Automated regression when a version is published as well through CD
    - This needs refinement depening on future requirements for releases
  - Test coverage reporting with JaCoCo
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
- Team Lead
  - Organize and run team meetings
  - Keep the team on track with deadlines and sprint goals
  - Coordinate task assignments and track progress
- Fullstack Developer
  - Develop frontend interface for game configuration and location guessing
  - Implement backend endpoints as needed for game features
  - Write unit tests for frontend and backend components
- System Architect:
  - Create system design documents and sequence diagrams
  - Planned software structure
  - Planned APIs for frontend to backend communication
- Backend Developer:
  - Setup up Spring Boot backend
  - Connected controller logic to service layers
  - Write integration tests
  - Construct service layer

## 2. Test Methodology

### 2.1 Automated Regression Testing

### 2.1.1 Test Levels

Course mandatory levels: unit, integration, acceptance, regression, and load testing.

Sprint 2 status summary:

- Current implemented automated tests are primarily backend service/unit-oriented.
- CI runs backend build/test and container build validation.
- Frontend automated tests may be planned for future sprints


Total baseline tests: 35.

| Test Level | Scope and Requirement | Methodology (How will you do this?) |
|------------|-----------------------|-------------------------------------|
| Unit Testing | Minimum 10 tests per core feature. Sprint 2 baseline: 35 tests | We use JUnit 5 with Mockito to isolate and test service logic. Integration tests using Spring Boot testing tools will be added later to verify interactions between controllers, services, and repositories |
| Integration Testing | Minimum 10 tests total across core feature interactions. Sprint 2 baseline: partial (currently limited integration coverage). | Add Spring Boot integration tests using test profile + H2/Postgres test setup to validate controller-service-repository flow and database interactions. |
| Acceptance Testing | End-user testing for every user story. | Team members will perform manual walkthroughs based on user story criteria. |
| Regression Testing | Unit + Integration tests must run on every push to `main` (and PRs). | Use GitHub Actions CI to run backend build/tests on push and PR. Merges are blocked by required status checks when configured in repository branch protection. |

Tools and environment used/planned:

- Backend: Java 21, Spring Boot, JUnit 5, Mockito, Gradle, JaCoCo
- Database for CI tests: PostgreSQL service container, H2
- CI/CD: GitHub Actions (`.github/workflows/ci.yaml`)

### 2.1.2 CI/CD Regression Workflow

Current automated pipeline (Sprint 2):

- Trigger conditions:
  - Every push on all branches
  - Every pull request targeting `main`
  - Manual workflow dispatch
- CI actions:
  - Start PostgreSQL service container
  - Build backend with `./gradlew build` (runs tests and JaCoCo report generation)
  - Build frontend Docker image for validation
  - Build backend Docker image for validation
  - Run SonarQube Cloud scan on PR events

Planned tightening after Sprint 2:

- Enforce branch protection so `main` requires all test jobs to pass
- Add explicit integration and frontend test jobs as suites grow

### 2.2 Mutation Testing (Test Effectiveness)

Requirement reminder per core feature:

- Generate at least 10 non-equivalent mutants per feature
- Achieve 100% mutation score per feature (all mutants killed)

Proposed plan:

- Tool:
  - Backend Java: PIT (Pitest) Gradle plugin
  - Frontend: Stryker

- Game configuration
  - Primary classes/areas: `GameService.createNewGame`, request validation
  - Mutation type: Decision mutation, Value mutation
  - Operators:
    - Replacement of conditional expressions (e.g., `>` with `>=`, `==` with `<=`)
    - Negation of conditional expressions
    - Replacement of constant with variable or vice versa
    - Replacement of return statement

- Location guessing
  - Primary classes/areas: `GuessService`, `GameService.submitGuess`, `timeout`, `nextRound`
  - Mutation type: Value mutation, Decision mutation, Statement mutation
  - Operators:
    - Replacement of arithmetic operations with others (e.g., `+` with `*`, `-` with `/`)
    - Replacement of conditional expressions
    - Negation of conditional expressions
    - Statement deletion

- Player statistics
  - Primary classes/areas: score and round aggregation logic in the game flow
  - Mutation type: Value mutation, Decision mutation
  - Operators:
    - Replacement of arithmetic operations with others
    - Replacement of constant with variable or vice versa
    - Replacement of conditional expressions
    - Replacement of return statement

- Account management (planned)
  - Primary classes/areas: future authentication/user service and controller endpoints
  - Mutation type: Decision mutation, Object-oriented mutation
  - Operators:
    - Replacement of Boolean expressions with true or false
    - Replacement of variables (must be type compatible)
    - Replacement of return statement
    - Changing the order of parameters in a call


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
  - Endpoints: `GET /api/v1/stats/{userId}`, `POST /api/v1/games/{gameId}/guess`
  - Load: 200–300 users retrieving stats while others play

- **Account Management**
  - Endpoints: `POST /api/v1/auth/login`, `GET /api/v1/users/{id}`
  - Load: 200–300 users logging in and fetching profiles

## 3. Terms and Acronyms

| Term/Acronym | Definition                        |
|--------------|-----------------------------------|
| API          | Application Programming Interface |
| CI           | Continuous Integration            |
| CD           | Continuous Delivery/Deployment    |
| PR           | Pull Request                      |
| UMG          | UManitobaGuessr                   |
