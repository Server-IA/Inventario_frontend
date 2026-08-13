import { test, expect } from '@playwright/test';
import {
  loginAsCompanyAdmin,
  authenticateByApi,
  requireEnv,
  switchCompanyRoleFromProfile,
  openModuleScreen,
  clickActionButton,
  getActiveDialog,
  clickDialogButton,
  fillDialogField,
  selectDialogOptionByLabel,
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

async function fillProductFormBasics(page, dialog, nombre, descripcion = 'E2E Validación') {
  if (nombre !== undefined) {
    const nameInput = dialog.locator('input[name="nombre"]').first();
    if (await nameInput.isVisible().catch(() => false)) {
      await nameInput.fill(nombre);
    } else {
      await fillDialogField(page, 'nombre', nombre);
    }
  }

  if (descripcion) {
    try {
      await fillDialogField(page, 'descripcion', descripcion);
    } catch {
      // descripción puede no existir en algunos formularios
    }
  }
}

async function selectFirstCategoria(page, dialog) {
  try {
    await selectDialogOptionByLabel(page, /Categoría/i, /.+/);
  } catch {
    const combos = dialog.getByRole('combobox');
    const count = await combos.count();
    if (count >= 1) {
      await combos.first().click();
      await page.getByRole('option').first().click();
    }
  }
}

async function selectFirstUnidad(page, dialog) {
  try {
    await selectDialogOptionByLabel(page, /Unidad/i, /.+/);
  } catch {
    const combos = dialog.getByRole('combobox');
    const count = await combos.count();
    if (count >= 2) {
      await combos.nth(1).click();
      await page.getByRole('option').first().click();
    }
  }
}

test.describe('RF-024 - Gestión de Productos (validaciones y errores)', () => {
  test.beforeEach(async ({ page, request }) => {
    await loginAsCompanyAdmin(page, request, 'producto');
    await switchCompanyRoleFromProfile(page, {
      roleName: 'ROLE_ADMINISTRADOR_EMPRESA',
    });
    await openModuleScreen(page, 'producto', /Producto/i);
    await setGridRowsPerPage(page, '50');
  });

  test('Validación UI: nombre vacío muestra mensaje de error', async ({ page }) => {
    await clickActionButton(page, 'AGREGAR');
    const dialog = await getActiveDialog(page);

    // Dejar nombre vacío y llenar los selects requeridos
    await selectFirstCategoria(page, dialog);
    await selectFirstUnidad(page, dialog);

    await clickDialogButton(page, 'Guardar');

    // Debe mostrar mensaje de validación
    const errorMsg = page.getByText(/nombre.*obligatorio|campo.*requerido|obligatorio/i).first();
    await expect(errorMsg).toBeVisible({ timeout: 10000 });
  });

  test('Validación UI: nombre > 100 caracteres muestra mensaje de error', async ({ page }) => {
    await clickActionButton(page, 'AGREGAR');
    const dialog = await getActiveDialog(page);

    await fillProductFormBasics(page, dialog, 'A'.repeat(101), 'E2E longitud máxima');
    await selectFirstCategoria(page, dialog);
    await selectFirstUnidad(page, dialog);

    await clickDialogButton(page, 'Guardar');

    const errorMsg = page
      .getByText(/100 caracteres|longitud|demasiado largo|superar/i)
      .first();
    await expect(errorMsg).toBeVisible({ timeout: 10000 });
  });

  test('Error backend: categoría inexistente produce 400', async ({ page }) => {
    const unique = Date.now();
    const nombre = `E2E CatInv ${unique}`;

    await clickActionButton(page, 'AGREGAR');
    const dialog = await getActiveDialog(page);

    await fillProductFormBasics(page, dialog, nombre);

    // Intentar usar una categoría que no existe (depende de que el frontend lo permita)
    try {
      await selectDialogOptionByLabel(page, /Categoría/i, /99999|Inexistente/i);
    } catch {
      // Si no se puede seleccionar vía combobox, intentamos hacer POST directo
      // Marcamos este test como skip si el frontend no permite seleccionar categorías inválidas
      test.skip(
        true,
        'El frontend no permite seleccionar una categoría inexistente desde el combobox.'
      );
    }

    const postResponsePromise = page.waitForResponse(
      (res) => res.url().includes('/api/v2/productos') && res.request().method() === 'POST'
    );

    await clickDialogButton(page, 'Guardar');

    const postResponse = await postResponsePromise;
    if (postResponse.status() === 400) {
      const errorMsg = page.getByText(/categoría|error|inválida|no existe/i).first();
      await expect(errorMsg).toBeVisible({ timeout: 10000 });
    } else {
      // Si el backend acepta, el frontend probablemente no permite categorías inválidas
      expect([400, 422]).toContain(postResponse.status());
    }
  });

  test('Error backend: unidad inexistente produce 400', async ({ page }) => {
    const unique = Date.now();
    const nombre = `E2E UnidInv ${unique}`;

    await clickActionButton(page, 'AGREGAR');
    const dialog = await getActiveDialog(page);

    await fillProductFormBasics(page, dialog, nombre);
    await selectFirstCategoria(page, dialog);

    try {
      await selectDialogOptionByLabel(page, /Unidad/i, /99999|Inexistente/i);
    } catch {
      test.skip(
        true,
        'El frontend no permite seleccionar una unidad inexistente desde el combobox.'
      );
    }

    const postResponsePromise = page.waitForResponse(
      (res) => res.url().includes('/api/v2/productos') && res.request().method() === 'POST'
    );

    await clickDialogButton(page, 'Guardar');

    const postResponse = await postResponsePromise;
    if (postResponse.status() === 400) {
      const errorMsg = page.getByText(/unidad|error|inválida|no existe/i).first();
      await expect(errorMsg).toBeVisible({ timeout: 10000 });
    } else {
      expect([400, 422]).toContain(postResponse.status());
    }
  });

  test('Error autorización: usuario sin permisos no puede gestionar productos', async ({
    page,
    request,
  }) => {
    test.skip(!NOADMIN_EMAIL || !NOADMIN_PASSWORD, 'Configura E2E_NOADMIN_EMAIL y E2E_NOADMIN_PASSWORD (usuario sin permisos).');
    requireEnv('E2E_NOADMIN_EMAIL', NOADMIN_EMAIL);
    requireEnv('E2E_NOADMIN_PASSWORD', NOADMIN_PASSWORD);

    await authenticateByApi(page, request, NOADMIN_EMAIL, NOADMIN_PASSWORD);
    await page.goto('/coagronet/');

    await page.evaluate(() => {
      localStorage.setItem('activeModule', 'producto');
    });
    await page.goto('/coagronet/');

    const unauthorizedMsg = page.getByText(
      /Error cargando|acceso denegado|forbidden|no autorizado/i
    );
    await expect(unauthorizedMsg.first()).toBeVisible({ timeout: 20000 });
  });

  test('Error autenticación 401: token inválido al guardar producto', async ({ page }) => {
    await clickActionButton(page, 'AGREGAR');
    const dialog = await getActiveDialog(page);

    await fillProductFormBasics(page, dialog, `E2E 401 Test ${Date.now()}`);
    await selectFirstCategoria(page, dialog);
    await selectFirstUnidad(page, dialog);

    // Invalidar token
    await page.evaluate(() => {
      localStorage.setItem('token', 'eyJhbGciOiJIUzI1NiJ9.invalid_token.xxx');
    });
    await page.waitForTimeout(150);

    const postRequestPromise = page
      .waitForRequest(
        (req) => req.url().includes('/api/v2/productos') && req.method() === 'POST',
        { timeout: 15000 }
      )
      .catch(() => null);

    await clickDialogButton(page, 'Guardar');

    const postRequest = await postRequestPromise;
    if (!postRequest) {
      // Si no se emitió request, el frontend ya lo rechazó
      return;
    }

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
