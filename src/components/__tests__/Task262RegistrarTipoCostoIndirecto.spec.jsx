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

describe("Task #262 - HU-044.1 Registrar tipo de costo indirecto", () => {
  const theme = createTheme({ palette: { mode: "light" } });

  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("1. Valida campo obligatorio 'nombre' síncronamente antes de enviar", async () => {
    const setMessage = vi.fn();
    const setOpen = vi.fn();

    render(
      <ThemeProvider theme={theme}>
        <FormTipoCostoIndirecto
          open={true}
          setOpen={setOpen}
          setMessage={setMessage}
          onCreated={vi.fn()}
        />
      </ThemeProvider>
    );

    // Intentar guardar con nombre vacío
    const submitBtn = screen.getByRole("button", { name: /guardar/i });
    fireEvent.click(submitBtn);

    // No debe llamar a axios.post
    expect(axios.post).not.toHaveBeenCalled();

    // Debe mostrar mensaje de error en el helperText
    expect(await screen.findByText("costos.tipoCostoIndirecto.form.validation.nombreRequired")).toBeInTheDocument();
  });

  it("2. Envía POST /v1/tipos-costos-indirectos y notifica registro exitoso", async () => {
    const setMessage = vi.fn();
    const setOpen = vi.fn();
    const onCreated = vi.fn();

    axios.post.mockResolvedValueOnce({
      data: {
        id: 10,
        nombre: "Transporte y Fletes",
        descripcion: "Fletes de materia prima",
        estadoId: 1,
        estadoNombre: "Activo",
        mensaje: "Tipo de costo registrado correctamente",
      },
    });

    render(
      <ThemeProvider theme={theme}>
        <FormTipoCostoIndirecto
          open={true}
          setOpen={setOpen}
          setMessage={setMessage}
          onCreated={onCreated}
        />
      </ThemeProvider>
    );

    const nombreInput = screen.getByLabelText(/nombre/i);
    const descInput = screen.getByLabelText(/descripción/i);

    fireEvent.change(nombreInput, { target: { value: "Transporte y Fletes" } });
    fireEvent.change(descInput, { target: { value: "Fletes de materia prima" } });

    const submitBtn = screen.getByRole("button", { name: /guardar/i });
    fireEvent.click(submitBtn);

    await waitFor(() => {
      expect(axios.post).toHaveBeenCalledWith("/v1/tipos-costos-indirectos", {
        nombre: "Transporte y Fletes",
        descripcion: "Fletes de materia prima",
      });
    });

    expect(setMessage).toHaveBeenCalledWith(
      expect.objectContaining({
        open: true,
        severity: "success",
        text: "Tipo de costo registrado correctamente",
      })
    );
    expect(setOpen).toHaveBeenCalledWith(false);
    expect(onCreated).toHaveBeenCalled();
  });

  it("3. Muestra alerta de error cuando el backend retorna 409 por nombre duplicado", async () => {
    const setMessage = vi.fn();
    const setOpen = vi.fn();

    axios.post.mockRejectedValueOnce({
      response: {
        status: 409,
        data: {
          detail: "Ya existe un tipo de costo indirecto con ese nombre para la empresa.",
        },
      },
    });

    render(
      <ThemeProvider theme={theme}>
        <FormTipoCostoIndirecto
          open={true}
          setOpen={setOpen}
          setMessage={setMessage}
          onCreated={vi.fn()}
        />
      </ThemeProvider>
    );

    const nombreInput = screen.getByLabelText(/nombre/i);
    fireEvent.change(nombreInput, { target: { value: "Transporte y Fletes" } });

    const submitBtn = screen.getByRole("button", { name: /guardar/i });
    fireEvent.click(submitBtn);

    await waitFor(() => {
      expect(axios.post).toHaveBeenCalled();
    });

    expect(await screen.findByText(/Ya existe un tipo de costo indirecto/i)).toBeInTheDocument();
    expect(setOpen).not.toHaveBeenCalledWith(false);
  });
});
