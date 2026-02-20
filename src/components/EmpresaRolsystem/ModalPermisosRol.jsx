// src/components/empresaRolSystem/ModalPermisosRol.jsx
import React, { useEffect, useState } from "react";
import PropTypes from "prop-types";
import axios from "../axiosConfig";
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  Typography,
  Accordion,
  AccordionSummary,
  AccordionDetails,
  Checkbox,
  FormControlLabel,
  Box,
  CircularProgress,
  Stack,
  TextField,
  Divider,
} from "@mui/material";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import DoneAllIcon from '@mui/icons-material/DoneAll';

export default function ModalPermisosRol({ open, setOpen, rolId, rolNombre, setMessage }) {
  const [modulos, setModulos] = useState([]);
  const [seleccion, setSeleccion] = useState({});
  const [loading, setLoading] = useState(false);

  const [subsistemaIds, setSubsistemaIds] = useState("");
  const [rempModulo, setRempModulo] = useState({ actual: "", nuevo: "" });
  const [rempPermiso, setRempPermiso] = useState({ actual: "", nuevo: "" });

  /* ==============================
      1. CARGAR DATOS (GET)
  ============================== */
const fetchPermisosActuales = async () => {
  if (!rolId) return;

  try {
    // ⚠️ Esto solo funcionará si realmente quieres asignar
    const modulosIds = obtenerModulosSeleccionados();

    if (!modulosIds.length) return;

    const res = await axios.post(
      `/v1/empresa-rol-permisos/${rolId}/asignar-modulos-permisos`,
      { modulosIds }
    );

    const autoridades = res.data?.autoridades || [];

    const nuevaSeleccion = {};

    modulos.forEach((modulo) => {
      const mId = modulo.id || modulo.moduloId;

      const metodosAsignados = modulo.permisos
        ?.filter((permiso) =>
          autoridades.includes(permiso.autoridad)
        )
        .map((permiso) => permiso.metodo || permiso.autoridad);

      if (metodosAsignados?.length) {
        nuevaSeleccion[mId] = metodosAsignados;
      }
    });

    setSeleccion(nuevaSeleccion);

  } catch (err) {
    console.error("Error asignando módulos:", err.response?.data || err.message);
  }
};

  const fetchModulos = async (subsistemas = null) => {
    try {
      setLoading(true);
      const endpoint = subsistemas 
        ? `/v1/empresa-rol-permisos/test-modulos?subsistemaIds=${subsistemas}`
        : `/v1/empresa-rol-permisos/modulos-disponibles?page=0&size=100`;

      const res = await axios.get(endpoint);
      const data = subsistemas ? res.data : (res.data?.content ?? []);
      setModulos(Array.isArray(data) ? data : []);

      if (!subsistemas) await fetchPermisosActuales();
    } catch (err) {
      handleError(err, "cargar módulos");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (!open || !rolId) return;
    fetchModulos();
    setSeleccion({});
    setSubsistemaIds("");
    setRempModulo({ actual: "", nuevo: "" });
    setRempPermiso({ actual: "", nuevo: "" });
  }, [open, rolId]);

  /* ==============================
      2. LÓGICA DE SELECCIÓN RÁPIDA
  ============================== */
  
  // MARCAR TODO EL MODAL (GLOBAL)
  const handleSeleccionarTodoGlobal = () => {
    const nuevaSeleccion = {};
    modulos.forEach((modulo) => {
      const mId = modulo.id || modulo.moduloId;
      nuevaSeleccion[mId] = modulo.permisos?.map(p => p.metodo || p.autoridad) || [];
    });
    setSeleccion(nuevaSeleccion);
  };

  // MARCAR SOLO UN BLOQUE (MÓDULO)
  const handleSeleccionarBloque = (e, modulo) => {
    e.stopPropagation(); // Evita que se cierre el acordeón
    const mId = modulo.id || modulo.moduloId;
    const todosLosMetodos = modulo.permisos?.map(p => p.metodo || p.autoridad) || [];
    setSeleccion(prev => ({ ...prev, [mId]: todosLosMetodos }));
  };

  const handleCheck = (moduloId, metodo) => {
    setSeleccion((prev) => {
      const actuales = prev[moduloId] || [];
      if (actuales.includes(metodo)) {
        return { ...prev, [moduloId]: actuales.filter((m) => m !== metodo) };
      }
      return { ...prev, [moduloId]: [...actuales, metodo] };
    });
  };

  const obtenerModulosSeleccionados = () => Object.keys(seleccion).map(Number).filter(id => !isNaN(id));

  const handleError = (err, contexto) => {
    console.error(`🚨 Error en ${contexto}:`, err.response?.data || err.message);
    const status = err?.response?.status;
    let text = `Error en ${contexto}`;
    if (status === 400) text = "Datos inválidos (verificar IDs)";
    if (status === 404) text = "No encontrado (Rol o Módulo inexistente)";
    if (status === 409) text = "Conflicto con los datos actuales";
    if (status === 500) text = "Error interno del servidor";
    setMessage({ open: true, severity: "error", text });
  };

  /* ==============================
      3. ACCIONES DE ASIGNACIÓN
  ============================== */
const handleAsignarTodos = async () => {
  const modulosIds = obtenerModulosSeleccionados();

  if (!modulosIds.length) {
    return setMessage({
      open: true,
      severity: "warning",
      text: "Selecciona al menos un módulo",
    });
  }

  try {
    setLoading(true);

    const res = await axios.post(
      `/v1/empresa-rol-permisos/${rolId}/asignar-modulos-permisos`,
      { modulosIds }
    );

    const autoridades = res.data?.autoridades || [];

    // 🔥 Construir nueva selección según autoridades devueltas
    const nuevaSeleccion = {};

    modulos.forEach((modulo) => {
      const mId = modulo.id || modulo.moduloId;

      const metodosAsignados = modulo.permisos
        ?.filter((permiso) =>
          autoridades.includes(permiso.autoridad)
        )
        .map((permiso) => permiso.metodo || permiso.autoridad);

      if (metodosAsignados?.length) {
        nuevaSeleccion[mId] = metodosAsignados;
      }
    });

    setSeleccion(nuevaSeleccion);

    setMessage({
      open: true,
      severity: "success",
      text: "Permisos asignados correctamente",
    });

  } catch (err) {
    handleError(err, "asignar todos");
  } finally {
    setLoading(false);
  }
};

  const handleGuardarMetodos = async () => {
    const modulosMetodos = Object.entries(seleccion)
      .filter(([_, metodos]) => metodos.length > 0)
      .map(([moduloId, metodos]) => ({ moduloId: Number(moduloId), metodos }));

    if (!modulosMetodos.length) return setMessage({ open: true, severity: "warning", text: "Selecciona al menos un permiso" });

    try {
      setLoading(true);
      await axios.post(`/v1/empresa-rol-permisos/${rolId}/asignar-modulos-metodos`, { modulosMetodos });
      setMessage({ open: true, severity: "success", text: "Permisos personalizados guardados." });
      await fetchPermisosActuales(); 
    } catch (err) { handleError(err, "guardar métodos"); } finally { setLoading(false); }
  };

  const handleAsignarLectura = async () => {
    const modulosIds = obtenerModulosSeleccionados();
    if (!modulosIds.length) return setMessage({ open: true, severity: "warning", text: "Selecciona módulos primero." });
    try {
      setLoading(true);
      await axios.post(`/v1/empresa-rol-permisos/${rolId}/asignar-modulos-lectura`, { modulosIds });
      setMessage({ open: true, severity: "success", text: "Lectura asignada con éxito." });
      await fetchPermisosActuales();
    } catch (err) { handleError(err, "asignar lectura"); } finally { setLoading(false); }
  };

  const handleQuitar = async () => {
    const modulosIds = obtenerModulosSeleccionados();
    if (!modulosIds.length) return setMessage({ open: true, severity: "warning", text: "Selecciona qué quitar." });
    try {
      setLoading(true);
      await axios.delete(`/v1/empresa-rol-permisos/${rolId}/quitar-modulos-permisos`, { data: { modulosIds } });
      setMessage({ open: true, severity: "success", text: "Módulos removidos." });
      setSeleccion({});
      await fetchPermisosActuales();
    } catch (err) { handleError(err, "quitar módulos"); } finally { setLoading(false); }
  };

  /* ==============================
      4. ACCIONES DE REEMPLAZO
  ============================== */
  const handleReemplazarPermiso = async () => {
    if (!rempPermiso.actual || !rempPermiso.nuevo) return setMessage({ open: true, severity: "warning", text: "Completa ambos IDs de permiso." });
    try {
      setLoading(true);
      await axios.put(`/v1/empresa-rol-permisos/${rolId}/reemplazar-permiso`, {
        permisoIdActual: Number(rempPermiso.actual),
        nuevoPermisoId: Number(rempPermiso.nuevo)
      });
      setMessage({ open: true, severity: "success", text: "Permiso reemplazado." });
      setRempPermiso({ actual: "", nuevo: "" });
      await fetchPermisosActuales();
    } catch (err) { handleError(err, "reemplazar permiso"); } finally { setLoading(false); }
  };

  const handleReemplazarModulo = async () => {
    if (!rempModulo.actual || !rempModulo.nuevo) return setMessage({ open: true, severity: "warning", text: "Completa ambos IDs de módulo." });
    try {
      setLoading(true);
      await axios.put(`/v1/empresa-rol-permisos/${rolId}/reemplazar-modulo`, {
        moduloIdActual: Number(rempModulo.actual),
        nuevoModuloId: Number(rempModulo.nuevo)
      });
      setMessage({ open: true, severity: "success", text: "Módulo reemplazado." });
      setRempModulo({ actual: "", nuevo: "" });
      await fetchPermisosActuales();
    } catch (err) { handleError(err, "reemplazar módulo"); } finally { setLoading(false); }
  };

  return (
    <Dialog open={open} onClose={() => setOpen(false)} fullWidth maxWidth="md">
      <DialogTitle>Gestionar Permisos – {rolNombre} (ID: {rolId})</DialogTitle>

      <DialogContent dividers>
        {/* BOTÓN GLOBAL DE SELECCIÓN */}
        <Box sx={{ mb: 2, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <Typography variant="body2" color="text.secondary">
            Selecciona módulos para realizar asignaciones o utiliza las opciones avanzadas abajo.
          </Typography>
          <Button 
            variant="contained" 
            color="info" 
            size="small"
            startIcon={<CheckCircleIcon />}
            onClick={handleSeleccionarTodoGlobal}
          >
            Marcar Todo
          </Button>
        </Box>

        {loading && modulos.length === 0 ? (
          <CircularProgress sx={{ display: 'block', mx: 'auto', my: 3 }} />
        ) : modulos.length === 0 ? (
          <Typography variant="body2">No hay módulos disponibles.</Typography>
        ) : (
          modulos.map((modulo) => {
            const currentMId = modulo.id || modulo.moduloId;
            return (
              <Accordion key={currentMId}>
                <AccordionSummary expandIcon={<ExpandMoreIcon />}>
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', width: '100%', alignItems: 'center', pr: 1 }}>
                    <Typography sx={{ fontWeight: 600 }}>
                      {modulo.nombre || modulo.moduloNombre} (ID: {currentMId})
                    </Typography>
                    {/* BOTÓN DE BLOQUE ESPECÍFICO */}
                    <Button 
                      size="small" 
                      variant="outlined" 
                      startIcon={<DoneAllIcon />}
                      onClick={(e) => handleSeleccionarBloque(e, modulo)}
                      sx={{ fontSize: '0.65rem', py: 0 }}
                    >
                      Bloque
                    </Button>
                  </Box>
                </AccordionSummary>
                <AccordionDetails>
                  <Box display="flex" flexWrap="wrap" gap={2}>
                    {modulo.permisos?.map((permiso) => (
                      <FormControlLabel
                        key={permiso.id}
                        control={
                          <Checkbox
                            checked={seleccion[currentMId]?.includes(permiso.metodo || permiso.autoridad) || false}
                            onChange={() => handleCheck(currentMId, permiso.metodo || permiso.autoridad)}
                          />
                        }
                        label={`${permiso.nombre} (ID: ${permiso.id})`}
                      />
                    ))}
                  </Box>
                </AccordionDetails>
              </Accordion>
            );
          })
        )}

        <Divider sx={{ my: 4 }} />
        
        <Typography variant="subtitle1" fontWeight="bold" sx={{ mb: 2 }}>⚙️ Acciones Avanzadas</Typography>

        {/* BUSCADOR */}
        <Accordion sx={{ bgcolor: 'rgba(0, 0, 0, 0.03)' }}>
          <AccordionSummary expandIcon={<ExpandMoreIcon />}>
            <Typography>Buscar Módulos por Subsistema</Typography>
          </AccordionSummary>
          <AccordionDetails>
            <Stack direction="row" spacing={2} alignItems="center">
              <TextField 
                size="small" label="IDs de subsistemas (Ej: 1,2,3)" 
                value={subsistemaIds} onChange={(e) => setSubsistemaIds(e.target.value)} fullWidth
              />
              <Button variant="outlined" onClick={() => fetchModulos(subsistemaIds)} disabled={loading}>Buscar</Button>
            </Stack>
          </AccordionDetails>
        </Accordion>

        {/* REEMPLAZO MÓDULO */}
        <Accordion sx={{ bgcolor: 'rgba(0, 0, 0, 0.03)' }}>
          <AccordionSummary expandIcon={<ExpandMoreIcon />}>
            <Typography>Reemplazar Módulo Completo</Typography>
          </AccordionSummary>
          <AccordionDetails>
            <Stack direction="row" spacing={2} alignItems="center">
              <TextField size="small" label="ID Actual" type="number" value={rempModulo.actual} onChange={(e) => setRempModulo({ ...rempModulo, actual: e.target.value })} />
              <Typography>por</Typography>
              <TextField size="small" label="Nuevo ID" type="number" value={rempModulo.nuevo} onChange={(e) => setRempModulo({ ...rempModulo, nuevo: e.target.value })} />
              <Button variant="contained" color="secondary" onClick={handleReemplazarModulo} disabled={loading}>Reemplazar</Button>
            </Stack>
          </AccordionDetails>
        </Accordion>

        {/* REEMPLAZO PERMISO */}
        <Accordion sx={{ bgcolor: 'rgba(0, 0, 0, 0.03)' }}>
          <AccordionSummary expandIcon={<ExpandMoreIcon />}>
            <Typography>Reemplazar Permiso Individual</Typography>
          </AccordionSummary>
          <AccordionDetails>
            <Stack direction="row" spacing={2} alignItems="center">
              <TextField size="small" label="ID Permiso Actual" type="number" value={rempPermiso.actual} onChange={(e) => setRempPermiso({ ...rempPermiso, actual: e.target.value })} />
              <Typography>por</Typography>
              <TextField size="small" label="Nuevo ID Permiso" type="number" value={rempPermiso.nuevo} onChange={(e) => setRempPermiso({ ...rempPermiso, nuevo: e.target.value })} />
              <Button variant="contained" color="secondary" onClick={handleReemplazarPermiso} disabled={loading}>Reemplazar</Button>
            </Stack>
          </AccordionDetails>
        </Accordion>
      </DialogContent>

      <DialogActions sx={{ p: 2, flexDirection: "column", gap: 2 }}>
        <Stack direction="row" spacing={1} justifyContent="center" flexWrap="wrap">
          <Button variant="outlined" color="error" onClick={handleQuitar} disabled={loading}>Quitar</Button>
          <Button variant="outlined" color="info" onClick={handleAsignarLectura} disabled={loading}>Solo Lectura</Button>
          <Button variant="outlined" color="warning" onClick={handleAsignarTodos} disabled={loading}>Asignar Todos</Button>
          <Button variant="contained" color="primary" onClick={handleGuardarMetodos} disabled={loading}>
            {loading ? <CircularProgress size={20} color="inherit" /> : "Guardar Selección"}
          </Button>
        </Stack>
        <Button onClick={() => setOpen(false)} fullWidth color="inherit">Cerrar</Button>
      </DialogActions>
    </Dialog>
  );
}

ModalPermisosRol.propTypes = {
  open: PropTypes.bool.isRequired,
  setOpen: PropTypes.func.isRequired,
  rolId: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  rolNombre: PropTypes.string,
  setMessage: PropTypes.func.isRequired,
};