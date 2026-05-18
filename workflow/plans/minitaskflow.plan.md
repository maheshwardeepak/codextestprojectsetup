# MiniTaskFlow Human Approval Plan

Status: `PLAN_APPROVED`  
Project slug: `minitaskflow`  
Lifecycle case: `NEW_PROJECT`  
Created: `2026-05-18T07:22:06Z`
Approved: `2026-05-18T07:35:10Z`
Post-approval work started by this update: no

## Product Summary

MiniTaskFlow is a small enterprise task management application for local Docker validation. It will provide JWT-secured user access, project creation, task creation inside projects, task completion, dashboard counts, a backend health endpoint, and a frontend that loads successfully in a browser.

This is a planning artifact only. No architecture, architecture contract, DAG, application code, or Docker deployment is created before explicit human approval of this plan.

## Scope

In scope:
- User registration and login.
- Authenticated project creation and listing.
- Task creation within a project.
- Marking tasks as completed.
- Dashboard counts for projects and tasks.
- Backend health endpoint at `/api/health`.
- React + Vite frontend that supports the core workflows.
- PostgreSQL persistence.
- Docker Compose local runtime validation.
- Playwright E2E coverage for the generated frontend.

Out of scope for the initial build:
- Multi-tenant organizations.
- Admin console.
- Email notifications.
- File attachments.
- Comments, labels, due dates, and task assignment.
- External OAuth or SSO.
- Production cloud deployment.

## Roles And Users

- Unauthenticated visitor: can register and log in.
- Authenticated user: can manage their own projects and tasks, view dashboard counts, and log out.
- Runtime operator: can validate backend health, Docker Compose state, quality reports, and Playwright results.

## Modules

- Users: registration identity, password handling, authenticated user lookup.
- Auth: JWT login, token issuance, protected API access, frontend auth state.
- Projects: authenticated project creation and retrieval.
- Tasks: authenticated task creation inside projects, task listing, completion status update.
- Dashboard: per-user project and task count summary.
- Health check endpoint: backend runtime health response for `/api/health`.

## User Flows

1. A visitor registers with valid credentials, receives an authenticated session, and can access the app.
2. A registered user logs in and receives a JWT-backed session.
3. An authenticated user creates a project and sees it in the project list.
4. An authenticated user opens a project, creates a task, and sees the task inside that project.
5. An authenticated user marks a task as completed and sees the completed state reflected in the UI.
6. An authenticated user views a dashboard showing project count and task count.
7. A runtime operator calls `/api/health` and receives a successful backend health response.
8. A browser opens the frontend URL and the app renders successfully.

## Features

- Secure registration and login.
- JWT-protected API requests.
- Project create/list experience.
- Task create/list/complete experience.
- Dashboard summary counts.
- Health endpoint for runtime proof.
- Basic loading, empty, error, and validation states for user-facing flows.

## Backend Requirements

- Spring Boot backend targeting Java 21.
- JWT authentication with protected project, task, dashboard, and user surfaces.
- Passwords stored only as secure hashes.
- API responses use stable DTO contracts suitable for frontend consumption.
- Authenticated data access is scoped to the current user.
- Health endpoint available at `GET /api/health` without requiring login.
- Expected API surfaces for planning:
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

## Frontend Requirements

- React + Vite frontend using npm.
- Browser routes or screens for login, registration, dashboard, projects, and project tasks.
- Frontend stores and sends the JWT for protected API calls.
- Project and task forms provide basic validation and user-readable error states.
- Dashboard displays project count, total task count, and completed task count.
- UI must be simple, responsive, and suitable for repeated workflow validation.
- Frontend must build successfully and load through the Docker Compose runtime URL.

## Data Entities

- User: identity, credential hash, and audit timestamps.
- Project: name, owner user, and audit timestamps.
- Task: title, completion status, parent project, owner user, and audit timestamps.
- Dashboard summary: derived per-user counts from project and task data.

## Database Requirements

- PostgreSQL is the required database.
- Schema must persist users, projects, and tasks with ownership relationships.
- Migrations must be reproducible in local Docker runtime.
- Data access must prevent one user from reading or modifying another user's projects or tasks.
- Database configuration must be environment-driven for Docker Compose.

## Docker And Runtime Requirements

