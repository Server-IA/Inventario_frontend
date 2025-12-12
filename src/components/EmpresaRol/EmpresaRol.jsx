import React, { useEffect, useState } from "react";
import axios from "../axiosConfig";
import { Box, Stack, Typography, Button } from "@mui/material";
import AddRounded from "@mui/icons-material/AddRounded";

import GridEmpresaRol from "./GridEmpresaRol.jsx";
import FormEmpresaRol from "./FormEmpresaRol.jsx";
import MessageSnackBar from "../MessageSnackBar";

const toArray = (data) =>
  Array.isArray(data)
    ? data
    : Array.isArray(data?.content)
    ? data.content
    : Array.isArray(data?.data)
    ? data.data
    : [];

export default function EmpresaRol() {
  const [rows, setRows] = useState([]);
  const [roles, setRoles] = useState([]);
  const [loading, setLoading] = useState(false);

  const [openForm, setOpenForm] = useState(false);

  const [message, setMessage] = useState({
    open: false,
    severity: "success",
    text: "",
  });

  const [columnVisibilityModel, setColumnVisibilityModel] = useState({
    id: true,
    empresaNombre: true,
    rolNombre: true,
    estadoNombre: true,
    acciones: true,
  });

  /* =========== Cargar datos =========== */
  const loadEmpresaRoles = async () => {
    try {
      setLoading(true);
      // SIN /api, igual que UsuarioRol
      const resp = await axios.get("v1/empresa-rol", {
        params: { page: 0, size: 1000 },
      });
      setRows(toArray(resp.data));
    } catch (error) {
      console.error("Error cargando empresa-rol", error);
      setMessage({
        open: true,
        severity: "error",
        text: "Error cargando empresa-rol",
      });
    } finally {
      setLoading(false);
    }
  };

  const loadRoles = async () => {
    try {
      // Catálogo de roles sin /api
      const resp = await axios.get("v1/items/rol/0");
      setRoles(toArray(resp.data));
    } catch (error) {
      console.error("Error cargando roles", error);
      setMessage({
        open: true,
        severity: "error",
        text: "Error cargando la lista de roles",
      });
    }
  };

  useEffect(() => {
    loadEmpresaRoles();
    loadRoles();
  }, []);

  /* =========== Handlers =========== */

  const handleOpenForm = () => {
    setOpenForm(true);
  };

  const handleCloseForm = () => {
    setOpenForm(false);
  };

  const handleCreate = async (rolId) => {
    if (!rolId) {
      setMessage({
        open: true,
        severity: "warning",
        text: "Debes seleccionar un rol",
      });
      return;
    }

    try {
      // SIN /api
      await axios.post("v1/empresa-rol", { rolId });

      setMessage({
        open: true,
        severity: "success",
        text: "Rol asignado a la empresa correctamente",
      });

      handleCloseForm();
      loadEmpresaRoles();
    } catch (error) {
      console.error("Error creando empresa-rol", error);
      setMessage({
        open: true,
        severity: "error",
        text: "Error al asignar el rol a la empresa",
      });
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm("¿Seguro que deseas eliminar este registro?")) return;

    try {
      // SIN /api
      await axios.delete(`v1/empresa-rol/${id}`);
      setMessage({
        open: true,
        severity: "success",
        text: "Registro eliminado correctamente",
      });
      loadEmpresaRoles();
    } catch (error) {
      console.error("Error eliminando empresa-rol", error);
      setMessage({
        open: true,
        severity: "error",
        text: "Error al eliminar el registro",
      });
    }
  };

  const handleResetColumns = () => {
    setColumnVisibilityModel({
      id: true,
      empresaNombre: true,
      rolNombre: true,
      estadoNombre: true,
      acciones: true,
    });
  };

  /* =========== Render =========== */

  return (
    <Box sx={{ p: 2 }}>
      <Stack
        direction="row"
        justifyContent="space-between"
        alignItems="center"
        mb={2}
      >
        <Typography variant="h6">Empresa – Roles</Typography>

        <Button
          variant="contained"
          startIcon={<AddRounded />}
          onClick={handleOpenForm}
        >
          Asignar rol a empresa
        </Button>
      </Stack>

      <MessageSnackBar message={message} setMessage={setMessage} />

      <GridEmpresaRol
        rows={rows}
        loading={loading}
        onDelete={handleDelete}
        columnVisibilityModel={columnVisibilityModel}
        setColumnVisibilityModel={setColumnVisibilityModel}
        onResetColumns={handleResetColumns}
      />

      <FormEmpresaRol
        open={openForm}
        onClose={handleCloseForm}
        roles={roles}
        onSubmit={handleCreate}
      />
    </Box>
  );
}
