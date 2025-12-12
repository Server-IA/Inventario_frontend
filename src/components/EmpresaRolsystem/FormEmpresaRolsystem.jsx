// src/components/empresaRolSystem/FormEmpresaRolsystem.jsx
import * as React from "react";
import PropTypes from "prop-types";
import axios from "../axiosConfig";
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  FormHelperText,
  Typography,
  Stack,
} from "@mui/material";
import StackButtons from "../StackButtons";

export default function FormEmpresaRolSystem({
  selectedRow,
  setSelectedRow,
  setMessage,
  reloadData,
  open,
  setOpen,
  empresas = [],
  roles = [],
}) {
  const [methodName, setMethodName] = React.useState("Agregar");

  const initialData = {
    empresaId: "", // string
    rolId: "", // string
  };

  const [formData, setFormData] = React.useState(initialData);
  const [errors, setErrors] = React.useState({});

  // Si NO te pasan empresas/roles por props, las cargamos aquí (usando items)
  const [empresasLocal, setEmpresasLocal] = React.useState([]);
  const [rolesLocal, setRolesLocal] = React.useState([]);

  const empresasList = empresas?.length ? empresas : empresasLocal;
  const rolesList = roles?.length ? roles : rolesLocal;

  const isEdit = methodName === "Actualizar";

  // --------- Cargar items (si no vienen por props) ----------
  React.useEffect(() => {
    const loadItems = async () => {
      try {
        if (!empresas?.length) {
          const rEmp = await axios.get("/v1/items/empresa/0");
          setEmpresasLocal(Array.isArray(rEmp.data) ? rEmp.data : []);
        }
        if (!roles?.length) {
          const rRol = await axios.get("/v1/items/rol/0");
          setRolesLocal(Array.isArray(rRol.data) ? rRol.data : []);
        }
      } catch (err) {
        console.error(err);
        // no bloquea el form, pero avisamos
        setMessage?.({
          open: true,
          severity: "warning",
          text: "No se pudieron cargar empresas/roles (items).",
        });
      }
    };

    loadItems();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  // --------- Precargar datos al abrir ----------
  React.useEffect(() => {
    if (!open) return;

    if (selectedRow?.id) {
      // Intentar obtener ids desde el row o mapear por nombre
      const empresaFromRowName = empresasList.find((e) => {
        const nombre = e.name ?? e.nombre ?? e.empresaNombre;
        return nombre === selectedRow.empresaNombre;
      });

      const rolFromRowName = rolesList.find((r) => {
        const nombre = r.name ?? r.nombre ?? r.rolNombre;
        return nombre === selectedRow.rolNombre;
      });

      const empresaId =
        selectedRow.empresaId ??
        selectedRow.empresa?.id ??
        empresaFromRowName?.id ??
        "";

      const rolId =
        selectedRow.rolId ??
        selectedRow.rol?.id ??
        rolFromRowName?.id ??
        "";

      setFormData({
        empresaId: empresaId ? String(empresaId) : "",
        rolId: rolId ? String(rolId) : "",
      });

      setMethodName("Actualizar");
    } else {
      setFormData(initialData);
      setMethodName("Agregar");
    }

    setErrors({});
  }, [open, selectedRow, empresasList, rolesList]);

  const handleClose = () => {
    setOpen(false);
    setSelectedRow(null);
    setFormData(initialData);
    setErrors({});
  };

  // ✅ handlers explícitos (evita problemas de name/type)
  const handleEmpresaChange = (e) => {
    setFormData((p) => ({ ...p, empresaId: String(e.target.value) }));
    setErrors((p) => ({ ...p, empresaId: "" }));
  };

  const handleRolChange = (e) => {
    setFormData((p) => ({ ...p, rolId: String(e.target.value) }));
    setErrors((p) => ({ ...p, rolId: "" }));
  };

  const validate = () => {
    const e = {};
    if (!formData.empresaId) e.empresaId = "La empresa es obligatoria.";
    if (!formData.rolId) e.rolId = "El rol es obligatorio.";
    setErrors(e);
    return Object.keys(e).length === 0;
  };

  // ✅ Guardar (sin /api)
  const handleSubmit = async (ev) => {
    ev.preventDefault();
    if (!validate()) return;

    const creating = methodName === "Agregar";

    // Crear: empresaId + rolId
    // Editar: SOLO rolId (estado NO se cambia por PUT, se cambia por PATCH toggleEstado)
    const payload = creating
      ? {
          empresaId: Number(formData.empresaId),
          rolId: Number(formData.rolId),
        }
      : {
          rolId: Number(formData.rolId),
        };

    const url = creating
      ? "/v1/system/empresa-rol"
      : `/v1/system/empresa-rol/${selectedRow.id}`;

    try {
      await (creating ? axios.post : axios.put)(url, payload);

      setMessage({
        open: true,
        severity: "success",
        text: creating
          ? "Empresa-Rol creado correctamente"
          : "Empresa-Rol actualizado correctamente",
      });

      handleClose();
      reloadData();
    } catch (err) {
      console.error(err);
      setMessage({
        open: true,
        severity: "error",
        text:
          err?.response?.data?.message ||
          "Error al guardar el registro empresa-rol",
      });
    }
  };

// ✅ ELIMINAR = INACTIVAR (soft delete)
const deleteRow = async () => {
  if (!selectedRow?.id) {
    return setMessage({
      open: true,
      severity: "error",
      text: "Selecciona un registro para inactivar",
    });
  }

  // Detectar si está activo (según nombre o id)
  const estadoNombre = String(selectedRow?.estadoNombre ?? "").toLowerCase();
  const estadoId = selectedRow?.estadoId;

  const isActivo =
    estadoNombre === "activo" ||
    estadoNombre === "activa" ||
    estadoId === 1 ||
    estadoId === "1" ||
    estadoId === true;

  if (!isActivo) {
    return setMessage({
      open: true,
      severity: "info",
      text: "Este registro ya está INACTIVO.",
    });
  }

  if (!window.confirm(`¿Inactivar el registro con id "${selectedRow.id}"?`)) return;

  try {
    // PATCH toggleEstado (sin /api)
    await axios.patch(`/v1/system/empresa-rol/toggleEstado/${selectedRow.id}`, {});

    setMessage({
      open: true,
      severity: "success",
      text: "Registro inactivado correctamente",
    });

    // ✅ NO cierres el modal necesariamente; pero refrescamos la grilla
    reloadData();
  } catch (err) {
    console.error(err);
    setMessage({
      open: true,
      severity: "error",
      text: err?.response?.data?.message || "No se pudo inactivar el registro",
    });
  }
};

  // ✅ Cambiar estado: PATCH toggleEstado/{id} (sin body o con {} para evitar 400)
  const toggleEstado = async () => {
    if (!selectedRow?.id) {
      return setMessage({
        open: true,
        severity: "error",
        text: "Selecciona un registro para cambiar el estado",
      });
    }

    try {
      await axios.patch(
        `/v1/system/empresa-rol/toggleEstado/${selectedRow.id}`,
        {} // 🔥 body vacío (muchos backends requieren {} y fallan con null)
      );

      setMessage({
        open: true,
        severity: "success",
        text: "Estado alternado correctamente",
      });

      // refrescar data (y si el modal está abierto, refresca la grilla igualmente)
      reloadData();
    } catch (err) {
      console.error(err);
      setMessage({
        open: true,
        severity: "error",
        text:
          err?.response?.data?.message ||
          "No se pudo alternar el estado (toggleEstado)",
      });
    }
  };

  return (
    <>
      {/* ✅ Botonera superior */}
      <StackButtons
        methods={{
          create: () => {
            setMethodName("Agregar");
            setSelectedRow(null);
            setFormData(initialData);
            setErrors({});
            setOpen(true);
          },
          update: () => {
            if (!selectedRow?.id) {
              return setMessage({
                open: true,
                severity: "error",
                text: "Selecciona un registro",
              });
            }
            setMethodName("Actualizar");
            setErrors({});
            setOpen(true);
          },
          deleteRow,
          toggleEstado, // ✅ nuevo
        }}
      />

      {/* ✅ Modal */}
      <Dialog open={open} onClose={handleClose} fullWidth maxWidth="sm">
        <form onSubmit={handleSubmit}>
          <DialogTitle>{methodName} Empresa-Rol</DialogTitle>

          <DialogContent>
            <DialogContentText>
              Formulario para asignar roles a empresas
            </DialogContentText>

            {/* Empresa: en editar NO se cambia */}
            <FormControl fullWidth margin="dense" error={!!errors.empresaId}>
              <InputLabel id="empresaId-label">Empresa</InputLabel>
              <Select
                labelId="empresaId-label"
                label="Empresa"
                value={formData.empresaId}
                onChange={handleEmpresaChange}
                disabled={isEdit}
              >
                {empresasList.map((e) => (
                  <MenuItem key={e.id} value={String(e.id)}>
                    {e.name ?? e.nombre ?? e.empresaNombre ?? e.id}
                  </MenuItem>
                ))}
              </Select>
              <FormHelperText>{errors.empresaId}</FormHelperText>
            </FormControl>

            {/* Rol: editable */}
            <FormControl fullWidth margin="dense" error={!!errors.rolId}>
              <InputLabel id="rolId-label">Rol</InputLabel>
              <Select
                labelId="rolId-label"
                label="Rol"
                value={formData.rolId}
                onChange={handleRolChange}
              >
                {rolesList.map((r) => (
                  <MenuItem key={r.id} value={String(r.id)}>
                    {r.name ?? r.nombre ?? r.rolNombre ?? r.id}
                  </MenuItem>
                ))}
              </Select>
              <FormHelperText>{errors.rolId}</FormHelperText>
            </FormControl>

            {/* Estado: NO se edita por PUT, se cambia por toggleEstado */}
            {isEdit && (
              <Stack direction="row" justifyContent="space-between" alignItems="center" sx={{ mt: 2 }}>
                <Typography variant="body2" sx={{ opacity: 0.85 }}>
                  Estado actual: <b>{selectedRow?.estadoNombre ?? "—"}</b>
                </Typography>

                <Button variant="outlined" onClick={toggleEstado}>
                  Cambiar estado
                </Button>
              </Stack>
            )}
          </DialogContent>

          <DialogActions>
            <Button onClick={handleClose}>Cancelar</Button>
            <Button type="submit" variant="contained">
              {methodName}
            </Button>
          </DialogActions>
        </form>
      </Dialog>
    </>
  );
}

FormEmpresaRolSystem.propTypes = {
  selectedRow: PropTypes.object,
  setSelectedRow: PropTypes.func.isRequired,
  setMessage: PropTypes.func.isRequired,
  reloadData: PropTypes.func.isRequired,
  open: PropTypes.bool.isRequired,
  setOpen: PropTypes.func.isRequired,
  empresas: PropTypes.array,
  roles: PropTypes.array,
};
