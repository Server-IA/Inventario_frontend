import { test, expect } from '@playwright/test';
import {
  loginAsSystemAdmin,
  openModuleScreen,
  clickActionButton,
  fillDialogFieldByName,
  selectDialogOptionByLabel,
  clickDialogButton,
  expectSnackMessage,
  findGridCellInColumnAcrossPages,
} from './helpers/e2e.shared.utils';

// Roles de sistema que NUNCA deben editarse ni eliminarse por un test.
const PROTECTED_ROLE_IDS = [1, 2];

// Prefijo reconocible de dato de prueba. El nombre se completa con letras
// aleatorias para garantizar unicidad (el backend solo acepta mayúsculas y un
// único '_', por eso NO se usa timestamp con dígitos: `ROLE_E2E_123` es inválido).
const TEST_ROLE_PREFIX = 'ROLE_QA';

function buildValidRoleName() {
  const rand = Array.from(
    { length: 8 },
    () => String.fromCharCode(65 + Math.floor(Math.random() * 26))
  ).join('');
  return `${TEST_ROLE_PREFIX}${rand}`;
}

async function waitForGridRowsLoaded(page, minRows = 1, timeout = 12000) {
  await expect
    .poll(() => page.locator('[role="row"][data-id]').count(), {
      timeout,
      message: 'No se cargaron filas en la tabla dentro del tiempo esperado',
    })
    .toBeGreaterThanOrEqual(minRows);
}

// Busca la fila cuyo valor en `column` coincide con `name` (recorriendo la
// paginación si hace falta) y devuelve el locator de la FILA, no de la celda.
async function getRowByExactText(page, column, name) {
  const cell = await findGridCellInColumnAcrossPages(page, column, name, {
    timeout: 15000,
    maxPages: 80,
  });
  return cell.locator('xpath=ancestor::*[@role="row" and @data-id]').first();
}

async function readConfirmDialogText(page) {
  const dialog = page.locator('[role="dialog"]:visible').last();
  await expect(dialog).toBeVisible({ timeout: 10000 });
  return ((await dialog.textContent()) || '').trim();
}

// Selecciona la fila por nombre y abre el diálogo de confirmación de borrado.
// Si el click cayó en otra fila (re-render del grid) el diálogo NO mostrará el
// rol esperado: se cierra y se reintenta. Nunca se confirma el borrado de un
// rol distinto al que creó el test.
async function selectRowAndOpenDeleteConfirm(page, roleName, maxAttempts = 3) {
  for (let attempt = 1; attempt <= maxAttempts; attempt += 1) {
    const row = await getRowByExactText(page, 'Nombre', roleName);
    const rowId = await row.getAttribute('data-id');
    await row.click();
    await page.waitForTimeout(300);

    // La fila debe quedar seleccionada (botón ELIMINAR habilitado).
    const deleteButton = page.getByRole('button', { name: /^ELIMINAR$/i }).first();
    if (!(await deleteButton.isEnabled().catch(() => false))) {
      continue;
    }
    await deleteButton.click();

    const dialogText = await readConfirmDialogText(page);
    if (dialogText.includes(roleName)) {
      return rowId;
    }

    // Selección incorrecta: cerrar y reintentar.
    await page.getByRole('button', { name: /^Cancelar$/i }).first().click();
    await page
      .locator('[role="dialog"]:visible')
      .last()
      .waitFor({ state: 'detached' })
      .catch(() => {});
  }

  throw new Error(
    `No se pudo seleccionar el rol "${roleName}" para eliminar tras ${maxAttempts} intentos.`
  );
}

// Selecciona la fila por nombre y abre el diálogo de actualización, verificando
// que el formulario muestre el rol esperado antes de editar (misma filosofía de
// reintento: si se seleccionó otro rol, se cierra y se vuelve a intentar).
async function selectRowAndOpenUpdateDialog(page, roleName, maxAttempts = 3) {
  for (let attempt = 1; attempt <= maxAttempts; attempt += 1) {
    const row = await getRowByExactText(page, 'Nombre', roleName);
    await row.click();
    await page.waitForTimeout(300);

    const updateButton = page.getByRole('button', { name: /^ACTUALIZAR$/i }).first();
    if (!(await updateButton.isEnabled().catch(() => false))) {
      continue;
    }
    await updateButton.click();

    const dialog = page.locator('[role="dialog"]:visible').last();
    await expect(dialog).toBeVisible({ timeout: 10000 });
    const nameValue = (await dialog.locator('input[name="nombre"]').first().inputValue().catch(() => '')).trim();

    if (nameValue === roleName) {
      return dialog;
    }

    await dialog.getByRole('button', { name: /^Cancelar$/i }).first().click();
    await dialog.waitFor({ state: 'detached' }).catch(() => {});
  }

  throw new Error(
    `No se pudo seleccionar el rol "${roleName}" para actualizar tras ${maxAttempts} intentos.`
  );
}

