import React from "react";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { ThemeProvider, createTheme } from "@mui/material/styles";
import axios from "../axiosConfig";

vi.mock("../axiosConfig", () => ({
  default: {
    get: vi.fn(),
    delete: vi.fn(),
  },
}));

const { translate } = vi.hoisted(() => ({
  translate: (key, fallback) => (typeof fallback === "string" ? fallback : key),
}));

vi.mock("react-i18next", () => ({
  useTranslation: () => ({ t: translate }),
}));

vi.mock("../common/AppDataGrid", () => ({
  default: ({ rows, setSelectedRow }) => (
    <button data-testid="select-empresa-rol" onClick={() => setSelectedRow(rows[0])}>
      Seleccionar relación
    </button>
  ),
}));
vi.mock("../common/GridActionBar", () => ({
  default: ({ onDelete }) => <button onClick={onDelete}>Eliminar relación</button>,
}));
vi.mock("../EmpresaRol/FormEmpresaRol", () => ({ default: () => null }));
vi.mock("../EmpresaRol/ModalVerPermisos", () => ({ default: () => null }));
vi.mock("../common/CrudFilterModal", () => ({ default: () => null }));
vi.mock("../MessageSnackBar", () => ({ default: () => null }));
vi.mock("../common/SectionHeader", () => ({ default: () => null }));

import EmpresaRol from "../EmpresaRol/EmpresaRol";

const theme = createTheme({ palette: { mode: "light" } });

describe("EmpresaRol - eliminación con contrato de permisos obligatorio", () => {
  beforeEach(() => {
    vi.clearAllMocks();
    localStorage.setItem("empresaId", "1505");
    localStorage.setItem("rolNombre", "ROLE_ADMINISTRADOR_EMPRESA");
    localStorage.setItem(
      "rolesByCompany",
      JSON.stringify([{ empresaId: 1505, rolId: 2, rolNombre: "ROLE_ADMINISTRADOR_EMPRESA" }])
    );
    axios.get.mockImplementation((url) => {
      if (url === "/v1/empresa-rol") {
        return Promise.resolve({
          data: [{ id: 101, rolNombre: "ROLE_QA_TEST", empresaId: 1505, estadoId: 1 }],
        });
      }
      if (url === "/v1/items/rol/0") {
        return Promise.resolve({ data: [{ id: 10, name: "ROLE_QA_TEST" }] });
      }
      if (url.includes("/empresa-rol-permisos/rol/10/permisos")) {
        return Promise.resolve({ data: [{ id: 77, nombre: "PERMISO_QA" }] });
      }
      return Promise.resolve({ data: [] });
    });
    axios.delete.mockResolvedValue({ status: 204 });
  });

  it("elimina la relación directamente sin retirar primero su último permiso", async () => {
    render(
      <ThemeProvider theme={theme}>
        <EmpresaRol />
      </ThemeProvider>
    );

    await waitFor(() => expect(screen.getByTestId("select-empresa-rol")).toBeEnabled());
    fireEvent.click(screen.getByTestId("select-empresa-rol"));
    fireEvent.click(screen.getByRole("button", { name: "Eliminar relación" }));
    fireEvent.click(screen.getByRole("button", { name: "common.actions.delete" }));

    await waitFor(() => {
      expect(axios.delete).toHaveBeenCalledTimes(1);
      expect(axios.delete).toHaveBeenCalledWith("/v1/empresa-rol/101");
    });
  });
});
