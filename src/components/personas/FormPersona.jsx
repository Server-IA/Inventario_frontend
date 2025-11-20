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

export default function FormPersona({ selectedRow, setSelectedRow, setMessage, reloadData }) {
  const [open, setOpen] = React.useState(false);
  const [methodName, setMethodName] = React.useState("");

  const initialData = {
    tipoIdentificacion: "",
    identificacion: "",
    nombre: "",
    apellido: "",
    genero: "",
    fechaNacimiento: "",
    estrato: "",
    direccion: "",
    email: "",
    celular: "",
    estado: "1",
  };

  const [formData, setFormData] = React.useState(initialData);
  const [errors, setErrors] = React.useState({});
  const [tiposIdentificacion, setTiposIdentificacion] = React.useState([]);

  React.useEffect(() => {
    axios
      .get("/v1/items/tipo_identificacion/1")
      .then((res) => setTiposIdentificacion(res.data))
      .catch(() =>
        setMessage({
          open: true,
          severity: "error",
          text: "Error cargando tipos de identificación",
        })
      );
  }, [setMessage]);

  const create = () => {
    setFormData(initialData);
    setErrors({});
    setMethodName("Crear");
    setOpen(true);
  };

  const update = () => {
    if (!selectedRow?.id) {
      setMessage({
        open: true,
        severity: "error",
        text: "Selecciona una persona para editar.",
      });
      return;
    }

    const generoRaw = (selectedRow.genero || "").toString().toUpperCase();
    let generoValue = "";
    if (generoRaw.startsWith("M")) generoValue = "M";
    if (generoRaw.startsWith("F")) generoValue = "F";

    setFormData({
      tipoIdentificacion:
        selectedRow.tipoIdentificacion?.id || selectedRow.tipoIdentificacion || "",
      identificacion: selectedRow.identificacion || "",
      nombre: selectedRow.nombre || "",
      apellido: selectedRow.apellido || "",
      genero: generoValue,
      fechaNacimiento: selectedRow.fechaNacimiento || "",
      estrato: selectedRow.estrato?.toString() || "",
      direccion: selectedRow.direccion || "",
      email: selectedRow.email || "",
      celular: selectedRow.celular || "",
      estado: selectedRow.estado?.toString() || "1",
    });
    setErrors({});
    setMethodName("Actualizar");
    setOpen(true);
  };

  const deleteRow = () => {
    if (!selectedRow?.id) {
      setMessage({
        open: true,
        severity: "error",
        text: "Selecciona una persona para eliminar.",
      });
      return;
    }

    axios
      .delete(`/v1/persona/${selectedRow.id}`)
      .then(() => {
        setMessage({
          open: true,
          severity: "success",
          text: "Persona eliminada correctamente.",
        });
        setSelectedRow({});
        reloadData();
      })
      .catch((err) => {
        setMessage({
          open: true,
          severity: "error",
          text: `Error al eliminar: ${err.message}`,
        });
      });
  };

  const handleClose = () => setOpen(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
    setErrors((prev) => ({ ...prev, [name]: "" }));
  };

  // -------- Validaciones --------
  const validate = () => {
    const newErrors = {};

    // Tipo identificación (obligatorio)
    if (!formData.tipoIdentificacion) {
      newErrors.tipoIdentificacion = "Selecciona un tipo de identificación.";
    }

    // Identificación: obligatoria, sin caracteres especiales
    if (!formData.identificacion) {
      newErrors.identificacion = "La identificación es obligatoria.";
    } else if (!/^[0-9A-Za-z-]+$/.test(formData.identificacion.trim())) {
      newErrors.identificacion = "Solo se permiten letras, números y guiones.";
    }

    // Nombre: obligatorio, solo letras y espacios
    if (!formData.nombre) {
      newErrors.nombre = "El nombre es obligatorio.";
    } else if (!/^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ ]+$/.test(formData.nombre.trim())) {
      newErrors.nombre = "Solo se permiten letras y espacios.";
    }

    // Apellido: obligatorio, solo letras y espacios
    if (!formData.apellido) {
      newErrors.apellido = "El apellido es obligatorio.";
    } else if (!/^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ ]+$/.test(formData.apellido.trim())) {
      newErrors.apellido = "Solo se permiten letras y espacios.";
    }

    // Género: select M/F, obligatorio
    if (!formData.genero) {
      newErrors.genero = "Selecciona un género.";
    } else if (!["M", "F"].includes(formData.genero)) {
      newErrors.genero = "Valor de género inválido.";
    }

    // Fecha nacimiento: obligatoria y >= 18 años
    if (!formData.fechaNacimiento) {
      newErrors.fechaNacimiento = "La fecha de nacimiento es obligatoria.";
    } else {
      const fn = new Date(formData.fechaNacimiento);
      if (Number.isNaN(fn.getTime())) {
        newErrors.fechaNacimiento = "Fecha de nacimiento inválida.";
      } else {
        const hoy = new Date();
        const edad =
          hoy.getFullYear() -
          fn.getFullYear() -
          (hoy.getMonth() < fn.getMonth() ||
          (hoy.getMonth() === fn.getMonth() && hoy.getDate() < fn.getDate())
            ? 1
            : 0);
        if (edad < 18) {
          newErrors.fechaNacimiento = "La persona debe ser mayor de 18 años.";
        }
      }
    }

    // Estrato: select 1-10, obligatorio
    if (!formData.estrato) {
      newErrors.estrato = "El estrato es obligatorio.";
    } else {
      const estr = Number(formData.estrato);
      if (Number.isNaN(estr) || estr < 1 || estr > 10) {
        newErrors.estrato = "Estrato debe estar entre 1 y 10.";
      }
    }

    // Dirección: obligatoria, pero puede tener cualquier carácter
    if (!formData.direccion || !formData.direccion.trim()) {
      newErrors.direccion = "La dirección es obligatoria.";
    }

    // Email: obligatorio, formato válido
    if (!formData.email) {
      newErrors.email = "El email es obligatorio.";
    } else {
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!emailRegex.test(formData.email.trim())) {
        newErrors.email = "Correo electrónico inválido.";
      }
    }

    // Celular: obligatorio, solo números
    if (!formData.celular) {
      newErrors.celular = "El celular es obligatorio.";
    } else if (!/^[0-9]+$/.test(formData.celular.trim())) {
      newErrors.celular = "El celular solo puede contener números.";
    }

    // Estado: obligatorio
    if (!formData.estado) {
      newErrors.estado = "Selecciona un estado.";
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    if (!validate()) {
      setMessage({
        open: true,
        severity: "error",
        text: "Revisa los campos marcados en rojo.",
      });
      return;
    }

    const payload = {
      ...formData,
      tipoIdentificacion: parseInt(formData.tipoIdentificacion, 10),
      estado: parseInt(formData.estado, 10),
    };

    const method = methodName === "Crear" ? axios.post : axios.put;
    const url =
      methodName === "Crear" ? "/v1/persona" : `/v1/persona/${selectedRow.id}`;

    method(url, payload)
      .then(() => {
        setMessage({
          open: true,
          severity: "success",
          text: methodName === "Crear" ? "Persona creada!" : "Persona actualizada!",
        });
        setOpen(false);
        setSelectedRow({});
        reloadData();
      })
      .catch((err) => {
        setMessage({
          open: true,
          severity: "error",
          text: `Error: ${err.message}`,
        });
      });
  };

  return (
    <>
      <StackButtons methods={{ create, update, deleteRow }} />
      <Dialog open={open} onClose={handleClose}>
        <form onSubmit={handleSubmit} noValidate>
          <DialogTitle>{methodName} Persona</DialogTitle>
          <DialogContent>
            <DialogContentText>Formulario para Persona</DialogContentText>

            {/* Tipo identificación */}
            <FormControl
              fullWidth
              margin="normal"
              required
              error={Boolean(errors.tipoIdentificacion)}
            >
              <InputLabel>Tipo Identificación</InputLabel>
              <Select
                name="tipoIdentificacion"
                value={formData.tipoIdentificacion}
                onChange={handleChange}
                label="Tipo Identificación"
              >
                <MenuItem value="">Seleccione...</MenuItem>
                {tiposIdentificacion.map((tipo) => (
                  <MenuItem key={tipo.id} value={tipo.id}>
                    {tipo.name}
                  </MenuItem>
                ))}
              </Select>
              {errors.tipoIdentificacion && (
                <FormHelperText>{errors.tipoIdentificacion}</FormHelperText>
              )}
            </FormControl>

            {/* Identificación */}
            <TextField
              fullWidth
              margin="dense"
              required
              name="identificacion"
              label="Identificación"
              value={formData.identificacion}
              onChange={handleChange}
              error={Boolean(errors.identificacion)}
              helperText={errors.identificacion}
            />

            {/* Nombre */}
            <TextField
              fullWidth
              margin="dense"
              required
              name="nombre"
              label="Nombre"
              value={formData.nombre}
              onChange={handleChange}
              error={Boolean(errors.nombre)}
              helperText={errors.nombre}
            />

            {/* Apellido */}
            <TextField
              fullWidth
              margin="dense"
              required
              name="apellido"
              label="Apellido"
              value={formData.apellido}
              onChange={handleChange}
              error={Boolean(errors.apellido)}
              helperText={errors.apellido}
            />

            {/* Género: Select M / F */}
            <FormControl
              fullWidth
              margin="normal"
              required
              error={Boolean(errors.genero)}
            >
              <InputLabel>Género</InputLabel>
              <Select
                name="genero"
                value={formData.genero}
                onChange={handleChange}
                label="Género"
              >
                <MenuItem value="">Seleccione...</MenuItem>
                <MenuItem value="M">Masculino</MenuItem>
                <MenuItem value="F">Femenino</MenuItem>
              </Select>
              {errors.genero && (
                <FormHelperText>{errors.genero}</FormHelperText>
              )}
            </FormControl>

            {/* Fecha nacimiento */}
            <TextField
              fullWidth
              margin="dense"
              required
              type="date"
              name="fechaNacimiento"
              label="Fecha de Nacimiento"
              value={formData.fechaNacimiento}
              onChange={handleChange}
              InputLabelProps={{ shrink: true }}
              error={Boolean(errors.fechaNacimiento)}
              helperText={errors.fechaNacimiento}
            />

            {/* Estrato: select 1-10 */}
            <FormControl
              fullWidth
              margin="normal"
              required
              error={Boolean(errors.estrato)}
            >
              <InputLabel>Estrato</InputLabel>
              <Select
                name="estrato"
                value={formData.estrato}
                onChange={handleChange}
                label="Estrato"
              >
                <MenuItem value="">Seleccione...</MenuItem>
                {Array.from({ length: 10 }).map((_, i) => (
                  <MenuItem key={i + 1} value={(i + 1).toString()}>
                    {i + 1}
                  </MenuItem>
                ))}
              </Select>
              {errors.estrato && <FormHelperText>{errors.estrato}</FormHelperText>}
            </FormControl>

            {/* Dirección */}
            <TextField
              fullWidth
              margin="dense"
              required
              name="direccion"
              label="Dirección"
              value={formData.direccion}
              onChange={handleChange}
              error={Boolean(errors.direccion)}
              helperText={errors.direccion}
            />

            {/* Email */}
            <TextField
              fullWidth
              margin="dense"
              required
              name="email"
              label="Email personal"
              value={formData.email}
              onChange={handleChange}
              error={Boolean(errors.email)}
              helperText={errors.email}
            />

            {/* Celular */}
            <TextField
              fullWidth
              margin="dense"
              required
              name="celular"
              label="Celular"
              value={formData.celular}
              onChange={handleChange}
              error={Boolean(errors.celular)}
              helperText={errors.celular}
            />

            {/* Estado */}
            <FormControl
              fullWidth
              margin="normal"
              required
              error={Boolean(errors.estado)}
            >
              <InputLabel>Estado</InputLabel>
              <Select
                name="estado"
                value={formData.estado}
                onChange={handleChange}
                label="Estado"
              >
                <MenuItem value="1">Activo</MenuItem>
                <MenuItem value="2">Inactivo</MenuItem>
              </Select>
              {errors.estado && (
                <FormHelperText>{errors.estado}</FormHelperText>
              )}
            </FormControl>
          </DialogContent>
          <DialogActions>
            <Button onClick={handleClose}>Cancelar</Button>
            <Button type="submit">{methodName}</Button>
          </DialogActions>
        </form>
      </Dialog>
    </>
  );
}

FormPersona.propTypes = {
  selectedRow: PropTypes.object.isRequired,
  setSelectedRow: PropTypes.func.isRequired,
  setMessage: PropTypes.func.isRequired,
  reloadData: PropTypes.func.isRequired,
};
