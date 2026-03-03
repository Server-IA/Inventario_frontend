import React, { useMemo } from "react";
import { DataGrid } from "@mui/x-data-grid";
import { Box } from "@mui/material";
import GridBase from "../dashboard/GridBase";

export default function GridUsuarioRol({
  rows = [],
  selectedRow,
  setSelectedRow,
  paginationModel,
  setPaginationModel,
  rowCount,
  loading
}) {
  const columns = useMemo(() => ([
    { field: "id", headerName: "ID", width: 60 },
    { field: "usuarioId", headerName: "UID Ref", width: 80 },
    { field: "personaNombreCompleto", headerName: "Nombre", width: 220 },
    { field: "usuarioEmail", headerName: "Email", width: 220 },
    
    // Mostramos rolNombre que ya viene del backend
    { field: "rolNombre", headerName: "Rol", width: 200 },
    
    // Estado con estilo condicional básico (Texto Verde/Rojo)
    { 
      field: "estadoNombre", 
      headerName: "Estado", 
      width: 100,
      renderCell: (params) => (
        <span style={{ 
          fontWeight: 'bold', 
          color: params.row.estadoId === 1 ? 'green' : 'red' 
        }}>
          {params.value}
        </span>
      )
    },
    
    // Formato de fechas
    { 
      field: "iniciaContratoEn", 
      headerName: "Inicio Contrato", 
      width: 130,
      valueFormatter: (params) => {
        if (!params.value) return "";
        return new Date(params.value).toLocaleDateString();
      }
    },
    { 
      field: "finalizaContratoEn", 
      headerName: "Fin Contrato", 
      width: 130,
      valueFormatter: (params) => {
        if (!params.value) return "";
        return new Date(params.value).toLocaleDateString();
      }
    }
  ]), []);

  return (
    <Box sx={{ width: "100%", height: 500 }}>
      <GridBase
        rows={rows}
        columns={columns}
        getRowId={(row) => row.id}

        // Selección de fila
        onRowClick={(params) => setSelectedRow(params.row)}
        rowSelectionModel={selectedRow?.id ? [selectedRow.id] : []}
        
        // Configuración Servidor
        paginationMode="server"
        rowCount={rowCount}
        loading={loading}
        paginationModel={paginationModel}
        onPaginationModelChange={setPaginationModel}
        pageSizeOptions={[5, 10, 20]}
      />
    </Box>
  );
}