test.describe('RF-036 - Rol (admin sistema) casos positivos', () => {
  // Serial: un solo dato recorre crear -> actualizar -> eliminar. Así el paso
  // de eliminar SIEMPRE tiene un dato recién creado y no toca roles ajenos.
  test.describe.configure({ mode: 'serial' });

  let testRoleName = null;

  test.beforeEach(async ({ page, request }) => {
    await loginAsSystemAdmin(page, request, 'rol');
    await openModuleScreen(page, 'rol', /Gestión de Rol/i);
  });

  test('RS-01: visualizar listado de roles con nombre, descripción y estado', async ({ page }) => {
    await expect(page.getByRole('columnheader', { name: 'Nombre' })).toBeVisible();
    await expect(page.getByRole('columnheader', { name: 'Descripción' })).toBeVisible();
    await expect(page.getByRole('columnheader', { name: /^Estado$/i })).toBeVisible();
    await waitForGridRowsLoaded(page, 1, 30000);
  });

  test('RS-02: crear rol con nombre, descripción y estado activo', async ({ page }) => {
    testRoleName = buildValidRoleName();
    const uniqueDesc = `Rol de prueba E2E ${Date.now()}`;

    await clickActionButton(page, 'AGREGAR');
    await fillDialogFieldByName(page, 'nombre', testRoleName);
    await fillDialogFieldByName(page, 'descripcion', uniqueDesc);
    await selectDialogOptionByLabel(page, 'Estado', 'Activo');

    const postResponsePromise = page.waitForResponse(
      (res) => res.url().includes('/v1/roles') && res.request().method() === 'POST'
    );

    await clickDialogButton(page, 'Agregar');
    const postResponse = await postResponsePromise;
    expect([200, 201]).toContain(postResponse.status());
    await expectSnackMessage(page, /Rol creado/i);

    // Verificar que aparece en el grid (paginando si hace falta).
    const cell = await findGridCellInColumnAcrossPages(page, 'Nombre', testRoleName, {
      timeout: 15000,
      maxPages: 80,
    });
    await expect(cell).toBeVisible();
  });

  test('RS-03: actualizar descripción del rol creado', async ({ page }) => {
    test.slow();
    expect(testRoleName, 'RS-02 debió crear un rol primero').toBeTruthy();
    await waitForGridRowsLoaded(page, 1, 30000);

    const row = await getRowByExactText(page, 'Nombre', testRoleName);
    const rowId = await row.getAttribute('data-id');
    expect(PROTECTED_ROLE_IDS).not.toContain(Number(rowId));

    await selectRowAndOpenUpdateDialog(page, testRoleName);

    const updatedDesc = `Actualizado E2E ${Date.now()}`;
    await fillDialogFieldByName(page, 'descripcion', updatedDesc);

    const putResponsePromise = page.waitForResponse(
      (res) => res.url().includes(`/v1/roles/${rowId}`) && res.request().method() === 'PUT'
    );

    await clickDialogButton(page, 'Actualizar');
    const putResponse = await putResponsePromise;
    expect([200, 204]).toContain(putResponse.status());
    await expectSnackMessage(page, /Rol actualizado/i);
  });

  test('RS-04: eliminar el rol creado (soft delete)', async ({ page }) => {
    test.slow();
    expect(testRoleName, 'RS-02 debió crear un rol primero').toBeTruthy();
    await waitForGridRowsLoaded(page, 1, 30000);

    const rowId = await selectRowAndOpenDeleteConfirm(page, testRoleName);
    expect(PROTECTED_ROLE_IDS).not.toContain(Number(rowId));

    const deleteResponsePromise = page.waitForResponse(
      (res) => res.url().includes(`/v1/roles/${rowId}`) && res.request().method() === 'DELETE'
    );

    const dialog = page.locator('[role="dialog"]:visible').last();
    await dialog.getByRole('button', { name: /^Eliminar$/i }).click();
    const deleteResponse = await deleteResponsePromise;
    expect([200, 202, 204]).toContain(deleteResponse.status());
    await expectSnackMessage(page, /Rol eliminado|Registro eliminado|eliminado/i);

    // Verificación UI: el rol ya no debe aparecer en el grid.
    await expect
      .poll(
        async () => {
          try {
            await findGridCellInColumnAcrossPages(page, 'Nombre', testRoleName, {
              timeout: 3000,
              maxPages: 80,
            });
            return false;
          } catch {
            return true;
          }
        },
        { timeout: 15000, message: `El rol "${testRoleName}" sigue visible tras eliminar` }
      )
      .toBe(true);
  });
});
