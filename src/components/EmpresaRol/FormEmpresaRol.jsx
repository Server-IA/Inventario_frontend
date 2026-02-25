import React, { useEffect, useState } from "react";
import axios from "../axiosConfig";
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Checkbox,
  FormControlLabel,
  Typography,
  Box,
  CircularProgress,
  Radio,
  RadioGroup,
  Divider,
  Accordion,
  AccordionSummary,
  AccordionDetails,
} from "@mui/material";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";

export default function FormEmpresaRol({
  open,
  setOpen,
  selectedRow,
  setSelectedRow,
  setMessage,
  reloadData,
  roles,
}) {
  const isEdit = Boolean(selectedRow?.id);

  const [rolId, setRolId] = useState("");
  const [modulos, setModulos] = useState([]);
  const [configModulos, setConfigModulos] = useState({});
  const [loading, setLoading] = useState(false);
  const [loadingModulos, setLoadingModulos] = useState(false);
  const [resumen, setResumen] = useState(null);
  const [permisosActuales, setPermisosActuales] = useState([]);
  const [permisosSeleccionados, setPermisosSeleccionados] = useState([]);
  const [todosPermisos, setTodosPermisos] = useState([]);
  const METODOS_MAP = {
  Listar: "GET",
  Crear: "POST",
  Actualizar: "PUT",
  Eliminar: "DELETE",
};

  const handleClose = () => {
    setOpen(false);
    setSelectedRow(null);
    setRolId("");
    setConfigModulos({});
    setResumen(null);
  };
  /* ===============================
     Cargar módulos disponibles
  =============================== */
  const cargarModulos = async () => {
    try {
      setLoadingModulos(true);
      const res = await axios.get(
        "/v1/empresa-rol-permisos/modulos-disponibles?page=0&size=100"
      );
      setModulos(res.data?.content || []);
    } catch {
      setMessage({
        open: true,
        severity: "error",
        text: "Error al cargar módulos",
      });
    } finally {
      setLoadingModulos(false);
    }
  };
  const permisosDisponibles = todosPermisos.filter(
  (permiso) =>
    !permisosActuales.some((p) => p.id === permiso.id)
);
const agregarPermisos = async () => {
  try {
    if (permisosSeleccionados.length === 0) return;

    await axios.post(
      `/v1/empresa-rol-permisos/rol/${rolId}/permisos`,
      { permisosId: permisosSeleccionados }
    );

    const nuevos = todosPermisos.filter((p) =>
      permisosSeleccionados.includes(p.id)
    );

    setPermisosActuales((prev) => [...prev, ...nuevos]);
    setPermisosSeleccionados([]);
  } catch (error) {
    console.error("Error agregando permisos", error);
  }
};
const quitarPermisoIndividual = async (permisoId) => {
  try {
    await axios.delete(
      `/v1/empresa-rol-permisos/rol/${rolId}/permisos/quitar`,
      {
        data: { permisosId: [permisoId] },
      }
    );

    setPermisosActuales((prev) =>
      prev.filter((p) => p.id !== permisoId)
    );
  } catch (error) {
    console.error("Error quitando permiso", error);
  }
};
  useEffect(() => {
  const lista = modulos.flatMap((m) => m.permisos);
  setTodosPermisos(lista);
}, [modulos]);
useEffect(() => {
  if (!open) return;

  const init = async () => {
    await cargarModulos();

    if (isEdit && selectedRow) {
      //  Buscar el rolId real usando el nombre
      const idRol = await obtenerRolIdPorNombre(
        selectedRow.rolNombre
      );

      if (!idRol) {
        console.error("No se pudo obtener el rolId real");
        return;
      }

      setRolId(idRol);
      await cargarPermisosActuales(idRol);
    }
  };

  init();
}, [open, selectedRow]);

  /* ===============================
     Configuración por módulo
  =============================== */
  const cambiarModo = (moduloId, modo) => {
    setConfigModulos((prev) => ({
      ...prev,
      [moduloId]: {
        modo,
        metodos: [],
        permisos: [],
      },
    }));
  };
const cargarPermisosActuales = async (rolId) => {
  try {
    const res = await axios.get(
      `/v1/empresa-rol-permisos/rol/${rolId}/permisos`
    );

    setPermisosActuales(res.data);
  } catch (error) {
    console.error("Error cargando permisos actuales", error);
  }
};

const obtenerRolIdPorNombre = async (nombreRol) => {
  try {
    const res = await axios.get("/v1/items/rol/0");

    const rolEncontrado = res.data.find(
      (r) => r.name === nombreRol
    );

    return rolEncontrado?.id || null;
  } catch (error) {
    console.error("Error obteniendo roles:", error);
    return null;
  }
};
  const toggleMetodo = (moduloId, metodo) => {
    setConfigModulos((prev) => {
      const actuales = prev[moduloId]?.metodos || [];
      return {
        ...prev,
        [moduloId]: {
          ...prev[moduloId],
          metodos: actuales.includes(metodo)
            ? actuales.filter((m) => m !== metodo)
            : [...actuales, metodo],
        },
      };
    });
  };

  const togglePermiso = (moduloId, permisoId) => {
    setConfigModulos((prev) => {
      const actuales = prev[moduloId]?.permisos || [];
      return {
        ...prev,
        [moduloId]: {
          ...prev[moduloId],
          permisos: actuales.includes(permisoId)
            ? actuales.filter((p) => p !== permisoId)
            : [...actuales, permisoId],
        },
      };
    });
  };

  /* ===============================
     Guardar
  =============================== */
  const handleSave = async () => {
    if (!rolId) {
      setMessage({
        open: true,
        severity: "warning",
        text: "Debe seleccionar un rol",
      });
      return;
    }

    try {
      setLoading(true);

      // Crear empresa-rol si es nuevo
      if (!isEdit) {
        await axios.post("/v1/empresa-rol", {
          rolId: Number(rolId),
        });
      }

      let ultimaRespuesta = null;

      for (const moduloId in configModulos) {
        const config = configModulos[moduloId];

        if (config.modo === "ALL") {
          ultimaRespuesta = await axios.post(
            `/v1/empresa-rol-permisos/${rolId}/asignar-modulos-permisos`,
            { modulosIds: [Number(moduloId)] }
          );
        }

        if (config.modo === "READ") {
          ultimaRespuesta = await axios.post(
            `/v1/empresa-rol-permisos/${rolId}/asignar-modulos-lectura`,
            { modulosIds: [Number(moduloId)] }
          );
        }

        if (config.modo === "METHODS") {
          if (!config.metodos || config.metodos.length === 0) {
            setMessage({
              open: true,
              severity: "warning",
              text: "Debe seleccionar al menos un método",
            });
            return;
          }

          ultimaRespuesta = await axios.post(
            `/v1/empresa-rol-permisos/${rolId}/asignar-modulos-metodos`,
            {
              modulosMetodos: [
                {
                  moduloId: Number(moduloId),
                  metodos: config.metodos.map((m) => METODOS_MAP[m]),
                },
              ],
            }
          );
        }

        if (config.modo === "INDIVIDUAL") {
          ultimaRespuesta = await axios.post(
            `/v1/empresa-rol-permisos/rol/${rolId}/permisos`,
            { permisosId: config.permisos }
          );
        }
      }

      setResumen(ultimaRespuesta?.data);

      setMessage({
        open: true,
        severity: "success",
        text: "Permisos actualizados correctamente",
      });

      reloadData();
    } catch (err) {
      console.error(err.response?.data);
      setMessage({
        open: true,
        severity: "error",
        text: "Error al guardar",
      });
    } finally {
      setLoading(false);
    }
  };
const quitarModuloCompleto = async (moduloId) => {
  try {
    await axios.delete(
      `/v1/empresa-rol-permisos/${rolId}/quitar-modulos-permisos`,
      {
        data: { modulosIds: [Number(moduloId)] },
      }
    );

    setMessage({
      open: true,
      severity: "success",
      text: "Permisos del módulo eliminados correctamente",
    });

    await cargarPermisosActuales(rolId);
  } catch (error) {
    console.error("Error quitando módulo completo", error);
    setMessage({
      open: true,
      severity: "error",
      text: "Error eliminando permisos del módulo",
    });
  }
};
const reemplazarPermiso = async (permisoActualId, nuevoPermisoId) => {
  try {
    const res = await axios.put(
      `/v1/empresa-rol-permisos/${rolId}/reemplazar-permiso`,
      {
        permisoIdActual: permisoActualId,
        nuevoPermisoId: nuevoPermisoId,
      }
    );

    setResumen(res.data);

    setMessage({
      open: true,
      severity: "success",
      text: "Permiso reemplazado correctamente",
    });

    await cargarPermisosActuales(rolId);
  } catch (error) {
    console.error("Error reemplazando permiso", error);
  }
};
const reemplazarModulo = async (moduloActualId, nuevoModuloId) => {
  try {
    const res = await axios.put(
      `/v1/empresa-rol-permisos/${rolId}/reemplazar-modulo`,
      {
        moduloIdActual: moduloActualId,
        nuevoModuloId: nuevoModuloId,
      }
    );

    setResumen(res.data);

    setMessage({
      open: true,
      severity: "success",
      text: "Módulo reemplazado correctamente",
    });

    await cargarPermisosActuales(rolId);
  } catch (error) {
    console.error("Error reemplazando módulo", error);
  }
};

  return (
    <Dialog open={open} onClose={handleClose} fullWidth maxWidth="md">
      <DialogTitle>
        {isEdit ? "Editar Rol y Permisos" : "Crear Rol y Asignar Permisos"}
      </DialogTitle>

      <DialogContent>

        {/* SELECT ROL */}
        <FormControl fullWidth sx={{ mb: 3 }}>
          <InputLabel>Rol</InputLabel>
          <Select
            value={rolId}
            onChange={(e) => setRolId(e.target.value)}
            disabled={isEdit}
          >
            {roles.map((r) => (
              <MenuItem key={r.id} value={r.id}>
                {r.nombre ?? r.name ?? r.rolNombre}
              </MenuItem>
            ))}
          </Select>
        </FormControl>

        <Divider sx={{ mb: 2 }} />

        {/* ACCORDION POR MÓDULO */}
        {loadingModulos ? (
          <CircularProgress />
        ) : (
      modulos.map((modulo) => (
        <Accordion key={modulo.moduloId}>
          <AccordionSummary expandIcon={<ExpandMoreIcon />}>
            <Typography sx={{ fontWeight: 600 }}>
              {modulo.moduloNombre}
            </Typography>
          </AccordionSummary>

          <AccordionDetails>

            {isEdit && (
              <Button
                color="error"
                size="small"
                sx={{ mb: 2 }}
                onClick={() => quitarModuloCompleto(modulo.moduloId)}
              >
                Quitar todos los permisos de este módulo
              </Button>
            )}

            <RadioGroup
              value={configModulos[modulo.moduloId]?.modo || ""}
              onChange={(e) =>
                cambiarModo(modulo.moduloId, e.target.value)
              }
            >
              <FormControlLabel
                value="ALL"
                control={<Radio />}
                label="Todos los permisos"
              />

              <FormControlLabel
                value="READ"
                control={<Radio />}
                label="Solo lectura"
              />

              <FormControlLabel
                value="METHODS"
                control={<Radio />}
                label="Filtrar por tipo de acción"
              />

              {configModulos[modulo.moduloId]?.modo === "METHODS" && (
                <Box sx={{ ml: 4, mt: 1 }}>
                  {["Listar", "Crear", "Actualizar", "Eliminar"].map((metodoLabel) => (
                    <FormControlLabel
                      key={metodoLabel}
                      control={
                        <Checkbox
                          checked={
                            configModulos[modulo.moduloId]?.metodos?.includes(metodoLabel) || false
                          }
                          onChange={() =>
                            toggleMetodo(modulo.moduloId, metodoLabel)
                          }
                        />
                      }
                      label={metodoLabel}
                    />
                  ))}
                </Box>
              )}

              <FormControlLabel
                value="INDIVIDUAL"
                control={<Radio />}
                label="Seleccionar permisos individuales"
              />

              {configModulos[modulo.moduloId]?.modo === "INDIVIDUAL" && (
                <Box sx={{ ml: 4, mt: 1 }}>
                  {modulo.permisos.map((permiso) => (
                    <FormControlLabel
                      key={permiso.id}
                      control={
                        <Checkbox
                          checked={
                            configModulos[modulo.moduloId]?.permisos?.includes(
                              permiso.id
                            ) || false
                          }
                          onChange={() =>
                            togglePermiso(modulo.moduloId, permiso.id)
                          }
                        />
                      }
                      label={`${permiso.nombre} (${permiso.metodo})`}
                    />
                  ))}
                </Box>
              )}
            </RadioGroup>
          </AccordionDetails>
        </Accordion>
      ))
        )}
        {isEdit && (
  <>
    {/* PERMISOS ACTUALES */}
    <Typography variant="h6" sx={{ mt: 2 }}>
      Permisos actuales
    </Typography>

    {permisosActuales.map((permiso) => (
      <Box
        key={permiso.id}
        display="flex"
        justifyContent="space-between"
        alignItems="center"
        sx={{ mb: 1 }}
      >
        <Typography>
          {permiso.nombre}
        </Typography>

        <Button
          color="error"
          size="small"
          onClick={() =>
            quitarPermisoIndividual(permiso.id)
          }
        >
          Quitar
        </Button>
      </Box>
    ))}

    <Divider sx={{ my: 3 }} />
  </>
)}


        {/* RESUMEN */}
        {resumen && (
          <Box sx={{ mt: 3 }}>
            <Typography variant="h6">Resumen:</Typography>
            <Typography>
              Permisos asignados: {resumen.permisosAsignados}
            </Typography>
            <Typography>
              Módulos: {resumen.modulos?.join(", ")}
            </Typography>
          </Box>
        )}
      </DialogContent>

      <DialogActions>
        <Button onClick={handleClose}>Cerrar</Button>
        <Button variant="contained" onClick={handleSave} disabled={loading}>
          {loading ? "Guardando..." : "Guardar"}
        </Button>
  
      </DialogActions>
    </Dialog>
  );
}