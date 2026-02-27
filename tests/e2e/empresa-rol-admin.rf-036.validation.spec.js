import { test, expect } from '@playwright/test';
import {
  loginAsCompanyAdmin,
  loginAsNoAdmin,
  openModuleScreen,
  clickActionButton,
  getActiveDialog,
  clickDialogButton,
  requireEnv,
  NOADMIN_EMAIL,
  NOADMIN_PASSWORD,
} from './helpers/e2e.shared.utils';

test.describe('RF-036 - Empresa Rol (admin empresa) validaciones', () => {
  test.beforeEach(async ({ page, request }) => {
    await loginAsCompanyAdmin(page, request, 'EmpresaRol');
    await openModuleScreen(page, 'EmpresaRol', /Roles de Empresa/i);
  });

  test('ERAV-01: guardar sin rol seleccionado muestra validación', async ({ page }) => {
    await clickActionButton(page, 'AGREGAR');
    const dialog = await getActiveDialog(page);
    await expect(dialog.getByText(/Crear Rol y Asignar Permisos/i)).toBeVisible();

    await clickDialogButton(page, 'Guardar');
    await expect(page.getByText(/Debe seleccionar un rol|rol/i).first()).toBeVisible({ timeout: 10000 });
  });

  test('ERAV-02: actualizar sin seleccionar fila no abre modal', async ({ page }) => {
    await clickActionButton(page, 'ACTUALIZAR');
    await expect(page.getByText(/Selecciona una fila|Selecciona/i).first()).toBeVisible({ timeout: 10000 });
  });

  test('ERAV-03: eliminar sin seleccionar fila muestra advertencia', async ({ page }) => {
    await clickActionButton(page, 'ELIMINAR');
    await expect(page.getByText(/Selecciona una fila|Selecciona/i).first()).toBeVisible({ timeout: 10000 });
  });

  test('ERAV-04: usuario sin permisos de empresa no debe gestionar', async ({ page, request }) => {
    requireEnv('E2E_NOADMIN_EMAIL', NOADMIN_EMAIL);
    requireEnv('E2E_NOADMIN_PASSWORD', NOADMIN_PASSWORD);

    await loginAsNoAdmin(page, request, 'EmpresaRol');
    await page.goto('/');

    await expect(page.getByText(/acceso denegado|forbidden|error/i).first()).toBeVisible({ timeout: 20000 });
  });
});
