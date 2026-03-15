import React, { useEffect, useMemo, useState } from "react";
import PropTypes from "prop-types";
import {
    DataGrid,
    esES,
} from "@mui/x-data-grid";
import { KardexToolbar } from "./KardexToolbar";
import { createLookupMap, safeDateTime, formatEstado } from "./utils/kardexFormatters";

const LS_KEY = "gridKardex:columnVisibility:v1";

/**
 * @description Grid para visualizar listado de kardexes con filtros
 */
export default function GridKardex({
    // Datos
    kardexes = [],
    almacenes = [],
    producciones = [],
    tiposMovimiento = [],
    pedidos = [],
    ordenesCompra = [],
    empresas = [],

    // Selección
    selectedRow = null,
    setSelectedRow,

    // Paginación
    loading = false,
    rowCount,
    paginationModel,
    setPaginationModel,

    // Admin y filtros
    isAdmin = false,
    filters = {},
    setFilters = () => { },
}) {
    // Crear mapas lookup para mostrar nombres
    const almById = useMemo(
        () => createLookupMap(almacenes, (a) => a?.nombre || a?.name),
        [almacenes]
    );
    const prodById = useMemo(
        () => createLookupMap(producciones, (p) => p?.nombre || p?.name),
        [producciones]
    );
    const tmovById = useMemo(
        () => createLookupMap(tiposMovimiento, (t) => t?.nombre || t?.name),
        [tiposMovimiento]
    );
    const pedidoById = useMemo(
        () => createLookupMap(pedidos, (p) => p?.codigo || p?.numero || p?.nombre),
        [pedidos]
    );
    const ocById = useMemo(
        () => createLookupMap(ordenesCompra, (o) => o?.codigo || o?.numero || o?.nombre),
        [ordenesCompra]
    );
    const empresaById = useMemo(
        () => createLookupMap(empresas, (e) => e?.nombreComercial || e?.razonSocial),
        [empresas]
    );

    // Definir columnas
    const columns = useMemo(() => {
        const baseColumns = [
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
            // Columna Empresa SOLO para administrador
            ...(isAdmin
                ? [{ field: "empresaId", headerName: "Empresa", width: 120 }]
                : []),
            {
                field: "estadoId",
                headerName: "Estado",
                width: 140,
                valueGetter: (p) => formatEstado(p?.row?.estadoId),
            },
        ];
        return baseColumns;
    }, [almById, prodById, tmovById, pedidoById, ocById, empresaById, isAdmin]);

    // Gestionar visibilidad de columnas
    const [columnVisibilityModel, setColumnVisibilityModel] = useState({});

    useEffect(() => {
        try {
            const saved = JSON.parse(localStorage.getItem(LS_KEY) || "{}");
            if (saved && typeof saved === "object") {
                setColumnVisibilityModel(saved);
            }
        } catch { }
    }, []);

    const handleVisibilityChange = (model) => {
        setColumnVisibilityModel(model);
        try {
            localStorage.setItem(LS_KEY, JSON.stringify(model));
        } catch { }
    };

    const handleResetColumns = () => {
        localStorage.removeItem(LS_KEY);
        setColumnVisibilityModel({});
    };

    // Determinar modo paginación
    const serverPaging =
        typeof rowCount === "number" && paginationModel && setPaginationModel;

    const modelPage = paginationModel?.page ?? 0;
    const modelPageSize = paginationModel?.pageSize ?? paginationModel?.size ?? 10;

    return (
        <div style={{ width: "100%" }}>
            <DataGrid
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
                slotProps={{
                    toolbar: {
                        onResetColumns: handleResetColumns,
                        filters,
                        setFilters,
                        tiposMovimiento,
                    },
                }}
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
    isAdmin: PropTypes.bool,
    filters: PropTypes.object,
    setFilters: PropTypes.func,
};
