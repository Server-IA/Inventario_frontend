/*=============================================================================
 Nombre del archivo : reportes.spec.js
 Descripcion        : Pruebas E2E para Módulo de Reporte Vencimiento de Producto (Búsqueda).
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |   Fecha    | Versión |      Autor           | Descripción del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2026-07-28 | 1.0.0   | Jeisson Sanchez      | Creación de E2E reportes    |
 +------------+---------+----------------------+-----------------------------+
=============================================================================*/
import { test, expect } from '@playwright/test';
import { authenticateByApi } from './helpers/e2e.shared.utils';

test.describe('E2E Reporte Vencimiento - Búsqueda', () => {

  test('Test Búsqueda de Reporte de Vencimiento de Producto', async ({ page, request }) => {
    await authenticateByApi(page, request, process.env.TEST_USERNAME, process.env.TEST_PASSWORD, 'RE_pv');
    await page.goto('/');

    // Verify the page loaded the correct report
    await expect(
      page.locator('h4', { hasText: /Product Expiration Report|Reporte de Vencimiento/i })
    ).toBeVisible({ timeout: 15000 });

    // Open the date picker button (shows the current date range)
    const dateBtn = page.locator('button').filter({ hasText: /\d{4}-\d{2}-\d{2}/ }).first();
    await dateBtn.click();

    const inputs = page.locator('.rdrDateInput input');
    if (await inputs.count() > 0) {
      await inputs.first().fill('01/01/2020');
      await inputs.first().press('Enter');
      await inputs.last().fill('31/12/2030');
      await inputs.last().press('Enter');
    }
    await page.keyboard.press('Escape');

    // Click SEARCH
    await page.locator('button', { hasText: /^SEARCH$|^Buscar$/i }).click();
    
    // Wait for datagrid to load or show no results
    await expect(page.locator('div[role="grid"], text=resultado(s) encontrado(s), text=No se encontraron productos')).toBeVisible({ timeout: 10000 });
  });

});
