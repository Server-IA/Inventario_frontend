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
import { Box, Button, MenuItem, Select, TextField, InputLabel, FormControl } from "@mui/material";
import FilterAltIcon from "@mui/icons-material/FilterAlt";
import ClearIcon from "@mui/icons-material/Clear";

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
export default function GridEmpresa({ refreshKey = 0, selectedRow, setSelectedRow }) {
  const { t } = useTranslation();
  const [data, setData] = React.useState([]);
  const [loading, setLoading] = React.useState(false);
  const [rowCount, setRowCount] = React.useState(0);
  const [paginationModel, setPaginationModel] = React.useState({
    pageSize: 5,
    page: 0,
  });
  const [filters, setFilters] = React.useState({
    tipoIdentificacionId: "",
    identificacion: "",
    nombre: "",
    correo: "",
    estadoId: "",
  });
  const [tiposIdentificacion, setTiposIdentificacion] = React.useState([]);

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
      width: 220,
      type: "string",
    },
    {
      field: "correo",
      headerName: t("empresa.grid.correo", "Correo"),
      width: 240,
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
    async (page, pageSize) => {
      setLoading(true);
      try {
        const params = {
          page,
          size: pageSize,
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
    fetchData(paginationModel.page, paginationModel.pageSize);
  }, [fetchData, paginationModel, refreshKey]);

  React.useEffect(() => {
    axios
      .get("/v1/tipo_identificacion")
      .then((res) => setTiposIdentificacion(res.data || []))
      .catch((err) => console.error("Error al cargar tipos de identificación:", err));
  }, []);

  const handleFilterChange = (key, value) => {
    setFilters((prev) => ({ ...prev, [key]: value }));
  };

  const handleSearch = () => {
    setPaginationModel((prev) => ({ ...prev, page: 0 }));
    fetchData(0, paginationModel.pageSize);
  };

  const handleClear = () => {
    setFilters({
      tipoIdentificacionId: "",
      identificacion: "",
      nombre: "",
      correo: "",
      estadoId: "",
    });
    setPaginationModel((prev) => ({ ...prev, page: 0 }));
    fetchData(0, paginationModel.pageSize);
  };

  const filterBar = (
    <Box sx={{ display: "flex", flexWrap: "wrap", gap: 1, alignItems: "center" }}>
      <FormControl size="small" sx={{ minWidth: 180 }}>
        <InputLabel id="tipo-identificacion-filter-label">
          {t("empresa.grid.tipoIdentificacion", "Tipo de Identificación")}
        </InputLabel>
        <Select
          labelId="tipo-identificacion-filter-label"
          id="tipo-identificacion-filter"
          value={filters.tipoIdentificacionId}
          onChange={(e) => handleFilterChange("tipoIdentificacionId", e.target.value)}
          size="small"
          label={t("empresa.grid.tipoIdentificacion", "Tipo de Identificación")}
        >
          <MenuItem value="">
            <em>{t("common.labels.all", "Todos")}</em>
          </MenuItem>
          {tiposIdentificacion.map((tipo) => (
            <MenuItem key={tipo.id} value={tipo.id}>
              {tipo.nombre}
            </MenuItem>
          ))}
        </Select>
      </FormControl>
      <TextField
        size="small"
        label={t("empresa.grid.identificacion", "No. de Identificación")}
        value={filters.identificacion}
        onChange={(e) => handleFilterChange("identificacion", e.target.value)}
      />
      <TextField
        size="small"
        label={t("empresa.grid.nombre", "Nombre")}
        value={filters.nombre}
        onChange={(e) => handleFilterChange("nombre", e.target.value)}
      />
      <TextField
        size="small"
        label={t("empresa.grid.correo", "Correo")}
        value={filters.correo}
        onChange={(e) => handleFilterChange("correo", e.target.value)}
      />
      <FormControl size="small" sx={{ minWidth: 120 }}>
        <InputLabel id="estado-filter-label">{t("empresa.grid.estado", "Estado")}</InputLabel>
        <Select
          labelId="estado-filter-label"
          id="estado-filter"
          value={filters.estadoId}
          onChange={(e) => handleFilterChange("estadoId", e.target.value)}
          size="small"
          label={t("empresa.grid.estado", "Estado")}
        >
          <MenuItem value="">
            <em>{t("common.labels.all", "Todos")}</em>
          </MenuItem>
          <MenuItem value={1}>{t("common.labels.active", "Activo")}</MenuItem>
          <MenuItem value={2}>{t("common.labels.inactive", "Inactivo")}</MenuItem>
        </Select>
      </FormControl>
      <Button variant="contained" size="small" startIcon={<FilterAltIcon />} onClick={handleSearch}>
        {t("common.actions.filters", "Filtrar")}
      </Button>
      <Button variant="outlined" size="small" startIcon={<ClearIcon />} onClick={handleClear}>
        {t("common.actions.clear", "Limpiar")}
      </Button>
    </Box>
  );

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
        leftActions={filterBar}
        selectedRow={selectedRow}
        setSelectedRow={setSelectedRow}
      />
    </div>
  );
}

GridEmpresa.propTypes = {
  refreshKey: PropTypes.number,
  selectedRow: PropTypes.object,
  setSelectedRow: PropTypes.func,
};