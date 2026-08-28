/*=============================================================================
Nombre del archivo : UserStatusConfirmDialog.jsx
Descripción        : Modal de confirmación para activar o inactivar usuarios.
===============================================================================
CONTROL DE CAMBIOS
+------------+---------+----------------------+-----------------------------------------------+
|   Fecha    | Versión |      Autor           | Descripción del cambio                        |
+------------+---------+----------------------+-----------------------------------------------+
| 2026-08-24 | 0.4.0   | Cesar Medina         | Se crea modal MUI para confirmar cambio estado|
| 2026-08-24 | 0.4.0   | Cesar Medina         | Se suavizan transiciones y ajusta botón cerrar|
| 2026-08-24 | 0.4.0   | Cesar Medina         | Se separan acciones y refuerza feedback click |
+------------+---------+----------------------+-----------------------------------------------+
=============================================================================*/
/**
 * @module UserStatusConfirmDialog
 * @description Presenta una confirmación visual para activar o inactivar un
 * usuario, con identificación del registro y manejo de carga o error.
 */
import React from "react";
import PropTypes from "prop-types";
import {
  Alert,
  Avatar,
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
  IconButton,
  Stack,
  Typography,
} from "@mui/material";
import { alpha, useTheme } from "@mui/material/styles";
import CloseIcon from "@mui/icons-material/Close";
import CheckCircleOutlineIcon from "@mui/icons-material/CheckCircleOutline";
import HighlightOffOutlinedIcon from "@mui/icons-material/HighlightOffOutlined";
import PersonOutlineIcon from "@mui/icons-material/PersonOutline";
import MailOutlineIcon from "@mui/icons-material/MailOutline";
import BusinessOutlinedIcon from "@mui/icons-material/BusinessOutlined";
import WorkOutlineOutlinedIcon from "@mui/icons-material/WorkOutlineOutlined";
import { useTranslation } from "react-i18next";

const DetailRow = ({ icon: Icon, label, value, showDivider = true, darkGreen }) => (
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
          backgroundColor: theme.palette.mode === "dark" ? alpha("#2b6b60", 0.24) : "#dfeae6",
          color: theme.palette.mode === "dark" ? "#E7F6F7" : "#173f39",
          flexShrink: 0,
        })}
      >
        <Icon fontSize="small" sx={{ color: "inherit" }} />
      </Box>
      <Stack spacing={0.35} sx={{ minWidth: 0 }}>
        <Typography variant="caption" sx={{ fontWeight: 700, color: darkGreen }}>
          {label}
        </Typography>
        <Typography variant="body1" sx={{ fontWeight: 500, wordBreak: "break-word", color: darkGreen }}>
          {value || "-"}
        </Typography>
      </Stack>
    </Stack>
    {showDivider ? <Divider sx={{ borderColor: alpha(darkGreen, 0.12) }} /> : null}
  </Stack>
);

DetailRow.propTypes = {
  icon: PropTypes.elementType.isRequired,
  label: PropTypes.string.isRequired,
  value: PropTypes.string,
  showDivider: PropTypes.bool,
  darkGreen: PropTypes.string.isRequired,
};

