/* E2E HU-037.5 - contrato de activar/inactivar usuarios */
import { test, expect } from "@playwright/test";
import {
  ADMIN_EMAIL,
  ADMIN_PASSWORD,
  BACKEND_URI,
  COMPANY_ADMIN_EMAIL,
  COMPANY_ADMIN_PASSWORD,
  authenticateByApi,
  authHeaders,
  injectAuthStorageFromLoginData,
  loginByApi,
  openModuleScreen,
  requireEnv,
} from "./helpers/e2e.shared.utils";

const env = globalThis.process?.env ?? {};
const CONTEXT_COMPANY_ID = Number(env.E2E_COMPANY_ID || 1505);
const CONTEXT_COMPANY_ROLE_ID = Number(env.E2E_COMPANY_ROLE_ID || 2);
const CONTEXT_COMPANY_NAME = env.E2E_COMPANY_NAME || "Coagrohuila";

const inactiveContextUser = {
  id: 99001,
  username: "qa.context@example.invalid",
  nombre: "Usuario",
  apellido: "Contextual",
  celular: "3000000000",
  rolPreferido: "ROLE_ADMINISTRADOR_EMPRESA",
  // Estado global activo, pero para el tenant el acceso está inactivo.
  estadoId: 4,
  estadoNombre: "ACTIVO",
  estadoContextoId: 2,
  estadoContextoNombre: "Inactivo",
  asignaciones: [
    {
      empresaId: CONTEXT_COMPANY_ID,
      empresaNombre: CONTEXT_COMPANY_NAME,
      rolNombre: "ROLE_ADMINISTRADOR_EMPRESA",
      estadoId: 2,
      estadoNombre: "Inactivo",
    },
  ],
};

const mockUserList = async (page) => {
  await page.route("**/api/v1/usuarios?**", async (route) => {
    if (route.request().method() !== "GET") return route.fallback();
    await route.fulfill({
      contentType: "application/json",
      body: JSON.stringify({ content: [inactiveContextUser], page: { totalElements: 1 } }),
    });
  });
};

const mockLegacyStatusEndpoints = async (page) => {
  await page.route("**/api/v1/usuarios/99001/activar", (route) =>
    route.fulfill({ status: 500, body: "legacy endpoint used" })
  );
  await page.route("**/api/v1/usuarios/99001", (route) =>
    route.fulfill({ status: 500, body: "legacy endpoint used" })
  );
};

const selectMockUser = async (page) => {
  const row = page.locator('[role="row"][data-id="99001"]');
  await expect(row).toBeVisible({ timeout: 15000 });
  await row.click();
  return row;
};

test("Admin Sistema activa usando estado contextual y PATCH /estado", async ({ page, request }) => {
  requireEnv("E2E_ADMIN_EMAIL", ADMIN_EMAIL);
  requireEnv("E2E_ADMIN_PASSWORD", ADMIN_PASSWORD);
  await mockUserList(page);
  await mockLegacyStatusEndpoints(page);

  await authenticateByApi(page, request, ADMIN_EMAIL, ADMIN_PASSWORD, "Usuario");
  await openModuleScreen(page, "Usuario", /[Uu]suarios/i);
  await selectMockUser(page);

  // El contrato exige Activar porque el estado contextual es Inactivo.
  await expect(page.getByRole("button", { name: /^Activar$/i })).toBeEnabled();

  const patchRequest = page.waitForRequest(
    (req) => req.method() === "PATCH" && /\/api\/v1\/usuarios\/99001\/estado$/.test(new URL(req.url()).pathname)
  );
  await page.route("**/api/v1/usuarios/99001/estado", async (route) => {
    expect(route.request().method()).toBe("PATCH");
    expect(route.request().postDataJSON()).toEqual({ activo: true });
    await route.fulfill({ contentType: "application/json", body: JSON.stringify({ cambioAplicado: true }) });
  });

  await page.getByRole("button", { name: /^Activar$/i }).click();
  const dialog = page.locator('[role="dialog"]:visible').last();
  await expect(dialog).toBeVisible();
  await dialog.getByRole("button", { name: /confirmar activaci[oó]n/i }).click();
  await patchRequest;
});

