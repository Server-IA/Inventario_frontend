/*=============================================================================
 Nombre del archivo : reportes.spec.js
 Descripcion        : Pruebas E2E para Módulos de Reportes (Pedidos, PV, Kardex).
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

/**
 * E2E Tests for the 3 Report modules (Pedidos, Vencimiento de Producto, Kardex).
 *
 * CONFIRMED FLOW (verified via screenshots):
 * 1. Page loads with the report module (via localStorage activeModule key injection).
 * 2. User sets date filters. We use defaults for robustness (no location filters since
 *    they require hierarchical selection: Country→Dept→Municipality→Seat).
 * 3. Click "Buscar/SEARCH" → DataGrid shows results (or "No rows" if no data in test env).
 * 4. Click "Generar Reporte / GENERATE REPORT" → Opens a Dialog with PDF / Excel buttons.
 *    - Pedidos dialog title: "Generar Reporte"
 *    - Vencimiento dialog title: "Generate Expiration Report"
 *    - Kardex dialog title: "Generate Kardex Report"
 *
 * The E2E test verifies the complete UI flow up to and including the format selection modal.
 * (The download/preview step requires actual data in the test environment.)
 */

test.describe('E2E Reportes (Pedidos, Vencimiento, Kardex)', () => {

  // ─── TEST 1: Reporte de Pedidos ──────────────────────────────────────────────
  test('Test Reporte de Pedidos', async ({ page, request }) => {
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

    // Click "Generar Reporte / GENERATE REPORT" → opens format selection Dialog
    await page.locator('button', { hasText: /GENERATE REPORT|Generar Reporte/i }).click();

    // Verify the format selection Dialog opened (Pedidos title: "Generar Reporte")
    await expect(page.locator('[role="dialog"]')).toBeVisible({ timeout: 10000 });
    await expect(page.locator('[role="dialog"] button:has-text("PDF")')).toBeVisible({ timeout: 5000 });
    await expect(page.locator('[role="dialog"] button:has-text("Excel")')).toBeVisible({ timeout: 5000 });
  });

  // ─── TEST 2: Reporte de Vencimiento de Producto ──────────────────────────────
  test('Test Reporte de Vencimiento de Producto', async ({ page, request }) => {
    await authenticateByApi(page, request, 'u20211196841@usco.edu.co', 'Mivida1*', 'RE_pv');
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
    await page.waitForTimeout(3000);

    // Click "GENERATE REPORT" → opens format selection Dialog  
    // Confirmed dialog title: "Generate Expiration Report"
    await page.locator('button', { hasText: /GENERATE REPORT|Generar Reporte/i }).click();

    // Verify the format selection Dialog opened
    await expect(page.locator('[role="dialog"]')).toBeVisible({ timeout: 10000 });
    await expect(page.locator('[role="dialog"] button:has-text("PDF")')).toBeVisible({ timeout: 5000 });
    await expect(page.locator('[role="dialog"] button:has-text("EXCEL")')).toBeVisible({ timeout: 5000 });
  });

  // ─── TEST 3: Reporte Kardex ──────────────────────────────────────────────────
  test('Test Reporte Kardex', async ({ page, request }) => {
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

    // Click Generate Report
    // Confirmed dialog title: "Generate Kardex Report"
    await page.locator('button', { hasText: /GENERATE REPORT|Generar Reporte/i }).click();

    // Verify the format selection Dialog opened
    await expect(page.locator('[role="dialog"]')).toBeVisible({ timeout: 10000 });
    await expect(page.locator('[role="dialog"] button:has-text("PDF")')).toBeVisible({ timeout: 5000 });
    await expect(page.locator('[role="dialog"] button:has-text("EXCEL")')).toBeVisible({ timeout: 5000 });
  });

});
