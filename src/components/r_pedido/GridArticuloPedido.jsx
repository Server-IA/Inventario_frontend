import React, { useEffect, useMemo, useState } from "react";
import PropTypes from "prop-types";
import {
  DataGrid,
  esES,
  GridToolbarContainer,
  GridToolbarColumnsButton,
  GridToolbarFilterButton,
  GridToolbarDensitySelector,
  GridToolbarQuickFilter,
} from "@mui/x-data-grid";
import { Box, Button } from "@mui/material";
import RestartAltIcon from "@mui/icons-material/RestartAlt";

const LS_KEY = "gridArticuloPedido:columnVisibility:v1";

/* ---------- Toolbar personalizada ---------- */
function ArticuloToolbar({ onResetColumns }) {
  return (
    <GridToolbarContainer sx={{ p: 1, gap: 1, justifyContent: "space-between" }}>
      <div>
        <GridToolbarColumnsButton />
        <GridToolbarFilterButton />
        <GridToolbarDensitySelector />
      </div>
      <div style={{ display: "flex", alignItems: "center", gap: 12 }}>
        <GridToolbarQuickFilter debounceMs={300} />
        <Button
          variant="outlined"
          size="small"
          startIcon={<RestartAltIcon />}
          onClick={onResetColumns}
        >
          Restablecer columnas
        </Button>
      </div>
    </GridToolbarContainer>
  );
}

export default function GridArticuloPedido({
  // Datos
  items = [],
  presentaciones = [],

  // Selección (simple + múltiple)
  selectedRow = null,
  setSelectedRow = () => {},
  rowSelectionModel,
  onRowSelectionModelChange,
  setSelectedRows = () => {},

  // Paginación server-side (opcional)
  loading = false,
  rowCount,
  paginationModel, // { page, pageSize } o { page, size }
  onPaginationModelChange,
}) {
  /* -------- Lookup presentaciones por id -------- */
  const presById = useMemo(() => {
    const m = {};
    for (const p of presentaciones ?? []) {
      m[String(p?.id)] = p?.nombre ?? p?.name ?? `Presentación ${p?.id ?? ""}`;
    }
    return m;
  }, [presentaciones]);

  /* -------- Columnas -------- */
  const columns = useMemo(
    () => [
      { field: "id", headerName: "ID", width: 90, type: "number", hideable: true },
      { field: "cantidad", headerName: "Cantidad", width: 120, type: "number", hideable: true },
      { field: "pedidoId", headerName: "Pedido", width: 140, type: "number", hideable: true },
      {
        field: "presentacionProductoId",
        headerName: "Presentación de producto",
        width: 240,
        hideable: true,
        valueGetter: (p) =>
          p?.row?.presentacionProducto?.nombre ??
          p?.row?.presentacionProducto?.name ??
          presById[String(p?.row?.presentacionProductoId)] ??
          String(p?.row?.presentacionProductoId ?? ""),
      },
      {
        field: "estadoId",
        headerName: "Estado",
        width: 130,
        hideable: true,
        valueGetter: (p) =>
          p?.row?.estado?.nombre ??
          p?.row?.estado?.name ??
          (String(p?.row?.estadoId) === "1" ? "Activo" : "Inactivo"),
      },
    ],
    [presById]
  );

  /* -------- Visibilidad de columnas (persistencia) -------- */
  const [columnVisibilityModel, setColumnVisibilityModel] = useState({});

  useEffect(() => {
    try {
      const saved = JSON.parse(localStorage.getItem(LS_KEY) || "{}");
      if (saved && typeof saved === "object") setColumnVisibilityModel(saved);
    } catch {
      /* noop */
    }
  }, []);

  const handleVisibilityChange = (model) => {
    setColumnVisibilityModel(model);
    try {
      localStorage.setItem(LS_KEY, JSON.stringify(model));
    } catch {
      /* noop */
    }
  };

  const handleResetColumns = () => {
    localStorage.removeItem(LS_KEY);
    setColumnVisibilityModel({}); // todas visibles
  };

  /* -------- ¿Server o cliente? -------- */
  const serverPaging =
    typeof rowCount === "number" &&
    paginationModel &&
    typeof paginationModel.page === "number" &&
    typeof (paginationModel.pageSize ?? paginationModel.size) === "number" &&
    typeof onPaginationModelChange === "function";

  /* -------- Selección local si no viene controlada -------- */
  const handleLocalSelection = (ids) => {
    const idSet = new Set(ids);
    const selectedMany = (items ?? []).filter((r) => idSet.has(r.id));
    setSelectedRows(selectedMany);
    setSelectedRow(selectedMany[0] ?? null);
  };

  return (
    <Box sx={{ width: "100%" }}>
      <DataGrid
        rows={Array.isArray(items) ? items : []}
        columns={columns}
        getRowId={(row) => row.id}
        loading={loading}
        checkboxSelection
        // ❌ QUITAMOS ESTO PARA QUE EL CLICK EN LA FILA SÍ LA SELECCIONE
        // disableRowSelectionOnClick
        autoHeight
        pagination
        pageSizeOptions={[5, 10, 20, 50]}
        localeText={esES.components.MuiDataGrid.defaultProps.localeText}
        /* ------- selección múltiple controlada / local ------- */
        rowSelectionModel={rowSelectionModel ?? undefined}
        onRowSelectionModelChange={
          onRowSelectionModelChange ?? handleLocalSelection
        }
        onRowClick={(params) => setSelectedRow?.(params.row)}
        /* ------- columnas visibles + toolbar ------- */
        columnVisibilityModel={columnVisibilityModel}
        onColumnVisibilityModelChange={handleVisibilityChange}
        slots={{ toolbar: ArticuloToolbar }}
        slotProps={{ toolbar: { onResetColumns: handleResetColumns } }}
        /* ------- paginación ------- */
        paginationMode={serverPaging ? "server" : "client"}
        {...(serverPaging
          ? {
              rowCount: Math.max(
                Number(rowCount ?? 0),
                Array.isArray(items) ? items.length : 0
              ),
              paginationModel: {
                page: paginationModel.page ?? 0,
                pageSize: paginationModel.pageSize ?? paginationModel.size ?? 10,
              },
              onPaginationModelChange: (model) => {
                const next = {
                  page: model.page ?? 0,
                  pageSize: model.pageSize ?? 10,
                  size: model.pageSize ?? 10, // compat con padre {page,size}
                };
                onPaginationModelChange?.(next);
              },
            }
          : {
              initialState: {
                pagination: { paginationModel: { page: 0, pageSize: 5 } },
              },
            })}
      />
    </Box>
  );
}

GridArticuloPedido.propTypes = {
  items: PropTypes.array,
  presentaciones: PropTypes.array,
  selectedRow: PropTypes.object,
  setSelectedRow: PropTypes.func,
  rowSelectionModel: PropTypes.array,
  onRowSelectionModelChange: PropTypes.func,
  setSelectedRows: PropTypes.func,
  loading: PropTypes.bool,
  rowCount: PropTypes.number,
  paginationModel: PropTypes.shape({
    page: PropTypes.number,
    pageSize: PropTypes.number,
    size: PropTypes.number,
  }),
  onPaginationModelChange: PropTypes.func,
};
