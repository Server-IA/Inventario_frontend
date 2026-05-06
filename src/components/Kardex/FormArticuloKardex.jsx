import React, { useEffect, useMemo, useState } from "react";
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Box,
  Button,
  TextField,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Grid,
} from "@mui/material";
import axios from "../axiosConfig";
import { resolveArticuloKardexId } from "./utils/kardexFormatters";

const toArray = (d) =>
  Array.isArray(d) ? d : d?.content ?? d?.items ?? d?.data ?? d?.results ?? [];

const mapHeaderAndItemsToKardexPayload = (data, items) => ({
  almacenId: data?.almacenId ?? null,
  almacenDestinoId: data?.almacenDestinoId ?? null,
  ordenCompraId: data?.ordenCompraId ?? null,
  pedidoId: data?.pedidoId ?? null,
  produccionId: data?.produccionId ?? null,
  clienteProveedorId: data?.clienteProveedorId ?? null,
  descripcion: data?.descripcion ?? "",
  items,
});

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
  const [productos, setProductos] = useState([]);
  const [tiposPresentacion, setTiposPresentacion] = useState([]);

  const initialData = {
    productoId: "",
    presentacionProductoId: "",
    cantidad: "",
    precioUnitario: "",
    precioTotal: "",
    lote: "",
    fechaVencimiento: "",
  };

  const [formData, setFormData] = useState(initialData);

  const getProductoId = (p) =>
    p?.producto?.id ?? p?.productoId ?? p?.producto_id ?? p?.proId ?? p?.idProducto ?? null;

  const getPresentacionTipoId = (p) =>
    p?.presentacion?.id ?? p?.presentacionId ?? p?.presentacion_id ?? null;

  const getProductoLabel = (p) =>
    p?.producto?.nombre ?? p?.producto?.name ?? p?.productoNombre ?? `Producto ${getProductoId(p) ?? ""}`;

  const getPresentacionLabel = (p) =>
    p?.presentacionProducto?.nombre ??
    p?.presentacionProducto?.name ??
    p?.presentacion?.nombre ??
    p?.presentacion?.name ??
    p?.presentacionNombre ??
    p?.name ??
    p?.nombre ??
    tiposPresentacion.find((t) => String(t.id) === String(getPresentacionTipoId(p)))?.nombre ??
    tiposPresentacion.find((t) => String(t.id) === String(getPresentacionTipoId(p)))?.name ??
    `Presentacion ${p?.id ?? ""}`;

  const presentacionesFiltradas = useMemo(() => {
    if (!formData.productoId) return [];
    const filtered = presentaciones.filter((p) => String(getProductoId(p)) === String(formData.productoId));
    return filtered.length ? filtered : presentaciones;
  }, [presentaciones, formData.productoId]);

  useEffect(() => {
    if (formData.productoId || !formData.presentacionProductoId || !presentaciones.length) return;
    const selectedPresentacion = presentaciones.find(
      (p) => String(p?.id) === String(formData.presentacionProductoId)
    );
    const productoId = getProductoId(selectedPresentacion);
    if (productoId != null) {
      setFormData((prev) => ({ ...prev, productoId: Number(productoId) }));
    }
  }, [formData.productoId, formData.presentacionProductoId, presentaciones]);

  const loadData = async () => {
    const uniqueById = (arr) => {
      const seen = new Set();
      return arr.filter((x) => {
        const id = x?.id;
        if (id == null || seen.has(String(id))) return false;
        seen.add(String(id));
        return true;
      });
    };

    try {
      const [prodRes, tipoPresRes, prpItemsRes] = await Promise.allSettled([
        axios.get("/v1/items/producto/0"),
        axios.get("/v1/items/presentacion/0"),
        axios.get("/v1/items/producto_presentacion/0"),
      ]);

      const productosItems = prodRes.status === "fulfilled" ? toArray(prodRes.value.data) : [];
      const tiposPresItems = tipoPresRes.status === "fulfilled" ? toArray(tipoPresRes.value.data) : [];
      const prpItems = prpItemsRes.status === "fulfilled" ? toArray(prpItemsRes.value.data) : [];

      const mergedPrp = uniqueById([...prpItems]);
      setPresentaciones(mergedPrp);
      setTiposPresentacion(tiposPresItems);

      if (productosItems.length) {
        setProductos(
          productosItems.map((p) => ({
            id: Number(p.id),
            label: p?.nombre ?? p?.name ?? `Producto ${p?.id ?? ""}`,
          }))
        );
      } else {
        const derived = uniqueById(
          mergedPrp
            .map((p) => {
              const id = getProductoId(p);
              if (id == null) return null;
              return { id: Number(id), label: getProductoLabel(p) };
            })
            .filter(Boolean)
        );
        setProductos(derived);
      }
    } catch (err) {
      console.error("Error al cargar catalogos de articulo kardex:", err);
      setPresentaciones([]);
      setProductos([]);
      setTiposPresentacion([]);
    }
  };

  const create = () => {
    if (!kardexId) {
      setMessage({
        open: true,
        severity: "error",
        text: "Debes seleccionar un Kardex antes de crear un articulo.",
      });
      return;
    }
    setFormData({ ...initialData });
    setMethodName("Agregar");
    loadData();
    setOpen(true);
  };

  const update = async () => {
    const articuloId = resolveArticuloKardexId(selectedRow);
    if (!articuloId) {
      setMessage({ open: true, severity: "error", text: "Selecciona un articulo para editar." });
      return;
    }

    try {
      const res = await axios.get(`/v1/kardex/${kardexId}/update-form`);
      const data = res?.data ?? {};
      const items = Array.isArray(data?.items) ? data.items : [];
      const found = items.find((it) => String(it?.id ?? "") === String(articuloId));

      const cantidad = Number(found?.cantidad ?? selectedRow?.cantidad ?? 0);
      const precioUnitario = Number(found?.precio ?? selectedRow?.precio ?? 0);

      setFormData({
        ...initialData,
        ...selectedRow,
        id: articuloId,
        productoId: "",
        presentacionProductoId: found?.presentacionProductoId ?? selectedRow?.presentacionProductoId ?? "",
        cantidad,
        precioUnitario,
        precioTotal: cantidad * precioUnitario,
        lote: found?.lote ?? selectedRow?.lote ?? "",
        fechaVencimiento: found?.fechaVencimiento ?? selectedRow?.fechaVencimiento ?? "",
      });
    } catch (err) {
      console.error("Error al precargar articulo para editar:", err);
      const cantidad = Number(selectedRow?.cantidad ?? 0);
      const precioUnitario = Number(selectedRow?.precioUnitario ?? selectedRow?.precio ?? 0);
      setFormData({
        ...initialData,
        ...selectedRow,
        id: articuloId,
        productoId: "",
        presentacionProductoId: selectedRow?.presentacionProductoId ?? "",
        cantidad,
        precioUnitario,
        precioTotal: cantidad * precioUnitario,
      });
    } finally {
      setMethodName("Actualizar");
      loadData();
      setOpen(true);
    }
  };

  const inactivateRow = async () => {
    const articuloId = resolveArticuloKardexId(selectedRow);
    if (!articuloId) return;

    try {
      const formRes = await axios.get(`/v1/kardex/${kardexId}/update-form`);
      const data = formRes?.data ?? {};
      const items = Array.isArray(data?.items) ? data.items : [];
      const nextItems = items.filter((it) => String(it?.id ?? "") !== String(articuloId));

      const payload = mapHeaderAndItemsToKardexPayload(data, nextItems);
      await axios.put(`/v1/kardex/${kardexId}`, payload);

      setMessage({ open: true, severity: "success", text: "Anulado correctamente." });
      reloadData();
      setSelectedRow(null);
    } catch (err) {
      const d = err?.response?.data;
      setMessage({
        open: true,
        severity: "error",
        text: (typeof d === "string" && d) || d?.message || d?.detail || d?.title || "Error al anular",
      });
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => {
      const next = { ...prev };

      if (name === "productoId") {
        next.productoId = value === "" ? "" : Number(value);
        next.presentacionProductoId = "";
      } else if (["presentacionProductoId", "cantidad", "precioUnitario"].includes(name)) {
        next[name] = value === "" ? "" : Number(value);
      } else {
        next[name] = value;
      }

      const qty = Number(next.cantidad || 0);
      const unit = Number(next.precioUnitario || 0);
      next.precioTotal = qty * unit;
      return next;
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!formData.productoId || !formData.presentacionProductoId) {
      setMessage({ open: true, severity: "warning", text: "Debes seleccionar Producto y Presentacion." });
      return;
    }

    if (!Number(formData.cantidad) || Number(formData.cantidad) <= 0) {
      setMessage({ open: true, severity: "warning", text: "La cantidad debe ser mayor que cero." });
      return;
    }

    if (Number.isNaN(Number(formData.precioUnitario)) || Number(formData.precioUnitario) < 0) {
      setMessage({ open: true, severity: "warning", text: "El precio unitario es invalido." });
      return;
    }

    const isUpdate = methodName !== "Agregar";
    const articuloId = resolveArticuloKardexId(selectedRow);

    if (isUpdate && !articuloId) {
      setMessage({ open: true, severity: "error", text: "No se encontro el ID del articulo a editar." });
      return;
    }

    try {
      const formRes = await axios.get(`/v1/kardex/${kardexId}/update-form`);
      const data = formRes?.data ?? {};
      const items = Array.isArray(data?.items) ? data.items : [];

      const nextItemPatch = {
        presentacionProductoId: Number(formData.presentacionProductoId),
        cantidad: Number(formData.cantidad),
        precio: Number(formData.precioUnitario),
        lote: formData.lote || null,
        fechaVencimiento: formData.fechaVencimiento
          ? String(formData.fechaVencimiento).substring(0, 10)
          : null,
      };

      let nextItems;
      if (isUpdate) {
        nextItems = items.map((it) =>
          String(it?.id ?? "") === String(articuloId)
            ? {
                ...it,
                ...nextItemPatch,
                id: Number(articuloId),
              }
            : it
        );
      } else {
        nextItems = [
          ...items,
          {
            ...nextItemPatch,
            devolutivo: false,
            responsableId: null,
          },
        ];
      }

      const payload = mapHeaderAndItemsToKardexPayload(data, nextItems);
      await axios.put(`/v1/kardex/${kardexId}`, payload);

      setMessage({ open: true, severity: "success", text: "Guardado correctamente." });
      reloadData();
      setOpen(false);
    } catch (err) {
      const data = err?.response?.data;
      console.error("Error al guardar:", data || err);

      let errorMsg = "Error al guardar";
      if (data) {
        if (typeof data === "string") errorMsg = data;
        else errorMsg = data?.message || data?.error || data?.detail || data?.title || errorMsg;
      }

      setMessage({ open: true, severity: "error", text: errorMsg });
    }
  };

  return (
    <>
      <Box display="flex" gap={2} justifyContent="flex-start" mb={2}>
        <Button variant="outlined" sx={{ textTransform: "none" }} onClick={create}>
          + Agregar
        </Button>
        <Button variant="outlined" sx={{ textTransform: "none" }} onClick={update}>
          Actualizar
        </Button>
        <Button variant="outlined" color="warning" sx={{ textTransform: "none" }} onClick={inactivateRow}>
          Anular
        </Button>
      </Box>

      <Dialog open={open} onClose={() => setOpen(false)} fullWidth maxWidth="sm">
        <form onSubmit={handleSubmit}>
          <DialogTitle>Crear/Actualizar Articulo</DialogTitle>

          <DialogContent>
            <Grid container spacing={2} sx={{ mt: 0.5 }}>
              <Grid item xs={12} sm={6}>
                <FormControl fullWidth required>
                  <InputLabel id="producto-label">Producto</InputLabel>
                  <Select
                    labelId="producto-label"
                    label="Producto"
                    name="productoId"
                    value={formData.productoId ?? ""}
                    onChange={handleChange}
                  >
                    <MenuItem value="">
                      <em>Seleccione...</em>
                    </MenuItem>
                    {productos.map((p) => (
                      <MenuItem key={p.id} value={p.id}>
                        {p.label}
                      </MenuItem>
                    ))}
                  </Select>
                </FormControl>
              </Grid>

              <Grid item xs={12} sm={6}>
                <FormControl fullWidth required>
                  <InputLabel id="pp-label" shrink>
                    Presentacion
                  </InputLabel>
                  <Select
                    labelId="pp-label"
                    label="Presentacion"
                    name="presentacionProductoId"
                    value={formData.presentacionProductoId ?? ""}
                    onChange={handleChange}
                    displayEmpty
                    disabled={!formData.productoId}
                  >
                    <MenuItem value="">
                      <em>Seleccione...</em>
                    </MenuItem>
                    {presentacionesFiltradas.map((p) => (
                      <MenuItem key={p.id} value={p.id}>
                        {getPresentacionLabel(p)}
                      </MenuItem>
                    ))}
                  </Select>
                </FormControl>
              </Grid>

              <Grid item xs={12} sm={4}>
                <TextField fullWidth name="cantidad" label="Cantidad" value={formData.cantidad} onChange={handleChange} required />
              </Grid>

              <Grid item xs={12} sm={4}>
                <TextField
                  fullWidth
                  name="precioUnitario"
                  label="Precio Unitario"
                  value={formData.precioUnitario}
                  onChange={handleChange}
                  required
                />
              </Grid>

              <Grid item xs={12} sm={4}>
                <TextField
                  fullWidth
                  name="precioTotal"
                  label="Precio Total"
                  value={formData.precioTotal || 0}
                  InputProps={{ readOnly: true }}
                />
              </Grid>

              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  type="date"
                  name="fechaVencimiento"
                  label="Fecha de Vencimiento"
                  value={formData.fechaVencimiento || ""}
                  onChange={handleChange}
                  InputLabelProps={{ shrink: true }}
                />
              </Grid>

              <Grid item xs={12} sm={6}>
                <TextField fullWidth name="lote" label="Lote" value={formData.lote || ""} onChange={handleChange} />
              </Grid>
            </Grid>
          </DialogContent>

          <DialogActions>
            <Button onClick={() => setOpen(false)}>Cancelar</Button>
            <Button type="submit">Guardar</Button>
          </DialogActions>
        </form>
      </Dialog>
    </>
  );
}
