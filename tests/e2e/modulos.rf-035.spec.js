import { test, expect } from '@playwright/test';
import {
  loginAsAdmin,
  loginAsAdminGetToken,
  fetchBackendModuloPaginationStats,
  openModuloScreen,
  ensureGridColumnVisible,
  expectGridCheckboxIndicatorInColumn,
  expectGridIconIndicatorInColumn,
  toggleFirstGridSwitchInColumn,
  findGridCellInColumnAcrossPages,
  getGridColumnIndex,
  countFrontendGridPages,
  selectFirstGridRow,
  fillModuloForm,
  clickCreateModuloButton,
  clickDialogButton,
  fillDialogField,
  clickDialogSelectOption,
} from './helpers/e2e.shared.utils';

test.describe('RF-035.0 - Gestión de módulos (casos positivos)', () => {
  test.beforeEach(async ({ page, request }) => {
    await loginAsAdmin(page, request);
  });

  test('HU-035.1: visualiza listado de módulos', async ({ page, request }) => {
    await openModuloScreen(page);

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
    const expectedFrontendPages = Math.max(1, Math.ceil(backendStats.totalItems / 5));
    const actualFrontendPages = await countFrontendGridPages(page);

    expect(actualFrontendPages).toBe(
      expectedFrontendPages);
  });

  test('HU-035.2: crear módulo con datos válidos', async ({ page }) => {
    const unique = Date.now();
    const nombre = `E2E Modulo ${unique}`;

    await openModuloScreen(page);
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
    expect(typeof requestBody.nombreId).toBe('string');
    expect(requestBody.nombreId.trim().length).toBeGreaterThan(0);

    const createResponse = await createResponsePromise;
    expect([200, 201]).toContain(createResponse.status());

    await expect(page.getByText('Módulo guardado correctamente.')).toBeVisible({ timeout: 15000 });

    await ensureGridColumnVisible(page, 'Nombre');
    const createdNameCell = await findGridCellInColumnAcrossPages(page, 'Nombre', nombre);

    await ensureGridColumnVisible(page, 'Acrónimo');
    const acronimoColIndex = await getGridColumnIndex(page, 'Acrónimo');
    const createdRow = createdNameCell.locator('xpath=ancestor::*[@role="row" and @data-id]').first();
    const acronimoCell = createdRow.locator(`[role="cell"][aria-colindex="${acronimoColIndex}"]`);

    await expect(acronimoCell).toBeVisible();
    await expect(acronimoCell).not.toHaveText(/^\s*$/);
  });

  test('HU-035.3: modifica módulo existente', async ({ page }) => {
    await openModuloScreen(page);

    await selectFirstGridRow(page);
    await page.getByRole('button', { name: 'Editar' }).click();

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

    const requeridoResponsePromise = page.waitForResponse(
      (res) => /\/api\/v2\/modulos\/\d+\/requerido$/.test(res.url()) && res.request().method() === 'PUT'
    );

    await ensureGridColumnVisible(page, 'Requerido');
    await toggleFirstGridSwitchInColumn(page, 'Requerido');

    const requeridoResponse = await requeridoResponsePromise;
    expect([200, 204]).toContain(requeridoResponse.status());
    await expect(page.getByText('Obligatoriedad actualizada correctamente.')).toBeVisible({ timeout: 15000 });
  });

  test('HU-035.5: inactiva módulo desde edición (estado Inactivo)', async ({ page }) => {
    await openModuloScreen(page);

    await selectFirstGridRow(page);
    await page.getByRole('button', { name: 'Editar' }).click();
    //esperar a que los datos se carguen en el formulario
    await page.waitForTimeout(1000);
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
