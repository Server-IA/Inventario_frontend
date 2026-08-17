/*=============================================================================
 Nombre del archivo : GridEmpresa.jsx
 Descripcion        : Grilla de listado de empresas (HU-043.2).
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |   Fecha    | Versión |      Autor           | Descripción del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2026-08-16 | 0.5.0   | Jeisson Sanchez      | HU-043.2 Listado empresas   |
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

/**
 * Construye el filtro del backend a partir de los filtros del DataGrid.
 *
 * El endpoint `GET /api/v1/empresas` espera el parámetro `filtros` con un
 * objeto `EmpresaListadoFiltroDTO`:
 * `{ tipoIdentificacionId, identificacion, nombre, correo, estadoId }`.
 *
 * @param {Object} filterModel Modelo de filtros del DataGrid.
 * @returns {Object} Filtro serializable para el backend.
 */
const buildFiltros = (filterModel) => {
  const filtros = {};
  (filterModel?.items || []).forEach((item) => {
    const value = item.value;
    if (value === undefined || value === null || value === "") return;
    if (item.columnField === "estadoNombre") {
      filtros.estadoId = 1;
    } else {
      filtros[item.columnField] = value;
    }
  });
  return filtros;
};

/**
 * Componente GridEmpresa para mostrar el listado de empresas.
 *
 * Consume `GET /api/v1/empresas` con paginación, ordenamiento y filtros
 * server-side. Cada ítem expone `nombre`, `identificacion`, `correo`,
 * `estadoNombre` y `tipoIdentificacionNombre`.
 *
 * @param {object} props Propiedades del componente.
 * @param {number} [props.refreshKey] Contador que al incrementarse fuerza la
 * recarga del listado (se usa tras registrar una empresa).
 * @returns {JSX.Element}
 */
export default function GridEmpresa({ refreshKey = 0 }) {
  const { t } = useTranslation();
  const [data, setData] = React.useState([]);
  const [loading, setLoading] = React.useState(false);
  const [rowCount, setRowCount] = React.useState(0);
  const [paginationModel, setPaginationModel] = React.useState({
    pageSize: 5,
    page: 0,
  });

  const columns = [
    {
      field: "tipoIdentificacionNombre",
      headerName: t("empresa.grid.tipoIdentificacion", "Tipo de Identificación"),
      width: 180,
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
      width: 200,
      type: "string",
    },
    {
      field: "correo",
      headerName: t("empresa.grid.correo", "Correo"),
      width: 220,
      type: "string",
    },
    {
      field: "estadoNombre",
      headerName: t("empresa.grid.estado", "Estado"),
      width: 120,
      type: "string",
    },
  ];

  const fetchData = async (page, pageSize, filterModel) => {
    setLoading(true);
    try {
      const response = await axios.get("/v1/empresas", {
        params: {
          filtros: JSON.stringify(buildFiltros(filterModel)),
          page: page + 1,
          size: pageSize,
          sortBy: "id,desc",
        },
      });

      setData(response.data?.data || []);
      setRowCount(response.data?.header?.totalElements || 0);
    } catch (error) {
      console.error("Error al consultar empresas:", error);
    } finally {
      setLoading(false);
    }
  };

  React.useEffect(() => {
    fetchData(paginationModel.page, paginationModel.pageSize);
  }, [paginationModel, refreshKey]);

  return (
    <div style={{ height: 600, width: "100%" }}>
      <AppDataGrid
        rows={data || []}
        columns={columns}
        rowCount={rowCount}
        loading={loading}
        paginationModel={paginationModel}
        setPaginationModel={setPaginationModel}
        pageSizeOptions={[5, 10, 20, 50]}
        quickFilter
      />
    </div>
  );
}

GridEmpresa.propTypes = {
  refreshKey: PropTypes.number,
};