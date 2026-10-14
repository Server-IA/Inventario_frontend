import React from "react";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { fireEvent, render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { ThemeProvider, createTheme } from "@mui/material/styles";
import i18n from "../../i18n";
import UserFiltersDialog from "../Usuario/UserFiltersDialog";

const renderDialog = (overrides = {}) => {
  const callbacks = {
    onClose: vi.fn(),
    onChange: vi.fn(),
    onApply: vi.fn(),
    onClear: vi.fn(),
  };

  render(
    <ThemeProvider theme={createTheme({ palette: { mode: "light" } })}>
      <UserFiltersDialog
        open
        values={{ username: "", nombre: "", apellido: "", rolId: "", estadoId: "", empresaId: "" }}
        roles={[{ value: "1", label: "Administrador" }]}
        estados={[{ value: "1", label: "Activo" }]}
        empresas={[{ value: "1", label: "INMERO" }]}
        {...callbacks}
        {...overrides}
      />
    </ThemeProvider>
  );

  return { ...callbacks, ...overrides };
};

describe("Issue #323 - UserFiltersDialog simplificado", () => {
  beforeEach(async () => {
    await i18n.changeLanguage("es");
  });

  it("retira los bloques informativos redundantes", () => {
    renderDialog();

    expect(screen.queryByText("Acota el listado de usuarios utilizando los criterios disponibles.")).not.toBeInTheDocument();
    expect(screen.queryByText("Criterios de búsqueda")).not.toBeInTheDocument();
    expect(screen.queryByText("Puedes filtrar por correo electrónico, nombre, apellido, rol, estado y empresa.")).not.toBeInTheDocument();
  });

  it("mantiene disponibles los campos de filtro y propaga sus cambios", async () => {
    const user = userEvent.setup();
    const { onChange } = renderDialog();

    const correo = screen.getByLabelText("Correo");
    const nombre = screen.getByLabelText("Nombre");
    const apellido = screen.getByLabelText("Apellido");
    const rol = screen.getByLabelText("Rol");
    const estado = screen.getByLabelText("Estado");
    const empresa = screen.getByLabelText("Empresa");

    expect(correo).toBeInTheDocument();
    expect(nombre).toBeInTheDocument();
    expect(apellido).toBeInTheDocument();
    expect(rol).toBeInTheDocument();
    expect(estado).toBeInTheDocument();
    expect(empresa).toBeInTheDocument();

    fireEvent.change(correo, { target: { value: "qa@inmero.co" } });
    expect(onChange).toHaveBeenLastCalledWith("username", "qa@inmero.co");

    fireEvent.change(nombre, { target: { value: "Ana" } });
    expect(onChange).toHaveBeenLastCalledWith("nombre", "Ana");

    fireEvent.change(apellido, { target: { value: "Pérez" } });
    expect(onChange).toHaveBeenLastCalledWith("apellido", "Pérez");

    await user.click(rol);
    await user.click(await screen.findByRole("option", { name: "Administrador" }));
    expect(onChange).toHaveBeenLastCalledWith("rolId", "1");

    await user.click(estado);
    await user.click(await screen.findByRole("option", { name: "Activo" }));
    expect(onChange).toHaveBeenLastCalledWith("estadoId", "1");

    await user.click(empresa);
    await user.click(await screen.findByRole("option", { name: "INMERO" }));
    expect(onChange).toHaveBeenLastCalledWith("empresaId", "1");
  });

  it("conserva las acciones Limpiar, Cancelar, Aplicar y cerrar", async () => {
    const user = userEvent.setup();
    const { onApply, onClear, onClose } = renderDialog();

    await user.click(screen.getByRole("button", { name: "Limpiar" }));
    await user.click(screen.getByRole("button", { name: "Cancelar" }));
    await user.click(screen.getByRole("button", { name: "Aplicar" }));
    await user.click(screen.getByRole("button", { name: "Cerrar" }));

    expect(onClear).toHaveBeenCalledTimes(1);
    expect(onApply).toHaveBeenCalledTimes(1);
    expect(onClose).toHaveBeenCalledTimes(2);
  });
});
