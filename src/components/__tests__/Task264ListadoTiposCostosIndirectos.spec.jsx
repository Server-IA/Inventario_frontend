import React from "react";
import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen, waitFor } from "@testing-library/react";
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
    t: (key, fallback) => (typeof fallback === "string" ? fallback : key),
    i18n: { language: "es" },
  }),
}));

// Mock simple de MessageSnackBar y AppDataGrid para evitar la virtualización pesada de MUI X en test
vi.mock("../MessageSnackBar.jsx", () => ({
  default: ({ message = {} }) =>
    message.open ? (
      <div data-testid="mock-message-snackbar" role="alert">
        {message.text}
      </div>
    ) : null,
}));

vi.mock("../common/AppDataGrid.jsx", () => ({
  default: ({ rows = [], columns = [], loading = false }) => (
    <div data-testid="mock-app-data-grid">
      {loading ? (
        <span>Cargando datos...</span>
      ) : (
        <table>
          <thead>
            <tr>
              {columns.map((c) => (
                <th key={c.field}>{c.headerName}</th>
              ))}
            </tr>
          </thead>
          <tbody>
            {rows.map((row) => (
              <tr key={row.id}>
                <td>{row.nombre}</td>
                <td>{row.descripcion}</td>
                <td>{row.empresaNombre}</td>
                <td>{row.estadoNombre}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  ),
}));

import TipoCostoIndirecto from "../Costos/TipoCostoIndirecto/TipoCostoIndirecto";

describe("Task #264 - HU-044.2 Visualizar listado de tipos de costos indirectos", () => {
  const theme = createTheme({ palette: { mode: "light" } });

  const mockResponse = {
    header: {
      totalElements: 2,
      totalPages: 1,
      size: 10,
      number: 0,
      first: true,
      last: true,
    },
    data: [
      {
        id: 1,
        nombre: "Arrendamiento de Bodegas",
        descripcion: "Alquiler mensual de la bodega de almacenamiento principal",
        empresaId: 1505,
        empresaNombre: "Coagrohuila Central",
        estadoId: 1,
        estadoNombre: "Activo",
      },
      {
        id: 2,
        nombre: "Servicios Públicos",
        descripcion: "Energía eléctrica y acueducto",
        empresaId: 1505,
        empresaNombre: "Coagrohuila Central",
        estadoId: 2,
        estadoNombre: "Inactivo",
      },
    ],
  };

  beforeEach(() => {
    vi.clearAllMocks();
    axios.get.mockImplementation((url) => {
      if (url.includes("/v1/tipos-costos-indirectos")) {
        return Promise.resolve({ data: mockResponse });
      }
      return Promise.resolve({ data: [] });
    });
  });

  it("1. Llama a GET /v1/tipos-costos-indirectos y visualiza los registros retornados", async () => {
    axios.get.mockResolvedValueOnce({ data: mockResponse });

    render(
      <ThemeProvider theme={theme}>
        <TipoCostoIndirecto />
      </ThemeProvider>
    );

    await waitFor(() => {
      expect(axios.get).toHaveBeenCalledWith(
        "/v1/tipos-costos-indirectos",
        expect.objectContaining({
          params: expect.objectContaining({
            page: 0,
            size: 10,
            sortBy: "id,asc",
          }),
        })
      );
    });

    expect(screen.getByText("Arrendamiento de Bodegas")).toBeInTheDocument();
    expect(screen.getByText("Servicios Públicos")).toBeInTheDocument();
    expect(screen.getAllByText("Coagrohuila Central").length).toBe(2);
    expect(screen.getByText("Activo")).toBeInTheDocument();
    expect(screen.getByText("Inactivo")).toBeInTheDocument();
  });

  it("2. Maneja errores de backend mostrando la notificación correspondiente", async () => {
    axios.get.mockImplementation(() =>
      Promise.reject({
        response: {
          status: 403,
          data: { detail: "No tienes permisos de gestión de costos." },
        },
      })
    );

    render(
      <ThemeProvider theme={theme}>
        <TipoCostoIndirecto />
      </ThemeProvider>
    );

    expect(
      await screen.findByText(
        "No tienes permisos para realizar esta acción o el registro no pertenece a tu empresa."
      )
    ).toBeInTheDocument();
  });
});
