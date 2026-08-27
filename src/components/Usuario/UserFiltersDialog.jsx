/*=============================================================================
Nombre del archivo : UserFiltersDialog.jsx
Descripción        : Modal de filtros para el listado de usuarios.
===============================================================================
CONTROL DE CAMBIOS
+------------+---------+----------------------+-----------------------------------------------+
|   Fecha    | Versión |      Autor           | Descripción del cambio                        |
+------------+---------+----------------------+-----------------------------------------------+
| 2026-08-03 | 0.4.0   | Cesar Medina         | Se crea modal de filtros para usuarios.       |
+------------+---------+----------------------+-----------------------------------------------+
=============================================================================*/
/**
 * @module UserFiltersDialog
 * @description Renderiza el modal para aplicar filtros al listado de usuarios
 * usando los parámetros soportados por el endpoint actual.
 */
import React from "react";
import PropTypes from "prop-types";
import {
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Button,
  Box,
  Card,
  CardContent,
  Grid,
  IconButton,
  InputAdornment,
  MenuItem,
  Stack,
  TextField,
  Typography,
} from "@mui/material";
import { alpha, useTheme } from "@mui/material/styles";
import CloseIcon from "@mui/icons-material/Close";
import FilterListIcon from "@mui/icons-material/FilterList";
import PersonOutlineIcon from "@mui/icons-material/PersonOutline";
import BadgeOutlinedIcon from "@mui/icons-material/BadgeOutlined";
import BusinessOutlinedIcon from "@mui/icons-material/BusinessOutlined";
import { useTranslation } from "react-i18next";

