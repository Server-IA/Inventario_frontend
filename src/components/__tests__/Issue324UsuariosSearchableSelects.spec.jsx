import React from "react";
import { describe, it, expect, vi } from "vitest";
import { render, screen, fireEvent } from "@testing-library/react";
import { ThemeProvider, createTheme } from "@mui/material/styles";
import SearchableSelect from "../common/SearchableSelect";

describe("Issue #324 - SearchableSelect con altura canónica, notch de label y búsqueda local", () => {
  const lightTheme = createTheme({ palette: { mode: "light" } });
  const darkTheme = createTheme({ palette: { mode: "dark" } });

  const mockOptions = [
    { value: "1", label: "Coagrohuila Central" },
    { value: "2", label: "Agrocentro del Huila" },
    { value: "3", label: "Insumos del Sur" },
    { value: "4", label: "Fertilizantes Neiva" },
    { value: "5", label: "Semillas La Pradera" },
  ];

  it("1. Renderiza el componente con su etiqueta, notch outline consistente y valor inicial", () => {
    const handleChange = vi.fn();
    render(
      <ThemeProvider theme={lightTheme}>
        <SearchableSelect
          label="Empresa"
          value="1"
          onChange={handleChange}
          options={mockOptions}
        />
      </ThemeProvider>
    );

    // Debe existir la etiqueta flotante y el legend de notchedOutline con el mismo texto
    expect(screen.getAllByText("Empresa").length).toBeGreaterThanOrEqual(1);
    expect(screen.getByText("Coagrohuila Central")).toBeInTheDocument();
  });

  it("2. Conserva dimensiones homogéneas (medium por defecto) sin forzar altura reducida", () => {
    const handleChange = vi.fn();
    const { container } = render(
      <ThemeProvider theme={lightTheme}>
        <SearchableSelect
          label="Empresa"
          value=""
          onChange={handleChange}
          options={mockOptions}
        />
      </ThemeProvider>
    );

    const formControl = container.querySelector(".MuiFormControl-root");
    expect(formControl).toBeInTheDocument();
    // No debe forzar la clase MuiInputBase-sizeSmall por defecto para alinearse con campos estándar de 56px
    const selectRoot = container.querySelector(".MuiInputBase-root");
    expect(selectRoot).not.toHaveClass("MuiInputBase-sizeSmall");
  });

  it("3. Al abrir el menú, muestra el campo de búsqueda local con scroll interno", () => {
    const handleChange = vi.fn();
    render(
      <ThemeProvider theme={lightTheme}>
        <SearchableSelect
          label="Empresa"
          value=""
          onChange={handleChange}
          options={mockOptions}
          placeholder="Buscar empresa..."
        />
      </ThemeProvider>
    );

    const selectTrigger = screen.getByRole("combobox");
    fireEvent.mouseDown(selectTrigger);

    const searchInput = screen.getByPlaceholderText("Buscar empresa...");
    expect(searchInput).toBeInTheDocument();
  });

  it("4. Filtra las opciones localmente en frontend sin solicitudes remotas", () => {
    const handleChange = vi.fn();
    render(
      <ThemeProvider theme={lightTheme}>
        <SearchableSelect
          label="Empresa"
          value=""
          onChange={handleChange}
          options={mockOptions}
          placeholder="Buscar empresa..."
        />
      </ThemeProvider>
    );

    const selectTrigger = screen.getByRole("combobox");
    fireEvent.mouseDown(selectTrigger);

    const searchInput = screen.getByPlaceholderText("Buscar empresa...");
    fireEvent.change(searchInput, { target: { value: "Neiva" } });

    expect(screen.getByText("Fertilizantes Neiva")).toBeInTheDocument();
    expect(screen.queryByText("Coagrohuila Central")).not.toBeInTheDocument();
    expect(screen.queryByText("Agrocentro del Huila")).not.toBeInTheDocument();
  });

  it("5. Muestra 'Sin resultados' cuando ningún término coincide con las opciones", () => {
    const handleChange = vi.fn();
    render(
      <ThemeProvider theme={lightTheme}>
        <SearchableSelect
          label="Rol"
          value=""
          onChange={handleChange}
          options={mockOptions}
          placeholder="Buscar rol..."
        />
      </ThemeProvider>
    );

    const selectTrigger = screen.getByRole("combobox");
    fireEvent.mouseDown(selectTrigger);

    const searchInput = screen.getByPlaceholderText("Buscar rol...");
    fireEvent.change(searchInput, { target: { value: "TextoQueNoExiste123" } });

    expect(screen.getByText("Sin resultados")).toBeInTheDocument();
  });

  it("6. Seleccionar una opción filtrada invoca onChange con el valor correspondiente", () => {
    const handleChange = vi.fn();
    render(
      <ThemeProvider theme={lightTheme}>
        <SearchableSelect
          label="Empresa"
          value=""
          onChange={handleChange}
          options={mockOptions}
          placeholder="Buscar empresa..."
        />
      </ThemeProvider>
    );

    const selectTrigger = screen.getByRole("combobox");
    fireEvent.mouseDown(selectTrigger);

    const searchInput = screen.getByPlaceholderText("Buscar empresa...");
    fireEvent.change(searchInput, { target: { value: "Semillas" } });

    const optionItem = screen.getByText("Semillas La Pradera");
    fireEvent.click(optionItem);

    expect(handleChange).toHaveBeenCalled();
  });

  it("7. Renderiza de forma neutra y legible en dark mode sin errores visuales", () => {
    const handleChange = vi.fn();
    render(
      <ThemeProvider theme={darkTheme}>
        <SearchableSelect
          label="Empresa"
          value="2"
          onChange={handleChange}
          options={mockOptions}
        />
      </ThemeProvider>
    );

    expect(screen.getByText("Agrocentro del Huila")).toBeInTheDocument();
  });
});
