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
| 2026-06-10 | 0.4.0   | Cesar Medina         | Se integra actualización con HU-037.4.        |
| 2026-06-30 | 0.4.0   | Cesar Medina         | Se corrige timestamp del PUT de usuarios.     |
| 2026-06-30 | 0.4.0   | Cesar Medina         | Se alinea timestamp del PUT al body ejemplo.  |
| 2026-06-30 | 0.4.0   | Cesar Medina         | Se alinea rolPreferidoId al rol preferido.    |
| 2026-06-30 | 0.4.0   | Cesar Medina         | Se omite usuarioRolId en asignaciones nuevas. |
| 2026-08-10 | 0.4.0   | Cesar Medina         | Se integra filtro de usuarios.                |
| 2026-08-12 | 0.4.0   | Cesar Medina         | Se integra activación e inactivación HU-037.5.|
| 2026-08-12 | 0.4.0   | Cesar Medina         | Se corrige permiso HU-037.5 para admin sistema|
| 2026-08-12 | 0.4.0   | Cesar Medina         | Se alinea carga de empresa-rol por selección. |
| 2026-08-21 | 0.4.0   | Cesar Medina         | Se ajusta body del PUT según contrato backend.|
| 2026-08-21 | 0.4.0   | Cesar Medina         | Se traduce estado del listado vía i18n.       |
| 2026-08-21 | 0.4.0   | Cesar Medina         | Se amplía traducción de estados del listado.  |
| 2026-08-24 | 0.4.0   | Cesar Medina         | Se habilita estado para admin empresa contexto|
| 2026-08-24 | 0.4.0   | Cesar Medina         | Se reemplaza confirmación nativa por modal MUI|
| 2026-08-24 | 0.4.0   | Cesar Medina         | Se corrige prioridad de mapeo para inactivo.  |
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
import UserFiltersDialog from "./UserFiltersDialog.jsx";
import UserDetailDialog from "./UserDetailDialog.jsx";
import UserStatusConfirmDialog from "./UserStatusConfirmDialog.jsx";
import MessageSnackBar from "../MessageSnackBar";
import axios from "../axiosConfig";

const SYSTEM_ROLE_REGEX = /(ROLE_ADMINISTRADOR_SISTEMA|ADMINISTRADOR[_\s-]*SISTEMA|ADMIN\s*SISTEMA)/i;
const COMPANY_ROLE_REGEX = /(ROLE_ADMINISTRADOR_EMPRESA|ADMINISTRADOR[_\s-]*EMPRESA|ADMIN\s*EMPRESA)/i;

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

const buildInitialFilters = (isAdmin, empresaIdOwn) => ({
  username: "",
  nombre: "",
  apellido: "",
  rolId: "",
  estadoId: "",
  empresaId: isAdmin ? "" : String(empresaIdOwn || ""),
});

/**
 * Normaliza respuestas del backend que pueden venir paginadas o como arreglo.
 *
 * @param {object} resp Respuesta HTTP.
 * @returns {Array}
 */
const extractItems = (resp) => resp.data?.content ?? resp.data ?? [];

/**
 * Obtiene los roles por empresa persistidos en sesión.
 *
 * @returns {Array}
 */
const parseRolesByCompany = () => {
  try {
    return JSON.parse(localStorage.getItem("rolesByCompany") || "[]");
  } catch {
    return [];
  }
};

/**
 * Decodifica el payload del token activo sin dependencias externas.
 *
 * @returns {object}
 */
const parseSessionTokenPayload = () => {
  try {
    const token = localStorage.getItem("token") || "";
    const [, raw] = token.split(".");
    if (!raw) return {};

    const normalized = raw.replace(/-/g, "+").replace(/_/g, "/");
    const padding = normalized.length % 4 === 2 ? "==" : normalized.length % 4 === 3 ? "=" : "";

    return JSON.parse(atob(normalized + padding));
  } catch {
    return {};
  }
};

/**
 * Resuelve el nombre del rol actual tomando en cuenta el contexto de empresa.
 *
 * @returns {string}
 */
