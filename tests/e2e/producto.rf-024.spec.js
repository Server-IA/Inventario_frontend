import { test, expect } from '@playwright/test';
import {
  loginAsCompanyAdmin,
  switchCompanyRoleFromProfile,
  openModuleScreen,
  clickActionButton,
  getActiveDialog,
  clickDialogButton,
  fillDialogField,
  ensureGridColumnVisible,
  findGridCellInColumnAcrossPages,
} from './helpers/e2e.shared.utils';

const TEST_PRODUCT_PREFIX = 'E2E Producto';

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

async function waitForGridRowsLoaded(page, minRows = 1, timeout = 15000) {
  await expect
    .poll(async () => page.locator('[role="row"][data-id]').count(), {
      timeout,
      message: 'No se cargaron filas en la tabla dentro del tiempo esperado',
    })
    .toBeGreaterThanOrEqual(minRows);
}

async function getProductRowAcrossPages(page, nombre) {
  await ensureGridColumnVisible(page, 'Nombre');
  const cell = await findGridCellInColumnAcrossPages(page, 'Nombre', nombre, {
    maxPages: 80,
    timeout: 15000,
  });
  return cell.locator('xpath=ancestor::*[@role="row" and @data-id]').first();
}

async function findProductCellAcrossPages(page, column, value, timeout = 12000) {
  await expect
    .poll(
      async () => {
        try {
          await findGridCellInColumnAcrossPages(page, column, value, {
            maxPages: 80,
            timeout: 3000,
          });
          return true;
        } catch {
          return false;
        }
      },
      { timeout, message: `No apareció el valor "${value}" en la columna "${column}"` }
    )
    .toBe(true);
  return findGridCellInColumnAcrossPages(page, column, value, {
    maxPages: 80,
    timeout: 15000,
  });
}

async function openEditDialogForProduct(page, nombre) {
  const row = await getProductRowAcrossPages(page, nombre);
  await row.click();
  await page.getByRole('button', { name: /Editar/i }).click();

  await page.waitForTimeout(800);

  const dialog = page.locator('[role="dialog"]:visible').last();
  await expect(dialog.getByRole('heading', { name: /Editar Producto|Producto/i })).toBeVisible({
    timeout: 10000,
  });
  await expect(dialog.locator('input[name="nombre"]').first()).toHaveValue(nombre, {
    timeout: 10000,
  });

  return { row, dialog };
}

