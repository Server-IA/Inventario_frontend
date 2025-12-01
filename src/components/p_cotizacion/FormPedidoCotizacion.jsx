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

// Estados fijos: activo / inactivo
const estados = [
  { id: 1, nombre: "Activo" },
  { id: 2, nombre: "Inactivo" },
];

export default function FormPedidoCotizacion({
  selectedRow,
  setSelectedRow,
  setMessage,
  reloadData,
  open,
  setOpen,
  pedidos = [],
  proveedores = [],
}) {
  const [methodName, setMethodName] = React.useState("Agregar");

  const initialData = {
    descripcion: "",
    archivo: "",
    pedidoId: "",
    proveedorId: "",
    estadoId: 1, // 1 = Activo por defecto
  };

  const [formData, setFormData] = React.useState(initialData);
  const [errors, setErrors] = React.useState({});

  React.useEffect(() => {
    if (!open) return;

    if (selectedRow?.id) {
      setFormData({
        descripcion: selectedRow.descripcion || "",
        archivo: selectedRow.archivo || "",
        pedidoId: selectedRow.pedidoId ?? selectedRow.pedido?.id ?? "",
        proveedorId:
          selectedRow.proveedorId ?? selectedRow.proveedor?.id ?? "",
        estadoId: selectedRow.estadoId ?? selectedRow.estado?.id ?? 1,
      });
      setMethodName("Actualizar");
    } else {
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

    if (!formData.descripcion.trim()) {
      e.descripcion = "La descripción es obligatoria.";
    }

    // archivo es texto (nombre o base64), máximo 2048 caracteres
    if (formData.archivo && formData.archivo.length > 2048) {
      e.archivo = "El campo archivo no puede superar 2048 caracteres.";
    }

    if (!formData.pedidoId) {
      e.pedidoId = "El pedido es obligatorio.";
    }
    if (!formData.proveedorId) {
      e.proveedorId = "El proveedor es obligatorio.";
    }
    if (!formData.estadoId) {
      e.estadoId = "El estado es obligatorio.";
    }

    setErrors(e);
    return Object.keys(e).length === 0;
  };

  const handleSubmit = async (ev) => {
    ev.preventDefault();
    if (!validate()) return;

    const payload = {
      descripcion: formData.descripcion.trim(),
      archivo: formData.archivo || null,
      pedidoId: Number(formData.pedidoId),
      proveedorId: Number(formData.proveedorId),
      estadoId: Number(formData.estadoId),
    };

    const creating = methodName === "Agregar";
    const url = creating
      ? "/v1/pedido-cotizacion"
      : `/v1/pedido-cotizacion/${selectedRow.id}`;
    const req = creating ? axios.post : axios.put;

    try {
      await req(url, payload);

      // === Cambiar estado del pedido a "requiere-cotizacion" al CREAR ===
      if (creating && payload.pedidoId) {
        try {
          await axios.put(
            `/v1/pedido/${payload.pedidoId}/requiere-cotizacion`
          );
        } catch (errEstado) {
          console.error("Error cambiando estado del pedido:", errEstado);
          setMessage({
            open: true,
            severity: "warning",
            text:
              errEstado?.response?.data?.message ||
              "La cotización se creó, pero no se pudo cambiar el estado del pedido a 'Requiere cotización'.",
          });
        }
      }

      setMessage({
        open: true,
        severity: "success",
        text: creating
          ? "Pedido cotización creado"
          : "Pedido cotización actualizado",
      });
      handleClose();
      reloadData();
    } catch (err) {
      console.error(err);
      setMessage({
        open: true,
        severity: "error",
        text: err?.response?.data?.message || "Error al guardar",
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
    if (!window.confirm(`¿Eliminar la cotización "${selectedRow.descripcion}"?`))
      return;

    // Obtenemos el pedido asociado ANTES de borrar
    const pedidoId =
      selectedRow.pedidoId ?? selectedRow.pedido?.id ?? null;

    try {
      await axios.delete(`/v1/pedido-cotizacion/${selectedRow.id}`);

      // === Devolver el pedido a estado ACTIVO (18) si me equivoqué de pedido ===
      if (pedidoId) {
        try {
          // Ajusta esta URL al endpoint real que tengas para pasar el pedido a estado activo (18)
          await axios.put(`/v1/pedido/${pedidoId}/activo`);
        } catch (errEstado) {
          console.error(
            "Error devolviendo el pedido a estado activo:",
            errEstado
          );
          setMessage({
            open: true,
            severity: "warning",
            text:
              errEstado?.response?.data?.message ||
              "Se eliminó la cotización, pero no se pudo devolver el pedido a estado activo.",
          });
        }
      }

      setMessage({
        open: true,
        severity: "success",
        text: "Eliminado",
      });
      handleClose();
      reloadData();
    } catch (err) {
      console.error(err);
      setMessage({
        open: true,
        severity: "error",
        text: "No se pudo eliminar",
      });
    }
  };

  return (
    <>
      <StackButtons
        methods={{
          create: () => {
            setMethodName("Agregar");
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
          <DialogTitle>{methodName} Pedido Cotización</DialogTitle>
          <DialogContent>
            <DialogContentText>
              Formulario para gestionar las cotizaciones de un pedido
            </DialogContentText>

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

            <TextField
              fullWidth
              margin="dense"
              name="archivo"
              label="Archivo (nombre o base64, máx. 2048 caracteres)"
              value={formData.archivo}
              onChange={handleChange}
              error={!!errors.archivo}
              helperText={errors.archivo}
            />

            {/* Pedido */}
            <FormControl fullWidth margin="dense" error={!!errors.pedidoId}>
              <InputLabel id="pedidoId-label">Pedido</InputLabel>
              <Select
                labelId="pedidoId-label"
                label="Pedido"
                name="pedidoId"
                value={formData.pedidoId}
                onChange={handleChange}
              >
                {pedidos.map((p) => (
                  <MenuItem key={p.id} value={p.id}>
                    {p.nombre || p.descripcion || `Pedido ${p.id}`}
                  </MenuItem>
                ))}
              </Select>
              <FormHelperText>{errors.pedidoId}</FormHelperText>
            </FormControl>

            {/* Proveedor */}
            <FormControl
              fullWidth
              margin="dense"
              error={!!errors.proveedorId}
            >
              <InputLabel id="proveedorId-label">Proveedor</InputLabel>
              <Select
                labelId="proveedorId-label"
                label="Proveedor"
                name="proveedorId"
                value={formData.proveedorId}
                onChange={handleChange}
              >
                {proveedores.map((p) => (
                  <MenuItem key={p.id} value={p.id}>
                    {p.nombre || p.razonSocial || `Proveedor ${p.id}`}
                  </MenuItem>
                ))}
              </Select>
              <FormHelperText>{errors.proveedorId}</FormHelperText>
            </FormControl>

            {/* Estado (Activo / Inactivo) */}
            <FormControl fullWidth margin="dense" error={!!errors.estadoId}>
              <InputLabel id="estadoId-label">Estado</InputLabel>
              <Select
                labelId="estadoId-label"
                label="Estado"
                name="estadoId"
                value={formData.estadoId}
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

FormPedidoCotizacion.propTypes = {
  selectedRow: PropTypes.object.isRequired,
  setSelectedRow: PropTypes.func.isRequired,
  setMessage: PropTypes.func.isRequired,
  reloadData: PropTypes.func.isRequired,
  open: PropTypes.bool.isRequired,
  setOpen: PropTypes.func.isRequired,
  pedidos: PropTypes.array,
  proveedores: PropTypes.array,
};
