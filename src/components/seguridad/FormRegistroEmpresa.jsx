import Contenido from '../dashboard/Contenido.jsx';
import * as React from 'react';
import {
  Button, TextField, FormControl, InputLabel, MenuItem, Select,
  Typography, Container, Box, useTheme, Grid, Alert
} from '@mui/material';
import { SiteProps } from '../dashboard/SiteProps.jsx';
import axios from '../axiosConfig.js';
import { useNavigate } from 'react-router-dom';
import { validateCamposBase } from "../utils/validations";

// helper para leer estado desde un JWT si te lo devuelven
const decodeJwt = (jwt) => {
  try {
    const [, payload] = jwt.split(".");
    const json = atob(payload.replace(/-/g, "+").replace(/_/g, "/"));
    return JSON.parse(json);
  } catch {
    return {};
  }
};

// ===== Helpers de validación =====
const normalize = (v = "") => v.replace(/\s+/g, " ").trim();
const onlyLettersSpacesRx = /^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ\s]+$/;
const isEmail = (v="") => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v);
const isPhoneDigits = (v="") => /^\d{7,15}$/.test(v);

// Heurísticas por nombre del tipo de identificación
const isNitTipo = (tipo, items=[]) => {
  const it = items.find(x => String(x.id ?? x.code) === String(tipo));
  const label = String(it?.nombre ?? it?.name ?? it?.descripcion ?? "").toLowerCase();
  return /nit/.test(label);
};
const isCedulaTipo = (tipo, items=[]) => {
  const it = items.find(x => String(x.id ?? x.code) === String(tipo));
  const label = String(it?.nombre ?? it?.name ?? it?.descripcion ?? "").toLowerCase();
  return /c[eé]dula/.test(label);
};

// DV NIT (DIAN): pesa 71,67,59,53,47,43,41,37,29,23,19,17,13,7,3, según longitud
const nitDV = (nitSinDv) => {
  const pesos = [71,67,59,53,47,43,41,37,29,23,19,17,13,7,3];
  const s = String(nitSinDv).replace(/\D/g,"");
  let sum = 0;
  let j = pesos.length - s.length;
  if (j < 0) j = 0;
  for (let i = 0; i < s.length && j < pesos.length; i++, j++) {
    sum += Number(s[i]) * pesos[j];
  }
  const mod = sum % 11;
  return (mod > 1) ? 11 - mod : mod;
};

