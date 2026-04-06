import React, { useMemo, useState } from "react";
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

  const toArray = (d) =>
    Array.isArray(d)
      ? d
      : d?.content ?? d?.items ?? d?.data ?? d?.results ?? [];

  const getProductoId = (p) =>
    p?.producto?.id ??
    p?.productoId ??
    p?.producto_id ??
    p?.producto?.productoId ??
    p?.proId ??
    null;

  const getPresentacionTipoId = (p) =>
    p?.presentacion?.id ??
    p?.presentacionId ??
    p?.presentacion_id ??
    null;

  const getProductoLabel = (p) =>
    p?.producto?.nombre ??
    p?.producto?.name ??
    p?.productoNombre ??
    `Producto ${getProductoId(p) ?? ""}`;

  const getPresentacionLabel = (p) =>
    p?.presentacionProducto?.nombre ??
    p?.presentacionProducto?.name ??
    p?.presentacion?.nombre ??
    p?.presentacion?.name ??
    p?.presentacionNombre ??
    p?.name ??
    p?.nombre ??
    tiposPresentacion.find((t) => String(t.id) === String(getPresentacionTipoId(p)))
      ?.nombre ??
    tiposPresentacion.find((t) => String(t.id) === String(getPresentacionTipoId(p)))
      ?.name ??
    `Presentacion ${p?.id ?? ""}`;

  const presentacionesFiltradas = useMemo(() => {
    if (!formData.productoId) return [];
    const filtered = presentaciones.filter(
      (p) => String(getProductoId(p)) === String(formData.productoId)
    );
    // Fallback defensivo: si el backend no trae productoId en items, no bloquear el combo.
    return filtered.length ? filtered : presentaciones;
  }, [presentaciones, formData.productoId]);

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
      const [prodRes, tipoPresRes, prpItemsRes, prpCrudRes] = await Promise.allSettled([
        axios.get("/v1/items/producto/0"),
        axios.get("/v1/items/presentacion/0"),
        axios.get("/v1/items/producto_presentacion/0"),
        axios.get("/v1/producto_presentacion", { params: { page: 0, size: 500 } }),
      ]);

      const productosItems =
        prodRes.status === "fulfilled" ? toArray(prodRes.value.data) : [];
      const tiposPresItems =
        tipoPresRes.status === "fulfilled" ? toArray(tipoPresRes.value.data) : [];
      const prpItems =
        prpItemsRes.status === "fulfilled" ? toArray(prpItemsRes.value.data) : [];
      const prpCrud =
        prpCrudRes.status === "fulfilled" ? toArray(prpCrudRes.value.data) : [];

      const mergedPrp = uniqueById([...prpItems, ...prpCrud]);
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

  const update = () => {
    if (!selectedRow?.id) {
      setMessage({
        open: true,
        severity: "error",
        text: "Selecciona un articulo para editar.",
      });
      return;
    }

    const productoId =
      selectedRow.presentacionProducto?.producto?.id ?? selectedRow.productoId ?? "";
    const cantidad = Number(selectedRow.cantidad ?? 0);
    const precioUnitario = Number(selectedRow.precioUnitario ?? selectedRow.precio ?? 0);

    setFormData({
      ...initialData,
      ...selectedRow,
      productoId,
      presentacionProductoId:
        selectedRow.presentacionProducto?.id ?? selectedRow.presentacionProductoId ?? "",
      precioUnitario,
      precioTotal: cantidad * precioUnitario,
    });

    setMethodName("Actualizar");
    loadData();
    setOpen(true);
  };

  const inactivateRow = () => {
    if (!selectedRow?.id) return;

    axios
      .put(`/v1/articulo-kardex/${selectedRow.id}/inactivate`)
      .then(() => {
        setMessage({
          open: true,
          severity: "success",
          text: "Anulado correctamente.",
        });
        reloadData();
        setSelectedRow({});
      })
      .catch((err) => {
        console.error("Error al anular:", err?.response || err);
        setMessage({
          open: true,
          severity: "error",
          text: "Error al anular",
        });
      });
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

  const handleSubmit = (e) => {
    e.preventDefault();

    if (!formData.productoId || !formData.presentacionProductoId) {
      setMessage({
        open: true,
        severity: "warning",
        text: "Debes seleccionar Producto y Presentacion.",
      });
      return;
    }

    const payload = {
      ...formData,
      id: selectedRow?.id,
      cantidad: Number(formData.cantidad),
      precio: Number(formData.precioUnitario),
      lote: formData.lote,
      kardexId: Number(kardexId),
      presentacionProductoId: Number(formData.presentacionProductoId),
      estadoId: 1,
      fechaVencimiento: String(formData.fechaVencimiento || "").includes("T")
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
      <Box display="flex" gap={2} justifyContent="flex-end" mb={2}>
        <Button variant="outlined" sx={{ textTransform: "none" }} onClick={create}>
          + Agregar
        </Button>
        <Button variant="outlined" sx={{ textTransform: "none" }} onClick={update}>
          Actualizar
        </Button>
        <Button
          variant="outlined"
          color="warning"
          sx={{ textTransform: "none" }}
          onClick={inactivateRow}
        >
          Anular
        </Button>
      </Box>

      <Dialog open={open} onClose={() => setOpen(false)} fullWidth maxWidth="sm">
        <form onSubmit={handleSubmit}>
          <DialogTitle>Crear/Actualizar Artículo</DialogTitle>

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
                <TextField
                  fullWidth
                  name="cantidad"
                  label="Cantidad"
                  value={formData.cantidad}
                  onChange={handleChange}
                  required
                />
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
                  label="Precio total"
                  value={formData.precioTotal}
                  disabled
                />
              </Grid>

              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  type="date"
                  name="fechaVencimiento"
                  label="Fecha de Vencimiento"
                  value={(formData.fechaVencimiento || "").toString().substring(0, 10)}
                  onChange={handleChange}
                  InputLabelProps={{ shrink: true }}
                  required
                />
              </Grid>

              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  name="lote"
                  label="Lote"
                  value={formData.lote}
                  onChange={handleChange}
                />
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
