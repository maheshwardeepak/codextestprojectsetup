import { expect, test } from '@playwright/test';

test('auth screen protects the workspace and shows Google configuration state', async ({ page }) => {
  await page.goto('/');

  await expect(page.getByTestId('auth-screen')).toBeVisible();
  await expect(page.getByTestId('project-name')).toHaveCount(0);
  await expect(page.getByTestId('task-title')).toHaveCount(0);
  await expect(page.getByTestId('google-login')).toBeDisabled();
  await expect(page.getByTestId('google-config-message')).toHaveText('Google login requires configuration.');
});

test('user can register, create a project, create and complete a task, then see dashboard counts', async ({ page }) => {
  const stamp = Date.now();
  const email = `flow-${stamp}@example.com`;

  await page.goto('/');
  await expect(page.getByTestId('auth-screen')).toBeVisible();

  await page.getByTestId('auth-email').fill(email);
  await page.getByTestId('auth-password').fill('correct-password');
  await page.getByTestId('auth-submit').click();

  await expect(page.getByTestId('app-screen')).toBeVisible();
  await expect(page.getByText(email)).toBeVisible();

  await page.getByTestId('project-name').fill(`Validation Project ${stamp}`);
  await page.getByTestId('create-project').click();
  await expect(page.getByTestId('project-count')).toHaveText('1');

  await page.getByTestId('task-title').fill('Run runtime proof');
  await page.getByTestId('create-task').click();
  await expect(page.getByTestId('task-count')).toHaveText('1');

  await page.getByRole('button', { name: 'Complete' }).click();
  await expect(page.getByTestId('completed-count')).toHaveText('1');
  await expect(page.locator('[data-testid^="task-completed-"]')).toHaveText('Completed');
});

test('registered user can sign out and log back in with password', async ({ page }) => {
  const stamp = Date.now();
  const email = `login-${stamp}@example.com`;
  const password = 'correct-password';

  await page.goto('/');
  await page.getByTestId('auth-email').fill(email);
  await page.getByTestId('auth-password').fill(password);
  await page.getByTestId('auth-submit').click();
  await expect(page.getByTestId('app-screen')).toBeVisible();

  await page.getByTestId('sign-out').click();
  await expect(page.getByTestId('auth-screen')).toBeVisible();
  await page.getByRole('button', { name: 'Use existing account' }).click();
  await page.getByTestId('auth-email').fill(email);
  await page.getByTestId('auth-password').fill(password);
  await page.getByTestId('auth-submit').click();

  await expect(page.getByTestId('app-screen')).toBeVisible();
  await expect(page.getByText(email)).toBeVisible();
});