export default function FormRegistroEmpresa(props) {
  const url = import.meta.env.VITE_BACKEND_URI + '/api/v1/empresas/empresa-usuario';
  const theme = useTheme();
  const navigate = useNavigate();

  const [error, setError] = React.useState('');
  const [success, setSuccess] = React.useState('');
  const [fieldErrors, setFieldErrors] = React.useState({});

  // ---- Tipos de identificación desde backend ----
  const [tiposIdent, setTiposIdent] = React.useState([]);
  const [loadingTipos, setLoadingTipos] = React.useState(false);

  React.useEffect(() => {
    let mounted = true;
    setLoadingTipos(true);
    axios
      .get('/v1/items/tipo_identificacion/0')
      .then((res) => {
        const data = Array.isArray(res.data)
          ? res.data
          : Array.isArray(res.data?.content)
          ? res.data.content
          : [];
        if (mounted) setTiposIdent(data);
      })
      .catch((e) => {
        console.error('Error cargando tipos de identificación', e);
        if (mounted) setTiposIdent([]);
      })
      .finally(() => mounted && setLoadingTipos(false));
    return () => { mounted = false; };
  }, []);

  // ===== Validación integral (usa validateCamposBase + reglas de negocio) =====
  const validateAll = (raw) => {
    const e = {};

    // Central (seguridad/XSS) — mapeo mínimo
    const base = validateCamposBase({
      nombre: raw.nombre ?? "",
      descripcion: raw.descripcion ?? "",
      estado: raw.estadoId ?? 1,
    });
    if (base.nombre) e.nombre = base.nombre;
    if (base.descripcion) e.descripcion = base.descripcion;
    if (base._security) e._security = base._security;

    // Campos negocio
    const nombre = normalize(raw.nombre || "");
    const contacto = normalize(raw.contacto || "");
    const correo = normalize(raw.correo || "");
    const celular = normalize(raw.celular || "");
    const descripcion = normalize(raw.descripcion || "");
    const tipoIdentificacionId = String(raw.tipoIdentificacionId || "");
    const identificacion = String(raw.identificacion || "").trim();
    const estadoId = Number(raw.estadoId);

    if (!nombre) e.nombre = e.nombre || "El nombre de la empresa es obligatorio.";

    if (!contacto) e.contacto = "El nombre de encargado es obligatorio.";
    else if (!onlyLettersSpacesRx.test(contacto)) e.contacto = "Sólo letras y espacios.";

    if (!correo) e.correo = "El correo es obligatorio.";
    else if (!isEmail(correo)) e.correo = "Correo no válido.";

    if (!celular) e.celular = "El celular es obligatorio.";
    else if (!isPhoneDigits(celular)) e.celular = "Sólo números (7–15 dígitos).";

    if (!descripcion) e.descripcion = e.descripcion || "La descripción es obligatoria.";

    if (!tipoIdentificacionId) e.tipoIdentificacionId = "Seleccione el tipo de identificación.";
    // Identificación por tipo
    if (!identificacion) {
      e.identificacion = "La identificación es obligatoria.";
    } else if (isNitTipo(tipoIdentificacionId, tiposIdent)) {
      // NIT: 7–12 dígitos + DV opcional "-D"
      const m = identificacion.match(/^(\d{7,12})(?:-(\d))?$/);
      if (!m) {
        e.identificacion = "NIT inválido. Formato: 7–12 dígitos y DV opcional (ej. 900123456-7).";
      } else if (m[2] !== undefined) {
        const dv = nitDV(m[1]);
        if (dv !== Number(m[2])) {
          e.identificacion = `DV inválido para el NIT. Debe ser ${dv}.`;
        }
      }
    } else if (isCedulaTipo(tipoIdentificacionId, tiposIdent)) {
      if (!/^\d+$/.test(identificacion)) e.identificacion = "Para Cédula, sólo números.";
    }

    if (![1,2].includes(estadoId)) e.estadoId = "Debe seleccionar un estado válido.";

    return e;
  };

  // limita en tiempo real sin volver controlados
  const onlyLettersOnInput = (e) => {
    e.target.value = e.target.value.replace(/[^A-Za-zÁÉÍÓÚÜÑáéíóúüñ\s]/g, "");
  };
  const onlyDigitsOnInput = (e) => {
    e.target.value = e.target.value.replace(/\D/g, "");
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    const formData = new FormData(event.currentTarget);
    const formJson = Object.fromEntries(formData.entries());

    // valida todo
    const e = validateAll(formJson);
    setFieldErrors(e);
    if (Object.keys(e).length > 0) {
      setError(e._security || "Corrige los campos marcados.");
      setSuccess('');
      return; // ⛔️ NO enviar si hay errores
    }

    // transformaciones
    formJson.tipoIdentificacionId = parseInt(formJson.tipoIdentificacionId);
    formJson.estadoId = parseInt(formJson.estadoId);
    formJson.personaId = props.personaId;

    const token = localStorage.getItem('token');

    try {
      const response = await axios.post(url, formJson, {
        headers: { Authorization: `Bearer ${token}` },
      });

      setError('');
      setSuccess('Empresa creada con éxito');

      const { usuarioEstado, estado, token: newToken } = response.data || {};
      if (newToken) {
        const { exp } = decodeJwt(newToken);
        const expiration = exp ? exp * 1000 : Date.now() + 3 * 60 * 60 * 1000;
        localStorage.setItem('token', newToken);
        localStorage.setItem('token_expiration', String(expiration));
      }

      const estadoJwt = decodeJwt(newToken || token)?.estado;
      const candidatos = [estado, usuarioEstado, estadoJwt];
      const primeroValido = candidatos.find(v => Number.isFinite(Number(v)));
      const nextEstado = typeof primeroValido !== 'undefined' ? Number(primeroValido) : 4;

      if (nextEstado === 4) {
        localStorage.removeItem('activeModule');
        props.setIsAuthenticated?.(true);
        navigate('/coagronet/', { replace: true });
        return;
      }

      localStorage.setItem('activeModule', 'form_registro_empresa');
      props.setIsAuthenticated?.(false);
    } catch (e) {
      console.error('Error al crear la empresa:', e);
      const message = e.response?.data?.message || 'No se pudo crear la empresa.';
      setError(message);
      setSuccess('');
    }
  };

  return (
    <Container
      maxWidth={false}
      disableGutters
      sx={{
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        minHeight: '100vh',
        bgcolor: theme.palette.background.default,
        p: 3,
      }}
    >
      <Box
        sx={{
          display: 'flex',
          flexDirection: 'column',
          gap: 3,
          p: { xs: 3, sm: 4 },
          bgcolor: theme.palette.background.paper,
          borderRadius: 4,
          boxShadow: 6,
          width: '100%',
          maxWidth: 990,
        }}
      >
        {!!error && <Alert severity="error">{error}</Alert>}
        {!!success && <Alert severity="success">{success}</Alert>}

        <form onSubmit={handleSubmit}>
          <Typography variant="h5" component="h2" gutterBottom sx={{ fontWeight: 700 }}>
            Formulario Empresa
          </Typography>

          <Grid container spacing={3}>
            <Grid item xs={12} md={6}>
              <TextField
                required
                id="nombre"
                name="nombre"
                label="Nombre de la Empresa"
                fullWidth
                variant="outlined"
                placeholder="Ej: Inversiones ABC S.A.S."
                InputLabelProps={{ shrink: true }}
                defaultValue={props.selectedRow?.nombre || ''}
                error={!!fieldErrors.nombre}
                helperText={fieldErrors.nombre}
              />
            </Grid>

            <Grid item xs={12} md={6}>
              <TextField
                required
                id="contacto"
                name="contacto"
                label="Nombre de Encargado"
                fullWidth
                variant="outlined"
                placeholder="Ej: María Pérez"
                InputLabelProps={{ shrink: true }}
                defaultValue={props.selectedRow?.contacto || ''}
                error={!!fieldErrors.contacto}
                helperText={fieldErrors.contacto}
                inputProps={{ pattern: "[A-Za-zÁÉÍÓÚÜÑáéíóúüñ\\s]+", title: "Sólo letras y espacios" }}
                onInput={onlyLettersOnInput}
              />
            </Grid>

            <Grid item xs={12} md={6}>
              <TextField
                required
                id="correo"
                name="correo"
                type="email"
                label="Correo"
                fullWidth
                variant="outlined"
                placeholder="empresa@dominio.com"
                InputLabelProps={{ shrink: true }}
                defaultValue={props.selectedRow?.correo || ''}
                error={!!fieldErrors.correo}
                helperText={fieldErrors.correo}
              />
            </Grid>

            <Grid item xs={12} md={6}>
              <TextField
                required
                id="celular"
                name="celular"
                label="Celular"
                fullWidth
                variant="outlined"
                placeholder="3001234567"
                InputLabelProps={{ shrink: true }}
                defaultValue={props.selectedRow?.celular || ''}
                error={!!fieldErrors.celular}
                helperText={fieldErrors.celular}
                inputProps={{ inputMode: "numeric", pattern: "\\d{7,15}", title: "Sólo números (7–15 dígitos)" }}
                onInput={onlyDigitsOnInput}
              />
            </Grid>

            <Grid item xs={12}>
              <TextField
                required
                id="descripcion"
                name="descripcion"
                label="Descripción"
                fullWidth
                variant="outlined"
                multiline
                minRows={2}
                placeholder="Breve descripción de la empresa"
                InputLabelProps={{ shrink: true }}
                defaultValue={props.selectedRow?.descripcion || ''}
                error={!!fieldErrors.descripcion}
                helperText={fieldErrors.descripcion}
              />
            </Grid>

            <Grid item xs={12} md={6}>
              <TextField
                select
                required
                fullWidth
                id="tipoIdentificacionId"
                name="tipoIdentificacionId"
                label="Tipo de Identificación"
                variant="outlined"
                InputLabelProps={{ shrink: true }}
                defaultValue={props.selectedRow?.tipoIdentificacionId ?? ''}
                error={!!fieldErrors.tipoIdentificacionId}
                helperText={fieldErrors.tipoIdentificacionId}
              >
                <MenuItem value="" disabled>
                  {loadingTipos ? 'Cargando...' : 'Seleccione'}
                </MenuItem>
                {tiposIdent.map((it) => {
                  const value = it.id ?? it.code ?? '';
                  const label = it.nombre ?? it.name ?? it.descripcion ?? value;
                  return (
                    <MenuItem key={value} value={value}>
                      {label}
                    </MenuItem>
                  );
                })}
              </TextField>
            </Grid>

            <Grid item xs={12} md={6}>
              <TextField
                required
                id="identificacion"
                name="identificacion"
                label="Número de Identificación"
                fullWidth
                variant="outlined"
                placeholder="Ej: 900123456-7"
                InputLabelProps={{ shrink: true }}
                defaultValue={props.selectedRow?.identificacion || ''}
                error={!!fieldErrors.identificacion}
                helperText={fieldErrors.identificacion}
              />
            </Grid>

            <Grid item xs={12} md={6}>
              <TextField
                select
                required
                fullWidth
                id="estadoId"
                name="estadoId"
                label="Estado"
                variant="outlined"
                InputLabelProps={{ shrink: true }}
                defaultValue={props.selectedRow?.estadoId || 1}
                error={!!fieldErrors.estadoId}
                helperText={fieldErrors.estadoId}
              >
                <MenuItem value={1}>Activo</MenuItem>
                <MenuItem value={2}>Inactivo</MenuItem>
              </TextField>
            </Grid>
          </Grid>

          <Box sx={{ mt: 3 }}>
            <Button type="submit" variant="contained" color="primary" fullWidth sx={{ py: 1.25, fontWeight: 700 }}>
              Guardar Empresa
            </Button>
          </Box>
        </form>
      </Box>
    </Container>
  );
}
