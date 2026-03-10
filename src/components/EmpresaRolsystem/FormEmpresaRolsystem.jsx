import * as React from "react";
import PropTypes from "prop-types";
import axios from "../axiosConfig";
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  FormHelperText,
  Box,
  CircularProgress,
  Divider,
  Typography,
  Accordion,
  AccordionSummary,
  AccordionDetails,
  Checkbox,
  FormControlLabel,
} from "@mui/material";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";

export default function FormEmpresaRolSystem({
  setMessage,
  reloadData,
  open,
  setOpen,
  roles = [],
  selectedRow,
}) {

  const initialData = {
    rolId: "",
    estadoId: 1,
  };

  const [formData, setFormData] = React.useState(initialData);
  const [empresaRolId, setEmpresaRolId] = React.useState(null);
  const [modulos, setModulos] = React.useState([]);
  const [seleccion, setSeleccion] = React.useState({});
  const [loading, setLoading] = React.useState(false);
  const [loadingModulos, setLoadingModulos] = React.useState(false);
  const [errors, setErrors] = React.useState({});

  const isEditMode = Boolean(selectedRow);

  /* ==============================
     EMPRESA DESDE TOKEN
  ============================== */
  const getEmpresaIdFromToken = () => {
    const token = localStorage.getItem("token");
    if (!token) return null;

    try {
      const payload = JSON.parse(atob(token.split(".")[1]));
      return payload.empresaId ?? null;
    } catch {
      return null;
    }
  };

  /* ==============================
     CARGAR DATOS EN EDICIÓN
  ============================== */
  React.useEffect(() => {
    if (!open) return;

    if (isEditMode && selectedRow) {
      const rolEncontrado = roles.find(
        (r) =>
          r.id === selectedRow.rolId ||
          r.nombre === selectedRow.rolNombre ||
          r.name === selectedRow.rolNombre
      );

      setFormData({
        rolId: rolEncontrado?.id || "",
        estadoId: selectedRow.estadoId ?? 1,
      });

      setEmpresaRolId(selectedRow.id);
      cargarModulos();

    } else {
      setFormData(initialData);
      setEmpresaRolId(null);
      setModulos([]);
      setSeleccion({});
    }

    setErrors({});
  }, [open, selectedRow, roles]);

  const handleClose = () => {
    setOpen(false);
    setFormData(initialData);
    setEmpresaRolId(null);
    setModulos([]);
    setSeleccion({});
    setErrors({});
  };

  /* ==============================
     CREAR EMPRESA-ROL
  ============================== */
  const handleCrear = async () => {
    const empresaId = getEmpresaIdFromToken();

    if (!formData.rolId) {
      setErrors({ rolId: "El rol es obligatorio." });
      return;
    }

    try {
      setLoading(true);

      const res = await axios.post("/v1/system/empresa-rol", {
        empresaId: Number(empresaId),
        rolId: Number(formData.rolId),
      });

      const nuevoId = res.data?.id;
      setEmpresaRolId(nuevoId);

      setMessage({
        open: true,
        severity: "success",
        text: "Rol creado. Ahora asigna módulos.",
      });

      await cargarModulos();

    } catch (err) {
      setMessage({
        open: true,
        severity: "error",
        text: err?.response?.data?.message || "Error al crear",
      });
    } finally {
      setLoading(false);
    }
  };

  /* ==============================
     ACTUALIZAR
  ============================== */
  const handleUpdate = async () => {
    const empresaId = getEmpresaIdFromToken();

    try {
      setLoading(true);

      await axios.put(
        `/v1/system/empresa-rol/${selectedRow.id}`,
        {
          empresaId: Number(empresaId),
          rolId: Number(formData.rolId),
          estadoId: Number(formData.estadoId),
        }
      );

      setMessage({
        open: true,
        severity: "success",
        text: "Registro actualizado correctamente",
      });

      handleClose();
      reloadData();

    } catch (err) {
      setMessage({
        open: true,
        severity: "error",
        text: err?.response?.data?.message || "Error al actualizar",
      });
    } finally {
      setLoading(false);
    }
  };

  /* ==============================
     OBTENER MÓDULOS
  ============================== */
const cargarModulos = async () => {
  try {
    setLoadingModulos(true);

    const res = await axios.get(
      "/v1/empresa-rol-permisos/modulos-disponibles",
      {
        params: {
          page: 0,
          size: 20,
        },
      }
    );

    console.log("MODULOS OK:", res.data);

    setModulos(res.data?.content ?? []);

  } catch (err) {
    console.error("ERROR MODULOS FULL:", err);

    setMessage({
      open: true,
      severity: "error",
      text: err?.response?.data?.message || "Error al cargar módulos",
    });

    setModulos([]);
  } finally {
    setLoadingModulos(false);
  }
};

  /* ==============================
     CHECKBOX
  ============================== */
  const handleCheck = (moduloId) => {
    setSeleccion((prev) => ({
      ...prev,
      [moduloId]: !prev[moduloId],
    }));
  };

  /* ==============================
     ASIGNAR MÓDULOS
  ============================== */
  const handleAsignarModulos = async () => {
    const modulosIds = Object.entries(seleccion)
      .filter(([_, checked]) => checked)
      .map(([id]) => Number(id));

    if (!modulosIds.length) {
      setMessage({
        open: true,
        severity: "warning",
        text: "Selecciona al menos un módulo",
      });
      return;
    }

    try {
      setLoading(true);

      await axios.post(
        `/v1/empresa-rol-permisos/${empresaRolId}/asignar-modulos-permisos`,
        { modulosIds }
      );

      setMessage({
        open: true,
        severity: "success",
        text: "Permisos asignados correctamente",
      });

      handleClose();
      reloadData();

    } catch (err) {
      setMessage({
        open: true,
        severity: "error",
        text: err?.response?.data?.message || "Error al asignar permisos",
      });
    } finally {
      setLoading(false);
    }
  };

  return (
    <Dialog open={open} onClose={handleClose} fullWidth maxWidth="md">
      <DialogTitle>
        {isEditMode ? "Actualizar Empresa-Rol" : "Crear Empresa-Rol"}
      </DialogTitle>

      <DialogContent dividers>

        {!empresaRolId && (
          <>
            <FormControl fullWidth error={!!errors.rolId}>
              <InputLabel>Rol</InputLabel>
              <Select
                value={formData.rolId}
                label="Rol"
                onChange={(e) =>
                  setFormData({ ...formData, rolId: e.target.value })
                }
              >
                {roles.map((r) => (
                  <MenuItem key={r.id} value={r.id}>
                    {r.nombre ?? r.name}
                  </MenuItem>
                ))}
              </Select>
              <FormHelperText>{errors.rolId}</FormHelperText>
            </FormControl>

            <Box mt={3}>
              <Button
                variant="contained"
                fullWidth
                onClick={handleCrear}
                disabled={loading}
              >
                {loading ? <CircularProgress size={20} /> : "Crear"}
              </Button>
            </Box>
          </>
        )}

        {empresaRolId && (
          <>
            <Divider sx={{ my: 3 }} />
            <Typography variant="subtitle1" sx={{ mb: 2 }}>
              Selecciona Módulos
            </Typography>

            {loadingModulos && <CircularProgress size={24} />}

            {!loadingModulos &&
              modulos.map((modulo) => (
                <Accordion key={modulo.id}>
                  <AccordionSummary expandIcon={<ExpandMoreIcon />}>
                    <Typography>{modulo.nombre}</Typography>
                  </AccordionSummary>
                  <AccordionDetails>
                    <FormControlLabel
                      control={
                        <Checkbox
                          checked={seleccion[modulo.id] || false}
                          onChange={() => handleCheck(modulo.id)}
                        />
                      }
                      label="Asignar todos los permisos"
                    />
                  </AccordionDetails>
                </Accordion>
              ))}

            <Box mt={3}>
              <Button
                variant="contained"
                fullWidth
                onClick={handleAsignarModulos}
                disabled={loading}
              >
                {loading ? <CircularProgress size={20} /> : "Asignar Permisos"}
              </Button>
            </Box>
          </>
        )}

      </DialogContent>

      <DialogActions>
        <Button onClick={handleClose}>Cancelar</Button>
      </DialogActions>
    </Dialog>
  );
}

FormEmpresaRolSystem.propTypes = {
  setMessage: PropTypes.func.isRequired,
  reloadData: PropTypes.func.isRequired,
  open: PropTypes.bool.isRequired,
  setOpen: PropTypes.func.isRequired,
  roles: PropTypes.array,
  selectedRow: PropTypes.object,
};
