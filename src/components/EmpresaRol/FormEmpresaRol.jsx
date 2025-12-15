// src/components/EmpresaRol/FormEmpresaRol.jsx
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

export default function FormEmpresaRol({
  selectedRow,
  setSelectedRow,
  setMessage,
  reloadData,
  open,
  setOpen,
  roles = [],
}) {
  const [methodName, setMethodName] = React.useState("Agregar");

  const initialData = { rolId: "" };
  const [formData, setFormData] = React.useState(initialData);
  const [errors, setErrors] = React.useState({});

  const isEdit = methodName === "Actualizar";

  // Precargar datos al abrir
  React.useEffect(() => {
    if (!open) return;

    if (selectedRow?.id) {
      const rolFromRowName = roles.find((r) => {
        const nombre = r.name ?? r.nombre ?? r.rolNombre;
        return nombre === selectedRow.rolNombre;
      });

      const rolId = selectedRow.rolId ?? selectedRow.rol?.id ?? rolFromRowName?.id ?? "";

      setFormData({ rolId: rolId ? String(rolId) : "" });
      setMethodName("Actualizar");
    } else {
      setFormData(initialData);
      setMethodName("Agregar");
    }

    setErrors({});
  }, [open, selectedRow, roles]);

  const handleClose = () => {
    setOpen(false);
    setSelectedRow(null);
    setFormData(initialData);
    setErrors({});
  };

  const handleRolChange = (e) => {
    setFormData((p) => ({ ...p, rolId: String(e.target.value) }));
    setErrors((p) => ({ ...p, rolId: "" }));
  };

  const validate = () => {
    const e = {};
    if (!formData.rolId) e.rolId = "El rol es obligatorio.";
    setErrors(e);
    return Object.keys(e).length === 0;
  };

  // ✅ Guardar (sin estado)
  const handleSubmit = async (ev) => {
    ev.preventDefault();
    if (!validate()) return;

    const creating = methodName === "Agregar";

    // Crear: SOLO rolId
    // Editar: SOLO rolId
    const payload = { rolId: Number(formData.rolId) };

    const url = creating ? "/v1/empresa-rol" : `/v1/empresa-rol/${selectedRow.id}`;

    try {
      await (creating ? axios.post : axios.put)(url, payload);

      setMessage({
        open: true,
        severity: "success",
        text: creating ? "Empresa-Rol creado correctamente" : "Empresa-Rol actualizado correctamente",
      });

      handleClose();
      reloadData();
    } catch (err) {
      console.error(err);
      setMessage({
        open: true,
        severity: "error",
        text: err?.response?.data?.message || "Error al guardar el registro empresa-rol",
      });
    }
  };

  // ✅ Inactivar/Activar = toggleEstado
  const toggleEstado = async () => {
    if (!selectedRow?.id) {
      return setMessage({
        open: true,
        severity: "error",
        text: "Selecciona un registro para cambiar el estado",
      });
    }

    try {
      await axios.patch(`/v1/empresa-rol/toggleEstado/${selectedRow.id}`, {});
      setMessage({ open: true, severity: "success", text: "Estado alternado correctamente" });
      reloadData();
    } catch (err) {
      console.error(err);
      setMessage({
        open: true,
        severity: "error",
        text: err?.response?.data?.message || "No se pudo alternar el estado (toggleEstado)",
      });
    }
  };

  // ✅ ELIMINAR = INACTIVAR (soft) usando toggleEstado, solo si está activo
  const deleteRow = async () => {
    if (!selectedRow?.id) {
      return setMessage({
        open: true,
        severity: "error",
        text: "Selecciona un registro para inactivar",
      });
    }

    const estadoNombre = String(selectedRow?.estadoNombre ?? "").toLowerCase();
    const estadoId = selectedRow?.estadoId;

    const isActivo =
      estadoNombre === "activo" ||
      estadoNombre === "activa" ||
      estadoId === 1 ||
      estadoId === "1" ||
      estadoId === true ||
      estadoId === 23 ||
      estadoId === "23";

    if (!isActivo) {
      return setMessage({
        open: true,
        severity: "info",
        text: "Este registro ya está INACTIVO.",
      });
    }

    if (!window.confirm(`¿Inactivar el registro con id "${selectedRow.id}"?`)) return;

    try {
      await axios.patch(`/v1/empresa-rol/toggleEstado/${selectedRow.id}`, {});
      setMessage({ open: true, severity: "success", text: "Registro inactivado correctamente" });
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

  return (
    <>
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
          deleteRow,      // ✅ inactivar
          toggleEstado,   // ✅ alternar
        }}
      />

      <Dialog open={open} onClose={handleClose} fullWidth maxWidth="sm">
        <form onSubmit={handleSubmit}>
          <DialogTitle>{methodName} Empresa-Rol</DialogTitle>

          <DialogContent>
            <DialogContentText>Formulario para asignar roles a la empresa (contexto actual).</DialogContentText>

            <FormControl fullWidth margin="dense" error={!!errors.rolId}>
              <InputLabel id="rolId-label">Rol</InputLabel>
              <Select
                labelId="rolId-label"
                label="Rol"
                value={formData.rolId}
                onChange={handleRolChange}
              >
                {roles.map((r) => (
                  <MenuItem key={r.id} value={String(r.id)}>
                    {r.name ?? r.nombre ?? r.rolNombre ?? r.id}
                  </MenuItem>
                ))}
              </Select>
              <FormHelperText>{errors.rolId}</FormHelperText>
            </FormControl>

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

FormEmpresaRol.propTypes = {
  selectedRow: PropTypes.object,
  setSelectedRow: PropTypes.func.isRequired,
  setMessage: PropTypes.func.isRequired,
  reloadData: PropTypes.func.isRequired,
  open: PropTypes.bool.isRequired,
  setOpen: PropTypes.func.isRequired,
  roles: PropTypes.array,
};
