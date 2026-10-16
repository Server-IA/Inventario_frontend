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

vi.mock("../common/AppDataGrid.jsx", () => ({
  default: ({ rows = [], columns = [] }) => (
    <div data-testid="mock-app-data-grid">
      {rows.map((row) => (
        <div key={row.id} data-testid={`kardex-row-${row.id}`}>
          {columns.map((col) => (
            <span key={col.field} data-testid={`cell-${col.field}`}>
              {col.renderCell ? col.renderCell({ row, value: row[col.field] }) : row[col.field]}
            </span>
          ))}
        </div>
      ))}
    </div>
  ),
}));

import Rkardex from "../RKardex/Rkardex";

describe("HU-039.2 & HU-039.3 - Previsualización y Exportación del Reporte Kardex V2", () => {
  const theme = createTheme({ palette: { mode: "light" } });

  const mockKardexFiltros = {
    fechaInicio: "2026-09-01",
    fechaFin: "2026-10-16",
    paises: [{ id: 7, nombre: "Colombia" }],
    departamentos: [{ id: 7, nombre: "Huila" }],
    municipios: [{ id: 22, nombre: "Neiva" }],
    sedes: [{ id: 86, nombre: "Sede Neiva Primera" }],
    bloques: [{ id: 81, nombre: "Ventas Mayoristas" }],
    espacios: [{ id: 66, nombre: "Venta insumo Agropecuario" }],
    almacenes: [{ id: 85, nombre: "Agropecuario Central" }],
    categorias: [{ id: 102, nombre: "Insumos Agrícolas" }],
    producciones: [{ id: 50, nombre: "Producción General" }],
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

  const mockKardexResumen = {
    totalPages: 1,
    totalElements: 1,
    size: 10,
    number: 0,
    content: [
      {
        id: "mov-1",
        headerEmpresaId: 1505,
        headerEmpresa: "Coagrohuila",
        headerSedeId: 86,
        headerSede: "Sede Neiva Primera",
        headerAlmacenId: 85,
        headerAlmacen: "Agropecuario Central",
        itemProduccionNombre: "Producción de Insumos",
        itemTipoMovimientoNombre: "Entrada Insumo Agrícola",
        itemProducto: "CERTUS 70 WG Fungicida",
        itemPresentacionNombre: "Paquete de 500 g",
        itemCantidad: 20,
        itemSaldo: 20,
        itemFecha: "2026-09-15T10:29:00",
      },
    ],
  };

  beforeEach(() => {
    vi.clearAllMocks();
    window.URL.createObjectURL = vi.fn(() => "blob:mock-kardex-url");
    window.URL.revokeObjectURL = vi.fn();
    axios.get.mockImplementation((url) => {
      if (url.includes("/v2/report/kardex/filtros")) {
        return Promise.resolve({ data: mockKardexFiltros });
      }
      if (url.includes("/v2/report/kardex/productos")) {
        return Promise.resolve({ data: [{ id: 177, nombre: "CERTUS 70 WG Fungicida" }] });
      }
      if (url.includes("/v2/report/kardex/presentaciones")) {
        return Promise.resolve({ data: [{ id: 12, nombre: "Paquete de 500 g" }] });
      }
      return Promise.resolve({ data: [] });
    });

    axios.post.mockImplementation((url) => {
      if (url.includes("/v2/report/kardex/resumen")) {
        return Promise.resolve({ data: mockKardexResumen });
      }
      if (url.includes("/v2/report/kardex/exportar")) {
        return Promise.resolve({ data: new Blob(["xlsx-data"], { type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" }) });
      }
      if (url.includes("/v2/report/kardex")) {
        return Promise.resolve({ data: new Blob(["pdf-data"], { type: "application/pdf" }) });
      }
      return Promise.resolve({ data: {} });
    });
  });

  describe("HU-039.2: Previsualización de Kardex", () => {
    it("1. Inicializa y consulta filtros inteligentes desde GET /v2/report/kardex/filtros", async () => {
      render(
        <ThemeProvider theme={theme}>
          <Rkardex />
        </ThemeProvider>
      );

      await waitFor(() => {
        expect(axios.get).toHaveBeenCalledWith(
          expect.stringContaining("/v2/report/kardex/filtros"),
          expect.anything()
        );
      });

      expect(screen.getByText("Reportes de Kardex")).toBeInTheDocument();
    });

    it("2. Ejecuta búsqueda mediante POST /v2/report/kardex/resumen con DTO tipado sin condicion, SQL ni EMPRESA_ID", async () => {
      render(
        <ThemeProvider theme={theme}>
          <Rkardex />
        </ThemeProvider>
      );

      await waitFor(() => {
        expect(axios.get).toHaveBeenCalled();
      });

      const searchBtn = screen.getByRole("button", { name: /buscar/i });
      fireEvent.click(searchBtn);

      await waitFor(() => {
        expect(axios.post).toHaveBeenCalledWith(
          expect.stringContaining("/v2/report/kardex/resumen?page=0&size=10"),
          expect.any(Object)
        );
      });

      const [, sentBody] = axios.post.mock.calls.find(([url]) =>
        url.includes("/v2/report/kardex/resumen")
      );

      expect(sentBody).toHaveProperty("fechaInicio");
      expect(sentBody).toHaveProperty("fechaFin");
      expect(sentBody).not.toHaveProperty("condicion");
      expect(sentBody).not.toHaveProperty("EMPRESA_ID");
      expect(sentBody).not.toHaveProperty("formato");
      expect(sentBody).not.toHaveProperty("filtrosAplicados");

      await waitFor(() => {
        expect(screen.getByText("CERTUS 70 WG Fungicida")).toBeInTheDocument();
        expect(screen.getByText("Sede Neiva Primera")).toBeInTheDocument();
        expect(screen.getByText("Agropecuario Central")).toBeInTheDocument();
        expect(screen.getByText("Entrada Insumo Agrícola")).toBeInTheDocument();
        expect(screen.getByText("Paquete de 500 g")).toBeInTheDocument();
      });
    });

    it("3. Muestra mensaje amigable cuando no hay registros para los filtros seleccionados", async () => {
      axios.post.mockImplementationOnce((url) => {
        if (url.includes("/v2/report/kardex/resumen")) {
          return Promise.resolve({ data: { content: [], totalElements: 0 } });
        }
        return Promise.resolve({ data: {} });
      });

      render(
        <ThemeProvider theme={theme}>
          <Rkardex />
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

    it("4. Gestiona errores HTTP (400, 403) de forma controlada sin confundirlos con vacío", async () => {
      axios.post.mockImplementationOnce(() =>
        Promise.reject({
          response: { status: 403, data: { detail: "Acceso no autorizado al reporte" } },
        })
      );

      render(
        <ThemeProvider theme={theme}>
          <Rkardex />
        </ThemeProvider>
      );

      const searchBtn = screen.getByRole("button", { name: /buscar/i });
      fireEvent.click(searchBtn);

      await waitFor(() => {
        expect(
          screen.getByText(/no tiene permisos para consultar este reporte/i)
        ).toBeInTheDocument();
      });
    });
  });

  describe("HU-039.3: Exportación PDF y Excel (T-216)", () => {
    it("5. Exporta reporte en PDF conectando a POST /v2/report/kardex con DTO tipado", async () => {
      render(
        <ThemeProvider theme={theme}>
          <Rkardex />
        </ThemeProvider>
      );

      const exportModalBtn = screen.getByRole("button", { name: /generar reporte/i });
      fireEvent.click(exportModalBtn);

      const pdfBtn = screen.getByRole("button", { name: /^PDF$/i });
      fireEvent.click(pdfBtn);

      await waitFor(() => {
        expect(axios.post).toHaveBeenCalledWith(
          "/v2/report/kardex",
          expect.any(Object),
          expect.objectContaining({ responseType: "blob" })
        );
      });

      const [, pdfBody] = axios.post.mock.calls.find(([url]) =>
        url === "/v2/report/kardex"
      );
      expect(pdfBody).toHaveProperty("fechaInicio");
      expect(pdfBody).toHaveProperty("fechaFin");
      expect(pdfBody).not.toHaveProperty("condicion");
      expect(pdfBody).not.toHaveProperty("EMPRESA_ID");
      expect(pdfBody).not.toHaveProperty("filtrosAplicados");
    });

    it("6. Exporta reporte en Excel conectando a POST /v2/report/kardex/exportar?formato=XLSX", async () => {
      render(
        <ThemeProvider theme={theme}>
          <Rkardex />
        </ThemeProvider>
      );

      const exportModalBtn = screen.getByRole("button", { name: /generar reporte/i });
      fireEvent.click(exportModalBtn);

      const excelBtn = screen.getByRole("button", { name: /^Excel$/i });
      fireEvent.click(excelBtn);

      await waitFor(() => {
        expect(axios.post).toHaveBeenCalledWith(
          "/v2/report/kardex/exportar?formato=XLSX",
          expect.any(Object),
          expect.objectContaining({ responseType: "blob" })
        );
      });

      const [, excelBody] = axios.post.mock.calls.find(([url]) =>
        url.includes("/v2/report/kardex/exportar?formato=XLSX")
      );
      expect(excelBody).toHaveProperty("fechaInicio");
      expect(excelBody).toHaveProperty("fechaFin");
      expect(excelBody).not.toHaveProperty("condicion");
      expect(excelBody).not.toHaveProperty("EMPRESA_ID");
    });
  });
});
