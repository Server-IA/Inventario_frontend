import React, { useEffect, useState } from "react";
import {
  Box, Typography, TextField, Button, Stack, Grid,
  FormControl, InputLabel, Select, MenuItem,
  Dialog, DialogTitle, DialogContent, IconButton,
  Table, TableBody, TableCell, TableHead, TableRow, TableContainer, Paper
} from "@mui/material";
import CloseIcon from "@mui/icons-material/Close";
import axios from "../axiosConfig";
import MessageSnackBar from "../MessageSnackBar";

import useUbicacionFilters from "../useUbicacionFilters";
import UbicacionProductoVencimientoFilters from "../UbicacionProductoVencimientoFilters.jsx";

export default function RE_kardexPedido() {
  const empresaId = localStorage.getItem("empresaId");
  const token = localStorage.getItem("token");
  const headers = { headers: { Authorization: `Bearer ${token}` } };

  // ===== Utils
  const asArray = (x) => (Array.isArray(x) ? x : x?.content ?? x?.data ?? []);
  const getFechaKdx = (o) =>
    o?.karFechaHora ?? o?.fechaHora ?? o?.fecha ?? o?.createdAt ?? null;
  const toLocal = (val) => {
    if (!val) return "";
    const d = new Date(val);
    return isNaN(d.getTime()) ? String(val) : d.toLocaleString();
  };

  // "YYYY-MM-DDTHH:mm" -> "YYYY-MM-DD HH:mm"
  const toDateStr = (val, end = false) => {
    if (!val) return null;
    const [d, t] = String(val).split("T");
    if (!d) return null;
    const hhmm = t ? t.slice(0, 5) : end ? "23:59" : "00:00";
    return `${d} ${hhmm}`;
  };

  // ===== Hook de ubicación (mismo patrón que RE_pedido / RE_productoVencimiento)
  const {
    form: ubi,
    handleChange: handleUbiChange,
    data: ubiData,
    resetTodo,
  } = useUbicacionFilters({ empresaId, headers, autoselectSingle: true });

  // ===== Filtros de kardex (producto, categoría, fechas)
  const [kdxFiltro, setKdxFiltro] = useState({
    producto_id: "",
    producto_categoria_id: "",
    fecha_inicio: "",
    fecha_fin: "",
  });

  const handleFiltroChange = (name) => (e) => {
    const value = e?.target ? e.target.value : e;
    setKdxFiltro((f) => ({ ...f, [name]: value }));
  };

  // ===== Catálogos de producto / categoría (ubicación la maneja el hook)
  const [productos, setProductos] = useState([]);
  const [categorias, setCategorias] = useState([]);

  useEffect(() => {
    Promise.all([
      axios.get("/v1/items/producto/0", headers).catch(() => ({ data: [] })),
      axios
        .get("/v1/items/producto_categoria/0", headers)
        .catch(() => ({ data: [] })),
    ])
      .then(([pro, cat]) => {
        setProductos(asArray(pro.data));
        setCategorias(asArray(cat.data));
      })
      .catch(() =>
        setMessage({
          open: true,
          severity: "error",
          text: "No fue posible cargar los catálogos.",
        })
      );
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  // ===== Estado UI
  const [resultados, setResultados] = useState([]);
  const [previewUrl, setPreviewUrl] = useState("");
  const [previewOpen, setPreviewOpen] = useState(false);
  const [message, setMessage] = useState({
    open: false,
    severity: "info",
    text: "",
  });
  const [openUbi, setOpenUbi] = useState(false);
  const [errors, setErrors] = useState({ fechas_rango: false });

  const validarRango = () => {
    setErrors({ fechas_rango: false });
    if (kdxFiltro.fecha_inicio && kdxFiltro.fecha_fin) {
      const ini = new Date(kdxFiltro.fecha_inicio);
      const fin = new Date(kdxFiltro.fecha_fin);
      if (ini > fin) {
        setErrors({ fechas_rango: true });
        setMessage({
          open: true,
          severity: "warning",
          text: "La fecha de inicio no puede ser mayor que la fecha fin.",
        });
        return false;
      }
    }
    return true;
  };

  // ===== Listado (igual filosofía que Pedido/OC): trae todo y filtra por fechas
  const fetchAllKardex = async () => {
    const CANDIDATES = ["/v1/kardex"];
    const size = 200;
    for (const basePath of CANDIDATES) {
      try {
        // sin paginación
        const r0 = await axios.get(basePath, headers);
        const list0 = asArray(r0.data);
        if (list0.length) return list0;

        // con paginación
        let page = 0,
          acc = [];
        for (let i = 0; i < 15; i++) {
          const r = await axios.get(basePath, {
            params: { page, size },
            ...headers,
          });
          const list = asArray(r.data);
          if (!list.length) break;
          acc = acc.concat(list);
          page += 1;
        }
        if (acc.length) return acc;
      } catch (err) {
        if (import.meta.env.DEV)
          console.debug(
            "[fetchAllKardex] fallo",
            basePath,
            err?.response?.status
          );
      }
    }
    const e = new Error("No se encontró endpoint para listar kardex.");
    e.code = "NO_KARDEX_ENDPOINT";
    throw e;
  };

  // ===== Buscar (solo UX)
  const buscar = async () => {
    setResultados([]);
    setErrors({ fechas_rango: false });

    if (!validarRango()) return;

    try {
      const all = await fetchAllKardex();

      const ini = kdxFiltro.fecha_inicio
        ? new Date(kdxFiltro.fecha_inicio)
        : null;
      const fin = kdxFiltro.fecha_fin ? new Date(kdxFiltro.fecha_fin) : null;

      const lista = all.filter((row) => {
        const f = getFechaKdx(row);
        if (!f || (!ini && !fin)) return true;
        const d = new Date(f);
        if (isNaN(d.getTime())) return true;
        if (ini && d < ini) return false;
        if (fin && d > fin) return false;
        return true;
      });

      setResultados(lista);
      setMessage({
        open: true,
        severity: "info",
        text: `Mostrando ${lista.length} movimiento(s) de kardex.`,
      });
    } catch (err) {
      const msg =
        err?.code === "NO_KARDEX_ENDPOINT"
          ? "No se encontró el endpoint de listado de kardex (404)."
          : "No se pudo cargar el kardex.";
      console.error(err);
      setMessage({ open: true, severity: "error", text: msg });
    }
  };

  // ===== PDF: usa ubi + kdxFiltro (índices 0..8 como tenías) =====
  const buildCondicion = () => {
    const DEF_INI = "1900-01-01 00:00";
    const DEF_FIN = "2099-12-31 23:59";
    const userIni = toDateStr(kdxFiltro.fecha_inicio, false);
    const userFin = toDateStr(kdxFiltro.fecha_fin, true);

    const c = {};
    c["0"] = `e.emp_id = $EMPRESA_ID$`;
    c["1"] = ubi.municipio_id
      ? `AND m.mun_id = ${Number(ubi.municipio_id)}`
      : "";
    c["2"] = ubi.sede_id ? `AND s.sed_id = ${Number(ubi.sede_id)}` : "";
    c["3"] = ubi.bloque_id ? `AND blo.blo_id = ${Number(ubi.bloque_id)}` : "";
    c["4"] = ubi.espacio_id ? `AND esp.esp_id = ${Number(ubi.espacio_id)}` : "";
    c["5"] = ubi.almacen_id ? `AND al.alm_id = ${Number(ubi.almacen_id)}` : "";
    c["6"] = kdxFiltro.producto_id
      ? `AND p.pro_id = ${Number(kdxFiltro.producto_id)}`
      : "";
    c["7"] = kdxFiltro.producto_categoria_id
      ? `AND p.pro_producto_categoria_id = ${Number(
          kdxFiltro.producto_categoria_id
        )}`
      : "";
    c["8"] = `AND k.kar_fecha_hora BETWEEN '${
      userIni ?? DEF_INI
    }' AND '${userFin ?? DEF_FIN}'`;
    return c;
  };

  const requestPDF = async (condicion) => {
    try {
      return await axios({
        url: "/v2/report/nuevo/kardex",
        method: "POST",
        data: { condicion, EMPRESA_ID: empresaId },
        responseType: "blob",
        ...headers,
      });
    } catch (e1) {
      if (e1?.response?.status !== 404) throw e1;
      return await axios({
        url: "/v2/report/nuevo/kardex",
        method: "POST",
        data: { condicion, EMPRESA_ID: empresaId },
        responseType: "blob",
        ...headers,
      });
    }
  };

  const extractServerError = async (err) => {
    try {
      if (err?.response?.data instanceof Blob) {
        const t = await err.response.data.text();
        return t?.slice(0, 400) || err.message;
      }
    } catch {}
    return err?.message || "Error desconocido";
  };

  const generarReporte = async () => {
    try {
      const condicion = buildCondicion();
      const res = await requestPDF(condicion);
      const blob = new Blob([res.data], { type: "application/pdf" });
      if (previewUrl) URL.revokeObjectURL(previewUrl);
      const url = window.URL.createObjectURL(blob);
      setPreviewUrl(url);
      setPreviewOpen(true);
      setMessage({ open: true, severity: "success", text: "PDF generado." });
    } catch (err) {
      const txt = await extractServerError(err);
      console.error(err);
      setMessage({
        open: true,
        severity: "error",
        text: `No se pudo generar el PDF (HTTP ${
          err?.response?.status ?? "?"
        }). ${txt}`,
      });
    }
  };

  return (
    <Box sx={{ p: 4 }}>
      <Typography variant="h4" gutterBottom>
        Reporte Kardex
      </Typography>

      {/* Filtros principales (producto, categoría, fechas) */}
      <Grid container spacing={2} mb={2}>
        <Grid item xs={12} md={6}>
          <FormControl fullWidth>
            <InputLabel>Producto</InputLabel>
            <Select
              label="Producto"
              value={kdxFiltro.producto_id || ""}
              onChange={handleFiltroChange("producto_id")}
            >
              <MenuItem value="">
                <em>Todos</em>
              </MenuItem>
              {asArray(productos).map((it) => (
                <MenuItem key={it.id} value={String(it.id)}>
                  {it.nombre ?? it.name ?? `#${it.id}`}
                </MenuItem>
              ))}
            </Select>
          </FormControl>
        </Grid>

        <Grid item xs={12} md={6}>
          <FormControl fullWidth>
            <InputLabel>Categoría Producto</InputLabel>
            <Select
              label="Categoría Producto"
              value={kdxFiltro.producto_categoria_id || ""}
              onChange={handleFiltroChange("producto_categoria_id")}
            >
              <MenuItem value="">
                <em>Todas</em>
              </MenuItem>
              {asArray(categorias).map((it) => (
                <MenuItem key={it.id} value={String(it.id)}>
                  {it.nombre ?? it.name ?? `#${it.id}`}
                </MenuItem>
              ))}
            </Select>
          </FormControl>
        </Grid>

        <Grid item xs={12} md={6}>
          <TextField
            label="Fecha Inicio"
            name="fecha_inicio"
            type="datetime-local"
            fullWidth
            value={kdxFiltro.fecha_inicio || ""}
            onChange={handleFiltroChange("fecha_inicio")}
            InputLabelProps={{ shrink: true }}
            error={errors.fechas_rango}
            helperText={
              errors.fechas_rango ? "Inicio no puede ser mayor que fin." : ""
            }
          />
        </Grid>

        <Grid item xs={12} md={6}>
          <TextField
            label="Fecha Fin"
            name="fecha_fin"
            type="datetime-local"
            fullWidth
            value={kdxFiltro.fecha_fin || ""}
            onChange={handleFiltroChange("fecha_fin")}
            InputLabelProps={{ shrink: true }}
            error={errors.fechas_rango}
            helperText={
              errors.fechas_rango ? "Fin debe ser >= Inicio." : ""
            }
          />
        </Grid>
      </Grid>

      {/* Acciones */}
      <Stack direction="row" spacing={2} mb={3}>
        <Button variant="contained" onClick={buscar}>
          Buscar
        </Button>
        <Button variant="outlined" onClick={generarReporte}>
          Generar Reporte
        </Button>
        <Button variant="text" onClick={() => setOpenUbi(true)}>
          Filtros (ubicación + kardex)
        </Button>
      </Stack>

      {/* Diálogo: reutilizamos UbicacionProductoVencimientoFilters */}
      <UbicacionProductoVencimientoFilters
        variant="dialog"
        title="Filtros (ubicación + kardex)"
        open={openUbi}
        onClose={() => setOpenUbi(false)}
        // Ubicación
        ubiForm={ubi}
        ubiData={ubiData}
        handleUbiChange={handleUbiChange}
        onUbiReset={resetTodo}
        // Kardex: producto / categoría / fechas
        filtro={kdxFiltro}
        productos={productos}
        categorias={categorias}
        handleFiltroChange={handleFiltroChange}
        fechasError={errors.fechas_rango}
        // Aplicar
        onApply={() => {
          setOpenUbi(false);
          buscar();
        }}
      />

      {/* Tabla resultados */}
      {resultados.length > 0 && (
        <Box mt={4}>
          <Typography variant="h6" gutterBottom>
            Kardex encontrado
          </Typography>
          <TableContainer component={Paper} variant="outlined">
            <Table size="small">
              <TableHead>
                <TableRow>
                  <TableCell>ID</TableCell>
                  <TableCell>Fecha/Hora</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {resultados.map((row, idx) => (
                  <TableRow key={row.id ?? idx}>
                    <TableCell>{row.id ?? ""}</TableCell>
                    <TableCell>{toLocal(getFechaKdx(row))}</TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        </Box>
      )}

      {/* Preview PDF */}
      <Dialog
        open={previewOpen}
        onClose={() => setPreviewOpen(false)}
        fullWidth
        maxWidth="lg"
      >
        <DialogTitle>
          Vista previa del Reporte
          <IconButton
            onClick={() => setPreviewOpen(false)}
            sx={{ position: "absolute", right: 8, top: 8 }}
          >
            <CloseIcon />
          </IconButton>
        </DialogTitle>
        <DialogContent dividers>
          {previewUrl && (
            <iframe
              src={previewUrl}
              width="100%"
              height="600"
              title="PDF"
              style={{ border: "none" }}
            />
          )}
        </DialogContent>
      </Dialog>

      <MessageSnackBar message={message} setMessage={setMessage} />
    </Box>
  );
}
