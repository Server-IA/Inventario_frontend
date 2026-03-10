import { useEffect, useState } from "react";
import axios from "../axiosConfig";
import { useNavigate } from "react-router-dom";

import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  Typography,
  Box,
  Divider,
  CircularProgress,
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableRow,
  TableContainer,
  Paper
} from "@mui/material";

import * as MuiIcons from "@mui/icons-material";

export default function ModuloDisponiblesMenu({ open, onClose, setMessage }) {

  const [menuData, setMenuData] = useState([]);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const token = localStorage.getItem("token");

  const authHeaders = {
    headers: {
      Authorization: `Bearer ${token}`,
      "Content-Type": "application/json",
      "Accept-Language": "es",
    },
  };

  const listarModulosDisponibles = async () => {
    try {
      setLoading(true);

      const res = await axios.get(
        "/v2/modulos?disponiblesParaMenu=true",
        authHeaders
      );

      setMenuData(Array.isArray(res.data) ? res.data : []);

    } catch (error) {
      setMessage({
        open: true,
        severity: "error",
        text: "Error cargando módulos disponibles.",
      });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (open) {
      listarModulosDisponibles();
    }
  }, [open]);

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="md">

      <DialogTitle>
        Módulos disponibles para menú
      </DialogTitle>

      <DialogContent dividers sx={{ maxHeight: 550 }}>

        {loading && (
          <Box sx={{ display: "flex", justifyContent: "center", p: 3 }}>
            <CircularProgress />
          </Box>
        )}

        {!loading && menuData.map((sub, subIndex) => (

          <Box key={`${sub.nombre}-${subIndex}`} sx={{ mb: 4 }}>

            <Typography variant="h6" sx={{ mb: 1 }}>
              {sub.nombre}
            </Typography>

            <Divider sx={{ mb: 2 }} />

            <TableContainer component={Paper} variant="outlined">
              <Table size="small">

                <TableHead>
                  <TableRow>
                    <TableCell><strong>ID</strong></TableCell>
                    <TableCell><strong>Nombre</strong></TableCell>
                    <TableCell><strong>URL</strong></TableCell>
                    <TableCell><strong>Ícono</strong></TableCell>
                  </TableRow>
                </TableHead>

                <TableBody>

                  {sub.modulos?.map((mod, modIndex) => {

                    const ModIcon =
                      MuiIcons[mod.icono] || MuiIcons.HelpOutline;

                    return (
                      <TableRow
                        key={`${mod.id}-${modIndex}`}
                        hover
                        sx={{ cursor: "pointer" }}
                        onClick={() => navigate(mod.url)}
                      >
                        <TableCell>{mod.id}</TableCell>
                        <TableCell>{mod.nombre}</TableCell>
                        <TableCell>{mod.url}</TableCell>

                        <TableCell>
                          <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
                            <ModIcon fontSize="small" />
                            <Typography variant="body2">
                              {mod.icono}
                            </Typography>
                          </Box>
                        </TableCell>

                      </TableRow>
                    );
                  })}

                </TableBody>

              </Table>
            </TableContainer>

          </Box>

        ))}

        {!loading && menuData.length === 0 && (
          <Typography variant="body2">
            No hay módulos disponibles.
          </Typography>
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
