import React from "react";
import { act, render } from "@testing-library/react";
import { describe, expect, it, vi } from "vitest";
import { ThemeProvider, createTheme } from "@mui/material/styles";

const gridState = vi.hoisted(() => ({ props: null }));

vi.mock("@mui/x-data-grid", () => ({
  DataGrid: (props) => {
    gridState.props = props;
    return <div data-testid="data-grid" />;
  },
  GridToolbarContainer: () => null,
  GridToolbarColumnsButton: () => null,
  GridToolbarDensitySelector: () => null,
  GridToolbarExport: () => null,
}));

vi.mock("react-i18next", () => ({
  useTranslation: () => ({ t: (key) => key, i18n: { language: "es" } }),
}));

import AppDataGrid from "../common/AppDataGrid.jsx";

const renderGrid = (setPaginationModel, paginationModel = { page: 0, pageSize: 10 }) =>
  render(
    <ThemeProvider theme={createTheme()}>
      <AppDataGrid
        columns={[{ field: "id", headerName: "ID" }]}
        rows={[]}
        rowCount={28}
        paginationModel={paginationModel}
        setPaginationModel={setPaginationModel}
      />
    </ThemeProvider>
  );

describe("AppDataGrid server pagination", () => {
  it("preserves pageSize and legacy size when the user changes rows per page", () => {
    const setPaginationModel = vi.fn();
    renderGrid(setPaginationModel, { page: 0, pageSize: 10 });

    act(() => {
      gridState.props.onPaginationModelChange({ page: 0, pageSize: 20 });
    });

    expect(setPaginationModel).toHaveBeenCalledWith({
      page: 0,
      pageSize: 20,
      size: 20,
    });
  });

  it("keeps the page size when the grid reports only the legacy size field", () => {
    const setPaginationModel = vi.fn();
    renderGrid(setPaginationModel, { page: 1, size: 25 });

    act(() => {
      gridState.props.onPaginationModelChange({ page: 1, size: 50 });
    });

    expect(setPaginationModel).toHaveBeenCalledWith({
      page: 1,
      pageSize: 50,
      size: 50,
    });
  });

  it("falls back to the first page size option when the grid reports no size", () => {
    const setPaginationModel = vi.fn();
    renderGrid(setPaginationModel, { page: 0 });

    act(() => {
      gridState.props.onPaginationModelChange({ page: 2 });
    });

    expect(setPaginationModel).toHaveBeenCalledWith({
      page: 2,
      pageSize: 5,
      size: 5,
    });
  });

  it("exposes a valid page size to the grid from a legacy size-only model", () => {
    renderGrid(vi.fn(), { page: 0, size: 25 });

    expect(gridState.props.paginationModel).toEqual({
      page: 0,
      pageSize: 25,
    });
  });
});
