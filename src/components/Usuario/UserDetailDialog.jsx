/*=============================================================================
Nombre del archivo : UserDetailDialog.jsx
Descripción        : Modal para visualizar el detalle de un usuario y sus asignaciones.
===============================================================================
CONTROL DE CAMBIOS
+------------+---------+----------------------+-----------------------------------------------+
|   Fecha    | Versión |      Autor           | Descripción del cambio                        |
+------------+---------+----------------------+-----------------------------------------------+
| 2026-05-08 | 0.4.0   | Cesar Medina         | Creación del archivo.                         |
| 2026-06-03 | 0.4.0   | Cesar Medina         | Se documenta y ajusta detalle visual del modal|
+------------+---------+----------------------+-----------------------------------------------+
=============================================================================*/
/**
 * @module UserDetailDialog
 * @description Presenta la información personal del usuario y el listado de
 * asignaciones usando el detalle recuperado desde el backend.
 */
import React from "react";
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Stack,
  Typography,
  Button,
  Avatar,
  Chip,
  Card,
  CardContent,
  Grid,
  Divider,
  CircularProgress,
  Alert,
  Box,
  IconButton,
} from "@mui/material";
import { alpha, useTheme } from "@mui/material/styles";
import CloseIcon from "@mui/icons-material/Close";
import PersonOutlineIcon from "@mui/icons-material/PersonOutline";
import PermIdentityOutlinedIcon from "@mui/icons-material/PermIdentityOutlined";
import BadgeOutlinedIcon from "@mui/icons-material/BadgeOutlined";
import CalendarMonthOutlinedIcon from "@mui/icons-material/CalendarMonthOutlined";
import PhoneOutlinedIcon from "@mui/icons-material/PhoneOutlined";
import LocationOnOutlinedIcon from "@mui/icons-material/LocationOnOutlined";
import BusinessOutlinedIcon from "@mui/icons-material/BusinessOutlined";
import WorkOutlineOutlinedIcon from "@mui/icons-material/WorkOutlineOutlined";
import CheckCircleOutlineIcon from "@mui/icons-material/CheckCircleOutline";
import WcOutlinedIcon from "@mui/icons-material/WcOutlined";
import { useTranslation } from "react-i18next";

const formatDate = (value, language) => {
  if (!value) return "";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return value;
  return new Intl.DateTimeFormat(language, {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
  }).format(date);
};

const formatDateTime = (value, language) => {
  if (!value) return "";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return value;
  return new Intl.DateTimeFormat(language, {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
  }).format(date);
};

/**
 * Renderiza un campo de detalle con icono, etiqueta y valor.
 *
 * @param {object} props Propiedades del campo.
 * @param {React.ElementType} props.icon Icono a mostrar.
 * @param {string} props.label Etiqueta del campo.
 * @param {string} props.value Valor del campo.
 * @param {boolean} [props.showDivider=true] Define si se muestra separador.
 * @returns {JSX.Element}
 */
const DetailField = ({ icon: Icon, label, value, showDivider = true }) => (
  <Stack spacing={1.1}>
    <Stack direction="row" spacing={1.2} alignItems="flex-start">
      <Box
        sx={(theme) => ({
          width: 30,
          height: 30,
          borderRadius: 1.2,
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
          backgroundColor:
            theme.palette.mode === "dark"
              ? alpha("#2b6b60", 0.24)
              : "#dfeae6",
          color: theme.palette.mode === "dark" ? "#E7F6F7" : "#173f39",
          flexShrink: 0,
        })}
      >
        <Icon fontSize="small" sx={{ color: "inherit" }} />
      </Box>
      <Stack spacing={0.35} sx={{ minWidth: 0 }}>
        <Typography
          variant="caption"
          sx={(theme) => ({
            fontWeight: 700,
            color: theme.palette.mode === "dark" ? "#E7F6F7" : "#173f39",
          })}
        >
          {label}
        </Typography>
        <Typography
          variant="body1"
          sx={(theme) => ({
            fontWeight: 500,
            wordBreak: "break-word",
            color: theme.palette.mode === "dark" ? "#E7F6F7" : "#173f39",
          })}
        >
          {value || "-"}
        </Typography>
      </Stack>
    </Stack>
    {showDivider ? (
      <Divider
        sx={(theme) => ({
          borderColor:
            theme.palette.mode === "dark"
              ? alpha("#E7F6F7", 0.14)
              : alpha("#173f39", 0.12),
        })}
      />
    ) : null}
  </Stack>
);

