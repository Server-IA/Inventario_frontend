/*=============================================================================
 Nombre del archivo : GridSede.jsx
 Descripcion        : Grilla estandar para la gestion de sedes.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |   Fecha    | Versión |      Autor           | Descripción del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2026-09-21 | 0.4.0   | Cesar Medina         | Migra la grilla a           |
 |            |         |                      | AppDataGrid con i18n.       |
 +------------+---------+----------------------+-----------------------------+
=============================================================================*/

import React, { useMemo } from "react";
import PropTypes from "prop-types";
import AppDataGrid from "../common/AppDataGrid";

export default function GridSede({
  sedes = [],
  selectedRow = null,
  setSelectedRow,
  loading = false,
}) {
  const columns = useMemo(
    () => [
      { field: "id", headerKey: "sede.columns.id", type: "number", width: 80 },
      {
        field: "nombre",
        headerKey: "sede.columns.name",
        type: "text",
        minWidth: 220,
        flex: 1,
      },
      {
        field: "paisNombre",
        headerKey: "sede.columns.country",
        type: "text",
        minWidth: 160,
        flex: 0.8,
      },
      {
        field: "departamentoNombre",
        headerKey: "sede.columns.department",
        type: "text",
        minWidth: 180,
        flex: 0.9,
      },
      {
        field: "municipioNombre",
        headerKey: "sede.columns.municipality",
        type: "text",
        minWidth: 180,
        flex: 0.9,
        valueGetter: (p) =>
          p?.row?.municipioNombre ??
          p?.row?.municipio?.name ??
          p?.row?.municipio?.nombre ??
          String(p?.row?.municipioId ?? ""),
      },
      {
        field: "grupoNombre",
        headerKey: "sede.columns.group",
        type: "text",
        minWidth: 180,
        flex: 0.8,
        valueGetter: (p) =>
          p?.row?.grupoNombre ??
          p?.row?.grupo?.name ??
          p?.row?.grupo?.nombre ??
          String(p?.row?.grupoId ?? ""),
      },
      {
        field: "tipoSedeNombre",
        headerKey: "sede.columns.type",
        type: "text",
        minWidth: 180,
        flex: 0.9,
        valueGetter: (p) =>
          p?.row?.tipoSedeNombre ??
          p?.row?.tipoSede?.name ??
          p?.row?.tipoSede?.nombre ??
          String(p?.row?.tipoSedeId ?? ""),
      },
      {
        field: "geolocalizacion",
        headerKey: "sede.columns.geolocation",
        type: "text",
        width: 160,
      },
      {
        field: "coordenadas",
        headerKey: "sede.columns.coordinates",
        type: "text",
        width: 170,
      },
      { field: "area", headerKey: "sede.columns.area", type: "number", width: 120 },
      {
        field: "comuna",
        headerKey: "sede.columns.commune",
        type: "number",
        width: 120,
      },
      {
        field: "descripcion",
        headerKey: "sede.columns.description",
        type: "text",
        flex: 1.1,
        minWidth: 260,
      },
      {
        field: "estadoId",
        headerKey: "sede.columns.status",
        type: "status",
        width: 140,
        valueGetter: (p) =>
          p?.row?.estado?.name ??
          p?.row?.estado?.nombre ??
          p?.row?.estadoNombre ??
          p?.row?.estadoId,
      },
    ],
    []
  );

  return (
    <AppDataGrid
      rows={sedes}
      columns={columns}
      selectedRow={selectedRow}
      setSelectedRow={setSelectedRow}
      columnVisibilityKey="gridSede:columnVisibility:v2"
      quickFilter={false}
      containerSx={{ borderRadius: 4 }}
      loading={loading}
    />
  );
}

GridSede.propTypes = {
  sedes: PropTypes.array,
  selectedRow: PropTypes.object,
  setSelectedRow: PropTypes.func.isRequired,
  loading: PropTypes.bool,
};
