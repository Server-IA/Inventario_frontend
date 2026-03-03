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
import { Button } from "@mui/material";
import RestartAltIcon from "@mui/icons-material/RestartAlt";
import GridBase from "../dashboard/GridBase";

const LS_KEY = "gridKardex:columnVisibility:v1";

/* ---------- Toolbar personalizada ---------- */
function KardexToolbar({ onResetColumns }) {
  return (
    <GridToolbarContainer
      sx={{ p: 1, gap: 1, justifyContent: "space-between" }}
    >
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

/* ---------- Helper fecha ---------- */
const safeDateTime = (val) => (val ? new Date(val).toLocaleString() : "");

/* =========================================================
   🔹 Componente principal
========================================================= */
export default function GridKardex({
  // Datos
  kardexes = [],
  almacenes = [],
  producciones = [],
  tiposMovimiento = [],

  // NUEVOS: datos para mostrar nombres de pedido / OC / cliente-proveedor
  pedidos = [],
  ordenesCompra = [],
  empresas = [],

  // Selección
  selectedRow = null,
  setSelectedRow,

  // Paginación (server-side opcional)
  loading = false,
  rowCount,
  paginationModel, // { page, pageSize } o { page, size }
  setPaginationModel, // (model) => void
}) {
  /* ---------- Mapas de lookup ---------- */
  const almById = useMemo(() => {
    const m = {};
    for (const a of almacenes ?? []) {
      m[String(a?.id)] = a?.name ?? a?.nombre ?? `Almacén ${a?.id ?? ""}`;
    }
    return m;
  }, [almacenes]);

  const prodById = useMemo(() => {
    const m = {};
    for (const p of producciones ?? []) {
      m[String(p?.id)] = p?.name ?? p?.nombre ?? `Producción ${p?.id ?? ""}`;
    }
    return m;
  }, [producciones]);

  const tmovById = useMemo(() => {
    const m = {};
    for (const t of tiposMovimiento ?? []) {
      m[String(t?.id)] = t?.name ?? t?.nombre ?? `Tipo ${t?.id ?? ""}`;
    }
    return m;
  }, [tiposMovimiento]);

  // 🔹 NUEVOS mapas
  const pedidoById = useMemo(() => {
    const m = {};
    for (const p of pedidos ?? []) {
      m[String(p?.id)] =
        p?.codigo ??
        p?.numero ??
        p?.name ??
        p?.nombre ??
        `Pedido ${p?.id ?? ""}`;
    }
    return m;
  }, [pedidos]);

  const ocById = useMemo(() => {
    const m = {};
    for (const o of ordenesCompra ?? []) {
      m[String(o?.id)] =
        o?.codigo ??
        o?.numero ??
        o?.name ??
        o?.nombre ??
        `OC ${o?.id ?? ""}`;
    }
    return m;
  }, [ordenesCompra]);

  const empresaById = useMemo(() => {
    const m = {};
    for (const e of empresas ?? []) {
      m[String(e?.id)] =
        e?.nombreComercial ??
        e?.razonSocial ??
        e?.name ??
        e?.nombre ??
        `Empresa ${e?.id ?? ""}`;
    }
    return m;
  }, [empresas]);

  /* ---------- Columnas ---------- */
  const columns = useMemo(
    () => [
      { field: "id", headerName: "ID", width: 90 },
      {
        field: "fechaHora",
        headerName: "Fecha/Hora",
        width: 180,
        valueGetter: (p) => safeDateTime(p?.row?.fechaHora),
      },
      {
        field: "almacenId",
        headerName: "Almacén",
        width: 200,
        valueGetter: (p) =>
          p?.row?.almacen?.name ??
          p?.row?.almacen?.nombre ??
          almById[String(p?.row?.almacenId)] ??
          String(p?.row?.almacenId ?? ""),
      },
      {
        field: "produccionId",
        headerName: "Producción",
        width: 200,
        valueGetter: (p) =>
          p?.row?.produccion?.name ??
          p?.row?.produccion?.nombre ??
          prodById[String(p?.row?.produccionId)] ??
          String(p?.row?.produccionId ?? ""),
      },
      {
        field: "tipoMovimientoId",
        headerName: "Tipo Movimiento",
        width: 220,
        valueGetter: (p) =>
          p?.row?.tipoMovimiento?.name ??
          p?.row?.tipoMovimiento?.nombre ??
          tmovById[String(p?.row?.tipoMovimientoId)] ??
          String(p?.row?.tipoMovimientoId ?? ""),
      },

      // 🔹 NUEVOS CAMPOS con nombres bonitos
      {
        field: "pedidoId",
        headerName: "Pedido",
        width: 140,
        valueGetter: (p) =>
          p?.row?.pedido?.codigo ??
          p?.row?.pedido?.numero ??
          pedidoById[String(p?.row?.pedidoId)] ??
          String(p?.row?.pedidoId ?? ""),
      },
      {
        field: "ordenCompraId",
        headerName: "Orden compra",
        width: 160,
        valueGetter: (p) =>
          p?.row?.ordenCompra?.codigo ??
          p?.row?.ordenCompra?.numero ??
          ocById[String(p?.row?.ordenCompraId)] ??
          String(p?.row?.ordenCompraId ?? ""),
      },
      {
        field: "clienteProveedorId",
        headerName: "Cliente / Proveedor",
        width: 220,
        valueGetter: (p) =>
          p?.row?.clienteProveedor?.nombre ??
          p?.row?.clienteProveedor?.nombreComercial ??
          empresaById[String(p?.row?.clienteProveedorId)] ??
          String(p?.row?.clienteProveedorId ?? ""),
      },

      { field: "descripcion", headerName: "Descripción", flex: 1, minWidth: 260 },
      { field: "empresaId", headerName: "Empresa", width: 120 },
      {
        field: "estadoId",
        headerName: "Estado",
        width: 140,
        valueGetter: (p) => {
          const state =
            p?.row?.estado?.name ??
            p?.row?.estado?.nombre ??
            p?.row?.estadoId;
          if (state === 1 || state === "1") return "Activo";
          if ([0, "0", 2, "2"].includes(state)) return "Inactivo";
          return String(state ?? "");
        },
      },
    ],
    [almById, prodById, tmovById, pedidoById, ocById, empresaById]
  );

  /* -------- Visibilidad de columnas (con persistencia) -------- */
  const [columnVisibilityModel, setColumnVisibilityModel] = useState({});

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

  /* ---------- ¿Server o Cliente? ---------- */
  const serverPaging =
    typeof rowCount === "number" &&
    paginationModel &&
    typeof paginationModel.page === "number" &&
    typeof (paginationModel.pageSize ?? paginationModel.size) === "number" &&
    typeof setPaginationModel === "function";

  const modelPage = paginationModel?.page ?? 0;
  const modelPageSize =
    paginationModel?.pageSize ?? paginationModel?.size ?? 10;

  return (
    <div style={{ width: "100%" }}>
      <GridBase
        rows={Array.isArray(kardexes) ? kardexes : []}
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
        slots={{ toolbar: KardexToolbar }}
        slotProps={{ toolbar: { onResetColumns: handleResetColumns } }}
        {...(serverPaging
          ? {
              rowCount: Math.max(
                Number(rowCount ?? 0),
                Array.isArray(kardexes) ? kardexes.length : 0
              ),
              paginationModel: { page: modelPage, pageSize: modelPageSize },
              onPaginationModelChange: (model) => {
                const next = {
                  page: model.page ?? 0,
                  pageSize: model.pageSize ?? 10,
                  size: model.pageSize ?? 10,
                };
                setPaginationModel?.(next);
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

GridKardex.propTypes = {
  kardexes: PropTypes.array,
  almacenes: PropTypes.array,
  producciones: PropTypes.array,
  tiposMovimiento: PropTypes.array,
  pedidos: PropTypes.array,
  ordenesCompra: PropTypes.array,
  empresas: PropTypes.array,
  selectedRow: PropTypes.object,
  setSelectedRow: PropTypes.func,
  paginationModel: PropTypes.shape({
    page: PropTypes.number,
    pageSize: PropTypes.number,
    size: PropTypes.number,
  }),
  setPaginationModel: PropTypes.func,
  rowCount: PropTypes.number,
  loading: PropTypes.bool,
};
