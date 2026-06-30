/*=============================================================================
Nombre del archivo : Usuario.jsx
Descripción        : Módulo de gestión de usuarios con listado, edición e integración de detalle.
===============================================================================
CONTROL DE CAMBIOS
+------------+---------+----------------------+-----------------------------------------------+
|   Fecha    | Versión |      Autor           | Descripción del cambio                        |
+------------+---------+----------------------+-----------------------------------------------+
| 2026-05-08 | 0.4.0   | Cesar Medina         | Creación del archivo.                         |
| 2026-06-02 | 0.4.0   | Cesar Medina         | Se documenta e integra consulta de detalle.   |
| 2026-06-10 | 0.4.0   | Cesar Medina         | Se adapta el listado a GET /v1/usuarios.      |
| 2026-06-29 | 0.4.0   | Cesar Medina         | Se mejora el estilo visual del estado.        |
| 2026-06-29 | 0.4.0   | Cesar Medina         | Se corrige la paginación del listado.         |
| 2026-06-30 | 0.4.0   | Cesar Medina         | Se integra registro con HU-037.1 y nuevo modal|
| 2026-06-30 | 0.4.0   | Cesar Medina         | Se ajusta formato del payload de registro.    |
+------------+---------+----------------------+-----------------------------------------------+
=============================================================================*/
/**
 * @module Usuario
 * @description Administra el listado de usuarios, sus acciones CRUD visibles
 * en la interfaz y la consulta del detalle individual desde los endpoints del backend.
 */
import React, { useEffect, useMemo, useState } from "react";
import { Box, Button, Chip } from "@mui/material";
import VisibilityIcon from "@mui/icons-material/Visibility";
import { useTranslation } from "react-i18next";
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
  tipoIdentificacionId: "",
  codigoIdentificacion: "",
  identificacion: "",
  fechaNacimiento: "",
  estrato: "",
  direccion: "",
  celular: "",
  emailPersonal: "",
  rolPreferido: "",
  empresaId: "",
  empresaNombre: "",
  estadoId: 1,
  asignaciones: [],
};

/**
 * Normaliza respuestas del backend que pueden venir paginadas o como arreglo.
 *
 * @param {object} resp Respuesta HTTP.
 * @returns {Array}
 */
const extractItems = (resp) => resp.data?.content ?? resp.data ?? [];

/**
 * Obtiene las asignaciones de un usuario como arreglo seguro.
 *
 * @param {object} user Usuario del backend.
 * @returns {Array}
 */
const getAssignments = (user) => (Array.isArray(user?.asignaciones) ? user.asignaciones : []);

/**
 * Resuelve la asignación más representativa del usuario para la tabla.
 *
 * @param {object} user Usuario del backend.
 * @returns {object|null}
 */
const getPreferredAssignment = (user) => {
  const assignments = getAssignments(user);
  const preferredRole = String(user?.rolPreferido ?? "").trim().toLowerCase();

  return (
    assignments.find(
      (item) => String(item?.rolNombre ?? "").trim().toLowerCase() === preferredRole
    ) ??
    assignments[0] ??
    null
  );
};

/**
 * Resuelve la etiqueta visible del rol preferido.
 *
 * @param {object} user Usuario del backend.
 * @returns {string}
 */
const getPreferredRoleLabel = (user) => {
  const preferredRole = String(user?.rolPreferido ?? "").trim();

  if (preferredRole && !preferredRole.toUpperCase().startsWith("ROLE_")) {
    return preferredRole;
  }

  return getPreferredAssignment(user)?.rolNombre ?? preferredRole;
};

/**
 * Obtiene una etiqueta de empresas sin duplicados para la tabla.
 *
 * @param {object} user Usuario del backend.
 * @returns {string}
 */
const getCompanyLabel = (user) => {
  const companyNames = getAssignments(user)
    .map((item) => item?.empresaNombre)
    .filter(Boolean);

  return Array.from(new Set(companyNames)).join(", ");
};

/**
 * Normaliza estado activo/inactivo a un id compatible con la UI actual.
 *
 * @param {number|string} value Estado recibido desde el backend.
 * @returns {number}
 */
