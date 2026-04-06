import React, { useEffect, useMemo, useState } from "react";
import PropTypes from "prop-types";
import { DataGrid, esES } from "@mui/x-data-grid";
import { KardexToolbar } from "./KardexToolbar";
import { createLookupMap, safeDateTime, formatEstado } from "./utils/kardexFormatters";

const LS_KEY = "gridKardex:columnVisibility:v1";

/**
 * @description Grid para visualizar listado de kardexes con filtros
 */
export default function GridKardex({
    kardexes = [],
    almacenes = [],
    producciones = [],
    tiposMovimiento = [],
    pedidos = [],
    ordenesCompra = [],
    empresas = [],
    selectedRow = null,
    setSelectedRow,
    loading = false,
    rowCount,
    paginationModel,
    setPaginationModel,
    isAdmin = false,
    filters = {},
    setFilters = () => { },
}) {
    const getClienteProveedorId = (row) =>
        row?.clienteProveedorId ??
        row?.cliente_proveedor_id ??
        row?.cliProId ??
        row?.cli_pro_id ??
        row?.proveedorId ??
        row?.proveedor_id ??
        row?.clienteId ??
        row?.cliente_id ??
        row?.empresaClienteProveedorId ??
        row?.clienteProveedor?.id ??
        row?.proveedor?.id ??
        row?.cliente?.id ??
        row?.clienteProveedor?.empresaId ??
        null;

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
    const empresaById = useMemo(
        () => createLookupMap(empresas, (e) => e?.nombreComercial || e?.razonSocial || e?.nombre || e?.name),
        [empresas]
    );

    const columns = useMemo(() => {
        const baseColumns = [
            {
                field: "fechaHora",
                headerName: "Fecha y Hora",
                width: 180,
                valueGetter: (p) => safeDateTime(p?.row?.fechaHora),
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
                field: "almacenId",
                headerName: "Almacen",
                width: 200,
                valueGetter: (p) =>
                    p?.row?.almacen?.name ??
                    p?.row?.almacen?.nombre ??
                    almById[String(p?.row?.almacenId)] ??
                    String(p?.row?.almacenId ?? ""),
            },
            {
                field: "produccionId",
                headerName: "Produccion",
                width: 200,
                valueGetter: (p) =>
                    p?.row?.produccion?.name ??
                    p?.row?.produccion?.nombre ??
                    prodById[String(p?.row?.produccionId)] ??
                    String(p?.row?.produccionId ?? ""),
            },
            {
                field: "clienteProveedorId",
                headerName: "Cliente/Proveedor",
                width: 220,
                valueGetter: (p) => {
                    const row = p?.row ?? {};
                    const clienteProveedorId = getClienteProveedorId(row);
                    return (
                        row?.clienteProveedorNombre ??
                        row?.clienteProveedorName ??
                        row?.cliente_proveedor_nombre ??
                        row?.cliente_proveedor_name ??
                        row?.clienteProveedor?.razonSocial ??
                        row?.clienteProveedor?.nombre ??
                        row?.clienteProveedor?.nombreComercial ??
                        row?.clienteProveedor?.name ??
                        row?.proveedor?.razonSocial ??
                        row?.proveedor?.nombre ??
                        row?.proveedor?.nombreComercial ??
                        row?.proveedor?.name ??
                        row?.cliente?.razonSocial ??
                        row?.cliente?.nombre ??
                        row?.cliente?.nombreComercial ??
                        row?.cliente?.name ??
                        (typeof row?.clienteProveedor === "string" ? row?.clienteProveedor : null) ??
                        empresaById[String(clienteProveedorId)] ??
                        (clienteProveedorId != null ? `#${clienteProveedorId}` : "")
                    );
                },
            },
            ...(isAdmin ? [{ field: "empresaId", headerName: "Empresa", width: 140 }] : []),
            {
                field: "estadoId",
                headerName: "Estado",
                width: 140,
                valueGetter: (p) => formatEstado(p?.row?.estadoId),
            },
        ];
        return baseColumns;
    }, [almById, prodById, tmovById, empresaById, isAdmin]);

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

