import React, { useEffect, useMemo, useState } from "react";
import PropTypes from "prop-types";
import { esES } from "@mui/x-data-grid";
import { Box } from "@mui/material";
import AppDataGrid from "../common/AppDataGrid";
import { resolveArticuloKardexId } from "./utils/kardexFormatters";

const LS_KEY = "gridArticuloKardex:columnVisibility:v1";

export default function GridArticuloKardex({
  items = [],
  presentaciones = [],
  productos = [],
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
  const productNameById = useMemo(() => {
    const m = {};
    for (const p of productos ?? []) {
      if (p?.id == null) continue;
      m[String(p.id)] = p?.nombre ?? p?.name ?? p?.descripcion ?? "";
    }
    return m;
  }, [productos]);

  const pickRow = (arg1, arg2) => {
    if (arg2 && typeof arg2 === "object" && !Array.isArray(arg2)) return arg2;
    if (arg1?.row && typeof arg1.row === "object") return arg1.row;
    if (arg1 && typeof arg1 === "object" && !Array.isArray(arg1) && arg1.id !== undefined) return arg1;
    return {};
  };

  const getProductoIdFromPresentacion = (pr) =>
    pr?.producto?.id ??
    pr?.productoId ??
    pr?.idProducto ??
    pr?.productoID ??
    pr?.prpProductoId ??
    pr?.producto_id ??
    pr?.proId ??
    null;

  const productByPresentacionId = useMemo(() => {
    const m = {};
    for (const pr of presentaciones ?? []) {
      const pid = getProductoIdFromPresentacion(pr);
      const producto =
        pr?.producto?.nombre ??
        pr?.producto?.name ??
        pr?.productoNombre ??
        pr?.nombreProducto ??
        (pid != null ? productNameById[String(pid)] : "");
      if (pr?.id != null) m[String(pr.id)] = producto;
    }
    return m;
  }, [presentaciones, productNameById]);

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
      valueGetter: (arg1, arg2) => {
        const row = pickRow(arg1, arg2);
        return (
        productByPresentacionId[
          String(
            row?.presentacionProductoId ??
              row?.presentacion_producto_id ??
              row?.idPresentacionProducto ??
              ""
          )
        ] ??
        row?.productoNombre ??
        row?.nombreProducto ??
        row?.identificadorProducto ??
        `Producto #${
          row?.presentacionProductoId ??
          row?.presentacion_producto_id ??
          row?.idPresentacionProducto ??
          resolveArticuloKardexId(row) ??
          ""
        }`
        );
      },
    },
    { field: "cantidad", headerName: "Cantidad", width: 120 },
    { field: "precio", headerName: "Precio", width: 120 },
    { field: "lote", headerName: "Lote", width: 160 },
    {
      field: "fechaVencimiento",
      headerName: "Fecha Vencimiento",
      width: 170,
      valueGetter: (arg1, arg2) => {
        const row = pickRow(arg1, arg2);
        return (row?.fechaVencimiento || "").toString().substring(0, 10);
      },
    },
    {
      field: "estado",
      headerName: "Estado",
      width: 140,
      valueGetter: (arg1, arg2) => {
        const row = pickRow(arg1, arg2);
        return (
        row?.estadoNombre ??
        row?.estado?.name ??
        row?.estado?.nombre ??
        (String(row?.estadoId) === "1" ? "Activo" : "Inactivo")
        );
      },
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

  const serverPaging =
    typeof rowCount === "number" &&
    paginationModel &&
    typeof paginationModel.page === "number" &&
    typeof (paginationModel.pageSize ?? paginationModel.size) === "number" &&
    typeof onPaginationModelChange === "function";

  const rowId = (r) =>
    resolveArticuloKardexId(r) ??
    r?.kardexItemId ??
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
        containerSx={{ borderRadius: 4 }}
      />
    </Box>
  );
}

GridArticuloKardex.propTypes = {
  items: PropTypes.array,
  presentaciones: PropTypes.array,
  productos: PropTypes.array,
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
