import React, { useMemo, useCallback } from "react";
import PropTypes from "prop-types";
import { Box, Button } from "@mui/material";
import {
  DataGrid,
  GridToolbarContainer,
  GridToolbarFilterButton,
  esES,
} from "@mui/x-data-grid";
import axios from "../axiosConfig";

function CustomToolbar() {
  return (
    <GridToolbarContainer>
      <GridToolbarFilterButton />
    </GridToolbarContainer>
  );
}

export default function GridOrdenCompra({
  ordenes = [],
  rowCount,
  loading = false,
  paginationModel,
  setPaginationModel,
  sortModel,
  setSortModel,
  setFilterModel,
  setSelectedRow,
  proveedoresMap = {},
  setMessage = () => {},
  reloadData = () => {},
}) {
  const safeDateTime = (val) => (val ? new Date(val).toLocaleString() : "");

  /* =============== TOGGLE ESTADO 23 ↔ 24 CON PUT =============== */
  const handleEnviarOrdenCompra = useCallback(
    (row) => {
      if (!row?.id) {
        setMessage({
          open: true,
          severity: "error",
          text: "No se encontró el ID de la orden de compra.",
        });
        return;
      }

      const estadoActual = Number(row?.estadoId ?? row?.estado?.id);

      // Solo permitimos toggle entre ACTIVO (23) y ENTREGADO (24)
      if (estadoActual !== 23 && estadoActual !== 24) {
        setMessage({
          open: true,
          severity: "warning",
          text: "Solo se puede cambiar entre estados ACTIVO y ENTREGADO AL PROVEEDOR.",
        });
        return;
      }

      const esActivo = estadoActual === 23;
      const nuevoEstadoId = esActivo ? 24 : 23;
      const textoAccion = esActivo
        ? "enviada al proveedor"
        : "devuelta a estado ACTIVO";

      const url = `/v1/orden-compra/${row.id}`;

      const payload = {
        fechaHora: row.fechaHora,
        pedidoId: Number(row.pedidoId),
        proveedorId: Number(row.proveedorId),
        descripcion: row.descripcion ?? "",
        estadoId: nuevoEstadoId,
      };

      axios
        .put(url, payload)
        .then(() => {
          setMessage({
            open: true,
            severity: "success",
            text: `Orden de compra ${textoAccion} con éxito.`,
          });
          reloadData?.();
        })
        .catch((error) => {
          const errorMessage = error.response?.data?.message || error.message;
          setMessage({
            open: true,
            severity: "error",
            text: `Error al cambiar estado: ${errorMessage}`,
          });
        });
    },
    [setMessage, reloadData]
  );

  /* ================== COLUMNAS ================== */
  const columns = useMemo(() => {
    const proveedorValueGetter = ({ row }) => {
      const provId =
        row?.proveedorId ??
        row?.proveedor_id ??
        row?.proveedorIdFk ??
        row?.proveedor?.id ??
        null;

      const provName =
        row?.proveedorName ??
        row?.proveedor_name ??
        row?.proveedor?.name ??
        (provId != null ? proveedoresMap[Number(provId)] : undefined);

      return provName ?? String(provId ?? "");
    };

    const estadoValueGetter = ({ row }) => {
      const rawId = row?.estadoId ?? row?.estado?.id;
      if (rawId == null) return "";
      const id = Number(rawId);

      switch (id) {
        case 23:
          return "Activo";
        case 24:
          return "Entregado al proveedor";
        case 25:
          return "Entrega Parcial";
        case 26:
          return "Entrada total";
        default:
          return `Estado ${id}`;
      }
    };

    return [
      { field: "id", headerName: "ID", width: 80, type: "number" },
      {
        field: "fechaHora",
        headerName: "Fecha y Hora",
        width: 200,
        valueGetter: ({ row }) => safeDateTime(row?.fechaHora),
      },
      { field: "pedidoId", headerName: "Pedido", width: 120, type: "number" },
      {
        field: "proveedor",
        headerName: "Proveedor",
        width: 220,
        valueGetter: proveedorValueGetter,
      },
      {
        field: "descripcion",
        headerName: "Descripción",
        flex: 1,
        minWidth: 260,
      },
      {
        field: "estadoId",
        headerName: "Estado",
        width: 190,
        valueGetter: estadoValueGetter,
      },
      {
        field: "acciones",
        headerName: "Acciones",
        width: 250,
        sortable: false,
        filterable: false,
        renderCell: (params) => {
          const row = params.row;
          const estadoId = Number(row?.estadoId ?? row?.estado?.id);

          const esActivo = estadoId === 23;
          const esEntregado = estadoId === 24;
          const habilitado = esActivo || esEntregado;

          const label = esActivo
            ? "ENVIAR AL PROVEEDOR"
            : esEntregado
            ? "VOLVER A ACTIVO"
            : "CAMBIAR ESTADO";

          const color = esActivo
            ? "primary" // azul
            : esEntregado
            ? "warning" // amarillo
            : "inherit"; // gris

          return (
            <Button
              variant="contained"
              size="small"
              disabled={!habilitado}
              color={color}
              onClick={(e) => {
                e.stopPropagation();
                handleEnviarOrdenCompra(row);
              }}
              sx={{
                fontWeight: "bold",
                color: esEntregado ? "#000" : "white",
              }}
            >
              {label}
            </Button>
          );
        },
      },
    ];
  }, [proveedoresMap, handleEnviarOrdenCompra]);

  const serverPagination =
    paginationModel && setPaginationModel && typeof rowCount === "number";

  return (
    <Box sx={{ width: "100%", overflowX: "auto" }}>
      <DataGrid
        rows={Array.isArray(ordenes) ? ordenes : []}
        columns={columns}
        getRowId={(row) => row.id}
        onRowClick={(params) => setSelectedRow?.(params.row)}
        // 🔹 IMPORTANTE: quitamos disableRowSelectionOnClick
        // disableRowSelectionOnClick
        localeText={esES.components.MuiDataGrid.defaultProps.localeText}
        pagination
        pageSizeOptions={[5, 10, 20, 50]}
        slots={{ toolbar: CustomToolbar }}
        loading={loading}
        paginationMode={serverPagination ? "server" : "client"}
        {...(serverPagination
          ? {
              rowCount: Math.max(
                Number(rowCount ?? 0),
                Array.isArray(ordenes) ? ordenes.length : 0
              ),
              paginationModel: {
                page: paginationModel.page ?? 0,
                pageSize: paginationModel.pageSize ?? paginationModel.size ?? 10,
              },
              onPaginationModelChange: (model) => {
                const next = {
                  page: model.page ?? 0,
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
        sortingMode={setSortModel ? "server" : "client"}
        sortModel={sortModel}
        onSortModelChange={setSortModel}
        filterMode={setFilterModel ? "server" : "client"}
        onFilterModelChange={setFilterModel}
        autoHeight
        sx={{ minWidth: 720 }}
      />
    </Box>
  );
}

GridOrdenCompra.propTypes = {
  ordenes: PropTypes.array,
  rowCount: PropTypes.number,
  loading: PropTypes.bool,
  paginationModel: PropTypes.object,
  setPaginationModel: PropTypes.func,
  sortModel: PropTypes.array,
  setSortModel: PropTypes.func,
  setFilterModel: PropTypes.func,
  setSelectedRow: PropTypes.func.isRequired,
  proveedoresMap: PropTypes.object,
  setMessage: PropTypes.func,
  reloadData: PropTypes.func,
};
