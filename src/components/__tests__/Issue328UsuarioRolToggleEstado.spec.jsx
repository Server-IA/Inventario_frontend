import React from "react";
import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { ThemeProvider, createTheme } from "@mui/material/styles";
import axios from "../axiosConfig";

vi.mock("../axiosConfig", () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    patch: vi.fn(),
    delete: vi.fn(),
  },
}));

// Mock simple de GridUsuarioRoles para ejecutar selección y acciones sin virtualización pesada de DataGrid
vi.mock("../UsuarioRoles/GridUsuarioRoles.jsx", () => ({
  default: ({ rows = [], setSelectedRow }) => (
    <div data-testid="mock-grid-usuario-roles">
      {rows.map((row) => (
        <button
          key={row.id}
          data-testid={`select-row-${row.id}`}
          onClick={() => setSelectedRow(row)}
        >
          {`Row ${row.id} - ${row.estadoId === 1 ? "Activo" : "Inactivo"}`}
        </button>
      ))}
    </div>
  ),
}));

vi.mock("../UsuarioRoles/FormUsuarioRoles.jsx", () => ({
  default: () => <div data-testid="mock-form-usuario-roles" />,
}));

import UsuarioRoles from "../UsuarioRoles/UsuarioRoles";

describe("Issue #328 - Usuario-Rol Toggle de Estado Contractual", () => {
  const theme = createTheme({ palette: { mode: "light" } });

  const mockRows = [
    {
      id: 15,
      usuarioId: 101,
      empresaId: 1505,
      rolId: 3,
      estadoId: 1, // Activo
    },
    {
      id: 16,
      usuarioId: 102,
      empresaId: 1505,
      rolId: 4,
      estadoId: 2, // Inactivo
    },
  ];

  const mockEstados = [
    { id: 1, nombre: "Activo" },
    { id: 2, nombre: "Inactivo" },
  ];

  beforeEach(() => {
    vi.clearAllMocks();
    window.confirm = vi.fn(() => true);
    window.alert = vi.fn();

    axios.get.mockImplementation((url) => {
      if (url.includes("/v1/system/usuario-roles")) {
        return Promise.resolve({ data: mockRows });
      }
      if (url.includes("/v1/items/estado/0")) {
        return Promise.resolve({ data: mockEstados });
      }
      return Promise.resolve({ data: [] });
    });

    axios.patch.mockResolvedValue({ data: { message: "Estado actualizado exitosamente" } });
  });

  it("1. Llama a PATCH /v1/system/usuario-roles/{id}/toggle-estado con el parámetro empresaId obligatorio", async () => {
    render(
      <ThemeProvider theme={theme}>
        <UsuarioRoles />
      </ThemeProvider>
    );

    // Esperar a que carguen los datos
    await waitFor(() => {
      expect(axios.get).toHaveBeenCalledWith(
        expect.stringContaining("/v1/system/usuario-roles"),
        expect.anything()
      );
    });

    // Seleccionar fila activa (id 15, empresaId 1505)
    const selectActiveBtn = screen.getByTestId("select-row-15");
    fireEvent.click(selectActiveBtn);

    // El botón debe mostrar 'Inactivar'
    const toggleBtn = screen.getByRole("button", { name: /inactivar/i });
    expect(toggleBtn).toBeInTheDocument();

    fireEvent.click(toggleBtn);

    await waitFor(() => {
      expect(axios.patch).toHaveBeenCalledWith(
        "/v1/system/usuario-roles/15/toggle-estado",
        null,
        expect.objectContaining({
          params: { empresaId: 1505 },
        })
      );
    });

    // Ausencia total de llamadas a DELETE y PUT
    expect(axios.delete).not.toHaveBeenCalled();
    expect(axios.put).not.toHaveBeenCalled();
  });

  it("2. Cambia el botón a 'Activar' para una asignación inactiva y ejecuta toggle exitosamente", async () => {
    render(
      <ThemeProvider theme={theme}>
        <UsuarioRoles />
      </ThemeProvider>
    );

    await waitFor(() => {
      expect(axios.get).toHaveBeenCalled();
    });

    // Seleccionar fila inactiva (id 16, empresaId 1505)
    const selectInactiveBtn = screen.getByTestId("select-row-16");
    fireEvent.click(selectInactiveBtn);

    // El botón debe mostrar 'Activar'
    const activateBtn = screen.getByRole("button", { name: /activar/i });
    expect(activateBtn).toBeInTheDocument();

    fireEvent.click(activateBtn);

    await waitFor(() => {
      expect(axios.patch).toHaveBeenCalledWith(
        "/v1/system/usuario-roles/16/toggle-estado",
        null,
        expect.objectContaining({
          params: { empresaId: 1505 },
        })
      );
    });

    // Actualiza la grilla desde el backend
    expect(axios.get).toHaveBeenCalledTimes(6); // 4 iniciales + 1 recarga en test 1 + 1 en test 2
  });

  it("3. Ante error de backend (400, 403, 404, 409), muestra el mensaje de error y no ejecuta DELETE", async () => {
    axios.patch.mockRejectedValueOnce({
      response: {
        status: 409,
        data: { message: "No se puede inactivar la asignación porque tiene contratos en curso." },
      },
    });

    render(
      <ThemeProvider theme={theme}>
        <UsuarioRoles />
      </ThemeProvider>
    );

    await waitFor(() => {
      expect(axios.get).toHaveBeenCalled();
    });

    const selectActiveBtn = screen.getByTestId("select-row-15");
    fireEvent.click(selectActiveBtn);

    const toggleBtn = screen.getByRole("button", { name: /inactivar/i });
    fireEvent.click(toggleBtn);

    await waitFor(() => {
      expect(axios.patch).toHaveBeenCalled();
    });

    // Verifica que se muestre el mensaje retornado por el backend
    expect(window.alert).toHaveBeenCalledWith(
      "No se puede inactivar la asignación porque tiene contratos en curso."
    );

    // Asegura que no se ejecute DELETE como fallback erróneo
    expect(axios.delete).not.toHaveBeenCalled();
  });
});
