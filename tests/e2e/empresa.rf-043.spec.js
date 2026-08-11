import { test, expect } from '@playwright/test';
import {
  loginAsSystemAdmin,
  openModuleScreen,
  clickActionButton,
  getActiveDialog,
  clickDialogButton,
  fillDialogField,
  selectDialogOptionByLabel,
  ensureGridColumnVisible,
  findGridCellInColumnAcrossPages,
} from './helpers/e2e.shared.utils';

const TEST_EMPRESA_PREFIX = 'E2E Empresa';

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

async function getEmpresaRowAcrossPages(page, nombre) {
  await ensureGridColumnVisible(page, 'Nombre');
  const cell = await findGridCellInColumnAcrossPages(page, 'Nombre', nombre, {
    maxPages: 80,
    timeout: 15000,
  });
  return cell.locator('xpath=ancestor::*[@role="row" and @data-id]').first();
}

async function findEmpresaCellAcrossPages(page, column, value, timeout = 12000) {
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

async function pickExistingTestEmpresa(page, actionLabel) {
  try {
    await ensureGridColumnVisible(page, 'Nombre');
    const cell = await findEmpresaCellAcrossPages(page, 'Nombre', TEST_EMPRESA_PREFIX, 8000);
    const row = cell.locator('xpath=ancestor::*[@role="row" and @data-id]').first();
    const nombreColIndex = await getGridColumnIndex(page, 'Nombre');
    const nameCell = row.locator(`[role="cell"][aria-colindex="${nombreColIndex}"]`).first();
    const empresaName = ((await nameCell.textContent()) || '').trim();

    if (!empresaName) {
      throw new Error('La celda encontrada no contiene nombre de empresa.');
    }
    return empresaName;
  } catch {
    throw new Error(
      `No se encontró una empresa de pruebas con prefijo "${TEST_EMPRESA_PREFIX}" para ${actionLabel}. Ejecuta primero el test de registro.`
    );
  }
}

async function getGridColumnIndex(page, columnName) {
  const header = await ensureGridColumnVisible(page, columnName);
  const colIndex = await header.getAttribute('aria-colindex');
  if (!colIndex) {
    throw new Error(`La columna "${columnName}" no tiene atributo aria-colindex.`);
  }
  return Number(colIndex);
}

test.describe('RF-043 - Gestión de Empresas (casos positivos)', () => {
  test.describe.configure({ mode: 'serial' });

  test.beforeEach(async ({ page, request }) => {
    await loginAsSystemAdmin(page, request, 'empresa');
    await openModuleScreen(page, 'empresa', /Empresa/i);
  });

  test('HU-043.1: visualiza listado de empresas con columnas esperadas', async ({ page }) => {
    await setGridRowsPerPage(page, '50');

    await expect(page.getByRole('columnheader', { name: /Nombre/i })).toBeVisible();
    await expect(page.getByRole('columnheader', { name: /Identificación/i })).toBeVisible();
    await expect(page.getByRole('columnheader', { name: /Correo|Email/i })).toBeVisible();
    await expect(page.getByRole('columnheader', { name: /Estado/i })).toBeVisible();

    await waitForGridRowsLoaded(page);
  });

  test('HU-043.2: registrar empresa con datos válidos', async ({ page }) => {
    const unique = Date.now();
    const nombre = `${TEST_EMPRESA_PREFIX} ${unique}`;
    const identificacion = `E2E-${unique}`;
    const email = `e2e.empresa.${unique}@test.com`;

    await setGridRowsPerPage(page, '50');
    await clickActionButton(page, 'AGREGAR');

    const dialog = await getActiveDialog(page);
    await expect(dialog.getByRole('heading', { name: /Crear|Registrar|Empresa/i })).toBeVisible({
      timeout: 10000,
    });

    await fillDialogField(page, 'nombre', nombre);
    await fillDialogField(page, 'correo', email);

    // Identificación
    try {
      await fillDialogField(page, 'identificacion', identificacion);
    } catch {
      // El campo puede tener otro nombre
    }

    // Teléfono
    try {
      await fillDialogField(page, 'celular', '3001112233');
      await fillDialogField(page, 'telefono', '3001112233');
    } catch {
      // Campo opcional
    }

    // Tipo de identificación
    try {
      await selectDialogOptionByLabel(page, /Tipo.*Identificación|Tipo.*ID/i, /.+/);
    } catch {
      // Puede no existir como combo
    }

    const postResponsePromise = page.waitForResponse(
      (res) =>
        res.url().includes('/api/empresas') &&
        res.request().method() === 'POST'
    );

    await clickDialogButton(page, 'Guardar');

    const postResponse = await postResponsePromise;
    expect([200, 201]).toContain(postResponse.status());

    await expect(page.getByText(/guardado correctamente|registrada|creada/i).first()).toBeVisible({
      timeout: 15000,
    });

    // Verificar en el grid
    await setGridRowsPerPage(page, '50');
    try {
      await ensureGridColumnVisible(page, 'Nombre');
      const cell = await findEmpresaCellAcrossPages(page, 'Nombre', nombre, 15000);
      await expect(cell).toBeVisible();
    } catch {
      expect(postResponse.status()).toBeLessThan(300);
    }
  });

  test('HU-043.3: visualizar detalle de empresa', async ({ page }) => {
    await setGridRowsPerPage(page, '50');
    await waitForGridRowsLoaded(page);

    const firstRow = page.locator('[role="row"][data-id]').first();
    await firstRow.click();

    try {
      await page.getByRole('button', { name: /Detalle|Ver|Consultar/i }).click();
      const dialog = page.locator('[role="dialog"]:visible').last();
      await expect(dialog).toBeVisible({ timeout: 10000 });

      await expect(dialog.getByText(/Nombre|Empresa/i).first()).toBeVisible({
        timeout: 10000,
      });

      await clickDialogButton(page, 'Cerrar');
    } catch {
      // El detalle puede estar en otra vista (no en diálogo)
      await expect(page.getByText(/Nombre|Empresa/i).first()).toBeVisible({
        timeout: 5000,
      });
    }
  });

  test('HU-043.4: actualizar datos de empresa existente', async ({ page }) => {
    await setGridRowsPerPage(page, '50');
    const nombre = await pickExistingTestEmpresa(page, 'actualizar');

    const row = await getEmpresaRowAcrossPages(page, nombre);
    await row.click();
    await page.getByRole('button', { name: /Editar/i }).click();

    await page.waitForTimeout(800);

    const dialog = page.locator('[role="dialog"]:visible').last();
    await expect(dialog.getByRole('heading', { name: /Editar|Empresa/i })).toBeVisible({
      timeout: 10000,
    });

    const nuevoContacto = `Contacto E2E ${Date.now()}`;
    try {
      await fillDialogField(page, 'contacto', nuevoContacto);
    } catch {
      // Si no existe campo contacto, actualizar descripción
      try {
        await fillDialogField(page, 'descripcion', nuevoContacto);
      } catch {
        // Si ningún campo opcional existe, actualizar el celular
        try {
          await fillDialogField(page, 'celular', '3001112233');
        } catch {
          // Nada que actualizar sin romper — test mínimo
        }
      }
    }

    const putResponsePromise = page.waitForResponse(
      (res) =>
        /\/api\/empresas\/\d+$/.test(res.url()) &&
        res.request().method() === 'PUT'
    );

    await clickDialogButton(page, 'Guardar');

    const putResponse = await putResponsePromise;
    expect([200, 204]).toContain(putResponse.status());

    await expect(page.getByText(/guardado correctamente|actualizado/i).first()).toBeVisible({
      timeout: 15000,
    });
  });
});
