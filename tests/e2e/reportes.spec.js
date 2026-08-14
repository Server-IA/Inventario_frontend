/*=============================================================================
 Nombre del archivo : reportes.spec.js
 Descripcion        : Pruebas E2E para M├│dulos de Reportes (Pedidos, PV, Kardex).
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |   Fecha    | Versi├│n |      Autor           | Descripci├│n del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2026-07-28 | 1.0.0   | Jeisson Sanchez      | Creaci├│n de E2E reportes    |
 +------------+---------+----------------------+-----------------------------+
=============================================================================*/
import { test, expect } from '@playwright/test';
import { authenticateByApi, ADMIN_EMAIL, ADMIN_PASSWORD } from './helpers/e2e.shared.utils';

/**
 * E2E Tests for the 3 Report modules (Pedidos, Vencimiento de Producto, Kardex).
 *
 * CONFIRMED FLOW (verified via screenshots):
 * 1. Page loads with the report module (via localStorage activeModule key injection).
 * 2. User sets date filters. We use defaults for robustness (no location filters since
 *    they require hierarchical selection: CountryÔåÆDeptÔåÆMunicipalityÔåÆSeat).
 * 3. Click "Buscar/SEARCH" ÔåÆ DataGrid shows results (or "No rows" if no data in test env).
 * 4. Click "Generar Reporte / GENERATE REPORT" ÔåÆ Opens a Dialog with PDF / Excel buttons.
 *    - Pedidos dialog title: "Generar Reporte"
 *    - Vencimiento dialog title: "Generate Expiration Report"
 *    - Kardex dialog title: "Generate Kardex Report"
 *
 * The E2E test verifies the complete UI flow up to and including the format selection modal.
 * (The download/preview step requires actual data in the test environment.)
 */


test.describe('E2E Reportes (Pedidos, Vencimiento, Kardex)', () => {

  // ÔöÇÔöÇÔöÇ TEST 1: Reporte de Pedidos ÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇ
  test('Test Reporte de Pedidos', async ({ page, request }) => {
    await authenticateByApi(page, request, ADMIN_EMAIL, ADMIN_PASSWORD, 'RE_pedido');
    await page.goto('/coagronet/');

    // Verify the page loaded the correct report
    await expect(page.locator('h4', { hasText: /Order Report|Reporte de Pedido/i })).toBeVisible({ timeout: 15000 });

    // Open the date picker — use the Calendar icon since the text can vary based on state/i18n
    const dateBtn = page.locator('button:has(svg[data-testid="CalendarTodayIcon"])').first();
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
    // Wait for datagrid to load or show no results
    await expect(page.locator('div[role="grid"]')).toBeVisible({ timeout: 10000 });

    // Click "Generar Reporte / GENERATE REPORT" → opens format selection Dialog
    await page.locator('button', { hasText: /GENERATE REPORT|Generar Reporte/i }).click();

    // Verify the format selection Dialog opened (Pedidos title: "Generar Reporte")
    await expect(page.locator('[role="dialog"]')).toBeVisible({ timeout: 10000 });
    await expect(page.locator('[role="dialog"] button:has-text("PDF")')).toBeVisible({ timeout: 5000 });
    await expect(page.locator('[role="dialog"] button:has-text("Excel")')).toBeVisible({ timeout: 5000 });
  });

  // ÔöÇÔöÇÔöÇ TEST 2: Reporte de Vencimiento de Producto ÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇ
  test('Test Reporte de Vencimiento de Producto', async ({ page, request }) => {
    await authenticateByApi(page, request, ADMIN_EMAIL, ADMIN_PASSWORD, 'RE_pv');
    await page.goto('/coagronet/');

    // Verify the page loaded the correct report
    await expect(
      page.locator('h4', { hasText: /Product Expiration Report|Reporte de Vencimiento/i })
    ).toBeVisible({ timeout: 15000 });

    // Open the date picker button (shows the current date range or 'Select dates')
    const dateBtn = page.locator('button:has(svg[data-testid="CalendarTodayIcon"])').first();
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
    await expect(page.locator('div[role="grid"]')).toBeVisible({ timeout: 10000 });

    // Click "GENERATE REPORT" ÔåÆ opens format selection Dialog
    // Confirmed dialog title: "Generate Expiration Report"
    await page.locator('button', { hasText: /GENERATE REPORT|Generar Reporte/i }).click();

    // Verify the format selection Dialog opened
    await expect(page.locator('[role="dialog"]')).toBeVisible({ timeout: 10000 });
    await expect(page.locator('[role="dialog"] button:has-text("PDF")')).toBeVisible({ timeout: 5000 });
    await expect(page.locator('[role="dialog"] button:has-text("EXCEL")')).toBeVisible({ timeout: 5000 });
  });

  // ÔöÇÔöÇÔöÇ TEST 3: Reporte Kardex ÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇ
  test('Test Reporte Kardex', async ({ page, request }) => {
    await authenticateByApi(page, request, ADMIN_EMAIL, ADMIN_PASSWORD, 'RE_kardex');
    await page.goto('/coagronet/');

    // Verify the page loaded the correct report
    await expect(
      page.locator('h4', { hasText: /Kardex Report|Reporte Kardex|Kardex Reports/i })
    ).toBeVisible({ timeout: 15000 });

    // Kardex uses standard datetime-local inputs directly on the page, not a popup date picker.
    // Fill the datetime-local inputs
    await page.locator('input[type="datetime-local"]').first().fill('2020-01-01T00:00');
    await page.locator('input[type="datetime-local"]').last().fill('2030-12-31T23:59');

    // Click SEARCH / Buscar
    await page.locator('button', { hasText: /^SEARCH$|^Buscar$/i }).click();
    // Kardex uses a plain <Table> (not MUI DataGrid) that only shows when there are results.
    // Wait for the snackbar message that always appears after search (success or empty).
    await expect(page.getByRole('alert').first()).toBeVisible({ timeout: 10000 });

    // Click Generate Report - Kardex directly generates PDF (no format selection dialog)
    await page.locator('button', { hasText: /GENERATE REPORT|Generar Reporte/i }).click();

    // Verify the PDF preview dialog opened (Kardex shows an iframe preview, not PDF/EXCEL buttons)
    await expect(page.locator('[role="dialog"]')).toBeVisible({ timeout: 10000 });
    // Kardex preview dialog has a close button and the title "Vista previa del Reporte"
    await expect(page.locator('[role="dialog"]')).toContainText(/Vista previa del Reporte|Preview/i);
  });

});
