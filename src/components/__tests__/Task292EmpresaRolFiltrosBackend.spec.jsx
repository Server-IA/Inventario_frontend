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

const { translate } = vi.hoisted(() => ({
  translate: (key, fallback) => (typeof fallback === "string" ? fallback : key),
}));

vi.mock("react-i18next", () => ({
  useTranslation: () => ({
    t: translate,
    i18n: { language: "es" },
  }),
}));

vi.mock("../common/AppDataGrid.jsx", () => ({
  default: ({ rows = [] }) => (
    <div data-testid="mock-app-data-grid">
      {rows.map((row) => (
        <div key={row.id} data-testid={`empresa-rol-row-${row.id}`}>
          <span>{row.rolNombre}</span>
          <span>{row.empresaNombre}</span>
          <span>{row.estadoNombre}</span>
        </div>
      ))}
    </div>
  ),
}));

vi.mock("../EmpresaRol/FormEmpresaRol.jsx", () => ({
  default: ({ reloadData }) => (
    <button type="button" data-testid="form-reload-data" onClick={() => reloadData()}>
      Recargar desde formulario
    </button>
  ),
}));

vi.mock("../EmpresaRol/ModalVerPermisos.jsx", () => ({
  default: () => <div data-testid="mock-modal-ver-permisos" />,
}));

// Mock de CrudFilterModal para evitar renderizado y bucles de MUI Dialog en test
vi.mock("../common/CrudFilterModal.jsx", () => ({
  default: ({ open, fields = [], values = {}, onChange, onApply, onClear }) => {
    if (!open) return null;
    return (
      <div data-testid="mock-crud-filter-modal">
        {fields.map((f) => (
          <input
            key={f.name}
            data-testid={`filter-field-${f.name}`}
            value={values[f.name] || ""}
            onChange={(e) => onChange({ name: f.name, value: e.target.value })}
          />
        ))}
        <button data-testid="apply-filters-btn" onClick={onApply}>
          Aplicar
        </button>
        <button data-testid="clear-filters-btn" onClick={onClear}>
          Limpiar
        </button>
      </div>
    );
  },
}));

import EmpresaRol from "../EmpresaRol/EmpresaRol";

