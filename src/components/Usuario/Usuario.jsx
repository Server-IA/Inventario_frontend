import React, { useEffect, useMemo, useState } from "react";
import { Box, Button } from "@mui/material";
import VisibilityIcon from "@mui/icons-material/Visibility";
import GridActionBar from "../common/GridActionBar.jsx";
import SectionHeader from "../common/SectionHeader.jsx";
import AppDataGrid from "../common/AppDataGrid.jsx";
import FormUsuario from "./FormUsuario.jsx";
import UserDetailDialog from "./UserDetailDialog.jsx";
import MessageSnackBar from "../MessageSnackBar";
import axios from "../axiosConfig";

const emptyRow = {
  id: null,
  username: "",
  nombre: "",
  apellido: "",
  genero: "",
  tipoDocumentoIdentidadId: "",
  codigoIdentificacion: "",
  fechaNacimiento: "",
  estrato: "",
  direccion: "",
  celular: "",
  rolPreferido: "",
  empresaId: "",
  empresaNombre: "",
  estadoId: 1,
  asignaciones: [],
};

const extractItems = (resp) => resp.data?.content ?? resp.data ?? [];

export default function Usuario() {
  const [rows, setRows] = useState([]);
  const [selectedRow, setSelectedRow] = useState(null);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState({ open: false, severity: "", text: "" });
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [openForm, setOpenForm] = useState(false);
  const [formMode, setFormMode] = useState("create");
  const [formData, setFormData] = useState(emptyRow);
  const [openDetail, setOpenDetail] = useState(false);
  const [detail, setDetail] = useState(null);
  const [inactivatedCache, setInactivatedCache] = useState([]);

  const isAdmin = String(localStorage.getItem("rolId")) === "1";
  const empresaIdOwn = Number(localStorage.getItem("empresaId"));

  const [empresasList, setEmpresasList] = useState([]);
  const [rolesList, setRolesList] = useState([]);

  const baseRows = useMemo(() => rows, [rows]);

  const columns = useMemo(() => {
    const cols = [
      { field: "nombreCompleto", headerName: "Nombre", flex: 1.15, minWidth: 220 },
      { field: "username", headerName: "Correo", flex: 1.3, minWidth: 250 },
      { field: "rolNombre", headerName: "Rol", flex: 1.1, minWidth: 220 },
      {
        field: "estadoId",
        headerName: "Estado",
        flex: 0.7,
        minWidth: 140,
        align: "left",
        headerAlign: "left",
        statusChip: true,
      },
      ...(isAdmin ? [{ field: "empresaNombre", headerName: "Empresa", flex: 1.2, minWidth: 220 }] : []),
    ];
    return cols;
  }, [isAdmin]);

  const loadData = async () => {
    setLoading(true);
    try {
      const url = isAdmin ? "/v1/system/usuario-roles" : "/v1/usuario-roles";
      const resp = await axios.get(url, { params: { page: 0, size: 200 } });
      const list = extractItems(resp);
      let mapped = (Array.isArray(list) ? list : []).map((a) => ({
        id: a.usuarioId ?? a.id,
        username: a.usuarioEmail ?? "",
        nombreCompleto: a.personaNombreCompleto ?? "",
        empresaNombre: a.empresaNombre ?? "",
        rolNombre: a.rolNombre ?? "",
        estadoId: a.estadoId ?? 1,
        raw: a,
      }));
      // fusiona inactivados locales para garantizar visibilidad
      if (Array.isArray(inactivatedCache) && inactivatedCache.length) {
        const byId = new Map(mapped.map((r) => [r.id, r]));
        inactivatedCache.forEach((g) => {
          const existing = byId.get(g.id);
          if (existing) {
            byId.set(g.id, { ...existing, estadoId: 2 });
          } else {
            byId.set(g.id, g);
          }
        });
        mapped = Array.from(byId.values());
      }
      setRows(mapped);
    } catch (e) {
      console.error("Error cargando usuarios desde asignaciones", e);
      setRows([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
    // combos
    Promise.all([
      axios.get("/v1/items/empresa/0"),
      axios.get("/v1/items/rol/0"),
    ])
      .then(([eRes, rRes]) => {
        setEmpresasList(extractItems(eRes));
        setRolesList(extractItems(rRes) || []);
      })
      .catch(() => {
        setEmpresasList([]);
        setRolesList([]);
      });
  }, []);

  const handlePaginationModelChange = (model) => {
    if (model.size !== pageSize) {
      setPageSize(model.size);
      setPage(0);
    } else {
      setPage(model.page);
    }
  };

  const handleCreate = () => {
    setFormMode("create");
    setFormData(emptyRow);
    setOpenForm(true);
  };
  const handleEdit = () => {
    if (!selectedRow) return;
    setFormMode("edit");
    setFormData({
      username: selectedRow.username,
      rolId: selectedRow.raw?.rolId ?? "",
      nombre: selectedRow.nombreCompleto?.split(" ")[0] ?? "",
      apellido: selectedRow.nombreCompleto?.split(" ").slice(1).join(" ") ?? "",
      genero: "",
      tipoDocumentoIdentidadId: "",
      codigoIdentificacion: "",
      fechaNacimiento: "",
      estrato: "",
      direccion: "",
      celular: "",
      estadoId: selectedRow.estadoId ?? 1,
      asignaciones: [],
    });
    setOpenForm(true);
  };
  const handleView = () => {
    if (!selectedRow) return;
    setDetail(selectedRow);
    setOpenDetail(true);
  };
  const handleDelete = async () => {
    if (!selectedRow) return;
    const ok = window.confirm("¿Seguro que deseas INACTIVAR este usuario? (Debe permanecer visible)");
    if (!ok) return;
    const assignId = selectedRow?.raw?.id;
    const base = isAdmin ? "/v1/system/usuario-roles" : "/v1/usuario-roles";
    try {
      // intento toggle (si existe)
      try {
        const params = isAdmin ? { params: { empresaId: empresaIdOwn } } : undefined;
        await axios.patch(`${base}/${assignId}/toggle-estado`, null, params);
        const ghost = { ...selectedRow, estadoId: 2 };
        setRows((prev) => (Array.isArray(prev) ? prev : []).map((r) => r.id === selectedRow.id ? { ...r, estadoId: 2 } : r));
        setInactivatedCache((prev) => {
          const map = new Map((prev || []).map((x) => [x.id, x]));
          map.set(ghost.id, ghost);
          return Array.from(map.values());
        });
        setSelectedRow(null);
        return;
      } catch (e) {
        if (e?.response?.status && e.response.status !== 404) throw e;
      }
      // intento PUT forzando estado inactivo
      try {
        await axios.put(`${base}/${assignId}`, {
          id: assignId,
          usuarioId: selectedRow.raw.usuarioId,
          rolId: selectedRow.raw.rolId,
          estadoId: 2,
        });
        const ghost = { ...selectedRow, estadoId: 2 };
        setRows((prev) => (Array.isArray(prev) ? prev : []).map((r) => r.id === selectedRow.id ? { ...r, estadoId: 2 } : r));
        setInactivatedCache((prev) => {
          const map = new Map((prev || []).map((x) => [x.id, x]));
          map.set(ghost.id, ghost);
          return Array.from(map.values());
        });
        setSelectedRow(null);
        return;
      } catch (e) {
        // continúa a DELETE si no permite
      }
      // DELETE lógico
      await axios.delete(`${base}/${assignId}`);
      const ghost = { ...selectedRow, estadoId: 2 };
      setRows((prev) => (Array.isArray(prev) ? prev : []).map((r) => r.id === selectedRow.id ? { ...r, estadoId: 2 } : r));
      setInactivatedCache((prev) => {
        const map = new Map((prev || []).map((x) => [x.id, x]));
        map.set(ghost.id, ghost);
        return Array.from(map.values());
      });
      setSelectedRow(null);
    } catch (err) {
      setMessage({ open: true, severity: "error", text: err.response?.data?.message ?? "No se pudo inactivar" });
    }
  };

  const handleSubmit = async (dataFromForm) => {
    const payload = { ...(dataFromForm || formData) };
    try {
      if (formMode === "create") {
        // Orden exacto del payload de registro (con tipos numéricos donde aplica)
        const body = {};
        body.username = payload.username;
        const resolvedRolId = payload.rolId ?? (Array.isArray(payload.asignaciones) ? payload.asignaciones[0]?.rolId : undefined);
        body.rolId = resolvedRolId != null ? Number(resolvedRolId) : resolvedRolId;
        body.nombre = payload.nombre;
        body.apellido = payload.apellido;
        body.genero = payload.genero;
        body.tipoDocumentoIdentidadId = payload.tipoDocumentoIdentidadId ? Number(payload.tipoDocumentoIdentidadId) : payload.tipoDocumentoIdentidadId;
        body.codigoIdentificacion = payload.codigoIdentificacion;
        body.fechaNacimiento = payload.fechaNacimiento;
        body.estrato = Number(payload.estrato || 0);
        body.direccion = payload.direccion;
        body.celular = payload.celular;
        const ABS_AUTH_URL = `${import.meta.env.VITE_BACKEND_URI}/auth/empresa/usuario-roles`;
        await axios.post(ABS_AUTH_URL, body);
      } else {
        const assignId = selectedRow?.raw?.id;
        if (assignId) {
          const base = isAdmin ? "/v1/system/usuario-roles" : "/v1/usuario-roles";
          // Orden exacto del payload de asignación (mismo que POST)
          const body = {};
          body.usuarioId = selectedRow.raw.usuarioId;
          body.rolId = Number(payload.rolId ?? selectedRow.raw.rolId);
          body.estadoId = payload.estadoId ?? selectedRow.raw.estadoId;
          body.iniciaContratoEn = payload.iniciaContratoEn ?? selectedRow.raw.iniciaContratoEn;
          body.finalizaContratoEn = payload.finalizaContratoEn ?? selectedRow.raw.finalizaContratoEn;
          await axios.put(`${base}/${assignId}`, body);
        }
      }
      setOpenForm(false);
      await loadData();
      setMessage({ open: true, severity: "success", text: "Operación exitosa" });
    } catch (err) {
      setMessage({ open: true, severity: "error", text: err.response?.data?.message ?? "Error en la operación" });
    }
  };

  return (
    <Box p={2}>
      <SectionHeader title="Gestión de Usuarios" />

      <GridActionBar
        onAdd={handleCreate}
        onUpdate={handleEdit}
        onDelete={handleDelete}
        canUpdate={Boolean(selectedRow)}
        canDelete={Boolean(selectedRow)}
        onFilters={() =>
          setMessage({
            open: true,
            severity: "info",
            text: "Filtros disponibles próximamente",
          })
        }
        extraActions={
          <Button
            onClick={handleView}
            startIcon={<VisibilityIcon />}
            disabled={!selectedRow}
          >
            Ver detalle
          </Button>
        }
      />

      <AppDataGrid
        rows={rows}
        columns={columns}
        loading={loading}
        selectedRow={selectedRow}
        setSelectedRow={setSelectedRow}
        paginationModel={{ page, pageSize }}
        setPaginationModel={handlePaginationModelChange}
        rowCount={rows.length}
        containerSx={{ borderRadius: 4 }}
        onEscape={() => setOpenForm(false)}
      />

      <FormUsuario
        open={openForm}
        onClose={() => setOpenForm(false)}
        mode={formMode}
        initialData={formData}
        onSubmit={handleSubmit}
        roles={rolesList}
        empresas={empresasList}
        isAdmin={isAdmin}
      />

      <UserDetailDialog
        open={openDetail}
        data={detail ?? selectedRow}
        onClose={() => setOpenDetail(false)}
      />

      <MessageSnackBar message={message} setMessage={setMessage} />
    </Box>
  );
}
