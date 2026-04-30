import React, { useEffect, useMemo, useState } from "react";
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
  Grid,
} from "@mui/material";
import axios from "../axiosConfig";
import * as Yup from "yup";
import GridArticuloKardex from "./GridArticuloKardex";
import { resolveKardexId } from "./utils/kardexFormatters";

const numberRequired = (msg, opts = {}) => {
  let y = Yup.number().typeError(msg).required(msg);
  if (opts.min !== undefined) y = y.min(opts.min, `${msg} (min ${opts.min})`);
  return y;
};

const kardexSchema = Yup.object({
  fechaHora: Yup.string().required("Fecha/Hora obligatoria."),
  almacenId: numberRequired("Almacen obligatorio.", { min: 1 }),
  produccionId: numberRequired("Produccion obligatoria.", { min: 1 }),
  tipoMovimientoId: numberRequired("Tipo de movimiento obligatorio.", { min: 1 }),
  descripcion: Yup.string().max(500, "Max 500 caracteres."),
  pedidoId: Yup.number().nullable().transform((v, o) => (o === "" ? null : v)),
  ordenCompraId: Yup.number().nullable().transform((v, o) => (o === "" ? null : v)),
  clienteProveedorId: Yup.number().nullable().transform((v, o) => (o === "" ? null : v)),
});

const pickList = (res) => {
  const d = res?.data;
  if (Array.isArray(d)) return d;
  if (Array.isArray(d?.data)) return d.data;
  if (Array.isArray(d?.content)) return d.content;
  if (Array.isArray(d?.data?.content)) return d.data.content;
  return [];
};

const normalize = (v) =>
  String(v ?? "")
    .trim()
    .toLowerCase()
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "");

const renderName = (it) => it?.name ?? it?.nombre ?? it?.descripcion ?? `#${it?.id}`;

const renderPresentacion = (it) => {
  const p = it?.producto?.nombre ?? it?.producto?.name ?? it?.productoNombre ?? "";
  const pr =
    it?.presentacion?.nombre ??
    it?.presentacion?.name ??
    it?.presentacionNombre ??
    it?.nombre ??
    it?.name ??
    "";
  return [p, pr].filter(Boolean).join(" - ") || `#${it?.id}`;
};

const extractApiMessage = (err, fallback) => {
  const data = err?.response?.data;
  return (typeof data === "string" && data) || data?.message || data?.detail || data?.title || fallback;
};

