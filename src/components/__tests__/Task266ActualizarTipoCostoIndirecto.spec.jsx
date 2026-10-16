import React from "react";
import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { ThemeProvider, createTheme } from "@mui/material/styles";
import axios from "../axiosConfig";

vi.mock("../axiosConfig", () => ({
  default: {
    get: vi.fn(() => Promise.resolve({ data: {} })),
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

import FormTipoCostoIndirecto from "../Costos/TipoCostoIndirecto/FormTipoCostoIndirecto";

describe("Task #266 - HU-044.3 Actualizar tipo de costo indirecto", () => {
  const theme = createTheme({ palette: { mode: "light" } });

  const mockSelectedRow = {
    id: 15,
    nombre: "Arrendamiento de Bodega Principal",
    descripcion: "Alquiler mensual",
    empresaId: 1505,
    empresaNombre: "Coagrohuila Central",
    estadoId: 1,
    estadoNombre: "Activo",
  };

  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("1. Precarga los datos de la fila seleccionada y envía PUT /v1/tipos-costos-indirectos/{id}", async () => {
    const setMessage = vi.fn();
    const setOpen = vi.fn();
    const onCreated = vi.fn();

    axios.put.mockResolvedValueOnce({
      data: {
        id: 15,
        nombre: "Arrendamiento Bodegas Actualizado",
        descripcion: "Alquiler mensual actualizado",
        estadoId: 1,
        estadoNombre: "Activo",
        mensaje: "Tipo de costo actualizado correctamente",
      },
    });

    render(
      <ThemeProvider theme={theme}>
        <FormTipoCostoIndirecto
          open={true}
          setOpen={setOpen}
          selectedRow={mockSelectedRow}
          setMessage={setMessage}
          onCreated={onCreated}
        />
      </ThemeProvider>
    );

    // Debe precargar los valores existentes
    const nombreInput = screen.getByLabelText(/nombre/i);
    const descInput = screen.getByLabelText(/descripción/i);

    expect(nombreInput.value).toBe("Arrendamiento de Bodega Principal");
    expect(descInput.value).toBe("Alquiler mensual");

    // Modificar datos
    fireEvent.change(nombreInput, { target: { value: "Arrendamiento Bodegas Actualizado" } });
    fireEvent.change(descInput, { target: { value: "Alquiler mensual actualizado" } });

    const submitBtn = screen.getByRole("button", { name: /guardar/i });
    fireEvent.click(submitBtn);

    await waitFor(() => {
      expect(axios.put).toHaveBeenCalledWith("/v1/tipos-costos-indirectos/15", {
        nombre: "Arrendamiento Bodegas Actualizado",
        descripcion: "Alquiler mensual actualizado",
      });
    });

    expect(setMessage).toHaveBeenCalledWith(
      expect.objectContaining({
        open: true,
        severity: "success",
        text: "Tipo de costo actualizado correctamente",
      })
    );
    expect(setOpen).toHaveBeenCalledWith(false);
    expect(onCreated).toHaveBeenCalled();
  });

  it("2. Valida duplicidad en actualización ante respuesta 409", async () => {
    const setMessage = vi.fn();
    const setOpen = vi.fn();

    axios.put.mockRejectedValueOnce({
      response: {
        status: 409,
        data: {
          detail: "El nombre ya existe para la empresa",
        },
      },
    });

    render(
      <ThemeProvider theme={theme}>
        <FormTipoCostoIndirecto
          open={true}
          setOpen={setOpen}
          selectedRow={mockSelectedRow}
          setMessage={setMessage}
          onCreated={vi.fn()}
        />
      </ThemeProvider>
    );

    const nombreInput = screen.getByLabelText(/nombre/i);
    fireEvent.change(nombreInput, { target: { value: "Nombre Ya Existente" } });

    const submitBtn = screen.getByRole("button", { name: /guardar/i });
    fireEvent.click(submitBtn);

    await waitFor(() => {
      expect(axios.put).toHaveBeenCalled();
    });

    expect(await screen.findByText(/El nombre ya existe para la empresa/i)).toBeInTheDocument();
    expect(setOpen).not.toHaveBeenCalledWith(false);
  });
});