/**
 * Encabezado visual reutilizable para las secciones internas del modal.
 *
 * @param {object} props Propiedades del encabezado.
 * @param {React.ElementType} props.icon Icono de la sección.
 * @param {string} props.title Título visible de la sección.
 * @returns {JSX.Element}
 */
const SectionHeader = ({ icon: Icon, title }) => (
  <Stack direction="row" spacing={1.5} alignItems="center" sx={{ mb: 2.25 }}>
    <Box
      sx={(theme) => ({
        width: 42,
        height: 42,
        borderRadius: 1.5,
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        color: theme.palette.mode === "dark" ? "#E7F6F7" : "#173f39",
        backgroundColor:
          theme.palette.mode === "dark"
            ? alpha("#2b6b60", 0.24)
            : "#dfeae6",
      })}
    >
      <Icon fontSize="small" />
    </Box>
    <Typography
      variant="h6"
      sx={(theme) => ({
        fontSize: "1.1rem",
        fontWeight: 700,
        color: theme.palette.mode === "dark" ? "#E7F6F7" : "#173f39",
      })}
    >
      {title}
    </Typography>
  </Stack>
);

/**
 * Muestra el modal de detalle del usuario.
 *
 * @param {object} props Propiedades del modal.
 * @param {boolean} props.open Controla la visibilidad del diálogo.
 * @param {object|null} props.data Información del usuario a renderizar.
 * @param {boolean} [props.loading=false] Indica si el detalle está cargando.
 * @param {string} [props.error=""] Mensaje de error de carga.
 * @param {Function} props.onClose Acción para cerrar el diálogo.
 * @returns {JSX.Element}
 */
