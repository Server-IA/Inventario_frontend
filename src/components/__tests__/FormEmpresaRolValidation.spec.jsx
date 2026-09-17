import React from "react";
import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { ThemeProvider, createTheme } from "@mui/material/styles";
import FormEmpresaRol from "../EmpresaRol/FormEmpresaRol";
import axios from "../axiosConfig";

// Mock i18next
vi.mock("react-i18next", () => ({
  useTranslation: () => ({
    t: (key, fallback) => {
      const translations = {
        "empresaRol.messages.roleRequired": "Debe seleccionar un rol",
        "empresaRol.messages.companyRequired": "Debe seleccionar una empresa",
        "empresaRol.messages.permissionsRequired": "Debe seleccionar al menos un permiso.",
        "empresaRol.messages.saveSuccess": "Permisos actualizados correctamente",
        "common.actions.save": "Guardar",
        "common.actions.close": "Cerrar",
      };
      return translations[key] || (typeof fallback === "string" ? fallback : key);
    },
    i18n: {
      changeLanguage: vi.fn(),
      language: "es",
    },
  }),
}));

// Mock axios
vi.mock("../axiosConfig", () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    delete: vi.fn(),
  },
}));

const theme = createTheme();

const mockRoles = [
  { id: 10, name: "ROLE_TEST" },
  { id: 20, name: "ROLE_ADMIN" },
];

const mockSubsistemas = [
  { id: 1, nombre: "Seguridad" },
];

const mockModulos = [
  {
    moduloId: 101,
    moduloNombre: "Usuarios",
    permisos: [
      { id: 1001, nombre: "CREAR_USUARIO", descripcion: "Crear usuarios", autoridad: "USUARIO_CREAR" },
      { id: 1002, nombre: "EDITAR_USUARIO", descripcion: "Editar usuarios", autoridad: "USUARIO_EDITAR" },
    ],
  },
];

describe("Issue #297 - FormEmpresaRol validación de permisos", () => {
  const setMessage = vi.fn();
  const setOpen = vi.fn();
  const setSelectedRow = vi.fn();
  const reloadData = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();

    axios.get.mockImplementation((url) => {
      if (url.includes("/v1/items/empresa/0")) {
        return Promise.resolve({ data: [{ id: 1505, nombre: "Coagrohuila" }] });
      }
      if (url.includes("/v1/sub-sistemas")) {
        return Promise.resolve({ data: mockSubsistemas });
      }
      if (url.includes("/v1/empresa-rol-permisos/modulos-subsistema")) {
        return Promise.resolve({ data: mockModulos });
      }
      if (url.includes("/v1/empresa-rol-permisos/rol/")) {
        return Promise.resolve({ data: [] });
      }
      return Promise.resolve({ data: [] });
    });
  });

  it("bloquea el guardado en creación cuando no se selecciona ningún permiso", async () => {
    render(
      <ThemeProvider theme={theme}>
        <FormEmpresaRol
          open={true}
          setOpen={setOpen}
          selectedRow={null}
          setSelectedRow={setSelectedRow}
          setMessage={setMessage}
          reloadData={reloadData}
          roles={mockRoles}
          empresaId={1505}
          isSystemAdmin={false}
        />
      </ThemeProvider>
    );

    // Seleccionar rol usando role combobox
    const combobox = screen.getByRole("combobox");
    fireEvent.mouseDown(combobox);

    const optionRol = await screen.findByRole("option", { name: "ROLE_TEST" });
    fireEvent.click(optionRol);

    // Intentar guardar sin marcar permisos
    const saveButton = screen.getByRole("button", { name: /guardar/i });
    fireEvent.click(saveButton);

    // No debe llamar a axios.post ni axios.delete
    expect(axios.post).not.toHaveBeenCalled();
    expect(axios.delete).not.toHaveBeenCalled();

    // Debe mostrar advertencia
    expect(setMessage).toHaveBeenCalledWith(
      expect.objectContaining({
        open: true,
        severity: "warning",
        text: "Debe seleccionar al menos un permiso.",
      })
    );

    // No debe cerrar el modal
    expect(setOpen).not.toHaveBeenCalledWith(false);
  });

  it("permite guardar cuando se selecciona al menos un permiso", async () => {
    axios.post.mockResolvedValue({ data: { success: true } });

    render(
      <ThemeProvider theme={theme}>
        <FormEmpresaRol
          open={true}
          setOpen={setOpen}
          selectedRow={null}
          setSelectedRow={setSelectedRow}
          setMessage={setMessage}
          reloadData={reloadData}
          roles={mockRoles}
          empresaId={1505}
          isSystemAdmin={false}
        />
      </ThemeProvider>
    );

    // Seleccionar rol
    const combobox = screen.getByRole("combobox");
    fireEvent.mouseDown(combobox);
    const optionRol = await screen.findByRole("option", { name: "ROLE_TEST" });
    fireEvent.click(optionRol);

    // Esperar a que carguen los subsistemas/módulos
    await waitFor(() => {
      expect(screen.getByText("Seguridad")).toBeInTheDocument();
    });

    // Abrir acordeón del subsistema
    fireEvent.click(screen.getByText("Seguridad"));

    await waitFor(() => {
      expect(screen.getByText("Usuarios")).toBeInTheDocument();
    });

    // Abrir acordeón del módulo
    fireEvent.click(screen.getByText("Usuarios"));

    // Marcar un permiso buscando por texto del permiso
    await waitFor(() => {
      expect(screen.getByText("CREAR_USUARIO")).toBeInTheDocument();
    });

    // Encontrar el checkbox asociado
    const checkboxes = screen.getAllByRole("checkbox");
    expect(checkboxes.length).toBeGreaterThan(0);
    fireEvent.click(checkboxes[0]);

    // Guardar
    const saveButton = screen.getByRole("button", { name: /guardar/i });
    fireEvent.click(saveButton);

    await waitFor(() => {
      expect(axios.post).toHaveBeenCalled();
      expect(setMessage).toHaveBeenCalledWith(
        expect.objectContaining({
          open: true,
          severity: "success",
          text: "Permisos actualizados correctamente",
        })
      );
    });
  });
});
