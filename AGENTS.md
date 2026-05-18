# Autonomous AI SDLC Factory - Codex Multi-Agent Rules

## Workspace

Codex workspace:

/Users/nanofactory/ai-sdlc-factory/codexfolder

Main factory root:

/Users/nanofactory/ai-sdlc-factory

Generated app workspace pattern:

/Users/nanofactory/ai-sdlc-factory/runtime/workspaces/<project-slug>

## Mission

Build an enterprise-grade Autonomous AI SDLC Factory.

The factory takes a user product prompt and executes:

Prompt
-> planning
-> human plan edit/approval
-> autonomous architecture
-> autonomous phase DAG
-> autonomous code generation
-> validation after every phase
-> autonomous self-healing repair
-> code quality review
-> frontend Playwright testing
-> backend compile
-> frontend build
-> Docker packaging
-> local Docker deployment
-> runtime health verification
-> final runtime-proven completion

## One Human Business Gate Only

The only human business approval gate is planning approval.

Before plan approval:
- planning_agent creates the plan
- human may edit the plan
- human must approve the plan

After plan approval:
- do not ask for architecture approval
- do not ask for phase approval
- do not ask for validation approval
- do not ask for repair approval
- do not ask for code quality approval
- do not ask for Playwright approval
- do not ask for Docker deployment approval
- do not ask for runtime proof approval

After plan approval, the system continues automatically until:
- runtime proof passes, or
- a non-repairable blocker is reached, or
- max repair attempts are exhausted.

## Final Autonomous Workflow

Requirement
-> lifecycle_router_agent
-> memory_agent
-> planning_agent
-> show plan to human
-> human edits or approves plan
-> plan approved
-> architecture_agent automatically creates architecture
-> dag_agent creates phase DAG
-> phase_runner_agent runs phases automatically
-> validation_agent tests after every phase
-> self_healing_agent fixes failures immediately
-> impact_analysis_agent runs for feature/change cases
-> code_quality_agent reviews quality
-> quality_score_agent requires >= 9/10
-> playwright_agent creates and runs frontend E2E tests
-> regression_validation_agent checks existing behavior for feature/change cases
-> docker_local_deploy_agent deploys locally using Docker Compose
-> runtime_proof_agent proves backend/frontend/Docker health
-> status_agent marks COMPLETE only after runtime proof

## Required Lifecycle Cases

The factory supports exactly three cases.

### Case A - NEW_PROJECT

Use when the user wants to create a new application.

Flow:
1. lifecycle_router_agent classifies request as NEW_PROJECT.
2. memory_agent creates durable project memory.
3. planning_agent creates the initial project plan.
4. Human approves or edits the plan.
5. After plan approval, no more human business approval.
6. architecture_agent creates architecture and architecture-contract.json.
7. dag_agent creates full project DAG.
8. phase_runner_agent runs all phases.
9. validation_agent tests after every phase.
10. self_healing_agent repairs failures.
11. code_quality_agent reviews code.
12. quality_score_agent requires score >= 9/10.
13. playwright_agent generates and runs frontend E2E tests.
14. docker_local_deploy_agent deploys locally.
15. runtime_proof_agent verifies runtime.
16. status_agent marks COMPLETE only after runtime proof.

### Case B - ADD_FEATURE

Use when the user wants to add a new feature to a completed project.

Flow:
1. lifecycle_router_agent classifies request as ADD_FEATURE.
2. memory_agent loads existing project memory.
3. impact_analysis_agent analyzes affected modules, APIs, UI, database, Docker, tests, and regression risk.
4. change_request_agent creates feature delta plan.
5. architecture_agent checks compatible architecture extension if required.
6. dag_agent creates feature delta DAG.
7. phase_runner_agent applies feature changes carefully.
8. validation_agent tests after every phase.
9. self_healing_agent repairs failures.
10. regression_validation_agent proves existing behavior still works.
11. code_quality_agent reviews changed and affected code.
12. quality_score_agent requires score >= 9/10.
13. playwright_agent updates and runs E2E tests.
14. docker_local_deploy_agent redeploys locally.
15. runtime_proof_agent proves full runtime again.
16. memory_agent saves feature history.
17. status_agent marks COMPLETE only after runtime proof.

