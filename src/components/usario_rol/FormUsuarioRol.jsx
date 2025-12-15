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

// ===== Helpers fechas =====
const toInputDateTime = (iso) => {
  if (!iso) return "";
  const d = new Date(iso);
  if (Number.isNaN(d.getTime())) return "";
  const pad = (n) => String(n).padStart(2, "0");
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(
    d.getDate()
  )}T${pad(d.getHours())}:${pad(d.getMinutes())}`;
};

const toIsoOrNull = (val) => {
  if (!val) return null;
  const d = new Date(val);
  if (Number.isNaN(d.getTime())) return null;
  return d.toISOString();
};

// ===== Helpers items =====
const looksLikeEmail = (v) =>
  typeof v === "string" && v.includes("@") && v.includes(".");

const pickEmail = (obj) => {
  if (!obj || typeof obj !== "object") return "";
  const byKey =
    obj.email ??
    obj.usuarioEmail ??
    obj.correo ??
    obj.correoElectronico ??
    obj.usuario_email ??
    obj.mail ??
    "";
  if (looksLikeEmail(byKey)) return String(byKey).trim();
  const found = Object.values(obj).find((v) => looksLikeEmail(v));
  return found ? String(found).trim() : "";
};

const pickUsuarioEmpresa = (obj) => {
  if (!obj || typeof obj !== "object") return "";
  return String(
    obj.usuario_empresa ??
      obj.usuarioEmpresa ??
      obj.usuarioempresa ??
      obj.userEmpresa ??
      obj.nombre ??
      ""
  ).trim();
};

const pickRolId = (obj) => {
  const v = obj?.id ?? obj?.rolId ?? obj?.rol_id ?? obj?.codigo ?? null;
  if (v === null || v === undefined || v === "") return "";
  const n = Number(v);
  return Number.isNaN(n) ? "" : n;
};

const pickRolEmpresa = (obj) => {
  if (!obj || typeof obj !== "object") return "";
  return String(
    obj.rol_empresa ??
      obj.rolEmpresa ??
      obj.rolNombre ??
      obj.nombre ??
      obj.descripcion ??
      ""
  ).trim();
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
  roles = [],
}) {
  const [methodName, setMethodName] = React.useState("Agregar");

  const initialData = {
    usuarioEmail: "", // ✅ se envía al backend
    rolId: "", // ✅ id del rol (items rol_empresa)
    estadoId: 1,
    iniciaContratoEn: "",
    finalizaContratoEn: "",
  };

  const [formData, setFormData] = React.useState(initialData);
  const [errors, setErrors] = React.useState({});

  React.useEffect(() => {
    if (!open) return;

    if (selectedRow?.id) {
      setFormData({
        usuarioEmail: selectedRow.usuarioEmail ?? "",
        rolId: selectedRow.rolId ?? "",
        estadoId: selectedRow.estadoId ?? 1,
        iniciaContratoEn: toInputDateTime(selectedRow.iniciaContratoEn),
        finalizaContratoEn: toInputDateTime(selectedRow.finalizaContratoEn),
      });
      setMethodName("Actualizar");
    } else {
      setFormData(initialData);
      setMethodName("Agregar");
    }
    setErrors({});
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [open, selectedRow]);

  const handleClose = () => {
    setOpen(false);
    setSelectedRow({});
    setFormData(initialData);
    setErrors({});
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
    setErrors((prev) => ({ ...prev, [name]: "" }));
  };

  const validate = () => {
    const e = {};
    if (!formData.usuarioEmail) e.usuarioEmail = "El usuario es obligatorio.";
    if (!formData.rolId && formData.rolId !== 0) e.rolId = "El rol es obligatorio.";
    if (!formData.estadoId) e.estadoId = "El estado es obligatorio.";
    if (!formData.iniciaContratoEn) e.iniciaContratoEn = "La fecha de inicio es obligatoria.";
    if (!formData.finalizaContratoEn) e.finalizaContratoEn = "La fecha de finalización es obligatoria.";

    if (formData.iniciaContratoEn && formData.finalizaContratoEn) {
      const ini = new Date(formData.iniciaContratoEn);
      const fin = new Date(formData.finalizaContratoEn);
      if (!Number.isNaN(ini.getTime()) && !Number.isNaN(fin.getTime()) && fin < ini) {
        e.finalizaContratoEn =
          "La fecha de finalización debe ser mayor o igual a la fecha de inicio.";
      }
    }

    setErrors(e);
    return Object.keys(e).length === 0;
  };

  const handleSubmit = async (ev) => {
    ev.preventDefault();
    if (!validate()) return;

    const payload = {
      usuarioEmail: String(formData.usuarioEmail).trim(), // ✅ correo
      rolId: Number(formData.rolId), // ✅ id rol
      estadoId: Number(formData.estadoId),
      iniciaContratoEn: toIsoOrNull(formData.iniciaContratoEn),
      finalizaContratoEn: toIsoOrNull(formData.finalizaContratoEn),
    };

    const creating = methodName === "Agregar";
    const url = creating ? "v1/usuario-roles" : `v1/usuario-roles/${selectedRow.id}`;
    const req = creating ? axios.post : axios.put;

    try {
      await req(url, payload);
      setMessage({
        open: true,
        severity: "success",
        text: creating ? "Usuario-Rol creado correctamente" : "Usuario-Rol actualizado correctamente",
      });
      handleClose();
      reloadData();
    } catch (err) {
      console.error(err);
      setMessage({
        open: true,
        severity: "error",
        text: err?.response?.data?.message || "Error al guardar el registro de usuario-rol",
      });
    }
  };

  const deleteRow = async () => {
    if (!selectedRow?.id) {
      setMessage({ open: true, severity: "error", text: "Selecciona un registro para eliminar" });
      return;
    }
    if (!window.confirm(`¿Eliminar el registro de usuario-rol con id "${selectedRow.id}"?`)) return;

    try {
      await axios.delete(`v1/usuario-roles/${selectedRow.id}`);
      setMessage({ open: true, severity: "success", text: "Usuario-Rol eliminado" });
      handleClose();
      reloadData();
    } catch (err) {
      console.error(err);
      setMessage({ open: true, severity: "error", text: "No se pudo eliminar el registro" });
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
              return setMessage({ open: true, severity: "error", text: "Selecciona un registro" });
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

            {/* ✅ Usuario: items usuario_empresa (mostrar usuario_empresa, enviar correo) */}
            <FormControl fullWidth margin="dense" error={!!errors.usuarioEmail}>
              <InputLabel id="usuarioEmail-label">Usuario</InputLabel>
              <Select
                labelId="usuarioEmail-label"
                label="Usuario"
                name="usuarioEmail"
                value={formData.usuarioEmail}
                onChange={handleChange}
              >
                {(Array.isArray(usuarios) ? usuarios : []).map((u) => {
                  const email = pickEmail(u);
                  const usuarioEmpresa = pickUsuarioEmpresa(u);
                  return (
                    <MenuItem key={email || u.id} value={email}>
                      <div style={{ display: "flex", flexDirection: "column" }}>
                        <span style={{ fontWeight: 700 }}>
                          {usuarioEmpresa || "Sin nombre"}
                        </span>
                        <span style={{ fontSize: 12, opacity: 0.7 }}>
                          {email}
                        </span>
                      </div>
                    </MenuItem>
                  );
                })}
              </Select>
              <FormHelperText>{errors.usuarioEmail}</FormHelperText>
            </FormControl>

            {/* ✅ Rol: items rol_empresa (mostrar rol_empresa, enviar rolId) */}
            <FormControl fullWidth margin="dense" error={!!errors.rolId}>
              <InputLabel id="rolId-label">Rol</InputLabel>
              <Select
                labelId="rolId-label"
                label="Rol"
                name="rolId"
                value={formData.rolId}
                onChange={handleChange}
              >
                {(Array.isArray(roles) ? roles : []).map((r) => {
                  const id = pickRolId(r);
                  const rolEmpresa = pickRolEmpresa(r);
                  return (
                    <MenuItem key={id || r.id} value={id}>
                      {rolEmpresa || id}
                    </MenuItem>
                  );
                })}
              </Select>
              <FormHelperText>{errors.rolId}</FormHelperText>
            </FormControl>

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
  roles: PropTypes.array,
};
