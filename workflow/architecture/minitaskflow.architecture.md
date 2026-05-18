# MiniTaskFlow Architecture

Status: `ARCHITECTURE_CREATED`
Project slug: `minitaskflow`
Created: `2026-05-18T09:25:55Z`

## Contract

MiniTaskFlow uses the default enterprise architecture contract:

- `userIdType`: `Long`
- `javaVersion`: `21`
- `packageManager`: `npm`
- `database`: `postgres`
- `authStrategy`: `jwt`

The runtime application contract is written to `runtime/workspaces/minitaskflow/architecture-contract.json`. This contract is the source of truth for generators, validation, repair, quality review, Docker deployment, Playwright, and runtime proof.

## System Shape

MiniTaskFlow is a three-service local Docker application:

- Backend: Spring Boot 3, Java 21, Maven, JWT auth, REST APIs.
- Frontend: React + Vite, npm, browser UI for auth, dashboard, projects, and tasks.
- Database: PostgreSQL with Flyway migrations.

## Backend Architecture

The backend is organized by feature and shared infrastructure:

- `auth`: registration, login, JWT creation, JWT parsing, request authentication.
- `users`: user entity, repository, authenticated user lookup.
- `projects`: per-user project creation and listing.
- `tasks`: per-project task creation, listing, and completion.
- `dashboard`: per-user project and task counts.
- `health`: unauthenticated `/api/health` runtime endpoint.
- `config`: security and CORS configuration.

All protected APIs derive the current user from JWT authentication. User IDs are `Long` everywhere: entity IDs, repository IDs, DTO IDs, JWT subject parsing, and frontend API contracts.

## Frontend Architecture

The frontend is a compact operational UI with:

- Auth state managed in React and persisted to local storage.
- A typed API client that attaches JWT bearer tokens.
- Screens for login/register, dashboard, projects, and project task management.
- Explicit loading, empty, validation, and error states for core workflows.

The frontend is built as static assets and served by nginx in Docker. Runtime backend URL is injected through an nginx-generated `window.__MINITASKFLOW_CONFIG__` script so Docker dynamic backend ports can be used without rebuilding the frontend image.

## Data Model

- `users`: `id BIGSERIAL`, `email`, `password_hash`, `created_at`, `updated_at`.
- `projects`: `id BIGSERIAL`, `owner_id`, `name`, `created_at`, `updated_at`.
- `tasks`: `id BIGSERIAL`, `project_id`, `owner_id`, `title`, `completed`, `created_at`, `updated_at`.

Ownership is represented directly on projects and tasks to simplify authorization checks and dashboard queries.

## API Contract

- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/users/me`
- `GET /api/projects`
- `POST /api/projects`
- `GET /api/projects/{projectId}/tasks`
- `POST /api/projects/{projectId}/tasks`
- `PATCH /api/tasks/{taskId}/complete`
- `GET /api/dashboard/summary`
- `GET /api/health`

All protected endpoints return stable DTOs with `Long` IDs serialized as JSON numbers.

## Validation Strategy

Validation runs after each phase:

- Backend phases: `mvn test`.
- Frontend phases: `npm install`, `npm run build`.
- Docker phases: Dockerfiles exist, `docker compose config`, dynamic ports, build, and up.
- Playwright phase: tests run against the deployed frontend URL with one worker.
- Runtime proof: backend health, frontend HTTP response, quality score, Docker state, and Playwright result.

## Deployment Strategy

`docker-compose.yml` lives in the generated app workspace. Host ports are dynamically allocated and persisted to `runtime/port-registry.json`:

- backend: `18080-18999`
- frontend: `15000-15999`
- postgres: `55432-55999`

The Compose file uses those allocated ports and never assumes common host ports are free.