- Runtime uses Docker Compose with backend, frontend, and PostgreSQL services.
- Host ports must be dynamically allocated and persisted to `runtime/port-registry.json`.
- Preferred dynamic ranges:
  - backend: `18080-18999`
  - frontend: `15000-15999`
  - postgres: `55432-55999`
- Docker Compose config, build, and up must pass before runtime proof.
- Runtime proof must verify backend health and frontend browser accessibility.

## Quality Requirements

- Code quality score must be at least `9.0 / 10`.
- Review must cover correctness, architecture alignment, maintainability, readability, type safety, security, error handling, testability, performance basics, frontend UX quality, API contract cleanliness, database consistency, Docker/runtime quality, and observability basics.
- Evidence must include backend compile/test results, frontend install/build/test results where available, Playwright results, security notes, maintainability notes, and code review report.
- Any validation, quality, Playwright, Docker, or runtime failure after approval must trigger autonomous self-healing before continuing.

## Playwright Testing Requirements

- Playwright must be configured for the frontend.
- Required E2E coverage:
  - frontend smoke load
  - registration
  - login
  - project creation
  - task creation inside a project
  - mark task completed
  - dashboard count verification
- Playwright must run against the local Docker deployment URL after Docker Compose deployment.
- Use one worker when needed for stable CI-like execution.

## Validation Gates

- Plan approval is the only human business gate.
- After approval, architecture, DAG creation, implementation, validation, repair, quality review, Playwright, Docker deployment, and runtime proof proceed autonomously.
- Validation runs after every generated phase.
- Backend-related phases must pass compile/package and tests when present.
- Frontend-related phases must pass npm install, type checks where configured, tests where present, and build.
- Docker phases must pass Dockerfile checks, Docker Compose config, build, up, and service health.
- Quality phase must pass score `>= 9.0`.
- Playwright phase must pass against the Docker runtime URL.
- COMPLETE status is allowed only after runtime proof passes.

## Acceptance Criteria

- Human explicitly approves this plan, or edits it before approval.
- No architecture, DAG, or application code exists as a result of this planning step.
- After approval, the generated app allows a user to register and log in.
- An authenticated user can create a project.
- An authenticated user can create a task inside a project.
- An authenticated user can mark a task as completed.
- Dashboard shows correct project, total task, and completed task counts for the authenticated user.
- `GET /api/health` returns a successful backend health response.
- Frontend loads successfully in a browser from the Docker runtime URL.
- Backend compile/package passes.
- Frontend install/build passes.
- PostgreSQL migrations apply cleanly.
- Docker Compose config, build, and up pass using dynamic host ports.
- Playwright E2E tests pass against the Docker runtime URL.
- Code quality score is at least `9.0 / 10`.
- Runtime proof passes before status can become COMPLETE.

## Risks

- JWT subject type or user ID type mismatch between auth, DTOs, and persistence.
- API contract drift between Spring Boot DTOs and React client types.
- Incorrect ownership checks could expose projects or tasks across users.
- Dashboard counts could be wrong if task completion filtering is inconsistent.
- Docker host port collisions if dynamic allocation is not applied correctly.
- Frontend runtime environment variables could point to the wrong backend URL.
- Playwright tests may be flaky if auth state, timing, or Docker startup readiness is not handled carefully.

## Phase List For Post-Approval Automation

This is a high-level execution sequence for human planning visibility only. It is not a DAG.

1. Autonomous architecture creation and architecture contract generation.
2. Autonomous phase DAG creation.
3. Backend and frontend project scaffold generation.
4. Auth and users implementation.
5. Projects and tasks implementation.
6. Dashboard and health endpoint implementation.
7. Validation and self-healing after each phase.
8. Code quality review and score gate.
9. Playwright test creation and execution.
10. Docker Compose local deployment.
11. Runtime proof for backend health, frontend load, and integrated Docker runtime.
12. Final status update to COMPLETE only after runtime proof passes.

## Post-Approval Autonomous Workflow Summary

When the human approves this plan, the factory continues automatically with `architecture_agent`, then `dag_agent`, phase execution, validation after every phase, self-healing on failures, quality score enforcement, Playwright testing, Docker Compose deployment, runtime proof, memory updates, and final completion status. No architecture, phase, validation, repair, code quality, Playwright, Docker, or runtime proof approval will be requested after plan approval.
