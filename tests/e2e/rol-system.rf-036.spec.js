// En construcción - pendiente de definición final de flujos para gestión de roles a nivel sistema

// import { test, expect } from '@playwright/test';
// import {
//   loginAsSystemAdmin,
//   openModuleScreen,
//   clickActionButton,
//   fillDialogFieldByName,
//   selectDialogOptionByLabel,
//   clickDialogButton,
//   expectSnackMessage,
//   findGridCellInColumnAcrossPages,
// } from './helpers/e2e.shared.utils';

// const PROTECTED_ROLE_REGEX = /(^|\b)(ROLE_)?ADMIN(ISTRADOR)?[_\s]*(EMPRESA|SISTEMA)\b/i;

// function randomUpperLetters(length = 6) {
//   return Array.from({ length }, () => String.fromCharCode(65 + Math.floor(Math.random() * 26))).join('');
// }

// function buildValidRoleName(prefix = 'ROLE') {
//   // Formato valido en FormRol: solo mayusculas y maximo un '_' en medio.
//   return `${prefix}_${randomUpperLetters(7)}`;
// }

// async function waitForTableRows(page, minRows = 1, timeout = 20000) {
//   await expect
//     .poll(async () => page.locator('[role="row"][data-id]').count(), {
//       timeout,
//       message: 'La tabla de roles no cargó registros en el tiempo esperado',
//     })
//     .toBeGreaterThanOrEqual(minRows);
// }

// async function selectSafeRoleRow(page, actionLabel = 'modificar') {
//   await waitForTableRows(page, 1, 30000);

//   const rows = page.locator('[role="row"][data-id]');
//   const rowCount = await rows.count();

//   for (let i = 0; i < rowCount; i += 1) {
//     const row = rows.nth(i);
//     const roleName = ((await row.locator('[role="cell"]').first().textContent()) || '').trim();

//     if (!PROTECTED_ROLE_REGEX.test(roleName)) {
//       await row.click();
//       return roleName;
//     }
//   }

//   throw new Error(
//     `No hay filas seguras para ${actionLabel}. Todas las filas visibles son roles protegidos (admin empresa/sistema).`
//   );
// }

// test.describe('RF-036 - Rol (admin sistema) casos positivos', () => {
//   test.beforeEach(async ({ page, request }) => {
//     await loginAsSystemAdmin(page, request, 'rol');
//     await openModuleScreen(page, 'rol', /Gestión de Rol/i);
//   });

//   test('RS-01: visualiza listado de roles', async ({ page }) => {
//     await expect(page.getByRole('columnheader', { name: 'Nombre' })).toBeVisible();
//     await expect(page.getByRole('columnheader', { name: 'Descripción' })).toBeVisible();
//     await expect(page.getByRole('columnheader', { name: /^Estado ID$/i })).toBeVisible();
//     await expect(page.getByRole('columnheader', { name: /^Estado$/i })).toBeVisible();

//     await waitForTableRows(page, 1, 30000);
//     const totalRows = await page.locator('[role="row"][data-id]').count();
//     expect(totalRows).toBeGreaterThan(0);
//   });

//   test('RS-02: crear rol con nombre, descripción y estado', async ({ page }) => {
//     const unique = Date.now();
//     const roleName = buildValidRoleName('ROLE');

//     await clickActionButton(page, 'AGREGAR');
//     await fillDialogFieldByName(page, 'nombre', roleName);
//     await fillDialogFieldByName(page, 'descripcion', `Rol de prueba ${unique}`);
//     await selectDialogOptionByLabel(page, 'Estado', 'Activo');

//     const postResponsePromise = page.waitForResponse(
//       (res) => res.url().includes('/v1/roles') && res.request().method() === 'POST'
//     );

//     await clickDialogButton(page, 'Agregar');
//     const postResponse = await postResponsePromise;

//     expect([200, 201]).toContain(postResponse.status());
//     await expectSnackMessage(page, /Rol creado/i);

//     await findGridCellInColumnAcrossPages(page, 'Nombre', roleName, { timeout: 15000, maxPages: 80 });
//   });

//   test('RS-03: actualizar rol existente', async ({ page }) => {
//     await selectSafeRoleRow(page, 'actualizar');
//     await clickActionButton(page, 'ACTUALIZAR');

//     const updatedDescription = `Actualizado E2E ${Date.now()}`;
//     await fillDialogFieldByName(page, 'descripcion', updatedDescription);

//     const putResponsePromise = page.waitForResponse(
//       (res) => /\/v1\/roles\/\d+$/.test(res.url()) && res.request().method() === 'PUT'
//     );

//     await clickDialogButton(page, 'Actualizar');
//     const putResponse = await putResponsePromise;

//     expect([200, 204]).toContain(putResponse.status());
//     await expectSnackMessage(page, /Rol actualizado/i);
//   });

//   test('RS-04: eliminar rol no asociado', async ({ page }) => {
//     const unique = Date.now();
//     const roleName = buildValidRoleName('ROLEDEL');

//     // Crear un rol temporal para garantizar que no esté asociado.
//     await clickActionButton(page, 'AGREGAR');
//     await fillDialogFieldByName(page, 'nombre', roleName);
//     await fillDialogFieldByName(page, 'descripcion', `Rol temporal para eliminar ${unique}`);
//     await selectDialogOptionByLabel(page, 'Estado', 'Activo');

//     const postResponsePromise = page.waitForResponse(
//       (res) => res.url().includes('/v1/roles') && res.request().method() === 'POST'
//     );

//     await clickDialogButton(page, 'Agregar');
//     const postResponse = await postResponsePromise;
//     expect([200, 201]).toContain(postResponse.status());
//     await expectSnackMessage(page, /Rol creado/i);

//     const targetCell = await findGridCellInColumnAcrossPages(page, 'Nombre', roleName, {
//       timeout: 15000,
//       maxPages: 80,
//     });
//     await targetCell.click();

//     const deleteResponsePromise = page.waitForResponse(
//       (res) => /\/v1\/roles\/\d+$/.test(res.url()) && res.request().method() === 'DELETE'
//     );

//     await clickActionButton(page, 'ELIMINAR');
//     await page.getByRole('button', { name: /^Eliminar$/i }).click();
//     const deleteResponse = await deleteResponsePromise;

//     expect([200, 202, 204]).toContain(deleteResponse.status());
//     await expectSnackMessage(page, /Rol eliminado|Registro eliminado|eliminado/i);
//   });
// });
