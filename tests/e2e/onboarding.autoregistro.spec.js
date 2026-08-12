import { test, expect } from '@playwright/test';
import {
  AUTOREGISTER_PASSWORD,
  BACKEND_URI,
  E2E_BASE_URL,
  ONBOARDING_STATE2_EMAIL,
  ONBOARDING_STATE2_PASSWORD,
  ONBOARDING_STATE3_EMAIL,
  ONBOARDING_STATE3_PASSWORD,
  goToRegisterFromHome,
  fillFieldByName,
  selectOptionByName,
  clickPrimaryButton,
  expectBaseMenuVisible,
  uniqueEmail,
  resolveVerificationLink,
  resolveVerificationToken,
  isMailhogAvailable,
  clearMailhogInbox,
  goToLoginFromHome,
  fillLoginForm,
  submitLogin,
} from './helpers/e2e.shared.utils';

function personaPayload() {
  const stamp = `${Date.now()}`;
  return {
    nombre: 'Maria',
    apellido: `Onboarding${stamp.slice(-4)}`,
    email: `persona.${stamp}@example.com`,
    identificacion: `10${stamp.slice(-8)}`,
    fechaNacimiento: '1998-05-20',
    direccion: 'Calle 1c #23-60',
    celular: `3${stamp.slice(-9)}`,
  };
}

function empresaPayload() {
  const stamp = `${Date.now()}`;
  return {
    nombre: `Empresa Onboarding ${stamp.slice(-5)}`,
    contacto: 'Carlos Perez',
    correo: `empresa.${stamp}@example.com`,
    celular: `3${stamp.slice(-9)}`,
    descripcion: 'Empresa creada por prueba E2E onboarding',
    identificacion: '900123456-7',
  };
}

async function completarFormularioPersona(page) {
  const data = personaPayload();

  await fillFieldByName(page, 'nombre', data.nombre);
  await fillFieldByName(page, 'apellido', data.apellido);
  await fillFieldByName(page, 'email', data.email);
  await selectOptionByName(page, 'tipoIdentificacion', /C[eé]dula|CC|Ciudadan/i);
  await fillFieldByName(page, 'identificacion', data.identificacion);
  await selectOptionByName(page, 'genero', /Masculino|Femenino/i);
  await fillFieldByName(page, 'fechaNacimiento', data.fechaNacimiento);
  await selectOptionByName(page, 'estrato', /^3$/);
  await fillFieldByName(page, 'direccion', data.direccion);
  await fillFieldByName(page, 'celular', data.celular);
  await clickPrimaryButton(page, /guardar/i);
}

async function completarFormularioEmpresa(page) {
  const data = empresaPayload();

  await fillFieldByName(page, 'nombre', data.nombre);
  await fillFieldByName(page, 'contacto', data.contacto);
  await fillFieldByName(page, 'correo', data.correo);
  await fillFieldByName(page, 'celular', data.celular);
  await fillFieldByName(page, 'descripcion', data.descripcion);
  await selectOptionByName(page, 'tipoIdentificacionId', /NIT/i);
  await fillFieldByName(page, 'identificacion', data.identificacion);
  await selectOptionByName(page, 'estadoId', /Activo/i);
  await clickPrimaryButton(page, /Guardar Empresa/i);
}

test.describe('Autorregistro + verificación + primer login', () => {
  test.beforeEach(async ({ request }) => {
    const available = await isMailhogAvailable(request);
    if (available) {
      await clearMailhogInbox(request);
    }
  });

  test.afterEach(async ({ request }) => {
    const available = await isMailhogAvailable(request);
    if (available) {
      await clearMailhogInbox(request);
    }
  });

  test('ONBA-01: flujo completo UI registro -> verify link -> persona -> empresa -> menú', async ({ page, request }) => {
    test.skip(!E2E_BASE_URL, 'Configura E2E_BASE_URL para flujo UI.');
    test.skip(!BACKEND_URI, 'Configura VITE_BACKEND_URI para fallback por token.');

    const email = uniqueEmail('e2e.autoreg.ui');

    await goToRegisterFromHome(page);
    await fillFieldByName(page, 'email', email);
    await fillFieldByName(page, 'password', AUTOREGISTER_PASSWORD);
    await clickPrimaryButton(page, /register|registrar/i);

    await expect(page.getByText(/Se ha enviado un email/i)).toBeVisible({ timeout: 15000 });

    let verificationLink = await resolveVerificationLink(request, email);
    if (!verificationLink) {
      const token = await resolveVerificationToken(request, email);
      if (token) verificationLink = `${BACKEND_URI}/auth/verify?token=${encodeURIComponent(token)}`;
    }

    test.skip(
      !verificationLink,
      'No se pudo obtener link de verificación automático. Integra MailHog (E2E_MAILHOG_API_URL) o provider de link/token.'
    );

    await page.goto(verificationLink);
    await expect(page.getByText(/verificad|exitos|correctamente|ya verificad/i).first()).toBeVisible({ timeout: 20000 });

    await goToLoginFromHome(page);
    await fillLoginForm(page, email, AUTOREGISTER_PASSWORD);
    await submitLogin(page);

    await expect(page.getByRole('heading', { name: /Formulario Persona/i })).toBeVisible({ timeout: 20000 });
    await completarFormularioPersona(page);

    await expect(page.getByRole('heading', { name: /Formulario Empresa/i })).toBeVisible({ timeout: 20000 });
    await completarFormularioEmpresa(page);

    await expectBaseMenuVisible(page);
  });

  test('ONBA-02: usuario semilla estado 2 va a persona y luego empresa', async ({ page }) => {
    test.skip(!E2E_BASE_URL, 'Configura E2E_BASE_URL para pruebas UI de onboarding.');
    test.skip(
      !ONBOARDING_STATE2_EMAIL || !ONBOARDING_STATE2_PASSWORD,
      'Configura E2E_ONBOARDING_STATE2_EMAIL y E2E_ONBOARDING_STATE2_PASSWORD.'
    );

    await goToLoginFromHome(page);
    await fillLoginForm(page, ONBOARDING_STATE2_EMAIL, ONBOARDING_STATE2_PASSWORD);
    await submitLogin(page);

    await expect(page.getByRole('heading', { name: /Formulario Persona/i })).toBeVisible({ timeout: 20000 });
    await completarFormularioPersona(page);

    await expect(page.getByRole('heading', { name: /Formulario Empresa/i })).toBeVisible({ timeout: 20000 });
    await completarFormularioEmpresa(page);

    await expectBaseMenuVisible(page);
  });

  test('ONBA-03: usuario semilla estado 3 va directo a empresa', async ({ page }) => {
    test.skip(!E2E_BASE_URL, 'Configura E2E_BASE_URL para pruebas UI de onboarding.');
    test.skip(
      !ONBOARDING_STATE3_EMAIL || !ONBOARDING_STATE3_PASSWORD,
      'Configura E2E_ONBOARDING_STATE3_EMAIL y E2E_ONBOARDING_STATE3_PASSWORD.'
    );

    await goToLoginFromHome(page);
    await fillLoginForm(page, ONBOARDING_STATE3_EMAIL, ONBOARDING_STATE3_PASSWORD);
    await submitLogin(page);

    await expect(page.getByRole('heading', { name: /Formulario Empresa/i })).toBeVisible({ timeout: 20000 });
    await completarFormularioEmpresa(page);
    await expectBaseMenuVisible(page);
  });
});
