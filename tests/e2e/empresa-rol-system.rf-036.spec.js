// En construccion

// import { test, expect } from '@playwright/test';
// import {
//   loginAsSystemAdmin,
//   openModuleScreen,
//   clickActionButton,
//   selectFirstGridRow,
//   getActiveDialog,
//   clickDialogButton,
//   selectDialogFirstOptionByLabel,
//   checkFirstDialogCheckbox,
//   expectSnackMessage,
// } from './helpers/e2e.shared.utils';

// test.describe('RF-036 - Empresa Rol System (casos positivos)', () => {
//   test.beforeEach(async ({ page, request }) => {
//     await loginAsSystemAdmin(page, request, 'EmpresaRolsystem');
//     await openModuleScreen(page, 'EmpresaRolsystem', /Gestión Empresa-Rol \(System\)/i);
//   });

//   test('ERS-01: visualizar listado con empresa, rol y estado', async ({ page }) => {
//     await expect(page.getByRole('columnheader', { name: 'Empresa' })).toBeVisible();
//     await expect(page.getByRole('columnheader', { name: 'Rol' })).toBeVisible();
//     await expect(page.getByRole('columnheader', { name: 'Estado' })).toBeVisible();

//     const totalRows = await page.locator('[role="row"][data-id]').count();
//     expect(totalRows).toBeGreaterThan(0);
//   });

//   test('ERS-02: crear relación empresa-rol y asignar permisos', async ({ page }) => {
//     await clickActionButton(page, 'AGREGAR');
//     const dialog = await getActiveDialog(page);

//     await expect(dialog.getByText(/Crear Empresa-Rol/i)).toBeVisible();
//     await expect(dialog.getByLabel(/Rol/i)).toBeVisible();

//     await selectDialogFirstOptionByLabel(page, 'Rol');
//     await clickDialogButton(page, 'Crear');

//     await expect(dialog.getByText(/Selecciona Módulos/i)).toBeVisible({ timeout: 15000 });
//     await checkFirstDialogCheckbox(page);
//     await clickDialogButton(page, 'Asignar Permisos');

//     await expectSnackMessage(page, /Permisos asignados correctamente|Rol creado/i);
//   });

//   test('ERS-03: actualizar relación seleccionada', async ({ page }) => {
//     await selectFirstGridRow(page);
//     await clickActionButton(page, 'ACTUALIZAR');

//     const dialog = await getActiveDialog(page);
//     await expect(dialog.getByText(/Actualizar Empresa-Rol/i)).toBeVisible();
//     await expect(dialog.getByLabel(/Rol/i)).toBeVisible();
//   });

//   test('ERS-04: eliminar relación empresa-rol seleccionada', async ({ page }) => {
//     await selectFirstGridRow(page);

//     const deleteResponsePromise = page.waitForResponse(
//       (res) => /\/v1\/system\/empresa-rol\/\d+$/.test(res.url()) && res.request().method() === 'DELETE'
//     );

//     await clickActionButton(page, 'ELIMINAR');
//     const deleteResponse = await deleteResponsePromise;

//     expect([200, 202, 204]).toContain(deleteResponse.status());
//     await expectSnackMessage(page, /Registro eliminado correctamente|eliminar/i);
//   });
// });