describe("Task #292 / HU-036.6 - Filtrar listado de roles por nombre y estado en backend", () => {
  const theme = createTheme({ palette: { mode: "light" } });

  const mockEmpresaRoles = [
    {
      id: 101,
      rolNombre: "ROLE_ADMINISTRADOR_EMPRESA",
      empresaId: 1505,
      empresaNombre: "Coagrohuila Central",
      estadoId: 1,
      estadoNombre: "Activo",
    },
    {
      id: 102,
      rolNombre: "ROLE_TECNICO_MANTENIMIENTO",
      empresaId: 1505,
      empresaNombre: "Coagrohuila Central",
      estadoId: 2,
      estadoNombre: "Inactivo",
    },
  ];

  beforeEach(() => {
    vi.clearAllMocks();
    localStorage.setItem("empresaId", "1505");
    localStorage.setItem("rolNombre", "ROLE_ADMINISTRADOR_EMPRESA");
    localStorage.setItem(
      "rolesByCompany",
      JSON.stringify([
        { empresaId: 1505, rolId: 2, rolNombre: "ROLE_ADMINISTRADOR_EMPRESA" },
      ])
    );

    axios.get.mockImplementation((url) => {
      if (url.includes("/v1/empresa-rol") || url.includes("/v1/system/empresa-rol")) {
        return Promise.resolve({ data: mockEmpresaRoles });
      }
      if (url.includes("/v1/items/rol/0")) {
        return Promise.resolve({
          data: [
            { id: 1, name: "ROLE_ADMINISTRADOR_EMPRESA" },
            { id: 2, name: "ROLE_TECNICO_MANTENIMIENTO" },
          ],
        });
      }
      if (url.includes("/v1/items/empresa/0")) {
        return Promise.resolve({ data: [{ id: 1505, nombre: "Coagrohuila Central" }] });
      }
      if (url.includes("/permisos")) {
        return Promise.resolve({ data: [] });
      }
      return Promise.resolve({ data: [] });
    });
  });

  it("1. Consulta inicial sin filtros hacia GET /v1/empresa-rol", async () => {
    render(
      <ThemeProvider theme={theme}>
        <EmpresaRol />
      </ThemeProvider>
    );

    await waitFor(() => {
      expect(axios.get).toHaveBeenCalledWith(
        "/v1/empresa-rol",
        expect.objectContaining({ params: {} })
      );
    });

    expect(await screen.findByText("ROLE_ADMINISTRADOR_EMPRESA")).toBeInTheDocument();
    expect(screen.getByText("ROLE_TECNICO_MANTENIMIENTO")).toBeInTheDocument();
  });

  it("2. Al aplicar filtros, solicita al backend con query params nombre y estadoId", async () => {
    render(
      <ThemeProvider theme={theme}>
        <EmpresaRol />
      </ThemeProvider>
    );

    await waitFor(() => {
      expect(axios.get).toHaveBeenCalled();
    });

    // Abrir modal de filtros
    const filtersBtn = screen.getByRole("button", { name: "common.actions.filters" });
    fireEvent.click(filtersBtn);

    // Escribir en el campo 'nombre'
    const nameInput = await screen.findByTestId("filter-field-nombre");
    fireEvent.change(nameInput, { target: { value: "Técnico" } });

    // Modificar estado
    const estadoInput = screen.getByTestId("filter-field-estadoId");
    fireEvent.change(estadoInput, { target: { value: "2" } });

    // Aplicar filtros
    const applyBtn = screen.getByTestId("apply-filters-btn");
    fireEvent.click(applyBtn);

    await waitFor(() => {
      expect(axios.get).toHaveBeenCalledWith(
        "/v1/empresa-rol",
        expect.objectContaining({
          params: expect.objectContaining({
            nombre: "Técnico",
            estadoId: 2,
          }),
        })
      );
    });
  });

  it("3. Las recargas posteriores del formulario conservan los filtros aplicados", async () => {
    render(
      <ThemeProvider theme={theme}>
        <EmpresaRol />
      </ThemeProvider>
    );

    await waitFor(() => {
      expect(axios.get).toHaveBeenCalled();
    });

    fireEvent.click(screen.getByRole("button", { name: "common.actions.filters" }));
    fireEvent.change(await screen.findByTestId("filter-field-nombre"), {
      target: { value: "Técnico" },
    });
    fireEvent.click(screen.getByTestId("apply-filters-btn"));

    await waitFor(() => {
      expect(axios.get).toHaveBeenCalledWith(
        "/v1/empresa-rol",
        expect.objectContaining({ params: expect.objectContaining({ nombre: "Técnico" }) })
      );
    });

    const filteredRequestCount = () =>
      axios.get.mock.calls.filter(
        ([url, config]) =>
          url === "/v1/empresa-rol" && config?.params?.nombre === "Técnico"
      ).length;
    const countBeforeFormReload = filteredRequestCount();

    fireEvent.click(screen.getByTestId("form-reload-data"));

    await waitFor(() => {
      expect(filteredRequestCount()).toBeGreaterThan(countBeforeFormReload);
    });
  });

  it("4. Al limpiar filtros, restablece los parámetros y vuelve a consultar la API completa", async () => {
    render(
      <ThemeProvider theme={theme}>
        <EmpresaRol />
      </ThemeProvider>
    );

    await waitFor(() => {
      expect(axios.get).toHaveBeenCalled();
    });

    // Abrir modal de filtros
    const filtersBtn = screen.getByRole("button", { name: "common.actions.filters" });
    fireEvent.click(filtersBtn);

    const clearBtn = await screen.findByTestId("clear-filters-btn");
    fireEvent.click(clearBtn);

    await waitFor(() => {
      expect(axios.get).toHaveBeenCalledWith(
        "/v1/empresa-rol",
        expect.objectContaining({ params: {} })
      );
    });
  });

  it("5. Maneja respuestas 404 (empresa inexistente) mostrando el mensaje del backend", async () => {
    axios.get.mockImplementationOnce((url) => {
      if (url.includes("/v1/empresa-rol")) {
        return Promise.reject({
          response: {
            status: 404,
            data: { detail: "La empresa especificada no existe en el sistema." },
          },
        });
      }
      return Promise.resolve({ data: [] });
    });

    render(
      <ThemeProvider theme={theme}>
        <EmpresaRol />
      </ThemeProvider>
    );

    expect(
      await screen.findByText("La empresa especificada no existe en el sistema.")
    ).toBeInTheDocument();
  });
});
