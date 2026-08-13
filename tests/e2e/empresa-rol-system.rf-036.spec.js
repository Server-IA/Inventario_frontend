import { test, expect } from '@playwright/test';
import {
  loginAsSystemAdmin,
  loginAsAdminGetToken,
  openModuleScreen,
  clickActionButton,
  getActiveDialog,
  clickDialogButton,
  expectSnackMessage,
  findGridCellInColumnAcrossPages,
  authHeaders,
  BACKEND_URI,
} from './helpers/e2e.shared.utils';

function buildUniqueRoleName() {
  const rand = Array.from(
    { length: 8 },
    () => String.fromCharCode(65 + Math.floor(Math.random() * 26))
  ).join('');
  return `ROLE_QA${rand}`;
}

async function createRoleByApi(request) {
  const token = await loginAsAdminGetToken(request);
  const nombre = buildUniqueRoleName();
  const res = await request.post(`${BACKEND_URI}/api/v1/roles`, {
    headers: authHeaders(token),
    data: { nombre, descripcion: 'Rol E2E para empresa-rol', estadoId: 1 },
  });
  expect([200, 201]).toContain(res.status());

  const catRes = await request.get(`${BACKEND_URI}/api/v1/items/rol/0`, { headers: authHeaders(token) });
  const cat = await catRes.json();
  const list = Array.isArray(cat) ? cat : cat?.content ?? [];
  const found = list.find((r) => (r.name ?? r.nombre ?? r.rolNombre) === nombre);
  return { nombre, id: found?.id ?? null };
}

async function createEmpresaRolByApi(request, rolId, empresaId) {
  const token = await loginAsAdminGetToken(request);
  const res = await request.post(`${BACKEND_URI}/api/v1/system/empresa-rol`, {
    headers: authHeaders(token),
    data: { empresaId, rolId },
  });
  return res.status();
}

async function deleteRoleByApi(request, id) {
  if (id == null) return;
  const token = await loginAsAdminGetToken(request);
  await request.delete(`${BACKEND_URI}/api/v1/roles/${id}`, { headers: authHeaders(token) });
}

async function waitForGridRowsLoaded(page, minRows = 1, timeout = 12000) {
  await expect
    .poll(async () => page.locator('[role="row"][data-id]').count(), {
      timeout,
      message: 'No se cargaron filas en la tabla dentro del tiempo esperado',
    })
    .toBeGreaterThanOrEqual(minRows);
}

async function getRowByRolNombre(page, rolNombre) {
  const cell = await findGridCellInColumnAcrossPages(page, 'Rol', rolNombre, {
    timeout: 15000,
    maxPages: 80,
  });
  return cell.locator('xpath=ancestor::*[@role="row" and @data-id]').first();
}

async function deleteEmpresaRolByRolNombre(page, rolNombre, maxAttempts = 3) {
  for (let attempt = 1; attempt <= maxAttempts; attempt += 1) {
    const row = await getRowByRolNombre(page, rolNombre);
    const rowId = await row.getAttribute('data-id');
    await row.click();
    await page.waitForTimeout(300);

    const deleteButton = page.getByRole('button', { name: /^ELIMINAR$/i }).first();
    if (!(await deleteButton.isEnabled().catch(() => false))) {
      continue;
    }
    await deleteButton.click();

    const deleteResponsePromise = page.waitForResponse(
      (res) =>
        res.url().includes(`/empresa-rol/${rowId}`) &&
        res.request().method() === 'DELETE'
    );

    const dialog = page.locator('[role="dialog"]:visible').last();
    await dialog.getByRole('button', { name: /^Eliminar$/i }).click();

    const deleteResponse = await deleteResponsePromise;
    return { rowId, status: deleteResponse.status() };
  }

  throw new Error(`No se pudo eliminar la relación con rol "${rolNombre}".`);
}

test.describe('RF-036 - Empresa Rol (admin sistema) casos positivos', () => {
  test.describe.configure({ mode: 'serial' });

  let uniqueRoleName = null;
  let uniqueRoleId = null;

  test.beforeAll(async ({ request }) => {
    const created = await createRoleByApi(request);
    uniqueRoleName = created.nombre;
    uniqueRoleId = created.id;
    expect(uniqueRoleId, 'No se pudo resolver el id del rol creado').toBeTruthy();
    await createEmpresaRolByApi(request, uniqueRoleId, 1505);
  });

  test.afterAll(async ({ request }) => {
    await deleteRoleByApi(request, uniqueRoleId);
  });

  test.beforeEach(async ({ page, request }) => {
    await loginAsSystemAdmin(page, request, 'EmpresaRol');
    await openModuleScreen(page, 'EmpresaRol', /Roles de Empresa/i);
  });

  test('ERS-01: visualizar listado con empresa, rol y estado', async ({ page }) => {
    await expect(page.getByRole('columnheader', { name: 'Empresa' })).toBeVisible();
    await expect(page.getByRole('columnheader', { name: 'Rol' })).toBeVisible();
    await expect(page.getByRole('columnheader', { name: 'Estado' })).toBeVisible();
    await waitForGridRowsLoaded(page);
  });

  test('ERS-02: la relación creada aparece en el listado', async ({ page }) => {
    expect(uniqueRoleName, 'beforeAll debió crear el rol de prueba').toBeTruthy();
    await waitForGridRowsLoaded(page);

    const cell = await findGridCellInColumnAcrossPages(page, 'Rol', uniqueRoleName, {
      timeout: 15000,
      maxPages: 80,
    });
    await expect(cell).toBeVisible();
  });

  test('ERS-03: actualizar la relación creada', async ({ page }) => {
    expect(uniqueRoleName, 'beforeAll debió crear el rol de prueba').toBeTruthy();
    await waitForGridRowsLoaded(page);

    const row = await getRowByRolNombre(page, uniqueRoleName);
    await row.click();
    await clickActionButton(page, 'ACTUALIZAR');

    const dialog = await getActiveDialog(page);
    await expect(dialog.getByText(/Editar Rol y Permisos/i)).toBeVisible();
    await expect(dialog.getByRole('button', { name: /^Guardar$/i })).toBeVisible();
    await clickDialogButton(page, 'Cerrar');
  });

  test('ERS-04: eliminar la relación creada', async ({ page }) => {
    expect(uniqueRoleName, 'beforeAll debió crear el rol de prueba').toBeTruthy();
    await waitForGridRowsLoaded(page);

    const { status } = await deleteEmpresaRolByRolNombre(page, uniqueRoleName);
    expect([200, 202, 204]).toContain(status);
    await expectSnackMessage(page, /eliminados correctamente|eliminado|eliminar|no se puede eliminar|asociad/i);

    await expect
      .poll(
        async () => {
          try {
            await findGridCellInColumnAcrossPages(page, 'Rol', uniqueRoleName, {
              timeout: 3000,
              maxPages: 80,
            });
            return false;
          } catch {
            return true;
          }
        },
        { timeout: 15000, message: `La relación con rol "${uniqueRoleName}" sigue visible tras eliminar` }
      )
      .toBe(true);
  });
});
