import React from "react";
import { describe, expect, it, vi } from "vitest";
import { render } from "@testing-library/react";
import { ThemeProvider, createTheme } from "@mui/material/styles";
import { I18nextProvider } from "react-i18next";
import i18n from "../../i18n";
import UserStatusConfirmDialog from "../Usuario/UserStatusConfirmDialog";
import UserDetailDialog from "../Usuario/UserDetailDialog";
import FormUsuario from "../Usuario/FormUsuario";

const darkTheme = createTheme({ palette: { mode: "dark" } });

const renderInDark = (element) =>
  render(
    <I18nextProvider i18n={i18n}>
      <ThemeProvider theme={darkTheme}>{element}</ThemeProvider>
    </I18nextProvider>
  );

/** Las sombras del modo oscuro deben ser negras, nunca derivadas del texto claro. */
const expectNeutralShadow = (element) => {
  const shadow = getComputedStyle(element).boxShadow;
  const normalized = shadow.replace(/\s+/g, "").toLowerCase();

  expect(normalized).toContain("rgba(0,0,0");
  expect(normalized).not.toContain("231,246,247");
  expect(normalized).not.toContain("223,234,230");
};

const currentDialog = () => {
  const dialogs = [...document.querySelectorAll(".MuiDialog-paper")];
  return dialogs[dialogs.length - 1];
};

const LIGHT_SHADOW_LITERALS = ["231,246,247", "223,234,230"];

/** El acento superior tampoco puede usar el color de texto claro en oscuro. */
const expectNeutralBorder = (element) => {
  const border = getComputedStyle(element).borderTop.replace(/\s+/g, "").toLowerCase();
  LIGHT_SHADOW_LITERALS.forEach((literal) => expect(border).not.toContain(literal));
};

/** Reglas :hover/:active generadas para el diálogo que aún usen un color claro. */
const lightInteractiveShadowRules = () => {
  const rules = [];

  Array.from(document.styleSheets).forEach((sheet) => {
    let cssRules;
    try {
      cssRules = sheet.cssRules;
    } catch {
      return;
    }
    Array.from(cssRules).forEach((rule) => {
      const text = String(rule.cssText || "");
      if (!/(:hover|:active)/.test(text)) return;
      const shadow = /box-shadow:([^;}]*)/i.exec(text);
      if (!shadow) return;
      rules.push(shadow[1].replace(/\s+/g, "").toLowerCase());
    });
  });

  return {
    total: rules.length,
    light: rules.filter((rule) => LIGHT_SHADOW_LITERALS.some((lit) => rule.includes(lit))),
  };
};

const user = {
  id: "1",
  nombre: "Ana",
  apellido: "QA",
  username: "ana.qa@inmero.co",
  empresaNombre: "INMERO",
  rolPreferido: "Administrador",
  estadoNombre: "Activo",
};

describe("Modal de estado del usuario - superficies en modo oscuro", () => {
  it("usa sombras neutras en el diálogo y sus tarjetas", () => {
    renderInDark(
      <UserStatusConfirmDialog open user={user} onClose={vi.fn()} onConfirm={vi.fn()} />
    );

    const dialog = currentDialog();
    expectNeutralShadow(dialog);
    expectNeutralBorder(dialog.querySelector(".MuiDialogTitle-root"));

    const cards = [...document.querySelectorAll(".MuiCard-root")];
    expect(cards.length).toBeGreaterThan(0);
    cards.forEach(expectNeutralShadow);

    const interactive = lightInteractiveShadowRules();
    expect(interactive.total).toBeGreaterThan(0);
    expect(interactive.light).toEqual([]);
  });
});

describe("Detalle del usuario - superficies en modo oscuro", () => {
  it("usa sombras neutras en el diálogo y en las tarjetas de asignación", () => {
    renderInDark(
      <UserDetailDialog
        open
        data={{
          ...user,
          asignaciones: [
            {
              usuarioRolId: "10",
              empresaNombre: "INMERO",
              rolNombre: "Administrador",
              estadoNombre: "Activo",
            },
          ],
        }}
        onClose={vi.fn()}
      />
    );

    const dialog = currentDialog();
    expectNeutralShadow(dialog);
    expectNeutralBorder(dialog.querySelector(".MuiDialogTitle-root"));

    const cards = [...document.querySelectorAll(".MuiCard-root")];
    expect(cards.length).toBeGreaterThan(0);
    cards.forEach(expectNeutralShadow);
  });
});

describe("Formulario de usuario - superficies en modo oscuro", () => {
  it("usa acento y tarjetas neutras", () => {
    renderInDark(
      <FormUsuario
        open
        mode="create"
        onClose={vi.fn()}
        onSubmit={vi.fn()}
        initialData={{
          asignaciones: [
            {
              rolId: "1",
              rolNombre: "Administrador",
              empresaId: "1",
              empresaNombre: "INMERO",
            },
          ],
        }}
        roles={[{ id: 1, nombre: "Administrador" }]}
        empresas={[{ id: 1, nombre: "INMERO" }]}
        empresaRoles={[]}
        tiposIdentificacion={[{ id: 1, nombre: "Cédula" }]}
        isAdmin
        sessionCompanyId="1"
        sessionCompanyName="INMERO"
      />
    );

    const dialog = currentDialog();
    expectNeutralBorder(dialog.querySelector(".MuiDialogTitle-root"));

    const cards = [...document.querySelectorAll(".MuiCard-root")];
    expect(cards.length).toBeGreaterThan(0);
    cards.forEach(expectNeutralShadow);
  });
});
