import * as React from 'react';
import {
  Button, TextField, MenuItem,
  Typography, Box, Container, useTheme
} from '@mui/material';
import FormRegistroEmpresa from "../seguridad/FormRegistroEmpresa";
import { Grid, Alert } from '@mui/material';
import axios from "../axiosConfig";
import { validateCamposBase } from "../utils/validations";

export default function FormRegistroPersona(props) {
  const theme = useTheme();

  const [error, setError] = React.useState('');
  const [success, setSuccess] = React.useState('');
  const [fieldErrors, setFieldErrors] = React.useState({});

  // catálogos tipo identificación
  const [tiposIdent, setTiposIdent] = React.useState([]);
  const [loadingTipos, setLoadingTipos] = React.useState(false);

  React.useEffect(() => {
    let mounted = true;
    setLoadingTipos(true);
    axios
      .get('/v1/items/tipo_identificacion/0')
      .then(res => {
        const data = Array.isArray(res.data)
          ? res.data
          : Array.isArray(res.data?.content)
          ? res.data.content
          : [];
        if (mounted) setTiposIdent(data);
      })
      .catch(() => mounted && setTiposIdent([]))
      .finally(() => mounted && setLoadingTipos(false));
    return () => { mounted = false; };
  }, []);

  // ===== VALIDACIONES =====
  const normalize = (v = "") => v.replace(/\s+/g, " ").trim();
  const isEmail = (v="") => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v);
  const isDigits = (v="") => /^\d+$/.test(v);
  const isPhoneDigits = (v="") => /^\d{7,15}$/.test(v); // sólo números, 7–15
  const onlyLettersSpaces = (v="") => /^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ\s]+$/.test(v); // sin _, -, +, *, ~, ?
  const getAge = (iso) => {
    const d = new Date(iso);
    if (Number.isNaN(d.getTime())) return -1;
    const today = new Date();
    let age = today.getFullYear() - d.getFullYear();
    const m = today.getMonth() - d.getMonth();
    if (m < 0 || (m === 0 && today.getDate() < d.getDate())) age--;
    return age;
  };
  const direccionRegex = /^(Calle|Carrera|Cra\.?|Transversal|Tv\.?|Avenida|Av\.?|Circular)\s+\d+[A-Za-z]?\s*#\s*\d{1,3}\s*-\s*\d{1,3}$/i;

  const isCedulaCiudadania = (tipoId) => {
    const found = tiposIdent.find(it => String(it.id ?? it.code) === String(tipoId));
    const label = String(found?.nombre ?? found?.name ?? found?.descripcion ?? "").toLowerCase();
    return /c[eé]dula/.test(label) && /ciudadan/.test(label);
  };

  const validateAll = (data) => {
    const e = {};

    // 1) Validación CENTRAL (seguridad / XSS)
    const baseErrors = validateCamposBase({
      nombre: data.nombre ?? "",
      descripcion: "N/A",
      estado: 1,
    });
    if (baseErrors.nombre) e.nombre = baseErrors.nombre;
    if (baseErrors._security) e._security = baseErrors._security;

    // 2) Validaciones requeridas por negocio
    const nombre = normalize(data.nombre || "");
    const apellido = normalize(data.apellido || "");
    const email = normalize(data.email || "");
    const tipoIdentificacion = String(data.tipoIdentificacion || "");
    const identificacion = String(data.identificacion || "");
    const genero = String(data.genero || "").toLowerCase();
    const fechaNacimiento = String(data.fechaNacimiento || "");
    const estrato = Number(data.estrato);
    const direccion = normalize(data.direccion || "");
    const celular = normalize(data.celular || "");

    if (!nombre) e.nombre = e.nombre || "El nombre es obligatorio.";
    else if (!onlyLettersSpaces(nombre)) e.nombre = "Sólo letras y espacios.";

    if (!apellido) e.apellido = "El apellido es obligatorio.";
    else if (!onlyLettersSpaces(apellido)) e.apellido = "Sólo letras y espacios.";

    if (!email) e.email = "El correo es obligatorio.";
    else if (!isEmail(email)) e.email = "Correo no válido.";

    if (!tipoIdentificacion) e.tipoIdentificacion = "Seleccione el tipo de identificación.";

    if (!identificacion) e.identificacion = "La identificación es obligatoria.";
    else if (isCedulaCiudadania(tipoIdentificacion) && !isDigits(identificacion)) {
      e.identificacion = "Para Cédula de ciudadanía, la identificación debe ser numérica.";
    }

    if (!genero) e.genero = "Seleccione el género.";
    else if (!["m","f"].includes(genero)) e.genero = "Género no válido.";

    if (!fechaNacimiento) {
      e.fechaNacimiento = "La fecha de nacimiento es obligatoria.";
    } else {
      const d = new Date(fechaNacimiento);
      if (Number.isNaN(d.getTime())) e.fechaNacimiento = "Fecha no válida.";
      else {
        const today = new Date(); today.setHours(0,0,0,0);
        if (d > today) e.fechaNacimiento = "La fecha no puede ser futura.";
        const age = getAge(fechaNacimiento);
        if (isCedulaCiudadania(tipoIdentificacion) && age >= 0 && age < 18) {
          e.fechaNacimiento = "Para Cédula de ciudadanía debe tener al menos 18 años.";
        }
      }
    }

    if (!estrato) e.estrato = "Seleccione el estrato.";
    else if (!Number.isInteger(estrato) || estrato < 1 || estrato > 6) e.estrato = "El estrato debe estar entre 1 y 6.";

    if (!direccion) e.direccion = "La dirección es obligatoria.";
    else if (!direccionRegex.test(direccion)) e.direccion = "Formato inválido. Ej: Calle 1c #23-60";

    if (!celular) e.celular = "El celular es obligatorio.";
    else if (!isPhoneDigits(celular)) e.celular = "Sólo números (7–15 dígitos).";

    return e;
  };

  const handleSubmit = (event) => {
    event.preventDefault();

    // 1) recolecta datos del form (sin volver controlados)
    const formData = new FormData(event.currentTarget);
    const formJson = Object.fromEntries(formData.entries());

    // 2) valida TODO
    const e = validateAll(formJson);
    setFieldErrors(e);

    // 3) si hay errores: BLOQUEA envío
    if (Object.keys(e).length > 0) {
      setError(e._security || "Por favor corrige los campos marcados.");
      setSuccess("");
      return; // importante para NO llamar axios
    }

    // 4) transformaciones finales
    formJson.tipoIdentificacion = parseInt(formJson.tipoIdentificacion);
    formJson.estrato = parseInt(formJson.estrato);
    formJson.estado = 1;
    formJson.genero = String(formJson.genero || "").toLowerCase();

    const token = localStorage.getItem('token');
    const url = import.meta.env.VITE_BACKEND_URI + '/api/v1/personas/persona-usuario';

    axios.post(url, formJson, { headers: { Authorization: `Bearer ${token}` } })
      .then((response) => {
        setFieldErrors({});
        setError('');
        setSuccess('Persona creada con éxito');
        if (response.data.usuarioEstado === 3) {
          props.setCurrentModule(
            <FormRegistroEmpresa
              setCurrentModule={props.setCurrentModule}
              personaId={response.data.id}
            />
          );
        }
      })
      .catch((err) => {
        const message = err.response?.data?.message || 'No se pudo crear la persona.';
        setError(message);
        setSuccess('');
      });
  };

  // helpers de input (evitan caracteres no válidos sin volver controlados)
  const onlyLettersOnInput = (e) => {
    e.target.value = e.target.value.replace(/[^A-Za-zÁÉÍÓÚÜÑáéíóúüñ\s]/g, "");
  };
  const onlyDigitsOnInput = (e) => {
    e.target.value = e.target.value.replace(/\D/g, "");
  };

  return (
    <Container
      maxWidth={false}
      disableGutters
      sx={{ display:'flex', alignItems:'center', justifyContent:'center', minHeight:'100vh',
            backgroundColor: theme.palette.background.default, padding:3 }}
    >
      <Box sx={{
        display:'flex', flexDirection:'column', gap:3, p:{ xs:3, sm:4 },
        bgcolor: theme.palette.background.paper, borderRadius:4, boxShadow:6,
        width:'100%', maxWidth:990,
      }}>
        {error && <Typography color="error" variant="body2">{error}</Typography>}
        {success && <Typography color="success.main" variant="body2">{success}</Typography>}

        <form onSubmit={handleSubmit}>
          <Typography variant="h5" component="h2" gutterBottom sx={{ fontWeight: 700 }}>
            Formulario Persona
          </Typography>

          {!!error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
          {!!success && <Alert severity="success" sx={{ mb: 2 }}>{success}</Alert>}

          <Grid container spacing={3}>
            <Grid item xs={12} md={6}>
              <TextField
                required id="nombre" name="nombre" label="Nombre" fullWidth variant="outlined" size="medium"
                placeholder="Ej: María" InputLabelProps={{ shrink: true }}
                defaultValue={props.selectedRow?.nombre || ''}
                error={!!fieldErrors.nombre} helperText={fieldErrors.nombre}
                inputProps={{ pattern: "[A-Za-zÁÉÍÓÚÜÑáéíóúüñ\\s]+", title: "Sólo letras y espacios" }}
                onInput={onlyLettersOnInput}
              />
            </Grid>

            <Grid item xs={12} md={6}>
              <TextField
                required id="apellido" name="apellido" label="Apellido" fullWidth variant="outlined" size="medium"
                placeholder="Ej: Murillo" InputLabelProps={{ shrink: true }}
                defaultValue={props.selectedRow?.apellido || ''}
                error={!!fieldErrors.apellido} helperText={fieldErrors.apellido}
                inputProps={{ pattern: "[A-Za-zÁÉÍÓÚÜÑáéíóúüñ\\s]+", title: "Sólo letras y espacios" }}
                onInput={onlyLettersOnInput}
              />
            </Grid>

            <Grid item xs={12} md={6}>
              <TextField
                required id="email" name="email" label="Correo electrónico" type="email"
                fullWidth variant="outlined" size="medium" placeholder="nombre@dominio.com"
                InputLabelProps={{ shrink: true }}
                defaultValue={props.selectedRow?.email || ''}
                error={!!fieldErrors.email} helperText={fieldErrors.email}
              />
            </Grid>

            <Grid item xs={12} md={6}>
              <TextField
                select fullWidth id="tipoIdentificacion" name="tipoIdentificacion" label="Tipo de Identificación"
                variant="outlined" size="medium" InputLabelProps={{ shrink: true }}
                defaultValue={props.selectedRow?.tipoIdentificacion ?? ''}
                error={!!fieldErrors.tipoIdentificacion} helperText={fieldErrors.tipoIdentificacion}
              >
                <MenuItem value="" disabled>{loadingTipos ? 'Cargando...' : 'Seleccione'}</MenuItem>
                {tiposIdent.map((it) => {
                  const value = it.id ?? it.code ?? '';
                  const label = it.nombre ?? it.name ?? it.descripcion ?? value;
                  return <MenuItem key={value} value={value}>{label}</MenuItem>;
                })}
              </TextField>
            </Grid>

            <Grid item xs={12} md={6}>
              <TextField
                required id="identificacion" name="identificacion" label="Número de Identificación"
                fullWidth variant="outlined" size="medium" placeholder="Ej: 1234567890"
                InputLabelProps={{ shrink: true }}
                defaultValue={props.selectedRow?.identificacion || ''}
                error={!!fieldErrors.identificacion} helperText={fieldErrors.identificacion}
                inputProps={{ inputMode: "numeric", pattern: "\\d+", title: "Sólo números" }}
                onInput={onlyDigitsOnInput}
              />
            </Grid>

            <Grid item xs={12} md={6}>
              <TextField
                select fullWidth id="genero" name="genero" label="Género" variant="outlined" size="medium"
                InputLabelProps={{ shrink: true }} defaultValue={props.selectedRow?.genero ?? ''}
                error={!!fieldErrors.genero} helperText={fieldErrors.genero}
              >
                <MenuItem value="" disabled>Seleccione</MenuItem>
                <MenuItem value="m">Masculino</MenuItem>
                <MenuItem value="f">Femenino</MenuItem>
              </TextField>
            </Grid>

            <Grid item xs={12} md={6}>
              <TextField
                required id="fechaNacimiento" name="fechaNacimiento" label="Fecha de Nacimiento" type="date"
                fullWidth variant="outlined" size="medium" InputLabelProps={{ shrink: true }}
                inputProps={{ max: new Date().toISOString().slice(0,10) }}
                defaultValue={props.selectedRow?.fechaNacimiento || ''}
                error={!!fieldErrors.fechaNacimiento} helperText={fieldErrors.fechaNacimiento}
              />
            </Grid>

            <Grid item xs={12} md={6}>
              <TextField
                select required id="estrato" name="estrato" label="Estrato" fullWidth variant="outlined" size="medium"
                InputLabelProps={{ shrink: true }} defaultValue={props.selectedRow?.estrato ?? ''}
                error={!!fieldErrors.estrato} helperText={fieldErrors.estrato}
              >
                <MenuItem value="" disabled>Seleccione</MenuItem>
                {[1,2,3,4,5,6].map((n) => (<MenuItem key={n} value={n}>{n}</MenuItem>))}
              </TextField>
            </Grid>

            <Grid item xs={12} md={6}>
              <TextField
                required id="direccion" name="direccion" label="Dirección" fullWidth variant="outlined" size="medium"
                placeholder="Calle 1c #23-60" InputLabelProps={{ shrink: true }}
                defaultValue={props.selectedRow?.direccion || ''}
                error={!!fieldErrors.direccion} helperText={fieldErrors.direccion}
                inputProps={{
                  pattern: "(Calle|Carrera|Cra\\.?|Transversal|Tv\\.?|Avenida|Av\\.?|Circular)\\s+\\d+[A-Za-z]?\\s*#\\s*\\d{1,3}\\s*-\\s*\\d{1,3}",
                  title: "Ej: Calle 1c #23-60",
                }}
              />
            </Grid>

            <Grid item xs={12} md={6}>
              <TextField
                required id="celular" name="celular" label="Celular" fullWidth variant="outlined" size="medium"
                placeholder="3001234567" InputLabelProps={{ shrink: true }}
                defaultValue={props.selectedRow?.celular || ''}
                error={!!fieldErrors.celular} helperText={fieldErrors.celular}
                inputProps={{ inputMode: "numeric", pattern: "\\d{7,15}", title: "Sólo números (7–15 dígitos)" }}
                onInput={onlyDigitsOnInput}
              />
            </Grid>
          </Grid>

          <Box sx={{ mt: 3 }}>
            <Button type="submit" variant="contained" color="primary" fullWidth sx={{ py: 1.25, fontWeight: 700 }}>
              GUARDAR
            </Button>
          </Box>
        </form>
      </Box>
    </Container>
  );
}
