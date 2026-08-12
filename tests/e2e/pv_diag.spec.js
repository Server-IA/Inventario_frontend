/*=============================================================================
 Nombre del archivo : pv_diag.spec.js
 Descripcion        : Pruebas de diagnóstico E2E para interceptar payload de búsqueda PV.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |   Fecha    | Versión |      Autor           | Descripción del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2026-07-28 | 1.0.0   | Jeisson Sanchez      | Creación de prueba diag.    |
 +------------+---------+----------------------+-----------------------------+
=============================================================================*/
import { test, expect } from '@playwright/test';
import { authenticateByApi } from './helpers/e2e.shared.utils';

/**
 * Diagnostic test: Intercept the /buscar network request in RE_pv
 * to see the exact payload being sent and debug the 400 error.
 */
test('Diagnostico PV - Interceptar payload de buscar', async ({ page, request }) => {
  await authenticateByApi(page, request, 'u20211196841@usco.edu.co', 'Mivida1*', 'RE_pv');
  await page.goto('/');

  // Verify the page loaded
  await expect(
    page.locator('h4', { hasText: /Product Expiration Report|Reporte de Vencimiento/i })
  ).toBeVisible({ timeout: 15000 });

  // Intercept the buscar request to log payload and response
  let requestPayload = null;
  let responseBody = null;
  let responseStatus = null;

  page.on('request', req => {
    if (req.url().includes('vencimiento-producto/buscar')) {
      try {
        requestPayload = JSON.parse(req.postData() || '{}');
      } catch {
        requestPayload = req.postData();
      }
      console.log('=== REQUEST PAYLOAD ===');
      console.log(JSON.stringify(requestPayload, null, 2));
    }
  });

  page.on('response', async resp => {
    if (resp.url().includes('vencimiento-producto/buscar')) {
      responseStatus = resp.status();
      try {
        responseBody = await resp.text();
      } catch {
        responseBody = '(could not read body)';
      }
      console.log(`=== RESPONSE STATUS: ${responseStatus} ===`);
      console.log(responseBody?.slice(0, 500));
    }
  });

  // Take screenshot of the initial state
  await page.screenshot({ path: 'test-results/pv-diag-initial.png', fullPage: false });

  // Wait for filters to load (the seleccionInicial should auto-select Colombia → Huila → Neiva → Sede Neiva Primera)
  await page.waitForTimeout(3000);

  // Take screenshot after loading
  await page.screenshot({ path: 'test-results/pv-diag-after-load.png', fullPage: false });

  // Click SEARCH without changing anything
  await page.locator('button', { hasText: /^SEARCH$|^Buscar$/i }).click();
  
  // Wait for request to complete
  await page.waitForTimeout(3000);

  // Take screenshot after search
  await page.screenshot({ path: 'test-results/pv-diag-after-search.png', fullPage: false });

  console.log('=== FINAL STATE ===');
  console.log('Request Payload:', JSON.stringify(requestPayload, null, 2));
  console.log('Response Status:', responseStatus);
  console.log('Response Body:', responseBody?.slice(0, 500));

  // Expect something (not necessarily pass, just capture the state)
  expect(requestPayload).not.toBeNull();
});
