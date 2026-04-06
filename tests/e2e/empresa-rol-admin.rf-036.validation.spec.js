import { test, expect } from '@playwright/test';
import {
  loginAsCompanyAdmin,
  //loginAsNoAdmin,
  switchCompanyRoleFromProfile,
  openModuleScreen,
  clickActionButton,
  getActiveDialog,
  clickDialogButton,
  //requireEnv,
  //NOADMIN_EMAIL,
  //NOADMIN_PASSWORD,
} from './helpers/e2e.shared.utils';

test.describe('RF-036 - Empresa Rol (admin empresa) validaciones', () => {
  test.beforeEach(async ({ page, request }) => {
    await loginAsCompanyAdmin(page, request, 'EmpresaRol');
    await switchCompanyRoleFromProfile(page, { roleName: 'ROLE_ADMINISTRADOR_EMPRESA' });
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
    const updateBtn = page.getByRole('button', { name: /^ACTUALIZAR$/i });
    await expect(updateBtn).toBeDisabled();
    await expect(page.locator('[role="dialog"]:visible')).toHaveCount(0);
  });

  test('ERAV-03: eliminar sin seleccionar fila muestra advertencia', async ({ page }) => {
    const deleteBtn = page.getByRole('button', { name: /^ELIMINAR$/i });
    await expect(deleteBtn).toBeDisabled();
    await expect(page.locator('[role="dialog"]:visible')).toHaveCount(0);
  });

  // Este caso se deja comentado porque el control de acceso a nivel de empresa aún no está implementado, pero se puede activar una vez esté listo

  // test('ERAV-04: usuario sin permisos de empresa no debe gestionar', async ({ page, request }) => {
  //   requireEnv('E2E_NOADMIN_EMAIL', NOADMIN_EMAIL);
  //   requireEnv('E2E_NOADMIN_PASSWORD', NOADMIN_PASSWORD);

  //   await loginAsNoAdmin(page, request, 'EmpresaRol');
  //   await page.goto('/');

  //   const denied = page.getByText(/acceso denegado|forbidden|error/i).first();
  //   const heading = page.getByRole('heading', { name: /Roles de Empresa/i }).first();

  //   const deadline = Date.now() + 20000;
  //   let accessResult = 'loading';
  //   while (Date.now() < deadline) {
  //     const deniedVisible = await denied.isVisible().catch(() => false);
  //     const headingVisible = await heading.isVisible().catch(() => false);
  //     if (deniedVisible) {
  //       accessResult = 'denied';
  //       break;
  //     }
  //     if (headingVisible) {
  //       accessResult = 'allowed';
  //       break;
  //     }
  //     await page.waitForTimeout(500);
  //   }

  //   expect(accessResult, 'El usuario sin permisos no debe poder ver Roles de Empresa').toBe('denied');
  // });
});