Rules:
- Do not regenerate the whole project unnecessarily.
- Do not break existing behavior.
- Do not change core architecture unless feature requires compatible extension.
- Always run impact analysis before changing code.
- Always run regression validation after feature addition.
- Runtime proof is required again.

### Case C - CHANGE_FUNCTIONALITY

Use when the user says existing functionality is wrong, broken, incomplete, or should behave differently.

Flow:
1. lifecycle_router_agent classifies request as CHANGE_FUNCTIONALITY.
2. memory_agent loads existing project memory.
3. diagnosis_agent compares current behavior vs desired behavior.
4. impact_analysis_agent analyzes blast radius.
5. change_request_agent creates targeted behavior-change plan.
6. dag_agent creates fix delta DAG.
7. phase_runner_agent applies the smallest safe fix.
8. validation_agent validates changed behavior.
9. self_healing_agent repairs failures.
10. regression_validation_agent proves unrelated behavior still works.
11. code_quality_agent reviews changed and affected code.
12. quality_score_agent requires score >= 9/10.
13. playwright_agent updates and runs E2E tests.
14. docker_local_deploy_agent redeploys locally.
15. runtime_proof_agent proves full runtime again.
16. memory_agent saves issue, fix, and prevention lesson.
17. status_agent marks COMPLETE only after runtime proof.

Rules:
- Do not blindly rewrite large modules.
- First understand expected behavior vs actual behavior.
- Change the smallest safe surface.
- Preserve architecture contract.
- Always run impact analysis before changing code.
- Always run regression validation after the fix.
- Runtime proof is required again.

## Persistent Project Memory Rule

Do not rely only on chat context.

Before any agent works, memory_agent must load:

workflow/memory/<project-slug>/project-memory.json
workflow/memory/<project-slug>/architecture-decisions.md
workflow/memory/<project-slug>/issue-history.jsonl
workflow/memory/<project-slug>/repair-history.jsonl
workflow/memory/<project-slug>/change-history.jsonl
workflow/memory/<project-slug>/runtime-history.jsonl
workflow/memory/<project-slug>/quality-history.jsonl
workflow/memory/<project-slug>/playwright-history.jsonl
workflow/memory/<project-slug>/impact-history.jsonl
workflow/memory/<project-slug>/lessons-learned.md

If files do not exist, create them.

Every agent must read:
- approved plan
- architecture contract
- project memory
- issue history
- repair history
- change history
- runtime history
- quality history
- Playwright history
- lessons learned

before making changes.

## Mandatory Memory Writes

memory_agent must save memory after:
- requirement received
- plan created
- plan edited
- plan approved
- architecture created
- DAG created
- impact analysis completed
- phase started
- phase completed
- phase failed
- validation failed
- repair attempted
- repair succeeded
- repair failed
- code quality review completed
- quality score failed
- quality score passed
- Playwright tests created
- Playwright tests failed
- Playwright tests passed
- Docker deployment attempted
- runtime proof attempted
- runtime proof passed
- runtime proof failed
- feature request received
- functionality change request received
- feature completed
- functionality change completed

## Architecture Contract Rule

Default enterprise architecture contract:

{
  "userIdType": "Long",
  "javaVersion": 21,
  "packageManager": "npm",
  "database": "postgres",
  "authStrategy": "jwt"
}

All generator, validation, repair, quality, and testing agents must read architecture-contract.json before changing code.

If memory conflicts with architecture-contract.json:
- architecture contract wins
- write conflict to memory
- do not silently change architecture

## Self-Healing Autonomous Rule

Every validation, quality, Playwright, Docker, runtime, or regression issue must trigger self_healing_agent automatically.

self_healing_agent must:
- classify the failure
- read architecture contract
- read previous similar failures from memory
- repair only the affected area
- replace structured/code files as whole files
- never append duplicate Java/XML/YAML/JSON/TS/TSX/Docker content
- rerun the exact failed validation
- save repair attempt to memory
- add prevention rule to lessons learned
- stop on max attempts
- stop on repeated error signature
- stop on no progress

The system must not continue to the next phase while the current phase is failed.

## Failure Categories

Allowed issue categories:

