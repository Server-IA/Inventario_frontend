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
import GridBase from "../dashboard/GridBase";

const LS_KEY = "gridArticuloKardex:columnVisibility:v1";

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

export default function GridArticuloKardex({
  // Datos
  items = [],
  presentaciones = [],
  kardexId,
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
  /* ---------- Mapa presentaciones ---------- */
  const presById = useMemo(() => {
    const m = {};
    for (const pr of presentaciones ?? []) {
      const composed = [pr?.producto?.nombre, pr?.presentacion?.nombre]
        .filter(Boolean)
        .join(" · ");
      const label =
        pr?.name ?? pr?.nombre ?? (composed || `Presentación ${pr?.id ?? ""}`);
      if (pr?.id != null) m[String(pr.id)] = label;
    }
    return m;
  }, [presentaciones]);

  /* ---------- Filtro defensivo por kardexId ---------- */
  const filteredRows = useMemo(() => {
    if (!kardexId) return Array.isArray(items) ? items : [];
    const id = String(kardexId);
    const src = Array.isArray(items) ? items : [];
    return src.filter((it) => {
      const k =
        it?.kardexId ??
        it?.kardex_id ??
        it?.karId ??
        it?.kar_id ??
        it?.kdxId ??
        it?.kdx_id;
      return String(k ?? "") === id;
    });
  }, [items, kardexId]);

  /* ---------- Columnas ---------- */
  const columns = [
    { field: "id", headerName: "ID", width: 90 },
    { field: "cantidad", headerName: "Cantidad", width: 120 },
    { field: "precio", headerName: "Precio", width: 120 },
    {
      field: "fechaVencimiento",
      headerName: "Vence",
      width: 150,
      valueGetter: (params) =>
        (params?.row?.fechaVencimiento || "").toString().substring(0, 10),
    },
    {
      field: "identificadorProducto",
      headerName: "Identificador producto",
      width: 260,
      valueGetter: (params) => params?.row?.identificadorProducto ?? "",
    },
    { field: "kardexId", headerName: "Kardex ID", width: 120, hide: true },
    {
      field: "presentacionProductoId",
      headerName: "Presentación",
      width: 260,
      valueGetter: (p) =>
        p?.row?.presentacionProducto?.nombre ??
        p?.row?.presentacionProducto?.name ??
        presById[String(p?.row?.presentacionProductoId)] ??
        String(p?.row?.presentacionProductoId ?? ""),
    },
    {
      field: "estadoId",
      headerName: "Estado",
      width: 140,
      valueGetter: (params) =>
        params?.row?.estado?.name ??
        params?.row?.estado?.nombre ??
        (String(params?.row?.estadoId) === "1" ? "Activo" : "Inactivo"),
    },
  ];

  /* -------- Visibilidad de columnas (persistencia) -------- */
  const [columnVisibilityModel, setColumnVisibilityModel] = useState({});

  // Cargar de localStorage al montar
  useEffect(() => {
    try {
      const saved = JSON.parse(localStorage.getItem(LS_KEY) || "{}");
      if (saved && typeof saved === "object") setColumnVisibilityModel(saved);
    } catch {
      /* noop */
    }
  }, []);

  // Guardar cada cambio
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
    setColumnVisibilityModel({}); // todas visibles por defecto
  };

  /* -------- ¿Server o cliente? -------- */
  const serverPaging =
    typeof rowCount === "number" &&
    paginationModel &&
    typeof paginationModel.page === "number" &&
    typeof (paginationModel.pageSize ?? paginationModel.size) === "number" &&
    typeof onPaginationModelChange === "function";

  /* -------- Selección no controlada (fallback) -------- */
  const handleLocalSelection = (ids) => {
    const idSet = new Set(ids);
    const selectedMany = (filteredRows ?? []).filter((r) => idSet.has(r.id));
    setSelectedRows(selectedMany);
    setSelectedRow(selectedMany[0] ?? null);
  };

  return (
    <Box sx={{ width: "100%" }}>
      <GridBase
        rows={Array.isArray(filteredRows) ? filteredRows : []}
        columns={columns}
        getRowId={(row) => row.id}
        loading={loading}
        checkboxSelection
        disableRowSelectionOnClick
        autoHeight
        pagination
        pageSizeOptions={[5, 10, 20, 50]}
        localeText={esES.components.MuiDataGrid.defaultProps.localeText}
        /* ------- selección múltiple controlada / local ------- */
        rowSelectionModel={rowSelectionModel ?? undefined}
        onRowSelectionModelChange={onRowSelectionModelChange ?? handleLocalSelection}
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
                Array.isArray(filteredRows) ? filteredRows.length : 0
              ),
              paginationModel: {
                page: paginationModel.page ?? 0,
                pageSize:
                  paginationModel.pageSize ?? paginationModel.size ?? 10,
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

GridArticuloKardex.propTypes = {
  items: PropTypes.array,
  presentaciones: PropTypes.array,
  kardexId: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
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
