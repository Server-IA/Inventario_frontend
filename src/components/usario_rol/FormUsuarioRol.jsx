// src/components/usuarioRol/FormUsuarioRol.jsx
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

// Helpers para fechas
const toInputDateTime = (iso) => {
  if (!iso) return "";
  const d = new Date(iso);
  if (Number.isNaN(d.getTime())) return "";
  const pad = (n) => String(n).padStart(2, "0");
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(
    d.getHours()
  )}:${pad(d.getMinutes())}`;
};

const toIsoOrNull = (val) => {
  if (!val) return null;
  const d = new Date(val);
  if (Number.isNaN(d.getTime())) return null;
  return d.toISOString();
};

export default function FormUsuarioRol({
  selectedRow,
  setSelectedRow,
  setMessage,
  reloadData,
  open,
  setOpen,
  estados = [],
  usuarios = [],
  empresas = [],
  roles = [],
}) {
  const [methodName, setMethodName] = React.useState("Agregar");

  const initialData = {
    usuarioId: "",
    empresaId: "",
    rolId: "",
    estadoId: 1,
    iniciaContratoEn: "",
    finalizaContratoEn: "",
  };

  const [formData, setFormData] = React.useState(initialData);
  const [errors, setErrors] = React.useState({});

  React.useEffect(() => {
    if (!open) return;

    if (selectedRow?.id) {
      // Modo actualizar
      setFormData({
        usuarioId: selectedRow.usuarioId ?? "",
        empresaId: selectedRow.empresaId ?? "",
        rolId: selectedRow.rolId ?? "",
        estadoId: selectedRow.estadoId ?? 1,
        iniciaContratoEn: toInputDateTime(selectedRow.iniciaContratoEn),
        finalizaContratoEn: toInputDateTime(selectedRow.finalizaContratoEn),
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

    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));

    setErrors((prev) => ({ ...prev, [name]: "" }));
  };

  const validate = () => {
    const e = {};

    if (!formData.usuarioId) e.usuarioId = "El usuario es obligatorio.";
    if (!formData.empresaId) e.empresaId = "La empresa es obligatoria.";
    if (!formData.rolId) e.rolId = "El rol es obligatorio.";
    if (!formData.estadoId) e.estadoId = "El estado es obligatorio.";

    if (!formData.iniciaContratoEn) {
      e.iniciaContratoEn = "La fecha de inicio es obligatoria.";
    }
    if (!formData.finalizaContratoEn) {
      e.finalizaContratoEn = "La fecha de finalización es obligatoria.";
    }

    if (formData.iniciaContratoEn && formData.finalizaContratoEn) {
      const ini = new Date(formData.iniciaContratoEn);
      const fin = new Date(formData.finalizaContratoEn);
      if (!Number.isNaN(ini.getTime()) && !Number.isNaN(fin.getTime())) {
        if (fin < ini) {
          e.finalizaContratoEn =
            "La fecha de finalización debe ser mayor o igual a la fecha de inicio.";
        }
      }
    }

    setErrors(e);
    return Object.keys(e).length === 0;
  };

  const handleSubmit = async (ev) => {
    ev.preventDefault();
    if (!validate()) return;

    const payload = {
      usuarioId: Number(formData.usuarioId),
      empresaId: Number(formData.empresaId),
      rolId: Number(formData.rolId),
      estadoId: Number(formData.estadoId),
      iniciaContratoEn: toIsoOrNull(formData.iniciaContratoEn),
      finalizaContratoEn: toIsoOrNull(formData.finalizaContratoEn),
    };

    const creating = methodName === "Agregar";
    const url = creating
      ? "/v1/admin/usuario-roles"
      : `/v1/admin/usuario-roles/${selectedRow.id}`;
    const req = creating ? axios.post : axios.put;

    try {
      await req(url, payload);
      setMessage({
        open: true,
        severity: "success",
        text: creating
          ? "Usuario-Rol creado correctamente"
          : "Usuario-Rol actualizado correctamente",
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
          "Error al guardar el registro de usuario-rol",
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
        `¿Eliminar el registro de usuario-rol con id "${selectedRow.id}"?`
      )
    )
      return;

    try {
      await axios.delete(`/v1/admin/usuario-roles/${selectedRow.id}`);
      setMessage({
        open: true,
        severity: "success",
        text: "Usuario-Rol eliminado",
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

      <Dialog open={open} onClose={handleClose} fullWidth maxWidth="sm">
        <form onSubmit={handleSubmit}>
          <DialogTitle>{methodName} Usuario-Rol</DialogTitle>
          <DialogContent>
            <DialogContentText>
              Formulario para gestionar la asignación de roles a usuarios
            </DialogContentText>

            {/* Usuario */}
            {usuarios.length ? (
              <FormControl
                fullWidth
                margin="dense"
                error={!!errors.usuarioId}
              >
                <InputLabel id="usuarioId-label">Usuario</InputLabel>
                <Select
                  labelId="usuarioId-label"
                  label="Usuario"
                  name="usuarioId"
                  value={formData.usuarioId}
                  onChange={handleChange}
                >
                  {usuarios.map((u) => (
                    <MenuItem key={u.id} value={u.id}>
                      {u.email || u.nombre || u.usuarioEmail || u.id}
                    </MenuItem>
                  ))}
                </Select>
                <FormHelperText>{errors.usuarioId}</FormHelperText>
              </FormControl>
            ) : (
              <TextField
                fullWidth
                margin="dense"
                name="usuarioId"
                label="Usuario ID"
                type="number"
                value={formData.usuarioId}
                onChange={handleChange}
                error={!!errors.usuarioId}
                helperText={errors.usuarioId}
              />
            )}

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
                      {e.nombre || e.empresaNombre || e.id}
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
                      {r.nombre || r.rolNombre || r.id}
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

            {/* Estado */}
            <FormControl fullWidth margin="dense" error={!!errors.estadoId}>
              <InputLabel id="estadoId-label">Estado</InputLabel>
              <Select
                labelId="estadoId-label"
                label="Estado"
                name="estadoId"
                value={formData.estadoId ?? 1}
                onChange={handleChange}
              >
                {estados.map((e) => (
                  <MenuItem key={e.id} value={e.id}>
                    {e.nombre}
                  </MenuItem>
                ))}
              </Select>
              <FormHelperText>{errors.estadoId}</FormHelperText>
            </FormControl>

            {/* Fechas */}
            <TextField
              fullWidth
              margin="dense"
              name="iniciaContratoEn"
              label="Inicia contrato"
              type="datetime-local"
              InputLabelProps={{ shrink: true }}
              value={formData.iniciaContratoEn}
              onChange={handleChange}
              error={!!errors.iniciaContratoEn}
              helperText={errors.iniciaContratoEn}
            />

            <TextField
              fullWidth
              margin="dense"
              name="finalizaContratoEn"
              label="Finaliza contrato"
              type="datetime-local"
              InputLabelProps={{ shrink: true }}
              value={formData.finalizaContratoEn}
              onChange={handleChange}
              error={!!errors.finalizaContratoEn}
              helperText={errors.finalizaContratoEn}
            />
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

FormUsuarioRol.propTypes = {
  selectedRow: PropTypes.object.isRequired,
  setSelectedRow: PropTypes.func.isRequired,
  setMessage: PropTypes.func.isRequired,
  reloadData: PropTypes.func.isRequired,
  open: PropTypes.bool.isRequired,
  setOpen: PropTypes.func.isRequired,
  estados: PropTypes.array,
  usuarios: PropTypes.array,
  empresas: PropTypes.array,
  roles: PropTypes.array,
};
