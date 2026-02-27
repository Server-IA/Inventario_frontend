import { test, expect } from '@playwright/test';
import {
  loginAsSystemAdmin,
  loginAsNoAdmin,
  openModuleScreen,
  clickActionButton,
  getActiveDialog,
  clickDialogButton,
  requireEnv,
  NOADMIN_EMAIL,
  NOADMIN_PASSWORD,
} from './helpers/e2e.shared.utils';

test.describe('RF-036 - Empresa Rol System (validaciones y errores)', () => {
  test.beforeEach(async ({ page, request }) => {
    await loginAsSystemAdmin(page, request, 'EmpresaRolsystem');
    await openModuleScreen(page, 'EmpresaRolsystem', /Gestión Empresa-Rol \(System\)/i);
  });

  test('ERSV-01: crear sin rol muestra validación', async ({ page }) => {
    await clickActionButton(page, 'AGREGAR');
    const dialog = await getActiveDialog(page);
    await expect(dialog.getByText(/Crear Empresa-Rol/i)).toBeVisible();

    await clickDialogButton(page, 'Crear');
    await expect(page.getByText(/rol es obligatorio|obligatorio/i).first()).toBeVisible({ timeout: 10000 });
  });

  test('ERSV-02: actualizar sin seleccionar fila no abre formulario', async ({ page }) => {
    await clickActionButton(page, 'ACTUALIZAR');
    await expect(page.getByText(/Selecciona un registro|Selecciona/i).first()).toBeVisible({ timeout: 10000 });
  });

  test('ERSV-03: eliminar sin seleccionar fila no ejecuta delete', async ({ page }) => {
    await clickActionButton(page, 'ELIMINAR');
    const dialogCount = await page.locator('[role="dialog"]').count();
    expect(dialogCount).toBe(0);
  });

  test('ERSV-04: usuario no autorizado no debe gestionar EmpresaRolSystem', async ({ page, request }) => {
    requireEnv('E2E_NOADMIN_EMAIL', NOADMIN_EMAIL);
    requireEnv('E2E_NOADMIN_PASSWORD', NOADMIN_PASSWORD);

    await loginAsNoAdmin(page, request, 'EmpresaRolsystem');
    await page.goto('/');

    await expect(page.getByText(/acceso denegado|forbidden|error al cargar/i).first()).toBeVisible({ timeout: 20000 });
  });
});
