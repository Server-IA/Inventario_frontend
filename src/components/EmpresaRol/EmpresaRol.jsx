import React, { useEffect, useState } from "react";
import axios from "../axiosConfig";
import {
  Box,
  Stack,
  Typography,
  Button,
  Snackbar,
  Alert,
} from "@mui/material";
import AddRounded from "@mui/icons-material/AddRounded";

import GridEmpresaRol from "./GridEmpresaRol.jsx";
import FormEmpresaRol from "./FormEmpresaRol.jsx";

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

  const [snack, setSnack] = useState({
    open: false,
    severity: "success",
    message: "",
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
      // 🔴 SIN /api
      const resp = await axios.get("/v1/empresa-rol");
      setRows(toArray(resp.data));
    } catch (error) {
      console.error("Error cargando empresa-rol", error);
      setSnack({
        open: true,
        severity: "error",
        message: "Error cargando empresa-rol",
      });
    } finally {
      setLoading(false);
    }
  };

  const loadRoles = async () => {
    try {
      // Ajusta este endpoint si tu catálogo de roles es distinto
      const resp = await axios.get("/v1/items/rol/0"); // 🔴 SIN /api
      setRoles(toArray(resp.data));
    } catch (error) {
      console.error("Error cargando roles", error);
      setSnack({
        open: true,
        severity: "error",
        message: "Error cargando la lista de roles",
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
      setSnack({
        open: true,
        severity: "warning",
        message: "Debes seleccionar un rol",
      });
      return;
    }

    try {
      // 🔴 SIN /api
      await axios.post("/v1/empresa-rol", { rolId });

      setSnack({
        open: true,
        severity: "success",
        message: "Rol asignado a la empresa correctamente",
      });

      handleCloseForm();
      loadEmpresaRoles();
    } catch (error) {
      console.error("Error creando empresa-rol", error);
      setSnack({
        open: true,
        severity: "error",
        message: "Error al asignar el rol a la empresa",
      });
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm("¿Seguro que deseas eliminar este registro?")) return;

    try {
      // 🔴 SIN /api
      await axios.delete(`/v1/empresa-rol/${id}`);
      setSnack({
        open: true,
        severity: "success",
        message: "Registro eliminado correctamente",
      });
      loadEmpresaRoles();
    } catch (error) {
      console.error("Error eliminando empresa-rol", error);
      setSnack({
        open: true,
        severity: "error",
        message: "Error al eliminar el registro",
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

  const handleCloseSnack = () =>
    setSnack((prev) => ({ ...prev, open: false }));

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

      <Snackbar
        open={snack.open}
        autoHideDuration={4000}
        onClose={handleCloseSnack}
        anchorOrigin={{ vertical: "bottom", horizontal: "center" }}
      >
        <Alert
          onClose={handleCloseSnack}
          severity={snack.severity}
          variant="filled"
          sx={{ width: "100%" }}
        >
          {snack.message}
        </Alert>
      </Snackbar>
    </Box>
  );
}
