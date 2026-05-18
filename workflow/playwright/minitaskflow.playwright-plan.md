# MiniTaskFlow Playwright Plan

Generated: `2026-05-18T12:40:39Z`
Target: `http://localhost:15000`
Command: `npx playwright test --workers=1`

## Coverage

- Protected workspace check: unauthenticated users see the auth screen and cannot access project/task controls.
- Google auth UX: Google login button exists and shows safe configuration-required state when credentials are absent.
- Registration flow: user registers with username/password credentials.
- Authenticated project/task flow: user creates a project, creates a task, completes it, and dashboard counts update.
- Login flow: registered user signs out and logs back in with password credentials.

## Stability

- Runs with one worker.
- Uses unique email addresses per test.
- Runs against the Docker frontend URL, which uses the Docker backend URL through runtime `config.js`.