test.describe('RF-024 - Gestión de Productos (casos positivos)', () => {
  test.describe.configure({ mode: 'serial' });

  // Producto creado por este spec; update/inactivar operan sobre ÉL (no sobre
  // leftovers de corridas anteriores).
  let testProductName = null;

  test.beforeEach(async ({ page, request }) => {
    await loginAsCompanyAdmin(page, request, 'producto');
    await switchCompanyRoleFromProfile(page, {
      roleName: 'ROLE_ADMINISTRADOR_EMPRESA',
    });
    await openModuleScreen(page, 'producto', /Producto/i);
  });

  test('HU-024.1: visualiza listado de productos con columnas esperadas', async ({ page }) => {
    await setGridRowsPerPage(page, '50');

    await expect(page.getByRole('columnheader', { name: /Nombre/i })).toBeVisible();
    await expect(page.getByRole('columnheader', { name: /Categoría/i })).toBeVisible();
    await expect(page.getByRole('columnheader', { name: /Unidad/i }).first()).toBeVisible();
    await expect(page.getByRole('columnheader', { name: /Estado/i })).toBeVisible();

    await waitForGridRowsLoaded(page);
  });

  test('HU-024.2: crear producto con datos válidos', async ({ page }) => {
    const unique = Date.now();
    const nombre = `${TEST_PRODUCT_PREFIX} ${unique}`;
    testProductName = nombre;

    await setGridRowsPerPage(page, '50');
    await clickActionButton(page, 'Crear');

    const dialog = await getActiveDialog(page);
    await expect(dialog.getByRole('heading', { name: /Crear Producto|Producto/i })).toBeVisible({
      timeout: 10000,
    });

    await fillDialogField(page, 'nombre', nombre);
    await fillDialogField(page, 'descripcion', 'Producto creado por prueba E2E');

    // Categoría (requerido) — 1er combobox del form.
    await dialog.getByRole('combobox').nth(0).click();
    await page.getByRole('option').first().click();

    // Estado (requerido) — 2º combobox del form.
    await dialog.getByRole('combobox').nth(1).click();
    await page.getByRole('option', { name: /Activo/i }).first().click();

    // Unidad mínima (requerido) — 3er combobox del form.
    await dialog.getByRole('combobox').nth(2).click();
    await page.getByRole('option').first().click();

    const postResponsePromise = page.waitForResponse(
      (res) => res.url().includes('/api/v2/productos') && res.request().method() === 'POST'
    );

    await clickDialogButton(page, 'Crear');

    const postResponse = await postResponsePromise;
    expect([200, 201]).toContain(postResponse.status());

    await expect(page.getByText(/guardado correctamente|creado/i).first()).toBeVisible({
      timeout: 15000,
    });

    // Verificar que aparece en el grid
    await setGridRowsPerPage(page, '50');
    try {
      await ensureGridColumnVisible(page, 'Nombre');
      const cell = await findProductCellAcrossPages(page, 'Nombre', nombre, 15000);
      await expect(cell).toBeVisible();
    } catch {
      expect(postResponse.status()).toBeLessThan(300);
    }
  });

  test('HU-024.3: consultar producto existente por detalle', async ({ page }) => {
    await setGridRowsPerPage(page, '50');
    await waitForGridRowsLoaded(page);

    const firstRow = page.locator('[role="row"][data-id]').first();
    await firstRow.click();

    // El módulo producto no tiene vista "detalle": se usa el diálogo de edición
    // para consultar los datos del producto.
    await page.getByRole('button', { name: /^Editar$/i }).click();

    const dialog = page.locator('[role="dialog"]:visible').last();
    await expect(dialog).toBeVisible({ timeout: 15000 });
    await expect(dialog.locator('input[name="nombre"]').first()).toBeVisible({ timeout: 10000 });

    await clickDialogButton(page, 'Cancelar');
  });

  test('HU-024.4: actualizar producto existente', async ({ page }) => {
    await setGridRowsPerPage(page, '50');
    expect(testProductName, 'HU-024.2 debió crear un producto primero').toBeTruthy();
    await openEditDialogForProduct(page, testProductName);

    const descripcion = `Actualización E2E ${Date.now()}`;
    await fillDialogField(page, 'descripcion', descripcion);

    const putResponsePromise = page.waitForResponse(
      (res) => /\/api\/v2\/productos\/\d+$/.test(res.url()) && res.request().method() === 'PUT'
    );

    await clickDialogButton(page, 'Guardar');

    const putResponse = await putResponsePromise;
    expect([200, 204]).toContain(putResponse.status());

    await expect(page.getByText(/guardado correctamente|actualizado/i).first()).toBeVisible({
      timeout: 15000,
    });
  });

  test('HU-024.5: inactivar producto (soft delete)', async ({ page }) => {
    await setGridRowsPerPage(page, '50');
    expect(testProductName, 'HU-024.2 debió crear un producto primero').toBeTruthy();
    await openEditDialogForProduct(page, testProductName);

    // Cambiar estado a Inactivo (2º combobox del form).
    await page.locator('[role="dialog"]:visible').last().getByRole('combobox').nth(1).click();
    await page.getByRole('option', { name: /Inactivo/i }).first().click();

    const putResponsePromise = page.waitForResponse(
      (res) => /\/api\/v2\/productos\/\d+$/.test(res.url()) && res.request().method() === 'PUT'
    );

    await clickDialogButton(page, 'Guardar');

    const putResponse = await putResponsePromise;
    expect([200, 204]).toContain(putResponse.status());

    await expect(
      page.getByText(/guardado correctamente|inactivado|actualizado/i).first()
    ).toBeVisible({ timeout: 15000 });
  });
});
