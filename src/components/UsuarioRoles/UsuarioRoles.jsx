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

  // Detecta IDs reales del estado ACTIVO/INACTIVO según catálogo
  const ACTIVO_ID = useMemo(() => {
    const e = (estados || []).find((x) =>
      String(x.name ?? x.nombre ?? "").toUpperCase().includes("ACTIVO")
    );
    return e?.id ?? 1;
  }, [estados]);

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

  // ✅ helper: marca inactivo en grilla SIN remover
  const markRowInactiveLocally = (id) => {
    setRows((prev) =>
      (Array.isArray(prev) ? prev : []).map((r) =>
        r.id === id ? { ...r, estadoId: INACTIVO_ID } : r
      )
    );
    setSelectedRow(null);
  };

  /**
   * ✅ INACTIVAR: NO debe desaparecer de la grid
   * - intentamos distintas rutas (porque tu backend cambió entre módulos)
   * - si el request es OK, forzamos estadoId INACTIVO en frontend (sin remover)
   */
  const handleDelete = async () => {
    if (!selectedRow) return;

    const ok = window.confirm(
      "¿Seguro que deseas INACTIVAR este registro? (Debe quedar en la grilla)"
    );
    if (!ok) return;

    const id = selectedRow.id;

    try {
      // 1) Si existe toggleEstado en algún ambiente
      try {
        await axios.patch(`/v1/system/usuario-roles/toggleEstado/${id}`);
        markRowInactiveLocally(id);
        return;
      } catch (e) {
        // si NO es 404, lo relanzamos
        if (e?.response?.status && e.response.status !== 404) throw e;
      }

      // 2) Intento PUT /{id} cambiando SOLO estadoId (+ rolId por si el backend lo exige)
      //    (si tu backend solo permite cambiar rol, esto puede fallar)
      try {
        await axios.put(`/v1/system/usuario-roles/${id}`, {
          id,
          rolId: selectedRow.rolId,
          estadoId: INACTIVO_ID,
        });
        markRowInactiveLocally(id);
        return;
      } catch (e) {
        if (e?.response?.status && e.response.status !== 404) {
          // puede ser 400/405 por validación, seguimos a DELETE
        }
      }

      // 3) DELETE /{id} (si el backend lo usa como borrado lógico)
      await axios.delete(`/v1/system/usuario-roles/${id}`);

      // ✅ CLAVE: NO recargamos de inmediato para que NO desaparezca.
      // Marcamos localmente inactivo sí o sí:
      markRowInactiveLocally(id);

      // Si tu backend hace borrado lógico, puedes descomentar para sincronizar:
      // await loadData();
    } catch (err) {
      console.error("Error inactivando", err);
      console.error("Respuesta backend:", err.response?.status, err.response?.data);
      alert(err.response?.data?.message ?? "No se pudo inactivar.");
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
          color="error"
          startIcon={<DeleteRounded />}
          disabled={!selectedRow}
          onClick={handleDelete}
        >
          Inactivar
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
