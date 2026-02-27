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
  Typography,
  Box,
  CircularProgress,
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
  const [loading, setLoading] = useState(false);
  const [loadingModulos, setLoadingModulos] = useState(false);
  const [permisosSeleccionados, setPermisosSeleccionados] = useState([]);
  const [permisosOriginales, setPermisosOriginales] = useState([]);

  const handleClose = () => {
    setOpen(false);
    setSelectedRow(null);
    setRolId("");
    setPermisosSeleccionados([]);
    setPermisosOriginales([]);
  };

  /* ===============================
     Obtener rol por nombre
  =============================== */
  const obtenerRolIdPorNombre = async (nombreRol) => {
    try {
      const res = await axios.get("/v1/items/rol/0");
      const rolEncontrado = res.data.find((r) => r.name === nombreRol);
      return rolEncontrado?.id || null;
    } catch (error) {
      console.error("Error obteniendo roles:", error);
      return null;
    }
  };

  /* ===============================
     Cargar módulos
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

  /* ===============================
     Cargar permisos actuales
  =============================== */
  const cargarPermisosActuales = async (rolId) => {
    try {
      const res = await axios.get(
        `/v1/empresa-rol-permisos/rol/${rolId}/permisos`
      );

      const ids = res.data.map((p) => p.id);
      setPermisosSeleccionados(ids);
      setPermisosOriginales(ids);
    } catch (error) {
      console.error("Error cargando permisos actuales", error);
    }
  };

  /* ===============================
     INIT
  =============================== */
  useEffect(() => {
    if (!open) return;

    const init = async () => {
      await cargarModulos();

      if (isEdit && selectedRow) {
        let idRol = selectedRow.rolId;

        if (!idRol && selectedRow.rolNombre) {
          idRol = await obtenerRolIdPorNombre(selectedRow.rolNombre);
        }

        if (!idRol) return;

        setRolId(idRol);
        await cargarPermisosActuales(idRol);
      }
    };

    init();
  }, [open, selectedRow]);

  /* ===============================
     Toggle permiso (solo visual)
  =============================== */
  const togglePermiso = (permisoId) => {
    setPermisosSeleccionados((prev) =>
      prev.includes(permisoId)
        ? prev.filter((id) => id !== permisoId)
        : [...prev, permisoId]
    );
  };

  const seleccionarTodosModulo = (permisos) => {
    const ids = permisos.map((p) => p.id);

    const todosSeleccionados = ids.every((id) =>
      permisosSeleccionados.includes(id)
    );

    if (todosSeleccionados) {
      setPermisosSeleccionados((prev) =>
        prev.filter((id) => !ids.includes(id))
      );
    } else {
      setPermisosSeleccionados((prev) => [
        ...new Set([...prev, ...ids]),
      ]);
    }
  };

  /* ===============================
     Quitar permiso inmediato
  =============================== */
  const quitarPermiso = async (permisoId) => {
    try {
      await axios.delete(
        `/v1/empresa-rol-permisos/rol/${rolId}/permisos/quitar`,
        { data: { permisosId: [permisoId] } }
      );

      setPermisosSeleccionados((prev) =>
        prev.filter((id) => id !== permisoId)
      );

      setPermisosOriginales((prev) =>
        prev.filter((id) => id !== permisoId)
      );

      setMessage({
        open: true,
        severity: "success",
        text: "Permiso eliminado correctamente",
      });
    } catch (error) {
      console.error(error.response?.data);
      setMessage({
        open: true,
        severity: "error",
        text: "Error eliminando permiso",
      });
    }
  };

  /* ===============================
     Guardar (solo agrega nuevos)
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

    if (!isEdit) {
      await axios.post("/v1/empresa-rol", {
        rolId: Number(rolId),
      });
    }

    let modulosALL = [];
    let permisosINDIVIDUAL = [];

    modulos.forEach((modulo) => {
      const permisosModuloIds = modulo.permisos.map((p) => p.id);

      const todosSeleccionados =
        permisosModuloIds.length > 0 &&
        permisosModuloIds.every((id) =>
          permisosSeleccionados.includes(id)
        );

      if (todosSeleccionados) {
        modulosALL.push(modulo.moduloId);
      } else {
        permisosModuloIds.forEach((id) => {
          if (permisosSeleccionados.includes(id)) {
            permisosINDIVIDUAL.push(id);
          }
        });
      }
    });

/* ===============================
   1️⃣ Ejecutar ALL
=============================== */

let permisosALLIds = [];

if (modulosALL.length > 0) {
  await axios.post(
    `/v1/empresa-rol-permisos/${rolId}/asignar-modulos-permisos`,
    { modulosIds: modulosALL }
  );

  // 🔥 Obtener todos los permisos que pertenecen a los módulos ALL
  modulos.forEach((modulo) => {
    if (modulosALL.includes(modulo.moduloId)) {
      modulo.permisos.forEach((p) => {
        permisosALLIds.push(p.id);
      });
    }
  });
}

/* ===============================
   2️⃣ Limpiar duplicados
=============================== */

