import React from "react";
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  Typography,
  Chip,
  Accordion,
  AccordionSummary,
  AccordionDetails,
  Stack,
  Box,
} from "@mui/material";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import { useTheme, alpha } from "@mui/material/styles";

export default function ModalVerPermisos({
  open,
  onClose,
  permisos = [],
  rolNombre = "",
}) {
  const theme = useTheme();
  const isDark = theme.palette.mode === "dark";

  // Agrupar por módulo usando autoridad
  const permisosPorModulo = permisos.reduce((acc, permiso) => {
    let moduloNombre = "General";

    if (permiso.autoridad) {
      moduloNombre = permiso.autoridad.split("_")[0];
      moduloNombre =
        moduloNombre.charAt(0) +
        moduloNombre.slice(1).toLowerCase();
    }

    if (!acc[moduloNombre]) {
      acc[moduloNombre] = [];
    }

    acc[moduloNombre].push(permiso);
    return acc;
  }, {});

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="md">
      <DialogTitle
        sx={{
          fontWeight: 600,
          backgroundColor: alpha(theme.palette.primary.main, isDark ? 0.15 : 0.08),
        }}
      >
        Permisos del Rol: {rolNombre}
      </DialogTitle>

      <DialogContent
        dividers
        sx={{
          maxHeight: 500,
          overflowY: "auto",
          backgroundColor: theme.palette.background.default,
        }}
      >
        {permisos.length === 0 ? (
          <Typography color="text.secondary">
            Este rol no tiene permisos asignados.
          </Typography>
        ) : (
          Object.entries(permisosPorModulo).map(
            ([moduloNombre, listaPermisos]) => (
              <Accordion
                key={moduloNombre}
                disableGutters
                sx={{
                  mb: 2,
                  borderRadius: 2,
                  backgroundColor: alpha(
                    theme.palette.primary.main,
                    isDark ? 0.08 : 0.04
                  ),
                  "&:before": { display: "none" },
                }}
              >
                <AccordionSummary
                  expandIcon={<ExpandMoreIcon />}
                  sx={{
                    "& .MuiAccordionSummary-content": {
                      alignItems: "center",
                    },
                  }}
                >
                  <Box
                    sx={{
                      display: "flex",
                      alignItems: "center",
                      justifyContent: "space-between",
                      width: "100%",
                    }}
                  >
                    <Typography fontWeight={600}>
                      {moduloNombre}
                    </Typography>

                    <Chip
                      size="small"
                      label={`${listaPermisos.length} permisos`}
                      sx={{
                        backgroundColor: alpha(
                          theme.palette.primary.main,
                          isDark ? 0.25 : 0.15
                        ),
                        fontWeight: 600,
                      }}
                    />
                  </Box>
                </AccordionSummary>

                <AccordionDetails>
                  <Stack direction="row" flexWrap="wrap" gap={1}>
                    {listaPermisos.map((permiso) => (
                      <Chip
                        key={permiso.id}
                        label={permiso.nombre}
                        size="small"
                        variant="outlined"
                      />
                    ))}
                  </Stack>
                </AccordionDetails>
              </Accordion>
            )
          )
        )}
      </DialogContent>

      <DialogActions>
        <Button onClick={onClose} variant="contained">
          Cerrar
        </Button>
      </DialogActions>
    </Dialog>
  );
}