import { test, expect } from '@playwright/test';
import {
  loginAsCompanyAdmin,
  openModuleScreen,
  clickActionButton,
  selectFirstGridRow,
  getActiveDialog,
  clickDialogButton,
  selectDialogFirstOptionByLabel,
  openFirstAccordionInDialog,
  clickDialogRadioByLabel,
  expectSnackMessage,
} from './helpers/e2e.shared.utils';

test.describe('RF-036 - Empresa Rol (admin empresa) casos positivos', () => {
  test.beforeEach(async ({ page, request }) => {
    await loginAsCompanyAdmin(page, request, 'EmpresaRol');
    await openModuleScreen(page, 'EmpresaRol', /Roles de Empresa/i);
  });

  test('ERA-01: visualizar listado de roles con permisos y estado', async ({ page }) => {
    await expect(page.getByRole('columnheader', { name: 'Rol' })).toBeVisible();
    await expect(page.getByRole('columnheader', { name: 'Permisos' })).toBeVisible();
    await expect(page.getByRole('columnheader', { name: 'Estado' })).toBeVisible();

    const totalRows = await page.locator('[role="row"][data-id]').count();
    expect(totalRows).toBeGreaterThan(0);
  });

  test('ERA-02: crear rol para empresa con permisos', async ({ page }) => {
    await clickActionButton(page, 'AGREGAR');
    const dialog = await getActiveDialog(page);

    await expect(dialog.getByText(/Crear Rol y Asignar Permisos/i)).toBeVisible();
    await selectDialogFirstOptionByLabel(page, 'Rol');

    await openFirstAccordionInDialog(page);
    await clickDialogRadioByLabel(page, 'Todos los permisos');

    await clickDialogButton(page, 'Guardar');
    await expectSnackMessage(page, /Permisos actualizados correctamente|guardado|actualizados/i);
  });

  test('ERA-03: actualizar rol/permisos seleccionados', async ({ page }) => {
    await selectFirstGridRow(page);
    await clickActionButton(page, 'ACTUALIZAR');

    const dialog = await getActiveDialog(page);
    await expect(dialog.getByText(/Editar Rol y Permisos/i)).toBeVisible();

    await openFirstAccordionInDialog(page);
    await clickDialogRadioByLabel(page, 'Solo lectura');
    await clickDialogButton(page, 'Guardar');

    await expectSnackMessage(page, /Permisos actualizados correctamente|guardado|actualizados/i);
  });

  test('ERA-04: ver permisos completos desde botón Ver permisos', async ({ page }) => {
    await selectFirstGridRow(page);
    await page.getByRole('button', { name: /Ver permisos/i }).click();

    const dialog = await getActiveDialog(page);
    await expect(dialog.getByText(/Permisos del Rol/i)).toBeVisible();
    await clickDialogButton(page, 'Cerrar');
  });

  test('ERA-05: eliminar relación rol-empresa y permisos', async ({ page }) => {
    await selectFirstGridRow(page);

    page.once('dialog', (d) => d.accept());

    const deleteResponsePromise = page.waitForResponse(
      (res) => /\/v1\/empresa-rol\/\d+$/.test(res.url()) && res.request().method() === 'DELETE'
    );

    await clickActionButton(page, 'ELIMINAR');
    const deleteResponse = await deleteResponsePromise;

    expect([200, 202, 204]).toContain(deleteResponse.status());
    await expectSnackMessage(page, /eliminados correctamente|eliminado|eliminar/i);
  });
});
