import { defineConfig, devices } from '@playwright/test';
import 'dotenv/config';

const baseURL = process.env.E2E_BASE_URL;
const webServerURL = process.env.E2E_WEB_SERVER_URL;
const webServerCommand = process.env.E2E_WEB_SERVER_COMMAND || 'npm run dev --port 5173';

export default defineConfig({
  testDir: './tests/e2e',
  fullyParallel: false,
  forbidOnly: !!process.env.CI,
  retries: 0,
  workers: 2,
  reporter: [['list']],
  use: {
    baseURL,
    trace: 'off',
    screenshot: 'off',
    video: 'off',
    headless: true,
    bypassCSP: true,
  },
  webServer: {
    command: webServerCommand,
    url: webServerURL,
    reuseExistingServer: true,
    timeout: 120000,
  },
  projects: [
    {
      name: 'chromium',
      use: { ...devices['Desktop Chrome'], locale: 'es-CO', launchOptions: { args: ['--disable-web-security'] } },
    },
  ],
});
