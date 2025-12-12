// src/components/UsuarioRoles/UsuarioRoles.jsx
import React, { useEffect, useState, useMemo } from "react";
import { Box, Typography, Stack, Button } from "@mui/material";
import AddRounded from "@mui/icons-material/AddRounded";
import EditRounded from "@mui/icons-material/EditRounded";
import DeleteRounded from "@mui/icons-material/DeleteRounded";

import axios from "../axiosConfig";
import GridUsuarioRoles from "./GridUsuarioRoles.jsx";
import FormUsuarioRoles from "./FormUsuarioRoles.jsx";

const emptyRow = {
  id: null,
  usuarioId: "",
  empresaId: "",
  rolId: "",
  estadoId: "",
  iniciaContratoEn: "",
  finalizaContratoEn: "",
};

const extractItems = (resp) => resp.data?.content ?? resp.data ?? [];

const buildMap = (list) =>
  list.reduce((acc, item) => {
    acc[item.id] = item.name ?? item.nombre ?? item.id;
    return acc;
  }, {});

export default function UsuarioRoles() {
  const [rows, setRows] = useState([]);
  const [selectedRow, setSelectedRow] = useState(null);
  const [loading, setLoading] = useState(false);

  const [usuarios, setUsuarios] = useState([]);
  const [empresas, setEmpresas] = useState([]);
  const [roles, setRoles] = useState([]);
  const [estados, setEstados] = useState([]);

  const [openForm, setOpenForm] = useState(false);
  const [formMode, setFormMode] = useState("create");

  const usuariosMap = useMemo(() => buildMap(usuarios), [usuarios]);
  const empresasMap = useMemo(() => buildMap(empresas), [empresas]);
  const rolesMap = useMemo(() => buildMap(roles), [roles]);
  const estadosMap = useMemo(() => buildMap(estados), [estados]);

  // -----------------------
  // LOAD DATA
  // -----------------------
  const loadData = async () => {
    setLoading(true);
    try {
      const resp = await axios.get("/v1/system/usuario-roles", {
        params: { page: 0, size: 50 },
      });
      setRows(extractItems(resp));
    } catch (err) {
      console.error("Error cargando usuario-roles", err);
    } finally {
      setLoading(false);
    }
  };

  const loadCombos = async () => {
    try {
      const [uRes, eRes, rRes, esRes] = await Promise.all([
        axios.get("/v1/items/usuario_system/0"),
        axios.get("/v1/items/empresa/0"),
        axios.get("/v1/items/rol/0"),
        axios.get("/v1/items/estado/0"),
      ]);

      setUsuarios(extractItems(uRes));
      setEmpresas(extractItems(eRes));
      setRoles(extractItems(rRes));

      // 👉 Guardamos TODOS los estados (para que el mapa tenga todos los ids)
      const estadosApi = extractItems(esRes);
      setEstados(estadosApi);
    } catch (err) {
      console.error("Error cargando combos", err);
    }
  };

  useEffect(() => {
    loadData();
    loadCombos();
  }, []);

  // -----------------------
  // ACTIONS
  // -----------------------
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
      await axios.delete(`/v1/system/usuario-roles/${selectedRow.id}`);
      await loadData();
      setSelectedRow(null);
    } catch (err) {
      console.error("Error al eliminar", err);
    }
  };

  const handleSubmitForm = async (data) => {
    try {
      if (formMode === "create") {
        await axios.post("/v1/system/usuario-roles", data);
      } else {
        await axios.put(`/v1/system/usuario-roles/${data.id}`, data);
      }

      setOpenForm(false);
      await loadData();
      setSelectedRow(null);
    } catch (err) {
      console.error("Error guardando usuario-rol", err);
      console.error(
        "Respuesta backend:",
        err.response?.status,
        err.response?.data
      );
    }
  };

  return (
    <Box p={2}>
      <Typography variant="h6" mb={2}>
        Gestión Usuario–Roles
      </Typography>

      <Stack direction="row" spacing={2} mb={2}>
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
        usuariosMap={usuariosMap}
        empresasMap={empresasMap}
        rolesMap={rolesMap}
        estadosMap={estadosMap}
      />

      <FormUsuarioRoles
        open={openForm}
        onClose={() => setOpenForm(false)}
        mode={formMode}
        initialData={selectedRow ?? emptyRow}
        onSubmit={handleSubmitForm}
        usuarios={usuarios}
        empresas={empresas}
        roles={roles}
        estados={estados}
      />
    </Box>
  );
}