permisosINDIVIDUAL = permisosINDIVIDUAL.filter(
  (id) =>
    !permisosOriginales.includes(id) &&
    !permisosALLIds.includes(id)
);

/* ===============================
   INDIVIDUAL SEGURO
=============================== */

let permisosNuevos = permisosSeleccionados.filter(
  (id) => !permisosOriginales.includes(id)
);

if (permisosNuevos.length > 0) {
  await axios.post(
    `/v1/empresa-rol-permisos/rol/${rolId}/permisos`,
    { permisosId: permisosNuevos }
  );
}

    setMessage({
      open: true,
      severity: "success",
      text: "Permisos actualizados correctamente",
    });

    reloadData();
    handleClose();
  } catch (error) {
    console.error(error.response?.data);

    setMessage({
      open: true,
      severity: "error",
      text: "Error al guardar permisos",
    });
  } finally {
    setLoading(false);
  }
};
  /* ===============================
     Separar módulos
  =============================== */
  const modulosConPermiso = modulos.filter((modulo) =>
    modulo.permisos.some((p) =>
      permisosSeleccionados.includes(p.id)
    )
  );

  const modulosSinPermiso = modulos.filter((modulo) =>
    !modulo.permisos.some((p) =>
      permisosSeleccionados.includes(p.id)
    )
  );

  const renderModulo = (modulo) => (
    <Accordion key={modulo.moduloId}>
      <AccordionSummary expandIcon={<ExpandMoreIcon />}>
        <Typography sx={{ fontWeight: 600 }}>
          {modulo.moduloNombre}
        </Typography>
      </AccordionSummary>

      <AccordionDetails>
        <Box sx={{ mb: 2, display: "flex", alignItems: "center" }}>
          <Checkbox
            checked={
              modulo.permisos.length > 0 &&
              modulo.permisos.every((p) =>
                permisosSeleccionados.includes(p.id)
              )
            }
            onChange={() =>
              seleccionarTodosModulo(modulo.permisos)
            }
          />
          <Typography sx={{ fontWeight: 600 }}>
            Todos los permisos
          </Typography>
        </Box>

        <Box
          sx={{
            display: "grid",
            gridTemplateColumns:
              "repeat(auto-fill, minmax(250px, 1fr))",
            gap: 2,
          }}
        >
          {modulo.permisos.map((permiso) => {
            const checked =
              permisosSeleccionados.includes(permiso.id);

            return (
              <Box
                key={permiso.id}
                sx={{
                  p: 2,
                  borderRadius: 3,
                  border: checked
                    ? "1px solid #1976d2"
                    : "1px solid rgba(255,255,255,0.1)",
                  backgroundColor: checked
                    ? "rgba(25,118,210,0.1)"
                    : "transparent",
                  display: "flex",
                  justifyContent: "space-between",
                  alignItems: "center",
                }}
              >
                <Box
                  onClick={() =>
                    togglePermiso(permiso.id)
                  }
                  sx={{
                    display: "flex",
                    alignItems: "center",
                    gap: 1,
                    cursor: "pointer",
                  }}
                >
                  <Checkbox size="small" checked={checked} />
                  <Typography variant="body2">
                    {permiso.nombre}
                  </Typography>
                </Box>

                {isEdit && checked && (
                  <Button
                    size="small"
                    color="error"
                    onClick={() =>
                      quitarPermiso(permiso.id)
                    }
                  >
                    Quitar
                  </Button>
                )}
              </Box>
            );
          })}
        </Box>
      </AccordionDetails>
    </Accordion>
  );

  return (
    <Dialog open={open} onClose={handleClose} fullWidth maxWidth="md">
      <DialogTitle>
        {isEdit
          ? "Editar Rol y Permisos"
          : "Crear Rol y Asignar Permisos"}
      </DialogTitle>

      <DialogContent>
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

        {loadingModulos ? (
          <CircularProgress />
        ) : (
          <>
            {isEdit && modulosConPermiso.length > 0 && (
              <>
                <Typography variant="h6" sx={{ mb: 2 }}>
                  Este rol tiene permisos en los siguientes módulos
                </Typography>
                {modulosConPermiso.map(renderModulo)}
              </>
            )}

            {modulosSinPermiso.length > 0 && (
              <>
                <Typography variant="h6" sx={{ mt: 4, mb: 1 }}>
                  Módulos sin permisos asignados
                </Typography>
                <Typography
                  variant="body2"
                  sx={{ mb: 2, opacity: 0.7 }}
                >
                  Estos módulos no tienen permisos. Puedes asignarlos si lo necesitas.
                </Typography>
                {modulosSinPermiso.map(renderModulo)}
              </>
            )}
          </>
        )}
      </DialogContent>

      <DialogActions>
        <Button onClick={handleClose}>Cerrar</Button>
        <Button
          variant="contained"
          onClick={handleSave}
          disabled={loading}
        >
          {loading ? "Guardando..." : "Guardar"}
        </Button>
      </DialogActions>
    </Dialog>
  );
}