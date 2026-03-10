import React, { useState } from "react";
import {
  Dialog, DialogTitle, DialogContent, DialogActions,
  TextField, Button, FormControl, InputLabel, Select, MenuItem,
  Grid, Typography, Divider, InputAdornment, FormHelperText, CircularProgress
} from "@mui/material";
// Iconos
import PersonIcon from '@mui/icons-material/Person';
import BadgeIcon from '@mui/icons-material/Badge';
import ToggleOnIcon from '@mui/icons-material/ToggleOn';
import EmailIcon from '@mui/icons-material/Email';

import axios from "../axiosConfig";
import StackButtons from "../StackButtons";

export default function FormUsuarioRol({ selectedRow, setSelectedRow, setMessage, reloadData }) {
  const [open, setOpen] = useState(false);
  const [methodName, setMethodName] = useState("");
  const [submitting, setSubmitting] = useState(false);

  // --- ESTADOS PARA LISTAS ---
  const [listaUsuarios, setListaUsuarios] = useState([]);
  const [listaRoles, setListaRoles] = useState([]);
  const [loadingCatalogs, setLoadingCatalogs] = useState(false);

  // Estado del Formulario
  const [formData, setFormData] = useState({
    usuarioId: "",
    personaNombreCompleto: "",
    usuarioEmail: "",
    rolId: "", 
    estadoId: 1,      
    iniciaContratoEn: "",
    finalizaContratoEn: ""
  });

  const [errors, setErrors] = useState({});
  const token = localStorage.getItem("token");
  const headers = { headers: { Authorization: `Bearer ${token}` } };

  // --- CARGA DE CATÁLOGOS ---
  const loadCatalogs = async () => {
    try {
      setLoadingCatalogs(true);
      const [usersRes, rolesRes] = await Promise.all([
        axios.get("/v1/items/usuario_empresa/0", headers),
        axios.get("/v1/items/empresa_rol/0", headers)
      ]);
      setListaUsuarios(usersRes.data || []);
      setListaRoles(rolesRes.data || []);
    } catch (error) {
      console.error("Error cargando catálogos:", error);
      setMessage({ open: true, severity: "error", text: "Error de conexión al cargar listas." });
    } finally {
      setLoadingCatalogs(false);
    }
  };

  // --- Helpers ---
  const resetForm = () => {
    setFormData({
      usuarioId: "",
      personaNombreCompleto: "",
      usuarioEmail: "",
      rolId: "",
      estadoId: 1,
      iniciaContratoEn: "",
      finalizaContratoEn: ""
    });
    setErrors({});
  };

  const formatDateForInput = (isoString) => {
    if (!isoString) return "";
    return isoString.substring(0, 10);
  };

  // --- Acciones ---
  const create = () => {
    resetForm();
    setMethodName("Crear");
    loadCatalogs();
    setOpen(true);
  };

  const update = () => {
    if (!selectedRow?.id) {
      setMessage({ open: true, severity: "error", text: "Selecciona un registro para editar." });
      return;
    }
    setMethodName("Editar");
    setErrors({});
    loadCatalogs();

    // CORRECCIÓN DE LECTURA: El backend envía "rolID" pero el form usa "rolId"
    // Leemos cualquiera de los dos para asegurar que el Select se llene.
    const rolValue = selectedRow.rolID || selectedRow.rolId || "";

    setFormData({
      usuarioId: selectedRow.usuarioId || "",
      personaNombreCompleto: selectedRow.personaNombreCompleto || "",
      usuarioEmail: selectedRow.usuarioEmail || "",
      rolId: rolValue, 
      estadoId: selectedRow.estadoId || 1,
      iniciaContratoEn: formatDateForInput(selectedRow.iniciaContratoEn),
      finalizaContratoEn: formatDateForInput(selectedRow.finalizaContratoEn),
    });
    setOpen(true);
  };

  const deleteRow = async () => {
    if (!selectedRow?.id) return;
    if (!window.confirm("¿Eliminar este registro permanentemente?")) return;

    try {
      await axios.delete(`/v1/usuario-roles/${selectedRow.id}`, headers);
      setMessage({ open: true, severity: "success", text: "Registro eliminado." });
      setSelectedRow({});
      reloadData();
    } catch (err) {
      setMessage({ open: true, severity: "error", text: "Error al eliminar." });
    }
  };

  // --- Handlers ---
  const handleUserChange = (e) => {
    const selectedId = e.target.value;
    const usuario = listaUsuarios.find(u => u.id === selectedId);
    setFormData(prev => ({
      ...prev,
      usuarioId: selectedId,
      personaNombreCompleto: usuario ? usuario.name : "",
    }));
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  // --- SUBMIT ---
  const handleSubmit = async (e) => {
    e.preventDefault();
    
    // Validaciones
    const newErrors = {};
    if (!formData.usuarioId) newErrors.usuarioId = "Seleccione un usuario";
    if (!formData.rolId) newErrors.rolId = "Seleccione un rol";
    if (!formData.iniciaContratoEn) newErrors.iniciaContratoEn = "Fecha requerida";

    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors);
      return;
    }

    // Preparar Payload LIMPIO (Igual a Postman)
    const payload = {
      usuarioId: Number(formData.usuarioId),
      // CORRECCIÓN PAYLOAD: Usamos "rolId" (minúscula) porque así funciona tu POSTMAN
      rolId: Number(formData.rolId), 
      estadoId: Number(formData.estadoId),
      // Fechas con Z para UTC
      iniciaContratoEn: formData.iniciaContratoEn ? `${formData.iniciaContratoEn}T00:00:00Z` : null,
      finalizaContratoEn: formData.finalizaContratoEn ? `${formData.finalizaContratoEn}T00:00:00Z` : null,
    };

    // CORRECCIÓN UPDATE: Agregar el ID dentro del JSON para evitar error de restricciones
    if (methodName === "Editar") {
      payload.id = selectedRow.id;
    }

    try {
      setSubmitting(true);
      const url = methodName === "Crear" ? "/v1/usuario-roles" : `/v1/usuario-roles/${selectedRow.id}`;
      const method = methodName === "Crear" ? axios.post : axios.put;

      console.log("Enviando Payload:", payload); // Debug en consola

      await method(url, payload, headers);

      setMessage({ open: true, severity: "success", text: "Operación exitosa." });
      setOpen(false);
      setSelectedRow({});
      reloadData();
    } catch (err) {
      console.error("Error submit:", err);
      const backendMsg = err.response?.data?.message;
      
      if (backendMsg && (backendMsg.includes("restricciones") || backendMsg.includes("constraint"))) {
        setMessage({ open: true, severity: "error", text: "Error: Datos duplicados o inválidos (Restricción BD)." });
      } else {
        setMessage({ open: true, severity: "error", text: backendMsg ? `Error: ${backendMsg}` : "Error al guardar." });
      }
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <>
      <StackButtons methods={{ create, update, deleteRow }} />

      <Dialog open={open} onClose={() => setOpen(false)} maxWidth="md" fullWidth>
        <form onSubmit={handleSubmit}>
          <DialogTitle sx={{ bgcolor: '#1976d2', color: 'white', display: 'flex', gap: 1 }}>
            <BadgeIcon /> {methodName} Usuario Rol
          </DialogTitle>
          
          <DialogContent dividers sx={{ p: 3 }}>
            <Grid container spacing={3}>
              
              {/* USUARIO */}
              <Grid item xs={12}>
                <Typography variant="subtitle2" color="primary">USUARIO</Typography>
                <Divider sx={{ mb: 1 }} />
              </Grid>

              <Grid item xs={12} sm={6}>
                <FormControl fullWidth error={!!errors.usuarioId}>
                  <InputLabel id="user-select">Usuario</InputLabel>
                  <Select
                    labelId="user-select"
                    name="usuarioId"
                    value={formData.usuarioId}
                    label="Usuario"
                    onChange={handleUserChange}
                    startAdornment={loadingCatalogs ? <CircularProgress size={20} sx={{mr:1}}/> : <InputAdornment position="start"><PersonIcon/></InputAdornment>}
                  >
                    <MenuItem value=""><em>Seleccione...</em></MenuItem>
                    {listaUsuarios.map((u) => (
                      <MenuItem key={u.id} value={u.id}>{u.name}</MenuItem>
                    ))}
                  </Select>
                  {!!errors.usuarioId && <FormHelperText>{errors.usuarioId}</FormHelperText>}
                </FormControl>
              </Grid>

              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Email (Manual)"
                  name="usuarioEmail"
                  value={formData.usuarioEmail}
                  onChange={handleChange}
                  InputProps={{ startAdornment: <InputAdornment position="start"><EmailIcon/></InputAdornment> }}
                />
              </Grid>

              <Grid item xs={12}>
                 <TextField
                    fullWidth disabled label="Nombre Seleccionado"
                    value={formData.personaNombreCompleto} variant="filled" size="small"
                 />
              </Grid>

              {/* ROL Y ESTADO */}
              <Grid item xs={12} sx={{ mt: 1 }}>
                <Typography variant="subtitle2" color="primary">CONFIGURACIÓN</Typography>
                <Divider sx={{ mb: 1 }} />
              </Grid>

              <Grid item xs={12} sm={6}>
                <FormControl fullWidth error={!!errors.rolId}>
                  <InputLabel id="rol-select">Rol</InputLabel>
                  <Select
                    labelId="rol-select"
                    name="rolId"
                    value={formData.rolId}
                    label="Rol"
                    onChange={handleChange}
                    startAdornment={loadingCatalogs ? <CircularProgress size={20} sx={{mr:1}}/> : <InputAdornment position="start"><BadgeIcon/></InputAdornment>}
                  >
                    <MenuItem value=""><em>Seleccione...</em></MenuItem>
                    {listaRoles.map((r) => (
                      <MenuItem key={r.id} value={r.id}>{r.name}</MenuItem>
                    ))}
                  </Select>
                  {!!errors.rolId && <FormHelperText>{errors.rolId}</FormHelperText>}
                </FormControl>
              </Grid>

              <Grid item xs={12} sm={6}>
                <FormControl fullWidth>
                  <InputLabel>Estado</InputLabel>
                  <Select
                    name="estadoId"
                    value={formData.estadoId}
                    label="Estado"
                    onChange={handleChange}
                    startAdornment={<InputAdornment position="start"><ToggleOnIcon/></InputAdornment>}
                  >
                    <MenuItem value={1}>Activo</MenuItem>
                    <MenuItem value={2}>Inactivo</MenuItem>
                  </Select>
                </FormControl>
              </Grid>

              {/* CONTRATO */}
              <Grid item xs={12} sx={{ mt: 1 }}>
                <Typography variant="subtitle2" color="primary">CONTRATO</Typography>
                <Divider sx={{ mb: 1 }} />
              </Grid>

              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth label="Inicio Contrato" name="iniciaContratoEn" type="date"
                  InputLabelProps={{ shrink: true }}
                  value={formData.iniciaContratoEn} onChange={handleChange}
                  error={!!errors.iniciaContratoEn} helperText={errors.iniciaContratoEn}
                />
              </Grid>

              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth label="Fin Contrato" name="finalizaContratoEn" type="date"
                  InputLabelProps={{ shrink: true }}
                  value={formData.finalizaContratoEn} onChange={handleChange}
                />
              </Grid>

            </Grid>
          </DialogContent>
          <DialogActions sx={{ p: 2, bgcolor: '#f5f5f5' }}>
            <Button onClick={() => setOpen(false)}>Cancelar</Button>
            <Button type="submit" variant="contained" disabled={submitting}>
              {submitting ? "Guardando..." : "Guardar"}
            </Button>
          </DialogActions>
        </form>
      </Dialog>
    </>
  );
}