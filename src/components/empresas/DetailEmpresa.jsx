/*=============================================================================
 Nombre del archivo : DetailEmpresa.jsx
 Descripcion        : Modal para visualizar el detalle de una empresa (HU-043.3).
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |   Fecha    | Versión |      Autor           | Descripción del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2026-08-16 | 0.5.0   | Jeisson Sanchez      | HU-043.3 Detalle empresa    |
 +------------+---------+----------------------+-----------------------------+
=============================================================================*/
/**
 * @module DetailEmpresa
 * @description Modal que presenta la información completa de una empresa
 * recuperada desde el backend mediante `GET /api/v1/empresas/{id}`.
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
import BusinessOutlinedIcon from "@mui/icons-material/BusinessOutlined";
import PermIdentityOutlinedIcon from "@mui/icons-material/PermIdentityOutlined";
import MailOutlineIcon from "@mui/icons-material/MailOutline";
import PhoneOutlinedIcon from "@mui/icons-material/PhoneOutlined";
import PersonOutlineIcon from "@mui/icons-material/PersonOutline";
import DescriptionOutlinedIcon from "@mui/icons-material/DescriptionOutlined";
import CheckCircleOutlineIcon from "@mui/icons-material/CheckCircleOutline";
import PropTypes from "prop-types";
import { useTranslation } from "react-i18next";

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
 * Muestra el modal de detalle de la empresa.
 *
 * @param {object} props Propiedades del modal.
 * @param {boolean} props.open Controla la visibilidad del diálogo.
 * @param {object|null} props.data Información de la empresa a renderizar.
 * @param {boolean} [props.loading=false] Indica si el detalle está cargando.
 * @param {string} [props.error=""] Mensaje de error de carga.
 * @param {Function} props.onClose Acción para cerrar el diálogo.
 * @returns {JSX.Element}
 */