const normalizeStatusId = (value) => {
  const normalized = String(value ?? "").trim().toLowerCase();

  if (
    value === 1 ||
    value === "1" ||
    normalized === "activo" ||
    normalized === "active" ||
    normalized === "activa"
  ) {
    return 1;
  }

  if (
    value === 2 ||
    value === "2" ||
    normalized === "inactivo" ||
    normalized === "inactive" ||
    normalized === "inactiva"
  ) {
    return 2;
  }

  return 1;
};

/**
 * Convierte una fecha simple a formato ISO con zona horaria para el endpoint
 * de registro de usuarios.
 *
 * @param {string} value Fecha en formato YYYY-MM-DD o datetime existente.
 * @returns {string|null}
 */
const toOffsetDateTime = (value) => {
  const normalized = String(value ?? "").trim();
  if (!normalized) return null;
  if (normalized.includes("T")) return normalized;
  return `${normalized}T00:00:00-05:00`;
};

/**
 * Resuelve los estilos visuales del estado del usuario en la tabla.
 *
 * Registro 2026-06-10: acompana el ajuste visual documentado en la cabecera
 * para presentar el estado con una apariencia mas clara dentro del grid.
 *
 * @param {string} statusName Nombre del estado.
 * @returns {{ text: string, background: string, border: string }}
 */
const getUserStatusMeta = (statusName) => {
  const normalized = String(statusName ?? "").trim().toLowerCase();

  if (!normalized) {
    return {
      text: "#4E6660",
      background: "#EEF3F1",
      border: "rgba(78,102,96,0.16)",
    };
  }

  if (normalized.includes("activo") || normalized.includes("active")) {
    return {
      text: "#1B5E20",
      background: "rgba(46,125,50,0.10)",
      border: "rgba(46,125,50,0.18)",
    };
  }

  if (normalized.includes("inactivo") || normalized.includes("inactive")) {
    return {
      text: "#B3261E",
      background: "rgba(211,47,47,0.10)",
      border: "rgba(211,47,47,0.18)",
    };
  }

  if (normalized.includes("pendiente") || normalized.includes("pending")) {
    return {
      text: "#B54708",
      background: "rgba(237,108,2,0.10)",
      border: "rgba(237,108,2,0.18)",
    };
  }

  return {
    text: "#0B5CAD",
    background: "rgba(2,136,209,0.10)",
    border: "rgba(2,136,209,0.18)",
  };
};

/**
 * Componente principal del módulo Usuarios.
 *
 * @returns {JSX.Element}
 */
