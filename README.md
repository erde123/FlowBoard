# FlowBoard

FlowBoard is a project and task management application being built alongside the IBM Applied DevOps Engineering learning roadmap. The planned MVP includes project/task CRUD, a Kanban board, authentication, and a deployed demo.

## Progress

- [x] Initialize a Spring Boot backend and React/TypeScript frontend.
- [x] Add separate GitHub Actions workflows for backend tests/build and frontend lint/build.
- [x] Add PostgreSQL configuration and Flyway migrations for projects and tasks.
- [x] Define JPA entities with UUID identifiers, timestamps, and a task-to-project relationship.
- [x] Define task statuses (`TODO`, `IN_PROGRESS`, `DONE`) and priorities (`LOW`, `MEDIUM`, `HIGH`, `URGENT`).
- [x] Verify application startup and both migrations using PostgreSQL Testcontainers.
- [x] Add initial Dockerfiles and a Docker Compose configuration.
- [ ] Build project and task CRUD APIs with validation and error responses.
- [ ] Replace the frontend starter screen with project forms and a Kanban board.
- [ ] Complete Compose runtime configuration and verify one-command startup.
- [ ] Add authentication, authorization, and security tests.
- [ ] Add monitoring and deploy a public demo.

The current milestone is the Day 3 database/domain foundation. CRUD endpoints and the Kanban interface are still planned.

## Technology

| Area | Tools |
| --- | --- |
| Backend | Java 21, Spring Boot 4, Spring Data JPA, Gradle |
| Database | PostgreSQL, Flyway |
| Frontend | React, TypeScript, Vite, Tailwind CSS |
| Testing | JUnit, Spring Boot Test, Testcontainers |
| Delivery | GitHub Actions, Docker, Docker Compose |

## Repository layout

```text
backend/                         Spring Boot application and Gradle wrapper
  src/main/java/                 Application and Project/Task entities
  src/main/resources/            Configuration and Flyway SQL migrations
  src/test/java/                 Application context test and Testcontainers setup
Frontend/                        React application
.github/workflows/               Backend and frontend CI
docker-compose.yml               Initial backend/frontend/PostgreSQL stack
```

## Local development

Use Java 21, Node.js 24 (matching CI), npm, and Docker. Running the backend directly also requires a reachable PostgreSQL database.

### Backend

Provide these environment variables through your shell or IDE run configuration:

```sh
export DB_URL='jdbc:postgresql://localhost:5432/flowboard'
export DB_USERNAME='flowboard'
export DB_PASSWORD='replace-with-your-local-password'
```

The URL above assumes PostgreSQL is available on local port 5432. A `backend/.env` file alone is not automatically loaded by the application.

```sh
cd backend
./gradlew bootRun
```

The backend listens on port **8000**. Flyway applies the migrations, then Hibernate validates the entity mappings against the schema. Spring Security is included; application-specific authentication and authorization are still to be implemented.

### Frontend

```sh
cd Frontend
npm ci
npm run dev
```

Open the URL printed by Vite. The development proxy currently forwards `/api` requests to port 8080; its target in `Frontend/vite.config.ts` needs to be aligned with backend port 8000 before API integration. The CRUD APIs are not implemented yet.

### Docker status

Dockerfiles and Compose services exist for the backend, frontend, and PostgreSQL. The backend port mapping is `8000:8000`, and the frontend mapping is `3000:80`.

Full-stack startup still needs verification: Compose does not yet pass the backend's database environment variables, publish a PostgreSQL port for host access, or configure a frontend container API proxy. The PostgreSQL service expects `POSTGRES_PASSWORD` through the shell or a root `.env` file. Database data uses the `postgres_data` named volume.

## Verification

With Docker running, execute the backend test suite:

```sh
cd backend
./gradlew test
```

The existing application context test starts PostgreSQL and Redis Testcontainers. It verifies startup, Flyway migration execution, and Hibernate schema validation; it does not yet test CRUD behavior. The Day 3 run passed with both migrations applied to a fresh database.

For the backend checks used in CI:

```sh
cd backend
./gradlew clean build
```

For frontend checks:

```sh
cd Frontend
npm ci
npm run lint
npm run build
```

Both CI workflows run on relevant pull requests to `main` and pushes to `main`.

## Commit naming

Use Conventional Commits: `type(scope): description`. The scope is optional.

| Type | Purpose |
| --- | --- |
| `feat` | New functionality |
| `fix` | Bug fixes |
| `ci` | CI/CD pipeline changes |
| `build` | Build system or dependency changes |
| `refactor` | Code restructuring without changing behavior |
| `test` | Adding or modifying tests |
| `docs` | Documentation changes |
| `chore` | General maintenance |

Example: `feat(backend): add project and task persistence models`.
