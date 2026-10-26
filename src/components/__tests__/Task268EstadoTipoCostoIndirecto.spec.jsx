import React from "react";
import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { ThemeProvider, createTheme } from "@mui/material/styles";
import axios from "../axiosConfig";

vi.mock("../axiosConfig", () => ({
  default: {
    get: vi.fn(() => Promise.resolve({ data: { header: {}, data: [] } })),
    post: vi.fn(() => Promise.resolve({ data: {} })),
    put: vi.fn(() => Promise.resolve({ data: {} })),
    patch: vi.fn(() => Promise.resolve({ data: {} })),
    delete: vi.fn(() => Promise.resolve({ data: {} })),
  },
}));

vi.mock("react-i18next", () => ({
  useTranslation: () => ({
    t: (key, options) => {
      if (typeof options === "object" && options?.defaultValue) return options.defaultValue;
      if (typeof options === "string") return options;
      return key;
    },
    i18n: { language: "es" },
  }),
}));

vi.mock("../MessageSnackBar.jsx", () => ({
  default: ({ message = {} }) =>
    message.open ? (
      <div data-testid="mock-message-snackbar" role="alert">
        {message.text}
      </div>
    ) : null,
}));

// Mock simple de AppDataGrid con botón para seleccionar la fila
vi.mock("../common/AppDataGrid.jsx", () => ({
  default: ({ rows = [], setSelectedRow }) => (
    <div data-testid="mock-app-data-grid">
      {rows.map((row) => (
        <button
          key={row.id}
          data-testid={`select-row-${row.id}`}
          onClick={() => setSelectedRow(row)}
        >
          {`Row ${row.id} - ${row.nombre} (${row.estadoNombre})`}
        </button>
      ))}
    </div>
  ),
}));

vi.mock("../Costos/TipoCostoIndirecto/FormTipoCostoIndirecto.jsx", () => ({
  default: () => <div data-testid="mock-form-tipo-costo-indirecto" />,
}));

import TipoCostoIndirecto from "../Costos/TipoCostoIndirecto/TipoCostoIndirecto";

describe("Task #268 - HU-044.4 Activar/Inactivar tipo de costo indirecto", () => {
  const theme = createTheme({ palette: { mode: "light" } });

  const mockRows = [
    {
      id: 1,
      nombre: "Arrendamiento de Bodegas",
      descripcion: "Alquiler mensual",
      empresaId: 1505,
      empresaNombre: "Coagrohuila Central",
      estadoId: 1,
      estadoNombre: "Activo",
    },
    {
      id: 2,
      nombre: "Mano de Obra Indirecta",
      descripcion: "Servicios temporales",
      empresaId: 1505,
      empresaNombre: "Coagrohuila Central",
      estadoId: 2,
      estadoNombre: "Inactivo",
    },
  ];

  beforeEach(() => {
    vi.clearAllMocks();
    axios.get.mockImplementation((url) => {
      if (url.includes("/v1/tipos-costos-indirectos")) {
        return Promise.resolve({
          data: {
            header: { totalElements: 2, totalPages: 1 },
            data: mockRows,
          },
        });
      }
      return Promise.resolve({ data: [] });
    });
  });

  it("1. Inactiva un tipo activo enviando PATCH /v1/tipos-costos-indirectos/{id}/estado con { activo: false }", async () => {
    axios.patch.mockResolvedValueOnce({
      data: {
        id: 1,
        nombre: "Arrendamiento de Bodegas",
        estadoId: 2,
        estadoNombre: "Inactivo",
        activo: false,
        cambioAplicado: true,
        mensaje: "Estado actualizado exitosamente",
      },
    });

    render(
      <ThemeProvider theme={theme}>
        <TipoCostoIndirecto />
      </ThemeProvider>
    );

    // Seleccionar fila 1 (Activa)
    const row1Btn = await screen.findByTestId("select-row-1");
    fireEvent.click(row1Btn);

    // El botón de acción debe decir "common.actions.inactivate" o Inactivar
    const deleteBtn = screen.getByRole("button", { name: /inactivar|delete/i });
    fireEvent.click(deleteBtn);

    // Debe abrirse el diálogo de confirmación
    const confirmBtn = screen.getByRole("button", { name: /inactivar/i });
    fireEvent.click(confirmBtn);

    await waitFor(() => {
      expect(axios.patch).toHaveBeenCalledWith(
        "/v1/tipos-costos-indirectos/1/estado",
        { activo: false }
      );
    });
  });

  it("2. Activa un tipo inactivo enviando PATCH /v1/tipos-costos-indirectos/{id}/estado con { activo: true }", async () => {
    axios.patch.mockResolvedValueOnce({
      data: {
        id: 2,
        nombre: "Mano de Obra Indirecta",
        estadoId: 1,
        estadoNombre: "Activo",
        activo: true,
        cambioAplicado: true,
        mensaje: "Estado actualizado exitosamente",
      },
    });

    render(
      <ThemeProvider theme={theme}>
        <TipoCostoIndirecto />
      </ThemeProvider>
    );

    // Seleccionar fila 2 (Inactiva)
    const row2Btn = await screen.findByTestId("select-row-2");
    fireEvent.click(row2Btn);

    // El botón de acción debe decir "Activar"
    const activateBtn = screen.getByRole("button", { name: /activar/i });
    fireEvent.click(activateBtn);

    // Confirmar activación en el modal
    const dialogConfirmBtn = screen.getByRole("button", { name: /activar/i });
    fireEvent.click(dialogConfirmBtn);

    await waitFor(() => {
      expect(axios.patch).toHaveBeenCalledWith(
        "/v1/tipos-costos-indirectos/2/estado",
        { activo: true }
      );
    });
  });

  it("3. Maneja error 409 cuando el tipo tiene costos indirectos activos asociados", async () => {
    axios.patch.mockRejectedValueOnce({
      response: {
        status: 409,
        data: {
          detail: "El tipo tiene costos indirectos activos asociados.",
        },
      },
    });

    render(
      <ThemeProvider theme={theme}>
        <TipoCostoIndirecto />
      </ThemeProvider>
    );

    const row1Btn = await screen.findByTestId("select-row-1");
    fireEvent.click(row1Btn);

    const deleteBtn = screen.getByRole("button", { name: /inactivar|delete/i });
    fireEvent.click(deleteBtn);

    const confirmBtn = screen.getByRole("button", { name: /inactivar/i });
    fireEvent.click(confirmBtn);

    await waitFor(() => {
      expect(axios.patch).toHaveBeenCalled();
    });

    expect(
      await screen.findByText(/El tipo tiene costos indirectos activos asociados/i)
    ).toBeInTheDocument();
  });
});
