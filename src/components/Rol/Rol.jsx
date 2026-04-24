// src/components/rol/Rol.jsx
import React, { useState, useEffect, useCallback } from "react";
import axios from "../axiosConfig";
import {
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Button,
} from "@mui/material";
import MessageSnackBar from "../MessageSnackBar";
import FormRol from "./FormRol.jsx";
import GridRol from "./GridRol.jsx";
import GridActionBar from "../common/GridActionBar";
import SectionHeader from "../common/SectionHeader";

const ESTADOS_ROL = [
  { id: 1, nombre: "Activo" },
  { id: 2, nombre: "Inactivo" },
];

export default function Rol() {
  const [selectedRow, setSelectedRow] = useState(null);
  const [message, setMessage] = useState({
    open: false,
    severity: "success",
    text: "",
  });

  const [rows, setRows] = useState([]);
  const [loading, setLoading] = useState(false);
  const [formOpen, setFormOpen] = useState(false);
  const [confirmOpen, setConfirmOpen] = useState(false);

  // Traer todos los roles
  const reloadData = useCallback(async () => {
    try {
      setLoading(true);
      const res = await axios.get("/v1/roles", {
        params: { page: 0, size: 1000 },
      });

      const data = res?.data ?? [];
      const list = Array.isArray(data) ? data : data.content ?? [];

      console.log("Roles recibidos:", list.length);
      setRows(list);
    } catch (error) {
      console.error(error);
      setMessage({
        open: true,
        severity: "error",
        text: "Error al cargar roles",
      });
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    reloadData();
  }, [reloadData]);

  const handleOpenCreate = () => {
    setSelectedRow(null);
    setFormOpen(true);
  };

  const handleOpenUpdate = () => {
    if (!selectedRow?.id) {
      setMessage({
        open: true,
        severity: "warning",
        text: "Selecciona un registro",
      });
      return;
    }
    setFormOpen(true);
  };

  const handleOpenDeleteConfirm = () => {
    if (!selectedRow?.id) {
      setMessage({
        open: true,
        severity: "warning",
        text: "Selecciona un registro",
      });
      return;
    }
    setConfirmOpen(true);
  };

  const handleConfirmDelete = async () => {
    if (!selectedRow?.id) {
      setConfirmOpen(false);
      return;
    }

    try {
      await axios.delete(`/v1/roles/${selectedRow.id}`);
      setMessage({
        open: true,
        severity: "success",
        text: "Rol eliminado",
      });
      setSelectedRow(null);
      setConfirmOpen(false);
      reloadData();
    } catch {
      setMessage({
        open: true,
        severity: "error",
        text: "No se pudo eliminar el rol",
      });
      setConfirmOpen(false);
    }
  };

  return (
    <div>
      <SectionHeader title="Gestión de Rol" />

      <MessageSnackBar message={message} setMessage={setMessage} />

      <GridActionBar
        onAdd={handleOpenCreate}
        onUpdate={handleOpenUpdate}
        onDelete={handleOpenDeleteConfirm}
        canUpdate={Boolean(selectedRow?.id)}
        canDelete={Boolean(selectedRow?.id)}
      />

      <FormRol
        open={formOpen}
        setOpen={setFormOpen}
        selectedRow={selectedRow}
        setSelectedRow={setSelectedRow}
        setMessage={setMessage}
        reloadData={reloadData}
        estados={ESTADOS_ROL}
      />

      <GridRol
        rows={rows}
        loading={loading}
        selectedRow={selectedRow}
        setSelectedRow={setSelectedRow}
      />

      <Dialog open={confirmOpen} onClose={() => setConfirmOpen(false)}>
        <DialogTitle>Confirmar eliminación</DialogTitle>
        <DialogContent>
          ¿Está seguro que desea eliminar el rol
          {selectedRow?.nombre ? ` "${selectedRow.nombre}"` : " seleccionado"}?
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setConfirmOpen(false)}>Cancelar</Button>
          <Button color="error" variant="contained" onClick={handleConfirmDelete}>
            Eliminar
          </Button>
        </DialogActions>
      </Dialog>
    </div>
  );
}
