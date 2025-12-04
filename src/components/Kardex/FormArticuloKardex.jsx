// src/components/FormArticuloKardex.jsx
import React, { useState } from "react";
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  TextField,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
} from "@mui/material";
import axios from "../axiosConfig";
import StackButtons from "../StackButtons";

export default function FormArticuloKardex({
  selectedRow,
  setSelectedRow,
  setMessage,
  reloadData,
  kardexId,
}) {
  const [open, setOpen] = useState(false);
  const [methodName, setMethodName] = useState("");
  const [presentaciones, setPresentaciones] = useState([]);

  const initialData = {
    cantidad: "",
    precio: "",
    fechaVencimiento: "",
    identificadorProducto: "",
    kardexId: kardexId || "",
    presentacionProductoId: "",
    estadoId: "1",
  };

  const [formData, setFormData] = useState(initialData);

  /* -------- Helpers catálogo -------- */
  const toArray = (d) =>
    Array.isArray(d)
      ? d
      : d?.content ?? d?.items ?? d?.data ?? d?.results ?? [];

  const ppLabel = (p) => {
    const base =
      p.name ??
      p.nombre ??
      [
        p.producto?.nombre ?? p.productoNombre,
        p.presentacion?.nombre ?? p.presentacionNombre,
        p.cantidad
          ? `${p.cantidad} ${
              p.unidad?.nombre ?? p.unidadNombre ?? ""
            }`.trim()
          : null,
      ]
        .filter(Boolean)
        .join(" · ");
    return base || `Presentación ${p.id}`;
  };

  const loadData = async () => {
    try {
      const res = await axios.get("/v1/items/producto_presentacion/0");
      setPresentaciones(toArray(res.data));
    } catch (err) {
      console.error("Error al cargar presentaciones:", err);
      setPresentaciones([]);
    }
  };

  /* -------- CRUD local -------- */
  const create = () => {
    if (!kardexId) {
      setMessage({
        open: true,
        severity: "error",
        text: "Debes seleccionar un Kardex antes de crear un artículo.",
      });
      return;
    }
    setFormData({ ...initialData, kardexId });
    setMethodName("Agregar");
    loadData();
    setOpen(true);
  };

  const update = () => {
    if (!selectedRow?.id) {
      setMessage({
        open: true,
        severity: "error",
        text: "Selecciona un artículo para editar.",
      });
      return;
    }

    setFormData({
      ...initialData,
      ...selectedRow,
      presentacionProductoId:
        selectedRow.presentacionProducto?.id ??
        selectedRow.presentacionProductoId ??
        "",
      identificadorProducto: selectedRow.identificadorProducto ?? "",
      estadoId: String(selectedRow.estadoId ?? 1),
    });

    setMethodName("Actualizar");
    loadData();
    setOpen(true);
  };

  const deleteRow = () => {
    if (!selectedRow?.id) return;

    axios
      .delete(`/v1/articulo-kardex/${selectedRow.id}`)
      .then(() => {
        setMessage({
          open: true,
          severity: "success",
          text: "Eliminado correctamente.",
        });
        reloadData();
        setSelectedRow({});
      })
      .catch((err) => {
        console.error("Error al eliminar:", err?.response || err);
        setMessage({
          open: true,
          severity: "error",
          text: "Error al eliminar",
        });
      });
  };

  /* -------- Manejo de formulario -------- */
  const handleChange = (e) => {
    const { name, value } = e.target;
    const numeric = new Set([
      "kardexId",
      "presentacionProductoId",
      "estadoId",
      "cantidad",
      "precio",
    ]);
    const cast = numeric.has(name) && value !== "" ? Number(value) : value;
    setFormData((prev) => ({ ...prev, [name]: cast }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    const payload = {
      ...formData,
      id: selectedRow.id,
      cantidad: Number(formData.cantidad),
      precio: Number(formData.precio),
      kardexId: Number(formData.kardexId),
      presentacionProductoId: Number(formData.presentacionProductoId),
      estadoId: Number(formData.estadoId),
      identificadorProducto: formData.identificadorProducto || null,
      fechaVencimiento: String(formData.fechaVencimiento).includes("T")
        ? formData.fechaVencimiento
        : `${formData.fechaVencimiento}T00:00:00`,
    };

    const method = methodName === "Agregar" ? axios.post : axios.put;
    const url =
      methodName === "Agregar"
        ? "/v1/articulo-kardex"
        : `/v1/articulo-kardex/${selectedRow.id}`;

    method(url, payload)
      .then(() => {
        setMessage({
          open: true,
          severity: "success",
          text: "Guardado correctamente.",
        });
        reloadData();
        setOpen(false);
      })
      .catch((err) => {
        const data = err?.response?.data;
        console.error("Error al guardar:", data || err);

        let errorMsg = "Error al guardar";

        if (data) {
          if (typeof data === "string") {
            errorMsg = data;
          } else if (data.message) {
            errorMsg = data.message;
          } else if (data.error) {
            errorMsg = data.error;
          } else if (data.detail) {
            errorMsg = data.detail;
          }
        }

        setMessage({ open: true, severity: "error", text: errorMsg });
      });
  };

  return (
    <>
      <StackButtons methods={{ create, update, deleteRow }} />

      <Dialog open={open} onClose={() => setOpen(false)} fullWidth maxWidth="sm">
        <form onSubmit={handleSubmit}>
          <DialogTitle>{methodName} Artículo Kardex</DialogTitle>

          <DialogContent>
            <TextField
              fullWidth
              name="kardexId"
              label="Kardex ID"
              value={formData.kardexId}
              margin="dense"
              required
              disabled
            />

            {/* Producto Presentación */}
            <FormControl fullWidth margin="normal" required>
              <InputLabel id="pp-label">Producto Presentación</InputLabel>
              <Select
                labelId="pp-label"
                label="Producto Presentación"
                name="presentacionProductoId"
                value={formData.presentacionProductoId ?? ""}
                onChange={handleChange}
                displayEmpty
              >
                <MenuItem value="">
                  <em>Seleccione...</em>
                </MenuItem>
                {presentaciones.map((p) => (
                  <MenuItem key={p.id} value={p.id}>
                    {ppLabel(p)}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>

            <TextField
              fullWidth
              name="cantidad"
              label="Cantidad"
              value={formData.cantidad}
              onChange={handleChange}
              margin="dense"
              required
            />

            <TextField
              fullWidth
              name="precio"
              label="Precio"
              value={formData.precio}
              onChange={handleChange}
              margin="dense"
              required
            />

            {/* Identificador de producto */}
            <TextField
              fullWidth
              name="identificadorProducto"
              label="Identificador producto"
              value={formData.identificadorProducto}
              onChange={handleChange}
              margin="dense"
            />

            <TextField
              fullWidth
              type="date"
              name="fechaVencimiento"
              label="Fecha Vencimiento"
              value={(
                formData.fechaVencimiento || ""
              ).toString().substring(0, 10)}
              onChange={handleChange}
              margin="dense"
              InputLabelProps={{ shrink: true }}
              required
            />

            <FormControl fullWidth margin="normal" required>
              <InputLabel id="estado-label">Estado</InputLabel>
              <Select
                labelId="estado-label"
                label="Estado"
                name="estadoId"
                value={formData.estadoId}
                onChange={handleChange}
              >
                <MenuItem value={1}>Activo</MenuItem>
                <MenuItem value={2}>Inactivo</MenuItem>
              </Select>
            </FormControl>
          </DialogContent>

          <DialogActions>
            <Button onClick={() => setOpen(false)}>Cancelar</Button>
            <Button type="submit">{methodName}</Button>
          </DialogActions>
        </form>
      </Dialog>
    </>
  );
}