export default function Usuario() {
  const { t } = useTranslation();
  const [rows, setRows] = useState([]);
  const [selectedRow, setSelectedRow] = useState(null);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState({ open: false, severity: "", text: "" });
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [totalRows, setTotalRows] = useState(0);
  const [openForm, setOpenForm] = useState(false);
  const [formMode, setFormMode] = useState("create");
  const [formData, setFormData] = useState(emptyRow);
  const [openDetail, setOpenDetail] = useState(false);
  const [detail, setDetail] = useState(null);
  const [detailLoading, setDetailLoading] = useState(false);
  const [detailError, setDetailError] = useState("");
  const [inactivatedCache, setInactivatedCache] = useState([]);

  const isAdmin = String(localStorage.getItem("rolId")) === "1";
  const empresaIdOwn = Number(localStorage.getItem("empresaId"));

  const [empresasList, setEmpresasList] = useState([]);
  const [rolesList, setRolesList] = useState([]);
  const [tiposIdentificacionList, setTiposIdentificacionList] = useState([]);

  const columns = useMemo(() => {
    const cols = [
      { field: "nombre", headerKey: "usuario.columns.firstName", type: "text", flex: 1, minWidth: 180 },
      { field: "apellido", headerKey: "usuario.columns.lastName", type: "text", flex: 1, minWidth: 180 },
      { field: "username", headerKey: "usuario.columns.username", type: "text", flex: 1.35, minWidth: 260 },
      { field: "celular", headerKey: "usuario.columns.phone", type: "text", flex: 0.95, minWidth: 170 },
      { field: "rolPreferido", headerKey: "usuario.columns.preferredRole", type: "text", flex: 1.05, minWidth: 190 },
      {
        field: "estadoNombre",
        headerKey: "usuario.columns.status",
        type: "status",
        flex: 1,
        minWidth: 190,
        align: "left",
        headerAlign: "left",
        renderCell: (params) => {
          const meta = getUserStatusMeta(params?.value);

          return (
            <Box sx={{ display: "flex", justifyContent: "flex-start", width: "100%" }}>
              <Chip
                label={params?.value || "-"}
                size="small"
                sx={{
                  height: 28,
                  borderRadius: "999px",
                  fontWeight: 700,
                  fontSize: "0.74rem",
                  letterSpacing: 0.1,
                  color: meta.text,
                  backgroundColor: meta.background,
                  border: `1px solid ${meta.border}`,
                  "& .MuiChip-label": {
                    px: 1.5,
                  },
                }}
              />
            </Box>
          );
        },
      },
      ...(isAdmin
        ? [{ field: "empresaNombre", headerKey: "usuario.columns.company", type: "text", flex: 1.25, minWidth: 220 }]
        : []),
    ];
    return cols;
  }, [isAdmin]);

  const loadData = async () => {
    setLoading(true);
    try {
      // Registro 2026-06-10: la consulta respeta page/size del backend para que
      // el grid avance pagina por pagina sin cargar todo el listado a la vez.
      const resp = await axios.get("/v1/usuarios", { params: { page, size: pageSize } });
      const list = extractItems(resp);
      const totalElements = Number(resp?.data?.page?.totalElements ?? 0);
      // Registro 2026-06-10: el listado se unifica con GET /v1/usuarios y se mapea
      // y ahora respeta la paginación server-side del nuevo contrato.
      let mapped = (Array.isArray(list) ? list : []).map((a) => {
        const preferredAssignment = getPreferredAssignment(a);

        return {
          id: a.id,
          username: a.username ?? "",
          nombre: a.nombre ?? "",
          apellido: a.apellido ?? "",
          celular: a.celular ?? "",
          rolPreferido: getPreferredRoleLabel(a),
          estadoNombre: a.estadoNombre ?? "",
          empresaNombre: getCompanyLabel(a),
          estadoId: normalizeStatusId(a.estadoId),
          preferredAssignment,
          raw: a,
        };
      });
      setRows(mapped);
      setTotalRows(totalElements || mapped.length);
      setSelectedRow(null);
    } catch (e) {
      const status = e?.response?.status;
      const resolvedMessage =
        e?.response?.data?.message ??
        (status === 401 || status === 403
          ? t("usuario.messages.listAccessDenied")
          : t("usuario.messages.loadError"));
      console.error(resolvedMessage, e);
      setRows([]);
      setTotalRows(0);
      setSelectedRow(null);
      setMessage({ open: true, severity: "error", text: resolvedMessage });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, [page, pageSize]);

  useEffect(() => {
    // combos
    Promise.all([
      axios.get("/v1/items/empresa/0"),
      axios.get("/v1/items/rol/0"),
      axios.get("/v1/items/tipo_identificacion/0"),
    ])
      .then(([eRes, rRes, tRes]) => {
        setEmpresasList(extractItems(eRes));
        setRolesList(extractItems(rRes) || []);
        setTiposIdentificacionList(extractItems(tRes) || []);
      })
      .catch(() => {
        setEmpresasList([]);
        setRolesList([]);
        setTiposIdentificacionList([]);
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
    const assignmentRef = selectedRow?.preferredAssignment ?? getPreferredAssignment(selectedRow?.raw);

    if (!assignmentRef) {
      setMessage({
        open: true,
        severity: "info",
        text: t("usuario.messages.assignmentActionUnavailable"),
      });
      return;
    }

    setFormMode("edit");
    setFormData({
      username: selectedRow.username,
      rolId: assignmentRef?.rolId ?? "",
      nombre: selectedRow.nombre ?? "",
      apellido: selectedRow.apellido ?? "",
      genero: selectedRow.raw?.genero ?? "",
      tipoIdentificacionId:
        selectedRow.raw?.tipoIdentificacionId ??
        selectedRow.raw?.tipoDocumentoIdentidadId ??
        "",
      identificacion:
        selectedRow.raw?.identificacion ?? selectedRow.raw?.codigoIdentificacion ?? "",
      fechaNacimiento: selectedRow.raw?.fechaNacimiento ?? "",
      estrato: selectedRow.raw?.estrato ?? "",
      direccion: selectedRow.raw?.direccion ?? "",
      celular: selectedRow.celular ?? selectedRow.raw?.celular ?? "",
      emailPersonal: selectedRow.raw?.emailPersonal ?? "",
      empresaId: assignmentRef?.empresaId ?? "",
      empresaNombre: assignmentRef?.empresaNombre ?? "",
      estadoId: selectedRow.estadoId ?? 1,
      asignaciones: [],
    });
    setOpenForm(true);
  };
  const handleView = () => {
    if (!selectedRow) return;
    const requestId = selectedRow?.raw?.id ?? selectedRow?.id;
    const roleById = new Map((rolesList || []).map((item) => [Number(item.id), item.nombre ?? item.name]));
    const companyById = new Map((empresasList || []).map((item) => [Number(item.id), item.nombre ?? item.name]));

    // Consulta el detalle real del usuario para mostrar la información completa en el modal.
    setOpenDetail(true);
    setDetail(null);
    setDetailError("");
    setDetailLoading(true);

    axios
      .get(`/v1/usuarios/${requestId}`)
      .then((resp) => {
        const payload = resp?.data ?? {};
        const asignaciones = Array.isArray(payload.asignaciones) ? payload.asignaciones : [];
        const normalized = {
          ...payload,
          estadoId: payload.estadoId ?? selectedRow?.estadoId ?? 1,
          rolPreferido:
            roleById.get(Number(payload.rolPreferidoId)) ??
            payload.rolPreferido ??
            "",
          empresaPreferida:
            companyById.get(Number(payload.empresaPreferidaId)) ??
            payload.empresaPreferida ??
            "",
          asignaciones: asignaciones.map((item) => ({
            ...item,
            rolNombre: roleById.get(Number(item.rolId)) ?? item.rolNombre ?? "",
            empresaNombre: companyById.get(Number(item.empresaId)) ?? item.empresaNombre ?? "",
          })),
        };
        setDetail(normalized);
      })
      .catch((err) => {
        const status = err?.response?.status;
        const resolvedMessage =
          err?.response?.data?.message ??
          (status === 401 || status === 403
            ? t("usuario.messages.detailAccessDenied")
            : t("usuario.messages.detailLoadError"));
        setDetailError(resolvedMessage);
        if (status === 401 || status === 403) {
          setMessage({ open: true, severity: "error", text: resolvedMessage });
        }
      })
      .finally(() => {
        setDetailLoading(false);
      });
  };
  const handleDelete = async () => {
    if (!selectedRow) return;
    const assignmentRef = selectedRow?.preferredAssignment ?? getPreferredAssignment(selectedRow?.raw);
    const assignId = assignmentRef?.usuarioRolId ?? selectedRow?.raw?.usuarioRolId;

    if (!assignId) {
      setMessage({
        open: true,
        severity: "info",
        text: t("usuario.messages.assignmentActionUnavailable"),
      });
      return;
    }

    const ok = window.confirm(t("usuario.messages.confirmInactivate"));
    if (!ok) return;
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
          usuarioId: selectedRow.raw.id ?? selectedRow.raw.usuarioId,
          rolId: assignmentRef?.rolId,
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
      setMessage({ open: true, severity: "error", text: err.response?.data?.message ?? t("usuario.messages.cannotInactivate") });
    }
  };

  const handleSubmit = async (dataFromForm) => {
    const payload = { ...(dataFromForm || formData) };
    try {
      if (formMode === "create") {
        // Registro 2026-06-10: el alta usa el contrato unificado de /v1/usuarios/registro
        // con datos personales y múltiples asignaciones alineadas a HU-037.1.
        const body = {};
        body.username = payload.username?.trim();
        body.tipoIdentificacionId = payload.tipoIdentificacionId
          ? Number(payload.tipoIdentificacionId)
          : null;
        body.identificacion = payload.identificacion?.trim() ?? "";
        body.nombre = payload.nombre;
        body.apellido = payload.apellido;
        body.emailPersonal = payload.emailPersonal?.trim() ?? "";
        body.genero = payload.genero;
        body.fechaNacimiento = payload.fechaNacimiento;
        body.direccion = payload.direccion?.trim() ?? "";
        body.celular = payload.celular?.trim() ?? "";
        body.estrato =
          payload.estrato === "" || payload.estrato == null
            ? null
            : Number(payload.estrato);
        body.asignaciones = (Array.isArray(payload.asignaciones) ? payload.asignaciones : []).map(
          (item) => ({
            empresaId: Number(
              isAdmin
                ? item.empresaId
                : empresaIdOwn
            ),
            rolId: Number(item.rolId),
            // Registro 2026-06-10: el backend espera OffsetDateTime en las
            // fechas contractuales, no solo la fecha plana del input HTML.
            iniciaContratoEn: toOffsetDateTime(item.iniciaContratoEn),
            finalizaContratoEn: toOffsetDateTime(item.finalizaContratoEn),
            esPreferida: Boolean(item.preferido),
          })
        );
        if (!isAdmin) {
          body.asignaciones = body.asignaciones.map((item) => ({
            ...item,
            empresaId: Number(empresaIdOwn),
          }));
        }
        await axios.post("/v1/usuarios/registro", body);
      } else {
        const assignmentRef =
          selectedRow?.preferredAssignment ?? getPreferredAssignment(selectedRow?.raw);
        const assignId = assignmentRef?.usuarioRolId ?? selectedRow?.raw?.usuarioRolId;

        if (!assignId) {
          throw new Error("assignment-action-unavailable");
        }

        if (assignId) {
          const base = isAdmin ? "/v1/system/usuario-roles" : "/v1/usuario-roles";
          // Orden exacto del payload de asignación (mismo que POST)
          const body = {};
          body.usuarioId = selectedRow.raw.id ?? selectedRow.raw.usuarioId;
          body.rolId = Number(payload.rolId ?? assignmentRef?.rolId);
          body.estadoId = payload.estadoId ?? assignmentRef?.estadoId ?? selectedRow.raw.estadoId;
          body.iniciaContratoEn =
            payload.iniciaContratoEn ??
            assignmentRef?.iniciaContratoEn ??
            assignmentRef?.fechaInicioContrato;
          body.finalizaContratoEn =
            payload.finalizaContratoEn ??
            assignmentRef?.finalizaContratoEn ??
            assignmentRef?.fechaFinContrato;
          await axios.put(`${base}/${assignId}`, body);
        }
      }
      setOpenForm(false);
      await loadData();
      setMessage({
        open: true,
        severity: "success",
        text: formMode === "create" ? t("usuario.messages.createSuccess") : t("usuario.messages.updateSuccess"),
      });
    } catch (err) {
      const fallbackMessage =
        err?.message === "assignment-action-unavailable"
          ? t("usuario.messages.assignmentActionUnavailable")
          : t("common.messages.operationError");
      setMessage({
        open: true,
        severity: "error",
        text: err.response?.data?.message ?? fallbackMessage,
      });
    }
  };

  return (
    <Box p={2}>
      <SectionHeader titleKey="usuario.title" />

      <GridActionBar
        onAdd={handleCreate}
        onUpdate={handleEdit}
        onDelete={handleDelete}
        canUpdate={Boolean(selectedRow)}
        canDelete={Boolean(selectedRow)}
        extraActions={
          <Button
            onClick={handleView}
            startIcon={<VisibilityIcon />}
            disabled={!selectedRow}
          >
            {t("common.actions.viewDetail")}
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
        rowCount={totalRows}
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
        tiposIdentificacion={tiposIdentificacionList}
        isAdmin={isAdmin}
        sessionCompanyId={empresaIdOwn}
        sessionCompanyName={localStorage.getItem("empresaNombre") || ""}
      />

      <UserDetailDialog
        open={openDetail}
        data={detail}
        loading={detailLoading}
        error={detailError}
        onClose={() => {
          setOpenDetail(false);
          setDetail(null);
          setDetailError("");
        }}
      />

      <MessageSnackBar message={message} setMessage={setMessage} />
    </Box>
  );
}
