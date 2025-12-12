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
  TextField,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  FormHelperText,
} from "@mui/material";
import StackButtons from "../StackButtons";

export default function FormEmpresaRol({
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
    empresaId: "",
    rolId: "",
  };

  const [formData, setFormData] = React.useState(initialData);
  const [errors, setErrors] = React.useState({});

  // --------- Carga de datos al abrir (crear / actualizar) ----------
  React.useEffect(() => {
    if (!open) return;

    if (selectedRow?.id) {
      // Intentar obtener los IDs a partir del row y de los combos
      const empresaFromRowName = empresas.find((e) => {
        const nombre = e.name ?? e.nombre ?? e.empresaNombre;
        return nombre === selectedRow.empresaNombre;
      });

      const rolFromRowName = roles.find((r) => {
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
        empresaId,
        rolId,
      });
      setMethodName("Actualizar");
    } else {
      setFormData(initialData);
      setMethodName("Agregar");
    }
    setErrors({});
  }, [open, selectedRow, empresas, roles]);

  const handleClose = () => {
    setOpen(false);
    setSelectedRow(null);
    setFormData(initialData);
    setErrors({});
  };

  const handleChange = (e) => {
    const { name, value } = e.target;

    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));

    setErrors((prev) => ({ ...prev, [name]: "" }));
  };

  const validate = () => {
    const e = {};

    if (!formData.empresaId) e.empresaId = "La empresa es obligatoria.";
    if (!formData.rolId) e.rolId = "El rol es obligatorio.";

    setErrors(e);
    return Object.keys(e).length === 0;
  };

  const handleSubmit = async (ev) => {
    ev.preventDefault();
    if (!validate()) return;

    const payload = {
      empresaId: Number(formData.empresaId),
      rolId: Number(formData.rolId),
    };

    const creating = methodName === "Agregar";
    const url = creating
      ? "/v1/system/empresa-rol"
      : `/v1/system/empresa-rol/${selectedRow.id}`;
    const req = creating ? axios.post : axios.put;

    try {
      await req(url, payload);
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

  const deleteRow = async () => {
    if (!selectedRow?.id) {
      setMessage({
        open: true,
        severity: "error",
        text: "Selecciona un registro para eliminar",
      });
      return;
    }
    if (
      !window.confirm(
        `¿Eliminar el registro empresa-rol con id "${selectedRow.id}"?`
      )
    )
      return;

    try {
      await axios.delete(`/v1/system/empresa-rol/${selectedRow.id}`);
      setMessage({
        open: true,
        severity: "success",
        text: "Empresa-Rol eliminado",
      });
      handleClose();
      reloadData();
    } catch (err) {
      console.error(err);
      setMessage({
        open: true,
        severity: "error",
        text: "No se pudo eliminar el registro",
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

      <Dialog open={open} onClose={handleClose} fullWidth maxWidth="sm">
        <form onSubmit={handleSubmit}>
          <DialogTitle>{methodName} Empresa-Rol</DialogTitle>
          <DialogContent>
            <DialogContentText>
              Formulario para asignar roles a empresas
            </DialogContentText>

            {/* Empresa */}
            {empresas.length ? (
              <FormControl
                fullWidth
                margin="dense"
                error={!!errors.empresaId}
              >
                <InputLabel id="empresaId-label">Empresa</InputLabel>
                <Select
                  labelId="empresaId-label"
                  label="Empresa"
                  name="empresaId"
                  value={formData.empresaId}
                  onChange={handleChange}
                >
                  {empresas.map((e) => (
                    <MenuItem key={e.id} value={e.id}>
                      {e.name ?? e.nombre ?? e.empresaNombre ?? e.id}
                    </MenuItem>
                  ))}
                </Select>
                <FormHelperText>{errors.empresaId}</FormHelperText>
              </FormControl>
            ) : (
              <TextField
                fullWidth
                margin="dense"
                name="empresaId"
                label="Empresa ID"
                type="number"
                value={formData.empresaId}
                onChange={handleChange}
                error={!!errors.empresaId}
                helperText={errors.empresaId}
              />
            )}

            {/* Rol */}
            {roles.length ? (
              <FormControl fullWidth margin="dense" error={!!errors.rolId}>
                <InputLabel id="rolId-label">Rol</InputLabel>
                <Select
                  labelId="rolId-label"
                  label="Rol"
                  name="rolId"
                  value={formData.rolId}
                  onChange={handleChange}
                >
                  {roles.map((r) => (
                    <MenuItem key={r.id} value={r.id}>
                      {r.name ?? r.nombre ?? r.rolNombre ?? r.id}
                    </MenuItem>
                  ))}
                </Select>
                <FormHelperText>{errors.rolId}</FormHelperText>
              </FormControl>
            ) : (
              <TextField
                fullWidth
                margin="dense"
                name="rolId"
                label="Rol ID"
                type="number"
                value={formData.rolId}
                onChange={handleChange}
                error={!!errors.rolId}
                helperText={errors.rolId}
              />
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
  empresas: PropTypes.array,
  roles: PropTypes.array,
};