export default function DetailEmpresa({ open, data, loading = false, error = "", onClose }) {
  const { t } = useTranslation();
  const theme = useTheme();
  const darkGreen = theme.palette.mode === "dark" ? "#E7F6F7" : "#173f39";
  const green = theme.palette.mode === "dark" ? "#2b6b60" : "#173f39";
  const lighterGreen = theme.palette.mode === "dark" ? alpha("#2b6b60", 0.18) : "#dfeae6";
  const dialogSurface = theme.palette.mode === "dark" ? "#10211f" : theme.palette.common.white;
  const sectionSurface = theme.palette.mode === "dark" ? "#142b28" : theme.palette.common.white;
  const summarySurface = theme.palette.mode === "dark" ? alpha("#2b6b60", 0.28) : lighterGreen;
  const subtleBorder = alpha(green, 0.14);
  const softShadow = `0 10px 30px ${alpha(darkGreen, theme.palette.mode === "dark" ? 0.18 : 0.08)}`;
  const sectionShadow = `0 4px 14px ${alpha(darkGreen, theme.palette.mode === "dark" ? 0.14 : 0.05)}`;
  const isActive = Number(data?.estadoId ?? 1) === 1;

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
              <BusinessOutlinedIcon fontSize="small" />
            </Box>
            <Typography
              variant="h5"
              sx={{ fontSize: "1.15rem", fontWeight: 700, color: darkGreen }}
            >
              {t("empresa.detail.title", "Detalle de Empresa")}
            </Typography>
          </Stack>
          <IconButton
            onClick={onClose}
            size="small"
            aria-label={t("common.actions.close")}
            sx={{ color: darkGreen }}
          >
            <CloseIcon sx={{ color: darkGreen }} />
          </IconButton>
        </Stack>
      </DialogTitle>

      <DialogContent
        dividers={false}
        sx={{
          px: { xs: 2.25, sm: 3 },
          pt: 3.25,
          pb: 2.5,
          backgroundColor: dialogSurface,
        }}
      >
        {loading ? (
          <Box sx={{ py: 6 }}>
            <Stack spacing={2} alignItems="center" justifyContent="center">
              <CircularProgress />
              <Typography color="text.secondary">{t("empresa.detail.loading", "Cargando...")}</Typography>
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
                  <Box
                    sx={{
                      width: 74,
                      height: 74,
                      borderRadius: 2,
                      overflow: "hidden",
                      flexShrink: 0,
                      display: "flex",
                      alignItems: "center",
                      justifyContent: "center",
                      backgroundColor: lighterGreen,
                      border: `1px solid ${subtleBorder}`,
                    }}
                  >
                    {data?.logo ? (
                      <Box
                        component="img"
                        src={data.logo}
                        alt={data?.nombre || "logo"}
                        sx={{ width: "100%", height: "100%", objectFit: "contain" }}
                      />
                    ) : (
                      <BusinessOutlinedIcon sx={{ fontSize: 40, color: darkGreen }} />
                    )}
                  </Box>
                  <Stack spacing={0.75} sx={{ flex: 1 }}>
                    <Typography variant="h6" sx={{ fontWeight: 700, color: darkGreen }}>
                      {data?.nombre || "-"}
                    </Typography>
                    <Typography variant="body2" sx={{ color: darkGreen }}>
                      {data?.identificacion ? `${data?.tipoIdentificacionNombre || ""} ${data.identificacion}`.trim() : "-"}
                    </Typography>
                    <Stack direction="row" spacing={1} useFlexGap flexWrap="wrap">
                      <Chip
                        icon={<CheckCircleOutlineIcon />}
                        label={isActive ? t("common.labels.active", "Activo") : t("common.labels.inactive", "Inactivo")}
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
                          "& .MuiChip-icon": { color: "inherit" },
                        }}
                      />
                      {data?.estadoNombre ? (
                        <Chip
                          label={data.estadoNombre}
                          size="small"
                          sx={{
                            fontWeight: 500,
                            color: darkGreen,
                            backgroundColor: lighterGreen,
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
                <Grid container spacing={{ xs: 2, sm: 3 }}>
                  <Grid item xs={12} sm={6}>
                    <Stack spacing={2}>
                      <DetailField
                        icon={PermIdentityOutlinedIcon}
                        label={t("empresa.form.tipoIdentificacion", "Tipo de Identificación")}
                        value={data?.tipoIdentificacionNombre}
                      />
                      <DetailField
                        icon={BusinessOutlinedIcon}
                        label={t("empresa.form.nombre", "Nombre")}
                        value={data?.nombre}
                      />
                      <DetailField
                        icon={MailOutlineIcon}
                        label={t("empresa.form.correo", "Correo")}
                        value={data?.correo}
                        showDivider={false}
                      />
                    </Stack>
                  </Grid>
                  <Grid item xs={12} sm={6}>
                    <Stack spacing={2}>
                      <DetailField
                        icon={PermIdentityOutlinedIcon}
                        label={t("empresa.form.identificacion", "No. de Identificación")}
                        value={data?.identificacion}
                      />
                      <DetailField
                        icon={PhoneOutlinedIcon}
                        label={t("empresa.form.celular", "Celular")}
                        value={data?.celular}
                      />
                      <DetailField
                        icon={PersonOutlineIcon}
                        label={t("empresa.form.contacto", "Contacto")}
                        value={data?.contacto}
                      />
                      <DetailField
                        icon={PersonOutlineIcon}
                        label={t("empresa.detail.personaResponsable", "Persona Responsable")}
                        value={data?.personaResponsableNombre}
                        showDivider={false}
                      />
                    </Stack>
                  </Grid>
                  <Grid item xs={12}>
                    <DetailField
                      icon={DescriptionOutlinedIcon}
                      label={t("empresa.form.descripcion", "Descripción")}
                      value={data?.descripcion}
                      showDivider={false}
                    />
                  </Grid>
                </Grid>
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
            "&:hover": { backgroundColor: green, boxShadow: "none" },
          }}
        >
          {t("common.actions.close")}
        </Button>
      </DialogActions>
    </Dialog>
  );
}

DetailEmpresa.propTypes = {
  open: PropTypes.bool.isRequired,
  data: PropTypes.object,
  loading: PropTypes.bool,
  error: PropTypes.string,
  onClose: PropTypes.func.isRequired,
};

DetailField.propTypes = {
  icon: PropTypes.elementType,
  label: PropTypes.string,
  value: PropTypes.string,
  showDivider: PropTypes.bool,
};