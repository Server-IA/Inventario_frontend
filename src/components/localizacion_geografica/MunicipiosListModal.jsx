/*=============================================================================
 Nombre del archivo : MunicipiosListModal.jsx
 Descripcion        : Modal que lista los municipios de un departamento.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |   Fecha    | Versión |      Autor           | Descripción del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2026-05-23 | 1.0.0   | Jeisson Sanchez      | Creación del archivo.       |
 +------------+---------+----------------------+-----------------------------+
=============================================================================*/

import React, { useState } from "react";
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  Box,
  Typography,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  IconButton,
} from "@mui/material";
import { Add, Edit, Block, FilterList, Close } from "@mui/icons-material";
import GridActionBar from "../common/GridActionBar.jsx";
import AppDataGrid from "../common/AppDataGrid.jsx";

export default function MunicipiosListModal({
  open,
  onClose,
  paisContext,
  deptoContext,
  municipios,
  onAdd,
  onEdit,
  onInactivate,
  onOpenFilter,
}) {
  const [selectedMunicipio, setSelectedMunicipio] = useState(null);

  const munColumns = React.useMemo(() => [
    { field: "codigo", headerName: "Código", flex: 1, minWidth: 100 },
    { field: "nombre", headerName: "Nombre", flex: 2, minWidth: 200 },
    { field: "acronimo", headerName: "Acrónimo", flex: 1, minWidth: 100 },
    { field: "estado", headerName: "Estado", type: "status", flex: 1, minWidth: 100 },
  ], []);

  const handleRowClick = (mun) => {
    if (selectedMunicipio?.id === mun.id) {
      setSelectedMunicipio(null);
    } else {
      setSelectedMunicipio(mun);
    }
  };

  return (
    <Dialog
      open={open}
      onClose={onClose}
      maxWidth="md"
      fullWidth
      PaperProps={{
        sx: {
          backgroundColor: "#1e1e1e",
          color: "#fff",
          border: "1px solid #555",
          borderRadius: 2,
        },
      }}
    >
      <DialogTitle sx={{ display: "flex", justifyContent: "space-between", alignItems: "center", pb: 1, borderBottom: "1px solid #333" }}>
        <Typography variant="h6">
          Municipios - {paisContext?.nombre} &gt; {deptoContext?.nombre}
        </Typography>
        <IconButton onClick={onClose} sx={{ color: "#aaa" }}>
          <Close />
        </IconButton>
      </DialogTitle>
      <DialogContent sx={{ pt: 2, pb: 2 }}>
        <GridActionBar
          onAdd={onAdd}
          onUpdate={() => onEdit(selectedMunicipio)}
          onDelete={() => onInactivate(selectedMunicipio)}
          canUpdate={!!selectedMunicipio}
          canDelete={!!selectedMunicipio}
          onFilters={onOpenFilter}
          labels={{ delete: selectedMunicipio?.estado === "Inactivo" ? "Activar" : "Inactivar" }}
        />

        <AppDataGrid
          rows={municipios}
          columns={munColumns}
          selectedRow={selectedMunicipio}
          setSelectedRow={(row) => setSelectedMunicipio(row || null)}
          containerSx={{ maxHeight: 400 }}
        />
      </DialogContent>
    </Dialog>
  );
}
