# MiniTaskFlow Phase DAG

Status: `DAG_CREATED`
Created: `2026-05-18T09:25:55Z`

This DAG is created after human plan approval and after architecture creation.

## Nodes

1. `architecture-created`
   - Inputs: approved plan, durable project memory.
   - Outputs: architecture report and `architecture-contract.json`.
   - Validation: contract has Java 21, `Long` user IDs, npm, PostgreSQL, JWT.

2. `backend-scaffold`
   - Depends on: `architecture-created`.
   - Outputs: Spring Boot Maven project, health endpoint, base config.
   - Validation: `mvn test`.

3. `backend-auth-users`
   - Depends on: `backend-scaffold`.
   - Outputs: users, password hashing, JWT register/login, current-user endpoint.
   - Validation: `mvn test`.

4. `backend-projects-tasks-dashboard`
   - Depends on: `backend-auth-users`.
   - Outputs: projects, tasks, dashboard APIs, Flyway schema.
   - Validation: `mvn test`.

5. `frontend-app`
   - Depends on: `backend-projects-tasks-dashboard`.
   - Outputs: React + Vite UI, typed API client, auth/dashboard/project/task flows.
   - Validation: `npm install`, `npm run build`.

6. `docker-runtime`
   - Depends on: `frontend-app`.
   - Outputs: backend Dockerfile, frontend Dockerfile, nginx runtime config, Docker Compose with dynamic ports.
   - Validation: `docker compose config`.

7. `quality-gate`
   - Depends on: `docker-runtime`.
   - Outputs: quality report and score.
   - Validation: score `>= 9.0`.

8. `docker-deploy`
   - Depends on: `quality-gate`.
   - Outputs: local Docker deployment and deployment evidence.
   - Validation: `docker compose build`, `docker compose up -d`, `docker compose ps`.

9. `playwright-e2e`
   - Depends on: `docker-deploy`.
   - Outputs: Playwright tests and report.
   - Validation: `npx playwright test --workers=1` against Docker frontend URL.

10. `runtime-proof`
    - Depends on: `playwright-e2e`.
    - Outputs: runtime proof report.
    - Validation: backend health, frontend URL, Docker state, Playwright pass, quality pass.

11. `complete-status`
    - Depends on: `runtime-proof`.
    - Outputs: workflow status `COMPLETE`.
    - Validation: runtime proof passed.

## Self-Healing Rule

Any failed validation pauses the current node, records the failure in memory, repairs only the affected area, reruns the exact failed validation, and continues only after the validation passes.