export default function UserStatusConfirmDialog({
  open,
  user,
  activating = false,
  submitting = false,
  error = "",
  tenantScoped = false,
  contextCompanyName = "",
  onClose,
  onConfirm,
}) {
  const { t } = useTranslation();
  const theme = useTheme();
  const darkGreen = theme.palette.mode === "dark" ? "#E7F6F7" : "#173f39";
  const green = theme.palette.mode === "dark" ? "#2b6b60" : "#173f39";
  const actionColor = activating ? theme.palette.success.main : theme.palette.error.main;
  const actionDark = activating ? theme.palette.success.dark : theme.palette.error.dark;
  const actionIcon = activating ? <CheckCircleOutlineIcon fontSize="small" /> : <HighlightOffOutlinedIcon fontSize="small" />;
  const dialogSurface = theme.palette.mode === "dark" ? "#10211f" : theme.palette.common.white;
  const summarySurface = theme.palette.mode === "dark" ? alpha("#2b6b60", 0.28) : "#dfeae6";
  const sectionSurface = theme.palette.mode === "dark" ? "#142b28" : theme.palette.common.white;
  const subtleBorder = alpha(green, 0.14);
  const sectionShadow = `0 4px 14px ${alpha(darkGreen, theme.palette.mode === "dark" ? 0.14 : 0.05)}`;
  const softShadow = `0 10px 30px ${alpha(darkGreen, theme.palette.mode === "dark" ? 0.18 : 0.08)}`;
  const fullName = [user?.nombre, user?.apellido].filter(Boolean).join(" ").trim();
  const initials = `${String(user?.nombre ?? "").charAt(0)}${String(user?.apellido ?? "").charAt(0)}`
    .trim()
    .toUpperCase();

  return (
    <Dialog
      open={open}
      onClose={submitting ? undefined : onClose}
      fullWidth
      maxWidth="sm"
      PaperProps={{
        sx: {
          borderRadius: 3,
          overflow: "hidden",
          backgroundColor: dialogSurface,
          boxShadow: softShadow,
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
                color: darkGreen,
                backgroundColor: theme.palette.mode === "dark" ? alpha("#2b6b60", 0.24) : "#dfeae6",
              }}
            >
              {actionIcon}
            </Box>
            <Typography variant="h5" sx={{ fontSize: "1.15rem", fontWeight: 700, color: darkGreen }}>
              {activating
                ? t("usuario.statusDialog.activateTitle")
                : t("usuario.statusDialog.inactivateTitle")}
            </Typography>
          </Stack>
          <IconButton
            onClick={onClose}
            size="small"
            disabled={submitting}
            aria-label={t("common.actions.close")}
            sx={{
              color: darkGreen,
              mr: { xs: 0.25, sm: 0.75 },
              transition: theme.transitions.create(["transform", "background-color", "box-shadow"], {
                duration: theme.transitions.duration.shorter,
              }),
              "&:hover": {
                backgroundColor: alpha(darkGreen, 0.08),
                transform: "translateX(-3px)",
              },
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
                    width: 68,
                    height: 68,
                    fontSize: "1.65rem",
                    fontWeight: 700,
                    color: theme.palette.common.white,
                    backgroundColor: alpha(darkGreen, 0.32),
                  }}
                >
                  {initials || "U"}
                </Avatar>
                <Stack spacing={0.8} sx={{ flex: 1 }}>
                  <Typography variant="h6" sx={{ fontWeight: 700, color: darkGreen }}>
                    {fullName || user?.username || "-"}
                  </Typography>
                  <Typography variant="body2" sx={{ color: darkGreen }}>
                    {activating
                      ? tenantScoped
                        ? t("usuario.statusDialog.activateTenantDescription")
                        : t("usuario.statusDialog.activateDescription")
                      : tenantScoped
                        ? t("usuario.statusDialog.inactivateTenantDescription")
                        : t("usuario.statusDialog.inactivateDescription")}
                  </Typography>
                  {tenantScoped ? (
                    <Typography variant="caption" sx={{ fontWeight: 700, color: darkGreen }}>
                      {t("usuario.statusDialog.tenantScope", { empresa: contextCompanyName || "-" })}
                    </Typography>
                  ) : null}
                  <Stack direction="row" spacing={1} useFlexGap flexWrap="wrap">
                    <Chip
                      icon={actionIcon}
                      label={activating ? t("usuario.actions.activate") : t("usuario.actions.inactivate")}
                      size="small"
                      sx={{
                        fontWeight: 700,
                        color: actionDark,
                        backgroundColor: alpha(actionColor, 0.12),
                        border: `1px solid ${alpha(actionColor, 0.22)}`,
                        "& .MuiChip-icon": {
                          color: "inherit",
                        },
                      }}
                    />
                    <Chip
                      icon={<PersonOutlineIcon />}
                      label={user?.estadoNombre || "-"}
                      size="small"
                      sx={{
                        fontWeight: 600,
                        color: darkGreen,
                        backgroundColor: theme.palette.mode === "dark" ? alpha("#2b6b60", 0.18) : "#f6fbf8",
                        "& .MuiChip-icon": {
                          color: darkGreen,
                        },
                      }}
                    />
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
              <Stack spacing={2}>
                <Typography variant="h6" sx={{ fontSize: "1.05rem", fontWeight: 700, color: darkGreen }}>
                  {t("usuario.statusDialog.userSectionTitle")}
                </Typography>
                <DetailRow
                  icon={PersonOutlineIcon}
                  label={t("usuario.statusDialog.userName")}
                  value={fullName || "-"}
                  darkGreen={darkGreen}
                />
                <DetailRow
                  icon={MailOutlineIcon}
                  label={t("usuario.statusDialog.userEmail")}
                  value={user?.username ?? ""}
                  darkGreen={darkGreen}
                />
                <DetailRow
                  icon={BusinessOutlinedIcon}
                  label={t("usuario.statusDialog.userCompany")}
                  value={user?.empresaNombre ?? ""}
                  darkGreen={darkGreen}
                />
                <DetailRow
                  icon={WorkOutlineOutlinedIcon}
                  label={t("usuario.statusDialog.userRole")}
                  value={user?.rolPreferido ?? ""}
                  showDivider={false}
                  darkGreen={darkGreen}
                />
              </Stack>
            </CardContent>
          </Card>

          {error ? <Alert severity="error">{error}</Alert> : null}
        </Stack>
      </DialogContent>

      <DialogActions
        sx={{
          px: { xs: 2.25, sm: 3 },
          py: 2,
          backgroundColor: dialogSurface,
          borderTop: `1px solid ${subtleBorder}`,
          gap: 1.25,
          justifyContent: "space-between",
          alignItems: "center",
        }}
      >
        <Button
          onClick={onClose}
          variant="outlined"
          disabled={submitting}
          sx={{
            minWidth: 112,
            fontWeight: 700,
            borderRadius: 1.5,
            color: darkGreen,
            borderColor: alpha(darkGreen, 0.24),
            transition: theme.transitions.create(
              ["background-color", "border-color", "transform", "box-shadow"],
              { duration: theme.transitions.duration.shorter }
            ),
            "&:hover": {
              borderColor: alpha(darkGreen, 0.34),
              backgroundColor: alpha(darkGreen, 0.05),
              boxShadow: `0 8px 18px ${alpha(darkGreen, 0.08)}`,
              transform: "translateY(-1px)",
            },
            "&:active": {
              backgroundColor: alpha(darkGreen, 0.12),
              borderColor: alpha(darkGreen, 0.4),
              boxShadow: `0 2px 8px ${alpha(darkGreen, 0.14)}`,
              transform: "translateY(1px) scale(0.98)",
            },
          }}
        >
          {t("common.actions.cancel")}
        </Button>
        <Button
          onClick={onConfirm}
          variant="contained"
          disabled={submitting}
          startIcon={submitting ? <CircularProgress size={18} color="inherit" /> : actionIcon}
          sx={{
            minWidth: 148,
            fontWeight: 700,
            borderRadius: 1.5,
            boxShadow: "none",
            backgroundColor: activating ? theme.palette.success.dark : theme.palette.error.main,
            transition: theme.transitions.create(
              ["background-color", "transform", "box-shadow"],
              { duration: theme.transitions.duration.shorter }
            ),
            "&:hover": {
              backgroundColor: activating ? theme.palette.success.main : theme.palette.error.dark,
              boxShadow: `0 10px 20px ${alpha(actionColor, 0.22)}`,
              transform: "translateY(-1px)",
            },
            "&:active": {
              backgroundColor: activating ? theme.palette.success.dark : theme.palette.error.dark,
              boxShadow: `0 3px 10px ${alpha(actionColor, 0.24)}`,
              transform: "translateY(1px) scale(0.985)",
            },
          }}
        >
          {submitting
            ? activating
              ? t("usuario.statusDialog.activating")
              : t("usuario.statusDialog.inactivating")
            : activating
              ? t("usuario.statusDialog.confirmActivate")
              : t("usuario.statusDialog.confirmInactivate")}
        </Button>
      </DialogActions>
    </Dialog>
  );
}

UserStatusConfirmDialog.propTypes = {
  open: PropTypes.bool.isRequired,
  user: PropTypes.shape({
    nombre: PropTypes.string,
    apellido: PropTypes.string,
    username: PropTypes.string,
    rolPreferido: PropTypes.string,
    empresaNombre: PropTypes.string,
    estadoNombre: PropTypes.string,
  }),
  activating: PropTypes.bool,
  submitting: PropTypes.bool,
  error: PropTypes.string,
  tenantScoped: PropTypes.bool,
  contextCompanyName: PropTypes.string,
  onClose: PropTypes.func.isRequired,
  onConfirm: PropTypes.func.isRequired,
};
