// src/components/Kardex/GridArticuloKardex.jsx
import React, { useMemo } from "react";
import { DataGrid, esES } from "@mui/x-data-grid";
import { Box } from "@mui/material";

export default function GridArticuloKardex({
  // Datos
  items = [],
  presentaciones = [],

  // Selección
  selectedRow,
  setSelectedRow,

  // Contexto
  kardexId,

  // Paginación server-side (opcional)
  paginationModel,        // { page, size } o { page, pageSize }
  setPaginationModel,     // (model) => void
  rowCount,               // total en servidor
  loading = false,
}) {
  /* ---------- Mapa presentaciones ---------- */
  const presById = useMemo(() => {
    const m = {};
    for (const pr of presentaciones ?? []) {
      const composed = [pr?.producto?.nombre, pr?.presentacion?.nombre]
        .filter(Boolean)
        .join(" · ");
      const label = pr?.name ?? pr?.nombre ?? (composed || `Presentación ${pr?.id ?? ""}`);
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

  const serverPagination = Boolean(
    paginationModel && setPaginationModel && typeof rowCount === "number"
  );

  return (
    <Box sx={{ width: "100%" }}>
      <DataGrid
        rows={filteredRows}
        columns={columns}
        getRowId={(row) => row.id}
        onRowClick={(params) => setSelectedRow?.(params.row)}
        rowSelectionModel={selectedRow ? [selectedRow.id] : []}

        // Paginación visible siempre
        pagination
        pageSizeOptions={[5, 10, 20, 50]}
        localeText={esES.components.MuiDataGrid.defaultProps.localeText}

        paginationMode={serverPagination ? "server" : "client"}
        loading={loading}
        {...(serverPagination
          ? {
              rowCount: Math.max(
                Number(rowCount ?? 0),
                Array.isArray(filteredRows) ? filteredRows.length : 0
              ),
              paginationModel: {
                page: paginationModel.page ?? 0,
                pageSize:
                  paginationModel.pageSize ??
                  paginationModel.size ??
                  10,
              },
              onPaginationModelChange: (model) => {
                const next = {
                  page: model.page ?? 0,
                  size: model.pageSize ?? model.size ?? 10,
                };
                setPaginationModel?.(next);
              },
            }
          : {
              initialState: {
                pagination: { paginationModel: { page: 0, pageSize: 10 } },
              },
            })}
        autoHeight
      />
    </Box>
  );
}
