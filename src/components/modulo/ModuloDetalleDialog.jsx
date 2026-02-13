import { useEffect, useState } from "react";
import axios from "../axiosConfig";

import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  Typography,
  Box,
  Grid,
  Divider,
  Chip,
  CircularProgress,
  Paper
} from "@mui/material";

import * as MuiIcons from "@mui/icons-material";

export default function ModuloDetalleDialog({
  open,
  onClose,
  moduloId,
  setMessage
}) {

  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(false);

  const [subSistemas, setSubSistemas] = useState([]);
  const [tipoModulos, setTipoModulos] = useState([]);
  const [tipoAplicaciones, setTipoAplicaciones] = useState([]);

  const token = localStorage.getItem("token");

  const authHeaders = {
    headers: {
      Authorization: `Bearer ${token}`,
      "Accept-Language": "es",
    },
  };

  useEffect(() => {
    if (!open || !moduloId) return;

    const fetchDetalle = async () => {
      try {
        setLoading(true);
        setData(null);

        // 🔥 Traemos todo en paralelo
        const [
          moduloRes,
          subSistemaRes,
          tipoModuloRes,
          tipoAplicacionRes
        ] = await Promise.all([
          axios.get(`/v1/modulos/${moduloId}`, authHeaders),
          axios.get("/v1/sub-sistemas?campos=id,nombre", authHeaders),
          axios.get("/v1/tipo-modulos?campos=id,nombre", authHeaders),
          axios.get("/v1/tipo-aplicaciones?campos=id,nombre", authHeaders),
        ]);

        setData(moduloRes.data);
        setSubSistemas(subSistemaRes.data || []);
        setTipoModulos(tipoModuloRes.data || []);
        setTipoAplicaciones(tipoAplicacionRes.data || []);

      } catch (error) {

        setMessage({
          open: true,
          severity: "error",
          text: "Error cargando información del módulo.",
        });

        onClose();

      } finally {
        setLoading(false);
      }
    };

    fetchDetalle();

  }, [open, moduloId]);

  // 🔎 Helpers para convertir ID → Nombre
  const getNombre = (list, id) =>
    list.find(item => item.id === id)?.nombre || id;

  const getEstadoNombre = (estadoId) => {
    if (estadoId === 1) return "Activo";
    if (estadoId === 2) return "Inactivo";
    return estadoId;
  };

  const IconComponent =
    data?.icon ? MuiIcons[data.icon] || MuiIcons.HelpOutline : null;

  return (
    <Dialog open={open} onClose={onClose} maxWidth="md" fullWidth>

      <DialogTitle sx={{ fontWeight: 600 }}>
        Detalle del Módulo
      </DialogTitle>

      <DialogContent dividers sx={{ py: 3 }}>

        {loading && (
          <Box sx={{ display: "flex", justifyContent: "center", py: 4 }}>
            <CircularProgress />
          </Box>
        )}

        {!loading && data && (

          <Paper variant="outlined" sx={{ p: 3 }}>

            {/* HEADER */}
            <Box
              sx={{
                display: "flex",
                alignItems: "center",
                gap: 2,
                mb: 3
              }}
            >
              {IconComponent && (
                <IconComponent fontSize="large" />
              )}

              <Box>
                <Typography variant="h6">
                  {data.nombre}
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  {data.descripcion}
                </Typography>
              </Box>
            </Box>

            <Divider sx={{ mb: 3 }} />

            {/* DATOS */}
            <Grid container spacing={2}>

              <Grid item xs={6}>
                <Typography variant="body2" color="text.secondary">
                  URL
                </Typography>
                <Typography>{data.url}</Typography>
              </Grid>

              <Grid item xs={6}>
                <Typography variant="body2" color="text.secondary">
                  Nombre ID
                </Typography>
                <Typography>{data.nombreId}</Typography>
              </Grid>

              <Grid item xs={6}>
                <Typography variant="body2" color="text.secondary">
                  Estado
                </Typography>
                <Chip
                  label={getEstadoNombre(data.estadoId)}
                  color={data.estadoId === 1 ? "success" : "error"}
                  size="small"
                />
              </Grid>

              <Grid item xs={6}>
                <Typography variant="body2" color="text.secondary">
                  SubSistema
                </Typography>
                <Typography>
                  {getNombre(subSistemas, data.subSistemaId)}
                </Typography>
              </Grid>

              <Grid item xs={6}>
                <Typography variant="body2" color="text.secondary">
                  Tipo Módulo
                </Typography>
                <Typography>
                  {getNombre(tipoModulos, data.tipoModuloId)}
                </Typography>
              </Grid>

              <Grid item xs={6}>
                <Typography variant="body2" color="text.secondary">
                  Tipo Aplicación
                </Typography>
                <Typography>
                  {getNombre(tipoAplicaciones, data.tipoAplicacionId)}
                </Typography>
              </Grid>

              <Grid item xs={6}>
                <Typography variant="body2" color="text.secondary">
                  Requerido
                </Typography>
                <Typography>
                  {data.requerido ? "Sí" : "No"}
                </Typography>
              </Grid>

            </Grid>

            {/* ROLES */}
            {data.roles?.length > 0 && (
              <>
                <Divider sx={{ my: 3 }} />

                <Typography variant="subtitle2" sx={{ mb: 1 }}>
                  Roles asociados
                </Typography>

                <Box sx={{ display: "flex", flexWrap: "wrap", gap: 1 }}>
                  {data.roles.map((rol) => (
                    <Chip
                      key={rol}
                      label={rol}
                      variant="outlined"
                      size="small"
                    />
                  ))}
                </Box>
              </>
            )}

          </Paper>

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
