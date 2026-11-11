import React from "react";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { ThemeProvider, createTheme } from "@mui/material/styles";
import axios from "../axiosConfig";

vi.mock("../axiosConfig", () => ({
  default: { get: vi.fn(), delete: vi.fn() },
}));

vi.mock("react-i18next", () => ({
  useTranslation: () => ({
    t: (key, fallback) => (typeof fallback === "string" ? fallback : key),
  }),
}));

vi.mock("../common/CrudFilterModal", () => ({
  default: ({ open, titleKey, summaryCardSx }) =>
    open ? (
      <div data-testid="sede-filter-modal">
        <span>{titleKey}</span>
        {summaryCardSx ? <span>{titleKey}</span> : null}
      </div>
    ) : null,
}));

vi.mock("../common/GridActionBar", () => ({
  default: ({ onFilters }) => <button onClick={onFilters}>Abrir filtros</button>,
}));
vi.mock("../common/SectionHeader", () => ({ default: () => null }));
vi.mock("../MessageSnackBar", () => ({ default: () => null }));
vi.mock("../sede/FormSede", () => ({ default: () => null }));
vi.mock("../sede/GridSede", () => ({ default: () => null }));

import Sede from "../sede/Sede.jsx";

const theme = createTheme({ palette: { mode: "light" } });

describe("Sede - modal de filtros", () => {
  beforeEach(() => {
    vi.clearAllMocks();
    axios.get.mockResolvedValue({ data: [] });
  });

  it("no envía la tarjeta que repetía el título del filtro", async () => {
    const user = userEvent.setup();

    render(
      <ThemeProvider theme={theme}>
        <Sede />
      </ThemeProvider>
    );

    await waitFor(() => {
      expect(axios.get).toHaveBeenCalledWith("/v1/sede", expect.any(Object));
    });

    await user.click(screen.getByRole("button", { name: "Abrir filtros" }));

    expect(screen.getAllByText("sede.filters.title")).toHaveLength(1);
  });
});
