import React from "react";
import { describe, it, expect, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import { ThemeProvider, createTheme } from "@mui/material/styles";
import GridRol from "../Rol/GridRol";
import FormRol from "../Rol/FormRol";

describe("Issue #320 - Gestión de Rol diseño, contraste y formularios", () => {
  const lightTheme = createTheme({ palette: { mode: "light" } });
  const darkTheme = createTheme({ palette: { mode: "dark" } });

  const mockRows = [
    { id: 1, nombre: "ROLE_ADMIN", descripcion: "Administrador del sistema", estadoId: 1, estadoNombre: "Activo" },
    { id: 2, nombre: "ROLE_USER", descripcion: "Usuario estándar", estadoId: 2, estadoNombre: "Inactivo" },
  ];

  const mockEstados = [
    { id: 1, nombre: "Activo" },
    { id: 2, nombre: "Inactivo" },
  ];

  it("Criterio 1: en modo oscuro, la grilla no usa fondo blanco hardcodeado para mantener contraste legible", () => {
    const { container } = render(
      <ThemeProvider theme={darkTheme}>
        <GridRol rows={mockRows} loading={false} />
      </ThemeProvider>
    );

    const dataGridRoot = container.querySelector(".MuiDataGrid-root");
    expect(dataGridRoot).toBeInTheDocument();
    
    // El estilo en mergedSx no debe forzar bgcolor #fff en dark mode
    // En dark mode bodyBg = "#0f1b1a"
    expect(dataGridRoot).not.toHaveStyle("background-color: rgb(255, 255, 255)");
    expect(screen.getByText("ROLE_ADMIN")).toBeInTheDocument();
  });

  it("Criterio 2: la grilla no muestra la barra de herramientas adicional (COLUMNAS / DENSIDAD / EXPORTAR)", () => {
    render(
      <ThemeProvider theme={lightTheme}>
        <GridRol rows={mockRows} loading={false} />
      </ThemeProvider>
    );

    // GridToolbarContainer y sus botones no deben estar presentes
    expect(screen.queryByRole("button", { name: /columnas|columns/i })).not.toBeInTheDocument();
    expect(screen.queryByRole("button", { name: /densidad|density/i })).not.toBeInTheDocument();
    expect(screen.queryByRole("button", { name: /exportar|export/i })).not.toBeInTheDocument();
  });

  it("Criterio 3: en FormRol, los campos Nombre, Descripción y Estado comparten el mismo tamaño y estilo outlined coherente", () => {
    const setSelectedRow = vi.fn();
    const setMessage = vi.fn();
    const reloadData = vi.fn();
    const setOpen = vi.fn();

    render(
      <ThemeProvider theme={lightTheme}>
        <FormRol
          open={true}
          setOpen={setOpen}
          selectedRow={null}
          setSelectedRow={setSelectedRow}
          setMessage={setMessage}
          reloadData={reloadData}
          estados={mockEstados}
        />
      </ThemeProvider>
    );

    const dialog = screen.getByRole("dialog");
    expect(dialog).toBeInTheDocument();

    const inputs = dialog.querySelectorAll(".MuiInputBase-sizeSmall");
    expect(inputs.length).toBeGreaterThanOrEqual(3);

    expect(screen.getByLabelText(/^Nombre/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/^Descripción/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/^Estado/i)).toBeInTheDocument();
  });
});
