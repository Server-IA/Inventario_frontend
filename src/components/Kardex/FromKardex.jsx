import React, { useEffect, useState } from "react";
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Button,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Box,
  FormHelperText,
} from "@mui/material";
import axios from "../axiosConfig";
import * as Yup from "yup";

/* ============================
   🔹 Helpers Yup
============================ */
const numberRequired = (msg, opts = {}) => {
  let y = Yup.number().typeError(msg).required(msg);
  if (opts.min !== undefined) y = y.min(opts.min, `${msg} (mín ${opts.min})`);
  if (opts.max !== undefined) y = y.max(opts.max, `${msg} (máx ${opts.max})`);
  return y;
};

/* ============================
   🔹 Anti-inyección
============================ */
const isSqlSuspicious = (val) => {
  if (!val) return false;
  const s = String(val).toLowerCase();
  return [
    "--",
    ";",
    "/*",
    "*/",
    " or ",
    " and ",
    " drop ",
    " insert ",
    " update ",
    " delete ",
    " select ",
    " union ",
  ].some((tok) => s.includes(tok));
};

/* ============================
   🔹 Schema (incluye opcionales)
============================ */
const kardexSchema = Yup.object({
  fechaHora: Yup.string().required("Fecha/Hora obligatoria."),
  almacenId: numberRequired("Almacén obligatorio.", { min: 1 }),
  produccionId: numberRequired("Producción obligatoria.", { min: 1 }),
  tipoMovimientoId: numberRequired("Tipo de movimiento obligatorio.", {
    min: 1,
  }),
  descripcion: Yup.string()
    .max(500, "Máx 500 caracteres.")
    .test("no-sql", "El texto contiene patrones no permitidos.", (v) =>
      !isSqlSuspicious(v)
    ),
  estadoId: Yup.number().oneOf([0, 1]).required("Estado obligatorio."),
  empresaId: numberRequired("Empresa obligatoria.", { min: 1 }),

  // opcionales
  pedidoId: Yup.number()
    .nullable()
    .transform((v, o) => (o === "" ? null : v)),
  ordenCompraId: Yup.number()
    .nullable()
    .transform((v, o) => (o === "" ? null : v)),
  clienteProveedorId: Yup.number()
    .nullable()
    .transform((v, o) => (o === "" ? null : v)),
});

