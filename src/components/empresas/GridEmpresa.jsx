/*=============================================================================
 Nombre del archivo : GridEmpresa.jsx
 Descripcion        : Grilla de listado de empresas (HU-043.2).
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |   Fecha    | Versión |      Autor           | Descripción del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2026-08-16 | 0.5.0   | Jeisson Sanchez      | HU-043.2 Listado empresas   |
 | 2026-08-16 | 0.5.1   | Jeisson Sanchez      | QA: filtros flat + page 0-based |
 +------------+---------+----------------------+-----------------------------+
=============================================================================*/
/**
 * Grilla de listado de empresas.
 * @module GridEmpresa
 * @component
 * @returns {JSX.Element}
 */

import * as React from "react";
import { useTranslation } from "react-i18next";
import PropTypes from "prop-types";
import axios from "../axiosConfig";
import AppDataGrid from "../common/AppDataGrid";
import { Box } from "@mui/material";

/**
 * Componente GridEmpresa para mostrar el listado de empresas.
 *
 * Consume `GET /api/v1/empresas` con paginación (0-based) y filtros
 * server-side mediante query params planos (nombre, identificacion,
 * correo, tipoIdentificacionId, estadoId). Cada ítem expone `nombre`,
 * `identificacion`, `correo`, `estadoNombre` y `tipoIdentificacionNombre`.
 *
 * @param {object} props Propiedades del componente.
 * @param {number} [props.refreshKey] Contador que al incrementarse fuerza la
 * recarga del listado (se usa tras registrar una empresa).
 * @param {Object|null} [props.selectedRow] Fila seleccionada externamente.
 * @param {function} [props.setSelectedRow] Setter para la selección externa.
 * @returns {JSX.Element}
 */
export default function GridEmpresa({ refreshKey = 0, selectedRow, setSelectedRow, filters = {} }) {
  const { t } = useTranslation();
  const [data, setData] = React.useState([]);
  const [loading, setLoading] = React.useState(false);
  const [rowCount, setRowCount] = React.useState(0);
  const [paginationModel, setPaginationModel] = React.useState({
    size: 5,
    page: 0,
  });

  const columns = [
    {
      field: "tipoIdentificacionNombre",
      headerName: t("empresa.grid.tipoIdentificacion", "Tipo de Identificación"),
      width: 190,
      type: "string",
    },
    {
      field: "identificacion",
      headerName: t("empresa.grid.identificacion", "No. de Identificación"),
      width: 180,
      type: "string",
    },
    {
      field: "nombre",
      headerName: t("empresa.grid.nombre", "Nombre"),
      flex: 1,
      minWidth: 200,
      type: "string",
    },
    {
      field: "correo",
      headerName: t("empresa.grid.correo", "Correo"),
      flex: 1,
      minWidth: 200,
      type: "string",
    },
    {
      field: "estadoNombre",
      headerName: t("empresa.grid.estado", "Estado"),
      width: 120,
      type: "string",
    },
  ];

  const fetchData = React.useCallback(
    async (page, size) => {
      setLoading(true);
      try {
        const params = {
          page,
          size,
          sortBy: "id,desc",
        };
        Object.entries(filters).forEach(([key, value]) => {
          if (value !== undefined && value !== null && value !== "") {
            params[key] = value;
          }
        });

        const response = await axios.get("/v1/empresas", { params });
        setData(response.data?.data || []);
        setRowCount(response.data?.header?.totalElements || 0);
      } catch (error) {
        console.error("Error al consultar empresas:", error);
      } finally {
        setLoading(false);
      }
    },
    [filters]
  );

  React.useEffect(() => {
    fetchData(paginationModel.page, paginationModel.size);
  }, [fetchData, paginationModel, refreshKey]);

  return (
    <Box sx={{ width: "100%", marginTop: 2, minHeight: 400 }}>
      <AppDataGrid
        rows={data}
        columns={columns}
        loading={loading}
        rowCount={rowCount}
        paginationModel={paginationModel}
        setPaginationModel={setPaginationModel}
        pageSizeOptions={[5, 10, 20, 50]}
        selectedRow={selectedRow}
        setSelectedRow={setSelectedRow}
        autoHeight
      />
    </Box>
  );
}

GridEmpresa.propTypes = {
  refreshKey: PropTypes.number,
  selectedRow: PropTypes.object,
  setSelectedRow: PropTypes.func,
  filters: PropTypes.object,
};