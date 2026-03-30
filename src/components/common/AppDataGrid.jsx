import React, { useMemo } from "react";
import PropTypes from "prop-types";
import { Box, Paper, Stack } from "@mui/material";
import { DataGrid, GridToolbarContainer, GridToolbarColumnsButton, GridToolbarDensitySelector, GridToolbarExport } from "@mui/x-data-grid";

export default function AppDataGrid({
  rows = [],
  columns = [],
  getRowId = (row) => row.id,
  loading = false,
  autoHeight = true,
  highlightOnHover = true,
  selectOnClick = true,
  selectedRow = null,
  setSelectedRow,
  paginationModel,
  setPaginationModel,
  rowCount,
  pageSizeOptions = [5, 10, 20, 50],
  quickFilter = false,
  columnVisibilityKey,
  columnVisibilityModel,
  onColumnVisibilityModelChange,
  localeText,
  sx,
  leftActions,
  rightActions,
  containerSx,
  onEscape,
}) {
  const serverPagination = Boolean(paginationModel && setPaginationModel && typeof rowCount === "number");

  const columnsMemo = useMemo(() => columns, [columns]);

  const Toolbar = quickFilter
    ? function Toolbar() {
        return (
          <GridToolbarContainer>
            <GridToolbarColumnsButton />
            <GridToolbarDensitySelector />
            <GridToolbarExport />
          </GridToolbarContainer>
        );
      }
    : undefined;

  const mergedSx = {
    border: 0,
    bgcolor: "grey.50",
    borderRadius: 4,
    overflow: "hidden",
    "& .MuiDataGrid-columnSeparator": { display: "none" },
    "& .MuiDataGrid-columnHeaders": {
      bgcolor: "grey.200",
      borderBottom: "none",
      borderTopLeftRadius: 4,
      borderTopRightRadius: 4,
      px: 0,
    },
    "& .MuiDataGrid-columnHeader": {
      pl: "12px !important",
      pr: "12px !important",
    },
    "& .MuiDataGrid-columnHeaderTitleContainer": {
      pl: "12px !important",
      pr: "12px !important",
      justifyContent: "flex-start",
    },
    "& .MuiDataGrid-columnHeaderTitle": {
      fontWeight: 700,
      textAlign: "left",
    },
    "& .MuiDataGrid-footerContainer": {
      borderTop: "none",
      px: 2,
      bgcolor: "grey.200",
      borderBottomLeftRadius: 4,
      borderBottomRightRadius: 4,
    },
    "& .MuiDataGrid-cell": {
      py: 1.25,
      pl: "12px !important",
      pr: "12px !important",
      borderBottom: "none",
    },
    "& .MuiDataGrid-cellContent": {
      pl: "12px !important",
      pr: "12px !important",
    },
    "& .MuiDataGrid-columnHeader.col-estado": {
      pl: "12px !important",
      pr: "12px !important",
      justifyContent: "flex-start",
    },
    "& .MuiDataGrid-cell.col-estado": {
      pl: "12px !important",
      pr: "12px !important",
    },
    "& .MuiDataGrid-row": {
      borderBottom: "none",
      position: "relative",
    },
    "& .MuiDataGrid-cell:focus, & .MuiDataGrid-cell:focus-within": {
      outline: "none",
    },
    "& .MuiDataGrid-row.Mui-selected": {
      outline: "none",
      boxShadow: "none",
      bgcolor: "rgba(47,106,245,0.08) !important",
    },
    "& .MuiDataGrid-row.Mui-selected::before": {
      content: '""',
      position: "absolute",
      left: 0,
      top: 0,
      bottom: 0,
      width: "4px",
      bgcolor: "#2F6AF5",
      borderTopLeftRadius: "4px",
      borderBottomLeftRadius: "4px",
    },
    ...(highlightOnHover ? { "& .MuiDataGrid-row:hover": { bgcolor: "action.hover" } } : {}),
    ...sx,
  };

  const handleVisibilityChange = (model) => {
    onColumnVisibilityModelChange?.(model);
    if (columnVisibilityKey) {
      try {
        localStorage.setItem(columnVisibilityKey, JSON.stringify(model));
      } catch {}
    }
  };

  return (
    <Paper sx={{ p: 0, borderRadius: 4, boxShadow: "0 4px 14px rgba(0,0,0,0.04)", border: "1px solid #ffffff", bgcolor: "transparent", ...containerSx }}>
      {(leftActions || rightActions) && (
        <Stack direction="row" alignItems="center" justifyContent="space-between" sx={{ mb: 2 }}>
          <Box sx={{ display: "flex", gap: 1 }}>{leftActions}</Box>
          <Box sx={{ display: "flex", gap: 2 }}>{rightActions}</Box>
        </Stack>
      )}
      <DataGrid
        onCellKeyDown={(params, e) => {
          if (e?.key === "Escape") {
            e.preventDefault();
            e.stopPropagation();
            setSelectedRow?.(null);
            onEscape?.();
            try {
              const active = document.activeElement;
              if (active && typeof active.blur === "function") active.blur();
            } catch {}
          }
        }}
        rows={Array.isArray(rows) ? rows : []}
        columns={columnsMemo}
        getRowId={getRowId}
        loading={loading}
        autoHeight={autoHeight}
        hideFooterSelectedRowCount
        paginationMode={serverPagination ? "server" : "client"}
        {...(serverPagination
          ? {
              paginationModel: {
                page: paginationModel.page ?? 0,
                pageSize: paginationModel.pageSize ?? paginationModel.size ?? pageSizeOptions[0] ?? 10,
              },
              onPaginationModelChange: (model) => {
                const next = {
                  page: model.page ?? 0,
                  size: model.pageSize ?? model.size ?? pageSizeOptions[0] ?? 10,
                };
                setPaginationModel?.(next);
              },
              rowCount,
            }
          : {
              pageSizeOptions,
              initialState: {
                pagination: { paginationModel: { page: 0, pageSize: pageSizeOptions[0] ?? 5 } },
              },
            })}
        onRowClick={selectOnClick ? (params) => setSelectedRow?.(params.row) : undefined}
        rowSelectionModel={
          selectOnClick && (selectedRow && getRowId ? [getRowId(selectedRow)] : selectedRow?.id ? [selectedRow.id] : [])
        }
        onRowSelectionModelChange={(selection) => {
          if (!selectOnClick) return;
          const id = Array.isArray(selection) && selection.length ? selection[0] : null;
          if (id == null) {
            setSelectedRow?.(null);
            return;
          }
          const row = (Array.isArray(rows) ? rows : []).find((r) => getRowId ? getRowId(r) === id : r?.id === id) || null;
          setSelectedRow?.(row);
        }}
        disableRowSelectionOnClick={!selectOnClick}
        slots={quickFilter ? { toolbar: Toolbar } : undefined}
        columnVisibilityModel={columnVisibilityModel}
        onColumnVisibilityModelChange={handleVisibilityChange}
        localeText={localeText}
        sx={mergedSx}
      />
    </Paper>
  );
}

AppDataGrid.propTypes = {
  rows: PropTypes.array,
  columns: PropTypes.array.isRequired,
  getRowId: PropTypes.func,
  loading: PropTypes.bool,
  autoHeight: PropTypes.bool,
  highlightOnHover: PropTypes.bool,
  selectOnClick: PropTypes.bool,
  selectedRow: PropTypes.object,
  setSelectedRow: PropTypes.func,
  paginationModel: PropTypes.shape({
    page: PropTypes.number,
    pageSize: PropTypes.number,
    size: PropTypes.number,
  }),
  setPaginationModel: PropTypes.func,
  rowCount: PropTypes.number,
  pageSizeOptions: PropTypes.array,
  quickFilter: PropTypes.bool,
  columnVisibilityKey: PropTypes.string,
  columnVisibilityModel: PropTypes.object,
  onColumnVisibilityModelChange: PropTypes.func,
  localeText: PropTypes.object,
  sx: PropTypes.object,
  leftActions: PropTypes.node,
  rightActions: PropTypes.node,
  containerSx: PropTypes.object,
  onEscape: PropTypes.func,
};
