import { expect } from '@playwright/test';

export const ADMIN_EMAIL = process.env.E2E_ADMIN_EMAIL;
export const ADMIN_PASSWORD = process.env.E2E_ADMIN_PASSWORD;
export const NOADMIN_EMAIL = process.env.E2E_NOADMIN_EMAIL;
export const NOADMIN_PASSWORD = process.env.E2E_NOADMIN_PASSWORD;
export const BACKEND_URI = process.env.VITE_BACKEND_URI;

function escapeRegex(value) {
  return String(value).replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
}

function exactTextRegex(value) {
  return new RegExp(`^${escapeRegex(value)}$`);
}

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
  const response = await request.get(`${BACKEND_URI}/api/v2/modulos`, {
    headers: authHeaders(token),
  });

  expect(response.ok(), `GET /api/v2/modulos falló con estado ${response.status()}`).toBeTruthy();
  const data = await response.json();
  return unwrap(data);
}

async function fetchModuloPage(request, token, page, size) {
  const response = await request.get(`${BACKEND_URI}/api/v2/modulos?page=${page}&size=${size}`, {
    headers: authHeaders(token),
  });

  expect(response.ok(), `GET /api/v2/modulos?page=${page}&size=${size} falló con estado ${response.status()}`).toBeTruthy();
  return response.json();
}