export default function UserDetailDialog({ open, data, loading = false, error = "", onClose }) {
  const { t, i18n } = useTranslation();
  const theme = useTheme();
  const language = String(i18n.resolvedLanguage || i18n.language || "es")
    .toLowerCase()
    .startsWith("en")
    ? "en"
    : "es";
  const firstName = data?.nombre ?? data?.nombreCompleto?.split(" ")?.[0] ?? "";
  const lastName = data?.apellido ?? data?.nombreCompleto?.split(" ")?.slice(1).join(" ") ?? "";
  const initials = String(firstName).charAt(0).toUpperCase() + String(lastName).charAt(0).toUpperCase();
  const asignaciones = Array.isArray(data?.asignaciones) ? data.asignaciones : [];
  const documentValue = data?.identificacion ?? data?.documento ?? "";
  const fullName = [data?.nombre ?? firstName, data?.apellido ?? lastName].filter(Boolean).join(" ");
  const isActive = Number(data?.estadoId ?? 1) === 1;
  const statusLabel = isActive ? t("common.labels.active") : t("common.labels.inactive");
  const darkGreen = theme.palette.mode === "dark" ? "#E7F6F7" : "#173f39";
  const green = theme.palette.mode === "dark" ? "#2b6b60" : "#173f39";
  const lightGreen = theme.palette.mode === "dark" ? alpha("#2b6b60", 0.24) : "#E7F6F7";
  const lighterGreen = theme.palette.mode === "dark" ? alpha("#2b6b60", 0.18) : "#dfeae6";
  const dialogSurface = theme.palette.mode === "dark" ? "#10211f" : theme.palette.common.white;
  const sectionSurface = theme.palette.mode === "dark" ? "#142b28" : theme.palette.common.white;
  const summarySurface = theme.palette.mode === "dark" ? alpha("#2b6b60", 0.28) : lighterGreen;
  const assignmentSurface = theme.palette.mode === "dark" ? alpha("#2b6b60", 0.22) : "#F6FBF8";
  const subtleBorder = alpha(green, 0.14);
  const subtleDivider = alpha(darkGreen, 0.14);
  const softShadow = `0 10px 30px ${alpha(darkGreen, theme.palette.mode === "dark" ? 0.18 : 0.08)}`;
  const sectionShadow = `0 4px 14px ${alpha(darkGreen, theme.palette.mode === "dark" ? 0.14 : 0.05)}`;

  return (
    <Dialog
      open={open}
      onClose={onClose}
      maxWidth="md"
      fullWidth
      PaperProps={{
        sx: {
          borderRadius: 3,
          overflow: "hidden",
          boxShadow: softShadow,
          backgroundColor: dialogSurface,
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
        <Stack direction="row" alignItems="center" justifyContent="space-between" spacing={2}>
          <Stack direction="row" spacing={1.5} alignItems="center">
            <Box
              sx={{
                width: 40,
                height: 40,
                borderRadius: 1.5,
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                color: theme.palette.mode === "dark" ? "#E7F6F7" : "#173f39",
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
              {t("usuario.detail.title")}
            </Typography>
          </Stack>
          <IconButton
            onClick={onClose}
            size="small"
            aria-label={t("common.actions.close")}
            sx={{
              color: darkGreen,
            }}
          >
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
        {loading ? (
          <Box sx={{ py: 6 }}>
            <Stack spacing={2} alignItems="center" justifyContent="center">
              <CircularProgress />
              <Typography color="text.secondary">{t("usuario.detail.loading")}</Typography>
            </Stack>
          </Box>
        ) : error ? (
          <Alert severity="error">{error}</Alert>
        ) : (
          <Stack spacing={3.25} sx={{ mt: 1.5 }}>
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
                <Stack direction={{ xs: "column", sm: "row" }} spacing={2} alignItems={{ xs: "flex-start", sm: "center" }}>
                  <Avatar
                    sx={{
                      width: 74,
                      height: 74,
                      fontSize: "1.85rem",
                      fontWeight: 700,
                      color: theme.palette.common.white,
                      backgroundColor: alpha(darkGreen, 0.32),
                    }}
                  >
                    {initials || "U"}
                  </Avatar>
                  <Stack spacing={0.75} sx={{ flex: 1 }}>
                    <Typography variant="h6" sx={{ fontWeight: 700, color: darkGreen }}>
                      {fullName || "-"}
                    </Typography>
                    <Typography variant="body2" sx={{ color: darkGreen }}>
                      {data?.username || "-"}
                    </Typography>
                    <Stack direction="row" spacing={1} useFlexGap flexWrap="wrap">
                      <Chip
                        icon={<CheckCircleOutlineIcon />}
                        label={statusLabel}
                        size="small"
                        sx={{
                          fontWeight: 700,
                          color: isActive ? darkGreen : theme.palette.error.dark,
                          backgroundColor: isActive
                            ? alpha(green, 0.14)
                            : alpha(theme.palette.error.main, 0.12),
                          border: isActive
                            ? `1px solid ${alpha(green, 0.18)}`
                            : `1px solid ${alpha(theme.palette.error.main, 0.18)}`,
                          "& .MuiChip-icon": {
                            color: "inherit",
                          },
                        }}
                      />
                      {data?.empresaPreferida ? (
                        <Chip
                          icon={<BusinessOutlinedIcon />}
                          label={data.empresaPreferida}
                          size="small"
                          sx={{
                            fontWeight: 500,
                            color: darkGreen,
                            backgroundColor: lighterGreen,
                            "& .MuiChip-icon": {
                              color: darkGreen,
                            },
                          }}
                        />
                      ) : null}
                    </Stack>
                  </Stack>
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
                <SectionHeader
                  icon={PermIdentityOutlinedIcon}
                  title={t("usuario.detail.personalInformation")}
                />
                <Grid container spacing={{ xs: 2, sm: 3 }}>
                  <Grid item xs={12} sm={6}>
                    <Stack spacing={2}>
                      <DetailField
                        icon={PersonOutlineIcon}
                        label={t("usuario.detail.username")}
                        value={data?.username}
                      />
                      <DetailField
                        icon={PermIdentityOutlinedIcon}
                        label={t("usuario.detail.firstName")}
                        value={data?.nombre ?? firstName}
                      />
                      <DetailField
                        icon={PermIdentityOutlinedIcon}
                        label={t("usuario.detail.lastName")}
                        value={data?.apellido ?? lastName}
                      />
                      <DetailField
                        icon={LocationOnOutlinedIcon}
                        label={t("usuario.detail.address")}
                        value={data?.direccion}
                        showDivider={false}
                      />
                    </Stack>
                  </Grid>
                  <Grid item xs={12} sm={6}>
                    <Stack spacing={2}>
                      <DetailField
                        icon={WcOutlinedIcon}
                        label={t("usuario.detail.gender")}
                        value={data?.genero}
                      />
                      <DetailField
                        icon={BadgeOutlinedIcon}
                        label={t("usuario.detail.document")}
                        value={documentValue}
                      />
                      <DetailField
                        icon={PhoneOutlinedIcon}
                        label={t("usuario.detail.phone")}
                        value={data?.celular}
                      />
                      <DetailField
                        icon={CalendarMonthOutlinedIcon}
                        label={t("usuario.detail.birthDate")}
                        value={formatDate(data?.fechaNacimiento, language)}
                        showDivider={false}
                      />
                    </Stack>
                  </Grid>
                </Grid>
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
                <SectionHeader
                  icon={WorkOutlineOutlinedIcon}
                  title={t("usuario.detail.assignments")}
                />
                <Stack spacing={1.25}>
                  {asignaciones.length === 0 ? (
                    <Typography color="text.secondary">{t("common.labels.withoutAssignments")}</Typography>
                  ) : (
                    asignaciones.map((a, i) => (
                      <Card
                        key={a.usuarioRolId ?? i}
                        sx={{
                          borderRadius: 1.75,
                          border: `1px solid ${alpha(green, 0.1)}`,
                          boxShadow: `0 2px 8px ${alpha(darkGreen, theme.palette.mode === "dark" ? 0.12 : 0.035)}`,
                          backgroundColor: assignmentSurface,
                        }}
                      >
                        <CardContent sx={{ py: 1.75 }}>
                          <Stack spacing={1.25}>
                            <Stack
                              direction={{ xs: "column", md: "row" }}
                              spacing={1.5}
                              alignItems={{ xs: "flex-start", md: "center" }}
                              justifyContent="space-between"
                            >
                              <Stack direction="row" spacing={1} useFlexGap flexWrap="wrap">
                                <Chip
                                  icon={<WorkOutlineOutlinedIcon />}
                                  label={`${t("common.labels.role")}: ${a.rolNombre || "-"}`}
                                  size="small"
                                  sx={{
                                    color: darkGreen,
                                    backgroundColor: lighterGreen,
                                    "& .MuiChip-icon": {
                                      color: darkGreen,
                                    },
                                  }}
                                />
                                <Chip
                                  icon={<BusinessOutlinedIcon />}
                                  label={`${t("common.labels.company")}: ${a.empresaNombre || "-"}`}
                                  size="small"
                                  sx={{
                                    color: darkGreen,
                                    backgroundColor: lighterGreen,
                                    "& .MuiChip-icon": {
                                      color: darkGreen,
                                    },
                                  }}
                                />
                              </Stack>
                              <Chip
                                icon={<CheckCircleOutlineIcon />}
                                label={Number(a.estadoId ?? 1) === 1 ? t("common.labels.active") : t("common.labels.inactive")}
                                size="small"
                                sx={{
                                  fontWeight: 600,
                                  color:
                                    Number(a.estadoId ?? 1) === 1
                                      ? darkGreen
                                      : theme.palette.error.dark,
                                  backgroundColor:
                                    Number(a.estadoId ?? 1) === 1
                                      ? alpha(green, 0.12)
                                      : alpha(theme.palette.error.main, 0.12),
                                  "& .MuiChip-icon": {
                                    color: "inherit",
                                  },
                                }}
                              />
                            </Stack>
                            <Divider sx={{ borderColor: subtleDivider }} />
                            <Grid container spacing={2}>
                              <Grid item xs={12} sm={6}>
                                <DetailField
                                  icon={CalendarMonthOutlinedIcon}
                                  label={t("usuario.detail.contractStart")}
                                  value={formatDateTime(a.fechaInicioContrato ?? a.iniciaContratoEn, language)}
                                />
                              </Grid>
                              <Grid item xs={12} sm={6}>
                                <DetailField
                                  icon={CalendarMonthOutlinedIcon}
                                  label={t("usuario.detail.contractEnd")}
                                  value={
                                    formatDateTime(a.fechaFinContrato ?? a.finalizaContratoEn, language) ||
                                    t("common.labels.withoutDate")
                                  }
                                  showDivider={false}
                                />
                              </Grid>
                            </Grid>
                          </Stack>
                        </CardContent>
                      </Card>
                    ))
                  )}
                </Stack>
              </CardContent>
            </Card>
          </Stack>
        )}
      </DialogContent>
      <DialogActions
        sx={{
          px: { xs: 2.25, sm: 3 },
          py: 2,
          backgroundColor: dialogSurface,
          borderTop: `1px solid ${subtleBorder}`,
        }}
      >
        <Button
          onClick={onClose}
          variant="contained"
          sx={{
            minWidth: 104,
            ml: "auto",
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
          {t("common.actions.close")}
        </Button>
      </DialogActions>
    </Dialog>
  );
}