/* ============================
   🔹 Component
============================ */
export default function FormKardex({
  open,
  setOpen,
  formMode = "create",
  selectedRow,
  reloadData,
  setMessage,
  setSelectedRow,
}) {
  const [formData, setFormData] = useState({
    id: undefined,
    fechaHora: "",
    almacenId: "",
    produccionId: "",
    tipoMovimientoId: "",
    pedidoId: "",
    ordenCompraId: "",
    clienteProveedorId: "",
    descripcion: "",
    estadoId: 1,
    empresaId: null,
  });

  const [errors, setErrors] = useState({});

  /* ============================
     🔹 Combos
  ============================ */
  const [almacenes, setAlmacenes] = useState([]);
  const [producciones, setProducciones] = useState([]);
  const [tiposMovimiento, setTiposMovimiento] = useState([]);
  const [pedidos, setPedidos] = useState([]);
  const [ordenesCompra, setOrdenesCompra] = useState([]);
  const [empresas, setEmpresas] = useState([]);

  /* ============================
     🔹 Token + empresaId
  ============================ */
  const token = localStorage.getItem("token");
  const headers = token
    ? { headers: { Authorization: `Bearer ${token}` } }
    : {};

  const empresaId = (() => {
    try {
      return token
        ? JSON.parse(atob(token.split(".")[1]))?.empresaId ?? null
        : null;
    } catch {
      return null;
    }
  })();

  /* ============================
     🔹 pickList Response
  ============================ */
  const pickList = (res) => {
    const d = res?.data;
    if (Array.isArray(d)) return d;
    if (Array.isArray(d?.data)) return d.data;
    if (Array.isArray(d?.content)) return d.content;
    if (Array.isArray(d?.data?.content)) return d.data.content;
    return [];
  };

  /* ============================
     🔹 CARGA INICIAL DE LISTAS
  ============================ */
  useEffect(() => {
    const loadLists = async () => {
      // OBLIGATORIAS
      try {
        const [mov, prod, alm] = await Promise.all([
          axios.get("/v1/items/tipo_movimiento/0", headers),
          axios.get("/v1/items/produccion/0", headers),
          axios.get("/v1/items/almacen/0", headers),
        ]);

        setTiposMovimiento(pickList(mov));
        setProducciones(pickList(prod));
        setAlmacenes(pickList(alm));
      } catch (e) {
        console.error("❌ Error cargando listas OBLIGATORIAS:", e);
        setTiposMovimiento([]);
        setProducciones([]);
        setAlmacenes([]);
        return;
      }

      // OPCIONALES - cada una aislada
      try {
        const p = await axios.get("/v1/items/pedido/0", headers);
        setPedidos(pickList(p));
      } catch {
        setPedidos([]);
      }

      try {
        const oc = await axios.get("/v1/items/orden_compra/0", headers);
        setOrdenesCompra(pickList(oc));
      } catch {
        setOrdenesCompra([]);
      }

      try {
        const cp = await axios.get("/v1/items/empresa/0", headers);
        setEmpresas(pickList(cp));
      } catch {
        setEmpresas([]);
      }
    };

    loadLists();
  }, []);

  /* ============================
     🔹 Cargar datos en modo editar
  ============================ */
  useEffect(() => {
    if (open && formMode === "edit" && selectedRow) {
      setFormData({
        ...selectedRow,
        pedidoId: selectedRow.pedidoId ?? "",
        ordenCompraId: selectedRow.ordenCompraId ?? "",
        clienteProveedorId: selectedRow.clienteProveedorId ?? "",
        empresaId,
      });
    } else if (open) {
      setFormData({
        id: undefined,
        fechaHora: "",
        almacenId: "",
        produccionId: "",
        tipoMovimientoId: "",
        pedidoId: "",
        ordenCompraId: "",
        clienteProveedorId: "",
        descripcion: "",
        estadoId: 1,
        empresaId,
      });
      setErrors({});
    }
  }, [open, formMode, selectedRow, empresaId]);

  /* ============================
     🔹 Handlers
  ============================ */
  const handleChange = (e) => {
    const { name, value } = e.target;
    const numeric = [
      "almacenId",
      "produccionId",
      "tipoMovimientoId",
      "estadoId",
      "pedidoId",
      "ordenCompraId",
      "clienteProveedorId",
    ];
    setFormData((prev) => ({
      ...prev,
      [name]: numeric.includes(name) && value !== "" ? Number(value) : value,
    }));
    setErrors((prev) => ({ ...prev, [name]: undefined }));
  };

  const handleSubmit = async () => {
    try {
      await kardexSchema.validate(formData, { abortEarly: false });
      setErrors({});

      const payload = {
        ...formData,
        pedidoId: formData.pedidoId || null,
        ordenCompraId: formData.ordenCompraId || null,
        clienteProveedorId: formData.clienteProveedorId || null,
      };

      const method = formMode === "edit" ? axios.put : axios.post;
      const url =
        formMode === "edit"
          ? `/v1/kardex/${payload.id}`
          : "/v1/kardex";

      await method(url, payload, headers);

      reloadData?.();
      setMessage?.({
        open: true,
        severity: "success",
        text: `Kardex ${
          formMode === "edit" ? "actualizado" : "creado"
        } correctamente.`,
      });
      setOpen(false);
      setSelectedRow(null);
    } catch (err) {
      if (err.name === "ValidationError") {
        const map = {};
        err.inner.forEach((e) => {
          if (e.path && !map[e.path]) map[e.path] = e.message;
        });
        setErrors(map);
        return;
      }

      console.error(err);
      setMessage?.({
        open: true,
        severity: "error",
        text: "Error al guardar Kardex.",
      });
    }
  };

  /* ============================
     🔹 Render Names
  ============================ */
  const renderName = (it) =>
    it?.name ?? it?.nombre ?? it?.descripcion ?? `#${it?.id}`;

  /* ============================
     🔹 UI
  ============================ */
  return (
    <Box>
      <Dialog open={open} onClose={() => setOpen(false)} fullWidth>
        <DialogTitle>
          {formMode === "edit" ? "Editar Kardex" : "Crear Kardex"}
        </DialogTitle>

        <DialogContent sx={{ display: "flex", flexDirection: "column", gap: 2 }}>
          
          {/* Fecha/Hora */}
          <TextField
            label="Fecha/Hora"
            type="datetime-local"
            name="fechaHora"
            value={formData.fechaHora}
            onChange={handleChange}
            InputLabelProps={{ shrink: true }}
            error={!!errors.fechaHora}
            helperText={errors.fechaHora}
            fullWidth
          />

          {/* Almacén */}
          <FormControl fullWidth error={!!errors.almacenId}>
            <InputLabel>Almacén</InputLabel>
            <Select
              name="almacenId"
              label="Almacén"
              value={formData.almacenId}
              onChange={handleChange}
            >
              <MenuItem value=""><em>Seleccione...</em></MenuItem>
              {almacenes.map((a) => (
                <MenuItem key={a.id} value={a.id}>{renderName(a)}</MenuItem>
              ))}
            </Select>
            <FormHelperText>{errors.almacenId}</FormHelperText>
          </FormControl>

          {/* Producción */}
          <FormControl fullWidth error={!!errors.produccionId}>
            <InputLabel>Producción</InputLabel>
            <Select
              name="produccionId"
              label="Producción"
              value={formData.produccionId}
              onChange={handleChange}
            >
              <MenuItem value=""><em>Seleccione...</em></MenuItem>
              {producciones.map((p) => (
                <MenuItem key={p.id} value={p.id}>{renderName(p)}</MenuItem>
              ))}
            </Select>
            <FormHelperText>{errors.produccionId}</FormHelperText>
          </FormControl>

          {/* Tipo Movimiento */}
          <FormControl fullWidth error={!!errors.tipoMovimientoId}>
            <InputLabel>Tipo Movimiento</InputLabel>
            <Select
              name="tipoMovimientoId"
              label="Tipo Movimiento"
              value={formData.tipoMovimientoId}
              onChange={handleChange}
            >
              <MenuItem value=""><em>Seleccione...</em></MenuItem>
              {tiposMovimiento.map((t) => (
                <MenuItem key={t.id} value={t.id}>{renderName(t)}</MenuItem>
              ))}
            </Select>
            <FormHelperText>{errors.tipoMovimientoId}</FormHelperText>
          </FormControl>

          {/* Pedido */}
          <FormControl fullWidth>
            <InputLabel>Pedido</InputLabel>
            <Select
              name="pedidoId"
              label="Pedido"
              value={formData.pedidoId}
              onChange={handleChange}
            >
              <MenuItem value=""><em>Sin pedido asociado</em></MenuItem>
              {pedidos.map((p) => (
                <MenuItem key={p.id} value={p.id}>{renderName(p)}</MenuItem>
              ))}
            </Select>
          </FormControl>

          {/* Orden de compra */}
          <FormControl fullWidth>
            <InputLabel>Orden de Compra</InputLabel>
            <Select
              name="ordenCompraId"
              label="Orden de Compra"
              value={formData.ordenCompraId}
              onChange={handleChange}
            >
              <MenuItem value=""><em>Sin orden asociada</em></MenuItem>
              {ordenesCompra.map((o) => (
                <MenuItem key={o.id} value={o.id}>{renderName(o)}</MenuItem>
              ))}
            </Select>
          </FormControl>

          {/* Cliente / proveedor */}
          <FormControl fullWidth>
            <InputLabel>Cliente / Proveedor</InputLabel>
            <Select
              name="clienteProveedorId"
              label="Cliente / Proveedor"
              value={formData.clienteProveedorId}
              onChange={handleChange}
            >
              <MenuItem value=""><em>Sin cliente/proveedor</em></MenuItem>
              {empresas.map((e) => (
                <MenuItem key={e.id} value={e.id}>{renderName(e)}</MenuItem>
              ))}
            </Select>
          </FormControl>

          {/* Descripción */}
          <TextField
            fullWidth
            multiline
            label="Descripción"
            name="descripcion"
            value={formData.descripcion}
            onChange={handleChange}
            error={!!errors.descripcion}
            helperText={errors.descripcion}
          />

          {/* Estado */}
          <FormControl fullWidth error={!!errors.estadoId}>
            <InputLabel>Estado</InputLabel>
            <Select
              name="estadoId"
              label="Estado"
              value={formData.estadoId}
              onChange={handleChange}
            >
              <MenuItem value={1}>Activo</MenuItem>
              <MenuItem value={0}>Inactivo</MenuItem>
            </Select>
            <FormHelperText>{errors.estadoId}</FormHelperText>
          </FormControl>
        </DialogContent>

        <DialogActions>
          <Button onClick={() => setOpen(false)}>Cancelar</Button>
          <Button onClick={handleSubmit}>Guardar</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}
