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
- [x] Implement project CRUD endpoints with request validation and basic error responses.
- [ ] Verify project CRUD behavior through HTTP tests.
- [x] Implement task CRUD endpoints, enum values, due dates, and project associations.
- [x] Support moving tasks between projects and test task service behavior.
- [ ] Verify task CRUD behavior through HTTP tests.
- [ ] Replace the frontend starter screen with project forms and a Kanban board.
- [ ] Complete Compose runtime configuration and verify one-command startup.
- [ ] Add authentication, authorization, and security tests.
- [ ] Add monitoring and deploy a public demo.

The Day 3 database/domain foundation and Day 4–5 project/task CRUD implementation are in place. Task service tests cover defaults, enum updates, project reassignment, and missing records. HTTP endpoint verification remains outstanding; the Kanban interface is still planned.

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
  src/main/java/                 Project/task controllers, DTOs, services, repositories, and entities
  src/main/resources/            Configuration and Flyway SQL migrations
  src/test/java/                 Application context test, Testcontainers setup, and task service tests
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

Open the URL printed by Vite. The development proxy currently forwards `/api` requests to port 8080; its target in `Frontend/vite.config.ts` needs to be aligned with backend port 8000 before API integration. Project CRUD endpoints are implemented, but the frontend is still the starter screen.

### Docker status

Dockerfiles and Compose services exist for the backend, frontend, and PostgreSQL. The backend port mapping is `8000:8000`, and the frontend mapping is `3000:80`.

Full-stack startup still needs verification: Compose does not yet pass the backend's database environment variables, publish a PostgreSQL port for host access, or configure a frontend container API proxy. The PostgreSQL service expects `POSTGRES_PASSWORD` through the shell or a root `.env` file. Database data uses the `postgres_data` named volume.

## Project API

Base URL for local development: `http://localhost:8000/api/projects`.

| Method | Path | Controller behavior |
| --- | --- | --- |
| `POST` | `/api/projects` | Create a project; return `201` and the saved project |
| `GET` | `/api/projects` | List projects; return `200` |
| `GET` | `/api/projects/id/{id}` | Return a project with `200`, or `404` if missing |
| `PUT` | `/api/projects/{id}` | Update name and description; return `200`, or `404` if missing |
| `DELETE` | `/api/projects/{id}` | Delete a project; return `200`, or `404` if missing |

Create and update requests use the same JSON structure:

```json
{
  "name": "FlowBoard",
  "description": "My DevOps portfolio project"
}
```

The name must contain a non-whitespace character and have at most 100 characters. The description is optional and has a maximum length of 1,000 characters. Request validation rejects invalid values with `400`. The server generates project UUIDs and timestamps; requests only supply name and description. A PUT request replaces both editable fields; omitting description clears it.

These are the controller's intended responses. Spring Security is enabled with its default configuration, so HTTP testing must account for authentication and CSRF protection on write requests. A custom API security configuration is not implemented yet.

## Task API

Base URL for local development: `http://localhost:8000/api/tasks`.

| Method | Path | Controller behavior |
| --- | --- | --- |
| `POST` | `/api/tasks` | Create a task for an existing project; return `201` |
| `GET` | `/api/tasks` | List tasks; return `200` |
| `GET` | `/api/tasks/id/{id}` | Return a task with `200`, or `404` if missing |
| `PUT` | `/api/tasks/{id}` | Update a task and its project association; return `200` |
| `DELETE` | `/api/tasks/{id}` | Delete a task; return `200`, or `404` if missing |

Create and update requests use this JSON structure; replace `projectId` with an existing project's UUID:

```json
{
  "title": "Build the task API",
  "description": "Implement and verify task CRUD",
  "status": "IN_PROGRESS",
  "priority": "HIGH",
  "projectId": "b3b1ed05-304c-40ce-a737-fd31a8e0f017",
  "dueDate": "2026-11-01"
}
```

- `title` is required, must not be blank, and has a maximum length of 150 characters.
- `description` is optional and has a maximum length of 2,000 characters.
- `status` accepts `TODO`, `IN_PROGRESS`, or `DONE`.
- `priority` accepts `LOW`, `MEDIUM`, `HIGH`, or `URGENT`.
- `projectId` is required. The service looks up the project; the request does not include a project entity.
- `dueDate` is optional and uses `YYYY-MM-DD` format.

Creation defaults to `TODO` and `MEDIUM` when status or priority is omitted or null. Updates preserve existing status and priority when omitted or null; title is required, and omitted description or due date clears those fields. Supplying a different existing `projectId` moves the task to that project.

Invalid request fields or unknown enum names are rejected with `400`; missing tasks or referenced projects produce `404`. As with the project API, authentication and CSRF configuration must be accounted for during HTTP testing. These endpoints have not yet been verified through HTTP tests.

## Verification

With Docker running, execute the backend test suite:

```sh
cd backend
./gradlew test
```

The application context test starts PostgreSQL and Redis Testcontainers and checks startup, Flyway migration execution, and Hibernate schema validation. Task service unit tests use mocked repositories to cover creation defaults, explicit enums and due dates, missing projects, enum updates, moving tasks, and rejected updates. These tests do not verify HTTP behavior or task CRUD against a real database.

Before closing Day 4, verify these HTTP scenarios:

- Create a valid project and confirm the generated ID and timestamps.
- List projects and retrieve the saved project by ID.
- Update its name and description, then retrieve it to confirm persistence.
- Submit a blank name or fields exceeding their length limits and confirm `400`.
- Get, update, and delete a missing project and confirm `404`.
- Delete an existing project and confirm subsequent retrieval returns `404`.

For Day 5, also verify task creation defaults, valid and invalid enum values, due-date serialization, CRUD persistence, and moving a task to a different project. Confirm missing tasks and destination projects return `404`.

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

## Branch workflow

Use a feature branch for each task, such as `feat/project-crud` or `feat/task-crud`. Commit the relevant files, push the branch, and open a pull request into `main`. Review the diff and wait for applicable CI checks before merging.

Git pushes commits, so unfinished work can stay local and uncommitted while a completed feature branch is pushed. Use `git stash push -u` to temporarily save tracked and untracked changes before switching branches, and `git stash pop` to restore them on the next feature branch.

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
