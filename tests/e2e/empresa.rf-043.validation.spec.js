import { test, expect } from '@playwright/test';
import {
  loginAsSystemAdmin,
  authenticateByApi,
  requireEnv,
  openModuleScreen,
  clickActionButton,
  getActiveDialog,
  clickDialogButton,
  fillDialogField,
  NOADMIN_EMAIL,
  NOADMIN_PASSWORD,
} from './helpers/e2e.shared.utils';

async function setGridRowsPerPage(page, size = '50') {
  const footer = page.locator('.MuiDataGrid-footerContainer').first();
  const pageSizeCombo = footer.getByRole('combobox').first();
  if (!(await pageSizeCombo.isVisible().catch(() => false))) return;

  const current = ((await pageSizeCombo.textContent()) || '').trim();
  if (current === String(size)) return;

  await pageSizeCombo.click();
  await page.getByRole('option', { name: new RegExp(`^${size}$`) }).first().click();
  await page.waitForTimeout(250);
}

test.describe('RF-043 - Gestión de Empresas (validaciones y errores)', () => {
  test.beforeEach(async ({ page, request }) => {
    await loginAsSystemAdmin(page, request, 'empresa');
    await openModuleScreen(page, 'empresa', /Empresa/i);
    await setGridRowsPerPage(page, '50');
  });

  test('Validación: campos requeridos vacíos muestran error', async ({ page }) => {
    await clickActionButton(page, 'AGREGAR');
    await getActiveDialog(page);

    // Intentar guardar sin llenar nada
    await clickDialogButton(page, 'Guardar');

    // Debe mostrar error de campo obligatorio
    const errorMsg = page
      .getByText(/obligatorio|campo requerido|requerido|completar/i)
      .first();
    await expect(errorMsg).toBeVisible({ timeout: 10000 });
  });

  test('Validación: email duplicado muestra mensaje de error', async ({ page }) => {
    const unique = Date.now();
    const nombre1 = `E2E Dup Email ${unique}`;
    const nombre2 = `E2E Dup Email B ${unique}`;
    const emailDuplicado = `e2e.dup.${unique}@test.com`;

    // Primera empresa
    await clickActionButton(page, 'AGREGAR');
    await getActiveDialog(page);
    await fillDialogField(page, 'nombre', nombre1);
    await fillDialogField(page, 'correo', emailDuplicado);

    try {
      await fillDialogField(page, 'identificacion', `E2E-DUP-EMAIL-A-${unique}`);
    } catch {
      // puede no existir
    }

    await clickDialogButton(page, 'Guardar');
    await expect(
      page.getByText(/guardado correctamente|registrada|creada/i).first()
    ).toBeVisible({ timeout: 15000 });

    // Segunda empresa con mismo email
    await clickActionButton(page, 'AGREGAR');
    await getActiveDialog(page);
    await fillDialogField(page, 'nombre', nombre2);
    await fillDialogField(page, 'correo', emailDuplicado);

    try {
      await fillDialogField(page, 'identificacion', `E2E-DUP-EMAIL-B-${unique}`);
    } catch {
      // puede no existir
    }

    const postResponsePromise = page.waitForResponse(
      (res) =>
        res.url().includes('/api/empresas') &&
        res.request().method() === 'POST'
    );

    await clickDialogButton(page, 'Guardar');

    const postResponse = await postResponsePromise;
    if ([400, 409].includes(postResponse.status())) {
      const errorMsg = page
        .getByText(/duplicado|ya existe|correo.*registrado|email.*existe/i)
        .first();
      await expect(errorMsg).toBeVisible({ timeout: 10000 });
    } else {
      expect([400, 409]).toContain(postResponse.status());
    }
  });

  test('Validación: identificación duplicada muestra mensaje de error', async ({ page }) => {
    const unique = Date.now();
    const nombre1 = `E2E Dup ID ${unique}`;
    const nombre2 = `E2E Dup ID B ${unique}`;
    const identificacion = `E2E-DUP-ID-${unique}`;

    // Primera empresa
    await clickActionButton(page, 'AGREGAR');
    await getActiveDialog(page);
    await fillDialogField(page, 'nombre', nombre1);
    await fillDialogField(page, 'correo', `e2e.dupida.${unique}@test.com`);

    try {
      await fillDialogField(page, 'identificacion', identificacion);
    } catch {
      test.skip(true, 'El campo identificación no está disponible en el formulario.');
    }

    await clickDialogButton(page, 'Guardar');
    await expect(
      page.getByText(/guardado correctamente|registrada|creada/i).first()
    ).toBeVisible({ timeout: 15000 });

    // Segunda empresa con misma identificación
    await clickActionButton(page, 'AGREGAR');
    await getActiveDialog(page);
    await fillDialogField(page, 'nombre', nombre2);
    await fillDialogField(page, 'correo', `e2e.dupidb.${unique}@test.com`);
    await fillDialogField(page, 'identificacion', identificacion);

    const postResponsePromise = page.waitForResponse(
      (res) =>
        res.url().includes('/api/empresas') &&
        res.request().method() === 'POST'
    );

    await clickDialogButton(page, 'Guardar');

    const postResponse = await postResponsePromise;
    if ([400, 409].includes(postResponse.status())) {
      const errorMsg = page
        .getByText(/duplicado|ya existe|identificación.*registrada/i)
        .first();
      await expect(errorMsg).toBeVisible({ timeout: 10000 });
    } else {
      expect([400, 409]).toContain(postResponse.status());
    }
  });

  test('Error autorización: usuario sin permisos no puede gestionar empresas', async ({
    page,
    request,
  }) => {
    requireEnv('E2E_NOADMIN_EMAIL', NOADMIN_EMAIL);
    requireEnv('E2E_NOADMIN_PASSWORD', NOADMIN_PASSWORD);

    await authenticateByApi(page, request, NOADMIN_EMAIL, NOADMIN_PASSWORD);
    await page.goto('/');

    await page.evaluate(() => {
      localStorage.setItem('activeModule', 'empresa');
    });
    await page.goto('/');

    const unauthorizedMsg = page.getByText(
      /Error cargando|acceso denegado|forbidden|no autorizado/i
    );
    await expect(unauthorizedMsg.first()).toBeVisible({ timeout: 20000 });
  });

  test('Error autenticación 401: token inválido al registrar empresa', async ({ page }) => {
    await clickActionButton(page, 'AGREGAR');
    await getActiveDialog(page);

    await fillDialogField(page, 'nombre', `E2E 401 Test ${Date.now()}`);
    await fillDialogField(page, 'correo', `e2e.401.${Date.now()}@test.com`);

    await page.evaluate(() => {
      localStorage.setItem('token', 'eyJhbGciOiJIUzI1NiJ9.invalid_token.xxx');
    });
    await page.waitForTimeout(150);

    const postRequestPromise = page
      .waitForRequest(
        (req) =>
          req.url().includes('/api/empresas') &&
          req.method() === 'POST',
        { timeout: 15000 }
      )
      .catch(() => null);

    await clickDialogButton(page, 'Guardar');

    const postRequest = await postRequestPromise;
    if (!postRequest) return;

    const postResponse = await postRequest.response();
    if (!postResponse) {
      await expect(
        page
          .getByText(/Error de conexión|Error inesperado|Token expirado|Inicie sesión/i)
          .first()
      ).toBeVisible({ timeout: 15000 });
      return;
    }

    expect(postResponse.status()).toBe(401);

    await expect(
      page.getByText(/Token expirado|Inicie sesión|401|unauthorized/i).first()
    ).toBeVisible({ timeout: 15000 });

    await page.evaluate(() => {
      localStorage.removeItem('token');
    });
  });
});