export default function UserFiltersDialog({
  open,
  onClose,
  values,
  onChange,
  onApply,
  onClear,
  roles = [],
  empresas = [],
  estados = [],
  companyLocked = false,
}) {
  const { t } = useTranslation();
  const theme = useTheme();
  const green = theme.palette.mode === "dark" ? "#2b6b60" : "#173f39";
  const darkGreen = theme.palette.mode === "dark" ? "#E7F6F7" : "#173f39";
  const dialogSurface = theme.palette.mode === "dark" ? "#10211f" : theme.palette.common.white;
  const sectionSurface = theme.palette.mode === "dark" ? "#142b28" : theme.palette.common.white;
  const summarySurface = theme.palette.mode === "dark" ? alpha("#2b6b60", 0.28) : "#dfeae6";
  const subtleBorder = alpha(green, 0.14);
  const sectionShadow = `0 4px 14px ${alpha(darkGreen, theme.palette.mode === "dark" ? 0.14 : 0.05)}`;
  const contentMaxWidth = 1180;

  const handleFieldChange = (name) => (event) => {
    onChange(name, event.target.value);
  };

  return (
    <Dialog
      open={open}
      onClose={onClose}
      fullWidth
      maxWidth="lg"
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
          sx={{ width: "100%", maxWidth: contentMaxWidth, mx: "auto" }}
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
                  theme.palette.mode === "dark" ? alpha("#2b6b60", 0.24) : "#dfeae6",
              }}
            >
              <FilterListIcon fontSize="small" />
            </Box>
            <Typography
              variant="h5"
              sx={{ fontSize: "1.15rem", fontWeight: 700, color: darkGreen }}
            >
              {t("usuario.filters.title")}
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
          pt: 6,
          pb: 2.5,
          backgroundColor: dialogSurface,
        }}
      >
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
                  {t("usuario.filters.title")}
                </Typography>
                <Typography variant="body2" sx={{ color: darkGreen }}>
                  {t("usuario.filters.description")}
                </Typography>
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
            <CardContent sx={{ p: { xs: 2.75, sm: 3.25 } }}>
              <Stack spacing={3}>
                <Stack spacing={0.45}>
                  <Stack direction="row" spacing={1.25} alignItems="center">
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
                          theme.palette.mode === "dark" ? alpha("#2b6b60", 0.24) : "#dfeae6",
                      }}
                    >
                      <PersonOutlineIcon fontSize="small" />
                    </Box>
                    <Typography
                      variant="h6"
                      sx={{
                        fontSize: "1.05rem",
                        fontWeight: 700,
                        color: theme.palette.mode === "dark" ? "#E7F6F7" : "#173f39",
                      }}
                    >
                      {t("usuario.filters.sections.main")}
                    </Typography>
                  </Stack>
                  <Typography variant="body2" color="text.secondary">
                    {t("usuario.filters.sections.mainDescription")}
                  </Typography>
                </Stack>

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
                      fullWidth
                      label={t("usuario.columns.username")}
                      value={values.username ?? ""}
                      onChange={handleFieldChange("username")}
                      InputLabelProps={{ shrink: true }}
                      InputProps={{
                        startAdornment: (
                          <InputAdornment position="start">
                            <BadgeOutlinedIcon
                              fontSize="small"
                              sx={{ color: alpha(darkGreen, 0.7) }}
                            />
                          </InputAdornment>
                        ),
                      }}
                    />
                  </Grid>
                  <Grid item xs={12} md={6}>
                    <TextField
                      fullWidth
                      label={t("usuario.columns.firstName")}
                      value={values.nombre ?? ""}
                      onChange={handleFieldChange("nombre")}
                      InputLabelProps={{ shrink: true }}
                      InputProps={{
                        startAdornment: (
                          <InputAdornment position="start">
                            <PersonOutlineIcon
                              fontSize="small"
                              sx={{ color: alpha(darkGreen, 0.7) }}
                            />
                          </InputAdornment>
                        ),
                      }}
                    />
                  </Grid>
                  <Grid item xs={12} md={6}>
                    <TextField
                      fullWidth
                      label={t("usuario.columns.lastName")}
                      value={values.apellido ?? ""}
                      onChange={handleFieldChange("apellido")}
                      InputLabelProps={{ shrink: true }}
                      InputProps={{
                        startAdornment: (
                          <InputAdornment position="start">
                            <PersonOutlineIcon
                              fontSize="small"
                              sx={{ color: alpha(darkGreen, 0.7) }}
                            />
                          </InputAdornment>
                        ),
                      }}
                    />
                  </Grid>
                  <Grid item xs={12} md={6}>
                    <TextField
                      select
                      fullWidth
                      label={t("usuario.form.fields.role")}
                      value={values.rolId ?? ""}
                      onChange={handleFieldChange("rolId")}
                      InputLabelProps={{ shrink: true }}
                    >
                      <MenuItem value="">{t("common.labels.all")}</MenuItem>
                      {roles.map((item) => (
                        <MenuItem key={item.value} value={item.value}>
                          {item.label}
                        </MenuItem>
                      ))}
                    </TextField>
                  </Grid>
                  <Grid item xs={12} md={6}>
                    <TextField
                      select
                      fullWidth
                      label={t("usuario.form.fields.status")}
                      value={values.estadoId ?? ""}
                      onChange={handleFieldChange("estadoId")}
                      InputLabelProps={{ shrink: true }}
                    >
                      <MenuItem value="">{t("common.labels.all")}</MenuItem>
                      {estados.map((item) => (
                        <MenuItem key={item.value} value={item.value}>
                          {item.label}
                        </MenuItem>
                      ))}
                    </TextField>
                  </Grid>
                  <Grid item xs={12} md={6}>
                    <TextField
                      select
                      fullWidth
                      label={t("usuario.form.fields.company")}
                      value={values.empresaId ?? ""}
                      onChange={handleFieldChange("empresaId")}
                      disabled={companyLocked}
                      InputLabelProps={{ shrink: true }}
                      InputProps={{
                        startAdornment: (
                          <InputAdornment position="start">
                            <BusinessOutlinedIcon
                              fontSize="small"
                              sx={{ color: alpha(darkGreen, 0.7) }}
                            />
                          </InputAdornment>
                        ),
                      }}
                    >
                      <MenuItem value="">{t("common.labels.all")}</MenuItem>
                      {empresas.map((item) => (
                        <MenuItem key={item.value} value={item.value}>
                          {item.label}
                        </MenuItem>
                      ))}
                    </TextField>
                  </Grid>
                </Grid>
              </Stack>
            </CardContent>
          </Card>
        </Stack>
      </DialogContent>
      <DialogActions
        sx={{
          px: { xs: 2.25, sm: 3 },
          py: 2.25,
          backgroundColor: dialogSurface,
          borderTop: `1px solid ${subtleBorder}`,
        }}
      >
        <Stack
          direction={{ xs: "column-reverse", sm: "row" }}
          spacing={1.5}
          justifyContent="space-between"
          alignItems={{ xs: "stretch", sm: "center" }}
          sx={{ width: "100%", maxWidth: contentMaxWidth, mx: "auto" }}
        >
          <Button
            onClick={onClear}
            sx={{
              borderRadius: 2,
              px: 2.5,
              textTransform: "none",
              fontWeight: 700,
              color: darkGreen,
            }}
          >
            {t("common.actions.clear")}
          </Button>
          <Stack direction={{ xs: "column", sm: "row" }} spacing={1.5} sx={{ width: { xs: "100%", sm: "auto" } }}>
            <Button
              onClick={onClose}
              sx={{
                borderRadius: 2,
                px: 2.5,
                textTransform: "none",
                fontWeight: 700,
                color: darkGreen,
                border: `1px solid ${subtleBorder}`,
              }}
            >
              {t("common.actions.cancel")}
            </Button>
            <Button
              variant="contained"
              onClick={onApply}
              startIcon={<FilterListIcon />}
              sx={{
                borderRadius: 2,
                px: 2.5,
                textTransform: "none",
                fontWeight: 700,
                backgroundColor: theme.palette.mode === "dark" ? "#173f39" : "#1d4d45",
                boxShadow: "none",
                "&:hover": {
                  backgroundColor: theme.palette.mode === "dark" ? "#21534b" : "#173f39",
                  boxShadow: "none",
                },
              }}
            >
              {t("common.actions.apply")}
            </Button>
          </Stack>
        </Stack>
      </DialogActions>
    </Dialog>
  );
}

UserFiltersDialog.propTypes = {
  open: PropTypes.bool.isRequired,
  onClose: PropTypes.func.isRequired,
  values: PropTypes.object.isRequired,
  onChange: PropTypes.func.isRequired,
  onApply: PropTypes.func.isRequired,
  onClear: PropTypes.func.isRequired,
  roles: PropTypes.array,
  empresas: PropTypes.array,
  estados: PropTypes.array,
  companyLocked: PropTypes.bool,
};
