/* E2E HU-037.1 - obligatorios del registro de usuarios */
import { test, expect } from "@playwright/test";
import { authenticateByApi, openModuleScreen, requireEnv } from "./helpers/e2e.shared.utils";

const env = globalThis.process?.env ?? {};
const ADMIN_EMAIL = env.E2E_ADMIN_EMAIL;
const ADMIN_PASSWORD = env.E2E_ADMIN_PASSWORD;

const selectComboOption = async (page, dialog, labelRegex, optionTextRegex, nth = 0) => {
  const combo = dialog.getByLabel(labelRegex);
  await combo.click();
  const option = page.getByRole("option", { name: optionTextRegex }).nth(nth);
  await expect(option).toBeVisible();
  await option.click();
};

test("Registro bloquea sin tipo de documento, identificación y fecha inicio contractual", async ({ page, request }) => {
  requireEnv("E2E_ADMIN_EMAIL", ADMIN_EMAIL);
  requireEnv("E2E_ADMIN_PASSWORD", ADMIN_PASSWORD);
  const sentRequests = [];
  page.on("request", (req) => {
    if (/\/v1\/usuarios\/registro/.test(req.url())) sentRequests.push(req.method() + " " + req.url());
  });

  await authenticateByApi(page, request, ADMIN_EMAIL, ADMIN_PASSWORD, "Usuario");
  await openModuleScreen(page, "Usuario", /[Uu]suarios/i);

  await page.getByRole("button", { name: /^Agregar$/i }).click();
  const dialog = page.locator('[role="dialog"]:visible').last();
  await expect(dialog).toBeVisible({ timeout: 10000 });

  // Datos personales mínimos (los obligatorios NUEVOS quedan vacíos a propósito).
  await dialog.getByLabel(/username \(correo\)/i).fill("qa.pr101.noenviar@example.invalid");
  await dialog.getByLabel(/correo personal/i).fill("qa.pr101.noenviar@example.com");
  await dialog.getByLabel(/nombre/i).fill("QA");
  await dialog.getByLabel(/apellido/i).fill("Pr101");
  await selectComboOption(page, dialog, /estrato/i, /^\d+$/);

  // Asignación: empresa y rol, PERO sin fecha inicio (obligatorio nuevo).
  await selectComboOption(page, dialog, /^empresa(\s|\*)*$/i, /^coagrohuila$/i, 1);
  const rolCombo = dialog.getByLabel(/^rol(\s|\*)*$/i);
  await expect(rolCombo).toBeEnabled();
  await selectComboOption(page, dialog, /^rol(\s|\*)*$/i, /ROLE_ADMINISTRADOR_EMPRESA/i, 0);
  await dialog.getByRole("button", { name: /agregar asignación/i }).click();

  // El draft debe marcar la fecha inicio como obligatoria sin agregar la asignación.
  await expect(dialog.getByText(/debes seleccionar la fecha inicio contractual/i)).toBeVisible();

  // No se selecciona tipo de documento ni se ingresa identificación.
  await dialog.getByRole("button", { name: /^registrar$/i }).click();

  await expect(dialog.getByText(/debes seleccionar el tipo de documento/i)).toBeVisible();
  await expect(dialog.getByText(/debes ingresar el número de identificación/i)).toBeVisible();

  // No debe haberse enviado ninguna petición de registro.
  expect(sentRequests).toEqual([]);
});
