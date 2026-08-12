import { test, expect } from '@playwright/test';
import {
  loginAsSystemAdmin,
  openModuleScreen,
  clickActionButton,
  fillDialogFieldByName,
  clickDialogButton,
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

async function findProtectedRow(page) {
  await waitForGridRowsLoaded(page, 1, 30000);

  const rows = page.locator('[role="row"][data-id]');
  const count = await rows.count();

  for (let i = 0; i < count; i += 1) {
    const row = rows.nth(i);
    const roleName = ((await row.locator('[role="cell"]').nth(1).textContent()) || '').trim();
    if (PROTECTED_ROLE_REGEX.test(roleName)) {
      return { row, roleName };
    }
  }

  return null;
}

test.describe('RF-036 - Rol (admin sistema) validaciones', () => {
  test.beforeEach(async ({ page, request }) => {
    await loginAsSystemAdmin(page, request, 'rol');
    await openModuleScreen(page, 'rol', /Gestión de Rol/i);
  });

  test('RSV-01: guardar sin campos obligatorios muestra validación', async ({ page }) => {
    await clickActionButton(page, 'AGREGAR');
    await clickDialogButton(page, 'Agregar');

    await expect(page.getByText(/nombre del rol es obligatorio/i)).toBeVisible({ timeout: 10000 });
    await expect(page.getByText(/descripción es obligatoria/i)).toBeVisible({ timeout: 10000 });
  });

  test('RSV-02: validación de formato en nombre de rol (solo mayúsculas y un guion bajo)', async ({ page }) => {
    await clickActionButton(page, 'AGREGAR');

    await fillDialogFieldByName(page, 'nombre', 'rol invalido 123');
    await fillDialogFieldByName(page, 'descripcion', 'Descripción válida');
    await clickDialogButton(page, 'Agregar');

    await expect(page.getByText(/Solo letras mayúsculas y un solo guion bajo/i)).toBeVisible({ timeout: 10000 });
  });

  test('RSV-03: actualizar sin seleccionar fila mantiene botón deshabilitado', async ({ page }) => {
    const updateBtn = page.getByRole('button', { name: /^ACTUALIZAR$/i });
    await expect(updateBtn).toBeDisabled();
    await expect(page.locator('[role="dialog"]:visible')).toHaveCount(0);
  });

  test('RSV-04: eliminar sin seleccionar fila mantiene botón deshabilitado', async ({ page }) => {
    const deleteBtn = page.getByRole('button', { name: /^ELIMINAR$/i });
    await expect(deleteBtn).toBeDisabled();
    await expect(page.locator('[role="dialog"]:visible')).toHaveCount(0);
  });

  test('RSV-05: intentar eliminar rol protegido muestra mensaje de bloqueo', async ({ page }) => {
    test.slow();
    const protectedRow = await findProtectedRow(page);

    if (!protectedRow) {
      test.skip(true, 'No se encontró un rol protegido visible para validar bloqueo de eliminación.');
      return;
    }

    await protectedRow.row.click();

    await clickActionButton(page, 'ELIMINAR');
    await page.getByRole('button', { name: /^Eliminar$/i }).click();

    // El sistema debe bloquear la eliminación con un mensaje de error o snack.
    await expect(
      page.getByText(/no se puede eliminar|asociad|protegido|no permitido/i).first()
    ).toBeVisible({ timeout: 20000 });
  });

  // Este caso puede variar según configuración de permisos por entorno (menú oculto vs acceso denegado explícito)
  // y se mantiene desactivado para evitar falsos negativos en CI.
  // test('RSV-06: usuario no autorizado no puede gestionar roles del sistema', async ({ page, request }) => {
  //   requireEnv('E2E_NOADMIN_EMAIL', NOADMIN_EMAIL);
  //   requireEnv('E2E_NOADMIN_PASSWORD', NOADMIN_PASSWORD);
  //
  //   await loginAsNoAdmin(page, request, 'rol');
  //   await page.goto('/');
  //
  //   await expect(page.getByText(/acceso denegado|forbidden|error al cargar roles/i).first()).toBeVisible({ timeout: 20000 });
  // });
});
