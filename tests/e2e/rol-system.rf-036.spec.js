import { test, expect } from '@playwright/test';
import {
  loginAsSystemAdmin,
  openModuleScreen,
  clickActionButton,
  selectFirstGridRow,
  fillDialogFieldByName,
  selectDialogOptionByLabel,
  clickDialogButton,
  expectSnackMessage,
} from './helpers/e2e.shared.utils';

test.describe('RF-036 - Rol (admin sistema) casos positivos', () => {
  test.beforeEach(async ({ page, request }) => {
    await loginAsSystemAdmin(page, request, 'rol');
    await openModuleScreen(page, 'rol', /Gestión de Rol/i);
  });

  test('RS-01: visualiza listado de roles', async ({ page }) => {
    await expect(page.getByRole('columnheader', { name: 'Nombre' })).toBeVisible();
    await expect(page.getByRole('columnheader', { name: 'Descripción' })).toBeVisible();
    await expect(page.getByRole('columnheader', { name: 'Estado' })).toBeVisible();

    const totalRows = await page.locator('[role="row"][data-id]').count();
    expect(totalRows).toBeGreaterThan(0);
  });

  test('RS-02: crear rol con nombre, descripción y estado', async ({ page }) => {
    const unique = Date.now();
    const roleName = `E2E_ROLE_${unique}`;

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

    await expect(page.getByRole('cell', { name: roleName }).first()).toBeVisible({ timeout: 15000 });
  });

  test('RS-03: actualizar rol existente', async ({ page }) => {
    await selectFirstGridRow(page);
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
});
