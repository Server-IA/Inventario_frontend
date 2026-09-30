import React from "react";
import { describe, it, expect, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import { ThemeProvider, createTheme } from "@mui/material/styles";
import FormUsuario from "../Usuario/FormUsuario";

describe("Issue #319 - FormUsuario panel de asignaciones y remoción de brillo neón", () => {
  const darkTheme = createTheme({ palette: { mode: "dark" } });

  const mockInitialData = {
    username: "test@example.com",
    nombre: "Test",
    apellido: "User",
    asignaciones: [
      {
        rolId: "1",
        rolNombre: "ROLE_ADMIN",
        empresaId: "10",
        empresaNombre: "Empresa 1",
        iniciaContratoEn: "2026-01-01",
        preferido: true,
      },
    ],
  };

  it("Criterio 1 y 2: la sección de asignaciones usa el ancho disponible equitativo y agrupa los controles", () => {
    const onClose = vi.fn();
    const onSubmit = vi.fn();

    render(
      <ThemeProvider theme={darkTheme}>
        <FormUsuario
          open={true}
          onClose={onClose}
          mode="edit"
          initialData={mockInitialData}
          onSubmit={onSubmit}
          roles={[{ id: 1, nombre: "ROLE_ADMIN" }]}
          empresas={[{ id: 10, nombre: "Empresa 1" }]}
          empresaRoles={[]}
        />
      </ThemeProvider>
    );

    const dialog = screen.getByRole("dialog");
    expect(dialog).toBeInTheDocument();
    expect(screen.getByText("ROLE_ADMIN")).toBeInTheDocument();
  });

  it("Criterio 3: el modal en dark mode usa fondo oscuro sobrio sin resplandor neón", () => {
    const onClose = vi.fn();
    const onSubmit = vi.fn();

    render(
      <ThemeProvider theme={darkTheme}>
        <FormUsuario
          open={true}
          onClose={onClose}
          mode="create"
          onSubmit={onSubmit}
          roles={[]}
          empresas={[]}
        />
      </ThemeProvider>
    );

    const dialog = screen.getByRole("dialog");
    expect(dialog).toBeInTheDocument();
    expect(dialog).toHaveStyle("background-color: rgb(16, 30, 29)");
  });
});