- BACKEND_COMPILE_ERROR
- BACKEND_TEST_ERROR
- FRONTEND_BUILD_ERROR
- FRONTEND_TEST_ERROR
- PLAYWRIGHT_E2E_ERROR
- TYPE_CONSISTENCY_ERROR
- ID_TYPE_MISMATCH
- DTO_CONTRACT_DRIFT
- API_CONTRACT_DRIFT
- JWT_SUBJECT_MISMATCH
- DATABASE_SCHEMA_MISMATCH
- MIGRATION_ERROR
- PACKAGE_MANAGER_ERROR
- CODE_QUALITY_BELOW_9
- SECURITY_QUALITY_ERROR
- MAINTAINABILITY_ERROR
- DOCKERFILE_MISSING
- DOCKER_JAVA_VERSION_MISMATCH
- DOCKER_COMPOSE_CONFIG_ERROR
- DOCKER_PORT_COLLISION
- DOCKER_BUILD_ERROR
- DOCKER_RUNTIME_ERROR
- BACKEND_HEALTH_FAILED
- FRONTEND_HEALTH_FAILED
- FEATURE_REGRESSION
- FUNCTIONALITY_BEHAVIOR_MISMATCH
- UNKNOWN_REPAIRABLE_ERROR
- NON_REPAIRABLE_BLOCKER

## Impact Analysis Rule

For ADD_FEATURE and CHANGE_FUNCTIONALITY, impact_analysis_agent must run before code changes.

Impact analysis must identify:
- affected backend modules
- affected frontend modules/pages/components
- affected API endpoints
- affected DTOs/types
- affected database tables/migrations
- affected auth/roles/permissions
- affected Docker/runtime config
- affected tests
- affected Playwright flows
- affected existing critical flows
- regression risk
- files likely to change
- files that must not change
- safest implementation strategy

For CHANGE_FUNCTIONALITY, impact analysis must also compare:
- current behavior
- desired behavior
- behavior gap
- root cause hypothesis
- smallest safe fix

The factory must not start code changes until impact analysis is written.

Impact analysis output:

workflow/impact-analysis/<project-slug>.<timestamp>.impact-analysis.md
workflow/impact-analysis/<project-slug>.<timestamp>.impact-analysis.json

## Code Quality Gate

Every generated or changed project must pass code quality review.

code_quality_agent must review:
- correctness
- architecture alignment
- maintainability
- readability
- type safety
- security
- error handling
- testability
- performance basics
- frontend UX quality
- API contract cleanliness
- database design consistency
- Docker/runtime quality
- observability/logging basics

quality_score_agent must assign a score from 0 to 10.

Minimum accepted score:

9.0

If score is below 9.0:
- status is CODE_QUALITY_FAILED
- issueCategory is CODE_QUALITY_BELOW_9
- self_healing_agent must improve the code
- code_quality_agent must review again
- continue only when score >= 9.0

A score is not enough by itself. The quality gate must include concrete evidence:
- lint results if available
- type check results if available
- backend compile/test results
- frontend build/test results
- Playwright results
- code review report
- security notes
- maintainability notes

Output files:

workflow/code-quality/<project-slug>.quality-report.md
workflow/code-quality/<project-slug>.quality-score.json

## Playwright Frontend Testing Rule

Every generated frontend must include Playwright E2E tests.

playwright_agent must:
- install or configure Playwright for the frontend
- create smoke tests
- create auth/navigation tests when auth exists
- create feature tests for generated features
- create regression tests for changed functionality
- run tests against local Docker deployment URL
- save reports and traces where possible
- call self_healing_agent if tests fail

Required commands, adapted to the project package manager:

npm install -D @playwright/test
npx playwright install
npx playwright test

For CI-like stable execution, use one worker when needed:

npx playwright test --workers=1

Playwright tests must run after local Docker deployment because the frontend and backend must be tested together through real browser flows.

Output files:

workflow/playwright/<project-slug>.playwright-plan.md
workflow/playwright/<project-slug>.playwright-result.json
workflow/playwright/<project-slug>.playwright-report.md

## Test After Every Phase Rule

The validation_agent must run after every phase.

Do not wait until final build.

