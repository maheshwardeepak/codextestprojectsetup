# MiniTaskFlow Architecture Decisions

## 2026-05-18T09:25:55Z - Initial Architecture

Status: `ARCHITECTURE_CREATED`

Decisions:
- Use Spring Boot 3 on Java 21 for the backend.
- Use React + Vite with npm for the frontend.
- Use PostgreSQL with Flyway migrations for persistence.
- Use JWT bearer authentication for protected APIs.
- Use `Long` IDs for users, projects, tasks, DTOs, repositories, and JWT subject parsing.
- Serve the frontend through nginx in Docker and inject the backend API URL at container startup.
- Use dynamic host ports persisted in `runtime/port-registry.json`.

Architecture artifacts:
- `workflow/architecture/minitaskflow.architecture.md`
- `workflow/architecture/minitaskflow.architecture.json`
- `runtime/workspaces/minitaskflow/architecture-contract.json`
