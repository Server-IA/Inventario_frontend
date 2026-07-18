/*=============================================================================
 Nombre del archivo : GridEmpresa.jsx
 Descripcion        : GridEmpresa componente principal.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |   Fecha    | Versión |      Autor           | Descripción del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2026-07-18 | 0.5.0   | Jeisson Sanchez      | HU-043 Gestión de Empresas  |
 +------------+---------+----------------------+-----------------------------+
=============================================================================*/
/**
 * GridEmpresa componente principal.
 * @module GridEmpresa
 * @component
 * @returns {JSX.Element}
 */

import * as React from 'react';
import { useTranslation } from 'react-i18next';
import AppDataGrid from '../common/AppDataGrid';
import axios from 'axios';
import { SiteProps } from "../dashboard/SiteProps";

/**
 * @typedef {Object} EmpresaRow
 * @property {number} id
 * @property {string} nombre
 * @property {string} descripcion
 * @property {number} estado
 * @property {string} celular
 * @property {string} correo
 * @property {string} contacto
 * @property {number} tipoIdentificacionId
 * @property {number} personaId
 * @property {string} identificacion
 * @property {string} logo // ✅ nuevo campo
 */

/**
 * @typedef {Object} GridEmpresaProps
 * @property {EmpresaRow} selectedRow - La fila seleccionada actualmente.
 * @property {function} setSelectedRow - Función para establecer la fila seleccionada.
 * @property {EmpresaRow[]} empresas - Lista de empresas disponibles.
 */

/**
 * Componente GridEmpresa para mostrar la tabla de empresas.
 * @param {GridEmpresaProps & { personas: Array, tiposIdentificacion: Array }} props
 * @returns {JSX.Element}
 */
export default function GridEmpresa({ selectedRow, setSelectedRow, empresas, personas = [], tiposIdentificacion = [] }) {
  const { t } = useTranslation();
  const [data, setData] = React.useState([]);
  const [loading, setLoading] = React.useState(false);
  const [rowCount, setRowCount] = React.useState(0);
  const [sortModel, setSortModel] = React.useState([]);
  const [filterModel, setFilterModel] = React.useState({ items: [] });

  const [paginationModel, setPaginationModel] = React.useState({
    pageSize: 5,
    page: 0,
  });

  const columns = [
    { field: 'id', headerName: t('empresa.grid.id', 'ID'), width: 90, type: 'number' },
    { field: 'nombre', headerName: t('empresa.grid.nombre', 'Nombre'), width: 150, type: 'string' },
    { field: 'descripcion', headerName: t('empresa.grid.descripcion', 'Descripción'), width: 250, type: 'string' },
    {
      field: 'estado',
      headerName: t('empresa.grid.estado', 'Estado'),
      width: 100,
      type: 'string',
      valueGetter: (params) => params.row.estado === 1 ? t('empresa.estado.activo', 'Activo') : t('empresa.estado.inactivo', 'Inactivo')
    },
    { field: 'celular', headerName: t('empresa.grid.celular', 'Celular'), width: 100, type: 'string' },
    { field: 'correo', headerName: t('empresa.grid.correo', 'Correo'), width: 150, type: 'string' },
    { field: 'contacto', headerName: t('empresa.grid.contacto', 'Contacto'), width: 150, type: 'string' },
    { 
      field: 'tipoIdentificacionId', 
      headerName: t('empresa.grid.tipoIdentificacion', 'Tipo de Identificación'), 
      width: 150, 
      type: 'string',
      valueGetter: (params) => {
        const tipo = tiposIdentificacion.find(t => t.id === params.row.tipoIdentificacionId);
        return tipo ? tipo.name : params.row.tipoIdentificacionId;
      }
    },
    { 
      field: 'personaId', 
      headerName: t('empresa.grid.persona', 'Persona'), 
      width: 150, 
      type: 'string',
      valueGetter: (params) => {
        const persona = personas.find(p => p.id === params.row.personaId);
        return persona ? `${persona.nombre} ${persona.apellido}` : params.row.personaId;
      }
    },
    { field: 'identificacion', headerName: t('empresa.grid.identificacion', 'No. de Identificación'), width: 150, type: 'string' },

    // ✅ NUEVO: columna para el logo
    {
      field: 'logo',
      headerName: t('empresa.grid.logo', 'Logo'),
      width: 100,
      renderCell: (params) =>
        params.value ? (
          <img
            src={params.value}
            alt={t('empresa.grid.logoAlt', 'logo')}
            style={{ maxHeight: 40, maxWidth: "100%", objectFit: "contain" }}
          />
        ) : (
          t('empresa.grid.sinLogo', 'Sin logo')
        ),
    }
  ];

  const fetchData = async (page, pageSize, sortModel, filterModel) => {
    setLoading(true);
    try {
      const baseURL = `${SiteProps.urlbasev1}/empresas`;

      const filterParams = filterModel.items.length > 0 ? {
        [filterModel.items[0]?.columnField]: filterModel.items[0]?.value
      } : {};

      const response = await axios.get(baseURL, {
        params: {
          page: page + 1,
          size: pageSize,
          sortBy: sortModel[0]?.field || '',
          sortDirection: sortModel[0]?.sort || 'asc',
          ...filterParams
        },
      });

      setData(response.data?.data || []);
      setRowCount(response.data?.header?.totalElements || 0);
    } catch (error) {
      console.error('Error fetching data:', error);
    } finally {
      setLoading(false);
    }
  };

  React.useEffect(() => {
    fetchData(paginationModel.page, paginationModel.pageSize, sortModel, filterModel);
  }, [paginationModel, sortModel, filterModel]);

  const handlePaginationModelChange = (model) => {
    setPaginationModel(model);
    fetchData(model.page, model.pageSize, sortModel, filterModel);
  };

  return (
    <div style={{ height: 600, width: '100%' }}>
      <AppDataGrid
        rows={data || []}
        columns={columns}
        rowCount={rowCount}
        loading={loading}
        paginationMode="server"
        paginationModel={paginationModel}
        onPaginationModelChange={handlePaginationModelChange}
        sortingMode="server"
        onSortModelChange={(model) => setSortModel(model)}
        filterMode="server"
        onFilterModelChange={(model) => setFilterModel(model)}
        pageSizeOptions={[5, 10, 20, 50]}
        selectedRow={selectedRow}
        setSelectedRow={setSelectedRow}
      />
    </div>
  );
}