const resolveCurrentRoleName = () => {
  const empresaId = Number(localStorage.getItem("empresaId"));
  const rolId = Number(localStorage.getItem("rolId"));
  const rolesByCompany = parseRolesByCompany();
  const tokenPayload = parseSessionTokenPayload();

  const byContext = rolesByCompany.find(
    (item) => Number(item?.empresaId) === empresaId && Number(item?.rolId) === rolId
  );

  const byRoleId = rolesByCompany.find((item) => Number(item?.rolId) === rolId);
  const tokenRoles = [
    tokenPayload?.rolNombre,
    tokenPayload?.role,
    tokenPayload?.authorities,
    tokenPayload?.roles,
  ]
    .flat()
    .filter(Boolean)
    .map((item) => String(item).trim());

  return (
    byContext?.rolNombre ||
    localStorage.getItem("rolNombre") ||
    byRoleId?.rolNombre ||
    tokenRoles.find((item) => SYSTEM_ROLE_REGEX.test(item)) ||
    tokenRoles[0] ||
    ""
  );
};

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
 * Determina si el usuario listado pertenece a la empresa activa en sesión.
 *
 * @param {object} user Usuario del backend o fila normalizada.
 * @param {number|string} empresaId Empresa en contexto.
 * @returns {boolean}
 */
const belongsToContextCompany = (user, empresaId) => {
  const targetEmpresaId = Number(empresaId);
  if (!targetEmpresaId) return false;

  const assignmentCompanyIds = getAssignments(user)
    .map((item) => Number(item?.empresaId))
    .filter((value) => Number.isFinite(value) && value > 0);

  if (assignmentCompanyIds.length > 0) {
    return assignmentCompanyIds.includes(targetEmpresaId);
  }

  const fallbackCompanyIds = [
    Number(user?.empresaId),
    Number(user?.empresaPreferidaId),
    Number(user?.preferredAssignment?.empresaId),
    Number(user?.raw?.empresaId),
    Number(user?.raw?.empresaPreferidaId),
    Number(user?.raw?.preferredAssignment?.empresaId),
  ].filter((value) => Number.isFinite(value) && value > 0);

  return fallbackCompanyIds.includes(targetEmpresaId);
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
    value === 0 ||
    value === "0" ||
    value === 2 ||
    value === "2" ||
    normalized === "inactivo" ||
    normalized === "inactive" ||
    normalized === "inactiva" ||
    normalized === "desactivado" ||
    normalized === "disabled"
  ) {
    return 2;
  }

  return 1;
};

/**
 * Resuelve un mensaje visible a partir de una respuesta del backend.
 *
 * @param {object} payload Cuerpo de respuesta.
 * @returns {string}
 */
const resolveBackendMessage = (payload) => {
  if (typeof payload === "string") return payload;
  if (!payload || typeof payload !== "object") return "";

  return (
    payload?.message ??
    payload?.mensaje ??
    payload?.detail ??
    payload?.data?.message ??
    payload?.data?.mensaje ??
    ""
  );
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
  return `${normalized}T08:00:00-05:00`;
};

/**
 * Convierte una fecha simple al formato UTC usado por el endpoint PUT de
 * actualización de usuarios.
 *
 * @param {string} value Fecha en formato YYYY-MM-DD o datetime existente.
 * @returns {string|null}
 */
const toUtcDateTime = (value) => {
  const normalized = String(value ?? "").trim();
  if (!normalized) return null;
  if (normalized.endsWith("Z")) return normalized;
  if (normalized.includes("T")) {
    const baseDate = normalized.split("T")[0];
    return `${baseDate}T00:00:00Z`;
  }
  return `${normalized}T00:00:00Z`;
};

/**
 * Convierte una fecha del backend al formato YYYY-MM-DD para inputs tipo date.
 *
 * @param {string} value Fecha recibida desde el backend.
 * @returns {string}
 */
const toDateInputValue = (value) => {
  const normalized = String(value ?? "").trim();
  if (!normalized) return "";
  return normalized.includes("T") ? normalized.split("T")[0] : normalized;
};

/**
 * Normaliza el valor de género para el formulario.
 *
 * @param {string} value Valor recibido del backend.
 * @returns {string}
 */
const normalizeGenderForForm = (value) => {
  const normalized = String(value ?? "").trim().toLowerCase();
  if (!normalized) return "";
  if (normalized === "m" || normalized.includes("mascul")) return "M";
  if (normalized === "f" || normalized.includes("femen")) return "F";
  return String(value ?? "");
};

/**
 * Serializa el género al formato esperado por el endpoint de actualización.
 *
 * @param {string} value Valor del formulario.
 * @returns {string}
 */
