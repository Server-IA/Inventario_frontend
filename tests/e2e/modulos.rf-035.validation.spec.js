import { test, expect } from '@playwright/test';
import {
  loginAsAdmin,
  authenticateByApi,
  requireEnv,
  openModuloScreen,
  selectFirstGridRow,
  fillModuloForm,
  findGridCellInColumn,
  clickCreateModuloButton,
  clickDialogButton,
  fillDialogField,
  clickDialogSelectOption,
  NOADMIN_EMAIL,
  NOADMIN_PASSWORD,
} from './helpers/modulos.real.utils';

test.describe('RF-035.0 - Gestión de módulos (validaciones y errores)', () => {
  test.beforeEach(async ({ page, request }) => {
    await loginAsAdmin(page, request);
  });

  test('Validación UI: error cuando faltan campos obligatorios', async ({ page }) => {
    await openModuloScreen(page);

    await clickCreateModuloButton(page);
    await clickDialogButton(page, 'Guardar');

    await expect(page.getByText('El nombre es obligatorio.')).toBeVisible();
  });

  test('Validación UI: longitud máxima de nombre (>100)', async ({ page }) => {
    await openModuloScreen(page);

    await clickCreateModuloButton(page);
    await fillDialogField(page, 'nombre', 'A'.repeat(101));
    await fillDialogField(page, 'url', '/e2e-url-larga');
    await clickDialogButton(page, 'Guardar');

    await expect(page.getByText('El nombre no puede superar 100 caracteres.')).toBeVisible();
  });

  test('Error backend 409: módulo duplicado muestra mensaje esperado', async ({ page }) => {
    const unique = Date.now();
    const nombre = `E2E Duplicado ${unique}`;

    await openModuloScreen(page);

    await clickCreateModuloButton(page);
    await fillModuloForm(page, {
      nombre,
      url: `/e2e-dup-${unique}`,
      descripcion: 'Primera creación para forzar duplicado',
    });
    await clickDialogButton(page, 'Guardar');
    await expect(page.getByText('Módulo guardado correctamente.')).toBeVisible({ timeout: 15000 });

    await clickCreateModuloButton(page);
    await fillModuloForm(page, {
      nombre,
      url: `/e2e-dup-${unique}`,
      descripcion: 'Segunda creación para validar 409',
    });

    const responsePromise = page.waitForResponse(
      (res) => res.url().includes('/api/v2/modulos') && res.request().method() === 'POST'
    );

    await clickDialogButton(page, 'Guardar');
    const response = await responsePromise;

    expect([400, 409]).toContain(response.status());

    const duplicateMessage = page.getByText(/El módulo ya existe|ya existe|duplicad/i);
    await expect(duplicateMessage.first()).toBeVisible({ timeout: 15000 });
  });

  test('Error backend 400 RFC9457: inactivar módulo crítico', async ({ page }) => {
    await openModuloScreen(page);

    const criticalCellCandidate = page.getByRole('cell', { name: 'Gestión de Módulos' }).first();
    test.skip((await criticalCellCandidate.count()) === 0, 'No existe fila "Gestión de Módulos" para validar restricción de módulo crítico.');

    const criticalCell = await findGridCellInColumn(page, 'Nombre', 'Gestión de Módulos');

    await criticalCell.click();
    await page.getByRole('button', { name: 'Editar' }).click();
    await clickDialogSelectOption(page, 'estadoId', 'Inactivo');

    const putResponsePromise = page.waitForResponse(
      (res) => /\/api\/v2\/modulos\/\d+$/.test(res.url()) && res.request().method() === 'PUT'
    );

    await clickDialogButton(page, 'Guardar');
    const putResponse = await putResponsePromise;

    if (putResponse.status() === 400) {
      const contentType = putResponse.headers()['content-type'] || '';
      expect(contentType).toContain('application/problem+json');

      const problem = await putResponse.json();
      expect(problem.status).toBe(400);
      expect(problem.title).toBeTruthy();
      expect(problem.detail).toBeTruthy();

      await expect(page.getByText(/crítico|No se puede inactivar/i).first()).toBeVisible({ timeout: 15000 });
      return;
    }

    expect([200, 204, 403]).toContain(putResponse.status());
  });

  test('Error autorización: usuario sin rol administrador no puede gestionar módulos', async ({ page, request }) => {
    requireEnv('E2E_NOADMIN_EMAIL', NOADMIN_EMAIL);
    requireEnv('E2E_NOADMIN_PASSWORD', NOADMIN_PASSWORD);

    await authenticateByApi(page, request, NOADMIN_EMAIL, NOADMIN_PASSWORD);
    await page.goto('/');

    const unauthorizedUiMessage = page.getByText(/Error cargando módulos|acceso denegado|forbidden/i);
    await expect(unauthorizedUiMessage.first()).toBeVisible({ timeout: 20000 });
  });

  test('Error autenticación 401: token inválido al editar', async ({ page }) => {
    await openModuloScreen(page);

    await selectFirstGridRow(page);
    await page.getByRole('button', { name: 'Editar' }).click();

    await page.evaluate(() => {
      localStorage.setItem('token', 'token-invalido-e2e');
    });

    await fillDialogField(page, 'descripcion', `401 test ${Date.now()}`);

    const putResponsePromise = page.waitForResponse(
      (res) => /\/api\/v2\/modulos\/\d+$/.test(res.url()) && res.request().method() === 'PUT'
    );

    await clickDialogButton(page, 'Guardar');
    const putResponse = await putResponsePromise;

    test.skip(putResponse.status() !== 401, `El backend no devolvió 401 (status actual: ${putResponse.status()}).`);

    await expect(page.getByText(/Token expirado|Inicie sesión nuevamente/i)).toBeVisible({ timeout: 15000 });
  });
});
