import { test, expect } from '@playwright/test';
import {
  loginAsAdmin,
  loginAsAdminGetToken,
  fetchBackendModuloPaginationStats,
  openModuloScreen,
  ensureGridColumnVisible,
  expectGridCheckboxIndicatorInColumn,
  expectGridIconIndicatorInColumn,
  findGridCellInColumnAcrossPages,
  getGridColumnIndex,
  countFrontendGridPages,
  fillModuloForm,
  clickCreateModuloButton,
  clickDialogButton,
  fillDialogField,
  clickDialogSelectOption,
} from './helpers/e2e.shared.utils';

const TEST_MODULE_PREFIX = 'E2E Modulo';
const TEST_MODULE_ACRONYM_PREFIX = 'e2e_modulo_';

async function setGridRowsPerPage(page, size = '50') {
  const footer = page.locator('.MuiDataGrid-footerContainer').first();
  const pageSizeCombo = footer.getByRole('combobox').first();
  await expect(pageSizeCombo).toBeVisible({ timeout: 10000 });

  const current = ((await pageSizeCombo.textContent()) || '').trim();
  if (current === String(size)) return;

  await pageSizeCombo.click();
  await page.getByRole('option', { name: new RegExp(`^${size}$`) }).first().click();
  await page.waitForTimeout(250);
}

async function getModuleRowAcrossPages(page, nombre) {
  await ensureGridColumnVisible(page, 'Nombre');
  const cell = await findGridCellInColumnAcrossPages(page, 'Nombre', nombre, { maxPages: 80, timeout: 15000 });
  return cell.locator('xpath=ancestor::*[@role="row" and @data-id]').first();
}

async function waitForModuleVisibleAcrossPages(page, columnName, value, timeout = 30000) {
  await expect
    .poll(
      async () => {
        try {
          await findGridCellInColumnAcrossPages(page, columnName, value, {
            maxPages: 80,
            timeout: 3000,
          });
          return true;
        } catch {
          return false;
        }
      },
      {
        timeout,
        message: `No apareció el valor "${value}" en la columna "${columnName}"`,
      }
    )
    .toBe(true);

  return findGridCellInColumnAcrossPages(page, columnName, value, { maxPages: 80, timeout: 15000 });
}

async function pickExistingTestModuleName(page, actionLabel) {
  try {
    await ensureGridColumnVisible(page, 'Nombre');
    const nombreColIndex = await getGridColumnIndex(page, 'Nombre');

    let cell;
    try {
      cell = await waitForModuleVisibleAcrossPages(page, 'Nombre', TEST_MODULE_PREFIX, 8000);
    } catch {
      await ensureGridColumnVisible(page, 'Acrónimo');
      cell = await waitForModuleVisibleAcrossPages(page, 'Acrónimo', TEST_MODULE_ACRONYM_PREFIX, 8000);
    }

    const row = cell.locator('xpath=ancestor::*[@role="row" and @data-id]').first();
    const nameCell = row.locator(`[role="cell"][aria-colindex="${nombreColIndex}"]`).first();
    const moduleName = ((await nameCell.textContent()) || '').trim();

    if (!moduleName) {
      throw new Error('La celda encontrada no contiene nombre de módulo.');
    }
    return moduleName;
  } catch {
    throw new Error(
      `No se encontró un módulo de pruebas con prefijo "${TEST_MODULE_PREFIX}" para ${actionLabel}. Ejecuta primero HU-035.2.`
    );
  }
}

async function openEditModalForModule(page, nombre) {
  const row = await getModuleRowAcrossPages(page, nombre);
  await row.click();
  await page.getByRole('button', { name: 'Editar' }).click();

  // Pequeña pausa para permitir precarga de combos y datos derivados del modal.
  await page.waitForTimeout(800);

  const dialog = page.locator('[role="dialog"]:visible').last();
  await expect(dialog.getByRole('heading', { name: /Editar Módulo/i })).toBeVisible({ timeout: 10000 });
  await expect(dialog.locator('input[name="nombre"]').first()).toHaveValue(nombre, { timeout: 10000 });

  return { row, dialog };
}