const toDateTimeLocal = (val) => {
  if (!val) return "";
  const d = new Date(val);
  if (Number.isNaN(d.getTime())) return "";
  const pad = (n) => String(n).padStart(2, "0");
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}`;
};

const newArticleDraft = () => ({
  id: null,
  kardexItemId: `tmp-${Date.now()}-${Math.random().toString(36).slice(2, 8)}`,
  presentacionProductoId: "",
  productoId: "",
  cantidad: "",
  precio: "",
  lote: "",
  fechaVencimiento: "",
  devolutivo: false,
  responsableId: null,
  estadoId: 1,
});

const toNumericIdOrNull = (value) => {
  if (value === null || value === undefined || value === "") return null;
  const n = Number(value);
  return Number.isFinite(n) ? n : null;
};

const isPresentacionDevolutiva = (p) =>
  Boolean(
    p?.desgregar ??
      p?.desagregar ??
      p?.devolutivo ??
      p?.esDevolutivo ??
      p?.requiereResponsable ??
      p?.producto?.devolutivo ??
      p?.producto?.esDevolutivo
  );

export default function FormKardex({
  open,
  setOpen,
  formMode = "create",
  startInArticles = false,
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
  });
  const [errors, setErrors] = useState({});

  const [almacenes, setAlmacenes] = useState([]);
  const [producciones, setProducciones] = useState([]);
  const [tiposMovimiento, setTiposMovimiento] = useState([]);
  const [productos, setProductos] = useState([]);
  const [pedidos, setPedidos] = useState([]);
  const [ordenesCompra, setOrdenesCompra] = useState([]);
  const [ordenesCompraByPedido, setOrdenesCompraByPedido] = useState([]);
  const [empresas, setEmpresas] = useState([]);
  const [presentaciones, setPresentaciones] = useState([]);
  const [responsables, setResponsables] = useState([]);

  const [draftItems, setDraftItems] = useState([]);
  const [articleModalOpen, setArticleModalOpen] = useState(false);
  const [articleSelectedRow, setArticleSelectedRow] = useState(null);

  const [articleFormOpen, setArticleFormOpen] = useState(false);
  const [articleFormMode, setArticleFormMode] = useState("create");
  const [articleFormData, setArticleFormData] = useState(newArticleDraft());

  const token = localStorage.getItem("token");
  const headers = token ? { headers: { Authorization: `Bearer ${token}` } } : {};

  const findIdByName = (items, rawName) => {
    const wanted = normalize(rawName);
    if (!wanted) return "";
    const found = (items || []).find((it) => normalize(renderName(it)) === wanted);
    return found?.id ?? "";
  };

  const getProductoIdFromPresentacionObj = (p) =>
    p?.producto?.id ??
    p?.productoId ??
    p?.producto_id ??
    p?.proId ??
    p?.idProducto ??
    p?.producto?.productoId ??
    p?.producto_presentacion?.productoId ??
    "";

  const getProductoIdFromPresentacion = (presentacionProductoId) => {
    const found = (presentaciones || []).find((p) => String(p?.id) === String(presentacionProductoId));
    return getProductoIdFromPresentacionObj(found);
  };

  const fetchProductoIdByPresentacion = async (presentacionProductoId) => {
    if (!presentacionProductoId) return "";
    try {
      const res = await axios.get(`/v1/producto_presentacion/${presentacionProductoId}`, headers);
      const pp = res?.data ?? {};
      return (
        pp?.producto?.id ??
        pp?.productoId ??
        pp?.producto_id ??
        pp?.proId ??
        pp?.idProducto ??
        pp?.producto?.productoId ??
        ""
      );
    } catch {
      return "";
    }
  };

  const fetchPresentacionDetalle = async (presentacionProductoId) => {
    if (!presentacionProductoId) return null;
    try {
      const res = await axios.get(`/v1/producto_presentacion/${presentacionProductoId}`, headers);
      return res?.data ?? null;
    } catch {
      return null;
    }
  };

  const getDevolutivoByPresentacionId = (presentacionProductoId, fallback = false) => {
    const pres = (presentaciones || []).find((p) => String(p?.id) === String(presentacionProductoId));
    if (!pres) return Boolean(fallback);
    return isPresentacionDevolutiva(pres);
  };

  const presentacionesByProducto = useMemo(() => {
    const pid = articleFormData?.productoId;
    if (!pid) return presentaciones;
    const filtered = (presentaciones || []).filter(
      (p) => String(getProductoIdFromPresentacionObj(p)) === String(pid)
    );
    return filtered.length ? filtered : presentaciones;
  }, [presentaciones, articleFormData?.productoId]);

  const presentacionSeleccionada = useMemo(
    () =>
      (presentaciones || []).find(
        (p) => String(p?.id) === String(articleFormData?.presentacionProductoId)
      ) ?? null,
    [presentaciones, articleFormData?.presentacionProductoId]
  );

  const tipoMovimientoSeleccionado = useMemo(
    () => tiposMovimiento.find((t) => String(t.id) === String(formData.tipoMovimientoId)) ?? null,
    [tiposMovimiento, formData.tipoMovimientoId]
  );

  const isEntradaUi = normalize(renderName(tipoMovimientoSeleccionado)).includes("entrada");

  const getPedidoIdFromOc = (oc) =>
    oc?.pedidoId ?? oc?.pedido_id ?? oc?.ped_id ?? oc?.orc_pedido_id ?? oc?.pedido?.id ?? null;

  const ordenesCompraFiltradas = useMemo(() => {
    if (!isEntradaUi || !formData.pedidoId) return [];
    if (ordenesCompraByPedido.length) return ordenesCompraByPedido;
    return ordenesCompra.filter((oc) => String(getPedidoIdFromOc(oc)) === String(formData.pedidoId));
  }, [isEntradaUi, formData.pedidoId, ordenesCompra, ordenesCompraByPedido]);

  useEffect(() => {
    if (!open && !articleModalOpen) return;

    const loadLists = async () => {
      const reqs = await Promise.allSettled([
        axios.get("/v1/items/tipo_movimiento/0", headers),
        axios.get("/v1/items/produccion/0", headers),
        axios.get("/v1/items/almacen/0", headers),
        axios.get("/v1/items/producto/0", headers),
        axios.get("/v1/items/pedido/0", headers),
        axios.get("/v1/items/orden_compra/0", headers),
        axios.get("/v1/items/empresa/0", headers),
        axios.get("/v1/items/proveedor/0", headers),
        axios.get("/v1/items/producto_presentacion/0", headers),
        axios.get("/v1/items/usuario_empresa/0", headers),
      ]);

      const pick = (idx) => (reqs[idx]?.status === "fulfilled" ? pickList(reqs[idx].value) : []);

      setTiposMovimiento(pick(0));
      setProducciones(pick(1));
      setAlmacenes(pick(2));
      setProductos(pick(3));
      setPedidos(pick(4));
      setOrdenesCompra(pick(5));

      const empresasBase = pick(6);
      const proveedoresBase = pick(7);
      const merged = [...empresasBase];
      const seen = new Set(empresasBase.map((e) => String(e?.id)));
      for (const p of proveedoresBase) {
        const id = p?.id;
        if (id == null || seen.has(String(id))) continue;
        merged.push(p);
        seen.add(String(id));
      }
      setEmpresas(merged);
      setPresentaciones(pick(8));
      setResponsables(pick(9));
    };

    loadLists();
  }, [open, articleModalOpen]);

  useEffect(() => {
    if (!articleFormOpen) return;
    if (articleFormData?.productoId || !articleFormData?.presentacionProductoId) return;
    const pid = getProductoIdFromPresentacion(articleFormData.presentacionProductoId);
    if (pid) {
      setArticleFormData((prev) => ({ ...prev, productoId: Number(pid) }));
    }
  }, [articleFormOpen, articleFormData?.productoId, articleFormData?.presentacionProductoId, presentaciones]);

  useEffect(() => {
    if (!open) return;

    const loadEditData = async () => {
      if (formMode !== "edit") {
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
        });
        setDraftItems([]);
        setArticleSelectedRow(null);
        return;
      }

      const kardexId = resolveKardexId(selectedRow);
      if (!kardexId) return;

      try {
        const res = await axios.get(`/v1/kardex/${kardexId}/update-form`, headers);
        const data = res?.data ?? {};

        setFormData({
          id: data.id ?? kardexId,
          fechaHora: toDateTimeLocal(selectedRow?.fechaHora),
          almacenId: data.almacenId ?? "",
          produccionId: data.produccionId ?? "",
          tipoMovimientoId: data.tipoMovimientoId ?? findIdByName(tiposMovimiento, selectedRow?.nombreTipoMovimiento),
          pedidoId: data.pedidoId ?? "",
          ordenCompraId: data.ordenCompraId ?? "",
          clienteProveedorId: data.clienteProveedorId ?? "",
          descripcion: data.descripcion ?? "",
        });

        const mapped = (data.items || []).map((it, idx) => ({
          id: it?.id ?? null,
          kardexItemId: it?.id ?? `tmp-edit-${idx}-${Date.now()}`,
          presentacionProductoId: Number(it?.presentacionProductoId ?? 0),
          productoId: "",
          cantidad: Number(it?.cantidad ?? 0),
          precio: Number(it?.precio ?? 0),
          lote: it?.lote || "",
          fechaVencimiento: it?.fechaVencimiento ? String(it.fechaVencimiento).substring(0, 10) : "",
          devolutivo: Boolean(it?.devolutivo ?? false),
          responsableId: it?.responsableId ? Number(it.responsableId) : null,
          estadoId: it?.estadoId ?? 1,
        }));

        setDraftItems(mapped);
      } catch {
        const kardexIdFallback = resolveKardexId(selectedRow);
        setFormData({
          id: kardexIdFallback,
          fechaHora: toDateTimeLocal(selectedRow?.fechaHora),
          almacenId: findIdByName(almacenes, selectedRow?.nombreAlmacen),
          produccionId: findIdByName(producciones, selectedRow?.nombreProduccion),
          tipoMovimientoId: findIdByName(tiposMovimiento, selectedRow?.nombreTipoMovimiento),
          pedidoId: "",
          ordenCompraId: "",
          clienteProveedorId: findIdByName(empresas, selectedRow?.nombreClienteProveedor),
          descripcion: "",
        });

        try {
          const itemsRes = await axios.get(`/v1/kardex/${kardexIdFallback}/items`, {
            ...headers,
            params: { page: 0, size: 200, sort: "id,desc" },
          });
          const content = pickList(itemsRes);
          const mapped = content.map((it, idx) => ({
            id: it?.id ?? null,
            kardexItemId: it?.id ?? `tmp-fallback-${idx}-${Date.now()}`,
            presentacionProductoId: Number(it?.presentacionProductoId ?? 0),
            productoId: "",
            cantidad: Number(it?.cantidad ?? 0),
            precio: Number(it?.precio ?? 0),
            lote: it?.lote || "",
            fechaVencimiento: it?.fechaVencimiento ? String(it.fechaVencimiento).substring(0, 10) : "",
            devolutivo: Boolean(it?.devolutivo ?? false),
            responsableId: it?.responsableId ? Number(it.responsableId) : null,
            estadoId: it?.estadoId ?? 1,
          }));
          setDraftItems(mapped);
        } catch {
          setDraftItems([]);
        }
      }
    };

    loadEditData();
  }, [open, formMode, selectedRow, tiposMovimiento, almacenes, producciones, empresas]);

  useEffect(() => {
    if (!open || !isEntradaUi || !formData.pedidoId) {
      setOrdenesCompraByPedido([]);
      return;
    }

    const localFiltered = ordenesCompra.filter((oc) => String(getPedidoIdFromOc(oc)) === String(formData.pedidoId));
    if (localFiltered.length) {
      setOrdenesCompraByPedido(localFiltered);
      return;
    }

    const fetchByPedido = async () => {
      try {
        const res = await axios.get(`/v1/orden-compra/pedido/${Number(formData.pedidoId)}/lookup`, headers);
        setOrdenesCompraByPedido(pickList(res));
      } catch {
        setOrdenesCompraByPedido([]);
      }
    };

    fetchByPedido();
  }, [open, isEntradaUi, formData.pedidoId, ordenesCompra]);

  useEffect(() => {
    if (!open || !startInArticles || formMode !== "edit") return;
    setArticleModalOpen(true);
  }, [open, startInArticles, formMode]);

  const handleHeaderChange = (e) => {
    const { name, value } = e.target;
    const numeric = [
      "almacenId",
      "produccionId",
      "tipoMovimientoId",
      "pedidoId",
      "ordenCompraId",
      "clienteProveedorId",
    ];
    const castValue = numeric.includes(name) && value !== "" ? Number(value) : value;

    setFormData((prev) => {
      const next = { ...prev, [name]: castValue };
      if (name === "pedidoId") next.ordenCompraId = "";
      if (name === "tipoMovimientoId") {
        const tipoSel = tiposMovimiento.find((t) => String(t.id) === String(castValue));
        const esEntrada = normalize(renderName(tipoSel)).includes("entrada");
        if (!esEntrada) {
          next.pedidoId = "";
          next.ordenCompraId = "";
        }
      }
      return next;
    });

    setErrors((prev) => ({ ...prev, [name]: undefined }));
  };

  const mapDraftItemsToPayload = () =>
    (draftItems || []).map((it) => {
      const mapped = {
        presentacionProductoId: Number(it?.presentacionProductoId ?? 0),
        cantidad: Number(it?.cantidad ?? 0),
        precio: Number(it?.precio ?? 0),
        devolutivo: Boolean(it?.devolutivo ?? false),
        responsableId: toNumericIdOrNull(it?.responsableId),
        lote: it?.lote || null,
        fechaVencimiento: it?.fechaVencimiento ? String(it.fechaVencimiento).substring(0, 10) : null,
      };
      const parsedId = toNumericIdOrNull(it?.id);
      if (parsedId != null) {
        mapped.id = parsedId;
      }
      return mapped;
    });

  const buildHeaderPayload = () => ({
    tipoMovimientoId: formData.tipoMovimientoId ? Number(formData.tipoMovimientoId) : null,
    almacenId: formData.almacenId ? Number(formData.almacenId) : null,
    almacenDestinoId: null,
    pedidoId: formData.pedidoId ? Number(formData.pedidoId) : null,
    ordenCompraId: formData.ordenCompraId ? Number(formData.ordenCompraId) : null,
    produccionId: formData.produccionId ? Number(formData.produccionId) : null,
    clienteProveedorId: formData.clienteProveedorId ? Number(formData.clienteProveedorId) : null,
    descripcion: formData.descripcion || null,
  });

  const handleNext = async () => {
    try {
      await kardexSchema.validate(formData, { abortEarly: false });
      setErrors({});
      setOpen(false);
      setArticleModalOpen(true);
    } catch (err) {
      if (err.name === "ValidationError") {
        const map = {};
        err.inner.forEach((e) => {
          if (e.path && !map[e.path]) map[e.path] = e.message;
        });
        setErrors(map);
      }
    }
  };

  const handleOpenCreateArticle = () => {
    setArticleFormMode("create");
    setArticleFormData(newArticleDraft());
    setArticleFormOpen(true);
  };

  const handleOpenEditArticle = () => {
    if (!articleSelectedRow) return;
    const selectedKey = String(articleSelectedRow?.id ?? articleSelectedRow?.kardexItemId ?? "");
    const latestRow =
      draftItems.find((it) => String(it?.id ?? it?.kardexItemId ?? "") === selectedKey) ?? articleSelectedRow;
    const productoId = getProductoIdFromPresentacion(latestRow.presentacionProductoId);
    const devolutivoFromPresentacion = getDevolutivoByPresentacionId(
      latestRow.presentacionProductoId,
      latestRow?.devolutivo
    );

    setArticleFormMode("edit");
    setArticleFormData({
      ...newArticleDraft(),
      ...latestRow,
      productoId: productoId || "",
      cantidad: latestRow?.cantidad ?? "",
      precio: latestRow?.precio ?? "",
      lote: latestRow?.lote ?? "",
      devolutivo: Boolean(devolutivoFromPresentacion),
      responsableId: Boolean(devolutivoFromPresentacion) ? toNumericIdOrNull(latestRow?.responsableId) : null,
      fechaVencimiento: latestRow?.fechaVencimiento
        ? String(latestRow.fechaVencimiento).substring(0, 10)
        : "",
    });
    setArticleFormOpen(true);

    if (!productoId && latestRow?.presentacionProductoId) {
      fetchProductoIdByPresentacion(latestRow.presentacionProductoId).then((pid) => {
        if (pid) {
          setArticleFormData((prev) => ({ ...prev, productoId: Number(pid) }));
        }
      });
    }
  };

  const handleDeleteArticle = () => {
    if (!articleSelectedRow) return;
    const key = articleSelectedRow?.id ?? articleSelectedRow?.kardexItemId;
    setDraftItems((prev) => prev.filter((it) => (it?.id ?? it?.kardexItemId) !== key));
    setArticleSelectedRow(null);
  };

  const handleArticleFormChange = (e) => {
    const { name, value } = e.target;
    setArticleFormData((prev) => {
      const next = { ...prev };

      if (name === "productoId") {
        next.productoId = value === "" ? "" : Number(value);
        next.presentacionProductoId = "";
        next.devolutivo = false;
        next.responsableId = null;
      } else if (["presentacionProductoId", "cantidad", "precio"].includes(name)) {
        next[name] = value === "" ? "" : Number(value);
        if (name === "presentacionProductoId") {
          const pres = (presentaciones || []).find((p) => String(p?.id) === String(next.presentacionProductoId));
          next.devolutivo = isPresentacionDevolutiva(pres);
          next.responsableId = null;
        }
      } else if (name === "responsableId") {
        next.responsableId = value === "" ? null : Number(value);
      } else {
        next[name] = value;
      }

      return next;
    });
  };

  const handleSaveArticleDraft = () => {
    const presentacionProductoId = Number(articleFormData.presentacionProductoId || 0);
    const cantidad = Number(articleFormData.cantidad || 0);
    const precio = Number(articleFormData.precio || 0);

    if (!presentacionProductoId || cantidad <= 0 || Number.isNaN(precio) || precio < 0) {
      setMessage({
        open: true,
        severity: "warning",
        text: "Debes diligenciar presentacion, cantidad y precio validos.",
      });
      return;
    }

    const devolutivo = Boolean(
      articleFormData?.devolutivo ?? isPresentacionDevolutiva(presentacionSeleccionada)
    );
    if (devolutivo && !toNumericIdOrNull(articleFormData?.responsableId)) {
      setMessage({
        open: true,
        severity: "warning",
        text: "La presentacion seleccionada es devolutiva y requiere responsable asignado.",
      });
      return;
    }

    const payload = {
      ...articleFormData,
      presentacionProductoId,
      cantidad,
      precio,
      devolutivo,
      responsableId: toNumericIdOrNull(articleFormData?.responsableId),
      fechaVencimiento: articleFormData.fechaVencimiento
        ? String(articleFormData.fechaVencimiento).substring(0, 10)
        : null,
      lote: articleFormData.lote || null,
    };

    setDraftItems((prev) => {
      if (articleFormMode === "edit") {
        const key = articleFormData?.id ?? articleFormData?.kardexItemId;
        const nextRows = prev.map((it) =>
          String(it?.id ?? it?.kardexItemId) === String(key) ? { ...it, ...payload } : it
        );
        const refreshed = nextRows.find((it) => String(it?.id ?? it?.kardexItemId) === String(key)) ?? null;
        setArticleSelectedRow(refreshed);
        return nextRows;
      }
      const createdRow = {
        ...newArticleDraft(),
        ...payload,
        id: null,
        kardexItemId: `tmp-${Date.now()}-${Math.random().toString(36).slice(2, 8)}`,
        estadoId: 1,
      };
      setArticleSelectedRow(createdRow);
      return [
        ...prev,
        createdRow,
      ];
    });

    setArticleFormOpen(false);
  };

  useEffect(() => {
    if (!articleFormOpen) return;
    const ppId = articleFormData?.presentacionProductoId;
    if (!ppId) return;

    const localPres = (presentaciones || []).find((p) => String(p?.id) === String(ppId));
    if (
      localPres &&
      (localPres?.desgregar !== undefined ||
        localPres?.desagregar !== undefined ||
        localPres?.devolutivo !== undefined)
    )
      return;

    let active = true;
    fetchPresentacionDetalle(ppId).then((detalle) => {
      if (!active || !detalle) return;
      const devolutivo = isPresentacionDevolutiva(detalle);
      setArticleFormData((prev) => ({
        ...prev,
        devolutivo,
        responsableId: devolutivo ? prev?.responsableId : null,
      }));
    });
    return () => {
      active = false;
    };
  }, [articleFormOpen, articleFormData?.presentacionProductoId, presentaciones]);

  useEffect(() => {
    if (!presentaciones.length) return;
    setDraftItems((prev) =>
      (prev || []).map((it) => {
        const devolutivo = getDevolutivoByPresentacionId(it?.presentacionProductoId, it?.devolutivo);
        return {
          ...it,
          devolutivo,
          responsableId: devolutivo ? toNumericIdOrNull(it?.responsableId) : null,
        };
      })
    );
  }, [presentaciones]);

  const handleSaveKardex = async () => {
    const items = mapDraftItemsToPayload();
    if (!items.length) {
      setMessage({
        open: true,
        severity: "warning",
        text: "Debes registrar al menos un articulo en el Kardex.",
      });
      return;
    }

    const missingResponsable = items.find((it) => Boolean(it?.devolutivo) && !toNumericIdOrNull(it?.responsableId));
    if (missingResponsable) {
      setMessage({
        open: true,
        severity: "warning",
        text: "Hay articulos devolutivos sin responsable asignado. Completa ese dato antes de guardar.",
      });
      return;
    }

    const payload = {
      ...buildHeaderPayload(),
      items,
    };

    try {
      if (formMode === "edit") {
        await axios.put(`/v1/kardex/${formData.id}`, payload, headers);
      } else {
        await axios.post("/v1/kardex/movimientos", payload, headers);
      }

      reloadData?.();
      setMessage({
        open: true,
        severity: "success",
        text: `Kardex ${formMode === "edit" ? "actualizado" : "creado"} correctamente.`,
      });

      setArticleModalOpen(false);
      setArticleFormOpen(false);
      setOpen(false);
      setSelectedRow(null);
    } catch (err) {
      setMessage({
        open: true,
        severity: "error",
        text: extractApiMessage(err, "Error al guardar Kardex."),
      });
    }
  };

  return (
    <Box>
      <Dialog open={open && !startInArticles} onClose={() => setOpen(false)} fullWidth maxWidth="md">
        <DialogTitle>{formMode === "edit" ? "Editar Kardex" : "Crear Kardex"}</DialogTitle>

        <DialogContent sx={{ pt: 2 }}>
          <Grid container spacing={2}>
            <Grid item xs={12} sm={6}>
              <TextField
                label="Fecha/Hora"
                type="datetime-local"
                name="fechaHora"
                value={formData.fechaHora}
                onChange={handleHeaderChange}
                error={!!errors.fechaHora}
                helperText={errors.fechaHora}
                fullWidth
                InputLabelProps={{
                  shrink: true,
                  sx: { transform: "translate(14px, -9px) scale(0.75)" },
                }}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <FormControl fullWidth error={!!errors.tipoMovimientoId}>
                <InputLabel>Tipo Movimiento</InputLabel>
                <Select
                  name="tipoMovimientoId"
                  label="Tipo Movimiento"
                  value={formData.tipoMovimientoId}
                  onChange={handleHeaderChange}
                  disabled={formMode === "edit"}
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

            <Grid item xs={12} sm={6}>
              <FormControl fullWidth error={!!errors.almacenId}>
                <InputLabel>Almacen</InputLabel>
                <Select name="almacenId" label="Almacen" value={formData.almacenId} onChange={handleHeaderChange}>
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
              <FormControl fullWidth error={!!errors.produccionId}>
                <InputLabel>Produccion</InputLabel>
                <Select
                  name="produccionId"
                  label="Produccion"
                  value={formData.produccionId}
                  onChange={handleHeaderChange}
                >
                  <MenuItem value="">
                    <em>Seleccione...</em>
                  </MenuItem>
                  {producciones.map((p) => (
                    <MenuItem key={p.id} value={p.id}>
                      {renderName(p)}
                    </MenuItem>
                  ))}
                </Select>
                <FormHelperText>{errors.produccionId}</FormHelperText>
              </FormControl>
            </Grid>

            {isEntradaUi && (
              <>
                <Grid item xs={12} sm={6}>
                  <FormControl fullWidth>
                    <InputLabel>Pedido</InputLabel>
                    <Select name="pedidoId" label="Pedido" value={formData.pedidoId} onChange={handleHeaderChange}>
                      <MenuItem value="">
                        <em>Seleccione...</em>
                      </MenuItem>
                      {pedidos.map((p) => (
                        <MenuItem key={p.id} value={p.id}>
                          {renderName(p)}
                        </MenuItem>
                      ))}
                    </Select>
                  </FormControl>
                </Grid>
                <Grid item xs={12} sm={6}>
                  <FormControl fullWidth disabled={!formData.pedidoId}>
                    <InputLabel>Orden de Compra</InputLabel>
                    <Select
                      name="ordenCompraId"
                      label="Orden de Compra"
                      value={formData.ordenCompraId}
                      onChange={handleHeaderChange}
                    >
                      <MenuItem value="">
                        <em>{formData.pedidoId ? "Sin orden asociada" : "Seleccione pedido primero"}</em>
                      </MenuItem>
                      {ordenesCompraFiltradas.map((o) => (
                        <MenuItem key={o.id} value={o.id}>
                          {renderName(o)}
                        </MenuItem>
                      ))}
                    </Select>
                  </FormControl>
                </Grid>
              </>
            )}

            <Grid item xs={12}>
              <FormControl fullWidth>
                <InputLabel>Cliente / Proveedor</InputLabel>
                <Select
                  name="clienteProveedorId"
                  label="Cliente / Proveedor"
                  value={formData.clienteProveedorId}
                  onChange={handleHeaderChange}
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

            <Grid item xs={12}>
              <TextField
                fullWidth
                multiline
                label="Descripcion"
                name="descripcion"
                value={formData.descripcion}
                onChange={handleHeaderChange}
                error={!!errors.descripcion}
                helperText={errors.descripcion}
                minRows={3}
              />
            </Grid>
          </Grid>
        </DialogContent>

        <DialogActions>
          <Button onClick={() => setOpen(false)}>Cancelar</Button>
          <Button onClick={handleNext}>Siguiente</Button>
        </DialogActions>
      </Dialog>

      <Dialog
        open={articleModalOpen}
        onClose={() => {
          setArticleModalOpen(false);
          if (startInArticles) setOpen(false);
        }}
        fullWidth
        maxWidth="lg"
      >
        <DialogTitle>Articulos del Kardex</DialogTitle>
        <DialogContent sx={{ pt: 1 }}>
          <Box sx={{ mb: 2, display: "flex", gap: 2, flexWrap: "wrap" }}>
            <Button variant="contained" sx={{ textTransform: "none" }} onClick={handleOpenCreateArticle}>
              + Agregar
            </Button>
            <Button
              variant="outlined"
              sx={{ textTransform: "none" }}
              onClick={handleOpenEditArticle}
              disabled={!articleSelectedRow}
            >
              Actualizar
            </Button>
            <Button
              variant="outlined"
              color="warning"
              sx={{ textTransform: "none" }}
              onClick={handleDeleteArticle}
              disabled={!articleSelectedRow}
            >
              Anular
            </Button>
          </Box>

          <GridArticuloKardex
            items={draftItems}
            presentaciones={presentaciones}
            selectedRow={articleSelectedRow}
            setSelectedRow={setArticleSelectedRow}
          />
        </DialogContent>

        <DialogActions>
          {!startInArticles && (
            <Button
              onClick={() => {
                setArticleModalOpen(false);
                setOpen(true);
              }}
            >
              Volver
            </Button>
          )}
          <Button
            onClick={() => {
              setArticleModalOpen(false);
              if (startInArticles) setOpen(false);
            }}
          >
            Cancelar
          </Button>
          <Button variant="contained" onClick={handleSaveKardex}>
            Guardar Kardex
          </Button>
        </DialogActions>
      </Dialog>

      <Dialog open={articleFormOpen} onClose={() => setArticleFormOpen(false)} fullWidth maxWidth="sm">
        <DialogTitle>{articleFormMode === "edit" ? "Actualizar Articulo" : "Agregar Articulo"}</DialogTitle>
        <DialogContent>
          <Grid container spacing={2} sx={{ mt: 0.5 }}>
            <Grid item xs={12} sm={6}>
              <FormControl fullWidth required>
                <InputLabel id="kdx-art-product-label">Producto</InputLabel>
                <Select
                  labelId="kdx-art-product-label"
                  label="Producto"
                  name="productoId"
                  value={articleFormData.productoId ?? ""}
                  onChange={handleArticleFormChange}
                >
                  <MenuItem value="">
                    <em>Seleccione...</em>
                  </MenuItem>
                  {Array.from(
                    (productos || []).map((p) => ({
                      id: Number(p?.id),
                      label: p?.nombre ?? p?.name ?? p?.descripcion ?? `Producto ${p?.id ?? ""}`,
                    }))
                  ).map((p) => (
                    <MenuItem key={p.id} value={p.id}>
                      {p.label}
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>
            </Grid>

            <Grid item xs={12} sm={6}>
              <FormControl fullWidth required>
                <InputLabel id="kdx-art-pp-label">Presentacion</InputLabel>
                <Select
                  labelId="kdx-art-pp-label"
                  label="Presentacion"
                  name="presentacionProductoId"
                  value={articleFormData.presentacionProductoId ?? ""}
                  onChange={handleArticleFormChange}
                  disabled={!articleFormData.productoId}
                >
                  <MenuItem value="">
                    <em>Seleccione...</em>
                  </MenuItem>
                  {presentacionesByProducto.map((p) => (
                    <MenuItem key={p.id} value={p.id}>
                      {renderPresentacion(p)}
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
                type="number"
                value={articleFormData.cantidad}
                onChange={handleArticleFormChange}
              />
            </Grid>

            <Grid item xs={12} sm={4}>
              <TextField
                fullWidth
                name="precio"
                label="Precio Unitario"
                type="number"
                value={articleFormData.precio}
                onChange={handleArticleFormChange}
              />
            </Grid>

            <Grid item xs={12} sm={4}>
              <TextField
                fullWidth
                name="precioTotal"
                label="Precio Total"
                value={Number(articleFormData.cantidad || 0) * Number(articleFormData.precio || 0)}
                InputProps={{ readOnly: true }}
              />
            </Grid>

            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                type="date"
                name="fechaVencimiento"
                label="Fecha de Vencimiento"
                value={articleFormData.fechaVencimiento || ""}
                onChange={handleArticleFormChange}
                InputLabelProps={{ shrink: true }}
              />
            </Grid>

            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                name="lote"
                label="Lote"
                value={articleFormData.lote || ""}
                onChange={handleArticleFormChange}
              />
            </Grid>

            {Boolean(articleFormData?.devolutivo) && (
              <Grid item xs={12}>
                <FormControl fullWidth required>
                  <InputLabel id="kdx-art-responsable-label">Responsable</InputLabel>
                  <Select
                    labelId="kdx-art-responsable-label"
                    label="Responsable"
                    name="responsableId"
                    value={articleFormData.responsableId ?? ""}
                    onChange={handleArticleFormChange}
                  >
                    <MenuItem value="">
                      <em>Seleccione...</em>
                    </MenuItem>
                    {responsables.map((r) => (
                      <MenuItem key={r.id} value={r.id}>
                        {r?.nombre ?? r?.name ?? r?.personaNombreCompleto ?? r?.descripcion ?? `Responsable ${r?.id}`}
                      </MenuItem>
                    ))}
                  </Select>
                </FormControl>
              </Grid>
            )}

          </Grid>
        </DialogContent>

        <DialogActions>
          <Button onClick={() => setArticleFormOpen(false)}>Cancelar</Button>
          <Button onClick={handleSaveArticleDraft}>Guardar</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}
