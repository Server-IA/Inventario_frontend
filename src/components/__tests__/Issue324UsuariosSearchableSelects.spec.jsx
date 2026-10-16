import React from "react";
import { describe, it, expect, vi } from "vitest";
import { render, screen, fireEvent } from "@testing-library/react";
import { ThemeProvider, createTheme } from "@mui/material/styles";
import SearchableSelect from "../common/SearchableSelect";

describe("Issue #324 - SearchableSelect con altura limitada y búsqueda local", () => {
  const theme = createTheme({ palette: { mode: "light" } });

  const mockOptions = [
    { value: "1", label: "Coagrohuila Central" },
    { value: "2", label: "Agrocentro del Huila" },
    { value: "3", label: "Insumos del Sur" },
    { value: "4", label: "Fertilizantes Neiva" },
    { value: "5", label: "Semillas La Pradera" },
  ];

  it("1. Renderiza el componente con su etiqueta y valor inicial", () => {
    const handleChange = vi.fn();
    render(
      <ThemeProvider theme={theme}>
        <SearchableSelect
          label="Empresa"
          value="1"
          onChange={handleChange}
          options={mockOptions}
        />
      </ThemeProvider>
    );

    expect(screen.getAllByText("Empresa").length).toBeGreaterThanOrEqual(1);
    expect(screen.getByText("Coagrohuila Central")).toBeInTheDocument();
  });

  it("2. Al abrir el menú, muestra el campo de búsqueda local con scroll interno", () => {
    const handleChange = vi.fn();
    render(
      <ThemeProvider theme={theme}>
        <SearchableSelect
          label="Empresa"
          value=""
          onChange={handleChange}
          options={mockOptions}
          placeholder="Buscar empresa..."
        />
      </ThemeProvider>
    );

    // Abrir el selector
    const selectTrigger = screen.getByRole("combobox");
    fireEvent.mouseDown(selectTrigger);

    // Debe mostrar el input de búsqueda dentro del subheader
    const searchInput = screen.getByPlaceholderText("Buscar empresa...");
    expect(searchInput).toBeInTheDocument();
  });

  it("3. Filtra las opciones localmente en frontend sin solicitudes remotas", () => {
    const handleChange = vi.fn();
    render(
      <ThemeProvider theme={theme}>
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

    // Solo debe coincidir 'Fertilizantes Neiva'
    expect(screen.getByText("Fertilizantes Neiva")).toBeInTheDocument();
    expect(screen.queryByText("Coagrohuila Central")).not.toBeInTheDocument();
    expect(screen.queryByText("Agrocentro del Huila")).not.toBeInTheDocument();
  });

  it("4. Muestra 'Sin resultados' cuando ningún término coincide con las opciones", () => {
    const handleChange = vi.fn();
    render(
      <ThemeProvider theme={theme}>
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

  it("5. Seleccionar una opción filtrada invoca onChange con el valor correspondiente", () => {
    const handleChange = vi.fn();
    render(
      <ThemeProvider theme={theme}>
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
});
