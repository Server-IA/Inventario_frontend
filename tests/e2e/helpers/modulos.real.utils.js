import { expect } from '@playwright/test';

export const ADMIN_EMAIL = process.env.E2E_ADMIN_EMAIL;
export const ADMIN_PASSWORD = process.env.E2E_ADMIN_PASSWORD;
export const NOADMIN_EMAIL = process.env.E2E_NOADMIN_EMAIL;
export const NOADMIN_PASSWORD = process.env.E2E_NOADMIN_PASSWORD;
export const BACKEND_URI = process.env.VITE_BACKEND_URI;

export function requireEnv(name, value) {
  if (!value) {
    throw new Error(`Falta variable de entorno requerida: ${name}`);
  }
}

export async function authenticateByApi(page, request, email, password) {
  requireEnv('VITE_BACKEND_URI', BACKEND_URI);

  const response = await request.post(`${BACKEND_URI}/auth/v2/login`, {
    data: { username: email, password },
  });

  expect(response.ok(), `Login API falló con estado ${response.status()}`).toBeTruthy();

  const data = await response.json();
  const token = data?.token;

  expect(token, 'No se recibió token en login').toBeTruthy();

  const expiration = Date.now() + 3 * 60 * 60 * 1000;

  await page.addInitScript(
    ({ jwt, exp }) => {
      localStorage.setItem('token', jwt);
      localStorage.setItem('token_expiration', String(exp));
      localStorage.setItem('activeModule', 'modulo');
      localStorage.setItem('activeMenu', 'Seguridad');
      localStorage.setItem('tipoAplicacion', 'web');
      window.sessionStorage.setItem('e2e', 'true');
    },
    { jwt: token, exp: expiration }
  );
}

export async function loginApiGetToken(request, email, password) {
  requireEnv('VITE_BACKEND_URI', BACKEND_URI);

  const response = await request.post(`${BACKEND_URI}/auth/v2/login`, {
    data: { username: email, password },
  });

  expect(response.ok(), `Login API falló con estado ${response.status()}`).toBeTruthy();

  const data = await response.json();
  const token = data?.token;
  expect(token, 'No se recibió token en login').toBeTruthy();
  return token;
}

export async function loginAsAdminGetToken(request) {
  requireEnv('E2E_ADMIN_EMAIL', ADMIN_EMAIL);
  requireEnv('E2E_ADMIN_PASSWORD', ADMIN_PASSWORD);
  return loginApiGetToken(request, ADMIN_EMAIL, ADMIN_PASSWORD);
}

export function authHeaders(token) {
  return {
    Authorization: `Bearer ${token}`,
    'Content-Type': 'application/json',
    'Accept-Language': 'es',
  };
}

function unwrap(data) {
  return Array.isArray(data) ? data : data?.content ?? [];
}

export async function fetchModulos(request, token) {
  const response = await request.get(`${BACKEND_URI}/api/v1/modulos`, {
    headers: authHeaders(token),
  });

  expect(response.ok(), `GET /api/v1/modulos falló con estado ${response.status()}`).toBeTruthy();
  const data = await response.json();
  return unwrap(data);
}

export async function fetchCatalogIds(request, token) {
  const [subsRes, tipoModRes, tipoAppRes] = await Promise.all([
    request.get(`${BACKEND_URI}/api/v1/sub-sistemas?campos=id,nombre`, { headers: authHeaders(token) }),
    request.get(`${BACKEND_URI}/api/v1/tipo-modulos?campos=id,nombre`, { headers: authHeaders(token) }),
    request.get(`${BACKEND_URI}/api/v1/tipo-aplicaciones?campos=id,nombre`, { headers: authHeaders(token) }),
  ]);

  expect(subsRes.ok(), `GET sub-sistemas falló con estado ${subsRes.status()}`).toBeTruthy();
  expect(tipoModRes.ok(), `GET tipo-modulos falló con estado ${tipoModRes.status()}`).toBeTruthy();
  expect(tipoAppRes.ok(), `GET tipo-aplicaciones falló con estado ${tipoAppRes.status()}`).toBeTruthy();

  const subs = unwrap(await subsRes.json());
  const tiposModulo = unwrap(await tipoModRes.json());
  const tiposAplicacion = unwrap(await tipoAppRes.json());

  expect(subs.length, 'No hay sub-sistemas disponibles').toBeGreaterThan(0);
  expect(tiposModulo.length, 'No hay tipo-modulos disponibles').toBeGreaterThan(0);
  expect(tiposAplicacion.length, 'No hay tipo-aplicaciones disponibles').toBeGreaterThan(0);

  return {
    subSistemaId: subs[0].id,
    tipoModuloId: tiposModulo[0].id,
    tipoAplicacionId: tiposAplicacion[0].id,
  };
}

export async function createModuloByApi(request, token, payload) {
  const response = await request.post(`${BACKEND_URI}/api/v1/modulos`, {
    headers: authHeaders(token),
    data: payload,
  });

  expect([200, 201]).toContain(response.status());

  let createdId = null;
  try {
    const body = await response.json();
    createdId = body?.id ?? null;
  } catch {
    createdId = null;
  }

  if (!createdId) {
    const location = response.headers()['location'];
    if (location) {
      const match = String(location).match(/\/(\d+)$/);
      if (match) createdId = Number(match[1]);
    }
  }

  return { response, createdId };
}

export async function loginAsAdmin(page, request) {
  requireEnv('E2E_ADMIN_EMAIL', ADMIN_EMAIL);
  requireEnv('E2E_ADMIN_PASSWORD', ADMIN_PASSWORD);
  await authenticateByApi(page, request, ADMIN_EMAIL, ADMIN_PASSWORD);
}

export async function openModuloScreen(page) {
  await page.goto('/');
  await expect(page.getByRole('heading', { name: 'Gestión de Módulos' })).toBeVisible({ timeout: 20000 });
}

export async function selectFirstGridRow(page) {
  const firstRow = page.locator('[role="row"][data-id]').first();
  await expect(firstRow).toBeVisible({ timeout: 20000 });
  await firstRow.click();
}

export async function fillModuloForm(page, {
  nombre,
  url,
  descripcion,
  icon,
  roles,
  nombreId,
  requerido = true,
} = {}) {
  if (nombre != null) await page.getByLabel('Nombre').fill(nombre);
  if (url != null) await page.getByLabel('URL').fill(url);
  if (descripcion != null) await page.getByLabel('Descripción').fill(descripcion);
  if (icon != null) await page.getByLabel('Icono').fill(icon);
  if (roles != null) await page.getByLabel('Roles (separados por coma)').fill(roles);
  if (nombreId != null) await page.getByLabel('Nombre ID').fill(nombreId);

  await page.getByLabel('SubSistema').click();
  await page.getByRole('option').first().click();

  await page.getByLabel('Tipo Módulo').click();
  await page.getByRole('option', { name: 'CRUD' }).first().click();

  await page.getByLabel('Tipo Aplicación').click();
  await page.getByRole('option', { name: 'Web' }).first().click();

  const requeridoCheck = page.getByLabel('Módulo requerido');
  const isChecked = await requeridoCheck.isChecked();

  if (requerido && !isChecked) await requeridoCheck.check();
  if (!requerido && isChecked) await requeridoCheck.uncheck();
}
