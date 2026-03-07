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
  Typography,
  Table,
  TableHead,
  TableBody,
  TableRow,
  TableCell,
  Grid,
  InputAdornment,
  CircularProgress,
} from "@mui/material";
import SearchIcon from "@mui/icons-material/Search";
import axios from "../axiosConfig";
import * as Yup from "yup";

/* ============================
   🔹 Constante: ID tipo mov ENTRADA COMPRA
   👉 AJUSTA ESTE VALOR SEGÚN TU CATÁLOGO
============================ */
const TIPO_MOV_ENTRADA_COMPRA = 2;

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
    .transform((v, o) => (o === "" ? null : v))
    .when("tipoMovimientoId", {
      is: TIPO_MOV_ENTRADA_COMPRA,
      then: numberRequired("Orden de compra obligatoria.", { min: 1 }),
      otherwise: (schema) =>
        schema.nullable().transform((v, o) => (o === "" ? null : v)),
    }),

  clienteProveedorId: Yup.number()
    .nullable()
    .transform((v, o) => (o === "" ? null : v)),
});

/* ============================
   🔹 pickList Response helper
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
     🔹 Items de la Orden de Compra
     (para ENTRADA COMPRA)
  ============================ */
  const [ocItems, setOcItems] = useState([]);
  const [loadingOcItems, setLoadingOcItems] = useState(false);

  /* ============================
     🔹 Modal de Búsqueda de Producción
  ============================ */
  const [produccionSearchOpen, setProduccionSearchOpen] = useState(false);
  const [produccionSearchNombre, setProduccionSearchNombre] = useState("");
  const [produccionesCompletas, setProduccionesCompletas] = useState([]);
  const [loadingProduccionesCompletas, setLoadingProduccionesCompletas] =
    useState(false);

  /* ============================
     🔹 Modal de Búsqueda de Pedido
  ============================ */
  const [pedidoSearchOpen, setPedidoSearchOpen] = useState(false);
  const [pedidoSearchNombre, setPedidoSearchNombre] = useState("");
  const [pedidosCompletos, setPedidosCompletos] = useState([]);
  const [loadingPedidosCompletos, setLoadingPedidosCompletos] = useState(false);

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
     🔹 CARGA INICIAL DE LISTAS
  ============================ */
  useEffect(() => {
    if (!open) return;

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
  }, [open]); // solo cuando se abre el modal

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
      // en modo editar, podrías cargar los items si aplica
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
      setOcItems([]);
    }
  }, [open, formMode, selectedRow, empresaId]);

  /* ============================
     🔹 Cargar items de OC cuando:
       - es tipo ENTRADA COMPRA
       - hay ordenCompraId
  ============================ */
  useEffect(() => {
    const { tipoMovimientoId, ordenCompraId } = formData;

    if (
      !open ||
      !ordenCompraId ||
      Number(tipoMovimientoId) !== TIPO_MOV_ENTRADA_COMPRA
    ) {
      setOcItems([]);
      return;
    }

    const loadOcItems = async () => {
      setLoadingOcItems(true);
      try {
        // 👉 AJUSTA ESTE ENDPOINT A TU BACKEND REAL
        const res = await axios.get(
          `/v1/orden_compra/${ordenCompraId}/items`,
          headers
        );
        const items = pickList(res).map((it) => {
          const cantidadPedida = Number(it.cantidadPedida ?? it.cantidad ?? 0);
          const cantidadRecibida = Number(it.cantidadRecibida ?? 0);
          const cantidadPendiente = cantidadPedida - cantidadRecibida;

          return {
            id: it.id,
            producto:
              it.productoNombre ||
              it.producto?.nombre ||
              it.descripcion ||
              `Item #${it.id}`,
            cantidadPedida,
            cantidadRecibida,
            cantidadPendiente: cantidadPendiente < 0 ? 0 : cantidadPendiente,
            cantidadARecibir: 0,
          };
        });
        setOcItems(items);
      } catch (e) {
        console.error("❌ Error cargando items de OC:", e);
        setOcItems([]);
        setMessage?.({
          open: true,
          severity: "error",
          text: "No se pudieron cargar los ítems de la orden de compra.",
        });
      } finally {
        setLoadingOcItems(false);
      }
    };

    loadOcItems();
  }, [open, formData.tipoMovimientoId, formData.ordenCompraId]); // deps primitivas

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

    // Si cambia la OC o el tipo de movimiento, reseteo items
    if (name === "ordenCompraId" || name === "tipoMovimientoId") {
      setOcItems([]);
    }
  };

  const handleChangeOcItemQty = (id, rawValue) => {
    const value = rawValue === "" ? "" : Number(rawValue);
    setOcItems((prev) =>
      prev.map((it) => {
        if (it.id !== id) return it;

        let cantidadARecibir = value;
        if (cantidadARecibir === "") {
          return { ...it, cantidadARecibir: "" };
        }

        if (isNaN(cantidadARecibir) || cantidadARecibir < 0) {
          cantidadARecibir = 0;
        }
        if (cantidadARecibir > it.cantidadPendiente) {
          cantidadARecibir = it.cantidadPendiente;
        }
        return { ...it, cantidadARecibir };
      })
    );
  };

  /* ============================
     🔹 Abrir modal de búsqueda de Producción
  ============================ */
  const handleOpenProduccionSearch = async () => {
    setProduccionSearchOpen(true);
    setProduccionSearchNombre("");
    setLoadingProduccionesCompletas(true);

    try {
      // Cargar TODAS las producciones una sola vez
      const res = await axios.get("/v1/items/produccion/0", headers);
      const allProds = pickList(res);
      setProduccionesCompletas(allProds);
    } catch (e) {
      console.error("❌ Error cargando producciones:", e);
      setProduccionesCompletas([]);
      setMessage?.({
        open: true,
        severity: "error",
        text: "Error al cargar producciones.",
      });
    } finally {
      setLoadingProduccionesCompletas(false);
    }
  };

  const handleOpenPedidoSearch = () => {
    setPedidoSearchOpen(true);
    setPedidoSearchNombre("");
    // cargar desde estado existente
    setPedidosCompletos(pedidos);
  };

  const handleClosePedidoSearch = () => {
    setPedidoSearchOpen(false);
    setPedidoSearchNombre("");
    setPedidosCompletos([]);
  };

  const getFilteredPedidos = () => {
    return pedidosCompletos.filter((ped) => {
      const nombre = (ped.nombre || ped.name || "").toLowerCase();
      const search = (pedidoSearchNombre || "").toLowerCase();
      return nombre.includes(search);
    });
  };

  const handleSelectPedido = (ped) => {
    setFormData((prev) => ({ ...prev, pedidoId: ped.id }));
    setPedidoSearchOpen(false);
    setPedidoSearchNombre("");
    setPedidosCompletos([]);
  };
  /* ============================
     🔹 Filtrar producciones localmente
  ============================ */
  const getFilteredProducciones = () => {
    const search = (produccionSearchNombre || "").toLowerCase();
    return produccionesCompletas.filter((prod) => {
      const nombre = (prod.nombre || prod.name || "").toLowerCase();
      return nombre.includes(search);
    });
  };

  const handleSelectProduccion = (prod) => {
    setFormData((prev) => ({
      ...prev,
      produccionId: prod.id,
    }));
    setProduccionSearchOpen(false);
    setProduccionSearchNombre("");
    setProduccionesCompletas([]);
  };

  const handleCloseProduccionSearch = () => {
    setProduccionSearchOpen(false);
    setProduccionSearchNombre("");
    setProduccionesCompletas([]);
  };

  const handleSubmit = async () => {
    try {
      await kardexSchema.validate(formData, { abortEarly: false });
      setErrors({});

      const isEntradaCompra =
        Number(formData.tipoMovimientoId) === TIPO_MOV_ENTRADA_COMPRA &&
        !!formData.ordenCompraId;

      // Si es entrada por compra, validar items
      if (isEntradaCompra) {
        if (!ocItems.length) {
          setMessage?.({
            open: true,
            severity: "warning",
            text: "No hay ítems de la orden de compra para recepcionar.",
          });
          return;
        }

        const itemsConCantidad = ocItems.filter(
          (it) => Number(it.cantidadARecibir) > 0
        );

        if (!itemsConCantidad.length) {
          setMessage?.({
            open: true,
            severity: "warning",
            text: "Debe ingresar cantidad a recepcionar al menos en un ítem.",
          });
          return;
        }

        // Validación adicional: cantidadARecibir <= cantidadPendiente
        const invalido = itemsConCantidad.find(
          (it) => it.cantidadARecibir > it.cantidadPendiente
        );
        if (invalido) {
          setMessage?.({
            open: true,
            severity: "warning",
            text: `La cantidad a recepcionar del ítem ${invalido.id} excede lo pendiente.`,
          });
          return;
        }

        // Payload para /kardex/entrada-compra
        const body = {
          ordenCompraId: formData.ordenCompraId,
          almacenId: formData.almacenId,
          fechaHora: formData.fechaHora,
          descripcion: formData.descripcion,
          items: itemsConCantidad.map((it) => ({
            ordenCompraItemId: it.id,
            cantidad: it.cantidadARecibir,
          })),
        };

        await axios.post("/v1/kardex/entrada-compra", body, headers);

        reloadData?.();
        setMessage?.({
          open: true,
          severity: "success",
          text: "Entrada de compra registrada y orden de compra actualizada.",
        });
        setOpen(false);
        setSelectedRow(null);
        return;
      }

      // 🔹 Flujo Kardex genérico (lo que ya tenías)
      const payload = {
        ...formData,
        pedidoId: formData.pedidoId || null,
        ordenCompraId: formData.ordenCompraId || null,
        clienteProveedorId: formData.clienteProveedorId || null,
      };

      const method = formMode === "edit" ? axios.put : axios.post;
      const url =
        formMode === "edit" ? `/v1/kardex/${payload.id}` : "/v1/kardex";

      await method(url, payload, headers);

      reloadData?.();
      setMessage?.({
        open: true,
        severity: "success",
        text: `Kardex ${formMode === "edit" ? "actualizado" : "creado"
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
        text: "Error al guardar Kardex / entrada de compra.",
      });
    }
  };

  /* ============================
     🔹 Render Names
  ============================ */
  const renderName = (it) =>
    it?.name ?? it?.nombre ?? it?.descripcion ?? `#${it?.id}`;

  const isEntradaCompraUi =
    Number(formData.tipoMovimientoId) === TIPO_MOV_ENTRADA_COMPRA;

  /* ============================
     🔹 UI
  ============================ */
  return (
    <Box>
      <Dialog open={open} onClose={() => setOpen(false)} fullWidth maxWidth="md">
        <DialogTitle>
          {formMode === "edit" ? "Editar Kardex" : "Crear Kardex"}
        </DialogTitle>

        <DialogContent sx={{ mt: 1 }}>
          <Grid container spacing={2}>
            {/* Fecha/Hora + Tipo movimiento */}
            <Grid item xs={12} sm={6}>
              <TextField
                label="Fecha/Hora"
                type="datetime-local"
                name="fechaHora"
                value={formData.fechaHora}
                onChange={handleChange}
                InputLabelProps={{
                  shrink: true,
                  sx: {
                    ml: "5px",
                    mt: "5px",
                  }
                }}
                error={!!errors.fechaHora}
                helperText={errors.fechaHora}
                fullWidth
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <FormControl fullWidth error={!!errors.tipoMovimientoId}>
                <InputLabel>Tipo Movimiento</InputLabel>
                <Select
                  name="tipoMovimientoId"
                  label="Tipo Movimiento"
                  value={formData.tipoMovimientoId}
                  onChange={handleChange}
                >
                  <MenuItem value="">
                    <em>Seleccione...</em>
                  </MenuItem>
                  {tiposMovimiento.map((t) => (
                    <MenuItem key={t.id} value={t.id}>
                      {renderName(t)}
                    </MenuItem>
                  ))}
                </Select>
                <FormHelperText>{errors.tipoMovimientoId}</FormHelperText>
              </FormControl>
            </Grid>

            {/* Almacén + Producción */}
            <Grid item xs={12} sm={6}>
              <FormControl fullWidth error={!!errors.almacenId}>
                <InputLabel>Almacén</InputLabel>
                <Select
                  name="almacenId"
                  label="Almacén"
                  value={formData.almacenId}
                  onChange={handleChange}
                >
                  <MenuItem value="">
                    <em>Seleccione...</em>
                  </MenuItem>
                  {almacenes.map((a) => (
                    <MenuItem key={a.id} value={a.id}>
                      {renderName(a)}
                    </MenuItem>
                  ))}
                </Select>
                <FormHelperText>{errors.almacenId}</FormHelperText>
              </FormControl>
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Producción"
                value={
                  formData.produccionId
                    ? producciones.find((p) => p.id === formData.produccionId)
                      ?.name || `ID: ${formData.produccionId}`
                    : ""
                }
                InputProps={{
                  readOnly: true,
                  endAdornment: (
                    <InputAdornment position="end">
                      <Button
                        size="small"
                        onClick={handleOpenProduccionSearch}
                        sx={{ minWidth: "40px", p: 0.5 }}
                      >
                        <SearchIcon fontSize="small" />
                      </Button>
                    </InputAdornment>
                  ),
                }}
                error={!!errors.produccionId}
                helperText={errors.produccionId}
              />
            </Grid>

            {/* Pedido + Orden compra */}
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Pedido"
                value={
                  formData.pedidoId
                    ? pedidos.find((p) => p.id === formData.pedidoId)
                      ?.nombre ||
                    pedidos.find((p) => p.id === formData.pedidoId)?.name ||
                    `ID: ${formData.pedidoId}`
                    : ""
                }
                InputProps={{
                  readOnly: true,
                  endAdornment: (
                    <InputAdornment position="end">
                      <Button
                        size="small"
                        onClick={handleOpenPedidoSearch}
                        sx={{ minWidth: "40px", p: 0.5 }}
                      >
                        <SearchIcon fontSize="small" />
                      </Button>
                    </InputAdornment>
                  ),
                }}
                error={!!errors.pedidoId}
                helperText={errors.pedidoId}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <FormControl
                fullWidth
                error={!!errors.ordenCompraId && isEntradaCompraUi}
              >
                <InputLabel>Orden de Compra</InputLabel>
                <Select
                  name="ordenCompraId"
                  label="Orden de Compra"
                  value={formData.ordenCompraId}
                  onChange={handleChange}
                >
                  <MenuItem value="">
                    <em>Sin orden asociada</em>
                  </MenuItem>
                  {ordenesCompra.map((o) => (
                    <MenuItem key={o.id} value={o.id}>
                      {renderName(o)}
                    </MenuItem>
                  ))}
                </Select>
                <FormHelperText>
                  {isEntradaCompraUi ? errors.ordenCompraId : ""}
                </FormHelperText>
              </FormControl>
            </Grid>

            {/* Cliente/proveedor (full row) */}
            <Grid item xs={12}>
              <FormControl fullWidth>
                <InputLabel>Cliente / Proveedor</InputLabel>
                <Select
                  name="clienteProveedorId"
                  label="Cliente / Proveedor"
                  value={formData.clienteProveedorId}
                  onChange={handleChange}
                >
                  <MenuItem value="">
                    <em>Sin cliente/proveedor</em>
                  </MenuItem>
                  {empresas.map((e) => (
                    <MenuItem key={e.id} value={e.id}>
                      {renderName(e)}
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>
            </Grid>

            {/* Descripción (full row) */}
            <Grid item xs={12}>
              <TextField
                fullWidth
                multiline
                label="Descripción"
                name="descripcion"
                value={formData.descripcion}
                onChange={handleChange}
                error={!!errors.descripcion}
                helperText={errors.descripcion}
                minRows={3}
              />
            </Grid>

            {/* Estado */}
            <Grid item xs={12} sm={6}>
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
            </Grid>
          </Grid>

          {/* ============================
              🔹 Tabla de ítems OC
              Solo si es ENTRADA COMPRA
          ============================ */}
          {isEntradaCompraUi && formData.ordenCompraId && (
            <Box mt={2}>
              <Typography variant="subtitle1" sx={{ mb: 1 }}>
                Ítems de la Orden de Compra
              </Typography>

              {loadingOcItems ? (
                <Typography variant="body2">Cargando ítems...</Typography>
              ) : !ocItems.length ? (
                <Typography variant="body2">
                  No hay ítems para esta orden de compra o ya están totalmente
                  recibidos.
                </Typography>
              ) : (
                <Table size="small">
                  <TableHead>
                    <TableRow>
                      <TableCell>Producto</TableCell>
                      <TableCell align="right">Pedida</TableCell>
                      <TableCell align="right">Recibida</TableCell>
                      <TableCell align="right">Pendiente</TableCell>
                      <TableCell align="right">A recepcionar</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {ocItems.map((it) => (
                      <TableRow key={it.id}>
                        <TableCell>{it.producto}</TableCell>
                        <TableCell align="right">
                          {it.cantidadPedida}
                        </TableCell>
                        <TableCell align="right">
                          {it.cantidadRecibida}
                        </TableCell>
                        <TableCell align="right">
                          {it.cantidadPendiente}
                        </TableCell>
                        <TableCell align="right">
                          <TextField
                            type="number"
                            size="small"
                            inputProps={{
                              min: 0,
                              max: it.cantidadPendiente,
                              step: "0.01",
                            }}
                            value={
                              it.cantidadARecibir === ""
                                ? ""
                                : it.cantidadARecibir
                            }
                            onChange={(e) =>
                              handleChangeOcItemQty(it.id, e.target.value)
                            }
                            sx={{ width: 100 }}
                          />
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              )}
            </Box>
          )}
        </DialogContent>

        <DialogActions>
          <Button onClick={() => setOpen(false)}>Cancelar</Button>
          <Button onClick={handleSubmit}>Guardar</Button>
        </DialogActions>
      </Dialog>

      {/* ============================
          🔹 Modal Búsqueda Producciones
      ============================ */}
      <Dialog
        open={produccionSearchOpen}
        onClose={handleCloseProduccionSearch}
        fullWidth
        maxWidth="md"
      >
        <DialogTitle>Buscar Producción</DialogTitle>
        <DialogContent sx={{ mt: 2 }}>
          <Grid container spacing={2} sx={{ mb: 2 }} justifyContent="center">
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Nombre"
                value={produccionSearchNombre}
                onChange={(e) => setProduccionSearchNombre(e.target.value)}
                placeholder="Escribe para filtrar por nombre..."
              />
            </Grid>
          </Grid>

          {/* Tabla de resultados */}
          {loadingProduccionesCompletas ? (
            <Box sx={{ display: "flex", justifyContent: "center", py: 3 }}>
              <CircularProgress />
            </Box>
          ) : getFilteredProducciones().length === 0 ? (
            <Typography variant="body2" sx={{ py: 2 }}>
              {produccionesCompletas.length === 0
                ? "No hay producciones disponibles."
                : "No hay producciones que coincidan con los filtros."}
            </Typography>
          ) : (
            <Table size="small">
              <TableHead>
                <TableRow>
                  <TableCell>Nombre</TableCell>
                  <TableCell align="center">Acción</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {getFilteredProducciones().map((prod) => (
                  <TableRow key={prod.id}>
                    <TableCell>{prod.nombre || prod.name || prod.id}</TableCell>
                    <TableCell align="center">
                      <Button
                        size="small"
                        variant="contained"
                        onClick={() => handleSelectProduccion(prod)}
                      >
                        Seleccionar
                      </Button>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseProduccionSearch}>Cancelar</Button>
        </DialogActions>
      </Dialog>

      {/* ============================
          🔹 Modal Búsqueda Pedidos
      ============================ */}
      <Dialog
        open={pedidoSearchOpen}
        onClose={handleClosePedidoSearch}
        fullWidth
        maxWidth="md"
      >
        <DialogTitle>Buscar Pedido</DialogTitle>
        <DialogContent sx={{ mt: 2 }}>
          <Grid container spacing={2} sx={{ mb: 2 }} justifyContent="center">
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Nombre"
                value={pedidoSearchNombre}
                onChange={(e) => setPedidoSearchNombre(e.target.value)}
                placeholder="Escribe para filtrar por nombre..."
              />
            </Grid>
          </Grid>

          {/* Tabla de resultados */}
          {getFilteredPedidos().length === 0 ? (
            <Typography variant="body2" sx={{ py: 2 }}>
              {pedidosCompletos.length === 0
                ? "No hay pedidos disponibles."
                : "No hay pedidos que coincidan con el filtro."}
            </Typography>
          ) : (
            <Table size="small">
              <TableHead>
                <TableRow>
                  <TableCell>Nombre</TableCell>
                  <TableCell align="center">Acción</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {getFilteredPedidos().map((ped) => (
                  <TableRow key={ped.id}>
                    <TableCell>{ped.nombre || ped.name || ped.id}</TableCell>
                    <TableCell align="center">
                      <Button
                        size="small"
                        variant="contained"
                        onClick={() => handleSelectPedido(ped)}
                      >
                        Seleccionar
                      </Button>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClosePedidoSearch}>Cancelar</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}