const serializeGenderForUpdate = (value) => {
  const normalized = String(value ?? "").trim().toUpperCase();
  if (normalized === "M") return "Masculino";
  if (normalized === "F") return "Femenino";
  return String(value ?? "");
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

  // OJO: "inactivo" contiene "activo" como subcadena; el chequeo de inactivo
  // debe ir ANTES del de activo para no clasificar estados inactivos como activos.
  if (
    normalized.includes("inactivo") ||
    normalized.includes("inactive") ||
    normalized.includes("desactivado") ||
    normalized.includes("disabled")
  ) {
    return {
      text: "#B3261E",
      background: "rgba(211,47,47,0.10)",
      border: "rgba(211,47,47,0.18)",
    };
  }

  if (normalized.includes("activo") || normalized.includes("active")) {
    return {
      text: "#1B5E20",
      background: "rgba(46,125,50,0.10)",
      border: "rgba(46,125,50,0.18)",
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
 * Resuelve la clave de traducción del estado visible en la tabla.
 *
 * @param {number|string} statusName Estado recibido desde backend.
 * @returns {string}
 */
const resolveUserStatusTranslationKey = (statusName) => {
  const normalized = String(statusName ?? "").trim().toLowerCase();
  const compact = normalized
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "")
    .replace(/\s+/g, "_");

  if (compact === "perfil_incompleto" || compact === "incomplete_profile") {
    return "usuario.statusLabels.incompleteProfile";
  }

  if (compact === "no_verificado" || compact === "not_verified") {
    return "usuario.statusLabels.notVerified";
  }

  if (compact === "sin_empresa" || compact === "without_company" || compact === "no_company") {
    return "usuario.statusLabels.withoutCompany";
  }

  if (compact === "clave_expirada" || compact === "expired_password" || compact === "password_expired") {
    return "usuario.statusLabels.expiredPassword";
  }

  // OJO: "inactivo" contiene "activo" como subcadena; el chequeo de inactivo
  // debe ir ANTES del de activo para no traducir estados inactivos como activos.
  if (
    normalized.includes("inactivo") ||
    normalized.includes("inactive") ||
    normalized.includes("desactivado") ||
    normalized.includes("disabled")
  ) {
    return "usuario.statusLabels.inactive";
  }

  if (
    normalized.includes("activo") ||
    normalized.includes("active")
  ) {
    return "usuario.statusLabels.active";
  }

  if (normalized.includes("pendiente") || normalized.includes("pending")) {
    return "usuario.statusLabels.pending";
  }

  return "";
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
  const [formLoading, setFormLoading] = useState(false);
  const [formMode, setFormMode] = useState("create");
  const [formData, setFormData] = useState(emptyRow);
  const [openFilters, setOpenFilters] = useState(false);
  const [openDetail, setOpenDetail] = useState(false);
  const [detail, setDetail] = useState(null);
  const [detailLoading, setDetailLoading] = useState(false);
  const [detailError, setDetailError] = useState("");
  const [openStatusDialog, setOpenStatusDialog] = useState(false);
  const [statusSubmitting, setStatusSubmitting] = useState(false);
  const [statusDialogError, setStatusDialogError] = useState("");
  const currentRoleName = resolveCurrentRoleName();
  const isAdmin = SYSTEM_ROLE_REGEX.test(currentRoleName);
  const isCompanyAdmin = COMPANY_ROLE_REGEX.test(currentRoleName);
  const empresaIdOwn = Number(localStorage.getItem("empresaId"));
  const [filters, setFilters] = useState(() => buildInitialFilters(isAdmin, empresaIdOwn));
  const [filterDraft, setFilterDraft] = useState(() => buildInitialFilters(isAdmin, empresaIdOwn));

  const [empresasList, setEmpresasList] = useState([]);
  const [rolesList, setRolesList] = useState([]);
  const [empresaRolesList, setEmpresaRolesList] = useState([]);
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
          const statusLabelKey = resolveUserStatusTranslationKey(params?.value);
          const statusLabel = statusLabelKey ? t(statusLabelKey) : params?.value || "-";

          return (
            <Box sx={{ display: "flex", justifyContent: "flex-start", width: "100%" }}>
              <Chip
                label={statusLabel}
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
  }, [isAdmin, t]);

  const roleFilterOptions = useMemo(
    () =>
      (Array.isArray(rolesList) ? rolesList : []).map((item) => ({
        value: String(item.id),
        label: item.nombre ?? item.name ?? String(item.id),
      })),
    [rolesList]
  );

  const companyFilterOptions = useMemo(
    () =>
      (Array.isArray(empresasList) ? empresasList : []).map((item) => ({
        value: String(item.id),
        label: item.nombre ?? item.name ?? String(item.id),
      })),
    [empresasList]
  );

  const statusFilterOptions = useMemo(
    () => [
      { value: "1", label: t("common.labels.active") },
      { value: "2", label: t("common.labels.inactive") },
    ],
    [t]
  );

  const hasActiveFilters = useMemo(
    () =>
      Boolean(
        String(filters.username ?? "").trim() ||
          String(filters.nombre ?? "").trim() ||
          String(filters.apellido ?? "").trim() ||
          String(filters.rolId ?? "").trim() ||
          String(filters.estadoId ?? "").trim() ||
          (isAdmin && String(filters.empresaId ?? "").trim())
      ),
    [filters, isAdmin]
  );

  const selectedRowStatusId = normalizeStatusId(
    selectedRow?.raw?.estadoNombre ??
      selectedRow?.estadoNombre ??
      selectedRow?.raw?.estadoId ??
      selectedRow?.estadoId
  );
  const selectedRowIsInactive = selectedRowStatusId === 2;
  const selectedRowBelongsToContextCompany = belongsToContextCompany(
    selectedRow?.raw ?? selectedRow,
    empresaIdOwn
  );
  const canToggleUserStatus = Boolean(selectedRow) && (
    isAdmin || (isCompanyAdmin && selectedRowBelongsToContextCompany)
  );
  const deleteActionLabel = selectedRowIsInactive
    ? t("usuario.actions.activate")
    : t("usuario.actions.inactivate");

  const loadData = async () => {
    setLoading(true);
    try {
      const params = {
        page,
        size: pageSize,
        ...(String(filters.username ?? "").trim()
          ? { username: String(filters.username).trim() }
          : {}),
        ...(String(filters.nombre ?? "").trim()
          ? { nombre: String(filters.nombre).trim() }
          : {}),
        ...(String(filters.apellido ?? "").trim()
          ? { apellido: String(filters.apellido).trim() }
          : {}),
        ...(String(filters.rolId ?? "").trim()
          ? { rolId: Number(filters.rolId) }
          : {}),
        ...(String(filters.estadoId ?? "").trim()
          ? { estadoId: Number(filters.estadoId) }
          : {}),
        // El backend permite filtrar por empresa a ADMINISTRADOR_SISTEMA
        // (lectura global multiempresa) y a Admin Empresa (tenant forzado).
        ...(String(filters.empresaId ?? "").trim()
          ? { empresaId: Number(filters.empresaId) }
          : {}),
      };

      // Registro 2026-06-10: la consulta respeta page/size del backend para que
      // el grid avance pagina por pagina sin cargar todo el listado a la vez.
      const resp = await axios.get("/v1/usuarios", { params });
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
          // estadoContexto: el backend devuelve el estado correcto segun el rol
          // (Admin Sistema -> estado global; Admin Empresa -> estado de la
          // asignacion de su tenant). Se usa con fallback al estado global.
          estadoNombre: a.estadoContextoNombre ?? a.estadoNombre ?? "",
          empresaNombre: getCompanyLabel(a),
          estadoId: normalizeStatusId(a.estadoContextoId ?? a.estadoId),
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
  }, [page, pageSize, filters, isAdmin]);

  useEffect(() => {
    const nextFilters = buildInitialFilters(isAdmin, empresaIdOwn);
    setFilters(nextFilters);
    setFilterDraft(nextFilters);
    setPage(0);
  }, [isAdmin, empresaIdOwn]);

  useEffect(() => {
    // combos
    const empresaRolesRequest =
      !isAdmin && Number(empresaIdOwn)
        ? axios.get("/v1/empresa-rol/select", {
            params: { empresaId: Number(empresaIdOwn) },
          })
        : Promise.resolve({ data: [] });

    Promise.all([
      axios.get("/v1/items/empresa/0"),
      axios.get("/v1/items/rol/0"),
      axios.get("/v1/items/tipo_identificacion/0"),
      empresaRolesRequest,
    ])
      .then(([eRes, rRes, tRes, erRes]) => {
        setEmpresasList(extractItems(eRes));
        setRolesList(extractItems(rRes) || []);
        setTiposIdentificacionList(extractItems(tRes) || []);
        setEmpresaRolesList(extractItems(erRes) || []);
      })
      .catch(() => {
        setEmpresasList([]);
        setRolesList([]);
        setEmpresaRolesList([]);
        setTiposIdentificacionList([]);
      });
  }, [isAdmin, empresaIdOwn]);

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

  const handleOpenFilters = () => {
    setFilterDraft(filters);
    setOpenFilters(true);
  };

  const handleFilterDraftChange = (name, value) => {
    setFilterDraft((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleApplyFilters = () => {
    setFilters({
      username: String(filterDraft.username ?? "").trim(),
      nombre: String(filterDraft.nombre ?? "").trim(),
      apellido: String(filterDraft.apellido ?? "").trim(),
      rolId: String(filterDraft.rolId ?? "").trim(),
      estadoId: String(filterDraft.estadoId ?? "").trim(),
      // Admin Sistema puede filtrar por cualquier empresa (lectura global);
      // Admin Empresa conserva su tenant (empresaIdOwn).
      empresaId: String(
        filterDraft.empresaId ?? filters.empresaId ?? (isAdmin ? "" : empresaIdOwn ?? "")
      ).trim(),
    });
    setOpenFilters(false);
    setPage(0);
  };

  const handleClearFilters = () => {
    const nextFilters = buildInitialFilters(isAdmin, empresaIdOwn);
    setFilters(nextFilters);
    setFilterDraft(nextFilters);
    setOpenFilters(false);
    setPage(0);
  };

  const handleResetFilterDraft = () => {
    setFilterDraft(buildInitialFilters(isAdmin, empresaIdOwn));
  };

  const handleEdit = async () => {
    if (!selectedRow) return;

    const requestId = selectedRow?.raw?.id ?? selectedRow?.id;
    const roleById = new Map((rolesList || []).map((item) => [Number(item.id), item.nombre ?? item.name]));
    const companyById = new Map((empresasList || []).map((item) => [Number(item.id), item.nombre ?? item.name]));

    setFormLoading(true);

    try {
      // HU-037.4: consulta el detalle real para editar datos personales y
      // asignaciones con el contrato unificado del usuario.
      const resp = await axios.get(`/v1/usuarios/${requestId}`);
      const payload = resp?.data ?? {};
      const asignaciones = Array.isArray(payload.asignaciones) ? payload.asignaciones : [];
      const preferredAssignment =
        asignaciones.find(
          (item) =>
            Number(item?.usuarioRolId) === Number(payload?.rolPreferidoId) &&
            Number(item?.empresaId) === Number(payload?.empresaPreferidaId)
        ) ??
        asignaciones.find((item) => Number(item?.usuarioRolId) === Number(payload?.rolPreferidoId)) ??
        asignaciones.find(
          (item) =>
            Number(item?.rolId) === Number(payload?.rolPreferidoId) &&
            Number(item?.empresaId) === Number(payload?.empresaPreferidaId)
        ) ??
        asignaciones[0] ??
        null;

      setFormMode("edit");
      setFormData({
        requestId,
        username: payload?.username ?? selectedRow.username ?? "",
        nombre: payload?.nombre ?? selectedRow.nombre ?? "",
        apellido: payload?.apellido ?? selectedRow.apellido ?? "",
        genero: normalizeGenderForForm(payload?.genero),
        tipoIdentificacionId:
          payload?.tipoIdentificacionId ??
          payload?.tipoDocumentoIdentidadId ??
          selectedRow.raw?.tipoIdentificacionId ??
          "",
        identificacion:
          payload?.identificacion ??
          selectedRow.raw?.identificacion ??
          selectedRow.raw?.codigoIdentificacion ??
          "",
        fechaNacimiento: toDateInputValue(payload?.fechaNacimiento),
        estrato: payload?.estrato != null ? String(payload.estrato) : "",
        direccion: payload?.direccion ?? "",
        celular: payload?.celular ?? "",
        emailPersonal: payload?.emailPersonal ?? "",
        rolPreferidoId: payload?.rolPreferidoId ?? preferredAssignment?.rolId ?? "",
        empresaPreferidaId: payload?.empresaPreferidaId ?? preferredAssignment?.empresaId ?? "",
        estadoId: payload?.estadoId ?? selectedRow.estadoId ?? 1,
        asignaciones: asignaciones.map((item) => ({
          usuarioRolId: item?.usuarioRolId ?? "",
          empresaId: String(item?.empresaId ?? ""),
          empresaNombre: companyById.get(Number(item?.empresaId)) ?? item?.empresaNombre ?? "",
          rolId: String(item?.rolId ?? ""),
          rolNombre: roleById.get(Number(item?.rolId)) ?? item?.rolNombre ?? "",
          estadoId: Number(item?.estadoId ?? 1),
          iniciaContratoEn: toDateInputValue(item?.fechaInicioContrato ?? item?.iniciaContratoEn),
          finalizaContratoEn: toDateInputValue(item?.fechaFinContrato ?? item?.finalizaContratoEn),
          fechaFinalizacionValida:
            item?.fechaFinalizacionValida ??
            !(
              item?.fechaInicioContrato &&
              item?.fechaFinContrato &&
              new Date(item.fechaFinContrato) < new Date(item.fechaInicioContrato)
            ),
          preferido:
            (Number(item?.usuarioRolId) === Number(payload?.rolPreferidoId) &&
              Number(item?.empresaId) === Number(payload?.empresaPreferidaId)) ||
            (Number(item?.rolId) === Number(payload?.rolPreferidoId) &&
              Number(item?.empresaId) === Number(payload?.empresaPreferidaId)),
        })),
      });
      setOpenForm(true);
    } catch (err) {
      const status = err?.response?.status;
      const resolvedMessage =
        err?.response?.data?.message ??
        (status === 401 || status === 403
          ? t("usuario.messages.updateAccessDenied")
          : t("usuario.messages.detailLoadError"));
      setMessage({ open: true, severity: "error", text: resolvedMessage });
    } finally {
      setFormLoading(false);
    }
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

    if (!isAdmin && !isCompanyAdmin) {
      setMessage({
        open: true,
        severity: "warning",
        text: t("usuario.messages.statusActionRestrictedByRole"),
      });
      return;
    }

    if (!isAdmin && !selectedRowBelongsToContextCompany) {
      setMessage({
        open: true,
        severity: "warning",
        text: t("usuario.messages.statusActionRestrictedByCompany"),
      });
      return;
    }

    setStatusDialogError("");
    setOpenStatusDialog(true);
  };

  const handleCloseStatusDialog = () => {
    if (statusSubmitting) return;
    setOpenStatusDialog(false);
    setStatusDialogError("");
  };

  const handleConfirmStatusChange = async () => {
    if (!selectedRow) return;

    const requestId = selectedRow?.raw?.id ?? selectedRow?.id;
    const activating = selectedRowIsInactive;

    setStatusSubmitting(true);
    setStatusDialogError("");

    try {
      if (activating) {
        await axios.post(`/v1/usuarios/${requestId}/activar`);
      } else {
        await axios.delete(`/v1/usuarios/${requestId}`);
      }

      setOpenStatusDialog(false);
      await loadData();
      setMessage({
        open: true,
        severity: "success",
        text: activating
          ? t("usuario.messages.activateSuccess")
          : t("usuario.messages.inactivateSuccess"),
      });
    } catch (err) {
      setStatusDialogError(
        err?.response?.data?.message ??
          (activating
            ? t("usuario.messages.cannotActivate")
            : t("usuario.messages.cannotInactivate"))
      );
    } finally {
      setStatusSubmitting(false);
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
        body.nombre = payload.nombre?.trim() ?? "";
        body.apellido = payload.apellido?.trim() ?? "";
        body.emailPersonal = payload.emailPersonal?.trim() ?? "";
        body.genero = payload.genero;
        body.fechaNacimiento = payload.fechaNacimiento || null;
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
        const response = await axios.post("/v1/usuarios/registro", body);
        const successMessage =
          resolveBackendMessage(response?.data) || t("usuario.messages.createSuccess");
        setOpenForm(false);
        await loadData();
        setMessage({
          open: true,
          severity: "success",
          text: successMessage,
        });
      } else {
        const requestId =
          payload.requestId ??
          selectedRow?.raw?.id ??
          selectedRow?.id;
        const assignments = Array.isArray(payload.asignaciones) ? payload.asignaciones : [];
        const preferredAssignment =
          assignments.find((item) => Boolean(item?.preferido)) ??
          assignments[0] ??
          null;

        if (!requestId) {
          throw new Error("assignment-action-unavailable");
        }

        const body = {};
        // HU-037.4: consolida en un solo PUT la información personal y todas
        // las asignaciones del usuario con su rol/empresa preferidos.
        body.username = payload.username?.trim();
        body.tipoIdentificacionId = payload.tipoIdentificacionId
          ? Number(payload.tipoIdentificacionId)
          : null;
        body.identificacion = payload.identificacion?.trim() ?? "";
        body.nombre = payload.nombre?.trim() ?? "";
        body.apellido = payload.apellido?.trim() ?? "";
        body.emailPersonal = payload.emailPersonal?.trim() ?? "";
        body.genero = serializeGenderForUpdate(payload.genero);
        body.fechaNacimiento = payload.fechaNacimiento || null;
        body.direccion = payload.direccion?.trim() ?? "";
        body.celular = payload.celular?.trim() ?? "";
        body.estrato =
          payload.estrato === "" || payload.estrato == null
            ? null
            : Number(payload.estrato);
        // Registro 2026-06-30: rolPreferidoId debe corresponder al rolId de la
        // asignación marcada como preferida para validar este contrato.
        body.rolPreferidoId = preferredAssignment?.rolId
          ? Number(preferredAssignment.rolId)
          : null;
        body.empresaPreferidaId = preferredAssignment?.empresaId
          ? Number(isAdmin ? preferredAssignment.empresaId : empresaIdOwn)
          : null;
        body.asignaciones = assignments.map((item) => {
          const startValue = item.fechaInicioContrato ?? item.iniciaContratoEn;
          const endValue = item.fechaFinContrato ?? item.finalizaContratoEn;

          return {
            ...(item?.usuarioRolId
              ? { usuarioRolId: Number(item.usuarioRolId) }
              : {}),
            empresaId: Number(isAdmin ? item.empresaId : empresaIdOwn),
            rolId: Number(item.rolId),
            estadoId: Number(item?.estadoId ?? 1),
            // Registro 2026-06-30: el PUT replica la sintaxis del body de
            // ejemplo usando timestamps UTC tipo 2023-01-01T00:00:00Z.
            fechaInicioContrato: toUtcDateTime(startValue),
            fechaFinContrato: toUtcDateTime(endValue),
          };
        });

        const response = await axios.put(`/v1/usuarios/${requestId}`, body);
        const successMessage =
          resolveBackendMessage(response?.data) || t("usuario.messages.updateSuccess");
        setOpenForm(false);
        await loadData();
        setMessage({
          open: true,
          severity: "success",
          text: successMessage,
        });
      }
    } catch (err) {
      const fallbackMessage =
        err?.message === "assignment-action-unavailable"
          ? t("usuario.messages.assignmentActionUnavailable")
          : t("common.messages.operationError");
      setMessage({
        open: true,
        severity: "error",
        text: resolveBackendMessage(err?.response?.data) || fallbackMessage,
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
        onFilters={handleOpenFilters}
        onClearFilters={handleClearFilters}
        hasActiveFilters={hasActiveFilters}
        canUpdate={Boolean(selectedRow)}
        canDelete={canToggleUserStatus}
        labels={{ delete: deleteActionLabel }}
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
        loading={formLoading}
        initialData={formData}
        onSubmit={handleSubmit}
        roles={rolesList}
        empresas={empresasList}
        empresaRoles={empresaRolesList}
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

      <UserStatusConfirmDialog
        open={openStatusDialog}
        user={selectedRow}
        activating={selectedRowIsInactive}
        submitting={statusSubmitting}
        error={statusDialogError}
        onClose={handleCloseStatusDialog}
        onConfirm={handleConfirmStatusChange}
      />

      <UserFiltersDialog
        open={openFilters}
        onClose={() => setOpenFilters(false)}
        values={filterDraft}
        onChange={handleFilterDraftChange}
        onApply={handleApplyFilters}
        onClear={handleResetFilterDraft}
        roles={roleFilterOptions}
        empresas={companyFilterOptions}
        estados={statusFilterOptions}
        companyLocked={!isAdmin}
      />

      <MessageSnackBar message={message} setMessage={setMessage} />
    </Box>
  );
}