export async function fetchBackendModuloPaginationStats(request, token, options = {}) {
  const { backendPageSize = 20, maxPages = 200 } = options;

  const firstPage = await fetchModuloPage(request, token, 0, backendPageSize);

  if (Array.isArray(firstPage)) {
    return {
      totalItems: firstPage.length,
      backendTotalPages: firstPage.length > 0 ? 1 : 0,
      backendPageSize: firstPage.length || backendPageSize,
    };
  }

  const size = Number(firstPage?.size ?? backendPageSize);
  const totalElements = Number(firstPage?.totalElements);
  const totalPages = Number(firstPage?.totalPages);

  if (Number.isFinite(totalElements)) {
    return {
      totalItems: totalElements,
      backendTotalPages: Number.isFinite(totalPages) ? totalPages : Math.ceil(totalElements / size),
      backendPageSize: size,
    };
  }

  if (Number.isFinite(totalPages)) {
    let totalItems = 0;
    for (let pageIndex = 0; pageIndex < Math.min(totalPages, maxPages); pageIndex += 1) {
      const pageData = pageIndex === 0 ? firstPage : await fetchModuloPage(request, token, pageIndex, size);
      totalItems += unwrap(pageData).length;
    }

    return {
      totalItems,
      backendTotalPages: totalPages,
      backendPageSize: size,
    };
  }

  let totalItems = 0;
  for (let pageIndex = 0; pageIndex < maxPages; pageIndex += 1) {
    const pageData = pageIndex === 0 ? firstPage : await fetchModuloPage(request, token, pageIndex, size);
    const content = unwrap(pageData);
    totalItems += content.length;

    if (content.length < size) {
      return {
        totalItems,
        backendTotalPages: pageIndex + 1,
        backendPageSize: size,
      };
    }
  }

  return {
    totalItems,
    backendTotalPages: maxPages,
    backendPageSize: size,
  };
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
  const response = await request.post(`${BACKEND_URI}/api/v2/modulos`, {
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

export async function getActiveDialog(page) {
  const dialog = page.locator('[role="dialog"]:visible').last();
  await expect(dialog).toBeVisible({ timeout: 15000 });
  return dialog;
}

export async function clickCreateModuloButton(page) {
  await page.getByRole('button', { name: /^(Agregar|Crear)$/ }).click();
}

export async function clickDialogButton(page, buttonText) {
  const dialog = await getActiveDialog(page);
  await dialog.getByRole('button', { name: exactTextRegex(buttonText) }).click();
}

export async function fillDialogField(page, fieldName, value) {
  const dialog = await getActiveDialog(page);
  const field = dialog
    .locator(`input[name="${fieldName}"], textarea[name="${fieldName}"]`)
    .first();
  await expect(field).toBeVisible({ timeout: 10000 });
  await field.fill(value);
}

async function openDialogSelectByName(page, fieldName) {
  const dialog = await getActiveDialog(page);
  const nativeInput = dialog.locator(`input[name="${fieldName}"]`).first();
  await expect(nativeInput).toBeAttached({ timeout: 10000 });

  const selectTrigger = nativeInput
    .locator('xpath=ancestor::div[contains(@class,"MuiInputBase-root")]//*[@role="combobox"]')
    .first();

  if (await selectTrigger.isVisible().catch(() => false)) {
    await selectTrigger.click();
    return;
  }

  const fallbackContainer = nativeInput
    .locator('xpath=ancestor::div[contains(@class,"MuiInputBase-root")]')
    .first();
  await fallbackContainer.click();
}

export async function clickDialogSelectOption(page, fieldName, optionText) {
  await openDialogSelectByName(page, fieldName);
  await page.getByRole('option', { name: exactTextRegex(optionText) }).first().click();
}

export async function clickDialogSelectFirstOption(page, fieldName) {
  await openDialogSelectByName(page, fieldName);
  await page.getByRole('option').first().click();
}

export async function toggleDialogCheckbox(page, fieldName, checked) {
  const dialog = await getActiveDialog(page);
  const checkbox = dialog.locator(`input[type="checkbox"][name="${fieldName}"]`).first();
  await expect(checkbox).toBeVisible({ timeout: 10000 });
  const isChecked = await checkbox.isChecked();

  if (checked && !isChecked) await checkbox.check();
  if (!checked && isChecked) await checkbox.uncheck();
}

export async function ensureGridColumnVisible(page, columnName, options = {}) {
  const { maxScrolls = 25, step = 300 } = options;

  const header = page.getByRole('columnheader', { name: columnName }).first();
  if (await header.isVisible().catch(() => false)) {
    return header;
  }

  const scroller = page.locator('.MuiDataGrid-virtualScroller').first();
  await expect(scroller).toBeVisible({ timeout: 15000 });

  for (let index = 0; index < maxScrolls; index += 1) {
    await scroller.evaluate((element, scrollStep) => {
      const next = Math.min(element.scrollLeft + scrollStep, element.scrollWidth);
      element.scrollLeft = next;
    }, step);

    await page.waitForTimeout(80);

    if (await header.isVisible().catch(() => false)) {
      return header;
    }
  }

  throw new Error(`No se encontró la columna "${columnName}" después de scroll horizontal en DataGrid.`);
}

export async function getGridColumnIndex(page, columnName) {
  const header = await ensureGridColumnVisible(page, columnName);
  const colIndex = await header.getAttribute('aria-colindex');

  if (!colIndex) {
    throw new Error(`La columna "${columnName}" no tiene atributo aria-colindex en DataGrid.`);
  }

  return Number(colIndex);
}

export async function findGridCellInColumn(page, columnName, cellText, options = {}) {
  const { timeout = 10000 } = options;
  const colIndex = await getGridColumnIndex(page, columnName);

  const cell = page
    .locator(`[role="row"][data-id] [role="cell"][aria-colindex="${colIndex}"]`)
    .filter({ hasText: cellText })
    .first();

  await expect(cell).toBeVisible({ timeout });
  return cell;
}

export async function expectGridColumnValueVisible(page, columnName, cellText, options = {}) {
  const cell = await findGridCellInColumn(page, columnName, cellText, options);
  await expect(cell).toBeVisible();
  return cell;
}

export async function expectGridCheckboxIndicatorInColumn(page, columnName, options = {}) {
  const { timeout = 10000 } = options;
  const colIndex = await getGridColumnIndex(page, columnName);

  const checkbox = page
    .locator(`[role="row"][data-id] [role="cell"][aria-colindex="${colIndex}"] input[type="checkbox"]`)
    .first();

  await expect(checkbox).toBeVisible({ timeout });
  return checkbox;
}

export async function expectGridIconIndicatorInColumn(page, columnName, options = {}) {
  const { timeout = 10000 } = options;
  const colIndex = await getGridColumnIndex(page, columnName);

  const iconSvg = page
    .locator(`[role="row"][data-id] [role="cell"][aria-colindex="${colIndex}"] svg`)
    .first();

  await expect(iconSvg).toBeVisible({ timeout });
  return iconSvg;
}

export async function toggleFirstGridSwitchInColumn(page, columnName, options = {}) {
  const { timeout = 10000 } = options;
  const colIndex = await getGridColumnIndex(page, columnName);

  const switchInput = page
    .locator(`[role="row"][data-id] [role="cell"][aria-colindex="${colIndex}"] input[type="checkbox"]`)
    .first();

  await expect(switchInput).toBeVisible({ timeout });
  await switchInput.click();
  return switchInput;
}

export async function selectFirstGridRow(page) {
  const firstRow = page.locator('[role="row"][data-id]').first();
  await expect(firstRow).toBeVisible({ timeout: 20000 });
  await firstRow.click();
}

function gridFooter(page) {
  return page.locator('.MuiDataGrid-footerContainer').first();
}

function gridPrevPageButton(page) {
  return gridFooter(page)
    .getByRole('button', {
      name: /previous|go to previous page|anterior|página anterior/i,
    })
    .first();
}

function gridNextPageButton(page) {
  return gridFooter(page)
    .getByRole('button', {
      name: /next|go to next page|siguiente|página siguiente/i,
    })
    .first();
}

export async function goToGridFirstPage(page, options = {}) {
  const { maxHops = 25 } = options;
  const prevButton = gridPrevPageButton(page);

  for (let index = 0; index < maxHops; index += 1) {
    const visible = await prevButton.isVisible().catch(() => false);
    if (!visible) return;

    const disabled = await prevButton.isDisabled().catch(() => true);
    if (disabled) return;

    await prevButton.click();
    await page.waitForTimeout(150);
  }
}

export async function goToNextGridPage(page) {
  const nextButton = gridNextPageButton(page);
  const visible = await nextButton.isVisible().catch(() => false);
  if (!visible) return false;

  const disabled = await nextButton.isDisabled().catch(() => true);
  if (disabled) return false;

  await nextButton.click();
  await page.waitForTimeout(150);
  return true;
}

export async function countFrontendGridPages(page, options = {}) {
  const { maxPages = 200 } = options;

  await goToGridFirstPage(page);

  let pages = 1;
  for (let index = 0; index < maxPages - 1; index += 1) {
    const moved = await goToNextGridPage(page);
    if (!moved) break;
    pages += 1;
  }

  await goToGridFirstPage(page);
  return pages;
}

export async function findGridCellInColumnAcrossPages(page, columnName, cellText, options = {}) {
  const { maxPages = 30, timeout = 10000 } = options;

  await goToGridFirstPage(page);
  const colIndex = await getGridColumnIndex(page, columnName);

  for (let pageIndex = 0; pageIndex < maxPages; pageIndex += 1) {
    const cell = page
      .locator(`[role="row"][data-id] [role="cell"][aria-colindex="${colIndex}"]`)
      .filter({ hasText: cellText })
      .first();

    const found = (await cell.count()) > 0;
    if (found) {
      await expect(cell).toBeVisible({ timeout });
      return cell;
    }

    const moved = await goToNextGridPage(page);
    if (!moved) break;
  }

  throw new Error(`No se encontró el valor buscado en columna "${columnName}" después de recorrer la paginación.`);
}

export async function fillModuloForm(page, {
  nombre,
  url,
  descripcion,
  requerido = true,
} = {}) {
  await clickDialogSelectFirstOption(page, 'subSistemaId');
  await clickDialogSelectOption(page, 'tipoModuloId', 'CRUD');
  await clickDialogSelectOption(page, 'tipoAplicacionId', 'Web');

  if (nombre != null) await fillDialogField(page, 'nombre', nombre);
  if (url != null) await fillDialogField(page, 'url', url);
  if (descripcion != null) await fillDialogField(page, 'descripcion', descripcion);

  await toggleDialogCheckbox(page, 'requerido', requerido);
}
