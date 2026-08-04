/*=============================================================================
 Nombre del archivo : reportes.spec.js
 Descripcion        : Pruebas E2E para Módulo de Reporte Kardex (Búsqueda y Previsualización).
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

test.describe('E2E Reporte Kardex - Búsqueda', () => {

  test('Test Búsqueda de Reporte Kardex', async ({ page, request }) => {
    await authenticateByApi(page, request, 'u20211196841@usco.edu.co', 'Mivida1*', 'RE_kardex');
    await page.goto('/');

    // Verify the page loaded the correct report
    await expect(
      page.locator('h4', { hasText: /Kardex Report|Reporte Kardex|Kardex Reports/i })
    ).toBeVisible({ timeout: 15000 });

    // Open the date picker (Kardex has a button with date range text)
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

    // Click SEARCH / Buscar
    await page.locator('button', { hasText: /^SEARCH$|^Buscar$/i }).click();
    await page.waitForTimeout(3000);
  });

});
