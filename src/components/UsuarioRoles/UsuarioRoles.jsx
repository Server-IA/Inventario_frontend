import React, { useEffect, useState } from "react";
import { Box, Button, Stack, Typography } from "@mui/material";
import AddRounded from "@mui/icons-material/AddRounded";
import EditRounded from "@mui/icons-material/EditRounded";
import DeleteRounded from "@mui/icons-material/DeleteRounded";
import axios from "../axiosConfig";
import GridUsuarioRoles from "./GridUsuarioRoles.jsx";
import FormUsuarioRol from "./FormUsuarioRol.jsx";

const emptyRow = {
  id: null,
  usuarioId: "",
  empresaId: "",
  rolId: "",
  estadoId: "",
  iniciaContratoEn: "",
  finalizaContratoEn: "",
};

export default function UsuarioRoles() {
  const [rows, setRows] = useState([]);
  const [loading, setLoading] = useState(false);
  const [selectedRow, setSelectedRow] = useState(null);

  const [openForm, setOpenForm] = useState(false);
  const [formMode, setFormMode] = useState("create"); // "create" | "edit"

  const loadData = async () => {
    try {
      setLoading(true);
      // SIN /api
      const resp = await axios.get("/v1/system/usuario-roles", {
        params: { page: 0, size: 50 },
      });

      const content = resp.data?.content ?? resp.data ?? [];
      setRows(content);
    } catch (err) {
      console.error("Error cargando usuario-roles:", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const handleCreate = () => {
    setFormMode("create");
    setSelectedRow(emptyRow);
    setOpenForm(true);
  };

  const handleEdit = () => {
    if (!selectedRow) return;
    setFormMode("edit");
    setOpenForm(true);
  };

  const handleDelete = async () => {
    if (!selectedRow) return;
    if (!window.confirm("¿Seguro que deseas eliminar este registro?")) return;

    try {
      // SIN /api
      await axios.delete(`/v1/system/usuario-roles/${selectedRow.id}`);
      await loadData();
      setSelectedRow(null);
    } catch (err) {
      console.error("Error eliminando usuario-rol:", err);
    }
  };

  const handleSubmitForm = async (values) => {
    try {
      if (formMode === "create") {
        // POST SIN /api
        await axios.post("/v1/system/usuario-roles/create", values);
      } else {
        // PUT SIN /api
        await axios.put(`/v1/system/usuario-roles/${values.id}`, values);
      }
      setOpenForm(false);
      setSelectedRow(null);
      await loadData();
    } catch (err) {
      console.error("Error guardando usuario-rol:", err);
    }
  };

  return (
    <Box p={2}>
      <Typography variant="h6" gutterBottom>
        Gestión de Usuario – Roles
      </Typography>

      <Stack direction="row" spacing={1} mb={1}>
        <Button
          variant="contained"
          startIcon={<AddRounded />}
          onClick={handleCreate}
        >
          Crear
        </Button>
        <Button
          variant="outlined"
          startIcon={<EditRounded />}
          disabled={!selectedRow}
          onClick={handleEdit}
        >
          Editar
        </Button>
        <Button
          variant="outlined"
          color="error"
          startIcon={<DeleteRounded />}
          disabled={!selectedRow}
          onClick={handleDelete}
        >
          Eliminar
        </Button>
      </Stack>

      <GridUsuarioRoles
        rows={rows}
        loading={loading}
        selectedRow={selectedRow}
        setSelectedRow={setSelectedRow}
      />

      <FormUsuarioRol
        open={openForm}
        onClose={() => setOpenForm(false)}
        mode={formMode}
        initialData={selectedRow || emptyRow}
        onSubmit={handleSubmitForm}
      />
    </Box>
  );
}
