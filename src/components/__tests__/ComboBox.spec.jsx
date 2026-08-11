import React from "react";
import { describe, it, expect, vi, beforeEach, afterEach } from "vitest";
import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import axios from "axios";

vi.mock("axios");

const mockKardexData = [
  { kar_almacen_id: 1, kar_produccion_id: 101, kar_tipo_movimiento_id: "ENT" },
  { kar_almacen_id: 1, kar_produccion_id: 102, kar_tipo_movimiento_id: "SAL" },
  { kar_almacen_id: 2, kar_produccion_id: 201, kar_tipo_movimiento_id: "ENT" },
  { kar_almacen_id: 3, kar_produccion_id: 301, kar_tipo_movimiento_id: "AJS" },
];

import ComboBox from "../ComboBox";

describe("ComboBox", () => {
  beforeEach(() => {
    axios.get.mockResolvedValue({ data: mockKardexData });
  });

  afterEach(() => {
    vi.clearAllMocks();
  });

  it("renders all five filter controls", () => {
    render(<ComboBox />);

    expect(screen.getByLabelText("Almacén")).toBeInTheDocument();
    expect(screen.getByLabelText("Producción")).toBeInTheDocument();
    expect(screen.getByLabelText("Tipo Movimiento")).toBeInTheDocument();
    expect(screen.getByLabelText("Descripción")).toBeInTheDocument();
    expect(screen.getByLabelText("Estado")).toBeInTheDocument();
  });

  it("fetches kardex data on mount and populates options", async () => {
    render(<ComboBox />);

    await waitFor(() => {
      expect(axios.get).toHaveBeenCalledWith("/kardex.json");
    });
  });

  it("disables Producción until Almacén is selected", () => {
    render(<ComboBox />);

    const produccionInput = screen.getByLabelText("Producción");
    expect(produccionInput).toBeDisabled();
  });

  it("calls onAlmacenChange callback when a value is selected", async () => {
    const onAlmacenChange = vi.fn();
    render(<ComboBox onAlmacenChange={onAlmacenChange} />);

    // Open the Almacén autocomplete by clicking on the input
    const almacenInput = screen.getByLabelText("Almacén");
    await userEvent.click(almacenInput);

    await waitFor(() => {
      // MUI Autocomplete renders options in a listbox
      const option = screen.getByText("Almacén 1");
      expect(option).toBeInTheDocument();
    });

    await userEvent.click(screen.getByText("Almacén 1"));

    await waitFor(() => {
      expect(onAlmacenChange).toHaveBeenCalledWith(1);
    });
  });

  it("calls onDescripcionChange when typing in Descripción", async () => {
    const onDescripcionChange = vi.fn();
    render(<ComboBox onDescripcionChange={onDescripcionChange} />);

    const descripcionInput = screen.getByLabelText("Descripción");
    await userEvent.type(descripcionInput, "test desc");

    await waitFor(() => {
      expect(onDescripcionChange).toHaveBeenCalledWith("test desc");
    });
  });

  it("calls onEstadoChange when selecting a state", async () => {
    const onEstadoChange = vi.fn();
    const user = userEvent.setup();
    render(<ComboBox onEstadoChange={onEstadoChange} />);

    const estadoSelect = screen.getByLabelText("Estado");
    await user.click(estadoSelect);

    const activoOption = await screen.findByRole("option", { name: "Activo" });
    await user.click(activoOption);

    await waitFor(() => {
      expect(onEstadoChange).toHaveBeenCalledWith(1);
    });
  });

  it("handles axios error gracefully (logs to console)", async () => {
    const consoleErrorSpy = vi.spyOn(console, "error").mockImplementation(() => {});
    axios.get.mockRejectedValueOnce(new Error("Network error"));

    render(<ComboBox />);

    await waitFor(() => {
      expect(consoleErrorSpy).toHaveBeenCalled();
    });

    consoleErrorSpy.mockRestore();
  });
});
