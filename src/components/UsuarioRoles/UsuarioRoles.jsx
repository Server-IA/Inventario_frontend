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
  (Array.isArray(list) ? list : []).reduce((acc, item) => {
    acc[item.id] = item.name ?? item.nombre ?? String(item.id);
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

  // Detecta ID real del estado INACTIVO según catálogo
  const INACTIVO_ID = useMemo(() => {
    const e = (estados || []).find((x) =>
      String(x.name ?? x.nombre ?? "").toUpperCase().includes("INACTIVO")
    );
    // si no lo encuentra, intenta el típico 2 o 0 (pero el real manda)
    return e?.id ?? 2;
  }, [estados]);

  // -----------------------
  // LOAD DATA
  // -----------------------
  const loadData = async () => {
    setLoading(true);
    try {
      const resp = await axios.get("/v1/system/usuario-roles", {
        params: { page: 0, size: 200 },
      });
      const data = extractItems(resp);
      setRows(Array.isArray(data) ? data : []);
    } catch (err) {
      console.error("Error cargando usuario-roles", err);
      setRows([]);
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
        axios.get("/v1/items/estado/0"), // ✅ necesario para mostrar nombres y tener IDs reales
      ]);

      setUsuarios(extractItems(uRes));
      setEmpresas(extractItems(eRes));
      setRoles(extractItems(rRes));

      const estadosApi = extractItems(esRes);
      setEstados(Array.isArray(estadosApi) ? estadosApi : []);
    } catch (err) {
      console.error("Error cargando combos", err);
      setUsuarios([]);
      setEmpresas([]);
      setRoles([]);
      setEstados([]);
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

  const isSelectedInactive = selectedRow?.estadoId === INACTIVO_ID;
  const toggleActionLabel = isSelectedInactive ? "Activar" : "Inactivar";

  /**
   * ✅ TOGGLE ESTADO: llama al endpoint oficial de backend y refresca desde el servidor
   * PATCH /v1/system/usuario-roles/{id}/toggle-estado?empresaId={empresaId}
   */
  const handleToggleEstado = async () => {
    if (!selectedRow?.id) return;

    const actionText = isSelectedInactive ? "ACTIVAR" : "INACTIVAR";
    const ok = window.confirm(
      `¿Seguro que deseas ${actionText} este registro?`
    );
    if (!ok) return;

    const id = selectedRow.id;
    const empresaId = selectedRow.empresaId;

    try {
      await axios.patch(`/v1/system/usuario-roles/${id}/toggle-estado`, null, {
        params: { empresaId },
      });

      // La grilla se actualiza desde el backend después del cambio exitoso
      await loadData();
      setSelectedRow(null);
    } catch (err) {
      console.error("Error cambiando estado de usuario-rol:", err);
      const backendMessage =
        err?.response?.data?.detail ||
        err?.response?.data?.message ||
        err?.response?.data?.mensaje ||
        (err?.response?.status === 403
          ? "No tiene permisos para modificar el estado de esta asignación."
          : err?.response?.status === 409
          ? "Conflicto al cambiar el estado de la asignación."
          : "No se pudo cambiar el estado de la asignación.");
      alert(backendMessage);
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
      console.error("Respuesta backend:", err.response?.status, err.response?.data);
      alert(err.response?.data?.message ?? "Error guardando usuario-rol");
    }
  };

  return (
    <Box p={2}>
      <Typography variant="h6" mb={2}>
        Gestión Usuario–Roles
      </Typography>

      <Stack direction="row" spacing={2} mb={2}>
        <Button variant="contained" startIcon={<AddRounded />} onClick={handleCreate}>
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
          color={isSelectedInactive ? "success" : "error"}
          startIcon={<DeleteRounded />}
          disabled={!selectedRow}
          onClick={handleToggleEstado}
        >
          {toggleActionLabel}
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
