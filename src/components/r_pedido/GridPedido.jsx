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
import { Button, Stack } from "@mui/material";
import RestartAltIcon from "@mui/icons-material/RestartAlt";
import GridBase from "../dashboard/GridBase";

const LS_KEY = "gridPedido:columnVisibility:v1";

/* ---------- Toolbar personalizada ---------- */
function PedidoToolbar({ onResetColumns }) {
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

export default function GridPedido({
  // Datos
  pedidos = [],
  producciones = [],
  almacenes = [],
  estados = [],

  // Selección
  selectedRow = null,
  setSelectedRow,

  // Acciones
  onAnularPedido,        // (pedido) => void   -> PUT /pedido/anular
  onCompletarPedido,     // (pedido) => void   -> PUT /pedido/completar

  // Paginación server-side (opcional)
  loading = false,
  rowCount,
  paginationModel,               // { page, pageSize } o { page, size }
  onPaginationModelChange,       // (next) => void
}) {
  /* -------- Lookups -------- */
  const prodById = useMemo(() => {
    const m = {};
    for (const p of producciones ?? []) {
      m[String(p?.id)] = p?.name ?? p?.nombre ?? `Producción ${p?.id ?? ""}`;
    }
    return m;
  }, [producciones]);

  const almById = useMemo(() => {
    const m = {};
    for (const a of almacenes ?? []) {
      m[String(a?.id)] = a?.name ?? a?.nombre ?? `Almacén ${a?.id ?? ""}`;
    }
    return m;
  }, [almacenes]);

  const estById = useMemo(() => {
    const m = {};
    for (const e of estados ?? []) {
      m[String(e?.id)] = e?.name ?? e?.nombre ?? `Estado ${e?.id ?? ""}`;
    }
    return m;
  }, [estados]);

  const safeDateTime = (v) => (v ? new Date(v).toLocaleString() : "");

  /* -------- Columnas -------- */
  const columns = useMemo(
    () => [
      { field: "id", headerName: "ID", width: 80, type: "number", hideable: true },
      { field: "descripcion", headerName: "Descripción", flex: 1.2, minWidth: 220, hideable: true },
      {
        field: "fechaHora",
        headerName: "Fecha y Hora",
        width: 200,
        valueGetter: (p) => safeDateTime(p?.row?.fechaHora),
        hideable: true,
      },
      {
        field: "produccionId",
        headerName: "Producción",
        width: 220,
        valueGetter: (p) =>
          p?.row?.produccion?.nombre ??
          p?.row?.produccion?.name ??
          prodById[String(p?.row?.produccionId)] ??
          String(p?.row?.produccionId ?? ""),
        hideable: true,
      },
      {
        field: "almacenId",
        headerName: "Almacén",
        width: 220,
        valueGetter: (p) =>
          p?.row?.almacen?.nombre ??
          p?.row?.almacen?.name ??
          almById[String(p?.row?.almacenId)] ??
          String(p?.row?.almacenId ?? ""),
        hideable: true,
      },
      {
        field: "estadoId",
        headerName: "Estado",
        width: 180,
        valueGetter: (p) => {
          const id = p?.row?.estado?.id ?? p?.row?.estadoId;
          return (
            p?.row?.estado?.nombre ??
            p?.row?.estado?.name ??
            estById[String(id)] ??
            String(id ?? "")
          );
        },
        hideable: true,
      },
      /* -------- Columna de acciones (Anular / Completar) -------- */
      {
        field: "acciones",
        headerName: "Acciones",
        width: 260,
        sortable: false,
        filterable: false,
        disableColumnMenu: true,
        renderCell: (params) => {
          const row = params.row;

          const handleAnularClick = (e) => {
            e.stopPropagation(); // que no seleccione la fila
            onAnularPedido?.(row);
          };

          const handleCompletarClick = (e) => {
            e.stopPropagation();
            onCompletarPedido?.(row);
          };

          // Aquí podrías deshabilitar según el estado si quieres:
          // const estado = row.estado?.codigo || row.estado?.nombre || "";
          // const isAnulado = estado.toUpperCase() === "ANULADO";
          // const isCompleto = estado.toUpperCase() === "COMPLETADO";

          return (
            <Stack direction="row" spacing={1}>
              <Button
                variant="outlined"
                size="small"
                color="error"
                onClick={handleAnularClick}
                // disabled={isAnulado || isCompleto}
              >
                Anular
              </Button>
              <Button
                variant="contained"
                size="small"
                color="success"
                onClick={handleCompletarClick}
                // disabled={isCompleto || isAnulado}
              >
                Completar
              </Button>
            </Stack>
          );
        },
      },
    ],
    [prodById, almById, estById, onAnularPedido, onCompletarPedido]
  );

  /* -------- Visibilidad de columnas (con persistencia) -------- */
  const [columnVisibilityModel, setColumnVisibilityModel] = useState({});

  // Cargar de localStorage al montar
  useEffect(() => {
    try {
      const saved = JSON.parse(localStorage.getItem(LS_KEY) || "{}");
      if (saved && typeof saved === "object") {
        setColumnVisibilityModel(saved);
      }
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
    setColumnVisibilityModel({});
  };

  /* -------- ¿Server o cliente? -------- */
  const serverPaging =
    typeof rowCount === "number" &&
    paginationModel &&
    typeof paginationModel.page === "number" &&
    typeof (paginationModel.pageSize ?? paginationModel.size) === "number" &&
    typeof onPaginationModelChange === "function";

  const modelPage = paginationModel?.page ?? 0;
  const modelPageSize = paginationModel?.pageSize ?? paginationModel?.size ?? 10;

  return (
    <div style={{ width: "100%" }}>
      <GridBase
        rows={Array.isArray(pedidos) ? pedidos : []}
        columns={columns}
        getRowId={(row) => row.id}
        loading={loading}
        autoHeight
        pagination
        pageSizeOptions={[5, 10, 20, 50]}
        disableRowSelectionOnClick
        rowSelectionModel={selectedRow?.id ? [selectedRow.id] : []}
        onRowClick={(params) => setSelectedRow?.(params.row)}
        localeText={esES.components.MuiDataGrid.defaultProps.localeText}
        paginationMode={serverPaging ? "server" : "client"}
        columnVisibilityModel={columnVisibilityModel}
        onColumnVisibilityModelChange={handleVisibilityChange}
        slots={{ toolbar: PedidoToolbar }}
        slotProps={{ toolbar: { onResetColumns: handleResetColumns } }}
        {...(serverPaging
          ? {
              rowCount: Math.max(
                Number(rowCount ?? 0),
                Array.isArray(pedidos) ? pedidos.length : 0
              ),
              paginationModel: { page: modelPage, pageSize: modelPageSize },
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
                pagination: { paginationModel: { page: 0, pageSize: 10 } },
              },
            })}
      />
    </div>
  );
}

GridPedido.propTypes = {
  pedidos: PropTypes.array,
  producciones: PropTypes.array,
  almacenes: PropTypes.array,
  estados: PropTypes.array,
  selectedRow: PropTypes.object,
  setSelectedRow: PropTypes.func.isRequired,

  onAnularPedido: PropTypes.func,
  onCompletarPedido: PropTypes.func,

  // Server-side opcional
  loading: PropTypes.bool,
  rowCount: PropTypes.number,
  paginationModel: PropTypes.shape({
    page: PropTypes.number,
    pageSize: PropTypes.number,
    size: PropTypes.number,
  }),
  onPaginationModelChange: PropTypes.func,
};
