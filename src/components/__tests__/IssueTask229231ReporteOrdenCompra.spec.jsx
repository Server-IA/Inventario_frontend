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
  default: ({ rows = [], columns = [], paginationModel, setPaginationModel }) => (
    <div data-testid="mock-app-data-grid">
      <div data-testid="pagination-controls">
        <button
          data-testid="next-page-btn"
          onClick={() => setPaginationModel?.({ page: (paginationModel?.page ?? 0) + 1, size: paginationModel?.pageSize ?? 10 })}
        >
          Siguiente página
        </button>
        <button
          data-testid="change-size-btn"
          onClick={() => setPaginationModel?.({ page: 0, size: 20 })}
        >
          20 filas
        </button>
      </div>
      {rows.map((row) => (
        <div key={row.id} data-testid={`row-${row.id}`}>
          {columns.map((col) => (
            <span key={col.field}>
              {col.renderCell ? col.renderCell({ row, value: row[col.field] }) : row[col.field]}
            </span>
          ))}
        </div>
      ))}
    </div>
  ),
}));

import RE_ordenCompra from "../RE_oc/re_oc";
import VistaPreviaPDFOrdenCompra from "../OrdenCompra/vistapreviapdfordencompra";

describe("HU-041.1 & HU-041.2 - Reporte de Orden de Compra y Resumen de Totales", () => {
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
        cantidadItems: 2,
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

  describe("HU-041.2: Pruebas unitarias del Resumen de Totales (VistaPreviaPDFOrdenCompra)", () => {
    it("1. Renderiza valor total y total de unidades devueltos por el backend", () => {
      const mockOrden = {
        ordenCompraId: 10,
        pedidoId: 5,
        fechaHora: "2026-09-10T14:30:00",
        proveedor: "Proveedor Prueba",
        sede: "Sede Central",
        almacen: "Almacén 1",
        estado: "Activo",
        totalUnidades: 35,
        valorTotal: 125000,
      };

      render(
        <ThemeProvider theme={theme}>
          <VistaPreviaPDFOrdenCompra orden={mockOrden} />
        </ThemeProvider>
      );

      expect(screen.getByText("35")).toBeInTheDocument();
      expect(screen.getByText(/125\.000/)).toBeInTheDocument();
    });

    it("2. Muestra mensaje explícito 'No aplica: unidades heterogéneas' cuando totalUnidades es null", () => {
      const mockOrdenHeterogenea = {
        ordenCompraId: 11,
        totalUnidades: null,
        valorTotal: 85000,
      };

      render(
        <ThemeProvider theme={theme}>
          <VistaPreviaPDFOrdenCompra orden={mockOrdenHeterogenea} />
        </ThemeProvider>
      );

      expect(screen.getByText(/No aplica: unidades heterogéneas/i)).toBeInTheDocument();
      expect(screen.getByText(/85\.000/)).toBeInTheDocument();
    });

    it("3. Maneja correctamente órdenes sin productos mostrando totales en 0", () => {
      const mockOrdenVacia = {
        ordenCompraId: 12,
        totalUnidades: 0,
        valorTotal: 0,
      };

      render(
        <ThemeProvider theme={theme}>
          <VistaPreviaPDFOrdenCompra orden={mockOrdenVacia} />
        </ThemeProvider>
      );

      expect(screen.getByText("0")).toBeInTheDocument();
      expect(screen.getByText(/0,00/)).toBeInTheDocument();
    });

    it("4. Representa un fallo técnico como estado de error y no como total cero", () => {
      const mockOrdenError = {
        error: true,
        errorMessage: "Error en la consulta de backend",
      };

      render(
        <ThemeProvider theme={theme}>
          <VistaPreviaPDFOrdenCompra orden={mockOrdenError} />
        </ThemeProvider>
      );

      expect(screen.getByText("Error en la consulta de backend")).toBeInTheDocument();
      expect(screen.queryByText("$0,00")).not.toBeInTheDocument();
    });
  });

  describe("HU-041.1 & HU-041.2: Integración en RE_ordenCompra", () => {
    it("5. Búsqueda y apertura de resumen desde resultados generales", async () => {
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
          expect.stringContaining("/v2/report/orden-compra/resumen"),
          expect.any(Object)
        );
      });

      // Abrir resumen desde la grilla
      const viewDetailBtn = await screen.findByRole("button", { name: /ver resumen/i });
      fireEvent.click(viewDetailBtn);

      // Verifica que la tarjeta de resumen se abra en pantalla
      expect(screen.getAllByText("Resumen de la Orden de Compra").length).toBeGreaterThanOrEqual(1);
      expect(screen.getByText("Totales de la Compra")).toBeInTheDocument();

      // Cerrar resumen
      const closeBtn = screen.getByRole("button", { name: /cerrar/i });
      fireEvent.click(closeBtn);

      expect(screen.queryByText("Totales de la Compra")).not.toBeInTheDocument();
    });

    it("6. Exportación en PDF mediante POST /v2/report/orden-compra/exportar?formato=PDF", async () => {
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
    });

    it("7. Exportación en Excel mediante POST /v2/report/orden-compra/exportar?formato=XLSX", async () => {
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
    });

    it("8. Paginación server-side y cambio de filas por página en Orden de Compra", async () => {
      render(
        <ThemeProvider theme={theme}>
          <RE_ordenCompra />
        </ThemeProvider>
      );

      const searchBtn = screen.getByRole("button", { name: /buscar/i });
      fireEvent.click(searchBtn);

      await waitFor(() => {
        expect(axios.post).toHaveBeenCalledWith(
          expect.stringContaining("/v2/report/orden-compra/resumen?page=0&size=10"),
          expect.any(Object)
        );
      });

      // Disparar siguiente página
      const nextPageBtn = screen.getByTestId("next-page-btn");
      fireEvent.click(nextPageBtn);

      await waitFor(() => {
        expect(axios.post).toHaveBeenCalledWith(
          expect.stringContaining("/v2/report/orden-compra/resumen?page=1&size=10"),
          expect.any(Object)
        );
      });

      // Disparar cambio de tamaño
      const changeSizeBtn = screen.getByTestId("change-size-btn");
      fireEvent.click(changeSizeBtn);

      await waitFor(() => {
        expect(axios.post).toHaveBeenCalledWith(
          expect.stringContaining("/v2/report/orden-compra/resumen?page=0&size=20"),
          expect.any(Object)
        );
      });
    });
  });
});
