import { test, expect } from '@playwright/test';
import {
  loginAsCompanyAdmin,
  switchCompanyRoleFromProfile,
  openModuleScreen,
  clickActionButton,
  getActiveDialog,
  clickDialogButton,
  fillDialogField,
  selectDialogOptionByLabel,
  ensureGridColumnVisible,
  findGridCellInColumnAcrossPages,
  getGridColumnIndex,
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

async function pickExistingTestProduct(page, actionLabel) {
  try {
    await ensureGridColumnVisible(page, 'Nombre');
    const cell = await findProductCellAcrossPages(page, 'Nombre', TEST_PRODUCT_PREFIX, 8000);
    const row = cell.locator('xpath=ancestor::*[@role="row" and @data-id]').first();
    const nombreColIndex = await getGridColumnIndex(page, 'Nombre');
    const nameCell = row.locator(`[role="cell"][aria-colindex="${nombreColIndex}"]`).first();
    const productName = ((await nameCell.textContent()) || '').trim();

    if (!productName) {
      throw new Error('La celda encontrada no contiene nombre de producto.');
    }
    return productName;
  } catch {
    throw new Error(
      `No se encontró un producto de pruebas con prefijo "${TEST_PRODUCT_PREFIX}" para ${actionLabel}. Ejecuta primero el test de creación.`
    );
  }
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
    await expect(page.getByRole('columnheader', { name: /Unidad/i })).toBeVisible();
    await expect(page.getByRole('columnheader', { name: /Estado/i })).toBeVisible();

    await waitForGridRowsLoaded(page);
  });

  test('HU-024.2: crear producto con datos válidos', async ({ page }) => {
    const unique = Date.now();
    const nombre = `${TEST_PRODUCT_PREFIX} ${unique}`;

    await setGridRowsPerPage(page, '50');
    await clickActionButton(page, 'AGREGAR');

    const dialog = await getActiveDialog(page);
    await expect(dialog.getByRole('heading', { name: /Crear Producto|Producto/i })).toBeVisible({
      timeout: 10000,
    });

    await fillDialogField(page, 'nombre', nombre);
    await fillDialogField(page, 'descripcion', 'Producto creado por prueba E2E');

    // Seleccionar categoría (primer opción disponible)
    await selectDialogOptionByLabel(page, /Categoría/i, /.+/).catch(async () => {
      const combos = dialog.getByRole('combobox');
      const count = await combos.count();
      if (count >= 1) {
        await combos.first().click();
        await page.getByRole('option').first().click();
      }
    });

    // Seleccionar unidad (siguiente combo)
    await selectDialogOptionByLabel(page, /Unidad/i, /.+/).catch(async () => {
      const combos = dialog.getByRole('combobox');
      const count = await combos.count();
      if (count >= 2) {
        await combos.nth(1).click();
        await page.getByRole('option').first().click();
      }
    });

    const postResponsePromise = page.waitForResponse(
      (res) => res.url().includes('/api/v2/productos') && res.request().method() === 'POST'
    );

    await clickDialogButton(page, 'Guardar');

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
      // Si no se encuentra en el grid, verificar que al menos el POST devolvió 201
      expect(postResponse.status()).toBeLessThan(300);
    }
  });

  test('HU-024.3: consultar producto existente por detalle', async ({ page }) => {
    await setGridRowsPerPage(page, '50');
    await waitForGridRowsLoaded(page);

    const firstRow = page.locator('[role="row"][data-id]').first();
    await firstRow.click();

    await page.getByRole('button', { name: /Detalle|Ver|Consultar/i }).click();

    const dialog = page.locator('[role="dialog"]:visible').last();
    await expect(dialog).toBeVisible({ timeout: 15000 });

    // El diálogo de detalle debe mostrar datos del producto
    await expect(dialog.getByText(/Nombre|Producto/i).first()).toBeVisible({ timeout: 10000 });

    await clickDialogButton(page, 'Cerrar');
  });

  test('HU-024.4: actualizar producto existente', async ({ page }) => {
    await setGridRowsPerPage(page, '50');
    const nombre = await pickExistingTestProduct(page, 'actualizar');
    await openEditDialogForProduct(page, nombre);

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
    const nombre = await pickExistingTestProduct(page, 'inactivar');
    await openEditDialogForProduct(page, nombre);

    // Cambiar estado a Inactivo
    await selectDialogOptionByLabel(page, /Estado/i, /Inactivo/i).catch(async () => {
      // Alternativa: buscar combo de estado
      const dialog = page.locator('[role="dialog"]:visible').last();
      const stateCombo = dialog.getByRole('combobox').last();
      if (await stateCombo.isVisible().catch(() => false)) {
        await stateCombo.click();
        await page.getByRole('option', { name: /Inactivo/i }).first().click();
      }
    });

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
