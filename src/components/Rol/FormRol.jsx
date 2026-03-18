// src/components/rol/FormRol.jsx
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
  TextField,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  FormHelperText,
} from "@mui/material";
import StackButtons from "../StackButtons";

export default function FormRol({
  selectedRow,
  setSelectedRow,
  setMessage,
  reloadData,
  open,
  setOpen,
  estados = [],
}) {
  const [methodName, setMethodName] = React.useState("Agregar");

  const initialData = {
    nombre: "",
    descripcion: "",
    estadoId: 1, // por defecto Activo
  };

  const [formData, setFormData] = React.useState(initialData);
  const [errors, setErrors] = React.useState({});
  const [confirmOpen, setConfirmOpen] = React.useState(false);


  React.useEffect(() => {
    if (!open) return;

    if (selectedRow?.id) {
      // Modo actualizar
      setFormData({
        nombre: selectedRow.nombre || "",
        descripcion: selectedRow.descripcion || "",
        estadoId: selectedRow.estadoId ?? 1, // si no viene, Activo
      });
      setMethodName("Actualizar");
    } else {
      // Modo crear
      setFormData(initialData);
      setMethodName("Agregar");
    }
    setErrors({});
  }, [open, selectedRow]);

  const handleClose = () => {
    setOpen(false);
    setSelectedRow({});
    setFormData(initialData);
    setErrors({});
  };

  const handleChange = (e) => {
    const { name, value } = e.target;

    if (name === "nombre") {
      // Solo letras y como máximo un "_"
      let limpio = value.toUpperCase().replace(/[^A-Z_]/g, ""); // solo letras y _
      const firstUnderscore = limpio.indexOf("_");
      if (firstUnderscore !== -1) {
        // deja solo el primer "_" y quita el resto
        const before = limpio.slice(0, firstUnderscore + 1);
        const after = limpio.slice(firstUnderscore + 1).replace(/_/g, "");
        limpio = before + after;
      }
      setFormData((prev) => ({ ...prev, [name]: limpio }));
    } else {
      setFormData((prev) => ({ ...prev, [name]: value }));
    }

    setErrors((prev) => ({ ...prev, [name]: "" }));
  };

  const validate = () => {
    const e = {};
    // Letras + opcional un "_" en medio, sin empezar ni terminar con "_"
    const rolRegex = /^[A-Z]+(?:_[A-Z]+)?$/;

    // NOMBRE
    if (!formData.nombre.trim()) {
      e.nombre = "El nombre del rol es obligatorio.";
    } else if (!rolRegex.test(formData.nombre.trim())) {
      e.nombre =
        "Solo letras mayúsculas y un solo guion bajo en medio. Ej: ROLE_ADMIN.";
    }

    // DESCRIPCION
    if (!formData.descripcion.trim()) {
      e.descripcion = "La descripción es obligatoria.";
    }

    // SOLO en actualizar
    if (methodName === "Actualizar" && !formData.estadoId) {
      e.estadoId = "El estado es obligatorio.";
    }

    setErrors(e);
    return Object.keys(e).length === 0;
  };

  const handleSubmit = async (ev) => {
    ev.preventDefault();
    if (!validate()) return;

  const payload = {
    nombre: formData.nombre.trim(),
    descripcion: formData.descripcion.trim(),
    estadoId:
      methodName === "Actualizar"
        ? Number(formData.estadoId)
        : 1, 
  };
    const creating = methodName === "Agregar";
    const url = creating ? "/v1/roles" : `/v1/roles/${selectedRow.id}`;
    const req = creating ? axios.post : axios.put;

    try {
      await req(url, payload);
      setMessage({
        open: true,
        severity: "success",
        text: creating ? "Rol creado" : "Rol actualizado",
      });
      handleClose();
      reloadData();
    } catch (err) {
      setMessage({
        open: true,
        severity: "error",
        text: err?.response?.data?.message || "Error al guardar",
      });
    }
  };

const handleConfirmDelete = async () => {
  try {
    await axios.delete(`/v1/roles/${selectedRow.id}`);
    setMessage({
      open: true,
      severity: "success",
      text: "Rol eliminado",
    });
    setConfirmOpen(false);
    handleClose();
    reloadData();
  } catch {
    setMessage({
      open: true,
      severity: "error",
      text: "No se pudo eliminar el rol",
    });
    setConfirmOpen(false);
  }
};
const deleteRow = () => {
  if (!selectedRow?.id) {
    setMessage({
      open: true,
      severity: "error",
      text: "Selecciona un registro para eliminar",
    });
    return;
  }

  setConfirmOpen(true);
};
return (
  <>
    <StackButtons
      methods={{
        create: () => {
          setMethodName("Agregar");
          setSelectedRow({});
          setFormData(initialData);
          setErrors({});
          setOpen(true);
        },
        update: () => {
          if (!selectedRow?.id)
            return setMessage({
              open: true,
              severity: "error",
              text: "Selecciona un registro",
            });
          setMethodName("Actualizar");
          setErrors({});
          setOpen(true);
        },
        deleteRow,
      }}
    />

    {/*  Dialog principal */}
    <Dialog open={open} onClose={handleClose} fullWidth maxWidth="sm">
      <form onSubmit={handleSubmit}>
        <DialogTitle>{methodName} Rol</DialogTitle>

  <DialogContent>
  <DialogContentText>
    Formulario para gestionar roles
  </DialogContentText>

  <TextField
    fullWidth
    margin="dense"
    name="nombre"
    label="Nombre (ej: ROLE_ADMIN)"
    value={formData.nombre}
    onChange={handleChange}
    error={!!errors.nombre}
    helperText={errors.nombre}
  />

  <TextField
    fullWidth
    margin="dense"
    name="descripcion"
    label="Descripción"
    value={formData.descripcion}
    onChange={handleChange}
    error={!!errors.descripcion}
    helperText={errors.descripcion}
  />

  {methodName === "Actualizar" && (
    <TextField
      select
      fullWidth
      margin="dense"
      size="small"
      variant="outlined"
      label="Estado"
      name="estadoId"
      value={formData.estadoId ?? 1}
      onChange={handleChange}
      error={!!errors.estadoId}
      helperText={errors.estadoId}
    >
      {estados.map((e) => (
        <MenuItem key={e.id} value={e.id}>
          {e.nombre}
        </MenuItem>
      ))}
    </TextField>
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

    {/* Dialog de confirmación (AHORA SÍ BIEN UBICADO) */}
    <Dialog
      open={confirmOpen}
      onClose={() => setConfirmOpen(false)}
    >
      <DialogTitle>Confirmar eliminación</DialogTitle>
      <DialogContent>
        <DialogContentText>
          ¿Estás seguro de que deseas eliminar el rol "{selectedRow?.nombre}"?
        </DialogContentText>
      </DialogContent>

      <DialogActions>
        <Button onClick={() => setConfirmOpen(false)}>
          Cancelar
        </Button>
        <Button
          onClick={handleConfirmDelete}
          color="error"
          variant="contained"
        >
          Eliminar
        </Button>
      </DialogActions>
    </Dialog>
  </>
);
}

FormRol.propTypes = {
  selectedRow: PropTypes.object.isRequired,
  setSelectedRow: PropTypes.func.isRequired,
  setMessage: PropTypes.func.isRequired,
  reloadData: PropTypes.func.isRequired,
  open: PropTypes.bool.isRequired,
  setOpen: PropTypes.func.isRequired,
  estados: PropTypes.array,
};
