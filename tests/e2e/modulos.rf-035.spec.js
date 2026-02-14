import { test, expect } from '@playwright/test';
import {
  loginAsAdmin,
  loginAsAdminGetToken,
  fetchModulos,
  authHeaders,
  BACKEND_URI,
  fetchCatalogIds,
  createModuloByApi,
  openModuloScreen,
  selectFirstGridRow,
  fillModuloForm,
} from './helpers/modulos.real.utils';

test.describe('RF-035.0 - Gestión de módulos (casos positivos)', () => {
  test.beforeEach(async ({ page, request }) => {
    await loginAsAdmin(page, request);
  });

  test('HU-035.1: visualiza listado de módulos', async ({ page }) => {
    await openModuloScreen(page);

    await expect(page.getByRole('columnheader', { name: 'Nombre' })).toBeVisible();
    await expect(page.getByRole('columnheader', { name: 'Icono' })).toBeVisible();
    await expect(page.getByRole('columnheader', { name: 'Requerido' })).toBeVisible();

    const totalRows = await page.locator('[role="row"][data-id]').count();
    expect(totalRows).toBeGreaterThan(0);
  });

  test('HU-035.2: crear módulo con datos válidos', async ({ page }) => {
    const unique = Date.now();
    const nombre = `E2E Modulo ${unique}`;
    const nombreId = `e2e_modulo_${unique}`;

    await openModuloScreen(page);
    await page.getByRole('button', { name: 'Agregar' }).click();

    await fillModuloForm(page, {
      nombre,
      url: `/e2e-modulo-${unique}`,
      descripcion: 'Módulo creado por prueba E2E real',
      icon: 'ViewModule',
      roles: 'Administrador del Sistema',
      nombreId,
      requerido: true,
    });

    const createResponsePromise = page.waitForResponse(
      (res) => res.url().includes('/api/v1/modulos') && res.request().method() === 'POST'
    );

    await page.getByRole('button', { name: 'Guardar' }).click();

    const createResponse = await createResponsePromise;
    expect([200, 201]).toContain(createResponse.status());

    await expect(page.getByText('Módulo guardado correctamente.')).toBeVisible({ timeout: 15000 });
  });

  test('HU-035.3 y HU-035.4: modifica módulo y obligatoriedad', async ({ page }) => {
    await openModuloScreen(page);

    await selectFirstGridRow(page);
    await page.getByRole('button', { name: 'Editar' }).click();

    const descripcion = `Actualización E2E ${Date.now()}`;
    await page.getByLabel('Descripción').fill(descripcion);

    const requerido = page.getByLabel('Módulo requerido');
    await requerido.click();

    const putResponsePromise = page.waitForResponse(
      (res) => /\/api\/v1\/modulos\/\d+$/.test(res.url()) && res.request().method() === 'PUT'
    );

    await page.getByRole('button', { name: 'Guardar' }).click();

    const putResponse = await putResponsePromise;
    expect([200, 204]).toContain(putResponse.status());

    await expect(page.getByText('Módulo guardado correctamente.')).toBeVisible({ timeout: 15000 });
  });

  test('Contrato API: cambia obligatoriedad con PUT /api/v1/modulos/{id}/requerido', async ({ request }) => {
    const token = await loginAsAdminGetToken(request);
    const modulos = await fetchModulos(request, token);

    expect(modulos.length).toBeGreaterThan(0);

    const objetivo = modulos.find((m) => typeof m?.requerido === 'boolean') || modulos[0];
    const moduloId = objetivo?.id;
    const requeridoActual = Boolean(objetivo?.requerido);
    const requeridoNuevo = !requeridoActual;

    expect(moduloId, 'No se encontró id de módulo para probar endpoint requerido').toBeTruthy();

    const putRes = await request.put(`${BACKEND_URI}/api/v1/modulos/${moduloId}/requerido`, {
      headers: authHeaders(token),
      data: { requerido: requeridoNuevo },
    });

    expect([200, 204]).toContain(putRes.status());

    const verifyRes = await request.get(`${BACKEND_URI}/api/v1/modulos/${moduloId}`, {
      headers: authHeaders(token),
    });

    expect([200, 404]).toContain(verifyRes.status());
    if (verifyRes.status() === 200) {
      const detalle = await verifyRes.json();
      if (typeof detalle?.requerido === 'boolean') {
        expect(detalle.requerido).toBe(requeridoNuevo);
      }
    }
  });

  test('HU-035.5: inactiva módulo con DELETE /api/v1/modulos/{id} (éxito)', async ({ request }) => {
    const token = await loginAsAdminGetToken(request);
    const unique = Date.now();
    const catalog = await fetchCatalogIds(request, token);

    const payload = {
      nombre: `E2E Delete ${unique}`,
      url: `/e2e-delete-${unique}`,
      descripcion: 'Módulo temporal para prueba de inactivación',
      icon: 'ViewModule',
      estadoId: 1,
      subSistemaId: Number(catalog.subSistemaId),
      tipoModuloId: Number(catalog.tipoModuloId),
      tipoAplicacionId: Number(catalog.tipoAplicacionId),
      roles: ['Administrador del Sistema'],
      nombreId: `e2e_delete_${unique}`,
      requerido: false,
    };

    const { createdId } = await createModuloByApi(request, token, payload);

    let moduloId = createdId;
    if (!moduloId) {
      const modulos = await fetchModulos(request, token);
      const created = modulos.find((m) => m?.nombreId === payload.nombreId || m?.nombre === payload.nombre);
      moduloId = created?.id;
    }

    expect(moduloId, 'No se pudo resolver el id del módulo temporal a inactivar').toBeTruthy();

    const deleteRes = await request.delete(`${BACKEND_URI}/api/v1/modulos/${moduloId}`, {
      headers: authHeaders(token),
    });

    expect([200, 202, 204]).toContain(deleteRes.status());

    const verifyRes = await request.get(`${BACKEND_URI}/api/v1/modulos/${moduloId}`, {
      headers: authHeaders(token),
    });

    expect([200, 404]).toContain(verifyRes.status());
    if (verifyRes.status() === 200) {
      const body = await verifyRes.json();
      if (body?.estadoId != null) {
        expect([0, 2, 'INACTIVO', 'Inactivo']).toContain(body.estadoId);
      }
      if (body?.estado != null) {
        expect(['Inactivo', 'INACTIVO', false]).toContain(body.estado);
      }
    }
  });
});
