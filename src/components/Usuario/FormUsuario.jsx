/*=============================================================================
Nombre del archivo : FormUsuario.jsx
Descripción        : Modal para registrar o actualizar usuarios del sistema.
===============================================================================
CONTROL DE CAMBIOS
+------------+---------+----------------------+-----------------------------------------------+
|   Fecha    | Versión |      Autor           | Descripción del cambio                        |
+------------+---------+----------------------+-----------------------------------------------+
| 2026-06-30 | 0.4.0   | Cesar Medina         | Se rediseña el modal y se alinea a HU-037.1.  |
| 2026-06-30 | 0.4.0   | Cesar Medina         | Se integra precarga por identificación.       |
| 2026-06-30 | 0.4.0   | Cesar Medina         | Se ajusta el modal para HU-037.4.             |
| 2026-08-12 | 0.4.0   | Cesar Medina         | Se alinea carga de roles por empresa activa.  |
| 2026-08-14 | 0.4.0   | Cesar Medina         | Se refuerzan validaciones y responsive.       |
+------------+---------+----------------------+-----------------------------------------------+
=============================================================================*/
/**
 * @module FormUsuario
 * @description Renderiza el formulario modal para registro y actualización de
 * usuarios, incluyendo datos personales y asignaciones de rol/empresa.
 */
import React, { useEffect, useMemo, useRef, useState } from "react";
import {
  Alert,
  Box,
  Button,
  Card,
  CardContent,
  Chip,
  CircularProgress,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Divider,
  FormControlLabel,
  Grid,
  IconButton,
  MenuItem,
  Stack,
  Switch,
  TextField,
  Typography,
} from "@mui/material";
import { alpha, useTheme } from "@mui/material/styles";
import { useTranslation } from "react-i18next";
import axios from "../axiosConfig";
import CloseIcon from "@mui/icons-material/Close";
import PersonOutlineIcon from "@mui/icons-material/PersonOutline";
import PermIdentityOutlinedIcon from "@mui/icons-material/PermIdentityOutlined";
import WorkOutlineOutlinedIcon from "@mui/icons-material/WorkOutlineOutlined";
import BusinessOutlinedIcon from "@mui/icons-material/BusinessOutlined";
import CalendarMonthOutlinedIcon from "@mui/icons-material/CalendarMonthOutlined";
import DeleteOutlineIcon from "@mui/icons-material/DeleteOutline";
import AddCircleOutlineIcon from "@mui/icons-material/AddCircleOutline";
import EmailOutlinedIcon from "@mui/icons-material/EmailOutlined";
import BadgeOutlinedIcon from "@mui/icons-material/BadgeOutlined";
import PhoneOutlinedIcon from "@mui/icons-material/PhoneOutlined";
import HomeOutlinedIcon from "@mui/icons-material/HomeOutlined";
import WcOutlinedIcon from "@mui/icons-material/WcOutlined";

const emptyAssignment = {
  rolId: "",
  rolNombre: "",
  empresaId: "",
  empresaNombre: "",
  iniciaContratoEn: "",
  finalizaContratoEn: "",
  preferido: false,
};

const PHONE_MAX_LENGTH = 10;
const IDENTIFICATION_MAX_LENGTH = 20;
const ADDRESS_MAX_LENGTH = 120;
const ADDRESS_REGEX =
  /^(?=.*[A-Za-zÁÉÍÓÚÜÑáéíóúüñ])(?=.*\d)[A-Za-zÁÉÍÓÚÜÑáéíóúüñ0-9\s#.,\-°/]+$/;
const STRATUM_OPTIONS = ["1", "2", "3", "4", "5", "6"];

const normalizeLookupKey = (value) =>
  String(value ?? "")
    .trim()
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "")
    .toLowerCase();

const resolveIdentificationRule = (label) => {
  const normalized = normalizeLookupKey(label);

  if (
    /cedula de ciudadania|cedula ciudadania|cedula de extranjeria|cedula extranjeria/.test(
      normalized
    )
  ) {
    return "numeric";
  }

  if (/dni|nit|pasaporte/.test(normalized)) {
    return "alphanumeric";
  }

  return "alphanumeric";
};

const parseRolesByCompany = () => {
  try {
    const raw = JSON.parse(localStorage.getItem("rolesByCompany") || "[]");
    return Array.isArray(raw) ? raw : [];
  } catch {
    return [];
  }
};

const buildAssignmentDraft = (isAdmin, sessionCompanyId, sessionCompanyName) => ({
  ...emptyAssignment,
  empresaId: isAdmin ? "" : String(sessionCompanyId || ""),
  empresaNombre: isAdmin ? "" : sessionCompanyName || "",
});

/**
 * Renderiza un encabezado de sección reutilizable.
 *
 * @param {object} props Propiedades del encabezado.
 * @param {React.ElementType} props.icon Icono visual.
 * @param {string} props.title Título de la sección.
 * @param {string} [props.description] Texto complementario.
 * @returns {JSX.Element}
 */
const SectionHeader = ({ icon: Icon, title, description }) => (
  <Stack spacing={0.45}>
    <Stack direction="row" spacing={1.25} alignItems="center">
      <Box
        sx={(theme) => ({
          width: 40,
          height: 40,
          borderRadius: 1.5,
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
          color: theme.palette.mode === "dark" ? "#E7F6F7" : "#173f39",
          backgroundColor:
            theme.palette.mode === "dark" ? alpha("#2b6b60", 0.24) : "#dfeae6",
        })}
      >
        <Icon fontSize="small" />
      </Box>
      <Typography
        variant="h6"
        sx={(theme) => ({
          fontSize: "1.05rem",
          fontWeight: 700,
          color: theme.palette.mode === "dark" ? "#E7F6F7" : "#173f39",
        })}
      >
        {title}
      </Typography>
    </Stack>
    {description ? (
      <Typography variant="body2" color="text.secondary">
        {description}
      </Typography>
    ) : null}
  </Stack>
);