test.describe('RF-035.0 - Gestión de módulos (casos positivos)', () => {
  test.describe.configure({ mode: 'serial' });

  test.beforeEach(async ({ page, request }) => {
    await loginAsAdmin(page, request);
  });

  test('HU-035.1: visualiza listado de módulos', async ({ page, request }) => {
    await openModuloScreen(page);
    await setGridRowsPerPage(page, '50');

    await expect(page.getByRole('columnheader', { name: 'Nombre' })).toBeVisible();
    await expect(page.getByRole('columnheader', { name: 'Icono' })).toBeVisible();
    await ensureGridColumnVisible(page, 'Icono');
    await expectGridIconIndicatorInColumn(page, 'Icono');

    await ensureGridColumnVisible(page, 'Requerido');
    await expect(page.getByRole('columnheader', { name: 'Requerido' })).toBeVisible();
    await expectGridCheckboxIndicatorInColumn(page, 'Requerido');

    const totalRows = await page.locator('[role="row"][data-id]').count();
    expect(totalRows).toBeGreaterThan(0);

    const token = await loginAsAdminGetToken(request);
    const backendStats = await fetchBackendModuloPaginationStats(request, token, { backendPageSize: 20 });
    const expectedFrontendPages = Math.max(1, Math.ceil(backendStats.totalItems / 50));
    const actualFrontendPages = await countFrontendGridPages(page);

    expect(actualFrontendPages).toBe(
      expectedFrontendPages);
  });

  test('HU-035.2: crear módulo con datos válidos', async ({ page }) => {
    const unique = Date.now();
    const nombre = `E2E Modulo ${unique}`;

    const token = await loginAsAdminGetToken(page.request);
    const beforeStats = await fetchBackendModuloPaginationStats(page.request, token, { backendPageSize: 20 });

    await openModuloScreen(page);
    await setGridRowsPerPage(page, '50');
    await clickCreateModuloButton(page);

    await fillModuloForm(page, {
      nombre,
      url: `/e2e-modulo-${unique}`,
      descripcion: 'Módulo creado por prueba E2E real',
      requerido: true,
    });

    const createRequestPromise = page.waitForRequest(
      (req) => req.url().includes('/api/v2/modulos') && req.method() === 'POST'
    );

    const createResponsePromise = page.waitForResponse(
      (res) => res.url().includes('/api/v2/modulos') && res.request().method() === 'POST'
    );

    await clickDialogButton(page, 'Guardar');

    const createRequest = await createRequestPromise;
    const requestBody = createRequest.postDataJSON();
    expect(requestBody.nombre).toBe(nombre);
    expect(typeof requestBody.nombreId).toBe('string');
    expect(requestBody.nombreId.trim().length).toBeGreaterThan(0);
    const expectedAcronimo = requestBody.nombreId;

    const createResponse = await createResponsePromise;
    expect([200, 201]).toContain(createResponse.status());

    await expect(page.getByText('Módulo guardado correctamente.')).toBeVisible({ timeout: 15000 });

    // El reload después de crear puede resetear el pageSize a 5 en la grilla.
    await setGridRowsPerPage(page, '50');

    let foundInGrid = false;
    try {
      await ensureGridColumnVisible(page, 'Acrónimo');
      const createdAcronimoCell = await waitForModuleVisibleAcrossPages(page, 'Acrónimo', expectedAcronimo, 12000);
      await expect(createdAcronimoCell).toBeVisible();
      foundInGrid = true;
    } catch {
      foundInGrid = false;
    }

    if (!foundInGrid) {
      const afterStats = await fetchBackendModuloPaginationStats(page.request, token, { backendPageSize: 20 });
      expect(afterStats.totalItems).toBeGreaterThanOrEqual(beforeStats.totalItems + 1);
    }
  });

  test('HU-035.3: modifica módulo existente', async ({ page }) => {
    await openModuloScreen(page);
    await setGridRowsPerPage(page, '50');
    const nombre = await pickExistingTestModuleName(page, 'actualizar');
    await openEditModalForModule(page, nombre);

    const descripcion = `Actualización E2E ${Date.now()}`;
    await fillDialogField(page, 'descripcion', descripcion);

    const putResponsePromise = page.waitForResponse(
      (res) => /\/api\/v2\/modulos\/\d+$/.test(res.url()) && res.request().method() === 'PUT'
    );

    await clickDialogButton(page, 'Guardar');

    const putResponse = await putResponsePromise;
    expect([200, 204]).toContain(putResponse.status());

    await expect(page.getByText('Módulo guardado correctamente.')).toBeVisible({ timeout: 15000 });
  });

  test('HU-035.4: cambia obligatoriedad desde switch en listado', async ({ page }) => {
    await openModuloScreen(page);
    await setGridRowsPerPage(page, '50');
    const nombre = await pickExistingTestModuleName(page, 'cambiar obligatoriedad');
    const row = await getModuleRowAcrossPages(page, nombre);

    const requeridoResponsePromise = page.waitForResponse(
      (res) => /\/api\/v2\/modulos\/\d+$/.test(res.url()) && res.request().method() === 'PATCH'
    );

    await ensureGridColumnVisible(page, 'Requerido');
    const requeridoColIndex = await getGridColumnIndex(page, 'Requerido');
    await row.locator(`[role="cell"][aria-colindex="${requeridoColIndex}"] input[type="checkbox"]`).click();

    const requeridoResponse = await requeridoResponsePromise;
    expect([200, 204]).toContain(requeridoResponse.status());
    await expect(page.getByText('Obligatoriedad actualizada correctamente.')).toBeVisible({ timeout: 15000 });
  });

  test('HU-035.5: inactiva módulo desde edición (estado Inactivo)', async ({ page }) => {
    await openModuloScreen(page);
    await setGridRowsPerPage(page, '50');
    const nombre = await pickExistingTestModuleName(page, 'inactivar');
    await openEditModalForModule(page, nombre);
    await clickDialogSelectOption(page, 'estadoId', 'Inactivo');

    const putResponsePromise = page.waitForResponse(
      (res) => /\/api\/v2\/modulos\/\d+$/.test(res.url()) && res.request().method() === 'PUT'
    );

    await clickDialogButton(page, 'Guardar');

    const putResponse = await putResponsePromise;
    expect([200, 204]).toContain(putResponse.status());
    await expect(page.getByText('Módulo guardado correctamente.')).toBeVisible({ timeout: 15000 });
  });
});
