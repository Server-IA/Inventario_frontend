// src/components/empresaRolSystem/EmpresaRolsystem.jsx
import React, { useState, useEffect, useCallback } from "react";
import axios from "../axiosConfig.js";
import { Box, Typography, Button } from "@mui/material"; // 🔥 Agregamos Button aquí
import MessageSnackBar from "../MessageSnackBar.jsx";
import ModalPermisosRol from "./ModalPermisosRol";
import FormEmpresaRolSystem from "./FormEmpresaRolsystem.jsx";
import GridEmpresaRol from "./GridEmpresaRolsystem.jsx";
import StackButtons from "../StackButtons";

export default function EmpresaRolsystem() {
  const [selectedRow, setSelectedRow] = useState(null);
  const [formOpen, setFormOpen] = useState(false);
  const [rows, setRows] = useState([]);
  const [roles, setRoles] = useState([]);
  const [loading, setLoading] = useState(false);
  const [permisosOpen, setPermisosOpen] = React.useState(false); // 🔥 Estado del nuevo modal
  const [message, setMessage] = useState({
    open: false,
    severity: "success",
    text: "",
  });

  /* ==============================
     RECARGAR GRID
  ============================== */
  const reloadData = useCallback(async () => {
    try {
      setLoading(true);
      const res = await axios.get("/v1/system/empresa-rol");
      setRows(Array.isArray(res?.data) ? res.data : []);
    } catch (error) {
      setMessage({
        open: true,
        severity: "error",
        text: "Error al cargar Empresa-Rol",
      });
    } finally {
      setLoading(false);
    }
  }, []);

  /* ==============================
     CARGAR ROLES
  ============================== */
  const loadRoles = useCallback(async () => {
    try {
      const res = await axios.get("/v1/items/rol/0");
      setRoles(Array.isArray(res?.data) ? res.data : []);
    } catch {
      setMessage({
        open: true,
        severity: "error",
        text: "Error al cargar roles",
      });
    }
  }, []);

  useEffect(() => {
    reloadData();
    loadRoles();
  }, [reloadData, loadRoles]);

  /* ==============================
     HANDLERS BOTONES
  ============================== */

  const handleCreate = () => {
    setSelectedRow(null); // 🔥 asegurar modo crear
    setFormOpen(true);
  };

  const handleEdit = () => {
    if (!selectedRow) return;

    // 🔥 FORZAR NUEVA REFERENCIA
    setSelectedRow({ ...selectedRow });
    setFormOpen(true);
  };

  const handleDelete = async () => {
    if (!selectedRow) return;

    try {
      await axios.delete(`/v1/system/empresa-rol/${selectedRow.id}`);

      setMessage({
        open: true,
        severity: "success",
        text: "Registro eliminado correctamente",
      });

      setSelectedRow(null);
      reloadData();
    } catch {
      setMessage({
        open: true,
        severity: "error",
        text: "Error al eliminar",
      });
    }
  };

  return (
    <Box sx={{ p: 3, width: "100%" }}>
      <Typography variant="h4" sx={{ mb: 3 }}>
        Gestión Empresa-Rol (System)
      </Typography>

      <MessageSnackBar message={message} setMessage={setMessage} />

      {/* CARD */}
      <Box
        sx={{
          backgroundColor: "background.paper",
          borderRadius: 2,
          p: 2,
          boxShadow: 2,
        }}
      >
        {/* BOTONES */}
        <Box sx={{ display: "flex", justifyContent: "flex-end", mb: 2 }}>
          <StackButtons
            onCreate={handleCreate}
            onEdit={handleEdit}
            onDelete={handleDelete}
            disableEdit={!selectedRow}
            disableDelete={!selectedRow}
          />

          {/* 🔥 BOTÓN NUEVO PARA ABRIR LOS PERMISOS 🔥 */}
          <Button
            variant="contained"
            color="info"
            disabled={!selectedRow}
            onClick={() => setPermisosOpen(true)}
            sx={{ ml: 2 }}
          >
            Gestionar Permisos
          </Button>
        </Box>

        {/* GRID */}
        <GridEmpresaRol
          rows={rows}
          loading={loading}
          selectedRow={selectedRow}
          setSelectedRow={setSelectedRow}
        />
      </Box>

      {/* FORMULARIO PARA CREAR/EDITAR EL ROL */}
      <FormEmpresaRolSystem
        open={formOpen}
        setOpen={setFormOpen}
        setMessage={setMessage}
        reloadData={reloadData}
        roles={roles}
        selectedRow={selectedRow}
      />

      {/* 🔥 MODAL DETALLADO DE LOS MÓDULOS Y PERMISOS 🔥 */}
      <ModalPermisosRol
        open={permisosOpen}
        setOpen={setPermisosOpen}
        // 👇 MAGIA AQUÍ: Buscamos el ID real del Rol (ej. 4), evitando usar el ID de la tabla (92)
        rolId={
          selectedRow?.rolId || 
          roles.find((r) => r.nombre === selectedRow?.rolNombre || r.name === selectedRow?.rolNombre)?.id
        }
        rolNombre={selectedRow?.rolNombre || "Rol Seleccionado"}
        setMessage={setMessage}
      />
    </Box>
  );
}