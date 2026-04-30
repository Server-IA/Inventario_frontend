import React, { useEffect, useMemo, useState } from "react";
import PropTypes from "prop-types";
import {
  esES,
  GridToolbarContainer,
  GridToolbarColumnsButton,
  GridToolbarFilterButton,
  GridToolbarDensitySelector,
  GridToolbarQuickFilter,
} from "@mui/x-data-grid";
import { Box, Button } from "@mui/material";
import RestartAltIcon from "@mui/icons-material/RestartAlt";
import AppDataGrid from "../common/AppDataGrid";
import { resolveArticuloKardexId } from "./utils/kardexFormatters";

const LS_KEY = "gridArticuloKardex:columnVisibility:v1";

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
        <Button variant="outlined" size="small" startIcon={<RestartAltIcon />} onClick={onResetColumns}>
          Restablecer columnas
        </Button>
      </div>
    </GridToolbarContainer>
  );
}

export default function GridArticuloKardex({
  items = [],
  presentaciones = [],
  kardexId,
  selectedRow = null,
  setSelectedRow = () => {},
  rowSelectionModel,
  onRowSelectionModelChange,
  setSelectedRows = () => {},
  loading = false,
  rowCount,
  paginationModel,
  onPaginationModelChange,
}) {
  const presById = useMemo(() => {
    const m = {};
    for (const pr of presentaciones ?? []) {
      const producto = pr?.producto?.nombre ?? pr?.producto?.name ?? pr?.productoNombre ?? "";
      const presentacion =
        pr?.presentacion?.nombre ??
        pr?.presentacion?.name ??
        pr?.presentacionNombre ??
        pr?.nombre ??
        pr?.name ??
        "";
      const label = [producto, presentacion].filter(Boolean).join(" - ");
      if (pr?.id != null) m[String(pr.id)] = label;
    }
    return m;
  }, [presentaciones]);

  const filteredRows = useMemo(() => {
    if (!kardexId) return Array.isArray(items) ? items : [];
    const id = String(kardexId);
    const src = Array.isArray(items) ? items : [];
    const hasKardexRef = src.some((it) => it?.kardexId != null || it?.kardex_id != null);
    if (!hasKardexRef) return src;
    return src.filter((it) => String(it?.kardexId ?? it?.kardex_id ?? "") === id);
  }, [items, kardexId]);

  const columns = [
    {
      field: "producto",
      headerName: "Producto",
      flex: 1,
      minWidth: 220,
      valueGetter: (params) =>
        params?.row?.productoNombre ??
        params?.row?.nombreProducto ??
        params?.row?.identificadorProducto ??
        presById[String(params?.row?.presentacionProductoId)] ??
        `Presentacion #${params?.row?.presentacionProductoId ?? resolveArticuloKardexId(params?.row) ?? ""}`,
    },
    { field: "cantidad", headerName: "Cantidad", width: 120 },
    { field: "precio", headerName: "Precio", width: 120 },
    { field: "lote", headerName: "Lote", width: 160 },
    {
      field: "fechaVencimiento",
      headerName: "Fecha Vencimiento",
      width: 170,
      valueGetter: (params) => (params?.row?.fechaVencimiento || "").toString().substring(0, 10),
    },
    {
      field: "estado",
      headerName: "Estado",
      width: 140,
      valueGetter: (params) =>
        params?.row?.estadoNombre ??
        params?.row?.estado?.name ??
        params?.row?.estado?.nombre ??
        (String(params?.row?.estadoId) === "1" ? "Activo" : "Inactivo"),
    },
  ];

  const [columnVisibilityModel, setColumnVisibilityModel] = useState({});

  useEffect(() => {
    try {
      const saved = JSON.parse(localStorage.getItem(LS_KEY) || "{}");
      if (saved && typeof saved === "object") setColumnVisibilityModel(saved);
    } catch {
      // noop
    }
  }, []);

  const handleVisibilityChange = (model) => {
    setColumnVisibilityModel(model);
    try {
      localStorage.setItem(LS_KEY, JSON.stringify(model));
    } catch {
      // noop
    }
  };

  const handleResetColumns = () => {
    localStorage.removeItem(LS_KEY);
    setColumnVisibilityModel({});
  };

  const serverPaging =
    typeof rowCount === "number" &&
    paginationModel &&
    typeof paginationModel.page === "number" &&
    typeof (paginationModel.pageSize ?? paginationModel.size) === "number" &&
    typeof onPaginationModelChange === "function";

  const rowId = (r) =>
    resolveArticuloKardexId(r) ??
    `${r?.kardexId ?? kardexId ?? ""}-${r?.presentacionProductoId ?? ""}-${r?.lote ?? ""}`;

  const handleLocalSelection = (ids) => {
    const idSet = new Set(ids);
    const selectedMany = (filteredRows ?? []).filter((r) => idSet.has(rowId(r)));
    setSelectedRows(selectedMany);
    setSelectedRow(selectedMany[0] ?? null);
  };

  return (
    <Box sx={{ width: "100%" }}>
      <AppDataGrid
        rows={Array.isArray(filteredRows) ? filteredRows : []}
        columns={columns}
        getRowId={rowId}
        loading={loading}
        selectedRow={selectedRow}
        setSelectedRow={setSelectedRow}
        checkboxSelection
        rowSelectionModel={rowSelectionModel ?? undefined}
        onRowSelectionModelChange={onRowSelectionModelChange ?? handleLocalSelection}
        pageSizeOptions={[5, 10, 20, 50]}
        localeText={esES.components.MuiDataGrid.defaultProps.localeText}
        paginationModel={
          serverPaging
            ? { page: paginationModel.page ?? 0, size: paginationModel.pageSize ?? paginationModel.size ?? 10 }
            : undefined
        }
        setPaginationModel={
          serverPaging
            ? (next) => {
                onPaginationModelChange?.({
                  page: next.page ?? 0,
                  pageSize: next.size ?? next.pageSize ?? 10,
                  size: next.size ?? next.pageSize ?? 10,
                });
              }
            : undefined
        }
        rowCount={serverPaging ? Math.max(Number(rowCount ?? 0), filteredRows.length) : undefined}
        columnVisibilityModel={columnVisibilityModel}
        onColumnVisibilityModelChange={handleVisibilityChange}
        slots={{ toolbar: ArticuloToolbar }}
        slotProps={{ toolbar: { onResetColumns: handleResetColumns } }}
        containerSx={{ borderRadius: 4 }}
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
