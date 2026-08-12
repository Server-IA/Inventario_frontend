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

const PROTECTED_ROLE_REGEX = /(ROLE_ADMINISTRADOR_EMPRESA|ROLE_ADMINISTRADOR_SISTEMA|ADMIN\s*EMPRESA|ADMIN\s*SISTEMA)/i;

function randomUpperLetters(length = 6) {
  return Array.from({ length }, () => String.fromCharCode(65 + Math.floor(Math.random() * 26))).join('');
}

function buildValidRoleName(prefix = 'ROLE') {
  // Formato válido en FormRol: solo mayúsculas y máximo un '_' en medio.
  return `${prefix}_${randomUpperLetters(7)}`;
}

async function waitForGridRowsLoaded(page, minRows = 1, timeout = 12000) {
  await expect
    .poll(async () => page.locator('[role="row"][data-id]').count(), {
      timeout,
      message: 'No se cargaron filas en la tabla dentro del tiempo esperado',
    })
    .toBeGreaterThanOrEqual(minRows);
}

async function getGridRowsSnapshot(page) {
  const rows = page.locator('[role="row"][data-id]');
  const count = await rows.count();
  const snapshot = [];

  for (let i = 0; i < count; i += 1) {
    const row = rows.nth(i);
    const id = ((await row.locator('[role="cell"]').nth(0).textContent()) || '').trim();
    const role = ((await row.locator('[role="cell"]').nth(1).textContent()) || '').trim();
    snapshot.push({ index: i, id, role, row });
  }

  return snapshot;
}

function parseRowId(value) {
  const parsed = Number.parseInt(String(value).trim(), 10);
  return Number.isFinite(parsed) ? parsed : Number.NaN;
}

async function pickSafeRowByHighestId(page, actionLabel = 'modificar') {
  const rows = await getGridRowsSnapshot(page);
  if (rows.length === 0) {
    throw new Error(`No hay filas en la tabla para ${actionLabel}.`);
  }

  const nonProtected = rows
    .filter((row) => !PROTECTED_ROLE_REGEX.test(row.role))
    .map((row) => ({ ...row, numericId: parseRowId(row.id) }))
    .sort((a, b) => {
      const aValid = Number.isFinite(a.numericId);
      const bValid = Number.isFinite(b.numericId);
      if (aValid && bValid) return b.numericId - a.numericId;
      if (aValid) return -1;
      if (bValid) return 1;
      return 0;
    });

  if (nonProtected.length === 0) {
    throw new Error(
      `Todas las filas visibles son roles protegidos (admin empresa/sistema). No se puede ${actionLabel}.`
    );
  }

  return nonProtected[0];
}

test.describe('RF-036 - Rol (admin sistema) casos positivos', () => {
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
    const unique = Date.now();
    const roleName = buildValidRoleName('ROLE');

    await clickActionButton(page, 'AGREGAR');
    await fillDialogFieldByName(page, 'nombre', roleName);
    await fillDialogFieldByName(page, 'descripcion', `Rol de prueba ${unique}`);
    await selectDialogOptionByLabel(page, 'Estado', 'Activo');

    const postResponsePromise = page.waitForResponse(
      (res) => res.url().includes('/v1/roles') && res.request().method() === 'POST'
    );

    await clickDialogButton(page, 'Agregar');
    const postResponse = await postResponsePromise;

    expect([200, 201]).toContain(postResponse.status());
    await expectSnackMessage(page, /Rol creado/i);

    await findGridCellInColumnAcrossPages(page, 'Nombre', roleName, { timeout: 15000, maxPages: 80 });
  });

  test('RS-03: actualizar descripción de rol existente', async ({ page }) => {
    test.slow();
    await waitForGridRowsLoaded(page, 1, 30000);

    const rowToUpdate = await pickSafeRowByHighestId(page, 'actualizar');
    await rowToUpdate.row.click();
    await clickActionButton(page, 'ACTUALIZAR');

    const updatedDescription = `Actualizado E2E ${Date.now()}`;
    await fillDialogFieldByName(page, 'descripcion', updatedDescription);

    const putResponsePromise = page.waitForResponse(
      (res) => /\/v1\/roles\/\d+$/.test(res.url()) && res.request().method() === 'PUT'
    );

    await clickDialogButton(page, 'Actualizar');
    const putResponse = await putResponsePromise;

    expect([200, 204]).toContain(putResponse.status());
    await expectSnackMessage(page, /Rol actualizado/i);
  });

  test('RS-04: eliminar rol no asociado (soft delete)', async ({ page }) => {
    test.slow();
    const unique = Date.now();
    const roleName = buildValidRoleName('ROLEDEL');

    // Crear un rol temporal para garantizar que no esté asociado.
    await clickActionButton(page, 'AGREGAR');
    await fillDialogFieldByName(page, 'nombre', roleName);
    await fillDialogFieldByName(page, 'descripcion', `Rol temporal para eliminar ${unique}`);
    await selectDialogOptionByLabel(page, 'Estado', 'Activo');

    const postResponsePromise = page.waitForResponse(
      (res) => res.url().includes('/v1/roles') && res.request().method() === 'POST'
    );

    await clickDialogButton(page, 'Agregar');
    const postResponse = await postResponsePromise;
    expect([200, 201]).toContain(postResponse.status());
    await expectSnackMessage(page, /Rol creado/i);

    const targetCell = await findGridCellInColumnAcrossPages(page, 'Nombre', roleName, {
      timeout: 15000,
      maxPages: 80,
    });
    await targetCell.click();

    const deleteResponsePromise = page.waitForResponse(
      (res) => /\/v1\/roles\/\d+$/.test(res.url()) && res.request().method() === 'DELETE'
    );

    await clickActionButton(page, 'ELIMINAR');
    await page.getByRole('button', { name: /^Eliminar$/i }).click();
    const deleteResponse = await deleteResponsePromise;

    expect([200, 202, 204]).toContain(deleteResponse.status());
    await expectSnackMessage(page, /Rol eliminado|Registro eliminado|eliminado/i);
  });
});
