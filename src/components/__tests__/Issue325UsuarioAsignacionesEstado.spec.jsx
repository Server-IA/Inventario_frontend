import React from "react";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { ThemeProvider, createTheme } from "@mui/material/styles";

vi.mock("react-i18next", () => ({
  useTranslation: () => ({
    t: (key, fallback) => fallback ?? key,
  }),
}));

vi.mock("../axiosConfig", () => ({
  default: {
    get: vi.fn(),
  },
}));

import axios from "../axiosConfig";
import FormUsuario from "../Usuario/FormUsuario";

const validUser = {
  requestId: 91,
  username: "qa.usuario@inmero.co",
  nombre: "Usuario",
  apellido: "QA",
  emailPersonal: "qa.personal@inmero.co",
  tipoIdentificacionId: "1",
  identificacion: "12345678",
  estrato: "3",
  asignaciones: [],
};

const EMPTY_ROLES = [];
const EMPTY_EMPRESAS = [];
const EMPTY_EMPRESA_ROLES = [];

const renderEditForm = ({ initialData, onSubmit = vi.fn() }) =>
  render(
    <ThemeProvider theme={createTheme()}>
      <FormUsuario
        open
        mode="edit"
        onClose={vi.fn()}
        onSubmit={onSubmit}
        initialData={initialData}
        roles={EMPTY_ROLES}
        empresas={EMPTY_EMPRESAS}
        empresaRoles={EMPTY_EMPRESA_ROLES}
        isAdmin
        tiposIdentificacion={[{ id: 1, nombre: "Cédula" }]}
      />
    </ThemeProvider>
  );

describe("Issue #325 - estado de asignaciones en Actualizar usuario", () => {
  beforeEach(() => {
    vi.clearAllMocks();
    axios.get.mockResolvedValue({ data: [] });
  });

  it("mantiene removible un borrador nuevo en modo edición", async () => {
    const user = userEvent.setup();

    renderEditForm({
      initialData: {
        ...validUser,
        asignaciones: [
          {
            empresaId: "1505",
            empresaNombre: "Empresa de prueba",
            rolId: "4",
            rolNombre: "Rol temporal",
            estadoId: 1,
          },
        ],
      },
    });

    expect(screen.getByRole("button", { name: "Quitar" })).toBeInTheDocument();
    expect(screen.queryByRole("button", { name: "Inactivar" })).not.toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "Quitar" }));

    expect(screen.queryByText("Rol: Rol temporal")).not.toBeInTheDocument();
  });

  it("envía la inactivación de una asignación persistida con su mismo usuarioRolId", async () => {
    const user = userEvent.setup();
    const onSubmit = vi.fn().mockResolvedValue({ success: true });

    renderEditForm({
      onSubmit,
      initialData: {
        ...validUser,
        asignaciones: [
          {
            usuarioRolId: 216,
            empresaId: "1505",
            empresaNombre: "Empresa de prueba",
            rolId: "4",
            rolNombre: "Rol activo",
            estadoId: 1,
            iniciaContratoEn: "2026-09-23",
            finalizaContratoEn: "",
          },
        ],
      },
    });

    await user.click(screen.getByRole("button", { name: "Inactivar" }));

    expect(screen.getByText("Inactivo")).toBeInTheDocument();
    expect(screen.getByText("Pendiente de guardar")).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "common.actions.save" }));

    await waitFor(() => {
      expect(onSubmit).toHaveBeenCalledWith(
        expect.objectContaining({
          asignaciones: [
            expect.objectContaining({
              usuarioRolId: 216,
              estadoId: 2,
              hasPendingStatusChange: true,
            }),
          ],
        })
      );
    });
  });

  it("revierte el cambio local pendiente cuando el backend rechaza la actualización", async () => {
    const user = userEvent.setup();
    const onSubmit = vi.fn().mockResolvedValue({ success: false });

    renderEditForm({
      onSubmit,
      initialData: {
        ...validUser,
        asignaciones: [
          {
            usuarioRolId: 215,
            empresaId: "1505",
            empresaNombre: "Empresa de prueba",
            rolId: "4",
            rolNombre: "Rol persistido",
            estadoId: 2,
            iniciaContratoEn: "2026-09-23",
            finalizaContratoEn: "",
          },
        ],
      },
    });

    await user.click(screen.getByRole("button", { name: "Activar" }));

    expect(screen.getByText("Activo")).toBeInTheDocument();
    expect(screen.getByText("Pendiente de guardar")).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: "common.actions.save" }));

    await waitFor(() => {
      expect(onSubmit).toHaveBeenCalledWith(
        expect.objectContaining({
          asignaciones: [
            expect.objectContaining({
              usuarioRolId: 215,
              estadoId: 1,
              hasPendingStatusChange: true,
            }),
          ],
        })
      );
    });

    await waitFor(() => {
      expect(screen.getByRole("button", { name: "Activar" })).toBeInTheDocument();
      expect(screen.queryByText("Pendiente de guardar")).not.toBeInTheDocument();
    });
  });
});
