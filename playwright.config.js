import { defineConfig, devices } from '@playwright/test';
import 'dotenv/config';

const baseURL = process.env.E2E_BASE_URL || 'http://127.0.0.1:5173/coagronet';
const webServerURL = process.env.E2E_WEB_SERVER_URL || 'http://127.0.0.1:5173/coagronet';
const webServerCommand = process.env.E2E_WEB_SERVER_COMMAND || 'npm run dev -- --host 127.0.0.1 --port 5173';

export default defineConfig({
  testDir: './tests/e2e',
  fullyParallel: false,
  forbidOnly: !!process.env.CI,
  retries: process.env.CI ? 2 : 0,
  workers: process.env.CI ? 1 : undefined,
  reporter: [['list'], ['html', { open: 'never' }]],
  use: {
    baseURL,
    trace: 'retain-on-failure',
    screenshot: 'only-on-failure',
    video: 'retain-on-failure',
    headless: true,
  },
  webServer: {
    command: webServerCommand,
    url: webServerURL,
    reuseExistingServer: !process.env.CI,
    timeout: 120000,
  },
  projects: [
    {
      name: 'chromium',
      use: { ...devices['Desktop Chrome'] },
    },
  ],
});
