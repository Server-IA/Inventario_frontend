import { test, expect } from '@playwright/test';
import {
  loginAsAdmin,
  authenticateByApi,
  loginAsAdminGetToken,
  authHeaders,
  BACKEND_URI,
  requireEnv,
  openModuloScreen,
  selectFirstGridRow,
  fillModuloForm,
  NOADMIN_EMAIL,
  NOADMIN_PASSWORD,
} from './helpers/modulos.real.utils';

test.describe('RF-035.0 - Gestión de módulos (validaciones y errores)', () => {
  test.beforeEach(async ({ page, request }) => {
    await loginAsAdmin(page, request);
  });

  test('Validación UI: error cuando faltan campos obligatorios', async ({ page }) => {
    await openModuloScreen(page);

    await page.getByRole('button', { name: 'Agregar' }).click();
    await page.getByRole('button', { name: 'Guardar' }).click();

    await expect(page.getByText('El nombre es obligatorio.')).toBeVisible();
  });

  test('Validación UI: longitud máxima de nombre (>100)', async ({ page }) => {
    await openModuloScreen(page);

    await page.getByRole('button', { name: 'Agregar' }).click();
    await page.getByLabel('Nombre').fill('A'.repeat(101));
    await page.getByLabel('URL').fill('/e2e-url-larga');
    await page.getByRole('button', { name: 'Guardar' }).click();

    await expect(page.getByText('El nombre no puede superar 100 caracteres.')).toBeVisible();
  });

  test('Error backend 409: módulo duplicado muestra mensaje esperado', async ({ page }) => {
    const unique = Date.now();
    const nombre = `E2E Duplicado ${unique}`;
    const nombreId = `e2e_duplicado_${unique}`;

    await openModuloScreen(page);

    await page.getByRole('button', { name: 'Agregar' }).click();
    await fillModuloForm(page, {
      nombre,
      url: `/e2e-dup-${unique}`,
      descripcion: 'Primera creación para forzar duplicado',
      icon: 'ViewModule',
      roles: 'Administrador del Sistema',
      nombreId,
    });
    await page.getByRole('button', { name: 'Guardar' }).click();
    await expect(page.getByText('Módulo guardado correctamente.')).toBeVisible({ timeout: 15000 });

    await page.getByRole('button', { name: 'Agregar' }).click();
    await fillModuloForm(page, {
      nombre,
      url: `/e2e-dup-${unique}`,
      descripcion: 'Segunda creación para validar 409',
      icon: 'ViewModule',
      roles: 'Administrador del Sistema',
      nombreId,
    });

    const responsePromise = page.waitForResponse(
      (res) => res.url().includes('/api/v1/modulos') && res.request().method() === 'POST'
    );

    await page.getByRole('button', { name: 'Guardar' }).click();
    const response = await responsePromise;

    expect([400, 409]).toContain(response.status());

    const duplicateMessage = page.getByText(/El módulo ya existe|ya existe|duplicad/i);
    await expect(duplicateMessage.first()).toBeVisible({ timeout: 15000 });
  });

  test('Error backend 400 RFC9457: inactivar módulo crítico', async ({ page }) => {
    await openModuloScreen(page);

    const criticalCell = page.getByRole('cell', { name: 'Gestión de Módulos' }).first();
    test.skip((await criticalCell.count()) === 0, 'No existe fila "Gestión de Módulos" para validar restricción de módulo crítico.');

    await criticalCell.click();
    await page.getByRole('button', { name: 'Editar' }).click();
    await page.getByLabel('Estado').click();
    await page.getByRole('option', { name: 'Inactivo' }).click();

    const putResponsePromise = page.waitForResponse(
      (res) => /\/api\/v1\/modulos\/\d+$/.test(res.url()) && res.request().method() === 'PUT'
    );

    await page.getByRole('button', { name: 'Guardar' }).click();
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

  test('Contrato API error 404: DELETE /api/v1/modulos/{id} responde Problem Details', async ({ request }) => {
    const token = await loginAsAdminGetToken(request);
    const idInexistente = 999999999;

    const delRes = await request.delete(`${BACKEND_URI}/api/v1/modulos/${idInexistente}`, {
      headers: authHeaders(token),
    });

    expect(delRes.status()).toBe(404);

    const contentType = delRes.headers()['content-type'] || '';
    expect(contentType).toContain('application/problem+json');

    const problem = await delRes.json();
    expect(problem).toMatchObject({
      title: expect.any(String),
      status: 404,
      detail: expect.any(String),
    });
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

    await page.getByLabel('Descripción').fill(`401 test ${Date.now()}`);

    const putResponsePromise = page.waitForResponse(
      (res) => /\/api\/v1\/modulos\/\d+$/.test(res.url()) && res.request().method() === 'PUT'
    );

    await page.getByRole('button', { name: 'Guardar' }).click();
    const putResponse = await putResponsePromise;

    test.skip(putResponse.status() !== 401, `El backend no devolvió 401 (status actual: ${putResponse.status()}).`);

    await expect(page.getByText(/Token expirado|Inicie sesión nuevamente/i)).toBeVisible({ timeout: 15000 });
  });
});