export default function FormUsuario({
  open,
  onClose,
  mode = "create",
  loading = false,
  initialData,
  onSubmit,
  roles = [],
  empresas = [],
  empresaRoles = [],
  tiposIdentificacion = [],
  isAdmin = false,
  sessionCompanyId = "",
  sessionCompanyName = "",
}) {
  const { t } = useTranslation();
  const theme = useTheme();
  const isCreateMode = mode === "create";
  const green = theme.palette.mode === "dark" ? "#2b6b60" : "#173f39";
  const darkGreen = theme.palette.mode === "dark" ? "#E7F6F7" : "#173f39";
  const dialogSurface = theme.palette.mode === "dark" ? "#10211f" : theme.palette.common.white;
  const sectionSurface = theme.palette.mode === "dark" ? "#142b28" : theme.palette.common.white;
  const summarySurface = theme.palette.mode === "dark" ? alpha("#2b6b60", 0.28) : "#dfeae6";
  const assignmentSurface = theme.palette.mode === "dark" ? alpha("#2b6b60", 0.22) : "#F6FBF8";
  const subtleBorder = alpha(green, 0.14);
  const sectionShadow = `0 4px 14px ${alpha(darkGreen, theme.palette.mode === "dark" ? 0.14 : 0.05)}`;
  const contentMaxWidth = 1180;

  const [formData, setFormData] = useState(initialData);
  const [assignDraft, setAssignDraft] = useState(
    buildAssignmentDraft(isAdmin, sessionCompanyId, sessionCompanyName)
  );
  const [errors, setErrors] = useState({});
  const [assignmentErrors, setAssignmentErrors] = useState({});
  const [formAlert, setFormAlert] = useState("");
  const [lookupLoading, setLookupLoading] = useState(false);
  const [companyRolesLoading, setCompanyRolesLoading] = useState(false);
  const [selectedCompanyRoles, setSelectedCompanyRoles] = useState(
    Array.isArray(empresaRoles) ? empresaRoles : []
  );
  const [existingPersonInfo, setExistingPersonInfo] = useState(null);
  const [lastLookupIdentification, setLastLookupIdentification] = useState("");
  const latestCompanyRequestRef = useRef("");

  useEffect(() => {
    setFormData({
      ...(initialData || {}),
      tipoIdentificacionId:
        initialData?.tipoIdentificacionId ??
        initialData?.tipoDocumentoIdentidadId ??
        "",
      identificacion:
        initialData?.identificacion ??
        initialData?.codigoIdentificacion ??
        "",
      emailPersonal: initialData?.emailPersonal ?? "",
      asignaciones: Array.isArray(initialData?.asignaciones) ? initialData.asignaciones : [],
    });
    setAssignDraft(buildAssignmentDraft(isAdmin, sessionCompanyId, sessionCompanyName));
    setErrors({});
    setAssignmentErrors({});
    setFormAlert("");
    setLookupLoading(false);
    setCompanyRolesLoading(false);
    setSelectedCompanyRoles(isAdmin ? [] : Array.isArray(empresaRoles) ? empresaRoles : []);
    setExistingPersonInfo(null);
    setLastLookupIdentification("");
  }, [initialData, open, isAdmin, sessionCompanyId, sessionCompanyName, empresaRoles]);

  const empresasOptions = useMemo(
    () =>
      (Array.isArray(empresas) ? empresas : []).map((e) => ({
        value: String(e.id),
        label: e.nombre ?? e.name ?? String(e.id),
      })),
    [empresas]
  );

  const roleCatalogById = useMemo(
    () =>
      new Map(
        (Array.isArray(roles) ? roles : []).map((role) => [
          String(role.id),
          {
            value: String(role.id),
            label: role.nombre ?? role.name ?? String(role.id),
          },
        ])
      ),
    [roles]
  );

  const roleCatalogByName = useMemo(
    () => {
      const aliases = new Map();

      (Array.isArray(roles) ? roles : []).forEach((role) => {
        const option = {
          value: String(role.id),
          label: role.nombre ?? role.name ?? String(role.id),
        };

        [
          role.nombre,
          role.name,
          role.rolNombre,
          role.descripcion,
        ]
          .map((alias) => normalizeLookupKey(alias))
          .filter(Boolean)
          .forEach((alias) => {
            if (!aliases.has(alias)) {
              aliases.set(alias, option);
            }
          });
      });

      return aliases;
    },
    [roles]
  );

  const selectedAssignmentCompanyId = useMemo(
    () => (isAdmin ? String(assignDraft?.empresaId ?? "") : String(sessionCompanyId || "")),
    [assignDraft?.empresaId, isAdmin, sessionCompanyId]
  );

  const loadCompanyRolesByCompanyId = async (companyId) => {
    const normalizedCompanyId = String(companyId ?? "").trim();
    latestCompanyRequestRef.current = normalizedCompanyId;

    if (!normalizedCompanyId) {
      setSelectedCompanyRoles([]);
      setCompanyRolesLoading(false);
      return;
    }

    setCompanyRolesLoading(true);

    try {
      const response = await axios.get("/v1/empresa-rol/select", {
        params: { empresaId: Number(normalizedCompanyId) },
      });

      const resolvedRows = Array.isArray(response?.data)
        ? response.data
        : Array.isArray(response?.data?.content)
          ? response.data.content
          : [];
      const matchingRows = resolvedRows.filter(
        (item) => String(item?.empresaId ?? normalizedCompanyId) === normalizedCompanyId
      );
      const normalizedRows = (matchingRows.length > 0 ? matchingRows : resolvedRows).map((item) => ({
        ...item,
        empresaId: item?.empresaId ?? normalizedCompanyId,
      }));
      const propRows = (Array.isArray(empresaRoles) ? empresaRoles : []).filter(
        (item) => String(item?.empresaId ?? "") === normalizedCompanyId
      );
      const storageRows = parseRolesByCompany().filter(
        (item) => String(item?.empresaId ?? "") === normalizedCompanyId
      );
      const rowsToUse =
        normalizedRows.length > 0
          ? normalizedRows
          : propRows.length > 0
            ? propRows
            : storageRows;

      if (latestCompanyRequestRef.current === normalizedCompanyId) {
        setSelectedCompanyRoles(rowsToUse);
      }
    } catch (error) {
      const propRows = (Array.isArray(empresaRoles) ? empresaRoles : []).filter(
        (item) => String(item?.empresaId ?? "") === normalizedCompanyId
      );
      const storageRows = parseRolesByCompany().filter(
        (item) => String(item?.empresaId ?? "") === normalizedCompanyId
      );

      if (latestCompanyRequestRef.current === normalizedCompanyId) {
        setSelectedCompanyRoles(propRows.length > 0 ? propRows : storageRows);
      }
    } finally {
      if (latestCompanyRequestRef.current === normalizedCompanyId) {
        setCompanyRolesLoading(false);
      }
    }
  };

  useEffect(() => {
    if (!open) return;

    if (!selectedAssignmentCompanyId) {
      setSelectedCompanyRoles([]);
      setCompanyRolesLoading(false);
      return;
    }

    loadCompanyRolesByCompanyId(selectedAssignmentCompanyId);
  }, [open, selectedAssignmentCompanyId]);

  const availableRolesOptions = useMemo(() => {
    if (!selectedAssignmentCompanyId) return [];

    const rows = Array.isArray(selectedCompanyRoles) ? selectedCompanyRoles : [];
    const seen = new Set();
    const options = [];

    rows.forEach((item) => {
      const companyId = String(
        item?.empresaId ?? selectedAssignmentCompanyId ?? (!isAdmin ? sessionCompanyId : "") ?? ""
      );

      if (companyId !== selectedAssignmentCompanyId) return;

      const resolvedRole =
        roleCatalogById.get(String(item?.rolId ?? "")) ??
        roleCatalogByName.get(
          normalizeLookupKey(
            item?.rolNombre ?? item?.nombre ?? item?.name ?? item?.descripcion ?? ""
          )
        );

      const fallbackRole =
        resolvedRole ??
        (item?.rolId || item?.rolNombre || item?.nombre || item?.name
          ? {
              value: String(item?.rolId ?? item?.rolNombre ?? item?.nombre ?? item?.name),
              label: item?.nombre ?? item?.name ?? item?.rolNombre ?? String(item?.rolId ?? ""),
            }
          : null);

      if (!fallbackRole || seen.has(fallbackRole.value)) return;

      seen.add(fallbackRole.value);
      options.push(fallbackRole);
    });

    return options;
  }, [
    roleCatalogById,
    roleCatalogByName,
    selectedCompanyRoles,
    selectedAssignmentCompanyId,
  ]);

  const roleFieldHelperText =
    assignmentErrors.rolId ||
    (companyRolesLoading
      ? t("usuario.form.helpers.loadingCompanyRoles")
      : isAdmin && !selectedAssignmentCompanyId
      ? t("usuario.form.helpers.selectCompanyFirst")
      : availableRolesOptions.length === 0
        ? t("usuario.form.helpers.noRolesForCompany")
        : "");

  const tiposIdentificacionOptions = useMemo(
    () =>
      (Array.isArray(tiposIdentificacion) ? tiposIdentificacion : []).map((it) => ({
        value: String(it.id ?? it.code ?? ""),
        label: it.nombre ?? it.name ?? it.descripcion ?? String(it.id ?? it.code ?? ""),
      })),
    [tiposIdentificacion]
  );

  const selectedIdentificationLabel = useMemo(() => {
    return (
      tiposIdentificacionOptions.find(
        (item) => String(item.value) === String(formData?.tipoIdentificacionId ?? "")
      )?.label ?? ""
    );
  }, [formData?.tipoIdentificacionId, tiposIdentificacionOptions]);

  const requiresNumericIdentification = useMemo(() => {
    return resolveIdentificationRule(selectedIdentificationLabel) === "numeric";
  }, [selectedIdentificationLabel]);

  const handleChange = (name, value) => {
    let normalizedValue = value;

    if (name === "celular") {
      normalizedValue = String(value ?? "")
        .replace(/\D/g, "")
        .slice(0, PHONE_MAX_LENGTH);
    }

    if (name === "estrato") {
      normalizedValue = STRATUM_OPTIONS.includes(String(value ?? ""))
        ? String(value)
        : "";
    }

    if (name === "identificacion") {
      const rawValue = String(value ?? "");
      normalizedValue = requiresNumericIdentification
        ? rawValue.replace(/\D/g, "").slice(0, IDENTIFICATION_MAX_LENGTH)
        : rawValue.replace(/[^A-Za-z0-9-]/g, "").slice(0, IDENTIFICATION_MAX_LENGTH);
    }

    if (name === "direccion") {
      normalizedValue = String(value ?? "")
        .replace(/\s{2,}/g, " ")
        .slice(0, ADDRESS_MAX_LENGTH);
    }

    setFormData((prev) => {
      const nextState = { ...prev, [name]: normalizedValue };

      if (name === "tipoIdentificacionId") {
        const nextLabel =
          tiposIdentificacionOptions.find(
            (item) => String(item.value) === String(normalizedValue ?? "")
          )?.label ?? "";
        const nextRule = resolveIdentificationRule(nextLabel);
        const currentIdentification = String(prev?.identificacion ?? "");

        nextState.identificacion =
          nextRule === "numeric"
            ? currentIdentification.replace(/\D/g, "").slice(0, IDENTIFICATION_MAX_LENGTH)
            : currentIdentification
                .replace(/[^A-Za-z0-9-]/g, "")
                .slice(0, IDENTIFICATION_MAX_LENGTH);
      }

      if (
        isCreateMode &&
        existingPersonInfo &&
        (name === "identificacion" || name === "tipoIdentificacionId")
      ) {
        return {
          ...nextState,
          username: "",
          nombre: "",
          apellido: "",
          emailPersonal: "",
          genero: "",
          fechaNacimiento: "",
          direccion: "",
          celular: "",
          estrato: "",
        };
      }

      return nextState;
    });
    setErrors((prev) => ({ ...prev, [name]: "" }));
    setFormAlert("");

    if (
      isCreateMode &&
      (name === "identificacion" || name === "tipoIdentificacionId")
    ) {
      setExistingPersonInfo(null);
      setLastLookupIdentification("");
    }
  };

  const handleAssignChange = (name, value) => {
    setAssignDraft((prev) => {
      if (name === "empresaId") {
        const selectedCompany =
          empresasOptions.find((item) => String(item.value) === String(value ?? "")) ?? null;

        return {
          ...prev,
          empresaId: value,
          empresaNombre: selectedCompany?.label ?? "",
          rolId: "",
          rolNombre: "",
        };
      }

      if (name === "iniciaContratoEn") {
        const nextStart = String(value ?? "");
        const nextEnd = String(prev?.finalizaContratoEn ?? "");

        return {
          ...prev,
          iniciaContratoEn: nextStart,
          finalizaContratoEn:
            nextStart && nextEnd && new Date(nextEnd) >= new Date(nextStart) ? nextEnd : "",
        };
      }

      if (name === "finalizaContratoEn" && !prev?.iniciaContratoEn) {
        return prev;
      }

      return { ...prev, [name]: value };
    });
    setAssignmentErrors((prev) => ({
      ...prev,
      [name]: "",
      ...(name === "empresaId" ? { rolId: "" } : {}),
      ...(name === "iniciaContratoEn" ? { finalizaContratoEn: "" } : {}),
    }));
    setFormAlert("");
  };

  useEffect(() => {
    if (!assignDraft?.rolId) return;

    const roleStillAvailable = availableRolesOptions.some(
      (role) => String(role.value) === String(assignDraft.rolId)
    );

    if (roleStillAvailable) return;

    setAssignDraft((prev) => ({
      ...prev,
      rolId: "",
      rolNombre: "",
    }));
    setAssignmentErrors((prev) => ({ ...prev, rolId: "" }));
  }, [assignDraft?.rolId, availableRolesOptions]);

  const validateForm = () => {
    const nextErrors = {};
    const username = String(formData?.username ?? "").trim();
    const nombre = String(formData?.nombre ?? "").trim();
    const apellido = String(formData?.apellido ?? "").trim();
    const emailPersonal = String(formData?.emailPersonal ?? "").trim();
    const estrato = String(formData?.estrato ?? "").trim();
    const identificacion = String(formData?.identificacion ?? "").trim();
    const celular = String(formData?.celular ?? "").trim();
    const direccion = String(formData?.direccion ?? "").trim();

    if (!username) nextErrors.username = t("usuario.form.validation.required");
    else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(username)) {
      nextErrors.username = t("usuario.form.validation.invalidEmail");
    }

    if (!nombre) nextErrors.nombre = t("usuario.form.validation.required");
    if (!apellido) nextErrors.apellido = t("usuario.form.validation.required");

    if (!emailPersonal) {
      nextErrors.emailPersonal = t("usuario.form.validation.personalEmailRequired");
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(emailPersonal)) {
      nextErrors.emailPersonal = t("usuario.form.validation.invalidEmail");
    }

    if (!estrato) {
      nextErrors.estrato = t("usuario.form.validation.stratumRequired");
    }

    if (identificacion) {
      if (requiresNumericIdentification && !/^\d{5,20}$/.test(identificacion)) {
        nextErrors.identificacion = t("usuario.form.validation.invalidNumericDocumentNumber");
      } else if (!requiresNumericIdentification && !/^[A-Za-z0-9-]{5,20}$/.test(identificacion)) {
        nextErrors.identificacion = t("usuario.form.validation.invalidDocumentNumber");
      }
    }

    if (celular && !/^\d{10}$/.test(celular)) {
      nextErrors.celular = t("usuario.form.validation.invalidPhone");
    }

    if (direccion) {
      const normalizedAddress = direccion.replace(/\s+/g, " ").trim();
      if (
        normalizedAddress.length < 8 ||
        normalizedAddress.length > ADDRESS_MAX_LENGTH ||
        !ADDRESS_REGEX.test(normalizedAddress)
      ) {
        nextErrors.direccion = t("usuario.form.validation.invalidAddress");
      }
    }

    if (
      isCreateMode &&
      (!Array.isArray(formData?.asignaciones) || formData.asignaciones.length === 0)
    ) {
      setFormAlert(t("usuario.form.validation.assignmentRequired"));
    }

    setErrors(nextErrors);
    return Object.keys(nextErrors).length === 0;
  };

  const validateAssignmentDraft = () => {
    const nextErrors = {};

    if (!assignDraft.rolId) nextErrors.rolId = t("usuario.form.validation.roleRequired");
    if (isAdmin && !assignDraft.empresaId) {
      nextErrors.empresaId = t("usuario.form.validation.companyRequired");
    }

    if (assignDraft.finalizaContratoEn && !assignDraft.iniciaContratoEn) {
      nextErrors.finalizaContratoEn = t("usuario.form.validation.contractStartRequiredForEnd");
    }

    if (
      assignDraft.iniciaContratoEn &&
      assignDraft.finalizaContratoEn &&
      new Date(assignDraft.finalizaContratoEn) < new Date(assignDraft.iniciaContratoEn)
    ) {
      nextErrors.finalizaContratoEn = t("usuario.form.validation.contractDateOrder");
    }

    setAssignmentErrors(nextErrors);
    return Object.keys(nextErrors).length === 0;
  };

  const addAssign = () => {
    if (!validateAssignmentDraft()) return;

    const empresaIdResolved = isAdmin ? assignDraft.empresaId : String(sessionCompanyId || "");
    const empresaResolved =
      empresasOptions.find((item) => String(item.value) === String(empresaIdResolved)) ?? null;
    const rol = (Array.isArray(roles) ? roles : []).find(
      (r) => String(r.id) === String(assignDraft.rolId)
    );

    const draft = {
      ...assignDraft,
      empresaId: empresaIdResolved,
      empresaNombre:
        empresaResolved?.label ??
        assignDraft.empresaNombre ??
        (!isAdmin ? sessionCompanyName : ""),
      rolNombre: rol?.nombre ?? rol?.name ?? assignDraft.rolNombre,
    };

    let asignaciones = Array.isArray(formData?.asignaciones)
      ? [...formData.asignaciones]
      : [];

    // Registro 2026-06-10: mantiene una sola asignación preferida tal como se
    // documenta en la cabecera y lo exige la HU-037.1.
    if (draft.preferido) {
      asignaciones = asignaciones.map((item) => ({ ...item, preferido: false }));
    }

    asignaciones.push(draft);
    setFormData((prev) => ({ ...prev, asignaciones }));
    setAssignDraft(buildAssignmentDraft(isAdmin, sessionCompanyId, sessionCompanyName));
    setAssignmentErrors({});
    setFormAlert("");
  };

  const removeAssign = (idx) => {
    const asignaciones = Array.isArray(formData?.asignaciones)
      ? [...formData.asignaciones]
      : [];
    asignaciones.splice(idx, 1);
    setFormData((prev) => ({ ...prev, asignaciones }));
  };

  const togglePreferido = (idx) => {
    const asignaciones = Array.isArray(formData?.asignaciones)
      ? [...formData.asignaciones]
      : [];
    asignaciones.forEach((item, index) => {
      item.preferido = index === idx;
    });
    setFormData((prev) => ({ ...prev, asignaciones }));
  };

  const lookupPersonByIdentification = async () => {
    if (!isCreateMode) return;

    const identification = String(formData?.identificacion ?? "").trim();
    const tipoIdentificacionId = String(formData?.tipoIdentificacionId ?? "").trim();

    if (!identification || !tipoIdentificacionId) return;
    if (lookupLoading && lastLookupIdentification === identification) return;

    setLookupLoading(true);

    try {
      // Registro 2026-06-10: consulta la persona por identificación para
      // precargar sus datos y bloquear username cuando ya existe usuario.
      const response = await axios.get(`/v1/personas/${encodeURIComponent(identification)}`);
      const payload = response?.data ?? {};

      setFormData((prev) => ({
        ...prev,
        tipoIdentificacionId:
          payload?.tipoIdentificacionId != null
            ? String(payload.tipoIdentificacionId)
            : prev.tipoIdentificacionId,
        identificacion: payload?.identificacion ?? identification,
        nombre: payload?.nombre ?? prev.nombre ?? "",
        apellido: payload?.apellido ?? prev.apellido ?? "",
        emailPersonal: payload?.emailPersonal ?? prev.emailPersonal ?? "",
        genero: payload?.genero ?? prev.genero ?? "",
        fechaNacimiento: payload?.fechaNacimiento ?? prev.fechaNacimiento ?? "",
        direccion: payload?.direccion ?? prev.direccion ?? "",
        celular: payload?.celular ?? prev.celular ?? "",
        estrato:
          payload?.estrato != null
            ? String(payload.estrato)
            : prev.estrato ?? "",
        username:
          payload?.existeUsuario
            ? payload?.username ?? prev.username ?? ""
            : prev.username ?? "",
      }));

      setExistingPersonInfo({
        existeUsuario: Boolean(payload?.existeUsuario),
        username: payload?.username ?? "",
      });
      setLastLookupIdentification(identification);
      setFormAlert(
        payload?.existeUsuario
          ? t("usuario.form.messages.existingPersonLoaded")
          : t("usuario.form.messages.personLoaded")
      );
    } catch (error) {
      if (error?.response?.status === 404) {
        setExistingPersonInfo(null);
        setLastLookupIdentification("");
        setFormAlert(t("usuario.form.messages.personNotFound"));
      }
    } finally {
      setLookupLoading(false);
    }
  };

  const handleSave = () => {
    const isFormValid = validateForm();
    if (!isFormValid) return;

    if (
      isCreateMode &&
      (!Array.isArray(formData?.asignaciones) || formData.asignaciones.length === 0)
    ) {
      return;
    }

    onSubmit?.(formData);
  };

  const summaryAssignments = Array.isArray(formData?.asignaciones)
    ? formData.asignaciones
    : [];

  return (
    <Dialog
      open={open}
      onClose={onClose}
      maxWidth="lg"
      fullWidth
      PaperProps={{
        sx: {
          borderRadius: 3,
          overflow: "hidden",
          backgroundColor: dialogSurface,
          boxShadow: `0 10px 30px ${alpha(darkGreen, theme.palette.mode === "dark" ? 0.18 : 0.08)}`,
        },
      }}
    >
      <DialogTitle
        sx={{
          px: { xs: 2.25, sm: 3 },
          py: 2.15,
          backgroundColor: dialogSurface,
          borderTop: `3px solid ${darkGreen}`,
          borderBottom: `1px solid ${subtleBorder}`,
        }}
      >
        <Stack
          direction="row"
          alignItems="center"
          justifyContent="space-between"
          spacing={2}
          sx={{
            width: "100%",
            maxWidth: contentMaxWidth,
            mx: "auto",
          }}
        >
          <Stack direction="row" spacing={1.5} alignItems="center">
            <Box
              sx={{
                width: 40,
                height: 40,
                borderRadius: 1.5,
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                color: darkGreen,
                backgroundColor:
                  theme.palette.mode === "dark"
                    ? alpha("#2b6b60", 0.24)
                    : "#dfeae6",
              }}
            >
              <PersonOutlineIcon fontSize="small" />
            </Box>
            <Typography
              variant="h5"
              sx={{
                fontSize: "1.15rem",
                fontWeight: 700,
                color: darkGreen,
              }}
            >
              {isCreateMode ? t("usuario.form.createTitle") : t("usuario.form.editTitle")}
            </Typography>
          </Stack>
          <IconButton onClick={onClose} size="small" aria-label={t("common.actions.close")} sx={{ color: darkGreen }}>
            <CloseIcon sx={{ color: darkGreen }} />
          </IconButton>
        </Stack>
      </DialogTitle>

      <DialogContent
        dividers={false}
        sx={{
          px: { xs: 2.25, sm: 3 },
          pt: 6,
          pb: 2.5,
          backgroundColor: dialogSurface,
        }}
      >
        {/* HU-037.4: muestra un estado de carga mientras se consulta el detalle
            completo del usuario antes de habilitar la edición. */}
        {loading ? (
          <Box sx={{ py: 6 }}>
            <Stack spacing={2} alignItems="center" justifyContent="center">
              <CircularProgress />
              <Typography color="text.secondary">
                {t("usuario.form.messages.loadingEdit")}
              </Typography>
            </Stack>
          </Box>
        ) : (
          <Stack
            spacing={3}
            sx={{
              width: "100%",
              maxWidth: contentMaxWidth,
              mx: "auto",
              mt: 1.5,
            }}
          >
            <Card
              elevation={0}
              sx={{
                borderRadius: 2,
                border: `1px solid ${subtleBorder}`,
                boxShadow: sectionShadow,
                backgroundColor: summarySurface,
              }}
            >
              <CardContent sx={{ p: { xs: 2, sm: 2.5 } }}>
                <Stack spacing={1}>
                  <Typography variant="h6" sx={{ fontWeight: 700, color: darkGreen }}>
                    {isCreateMode
                      ? t("usuario.form.summaryTitle")
                      : t("usuario.form.editSummaryTitle")}
                  </Typography>
                  <Typography variant="body2" sx={{ color: darkGreen }}>
                    {isCreateMode
                      ? t("usuario.form.summaryDescription")
                      : t("usuario.form.editSummaryDescription")}
                  </Typography>
                  {!isAdmin ? (
                    <Alert
                      severity="info"
                      sx={{
                        mt: 1,
                        borderRadius: 2,
                        backgroundColor: alpha(theme.palette.info.main, 0.08),
                      }}
                    >
                      {t(
                        isCreateMode
                          ? "usuario.form.messages.autoCompanyAssigned"
                          : "usuario.form.messages.companyRestricted",
                        {
                          company:
                            sessionCompanyName || t("usuario.form.messages.currentCompany"),
                        }
                      )}
                    </Alert>
                  ) : null}
                  {formAlert ? (
                    <Alert
                      severity={
                        existingPersonInfo?.existeUsuario
                          ? "info"
                          : formAlert === t("usuario.form.messages.personNotFound")
                            ? "success"
                            : "warning"
                      }
                      sx={{ mt: 1, borderRadius: 2 }}
                    >
                      {formAlert}
                    </Alert>
                  ) : null}
                </Stack>
              </CardContent>
            </Card>

            <Box
              sx={{
                display: "grid",
                gridTemplateColumns: {
                  xs: "1fr",
                  lg: "minmax(0, 1.45fr) minmax(0, 1fr)",
                },
                gap: 3,
                alignItems: "start",
                width: "100%",
              }}
            >
            <Box sx={{ minWidth: 0 }}>
              <Card
                sx={{
                  borderRadius: 2,
                  border: `1px solid ${subtleBorder}`,
                  boxShadow: sectionShadow,
                  backgroundColor: sectionSurface,
                }}
              >
                <CardContent sx={{ p: { xs: 2.75, sm: 3.25 } }}>
                  <Stack spacing={3}>
                    <SectionHeader
                      icon={PermIdentityOutlinedIcon}
                      title={t("usuario.form.sections.personalInformation")}
                      description={t("usuario.form.sections.personalInformationDescription")}
                    />

                    <Grid
                      container
                      rowSpacing={2}
                      columnSpacing={{ xs: 0, md: 2 }}
                      sx={{
                        mt: 0.25,
                        width: "100%",
                      }}
                    >
                      <Grid item xs={12} md={6}>
                        <TextField
                          label={t("usuario.form.fields.username")}
                          type="email"
                          value={formData?.username ?? ""}
                          onChange={(e) => handleChange("username", e.target.value)}
                          fullWidth
                          required
                          disabled={Boolean(isCreateMode && existingPersonInfo?.existeUsuario)}
                          error={Boolean(errors.username)}
                          helperText={
                            errors.username ||
                            (isCreateMode && existingPersonInfo?.existeUsuario
                              ? t("usuario.form.helpers.existingUsernameLocked")
                              : "")
                          }
                          InputLabelProps={{ shrink: true }}
                          InputProps={{
                            startAdornment: (
                              <EmailOutlinedIcon
                                fontSize="small"
                                sx={{ color: alpha(darkGreen, 0.7), mr: 1 }}
                              />
                            ),
                          }}
                        />
                      </Grid>
                      <Grid item xs={12} md={6}>
                        <TextField
                          label={t("usuario.form.fields.personalEmail")}
                          type="email"
                          value={formData?.emailPersonal ?? ""}
                          onChange={(e) => handleChange("emailPersonal", e.target.value)}
                          fullWidth
                          required
                          error={Boolean(errors.emailPersonal)}
                          helperText={errors.emailPersonal}
                          InputLabelProps={{ shrink: true }}
                        />
                      </Grid>
                      <Grid item xs={12} md={6}>
                        <TextField
                          label={t("usuario.form.fields.name")}
                          value={formData?.nombre ?? ""}
                          onChange={(e) => handleChange("nombre", e.target.value)}
                          fullWidth
                          required
                          error={Boolean(errors.nombre)}
                          helperText={errors.nombre}
                          InputLabelProps={{ shrink: true }}
                        />
                      </Grid>
                      <Grid item xs={12} md={6}>
                        <TextField
                          label={t("usuario.form.fields.lastName")}
                          value={formData?.apellido ?? ""}
                          onChange={(e) => handleChange("apellido", e.target.value)}
                          fullWidth
                          required
                          error={Boolean(errors.apellido)}
                          helperText={errors.apellido}
                          InputLabelProps={{ shrink: true }}
                        />
                      </Grid>
                      <Grid item xs={12} md={6}>
                        <TextField
                          select
                          label={t("usuario.form.fields.gender")}
                          value={formData?.genero ?? ""}
                          onChange={(e) => handleChange("genero", e.target.value)}
                          fullWidth
                          InputLabelProps={{ shrink: true }}
                        >
                          <MenuItem value="">{t("common.labels.select")}</MenuItem>
                          <MenuItem value="M">{t("usuario.form.options.genderMale")}</MenuItem>
                          <MenuItem value="F">{t("usuario.form.options.genderFemale")}</MenuItem>
                        </TextField>
                      </Grid>
                      <Grid item xs={12} md={6}>
                        <TextField
                          select
                          label={t("usuario.form.fields.stratum")}
                          value={formData?.estrato ?? ""}
                          onChange={(e) => handleChange("estrato", e.target.value)}
                          fullWidth
                          required
                          error={Boolean(errors.estrato)}
                          helperText={errors.estrato}
                          InputLabelProps={{ shrink: true }}
                        >
                          <MenuItem value="">{t("common.labels.select")}</MenuItem>
                          {STRATUM_OPTIONS.map((item) => (
                            <MenuItem key={item} value={item}>
                              {item}
                            </MenuItem>
                          ))}
                        </TextField>
                      </Grid>
                      <Grid item xs={12} md={6}>
                        <TextField
                          select
                          label={t("usuario.form.fields.documentType")}
                          value={formData?.tipoIdentificacionId ?? ""}
                          onChange={(e) => handleChange("tipoIdentificacionId", e.target.value)}
                          onBlur={lookupPersonByIdentification}
                          fullWidth
                          InputLabelProps={{ shrink: true }}
                        >
                          <MenuItem value="">{t("common.labels.select")}</MenuItem>
                          {tiposIdentificacionOptions.map((item) => (
                            <MenuItem key={item.value} value={item.value}>
                              {item.label}
                            </MenuItem>
                          ))}
                        </TextField>
                      </Grid>
                      <Grid item xs={12} md={6}>
                        <TextField
                          label={t("usuario.form.fields.documentNumber")}
                          value={formData?.identificacion ?? ""}
                          onChange={(e) => handleChange("identificacion", e.target.value)}
                          onBlur={lookupPersonByIdentification}
                          fullWidth
                          error={Boolean(errors.identificacion)}
                          helperText={
                            errors.identificacion ||
                            (lookupLoading
                              ? t("usuario.form.helpers.lookupLoading")
                              : "")
                          }
                          InputLabelProps={{ shrink: true }}
                          inputProps={{
                            inputMode: requiresNumericIdentification ? "numeric" : "text",
                            pattern: requiresNumericIdentification ? "[0-9]*" : "[A-Za-z0-9-]*",
                            maxLength: IDENTIFICATION_MAX_LENGTH,
                          }}
                          InputProps={{
                            startAdornment: (
                              <BadgeOutlinedIcon
                                fontSize="small"
                                sx={{ color: alpha(darkGreen, 0.7), mr: 1 }}
                              />
                            ),
                          }}
                        />
                      </Grid>
                      <Grid item xs={12} md={6}>
                        <TextField
                          label={t("usuario.form.fields.birthDate")}
                          type="date"
                          value={formData?.fechaNacimiento ?? ""}
                          onChange={(e) => handleChange("fechaNacimiento", e.target.value)}
                          fullWidth
                          InputLabelProps={{ shrink: true }}
                          InputProps={{
                            startAdornment: (
                              <CalendarMonthOutlinedIcon
                                fontSize="small"
                                sx={{ color: alpha(darkGreen, 0.7), mr: 1 }}
                              />
                            ),
                          }}
                        />
                      </Grid>
                      <Grid item xs={12} md={6}>
                        <TextField
                          label={t("usuario.form.fields.phone")}
                          value={formData?.celular ?? ""}
                          onChange={(e) => handleChange("celular", e.target.value)}
                          fullWidth
                          error={Boolean(errors.celular)}
                          helperText={errors.celular}
                          InputLabelProps={{ shrink: true }}
                          inputProps={{
                            inputMode: "numeric",
                            pattern: "[0-9]*",
                            maxLength: PHONE_MAX_LENGTH,
                          }}
                          InputProps={{
                            startAdornment: (
                              <PhoneOutlinedIcon
                                fontSize="small"
                                sx={{ color: alpha(darkGreen, 0.7), mr: 1 }}
                              />
                            ),
                          }}
                        />
                      </Grid>
                      <Grid item xs={12}>
                        <TextField
                          label={t("usuario.form.fields.address")}
                          value={formData?.direccion ?? ""}
                          onChange={(e) => handleChange("direccion", e.target.value)}
                          fullWidth
                          error={Boolean(errors.direccion)}
                          helperText={errors.direccion}
                          InputLabelProps={{ shrink: true }}
                          inputProps={{
                            maxLength: ADDRESS_MAX_LENGTH,
                          }}
                          InputProps={{
                            startAdornment: (
                              <HomeOutlinedIcon
                                fontSize="small"
                                sx={{ color: alpha(darkGreen, 0.7), mr: 1, mt: 0.5 }}
                              />
                            ),
                          }}
                        />
                      </Grid>
                    </Grid>
                  </Stack>
                </CardContent>
              </Card>
            </Box>

            <Box sx={{ minWidth: 0 }}>
              <Stack spacing={3}>
                <Card
                  sx={{
                    borderRadius: 2,
                    border: `1px solid ${subtleBorder}`,
                    boxShadow: sectionShadow,
                    backgroundColor: sectionSurface,
                  }}
                >
                  <CardContent sx={{ p: { xs: 2, sm: 2.5 } }}>
                    <Stack spacing={2.5}>
                      <SectionHeader
                        icon={WorkOutlineOutlinedIcon}
                        title={t("usuario.form.sections.assignmentConfiguration")}
                        description={t("usuario.form.sections.assignmentConfigurationDescription")}
                      />

                      <TextField
                        select
                        label={t("usuario.form.fields.company")}
                        value={selectedAssignmentCompanyId}
                        onChange={(e) => handleAssignChange("empresaId", e.target.value)}
                        fullWidth
                        required={isAdmin}
                        disabled={!isAdmin}
                        error={Boolean(assignmentErrors.empresaId)}
                        helperText={
                          assignmentErrors.empresaId ||
                          (!isAdmin
                            ? t("usuario.form.helpers.autoAssignedCompany", {
                                company:
                                  sessionCompanyName || t("usuario.form.messages.currentCompany"),
                              })
                            : "")
                        }
                        InputLabelProps={{ shrink: true }}
                      >
                        <MenuItem value="">{t("common.labels.select")}</MenuItem>
                        {empresasOptions.map((company) => (
                          <MenuItem key={company.value} value={company.value}>
                            {company.label}
                          </MenuItem>
                        ))}
                      </TextField>

                      <TextField
                        select
                        label={t("usuario.form.fields.role")}
                        value={assignDraft.rolId}
                        onChange={(e) => handleAssignChange("rolId", e.target.value)}
                        fullWidth
                        required
                        disabled={
                          !selectedAssignmentCompanyId ||
                          companyRolesLoading ||
                          availableRolesOptions.length === 0
                        }
                        error={Boolean(assignmentErrors.rolId)}
                        helperText={roleFieldHelperText}
                        InputLabelProps={{ shrink: true }}
                      >
                        <MenuItem value="">{t("common.labels.select")}</MenuItem>
                        {availableRolesOptions.map((role) => (
                          <MenuItem key={role.value} value={role.value}>
                            {role.label}
                          </MenuItem>
                        ))}
                      </TextField>

                      <Box
                        sx={{
                          display: "grid",
                          gridTemplateColumns: { xs: "1fr", sm: "repeat(2, minmax(0, 1fr))" },
                          columnGap: { xs: 0, sm: 2 },
                          rowGap: 2,
                          width: "100%",
                        }}
                      >
                        <Box>
                          <TextField
                            label={t("usuario.form.fields.contractStart")}
                            type="date"
                            value={assignDraft.iniciaContratoEn}
                            onChange={(e) =>
                              handleAssignChange("iniciaContratoEn", e.target.value)
                            }
                            fullWidth
                            InputLabelProps={{ shrink: true }}
                          />
                        </Box>
                        <Box>
                          <TextField
                            label={t("usuario.form.fields.contractEnd")}
                            type="date"
                            value={assignDraft.finalizaContratoEn}
                            onChange={(e) =>
                              handleAssignChange("finalizaContratoEn", e.target.value)
                            }
                            fullWidth
                            disabled={!assignDraft.iniciaContratoEn}
                            error={Boolean(assignmentErrors.finalizaContratoEn)}
                            helperText={
                              assignmentErrors.finalizaContratoEn ||
                              (!assignDraft.iniciaContratoEn
                                ? t("usuario.form.helpers.selectContractStartFirst")
                                : "")
                            }
                            InputLabelProps={{ shrink: true }}
                          />
                        </Box>
                      </Box>

                      <FormControlLabel
                        control={
                          <Switch
                            checked={Boolean(assignDraft.preferido)}
                            onChange={(e) =>
                              handleAssignChange("preferido", e.target.checked)
                            }
                          />
                        }
                        label={t("usuario.form.fields.preferredRole")}
                      />

                      <Button
                        variant="outlined"
                        onClick={addAssign}
                        startIcon={<AddCircleOutlineIcon />}
                        sx={{
                          alignSelf: "flex-start",
                          borderColor: alpha(green, 0.26),
                          color: darkGreen,
                        }}
                      >
                        {t("common.actions.addAssignment")}
                      </Button>
                    </Stack>
                  </CardContent>
                </Card>

                <Card
                  sx={{
                    borderRadius: 2,
                    border: `1px solid ${subtleBorder}`,
                    boxShadow: sectionShadow,
                    backgroundColor: sectionSurface,
                  }}
                >
                  <CardContent sx={{ p: { xs: 2, sm: 2.5 } }}>
                    <Stack spacing={2}>
                      <SectionHeader
                        icon={BusinessOutlinedIcon}
                        title={t("usuario.form.sections.assignmentList")}
                        description={t("usuario.form.sections.assignmentListDescription")}
                      />

                      {summaryAssignments.length === 0 ? (
                        <Alert severity="info" sx={{ borderRadius: 2 }}>
                          {t("usuario.form.messages.assignmentRequired")}
                        </Alert>
                      ) : (
                        <Stack spacing={1.25}>
                          {summaryAssignments.map((assignment, idx) => (
                            <Card
                              key={`${assignment.rolId}-${assignment.empresaId}-${idx}`}
                              sx={{
                                borderRadius: 1.75,
                                border: `1px solid ${alpha(green, 0.1)}`,
                                backgroundColor: assignmentSurface,
                                boxShadow: `0 2px 8px ${alpha(
                                  darkGreen,
                                  theme.palette.mode === "dark" ? 0.12 : 0.035
                                )}`,
                              }}
                            >
                              <CardContent sx={{ py: 1.75 }}>
                                <Stack spacing={1.25}>
                                  <Stack spacing={1.25} sx={{ width: "100%", minWidth: 0 }}>
                                    <Stack
                                      direction="row"
                                      spacing={1}
                                      useFlexGap
                                      flexWrap="wrap"
                                      sx={{ minWidth: 0, flex: 1 }}
                                    >
                                      <Chip
                                        icon={<WorkOutlineOutlinedIcon />}
                                        label={`${t("common.labels.role")}: ${
                                          assignment.rolNombre || "-"
                                        }`}
                                        size="small"
                                        sx={{
                                          maxWidth: "100%",
                                          color: darkGreen,
                                          backgroundColor:
                                            theme.palette.mode === "dark"
                                              ? alpha("#2b6b60", 0.26)
                                              : "#dfeae6",
                                          "& .MuiChip-label": {
                                            display: "block",
                                          },
                                          "& .MuiChip-icon": { color: darkGreen },
                                        }}
                                      />
                                      <Chip
                                        icon={<BusinessOutlinedIcon />}
                                        label={`${t("common.labels.company")}: ${
                                          assignment.empresaNombre || "-"
                                        }`}
                                        size="small"
                                        sx={{
                                          maxWidth: "100%",
                                          color: darkGreen,
                                          backgroundColor:
                                            theme.palette.mode === "dark"
                                              ? alpha("#2b6b60", 0.26)
                                              : "#dfeae6",
                                          "& .MuiChip-label": {
                                            display: "block",
                                          },
                                          "& .MuiChip-icon": { color: darkGreen },
                                        }}
                                      />
                                      {assignment.preferido ? (
                                        <Chip
                                          label={t("usuario.form.fields.preferred")}
                                          size="small"
                                          sx={{
                                            fontWeight: 700,
                                            color: darkGreen,
                                            backgroundColor: alpha(green, 0.12),
                                          }}
                                        />
                                      ) : null}
                                    </Stack>
                                    <Stack
                                      direction="row"
                                      spacing={1}
                                      useFlexGap
                                      flexWrap="wrap"
                                      sx={{
                                        width: "100%",
                                        justifyContent: "flex-start",
                                        alignItems: "center",
                                      }}
                                    >
                                      <FormControlLabel
                                        sx={{ m: 0 }}
                                        control={
                                          <Switch
                                            checked={Boolean(assignment.preferido)}
                                            onChange={() => togglePreferido(idx)}
                                          />
                                        }
                                        label={t("usuario.form.fields.preferred")}
                                      />
                                      <Button
                                        color="error"
                                        onClick={() => removeAssign(idx)}
                                        startIcon={<DeleteOutlineIcon />}
                                        sx={{ whiteSpace: "nowrap" }}
                                      >
                                        {t("common.actions.delete")}
                                      </Button>
                                    </Stack>
                                  </Stack>
                                  <Divider sx={{ borderColor: alpha(darkGreen, 0.14) }} />
                                  <Grid container spacing={2}>
                                    <Grid item xs={12} sm={6}>
                                      <Stack direction="row" spacing={1} alignItems="center">
                                        <CalendarMonthOutlinedIcon
                                          fontSize="small"
                                          sx={{ color: darkGreen }}
                                        />
                                        <Typography variant="body2" sx={{ color: darkGreen }}>
                                          {t("common.labels.start")}:{" "}
                                          {assignment.iniciaContratoEn || "-"}
                                        </Typography>
                                      </Stack>
                                    </Grid>
                                    <Grid item xs={12} sm={6}>
                                      <Stack direction="row" spacing={1} alignItems="center">
                                        <CalendarMonthOutlinedIcon
                                          fontSize="small"
                                          sx={{ color: darkGreen }}
                                        />
                                        <Typography variant="body2" sx={{ color: darkGreen }}>
                                          {t("common.labels.end")}:{" "}
                                          {assignment.finalizaContratoEn ||
                                            t("common.labels.withoutDate")}
                                        </Typography>
                                      </Stack>
                                    </Grid>
                                  </Grid>
                                </Stack>
                              </CardContent>
                            </Card>
                          ))}
                        </Stack>
                      )}
                    </Stack>
                  </CardContent>
                </Card>
              </Stack>
            </Box>
            </Box>
          </Stack>
        )}
      </DialogContent>

      <DialogActions
        sx={{
          px: { xs: 2.25, sm: 4 },
          py: 2,
          backgroundColor: dialogSurface,
          borderTop: `1px solid ${subtleBorder}`,
        }}
      >
        <Stack
          direction="row"
          spacing={1.5}
          justifyContent="flex-end"
          sx={{
            width: "100%",
            maxWidth: contentMaxWidth,
            mx: "auto",
          }}
        >
          <Button onClick={onClose}>{t("common.actions.cancel")}</Button>
          <Button
            variant="contained"
            onClick={handleSave}
            disabled={loading}
            sx={{
              minWidth: 120,
              fontWeight: 700,
              borderRadius: 1.5,
              boxShadow: "none",
              backgroundColor: darkGreen,
              "&:hover": {
                backgroundColor: green,
                boxShadow: "none",
              },
            }}
          >
            {isCreateMode ? t("usuario.form.actions.register") : t("common.actions.save")}
          </Button>
        </Stack>
      </DialogActions>
    </Dialog>
  );
}
