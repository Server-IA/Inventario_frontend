/*=============================================================================
 Nombre del archivo : reportes.spec.js
 Descripcion        : Pruebas E2E para Módulo de Reporte de Pedidos (Búsqueda).
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

test.describe('E2E Reporte Pedidos - Búsqueda', () => {

  test('Test Búsqueda de Reporte de Pedidos', async ({ page, request }) => {
    await authenticateByApi(page, request, 'u20211196841@usco.edu.co', 'Mivida1*', 'RE_pedido');
    await page.goto('/');

    // Verify the page loaded the correct report
    await expect(page.locator('h4', { hasText: /Order Report|Reporte de Pedido/i })).toBeVisible({ timeout: 15000 });

    // Open the date picker — the button shows the current date range e.g. "2026-07-27 - 2026-07-27"
    const dateBtn = page.locator('button').filter({ hasText: /\d{4}-\d{2}-\d{2}/ }).first();
    await dateBtn.click();

    // Fill the DateRange inputs with a wide range to capture any existing test data
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
    await page.waitForTimeout(3000);
  });

});