Backend-related phase checks:
- Maven compile/package if backend code changed
- backend tests if present
- Java type consistency
- entity/repository/DTO ID consistency
- JWT subject type consistency
- migration/schema consistency
- health endpoint existence

Frontend-related phase checks:
- package install result
- node_modules existence
- package manager consistency
- type check if configured
- frontend tests if configured
- frontend build
- frontend/backend API type consistency

Docker-related phase checks:
- backend/Dockerfile exists
- frontend/Dockerfile exists
- docker-compose.yml exists
- docker compose config passes
- Docker Java version matches Maven compiler release
- no hardcoded conflicting host ports

Quality phase checks:
- code quality score >= 9.0
- maintainability report exists
- self-healing performed if score < 9.0

Playwright phase checks:
- Playwright tests exist
- Playwright browser install completed
- Playwright tests pass against local Docker URL

## Regression Validation Rule

For ADD_FEATURE and CHANGE_FUNCTIONALITY, regression_validation_agent must run before final runtime proof.

Regression checks must include:
- backend compile/package
- backend tests if present
- frontend build
- frontend tests if present
- Playwright smoke tests
- Playwright affected-flow tests
- API contract compatibility
- architecture contract consistency
- database migration compatibility
- Docker Compose config
- existing health endpoints
- existing critical flows from project memory

The project must not be marked COMPLETE unless:
- changed behavior works
- old critical behavior still works
- quality score >= 9.0
- Playwright passes
- local Docker runtime proof passes

## Dynamic Local Docker Deployment Rule

The generated project must deploy locally using Docker Compose.

Do not hardcode host ports:
- do not assume 5432 is free
- do not assume 8080 is free
- do not assume 5173 or 5175 is free

Use dynamic host ports and persist them to:

runtime/port-registry.json

Preferred ranges:
- backend: 18080-18999
- frontend: 15000-15999
- postgres: 55432-55999

docker_local_deploy_agent must:
- validate backend/Dockerfile
- validate frontend/Dockerfile
- validate docker-compose.yml
- allocate ports
- update docker-compose.yml
- run docker compose config
- run docker compose build
- run docker compose up -d
- run docker compose ps
- save deployment evidence

## Runtime-Proven Completion Rule

Never mark project COMPLETE because DAG phases passed.

If all DAG phases pass but runtime proof has not passed, status must be:

DAG_COMPLETE_RUNTIME_PENDING

A project may be COMPLETE only after runtime_proof_agent proves:
- backend compile/package passes
- frontend install/build passes
- code quality score >= 9.0
- Playwright E2E tests pass
- docker compose config passes
- docker compose build passes
- docker compose up succeeds
- backend health endpoint responds
- frontend URL responds

If runtime proof fails, status must be:

RUNTIME_FAILED

Only runtime_proof_agent can set:

COMPLETE

## Mandatory Security Baseline Rule

Every generated enterprise project must include a security architecture.

Required auth modes:
- username + password
- backend-issued JWT
- Google login / Google OAuth / Google Identity integration

Default security contract:

{
  "authStrategy": "jwt",
  "authProviders": ["local_password", "google"],
  "passwordHashing": "bcrypt_or_delegating_password_encoder",
  "tokenType": "first_party_backend_jwt",
  "jwtIssuer": "<project-slug>",
  "jwtSubjectType": "Long",
  "googleIdentityKey": "googleSub",
  "useGoogleSubAsStableIdentifier": true,
  "secretsSource": "environment_variables",
  "securityTestingRequired": true
}

Rules:
- Do not store plaintext passwords.
- Do not hardcode JWT secrets.
- Do not hardcode Google client secrets.
- JWT secret, Google client ID, and Google client secret must come from environment variables or Docker secrets.
- Backend must own final app session/JWT creation.
- Google login must map Google identity to a local user record.
- Google `sub` must be stored as the stable provider identifier.
- Email may be used for display/login lookup, but not as the stable Google identity key.
- All protected APIs must require authentication.
- Project/task APIs must enforce owner-level authorization.
- Admin APIs, if generated, must enforce role-level authorization.
- `/api/health` may remain public.
- Auth errors must return safe messages and must not leak whether a specific account exists.

## Security Architecture Requirements

