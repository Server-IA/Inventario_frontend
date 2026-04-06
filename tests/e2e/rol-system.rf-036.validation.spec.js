// En construcción - pendiente de definición final de flujos para gestión de roles a nivel sistema

// import { test, expect } from '@playwright/test';
// import {
//   loginAsSystemAdmin,
//   loginAsNoAdmin,
//   openModuleScreen,
//   clickActionButton,
//   fillDialogFieldByName,
//   clickDialogButton,
//   requireEnv,
//   NOADMIN_EMAIL,
//   NOADMIN_PASSWORD,
// } from './helpers/e2e.shared.utils';

// test.describe('RF-036 - Rol (admin sistema) validaciones', () => {
//   test.beforeEach(async ({ page, request }) => {
//     await loginAsSystemAdmin(page, request, 'rol');
//     await openModuleScreen(page, 'rol', /Gestión de Rol/i);
//   });

//   test('RSV-01: validación de campos obligatorios', async ({ page }) => {
//     await clickActionButton(page, 'AGREGAR');
//     await clickDialogButton(page, 'Agregar');

//     await expect(page.getByText(/nombre del rol es obligatorio/i)).toBeVisible();
//     await expect(page.getByText(/descripción es obligatoria/i)).toBeVisible();
//   });

//   test('RSV-02: validación de formato en nombre de rol', async ({ page }) => {
//     await clickActionButton(page, 'AGREGAR');

//     await fillDialogFieldByName(page, 'nombre', 'rol invalido 123');
//     await fillDialogFieldByName(page, 'descripcion', 'Descripción válida');
//     await clickDialogButton(page, 'Agregar');

//     await expect(page.getByText(/Solo letras mayúsculas y un solo guion bajo/i)).toBeVisible();
//   });

//   test('RSV-03: usuario no autorizado no puede gestionar roles del sistema', async ({ page, request }) => {
//     requireEnv('E2E_NOADMIN_EMAIL', NOADMIN_EMAIL);
//     requireEnv('E2E_NOADMIN_PASSWORD', NOADMIN_PASSWORD);

//     await loginAsNoAdmin(page, request, 'rol');
//     await page.goto('/');

//     await expect(page.getByText(/acceso denegado|forbidden|error al cargar roles/i).first()).toBeVisible({ timeout: 20000 });
//   });
// });
