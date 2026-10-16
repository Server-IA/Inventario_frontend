import React from "react";
import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { ThemeProvider, createTheme } from "@mui/material/styles";
import axios from "../axiosConfig";

vi.mock("../axiosConfig", () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
  },
}));

vi.mock("react-i18next", () => ({
  useTranslation: () => ({
    t: (key, fallback) => (typeof fallback === "string" ? fallback : key),
    i18n: { language: "es" },
  }),
}));

vi.mock("react-date-range", () => ({
  DateRange: () => <div data-testid="mock-date-range" />,
}));

import RE_ordenCompra from "../RE_oc/re_oc";

describe("HU-041.1 / Task #229 - Reporte de Orden de Compra V2", () => {
  const theme = createTheme({ palette: { mode: "light" } });

  const mockFiltrosResponse = {
    fechaInicio: "2026-09-01",
    fechaFin: "2026-10-16",
    paises: [{ id: 7, nombre: "Colombia" }],
    departamentos: [{ id: 7, nombre: "Huila" }],
    municipios: [{ id: 22, nombre: "Neiva" }],
    sedes: [{ id: 86, nombre: "Sede Neiva Primera" }],
    bloques: [{ id: 81, nombre: "Ventas Mayoristas" }],
    espacios: [{ id: 66, nombre: "Venta insumo Agropecuario" }],
    almacenes: [{ id: 85, nombre: "Agropecuario Central" }],
    proveedores: [{ id: 88, nombre: "AGROCENTRO" }],
    estados: [{ id: 24, nombre: "Entregado al proveedor" }],
    seleccionInicial: {
      paisId: 7,
      departamentoId: 7,
      municipioId: 22,
      sedeId: 86,
      bloqueId: 81,
      espacioId: 66,
      almacenId: 85,
    },
  };

  const mockResumenResponse = {
    totalPages: 1,
    totalElements: 1,
    size: 10,
    number: 0,
    content: [
      {
        ordenCompraId: 75,
        pedidoId: 331,
        fechaHora: "2026-09-01T00:24:09",
        proveedor: "AGROCENTRO",
        sede: "Sede Neiva Primera",
        almacen: "Agropecuario Central",
        estado: "Entregado al proveedor",
        cantidadItems: 1,
        totalUnidades: 22,
        valorTotal: 50000,
      },
    ],
  };

  beforeEach(() => {
    vi.clearAllMocks();
    window.URL.createObjectURL = vi.fn(() => "blob:mock-url");
    window.URL.revokeObjectURL = vi.fn();
    axios.get.mockImplementation((url) => {
      if (url.includes("/v2/report/orden-compra/filtros")) {
        return Promise.resolve({ data: mockFiltrosResponse });
      }
      return Promise.resolve({ data: [] });
    });
    axios.post.mockImplementation((url) => {
      if (url.includes("/v2/report/orden-compra/resumen")) {
        return Promise.resolve({ data: mockResumenResponse });
      }
      if (url.includes("/v2/report/orden-compra/exportar")) {
        return Promise.resolve({ data: new Blob(["dummy"], { type: "application/pdf" }) });
      }
      return Promise.resolve({ data: {} });
    });
  });

  it("1. Consume correctamente los filtros inteligentes desde GET /v2/report/orden-compra/filtros", async () => {
    render(
      <ThemeProvider theme={theme}>
        <RE_ordenCompra />
      </ThemeProvider>
    );

    await waitFor(() => {
      expect(axios.get).toHaveBeenCalledWith(
        expect.stringContaining("/v2/report/orden-compra/filtros"),
        expect.anything()
      );
    });

    expect(screen.getByText("Reporte de Orden de Compra")).toBeInTheDocument();
  });

  it("2. Ejecuta la búsqueda enviando body tipado a POST /v2/report/orden-compra/resumen sin condicion ni EMPRESA_ID", async () => {
    render(
      <ThemeProvider theme={theme}>
        <RE_ordenCompra />
      </ThemeProvider>
    );

    await waitFor(() => {
      expect(axios.get).toHaveBeenCalled();
    });

    const searchBtn = screen.getByRole("button", { name: /buscar/i });
    fireEvent.click(searchBtn);

    await waitFor(() => {
      expect(axios.post).toHaveBeenCalledWith(
        expect.stringContaining("/v2/report/orden-compra/resumen?page=0&size=10"),
        expect.any(Object)
      );
    });

    const [, sentBody] = axios.post.mock.calls.find(([url]) =>
      url.includes("/v2/report/orden-compra/resumen")
    );

    // Valida que el body sea estrictamente tipado según contrato V2
    expect(sentBody).toHaveProperty("fechaInicio");
    expect(sentBody).toHaveProperty("fechaFin");
    expect(sentBody).not.toHaveProperty("condicion");
    expect(sentBody).not.toHaveProperty("EMPRESA_ID");
    expect(sentBody).not.toHaveProperty("formato");
  });

  it("3. Genera exportación en PDF mediante POST /v2/report/orden-compra/exportar?formato=PDF", async () => {
    render(
      <ThemeProvider theme={theme}>
        <RE_ordenCompra />
      </ThemeProvider>
    );

    const exportModalBtn = screen.getByRole("button", { name: /generar reporte/i });
    fireEvent.click(exportModalBtn);

    const pdfBtn = screen.getByRole("button", { name: /^PDF$/i });
    fireEvent.click(pdfBtn);

    await waitFor(() => {
      expect(axios.post).toHaveBeenCalledWith(
        expect.stringContaining("/v2/report/orden-compra/exportar?formato=PDF"),
        expect.any(Object),
        expect.objectContaining({ responseType: "blob" })
      );
    });

    const [, pdfBody] = axios.post.mock.calls.find(([url]) =>
      url.includes("/v2/report/orden-compra/exportar?formato=PDF")
    );
    expect(pdfBody).not.toHaveProperty("condicion");
    expect(pdfBody).not.toHaveProperty("EMPRESA_ID");
  });

  it("4. Genera exportación en Excel mediante POST /v2/report/orden-compra/exportar?formato=XLSX", async () => {
    render(
      <ThemeProvider theme={theme}>
        <RE_ordenCompra />
      </ThemeProvider>
    );

    const exportModalBtn = screen.getByRole("button", { name: /generar reporte/i });
    fireEvent.click(exportModalBtn);

    const excelBtn = screen.getByRole("button", { name: /^Excel$/i });
    fireEvent.click(excelBtn);

    await waitFor(() => {
      expect(axios.post).toHaveBeenCalledWith(
        expect.stringContaining("/v2/report/orden-compra/exportar?formato=XLSX"),
        expect.any(Object),
        expect.objectContaining({ responseType: "blob" })
      );
    });

    const [, excelBody] = axios.post.mock.calls.find(([url]) =>
      url.includes("/v2/report/orden-compra/exportar?formato=XLSX")
    );
    expect(excelBody).not.toHaveProperty("condicion");
    expect(excelBody).not.toHaveProperty("EMPRESA_ID");
  });

  it("5. Gestiona respuestas sin resultados mostrando mensaje accesible", async () => {
    axios.post.mockImplementationOnce((url) => {
      if (url.includes("/v2/report/orden-compra/resumen")) {
        return Promise.resolve({ data: { content: [], totalElements: 0 } });
      }
      return Promise.resolve({ data: [] });
    });

    render(
      <ThemeProvider theme={theme}>
        <RE_ordenCompra />
      </ThemeProvider>
    );

    const searchBtn = screen.getByRole("button", { name: /buscar/i });
    fireEvent.click(searchBtn);

    await waitFor(() => {
      expect(
        screen.getByText(/no se encontraron registros para los filtros seleccionados/i)
      ).toBeInTheDocument();
    });
  });

  it("6. Maneja errores 400 y 403 de forma controlada sin exponer detalles técnicos", async () => {
    axios.post.mockImplementationOnce(() =>
      Promise.reject({
        response: { status: 403, data: { detail: "Acceso denegado" } },
      })
    );

    render(
      <ThemeProvider theme={theme}>
        <RE_ordenCompra />
      </ThemeProvider>
    );

    const searchBtn = screen.getByRole("button", { name: /buscar/i });
    fireEvent.click(searchBtn);

    await waitFor(() => {
      expect(
        screen.getByText(/no tiene permisos para realizar esta operación/i)
      ).toBeInTheDocument();
    });
  });
});
