import React, { useEffect, useState } from "react";
import {
  Dialog, DialogTitle, DialogContent, DialogActions,
  TextField, Button, FormControl, InputLabel,
  Select, MenuItem, FormHelperText
} from "@mui/material";
import axios from "../axiosConfig";

export default function FormPedido({
  open = false,
  setOpen = () => {},
  formMode = "create",
  selectedRow = null,
  almacenId = "",
  reloadData = () => {},
  setMessage = () => {},
}) {
  const [producciones, setProducciones] = useState([]);
  const [almacenes, setAlmacenes] = useState([]);
  const [estados, setEstados] = useState([]);
  const [loadingCombos, setLoadingCombos] = useState(false);

  const token = localStorage.getItem("token");
  const headers = { headers: { Authorization: `Bearer ${token}` } };

  const initialData = {
    id: null,
    almacenId: almacenId || "",
    produccionId: "",
    descripcion: "",
    fechaHora: "",
    estadoId: 18, // <-- siempre Activo al crear
    empresaId: "",
  };

  const [formData, setFormData] = useState(initialData);
  const [errors, setErrors] = useState({});

  const toArray = (d) =>
    Array.isArray(d) ? d : (d?.content ?? d?.items ?? d?.data ?? d?.results ?? []);

  useEffect(() => {
    const loadCombos = async () => {
      try {
        setLoadingCombos(true);
        const [resAlm, resProd, resEst] = await Promise.all([
          axios.get("/v1/items/almacen/0", headers),
          axios.get("/v1/items/produccion/0", headers),
          axios.get("/v1/items/pedido_estado/0", headers),
        ]);
        setAlmacenes(toArray(resAlm.data));
        setProducciones(toArray(resProd.data));
        setEstados(toArray(resEst.data));
      } catch (e) {
        console.error("Error combos", e?.response?.status, e?.config?.url, e?.response?.data);
        setAlmacenes([]);
        setProducciones([]);
        setEstados([]);
        setMessage({
          open: true,
          severity: "error",
          text: `No se pudieron cargar Almacenes/Producciones/Estados${
            e?.response?.status ? ` (${e.response.status})` : ""
          }.`,
        });
      } finally {
        setLoadingCombos(false);
      }
    };
    loadCombos();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  useEffect(() => {
    if (!open) return;
    setErrors({});

    if (formMode === "edit" && selectedRow) {
      // EDITAR: carga el estado actual del pedido (puede ser distinto de 18)
      setFormData({
        id: selectedRow.id ?? null,
        almacenId: selectedRow.almacenId ?? "",
        produccionId: selectedRow.produccionId ?? "",
        descripcion: selectedRow.descripcion ?? "",
        fechaHora: selectedRow.fechaHora ?? "",
        estadoId: Number(selectedRow.estadoId ?? selectedRow.estado?.id ?? 18),
        empresaId: selectedRow.empresaId ?? "",
      });
    } else {
      // CREAR: siempre arranca en 18 (Activo)
      setFormData({
        ...initialData,
        almacenId: almacenId || "",
        estadoId: 18,
      });
    }
  }, [open, formMode, selectedRow, almacenId]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    const numericFields = new Set(["almacenId", "produccionId", "estadoId"]);
    setFormData((prev) => ({
      ...prev,
      [name]: numericFields.has(name) && value !== "" ? Number(value) : value,
    }));
  };

  const validate = () => {
    const newErrors = {};
    if (!formData.almacenId) newErrors.almacenId = "Almacén no asignado.";
    if (!formData.produccionId) newErrors.produccionId = "Seleccione una producción.";
    if (!formData.fechaHora) newErrors.fechaHora = "La fecha es obligatoria.";
    if (!formData.estadoId) newErrors.estadoId = "Seleccione estado.";
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async () => {
    if (!validate()) return;

    try {
      // Seguridad extra: al crear, SIEMPRE manda estadoId = 18
      const payload =
        formMode === "edit"
          ? formData
          : { ...formData, estadoId: 18 };

      if (formMode === "edit" && formData.id) {
        await axios.put(`/v1/pedido/${formData.id}`, payload, headers);
        setMessage({
          open: true,
          severity: "success",
          text: "Pedido actualizado correctamente.",
        });
      } else {
        await axios.post("/v1/pedido", payload, headers);
        setMessage({
          open: true,
          severity: "success",
          text: "Pedido creado correctamente.",
        });
      }

      setOpen(false);
      reloadData();
    } catch (err) {
      setMessage({
        open: true,
        severity: "error",
        text: err?.response?.data?.message || "Error al guardar pedido.",
      });
    }
  };

  return (
    <Dialog open={open} onClose={() => setOpen(false)} maxWidth="sm" fullWidth>
      <DialogTitle>{formMode === "edit" ? "Editar Pedido" : "Nuevo Pedido"}</DialogTitle>

      <DialogContent>
        <TextField
          fullWidth
          margin="normal"
          label="Fecha y Hora"
          name="fechaHora"
          type="datetime-local"
          value={formData.fechaHora}
          onChange={handleChange}
          error={!!errors.fechaHora}
          helperText={errors.fechaHora}
          InputLabelProps={{ shrink: true }}
        />

        <FormControl
          fullWidth
          margin="normal"
          error={!!errors.almacenId}
          disabled={loadingCombos}
        >
          <InputLabel>Almacén</InputLabel>
          <Select
            name="almacenId"
            value={formData.almacenId || ""}
            onChange={handleChange}
            label="Almacén"
            displayEmpty
          >
            <MenuItem value="">
              <em>Seleccione un almacén</em>
            </MenuItem>
            {almacenes.map((a) => (
              <MenuItem key={a.id} value={a.id}>
                {a.name ?? a.nombre ?? `Almacén ${a.id}`}
              </MenuItem>
            ))}
          </Select>
          {errors.almacenId && <FormHelperText>{errors.almacenId}</FormHelperText>}
        </FormControl>

        <FormControl
          fullWidth
          margin="normal"
          error={!!errors.produccionId}
          disabled={loadingCombos}
        >
          <InputLabel>Producción</InputLabel>
          <Select
            name="produccionId"
            value={formData.produccionId || ""}
            onChange={handleChange}
            label="Producción"
            displayEmpty
          >
            <MenuItem value="">
              <em>Seleccione una producción</em>
            </MenuItem>
            {producciones.map((p) => (
              <MenuItem key={p.id} value={p.id}>
                {p.name ?? p.nombre ?? p.descripcion ?? `Producción ${p.id}`}
              </MenuItem>
            ))}
          </Select>
          {errors.produccionId && (
            <FormHelperText>{errors.produccionId}</FormHelperText>
          )}
        </FormControl>

        <TextField
          fullWidth
          margin="normal"
          label="Descripción"
          name="descripcion"
          value={formData.descripcion}
          onChange={handleChange}
        />

        {/* ESTADO */}
        <FormControl
          fullWidth
          margin="normal"
          error={!!errors.estadoId}
          disabled={loadingCombos || formMode === "create"} // <-- bloqueado al crear
        >
          <InputLabel>Estado</InputLabel>
          <Select
            name="estadoId"
            value={formData.estadoId || ""}
            onChange={handleChange}
            label="Estado"
            displayEmpty
          >
            <MenuItem value="">
              <em>Seleccione un estado</em>
            </MenuItem>
            {estados.map((e) => (
              <MenuItem key={e.id} value={e.id}>
                {e.name ?? e.nombre ?? `Estado ${e.id}`}
              </MenuItem>
            ))}
          </Select>
          {errors.estadoId && <FormHelperText>{errors.estadoId}</FormHelperText>}
          {/* Puedes opcionalmente mostrar un helper fijo al crear */}
          {formMode === "create" && !errors.estadoId && (
            <FormHelperText>Al crear, el pedido queda siempre en estado Activo.</FormHelperText>
          )}
        </FormControl>
      </DialogContent>

      <DialogActions>
        <Button onClick={() => setOpen(false)}>Cancelar</Button>
        <Button variant="contained" onClick={handleSubmit}>
          Guardar
        </Button>
      </DialogActions>
    </Dialog>
  );
}