For Spring Boot projects, generated backend security must include:
- Spring Security configuration
- password encoder
- username/password registration
- username/password login
- JWT creation
- JWT validation filter
- authenticated current-user endpoint
- Google login endpoint or OAuth callback handling
- local user mapping for Google accounts
- protected routes
- ownership authorization checks
- security tests

Required backend endpoints:
- `POST /api/auth/register`
- `POST /api/auth/login`
- `POST /api/auth/google`
- `GET /api/users/me`
- `POST /api/auth/logout` if cookie/session mode is used
- `GET /api/health`

Recommended user table fields:
- `id BIGSERIAL`
- `email VARCHAR UNIQUE`
- `username VARCHAR UNIQUE NULL`
- `password_hash VARCHAR NULL`
- `auth_provider VARCHAR NOT NULL`
- `google_sub VARCHAR UNIQUE NULL`
- `email_verified BOOLEAN`
- `role VARCHAR NOT NULL`
- `created_at`
- `updated_at`

Allowed auth providers:
- `LOCAL`
- `GOOGLE`

Rules:
- Local users must have password hash.
- Google-only users may have null password hash.
- A single email may be linked carefully only when verified.
- Google account linking must not overwrite an existing local account without explicit safe linking logic.

## JWT Requirements

Backend-issued JWTs must include:
- subject as local user ID, matching architecture `userIdType`
- issuer
- issued-at
- expiry
- role/authorities
- token type

Validation must check:
- signature
- expiry
- issuer
- subject parse as `Long`
- user exists
- user is active

JWT validation failures must produce 401.
Authorization failures must produce 403.

## Google Auth Requirements

Google auth must support environment-driven configuration:

Required environment variables:
- `GOOGLE_CLIENT_ID`
- `GOOGLE_CLIENT_SECRET` if OAuth authorization-code flow is used
- `GOOGLE_REDIRECT_URI` if redirect flow is used

For local Docker:
- app must start even if Google credentials are missing
- Google login UI should show disabled/configuration-required state if credentials are missing
- runtime proof should not fail only because real Google credentials are absent
- security validation must still verify that Google auth code path exists and secrets are not hardcoded

Google ID token verification must validate:
- CSRF token for Google Identity Services POST flow
- token signature
- `aud` equals configured client ID
- `iss` is valid Google issuer
- `exp` has not passed
- `sub` exists and is used as stable external identity

## Security Validation Rule

validation_agent must run security validation after every security-related phase.

Security checks:
- password hashing configured
- no plaintext password storage
- no hardcoded JWT secret
- no hardcoded Google client secret
- protected endpoints reject unauthenticated requests
- cross-user project/task access is rejected
- JWT subject type matches architecture contract
- JWT expiry exists
- Google `sub` is used for Google identity
- Google email is not used as primary stable provider identifier
- CORS is limited to configured frontend origin
- `/api/health` remains public
- generated tests cover register/login/me/protected endpoint behavior

If any security validation fails:
- classify as SECURITY_QUALITY_ERROR, JWT_SUBJECT_MISMATCH, AUTHORIZATION_ERROR, GOOGLE_AUTH_ERROR, or CODE_QUALITY_BELOW_9
- call self_healing_agent
- rerun the exact failing security validation

## Security Code Quality Rule

code_quality_agent must include security in the quality score.

Security quality dimensions:
- password storage
- JWT correctness
- Google auth correctness
- secret handling
- authorization checks
- error handling
- CORS
- test coverage
- frontend auth UX
- Playwright auth coverage

A project cannot score 9.0 or above if:
- passwords are stored in plaintext
- JWT secret is hardcoded
- Google client secret is hardcoded
- protected APIs are not protected
- owner authorization is missing
- Google identity uses email instead of `sub` as stable provider ID
- auth tests are missing

## Playwright Security Testing Rule

playwright_agent must test auth flows.

Required Playwright coverage:
- registration flow
- username/password login flow
- logout flow if logout exists
- protected dashboard requires login
- user can create project only after login
- user can create task only after login
- Google login button or configured Google login entrypoint exists
- if Google credentials are missing, UI shows a safe disabled/configuration-required message
- if Google test credentials/mock mode is configured, Google login flow is tested

Playwright may reuse authenticated state for multiple tests when appropriate.