test("Admin Empresa limita y confirma la acción PATCH a su tenant", async ({ page, request }) => {
  requireEnv("VITE_BACKEND_URI", BACKEND_URI);
  requireEnv("E2E_COMPANY_ADMIN_EMAIL/E2E_ADMIN_EMAIL", COMPANY_ADMIN_EMAIL);
  requireEnv("E2E_COMPANY_ADMIN_PASSWORD/E2E_ADMIN_PASSWORD", COMPANY_ADMIN_PASSWORD);
  await mockUserList(page);
  await mockLegacyStatusEndpoints(page);

  const loginData = await loginByApi(request, COMPANY_ADMIN_EMAIL, COMPANY_ADMIN_PASSWORD);
  const switchRes = await request.post(`${BACKEND_URI}/auth/switch-context`, {
    headers: authHeaders(loginData.token),
    data: { rolId: CONTEXT_COMPANY_ROLE_ID, empresaId: CONTEXT_COMPANY_ID },
  });
  expect(switchRes.ok(), `Switch de contexto falló con estado ${switchRes.status()}`).toBeTruthy();
  const switchData = {
    ...(await switchRes.json()),
    empresaId: CONTEXT_COMPANY_ID,
    rolId: CONTEXT_COMPANY_ROLE_ID,
    empresaNombre: CONTEXT_COMPANY_NAME,
    rolesByCompany: [
      {
        empresaId: CONTEXT_COMPANY_ID,
        empresaNombre: CONTEXT_COMPANY_NAME,
        rolId: CONTEXT_COMPANY_ROLE_ID,
        rolNombre: "ROLE_ADMINISTRADOR_EMPRESA",
      },
    ],
  };
  await injectAuthStorageFromLoginData(page, switchData, "Usuario");
  await openModuleScreen(page, "Usuario", /[Uu]suarios/i);
  await selectMockUser(page);

  await page.getByRole("button", { name: /^Activar$/i }).click();
  const dialog = page.locator('[role="dialog"]:visible').last();
  await expect(dialog.getByText(new RegExp(`acción limitada a la empresa: ${CONTEXT_COMPANY_NAME}`, "i"))).toBeVisible();

  const patchRequest = page.waitForRequest(
    (req) => req.method() === "PATCH" && /\/api\/v1\/usuarios\/99001\/estado$/.test(new URL(req.url()).pathname)
  );
  await page.route("**/api/v1/usuarios/99001/estado", async (route) => {
    expect(route.request().postDataJSON()).toEqual({ activo: true });
    await route.fulfill({ contentType: "application/json", body: JSON.stringify({ cambioAplicado: true }) });
  });
  await dialog.getByRole("button", { name: /confirmar activaci[oó]n/i }).click();
  await patchRequest;
});

test("ProblemDetail: el diálogo muestra detail y no el message genérico", async ({ page, request }) => {
  requireEnv("E2E_ADMIN_EMAIL", ADMIN_EMAIL);
  requireEnv("E2E_ADMIN_PASSWORD", ADMIN_PASSWORD);
  await mockUserList(page);
  await page.route("**/api/v1/usuarios/99001/estado", (route) =>
    route.fulfill({
      status: 400,
      contentType: "application/problem+json",
      body: JSON.stringify({
        message: "Error genérico",
        detail: "No se puede activar mientras el usuario está inactivo globalmente.",
      }),
    })
  );

  await authenticateByApi(page, request, ADMIN_EMAIL, ADMIN_PASSWORD, "Usuario");
  await openModuleScreen(page, "Usuario", /[Uu]suarios/i);
  await selectMockUser(page);
  await page.getByRole("button", { name: /^Activar$/i }).click();
  const dialog = page.locator('[role="dialog"]:visible').last();
  await dialog.getByRole("button", { name: /confirmar activaci[oó]n/i }).click();

  await expect(dialog.getByText(/no se puede activar mientras el usuario está inactivo globalmente/i)).toBeVisible();
  await expect(dialog.getByText(/^error genérico$/i)).not.toBeVisible();
});
