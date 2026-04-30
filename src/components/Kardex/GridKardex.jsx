import React, { useEffect, useMemo, useState } from "react";
import PropTypes from "prop-types";
import { esES } from "@mui/x-data-grid";
import { KardexToolbar } from "./KardexToolbar";
import AppDataGrid from "../common/AppDataGrid";
import { safeDateTime, formatEstado, resolveKardexId } from "./utils/kardexFormatters";

const LS_KEY = "gridKardex:columnVisibility:v1";

export default function GridKardex({
    kardexes = [],
    tiposMovimiento = [],
    selectedRow = null,
    setSelectedRow,
    loading = false,
    rowCount,
    paginationModel,
    setPaginationModel,
    isAdmin = false,
    filters = {},
    setFilters = () => {},
}) {
    const columns = useMemo(() => {
        const baseColumns = [
            {
                field: "fechaHora",
                headerName: "Fecha y Hora",
                width: 200,
                valueGetter: (p) => safeDateTime(p?.row?.fechaHora),
            },
            {
                field: "nombreTipoMovimiento",
                headerName: "Tipo Movimiento",
                width: 220,
                valueGetter: (p) =>
                    p?.row?.nombreTipoMovimiento ??
                    p?.row?.tipoMovimiento?.name ??
                    p?.row?.tipoMovimiento?.nombre ??
                    "-",
            },
            {
                field: "nombreAlmacen",
                headerName: "Almacen",
                width: 220,
                valueGetter: (p) => p?.row?.nombreAlmacen ?? "-",
            },
            {
                field: "nombreProduccion",
                headerName: "Produccion",
                width: 220,
                valueGetter: (p) => p?.row?.nombreProduccion ?? "-",
            },
            {
                field: "nombreClienteProveedor",
                headerName: "Cliente/Proveedor",
                width: 240,
                valueGetter: (p) =>
                    p?.row?.nombreClienteProveedor ??
                    p?.row?.nombreProveedor ??
                    p?.row?.nombreCliente ??
                    "-",
            },
            {
                field: "nombreEstado",
                headerName: "Estado",
                width: 140,
                valueGetter: (p) => p?.row?.nombreEstado ?? formatEstado(p?.row?.estadoId),
            },
        ];

        if (isAdmin) {
            baseColumns.splice(5, 0, {
                field: "nombreEmpresa",
                headerName: "Empresa",
                width: 200,
                valueGetter: (p) => p?.row?.nombreEmpresa ?? "-",
            });
        }

        return baseColumns;
    }, [isAdmin]);

    const [columnVisibilityModel, setColumnVisibilityModel] = useState({});

    useEffect(() => {
        try {
            const saved = JSON.parse(localStorage.getItem(LS_KEY) || "{}");
            if (saved && typeof saved === "object") {
                setColumnVisibilityModel(saved);
            }
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

    const serverPaging = typeof rowCount === "number" && paginationModel && setPaginationModel;
    const modelPage = paginationModel?.page ?? 0;
    const modelPageSize = paginationModel?.pageSize ?? paginationModel?.size ?? 10;

    return (
        <div style={{ width: "100%" }}>
            <AppDataGrid
                rows={Array.isArray(kardexes) ? kardexes : []}
                columns={columns}
                getRowId={(row) => resolveKardexId(row) ?? `${row?.fechaHora ?? ""}-${row?.nombreTipoMovimiento ?? ""}`}
                loading={loading}
                selectedRow={selectedRow}
                setSelectedRow={setSelectedRow}
                autoHeight
                pageSizeOptions={[5, 10, 20, 50]}
                localeText={esES.components.MuiDataGrid.defaultProps.localeText}
                paginationModel={serverPaging ? { page: modelPage, size: modelPageSize } : undefined}
                setPaginationModel={serverPaging ? setPaginationModel : undefined}
                rowCount={serverPaging ? Math.max(Number(rowCount ?? 0), kardexes.length) : undefined}
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
                containerSx={{ borderRadius: 4 }}
                sx={{ minHeight: 360 }}
            />
        </div>
    );
}

GridKardex.propTypes = {
    kardexes: PropTypes.array,
    tiposMovimiento: PropTypes.array,
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
