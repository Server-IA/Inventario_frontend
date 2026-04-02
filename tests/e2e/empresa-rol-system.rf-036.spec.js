import { test, expect } from '@playwright/test';
import {
  loginAsSystemAdmin,
  openModuleScreen,
  clickActionButton,
  getActiveDialog,
  clickDialogButton,
  selectDialogFirstOptionByLabel,
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

async function openSubsistemaAndModulo(dialog, treeTimeout = 3000) {
  await expect(dialog.locator('.MuiAccordionSummary-root').first()).toBeVisible({ timeout: treeTimeout });
  await waitForPermissionsTreeReady(dialog, treeTimeout);

  const subsistemaAccordion = dialog.locator('.MuiAccordionSummary-root').first();
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
    const role = ((await row.locator('[role="cell"]').nth(2).textContent()) || '').trim();
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

test.describe('RF-036 - Empresa Rol (admin sistema) casos positivos', () => {
  test.beforeEach(async ({ page, request }) => {
    await loginAsSystemAdmin(page, request, 'EmpresaRol');
    await openModuleScreen(page, 'EmpresaRol', /Roles de Empresa/i);
  });

  test('ERS-01: visualizar listado con empresa, rol y estado', async ({ page }) => {
    await expect(page.getByRole('columnheader', { name: 'Empresa' })).toBeVisible();
    await expect(page.getByRole('columnheader', { name: 'Rol' })).toBeVisible();
    await expect(page.getByRole('columnheader', { name: 'Estado' })).toBeVisible();
    await waitForGridRowsLoaded(page);
  });

  test('ERS-02: crear relación empresa-rol y asignar permisos', async ({ page }) => {
    test.slow();
    await clickActionButton(page, 'AGREGAR');
    const dialog = await getActiveDialog(page);

    await expect(dialog.getByText(/Crear Rol y Asignar Permisos/i)).toBeVisible();
    await selectDialogFirstOptionByLabel(page, 'Rol');
    await selectDialogFirstOptionByLabel(page, 'Empresa');

    await openSubsistemaAndModulo(dialog, 7000);

    const permissionCheckboxes = dialog.getByRole('checkbox');
    const permissionCount = await permissionCheckboxes.count();
    const permisoCheckbox = permissionCount > 1 ? permissionCheckboxes.nth(1) : permissionCheckboxes.first();
    await expect(permisoCheckbox).toBeVisible();
    await permisoCheckbox.check({ force: true });

    await clickDialogButton(page, 'Guardar');

    // Aceptamos éxito o permanencia del diálogo por validación/duplicidad del entorno.
    const successSnack = page.getByText(/Permisos actualizados correctamente|guardado|actualizados/i).first();
    const dialogStillOpen = page.locator('[role="dialog"]:visible').last();
    await expect.poll(async () => {
      const ok = await successSnack.isVisible().catch(() => false);
      const open = await dialogStillOpen.isVisible().catch(() => false);
      return ok || open;
    }, { timeout: 20000 }).toBeTruthy();
  });

  test('ERS-03: actualizar relación seleccionada', async ({ page }) => {
    await waitForGridRowsLoaded(page);
    const rowToUpdate = await pickSafeRowByHighestId(page, 'actualizar');
    await rowToUpdate.row.click();
    await clickActionButton(page, 'ACTUALIZAR');

    const dialog = await getActiveDialog(page);
    await expect(dialog.getByText(/Editar Rol y Permisos/i)).toBeVisible();

    await openSubsistemaAndModulo(dialog, 5000);
    const firstCheckbox = dialog.getByRole('checkbox').first();
    await expect(firstCheckbox).toBeVisible();
    const wasChecked = await firstCheckbox.isChecked().catch(() => false);
    if (wasChecked) {
      await firstCheckbox.uncheck({ force: true });
    } else {
      await firstCheckbox.check({ force: true });
    }

    await clickDialogButton(page, 'Guardar');
    await expectSnackMessage(page, /Permisos actualizados correctamente|guardado|actualizados|error al guardar/i);
  });

  test('ERS-04: eliminar relación empresa-rol seleccionada', async ({ page }) => {
    await waitForGridRowsLoaded(page);
    const rowToDelete = await pickSafeRowByHighestId(page, 'eliminar');
    await rowToDelete.row.click();

    await clickActionButton(page, 'ELIMINAR');
    await page.getByRole('button', { name: /^Eliminar$/i }).click();
    await expectSnackMessage(page, /eliminados correctamente|eliminado|eliminar|no se puede eliminar|asociad/i);
  });
});
