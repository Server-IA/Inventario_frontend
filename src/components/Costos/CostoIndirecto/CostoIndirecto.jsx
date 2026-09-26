/*=============================================================================
 Nombre del archivo : CostoIndirecto.jsx
 Descripcion        : Módulo Gestión de Costos Indirectos del subsistema Costos.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |   Fecha    | Versión |      Autor           | Descripción del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2026-09-25 | 0.4.0   | Arekkazu             | Creación del archivo para   |
 |            |         |                      | la HU-045.1 (solo registro).|
 | 2026-09-26 | 0.4.0   | Arekkazu             | Listado paginado con filtros|
 |            |         |                      | (HU-045.2).                 |
 +------------+---------+----------------------+-----------------------------+
=============================================================================*/
/**
 * @module CostoIndirecto
 * @description Contenedor del módulo: encabezado, barra de acciones, listado
 * paginado de `GET /v1/costos-indirectos` con filtros y modal de registro.
 */
import React, { useEffect, useMemo, useState } from "react";
import { Box } from "@mui/material";
import { useTranslation } from "react-i18next";
import axios from "../../axiosConfig";
import SectionHeader from "../../common/SectionHeader.jsx";
import GridActionBar from "../../common/GridActionBar.jsx";
import AppDataGrid from "../../common/AppDataGrid.jsx";
import MessageSnackBar from "../../MessageSnackBar.jsx";
import FormCostoIndirecto from "./FormCostoIndirecto.jsx";
import FiltroCostoIndirecto, { filtrosVacios, toParams } from "./FiltroCostoIndirecto.jsx";
import { parseServerError } from "./costoIndirectoValidation";

// El backend envía fechas `yyyy-MM-dd`; se leen como fecha local para que no se corran un día por UTC.
const fechaLocal = ({ value }) => (value ? new Date(`${value}T00:00:00`) : null);
const FORMATO_FECHA = { day: "2-digit", month: "2-digit", year: "numeric" };

export default function CostoIndirecto() {
  const { t } = useTranslation();
  const [formOpen, setFormOpen] = useState(false);
  const [filtersOpen, setFiltersOpen] = useState(false);
  const [filtros, setFiltros] = useState(filtrosVacios);
  const [paginationModel, setPaginationModel] = useState({ page: 0, size: 10 });
  const [rows, setRows] = useState([]);
  const [rowCount, setRowCount] = useState(0);
  const [loading, setLoading] = useState(false);
  const [refreshKey, setRefreshKey] = useState(0);
  const [message, setMessage] = useState({ open: false, severity: "success", text: "" });

  const params = useMemo(() => toParams(filtros), [filtros]);

  useEffect(() => {
    let cancelled = false;
    setLoading(true);
    axios
      .get("/v1/costos-indirectos", {
        params: { page: paginationModel.page, size: paginationModel.size, ...params },
      })
      .then(({ data }) => {
        if (cancelled) return;
        setRows(data?.data ?? []);
        setRowCount(data?.header?.totalElements ?? 0);
      })
      .catch((error) => {
        if (cancelled) return;
        setRows([]);
        setRowCount(0);
        setMessage({
          open: true,
          severity: "error",
          text: parseServerError(error).detail ?? t("costos.costoIndirecto.messages.listError"),
        });
      })
      .finally(() => !cancelled && setLoading(false));
    return () => {
      cancelled = true;
    };
  }, [paginationModel, params, refreshKey, t]);

  const applyFilters = (nuevos) => {
    setFiltros(nuevos);
    setPaginationModel((prev) => ({ ...prev, page: 0 }));
    setFiltersOpen(false);
  };

  const columns = useMemo(
    () => [
      { field: "tipoCostoIndirectoNombre", headerKey: "costos.costoIndirecto.columns.tipo", flex: 1, minWidth: 110 },
      { field: "sedeNombre", headerKey: "costos.costoIndirecto.columns.sede", flex: 1, minWidth: 110 },
      { field: "bloqueNombre", headerKey: "costos.costoIndirecto.columns.bloque", flex: 1, minWidth: 110 },
      { field: "espacioNombre", headerKey: "costos.costoIndirecto.columns.espacio", flex: 1, minWidth: 110 },
      { field: "almacenNombre", headerKey: "costos.costoIndirecto.columns.almacen", flex: 1, minWidth: 110 },
      {
        field: "fechaInicio",
        headerKey: "costos.costoIndirecto.columns.fechaInicio",
        type: "date",
        valueGetter: fechaLocal,
        dateFormatOptions: FORMATO_FECHA,
        width: 120,
      },
      {
        field: "fechaFin",
        headerKey: "costos.costoIndirecto.columns.fechaFin",
        type: "date",
        valueGetter: fechaLocal,
        dateFormatOptions: FORMATO_FECHA,
        width: 120,
      },
      {
        field: "valor",
        headerKey: "costos.costoIndirecto.columns.valor",
        type: "number",
        numberFormatOptions: { style: "currency", currency: "COP", maximumFractionDigits: 2 },
        width: 160,
      },
      { field: "estadoNombre", headerKey: "costos.costoIndirecto.columns.estado", statusChip: true, width: 110 },
    ],
    []
  );
  const localeText = useMemo(() => ({ noRowsLabel: t("costos.costoIndirecto.grid.empty") }), [t]);

  return (
    <Box p={2}>
      <SectionHeader titleKey="costos.costoIndirecto.title" />

      <MessageSnackBar message={message} setMessage={setMessage} />

      {/* ponytail: Actualizar/Eliminar llegan con HU-045.3 y siguientes. */}
      <GridActionBar
        onAdd={() => setFormOpen(true)}
        canUpdate={false}
        showDelete={false}
        onFilters={() => setFiltersOpen(true)}
        onClearFilters={() => applyFilters(filtrosVacios)}
        hasActiveFilters={Object.keys(params).length > 0}
      />

      <FormCostoIndirecto
        open={formOpen}
        setOpen={setFormOpen}
        setMessage={setMessage}
        onCreated={() => setRefreshKey((key) => key + 1)}
      />

      <FiltroCostoIndirecto
        open={filtersOpen}
        filtros={filtros}
        onApply={applyFilters}
        onClose={() => setFiltersOpen(false)}
      />

      <AppDataGrid
        columns={columns}
        rows={rows}
        loading={loading}
        rowCount={rowCount}
        paginationModel={paginationModel}
        setPaginationModel={setPaginationModel}
        pageSizeOptions={[5, 10, 20, 50]}
        localeText={localeText}
        containerSx={{ borderRadius: 4 }}
      />
    </Box>
  );
}
