import { test, expect } from '@playwright/test';
import {
  loginAsCompanyAdmin,
  switchCompanyRoleFromProfile,
  openModuleScreen,
  clickActionButton,
  getActiveDialog,
  clickDialogButton,
  expectSnackMessage,
} from './helpers/e2e.shared.utils';

const PROTECTED_ROLE_REGEX = /(ROLE_ADMINISTRADOR_EMPRESA|ROLE_ADMINISTRADOR_SISTEMA|ADMIN\s*EMPRESA|ADMIN\s*SISTEMA)/i;

async function waitForGridRowsLoaded(page, minRows = 1, timeout = 12000) {
  await expect
    .poll(async () => page.locator('[role="row"][data-id]').count(), {
      timeout,
      message: 'No se cargaron filas en la tabla dentro del tiempo esperado',
    })
    .toBeGreaterThanOrEqual(minRows);
}

async function waitForPermissionsTreeReady(dialog, timeout = 3000) {
  await expect
    .poll(async () => dialog.locator('input[type="checkbox"]').count(), { timeout })
    .toBeGreaterThan(0);
}

async function openSubsistemaAndModulo(dialog, page, treeTimeout = 3000) {
  // Espera reactiva corta para que el árbol de permisos termine de montar.
  await expect(dialog.locator('.MuiAccordionSummary-root').first()).toBeVisible({ timeout: treeTimeout });
  await waitForPermissionsTreeReady(dialog, treeTimeout);

  const subsistemaAccordion = dialog.locator('.MuiAccordionSummary-root').first();
  await expect(subsistemaAccordion).toBeVisible();
  await subsistemaAccordion.click();
  await waitForPermissionsTreeReady(dialog, treeTimeout);

  const moduloAccordion = dialog.locator('.MuiAccordionSummary-root').nth(1);
  if (await moduloAccordion.isVisible().catch(() => false)) {
    await moduloAccordion.click({ force: true }).catch(() => {});
    await waitForPermissionsTreeReady(dialog, treeTimeout);
  }
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

async function ensureIdSortDescending(page) {
  const idHeader = page.getByRole('columnheader', { name: /^ID$/i }).first();
  if (!(await idHeader.isVisible().catch(() => false))) {
    return;
  }

  for (let attempt = 0; attempt < 3; attempt += 1) {
    const rows = await getGridRowsSnapshot(page);
    const numericIds = rows
      .map((r) => parseRowId(r.id))
      .filter((id) => Number.isFinite(id));

    if (numericIds.length >= 2 && numericIds[0] >= numericIds[1]) {
      return;
    }

    await idHeader.click();
    await page.waitForTimeout(120);
  }
}

async function pickSafeRowByHighestId(page, actionLabel = 'modificar') {
  await ensureIdSortDescending(page);

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

async function selectRoleForCreation(page, dialog) {
  const existingRoles = await page
    .locator('[role="row"][data-id] [role="cell"]:nth-child(2)')
    .allTextContents()
    .then((values) => values.map((v) => v.trim()).filter(Boolean));

  const roleCombo = dialog.getByRole('combobox').first();
  await roleCombo.click();
  const options = page.getByRole('option');
  const optionCount = await options.count();

  let fallbackText = '';
  for (let i = 0; i < optionCount; i += 1) {
    const option = options.nth(i);
    const text = ((await option.textContent()) || '').trim();
    if (!text) continue;
    if (!fallbackText) fallbackText = text;

    const normalized = text.split('-').pop()?.trim() || text;
    const existsInGrid = existingRoles.some((r) => r.toLowerCase().includes(normalized.toLowerCase()));
    if (!existsInGrid) {
      await option.click();
      return text;
    }
  }

  await options.first().click();
  return fallbackText;
}

test.describe('RF-036 - Empresa Rol (admin empresa) casos positivos', () => {
  test.beforeEach(async ({ page, request }) => {
    await loginAsCompanyAdmin(page, request, 'EmpresaRol');
    await switchCompanyRoleFromProfile(page, { roleName: 'ROLE_ADMINISTRADOR_EMPRESA' });
    await openModuleScreen(page, 'EmpresaRol', /Roles de Empresa/i);
  });

  test('ERA-01: visualizar listado de roles con permisos y estado', async ({ page }) => {
    await expect(page.getByRole('columnheader', { name: 'Rol' })).toBeVisible();
    await expect(page.getByRole('columnheader', { name: 'Permisos' })).toBeVisible();
    await expect(page.getByRole('columnheader', { name: 'Estado' })).toBeVisible();
    await waitForGridRowsLoaded(page);
  });

  test('ERA-02: crear rol para empresa con permisos', async ({ page }) => {
    await clickActionButton(page, 'AGREGAR');
    const dialog = await getActiveDialog(page);

    await expect(dialog.getByText(/Crear Rol y Asignar Permisos/i)).toBeVisible();
    await selectRoleForCreation(page, dialog);

    await openSubsistemaAndModulo(dialog, page, 7000);

    const permissionCheckboxes = dialog.getByRole('checkbox');
    const permissionCount = await permissionCheckboxes.count();
    const permisoCheckbox = permissionCount > 1 ? permissionCheckboxes.nth(1) : permissionCheckboxes.first();
    await expect(permisoCheckbox).toBeVisible();
    const wasChecked = await permisoCheckbox.isChecked().catch(() => false);
    if (wasChecked) {
      await permisoCheckbox.uncheck({ force: true });
    } else {
      await permisoCheckbox.check({ force: true });
    }

    await clickDialogButton(page, 'Guardar');
    await expectSnackMessage(page, /Permisos actualizados correctamente|guardado|actualizados/i);

    await waitForGridRowsLoaded(page);

  });

  test('ERA-03: actualizar rol/permisos seleccionados', async ({ page }) => {
    test.slow();
    await waitForGridRowsLoaded(page, 1, 20000);

    const rowToUpdate = await pickSafeRowByHighestId(page, 'actualizar');
    await rowToUpdate.row.click();
    await clickActionButton(page, 'ACTUALIZAR');

    const dialog = await getActiveDialog(page);
    await expect(dialog.getByText(/Editar Rol y Permisos/i)).toBeVisible();

    await openSubsistemaAndModulo(dialog, page, 5000);

    const firstCheckbox = dialog.getByRole('checkbox').first();
    await expect(firstCheckbox).toBeVisible();
    await firstCheckbox.check({ force: true });

    await clickDialogButton(page, 'Guardar');

    await expectSnackMessage(page, /Permisos actualizados correctamente|guardado|actualizados/i);
  });

  test('ERA-04: ver permisos completos desde botón Ver permisos', async ({ page }) => {
    await waitForGridRowsLoaded(page);

    const rowToView = await pickSafeRowByHighestId(page, 'visualizar');
    await rowToView.row.click();
    await page.getByRole('button', { name: /Ver permisos/i }).click();

    const dialog = await getActiveDialog(page);
    await expect(dialog.getByText(/Permisos del Rol/i)).toBeVisible();
    await clickDialogButton(page, 'Cerrar');
  });

  test('ERA-05: eliminar relación rol-empresa y permisos', async ({ page }) => {
    await waitForGridRowsLoaded(page);
    const rowToDelete = await pickSafeRowByHighestId(page, 'eliminar');
    await rowToDelete.row.click();

    const deleteResponsePromise = page.waitForResponse(
      (res) => /\/v1\/empresa-rol\/\d+$/.test(res.url()) && res.request().method() === 'DELETE'
    );

    await clickActionButton(page, 'ELIMINAR');
    await page.getByRole('button', { name: /^Eliminar$/i }).click();
    const deleteResponse = await deleteResponsePromise;

    expect([200, 202, 204]).toContain(deleteResponse.status());
    await expectSnackMessage(page, /eliminados correctamente|eliminado|eliminar/i);
  });
});
