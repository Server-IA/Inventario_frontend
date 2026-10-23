import React from "react";
import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { ThemeProvider, createTheme } from "@mui/material/styles";
import FormUsuario from "../Usuario/FormUsuario";
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

vi.mock("react-i18next", () => ({
  useTranslation: () => ({
    t: (key, fallback) => (typeof fallback === "string" ? fallback : key),
    i18n: { language: "es", resolvedLanguage: "es" },
  }),
}));

describe("Issue #325 (Nuevo alcance QA): Activar e inactivar asignaciones en FormUsuario", () => {
  const theme = createTheme({ palette: { mode: "light" } });

  const initialUser = {
    id: 10,
    nombre: "Carlos",
    apellido: "Pérez",
    emailPersonal: "carlos@test.com",
    username: "cperez",
    identificacion: "12345678",
    tipoIdentificacionId: 1,
    estadoId: 1,
    asignaciones: [
      {
        usuarioRolId: 101,
        rolId: 2,
        rolNombre: "Técnico",
        empresaId: 1505,
        empresaNombre: "Coagrohuila",
        estadoId: 1, // Activa inicialmente
        preferido: true,
      },
      {
        usuarioRolId: 102,
        rolId: 4,
        rolNombre: "Auditor",
        empresaId: 1505,
        empresaNombre: "Coagrohuila",
        estadoId: 2, // Inactiva inicialmente
        preferido: false,
      },
    ],
  };

  beforeEach(() => {
    vi.clearAllMocks();
    axios.get.mockImplementation((url) => {
      if (url.includes("/v1/items/empresa/0")) {
        return Promise.resolve({ data: [{ id: 1505, nombre: "Coagrohuila" }] });
      }
      if (url.includes("/v1/items/rol/0") || url.includes("/roles")) {
        return Promise.resolve({
          data: [
            { id: 2, nombre: "Técnico" },
            { id: 4, nombre: "Auditor" },
          ],
        });
      }
      if (url.includes("/v1/items/tipo_identificacion/0")) {
        return Promise.resolve({ data: [{ id: 1, nombre: "Cédula de Ciudadanía" }] });
      }
      return Promise.resolve({ data: [] });
    });
  });

  it("1. Cada tarjeta muestra el chip de estado correcto (Activo / Inactivo) y la acción correspondiente (Inactivar / Activar)", async () => {
    render(
      <ThemeProvider theme={theme}>
        <FormUsuario
          open={true}
          mode="edit"
          initialData={initialUser}
          onClose={() => {}}
          onSubmit={() => {}}
        />
      </ThemeProvider>
    );

    // Esperar carga de formulario
    expect(await screen.findByDisplayValue("Carlos")).toBeInTheDocument();

    // Verificación de chips de estado
    expect(screen.getByText("Activo")).toBeInTheDocument();
    expect(screen.getByText("Inactivo")).toBeInTheDocument();

    // La asignación activa ofrece "Inactivar"
    expect(screen.getByRole("button", { name: "Inactivar" })).toBeInTheDocument();

    // La asignación inactiva ofrece "Activar"
    expect(screen.getByRole("button", { name: "Activar" })).toBeInTheDocument();
  });

  it("2. Pulsar Inactivar o Activar conmuta el estado local pendiente de guardar sin eliminar la tarjeta", async () => {
    render(
      <ThemeProvider theme={theme}>
        <FormUsuario
          open={true}
          mode="edit"
          initialData={initialUser}
          onClose={() => {}}
          onSubmit={() => {}}
        />
      </ThemeProvider>
    );

    await screen.findByDisplayValue("Carlos");

    // Inactivar la asignación que estaba activa
    const inactivateBtn = screen.getByRole("button", { name: "Inactivar" });
    fireEvent.click(inactivateBtn);

    // Ahora ambas asignaciones quedan inactivas, por lo que debe haber 2 botones de "Activar"
    await waitFor(() => {
      expect(screen.getAllByRole("button", { name: "Activar" }).length).toBe(2);
    });

    // Activar una de ellas nuevamente
    const activateBtns = screen.getAllByRole("button", { name: "Activar" });
    fireEvent.click(activateBtns[0]);

    // Vuelve a aparecer el botón de Inactivar
    await waitFor(() => {
      expect(screen.getByRole("button", { name: "Inactivar" })).toBeInTheDocument();
    });
  });

  it("3. Al enviar el formulario (handleSubmit), se transmiten las asignaciones con usuarioRolId y estadoId actualizado", async () => {
    const handleSubmitMock = vi.fn();

    render(
      <ThemeProvider theme={theme}>
        <FormUsuario
          open={true}
          mode="edit"
          initialData={initialUser}
          onClose={() => {}}
          onSubmit={handleSubmitMock}
        />
      </ThemeProvider>
    );

    await screen.findByDisplayValue("Carlos");

    // Inactivar la asignación activa (id 101 pasa de 1 a 2)
    const inactivateBtn = screen.getByRole("button", { name: "Inactivar" });
    fireEvent.click(inactivateBtn);

    // Guardar cambios
    const saveBtn = screen.getByRole("button", { name: /guardar|actualizar/i });
    fireEvent.click(saveBtn);

    await waitFor(() => {
      expect(handleSubmitMock).toHaveBeenCalledWith(
        expect.objectContaining({
          asignaciones: expect.arrayContaining([
            expect.objectContaining({
              usuarioRolId: 101,
              estadoId: 2, // Conmutado a inactivo
            }),
            expect.objectContaining({
              usuarioRolId: 102,
              estadoId: 2, // Permaneció inactivo
            }),
          ]),
        })
      );
    });
  });
});
