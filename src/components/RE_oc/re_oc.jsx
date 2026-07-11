/*=============================================================================
 Nombre del archivo : re_oc.jsx
 Descripcion        : Módulo de Reportes de Ordenes de Compra.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |   Fecha    | Versión |      Autor           | Descripción del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2026-07-01 | 0.4.0   | Jeisson Sanchez      | Refactor UI/UX filtros, HU  |
 +------------+---------+----------------------+-----------------------------+
 | 2026-07-11 | 0.4.1   | Jeisson Sanchez      | Rediseño alineación filtros |
 +------------+---------+----------------------+-----------------------------+
=============================================================================*/
import React, { useEffect, useState, useMemo } from "react";
import {
  Box, Typography, TextField, Button, Stack, Grid,
  FormControl, InputLabel, Select, MenuItem,
  Dialog, DialogTitle, DialogContent, DialogActions, IconButton,
  Paper, Divider, Popover, LinearProgress
} from "@mui/material";
import {
  Close as CloseIcon,
  Search as SearchIcon,
  Download as DownloadIcon,
  PictureAsPdf as PdfIcon,
  TableView as ExcelIcon,
  CalendarToday as CalendarIcon,
  Refresh as RefreshIcon
} from "@mui/icons-material";
import { useTranslation } from "react-i18next";
import { useTheme } from "@mui/material/styles";
import { DateRange } from "react-date-range";
import "react-date-range/dist/styles.css";
import "react-date-range/dist/theme/default.css";
import { es, enUS } from "date-fns/locale";

import axios from "../axiosConfig";
import MessageSnackBar from "../MessageSnackBar";
import useUbicacionFilters from "../useUbicacionFilters";
import AppDataGrid from "../common/AppDataGrid.jsx";

import VistaPreviaPDFOrdenCompra from "../OrdenCompra/vistapreviapdfordencompra";
import GridArticuloOrdenCompra from "../OrdenCompra/GridArticuloOrdenCompra";

export default function RE_ordenCompra() {
  const { t, i18n } = useTranslation();
  const theme = useTheme();
  const isDark = theme.palette.mode === "dark";

  const empresaId = localStorage.getItem("empresaId");
  const token = localStorage.getItem("token");
  const headers = { headers: { Authorization: `Bearer ${token}` } };

  // ===== Utils
  const asArray = (x) => (Array.isArray(x) ? x : x?.content ?? x?.data ?? []);
  const getFechaOC = (o) => o?.orcFechaHora ?? o?.fechaHora ?? o?.fecha ?? o?.createdAt ?? null;
  const toLocal = (val) => {
    if (!val) return "";
    const d = new Date(val);
    return isNaN(d.getTime()) ? String(val) : d.toLocaleString();
  };

  const toDateTimeStr = (val) => {
    if (!val) return null;
    const [d, time = "00:00"] = String(val).split("T");
    return `${d} ${time}:00`;
  };

  const getInitDates = () => {
    const today = new Date();
    const firstDayLastMonth = new Date(today.getFullYear(), today.getMonth() - 1, 1);
    const y1 = firstDayLastMonth.getFullYear();
    const m1 = String(firstDayLastMonth.getMonth() + 1).padStart(2, '0');
    const d1 = String(firstDayLastMonth.getDate()).padStart(2, '0');
    const y2 = today.getFullYear();
    const m2 = String(today.getMonth() + 1).padStart(2, '0');
    const d2 = String(today.getDate()).padStart(2, '0');
    return {
      fecha_inicio: `${y1}-${m1}-${d1}T00:00`,
      fecha_fin: `${y2}-${m2}-${d2}T23:59`,
    };
  };

  // ===== Hook unificado
  const {
    form: ubi,
    handleChange: handleUbiChange,
    data: ubiData,
    loading: ubiLoading,
    error: ubiError,
    fetchInitialData,

    pedido,
    handlePedidoChange,
    pedidos,
  } = useUbicacionFilters({ empresaId, headers, autoselectSingle: true });

  const [formReporte, setFormReporte] = useState({
    ...getInitDates()
  });

  const [estadosOC, setEstadosOC] = useState([]);
  const [estadosLoading, setEstadosLoading] = useState(false);
  const [estadosError, setEstadosError] = useState(false);

  const fetchEstados = () => {
    setEstadosLoading(true);
    setEstadosError(false);
    axios.get("/v1/items/orden_compra_estado/0", headers)
      .then((res) => {
        setEstadosOC(asArray(res.data));
        setEstadosLoading(false);
      })
      .catch((err) => {
        setEstadosError(true);
        setEstadosLoading(false);
      });
  };

  useEffect(() => {
    fetchEstados();
    if (!pedidos?.length) axios.get("/v1/pedido", headers).catch(() => {});
    // eslint-disable-next-line
  }, []);

  // UI state
  const [ordenData, setOrdenData] = useState(null);
  const [articulos, setArticulos] = useState([]);
  const [presentaciones, setPresentaciones] = useState([]);
  const [resultados, setResultados] = useState([]);
  const [loadingSearch, setLoadingSearch] = useState(false);

  const [message, setMessage] = useState({ open: false, severity: "info", text: "" });
  const [errors, setErrors] = useState({ fechas_rango: false });

  // Date Popover
  const [anchorElDate, setAnchorElDate] = useState(null);
  const openDate = Boolean(anchorElDate);
  const handleOpenDate = (e) => setAnchorElDate(e.currentTarget);
  const handleCloseDate = () => setAnchorElDate(null);

  const setQuickDate = (type) => {
    const today = new Date();
    let fInicio, fFin;
    const y2 = today.getFullYear();
    const m2 = String(today.getMonth() + 1).padStart(2, '0');
    const d2 = String(today.getDate()).padStart(2, '0');
    fFin = `${y2}-${m2}-${d2}T23:59`;
    
    if (type === 'hoy') fInicio = `${y2}-${m2}-${d2}T00:00`;
    else if (type === 'semana') {
      const past = new Date(today.getTime() - 7 * 24 * 60 * 60 * 1000);
      fInicio = `${past.getFullYear()}-${String(past.getMonth() + 1).padStart(2, '0')}-${String(past.getDate()).padStart(2, '0')}T00:00`;
    } else if (type === 'mes') {
      const past = new Date(today.getFullYear(), today.getMonth() - 1, today.getDate());
      fInicio = `${past.getFullYear()}-${String(past.getMonth() + 1).padStart(2, '0')}-${String(past.getDate()).padStart(2, '0')}T00:00`;
    } else if (type === 'trimestre') {
      const past = new Date(today.getFullYear(), today.getMonth() - 3, today.getDate());
      fInicio = `${past.getFullYear()}-${String(past.getMonth() + 1).padStart(2, '0')}-${String(past.getDate()).padStart(2, '0')}T00:00`;
    } else if (type === 'anio') {
      fInicio = `${y2}-01-01T00:00`;
    }
    setFormReporte(f => ({ ...f, fecha_inicio: fInicio, fecha_fin: fFin }));
    handleCloseDate();
  };

  const handleSelectDateRange = (item) => {
    const { startDate, endDate } = item.selection;
    const y1 = startDate.getFullYear();
    const m1 = String(startDate.getMonth() + 1).padStart(2, '0');
    const d1 = String(startDate.getDate()).padStart(2, '0');
    const fInicio = `${y1}-${m1}-${d1}T00:00`;
    
    let fFin = "";
    if (endDate) {
      const y2 = endDate.getFullYear();
      const m2 = String(endDate.getMonth() + 1).padStart(2, '0');
      const d2 = String(endDate.getDate()).padStart(2, '0');
      fFin = `${y2}-${m2}-${d2}T23:59`;
    } else {
      fFin = `${y1}-${m1}-${d1}T23:59`;
    }
    setFormReporte(f => ({ ...f, fecha_inicio: fInicio, fecha_fin: fFin }));
  };

  const validarRango = () => {
    setErrors({ fechas_rango: false });
    if (!formReporte.fecha_inicio || !formReporte.fecha_fin) {
      setMessage({ open: true, severity: "warning", text: t("ordenCompra.messages.emptyDates", "El rango de fechas es obligatorio.") });
      return false;
    }
    const ini = new Date(formReporte.fecha_inicio);
    const fin = new Date(formReporte.fecha_fin);
    if (ini > fin) {
      setErrors({ fechas_rango: true });
      setMessage({ open: true, severity: "warning", text: t("ordenCompra.messages.invalidDateRange", "La fecha de inicio no puede ser mayor que la fecha fin.") });
      return false;
    }
    return true;
  };

  const fetchAllOrdenes = async () => {
    const CANDIDATES = ["/v1/orden_compra", "/v1/orden-compra", "/v1/ordenCompra"];
    const size = 200;
    let all = [];
    for (const basePath of CANDIDATES) {
      try {
        const r0 = await axios.get(basePath, headers);
        const list0 = asArray(r0.data);
        if (list0.length) return list0;
        let page = 0;
        for (let i = 0; i < 15; i++) {
          const r = await axios.get(basePath, { params: { page, size }, ...headers });
          const list = asArray(r.data);
          if (!list.length) break;
          all = all.concat(list);
          page += 1;
        }
        if (all.length) return all;
      } catch (err) {
        if (import.meta.env.DEV) console.debug(`[fetchAllOrdenes] fallo en ${basePath}`);
      }
    }
    const e = new Error("No se encontró endpoint.");
    e.code = "NO_OC_ENDPOINT";
    throw e;
  };

  const buscar = async () => {
    setOrdenData(null);
    setArticulos([]);
    setPresentaciones([]);
    setResultados([]);
    if (!validarRango()) return;

    setLoadingSearch(true);
    try {
      const all = await fetchAllOrdenes();
      const ini = new Date(formReporte.fecha_inicio);
      const fin = new Date(formReporte.fecha_fin);

      const byDate = all.filter((oc) => {
        const f = getFechaOC(oc);
        if (!f) return true;
        const d = new Date(f);
        if (isNaN(d.getTime())) return true;
        if (d < ini) return false;
        if (d > fin) return false;
        if (pedido.categoria_estado_id && oc.estado?.id !== Number(pedido.categoria_estado_id)) return false;
        return true;
      });

      if (!pedido.pedido_id) {
        // Calcular totales para la grilla
        const resultadosConTotales = await Promise.all(byDate.map(async (oc) => {
          try {
            const aRes = await axios.get(`/v1/orden_compra/${oc.id}/articulos`, headers).catch(async () => {
              return await axios.get(`/v1/orden-compra/${oc.id}/articulos`, headers).catch(() => {
                return axios.get(`/v1/ordenCompra/${oc.id}/articulos`, headers);
              });
            });
            const arts = asArray(aRes.data);
            const totalUnits = arts.reduce((sum, a) => sum + (Number(a.cantidad) || 0), 0);
            const totalValue = arts.reduce((sum, a) => sum + ((Number(a.cantidad) || 0) * (Number(a.precioUnitario) || Number(a.precio) || 0)), 0);
            return { ...oc, totalUnits, totalValue, articulosCargados: arts };
          } catch (e) {
            return { ...oc, totalUnits: 0, totalValue: 0, articulosCargados: [] };
          }
        }));

        setResultados(resultadosConTotales);
        if (resultadosConTotales.length === 0) {
          setMessage({ open: true, severity: "info", text: t("ordenCompra.messages.noResultsEmpty", "No se encontraron registros.") });
        } else {
          setMessage({ open: true, severity: "info", text: t("ordenCompra.messages.noResults", { count: resultadosConTotales.length }, `Mostrando ${resultadosConTotales.length} orden(es).`) });
        }
        return;
      }

      const orden = byDate.find((o) => String(o.pedidoId) === String(pedido.pedido_id));
      if (!orden) {
        setMessage({ open: true, severity: "warning", text: t("ordenCompra.messages.noOrders", "No se encontró la orden.") });
        return;
      }

      const [aRes, prRes] = await Promise.all([
        axios.get(`/v1/orden_compra/${orden.id}/articulos`, headers).catch(async () => {
          return await axios.get(`/v1/orden-compra/${orden.id}/articulos`, headers).catch(() => {
            return axios.get(`/v1/ordenCompra/${orden.id}/articulos`, headers);
          });
        }),
        axios.get("/v1/producto_presentacion", headers).catch(() => axios.get("/v1/presentacion", headers))
      ]);

      setOrdenData(orden);
      setArticulos(asArray(aRes.data));
      setPresentaciones(asArray(prRes.data));
      setMessage({ open: true, severity: "success", text: t("ordenCompra.messages.loaded", "Datos cargados.") });
    } catch (err) {
      const msg = err?.code === "NO_OC_ENDPOINT" ? t("ordenCompra.messages.endpointError", "Endpoint 404") : t("ordenCompra.messages.searchError", "Error");
      setMessage({ open: true, severity: "error", text: msg });
    } finally {
      setLoadingSearch(false);
    }
  };

  // Export
  const [modalExportOpen, setModalExportOpen] = useState(false);
  const [exporting, setExporting] = useState(false);
  const [previewUrl, setPreviewUrl] = useState("");
  const [previewOpen, setPreviewOpen] = useState(false);

  const buildCondicion = () => {
    const out = {};
    let idx = 0;
    out[String(idx++)] = `oc.orc_empresa_id = $EMPRESA_ID$`;
    if (pedido.pedido_id) out[String(idx++)] = `AND oc.orc_pedido_id = ${Number(pedido.pedido_id)}`;
    if (pedido.categoria_estado_id) out[String(idx++)] = `AND oc.orc_orden_compra_estado_id = ${Number(pedido.categoria_estado_id)}`;
    
    // Ubi
    if (ubi.municipio_id) out[String(idx++)] = `AND m.mun_id = ${Number(ubi.municipio_id)}`;
    if (ubi.sede_id) out[String(idx++)] = `AND s.sed_id = ${Number(ubi.sede_id)}`;
    if (ubi.bloque_id) out[String(idx++)] = `AND blo.blo_id = ${Number(ubi.bloque_id)}`;
    if (ubi.espacio_id) out[String(idx++)] = `AND esp.esp_id = ${Number(ubi.espacio_id)}`;
    if (ubi.almacen_id) out[String(idx++)] = `AND al.alm_id = ${Number(ubi.almacen_id)}`;

    const ini = toDateTimeStr(formReporte.fecha_inicio);
    const fin = toDateTimeStr(formReporte.fecha_fin);
    if (ini && fin) out[String(idx++)] = `AND oc.orc_fecha_hora BETWEEN "${ini}" AND "${fin}"`;
    return out;
  };

  const generarReporte = async (formato = "PDF") => {
    if (!validarRango()) return;
    setExporting(true);
    try {
      const condicion = buildCondicion();
      const payload = { condicion, EMPRESA_ID: empresaId, formato };
      
      const res = await axios({
        url: "/v2/report/nuevo/orden_compra",
        method: "POST",
        data: payload,
        responseType: "blob",
        ...headers,
      });
      
      const mimeType = formato === "EXCEL" ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" : "application/pdf";
      const blob = new Blob([res.data], { type: mimeType });
      const objUrl = window.URL.createObjectURL(blob);
      
      if (formato === "EXCEL") {
        const a = document.createElement("a");
        a.href = objUrl;
        a.download = `OrdenCompra_${new Date().getTime()}.xlsx`;
        a.click();
        URL.revokeObjectURL(objUrl);
        setMessage({ open: true, severity: "success", text: t("ordenCompra.messages.excelSuccess", "Excel descargado.") });
      } else {
        if (previewUrl) URL.revokeObjectURL(previewUrl);
        setPreviewUrl(objUrl);
        setPreviewOpen(true);
        setMessage({ open: true, severity: "success", text: t("ordenCompra.messages.pdfSuccess", "PDF generado.") });
      }
    } catch (err) {
      console.error(err);
      setMessage({ open: true, severity: "error", text: t("ordenCompra.messages.exportError", "Error al exportar.") });
    } finally {
      setExporting(false);
      setModalExportOpen(false);
    }
  };

  // Columns for AppDataGrid
  const columns = useMemo(() => [
    { field: "id", headerName: t("ordenCompra.columns.id", "ID"), width: 100 },
    { 
      field: "fecha", 
      headerName: t("ordenCompra.columns.fecha", "Fecha/Hora"), 
      flex: 1, minWidth: 200,
      renderCell: (params) => toLocal(getFechaOC(params.row))
    },
    {
      field: "totalUnits",
      headerName: t("ordenCompra.columns.totalUnits", "Total Unidades"),
      width: 150,
      align: "right",
      headerAlign: "right",
    },
    {
      field: "totalValue",
      headerName: t("ordenCompra.columns.totalValue", "Valor Total"),
      width: 150,
      align: "right",
      headerAlign: "right",
      renderCell: (params) => {
        const val = Number(params.row.totalValue) || 0;
        return new Intl.NumberFormat(i18n.language, { style: "currency", currency: "COP" }).format(val);
      }
    }
  ], [t, i18n.language]);

  const boxStyles = {
    p: 3, 
    borderRadius: 4, 
    bgcolor: isDark ? "rgba(255,255,255,0.02)" : "#fff",
    boxShadow: isDark ? "0 4px 12px rgba(0,0,0,0.2)" : "0 4px 12px rgba(23,63,57,0.06)"
  };
  const titleStyles = { fontWeight: 600, mb: 2, color: isDark ? "#e7f6f7" : "#173f39", display: "flex", alignItems: "center", gap: 1 };

  return (
    <Box sx={{ width: "100%", p: { xs: 2, md: 4 }, color: "text.primary" }}>
      <Box sx={{ display: "flex", justifyContent: "center", alignItems: "center", mb: 4 }}>
        <Typography variant="h4" sx={{ fontWeight: 700, color: isDark ? "#fff" : "#173f39" }}>
          {t("ordenCompra.title", "Reporte de Orden de Compra")}
        </Typography>
      </Box>

      <Grid container spacing={3} mb={3} alignItems="flex-start">
        {/* Ubicacion (Izquierda) */}
        <Grid item xs={12} md={7}>
          <Paper sx={boxStyles}>
            <Typography variant="h6" sx={titleStyles}>
              {t("ordenCompra.sections.location", "Ubicación")}
              {ubiLoading && <LinearProgress sx={{ flexGrow: 1, ml: 2, height: 2 }} />}
              {ubiError && (
                <IconButton size="small" onClick={fetchInitialData} color="error" title={t("common.actions.retry", "Reintentar")}>
                  <RefreshIcon fontSize="small"/>
                </IconButton>
              )}
            </Typography>
            <Grid container spacing={2}>
              <Grid item xs={12}>
                <FormControl size="small" fullWidth disabled={ubiLoading || ubiError}>
                  <InputLabel>{t("ordenCompra.filters.country", "País")}</InputLabel>
                  <Select label={t("ordenCompra.filters.country", "País")} value={ubi.pais_id || ""} onChange={handleUbiChange("pais_id")}>
                    <MenuItem value=""><em>{t("common.labels.all", "Todos")}</em></MenuItem>
                    {ubiData?.paises?.map(it => <MenuItem key={it.id} value={String(it.id)}>{it.nombre}</MenuItem>)}
                  </Select>
                </FormControl>
              </Grid>
              <Grid item xs={12} sm={4}>
                <FormControl size="small" fullWidth disabled={ubiLoading || ubiError || !ubi.pais_id}>
                  <InputLabel>{t("ordenCompra.filters.department", "Departamento")}</InputLabel>
                  <Select label={t("ordenCompra.filters.department", "Departamento")} value={ubi.departamento_id || ""} onChange={handleUbiChange("departamento_id")}>
                    <MenuItem value=""><em>{t("common.labels.all", "Todos")}</em></MenuItem>
                    {ubiData?.departamentos?.map(it => <MenuItem key={it.id} value={String(it.id)}>{it.nombre}</MenuItem>)}
                  </Select>
                </FormControl>
              </Grid>
              <Grid item xs={12} sm={4}>
                <FormControl size="small" fullWidth disabled={ubiLoading || ubiError || !ubi.departamento_id}>
                  <InputLabel>{t("ordenCompra.filters.municipality", "Municipio")}</InputLabel>
                  <Select label={t("ordenCompra.filters.municipality", "Municipio")} value={ubi.municipio_id || ""} onChange={handleUbiChange("municipio_id")}>
                    <MenuItem value=""><em>{t("common.labels.all", "Todos")}</em></MenuItem>
                    {ubiData?.municipios?.map(it => <MenuItem key={it.id} value={String(it.id)}>{it.nombre}</MenuItem>)}
                  </Select>
                </FormControl>
              </Grid>
              <Grid item xs={12} sm={4}>
                <FormControl size="small" fullWidth disabled={ubiLoading || ubiError}>
                  <InputLabel>{t("ordenCompra.filters.headquarters", "Sede")}</InputLabel>
                  <Select label={t("ordenCompra.filters.headquarters", "Sede")} value={ubi.sede_id || ""} onChange={handleUbiChange("sede_id")}>
                    <MenuItem value=""><em>{t("common.labels.all", "Todas")}</em></MenuItem>
                    {ubiData?.sedes?.map(it => <MenuItem key={it.id} value={String(it.id)}>{it.nombre}</MenuItem>)}
                  </Select>
                </FormControl>
              </Grid>
              <Grid item xs={12} sm={4}>
                <FormControl size="small" fullWidth disabled={ubiLoading || ubiError || !ubi.sede_id}>
                  <InputLabel>{t("ordenCompra.filters.block", "Bloque")}</InputLabel>
                  <Select label={t("ordenCompra.filters.block", "Bloque")} value={ubi.bloque_id || ""} onChange={handleUbiChange("bloque_id")}>
                    <MenuItem value=""><em>{t("common.labels.all", "Todos")}</em></MenuItem>
                    {ubiData?.bloques?.map(it => <MenuItem key={it.id} value={String(it.id)}>{it.nombre}</MenuItem>)}
                  </Select>
                </FormControl>
              </Grid>
              <Grid item xs={12} sm={4}>
                <FormControl size="small" fullWidth disabled={ubiLoading || ubiError || !ubi.bloque_id}>
                  <InputLabel>{t("ordenCompra.filters.space", "Espacio")}</InputLabel>
                  <Select label={t("ordenCompra.filters.space", "Espacio")} value={ubi.espacio_id || ""} onChange={handleUbiChange("espacio_id")}>
                    <MenuItem value=""><em>{t("common.labels.all", "Todos")}</em></MenuItem>
                    {ubiData?.espacios?.map(it => <MenuItem key={it.id} value={String(it.id)}>{it.nombre}</MenuItem>)}
                  </Select>
                </FormControl>
              </Grid>
              <Grid item xs={12} sm={4}>
                <FormControl size="small" fullWidth disabled={ubiLoading || ubiError || !ubi.espacio_id}>
                  <InputLabel>{t("ordenCompra.filters.warehouse", "Almacén")}</InputLabel>
                  <Select label={t("ordenCompra.filters.warehouse", "Almacén")} value={ubi.almacen_id || ""} onChange={handleUbiChange("almacen_id")}>
                    <MenuItem value=""><em>{t("common.labels.all", "Todos")}</em></MenuItem>
                    {ubiData?.almacenes?.map(it => <MenuItem key={it.id} value={String(it.id)}>{it.nombre}</MenuItem>)}
                  </Select>
                </FormControl>
              </Grid>
            </Grid>
          </Paper>
        </Grid>

        {/* Derecha: Datos de Orden + Tiempo */}
        <Grid item xs={12} md={5}>
          <Paper sx={boxStyles}>
            <Typography variant="h6" sx={titleStyles}>
              {t("ordenCompra.sections.orderData", "Datos de Orden")}
              {estadosLoading && <LinearProgress sx={{ flexGrow: 1, ml: 2, height: 2 }} />}
              {estadosError && (
                <IconButton size="small" onClick={fetchEstados} color="error" title={t("common.actions.retry", "Reintentar")}>
                  <RefreshIcon fontSize="small"/>
                </IconButton>
              )}
            </Typography>
            <Grid container spacing={2}>
              <Grid item xs={12}>
                <FormControl size="small" fullWidth>
                  <InputLabel>{t("ordenCompra.filters.order", "Pedido")}</InputLabel>
                  <Select name="pedido_id" value={pedido.pedido_id || ""} label={t("ordenCompra.filters.order", "Pedido")} onChange={handlePedidoChange("pedido_id")}>
                    <MenuItem value=""><em>{t("common.labels.all", "Todos")}</em></MenuItem>
                    {asArray(pedidos).map(p => <MenuItem key={p.id} value={String(p.id)}>{`${t("ordenCompra.filters.order", "Pedido")} ${p.id}`}</MenuItem>)}
                  </Select>
                </FormControl>
              </Grid>
              <Grid item xs={12}>
                <FormControl size="small" fullWidth disabled={estadosLoading || estadosError}>
                  <InputLabel>{t("ordenCompra.filters.status", "Estado de la orden")}</InputLabel>
                  <Select name="categoria_estado_id" value={pedido.categoria_estado_id || ""} label={t("ordenCompra.filters.status", "Estado de la orden")} onChange={handlePedidoChange("categoria_estado_id")}>
                    <MenuItem value=""><em>{t("common.labels.all", "Todos")}</em></MenuItem>
                    {estadosOC.map(e => <MenuItem key={e.id} value={String(e.id)}>{e.name ?? e.nombre ?? `Estado ${e.id}`}</MenuItem>)}
                  </Select>
                </FormControl>
              </Grid>
              {/* Tiempo — tercera fila, alineada con Bloque/Espacio/Almacén */}
              <Grid item xs={12}>
                <Box sx={{ display: "flex", alignItems: "center", gap: 2 }}>
                  <Typography variant="subtitle1" sx={{ fontWeight: 600, color: isDark ? "#e7f6f7" : "#173f39", whiteSpace: "nowrap" }}>
                    {t("ordenCompra.sections.time", "Tiempo")}
                  </Typography>
                  <Button 
                    variant="outlined" 
                    fullWidth 
                    onClick={handleOpenDate}
                    endIcon={<CalendarIcon />}
                    sx={{ 
                      justifyContent: "space-between", 
                      py: 1, 
                      color: isDark ? "#e7f6f7" : "#173f39",
                      borderColor: isDark ? "rgba(255,255,255,0.2)" : "rgba(0,0,0,0.23)",
                      textTransform: "none",
                      fontWeight: 500,
                      borderRadius: 2
                    }}
                  >
                    {formReporte.fecha_inicio && formReporte.fecha_fin 
                      ? `${toDateTimeStr(formReporte.fecha_inicio).split(" ")[0]} - ${toDateTimeStr(formReporte.fecha_fin).split(" ")[0]}`
                      : t("ordenCompra.filters.selectDates", "Seleccionar fechas")}
                  </Button>
                </Box>
              </Grid>
            </Grid>
            <Popover
              open={openDate}
              anchorEl={anchorElDate}
              onClose={handleCloseDate}
              anchorOrigin={{ vertical: 'bottom', horizontal: 'left' }}
              transformOrigin={{ vertical: 'top', horizontal: 'left' }}
              PaperProps={{ sx: { borderRadius: 3, mt: 1, maxWidth: 900 } }}
            >
              <Stack direction={{ xs: 'column', md: 'row' }} sx={{ p: 2 }}>
                <Box sx={{ minWidth: 200, pr: 2, borderRight: { md: `1px solid ${theme.palette.divider}` }, mb: { xs: 2, md: 0 } }}>
                  <Typography variant="subtitle2" sx={{ mb: 2, fontWeight: 600 }}>
                    {t("ordenCompra.filters.quickSelect", "Selección rápida")}
                  </Typography>
                  <Stack spacing={1}>
                    {["hoy", "semana", "mes", "trimestre", "anio"].map(type => (
                      <Button key={type} variant="text" size="small" fullWidth onClick={() => setQuickDate(type)} sx={{ justifyContent: "flex-start", textTransform: "none" }}>
                        {t(`ordenCompra.filters.quick.${type}`)}
                      </Button>
                    ))}
                  </Stack>
                </Box>
                <Box sx={{ pl: { md: 2 } }}>
                  <Typography variant="subtitle2" sx={{ mb: 2, fontWeight: 600 }}>
                    {t("ordenCompra.filters.customRange", "Rango personalizado")}
                  </Typography>
                  <Box sx={{ 
                    '.rdrCalendarWrapper': { 
                      color: '#000',
                      bgcolor: '#fff',
                      borderRadius: 2
                    } 
                  }}>
                    <DateRange
                      locale={i18n.language?.startsWith("en") ? enUS : es}
                      editableDateInputs={true}
                      onChange={handleSelectDateRange}
                      moveRangeOnFirstSelection={false}
                      ranges={[{
                        startDate: formReporte.fecha_inicio ? new Date(formReporte.fecha_inicio) : new Date(),
                        endDate: formReporte.fecha_fin ? new Date(formReporte.fecha_fin) : new Date(),
                        key: 'selection'
                      }]}
                      months={2}
                      direction="horizontal"
                      rangeColors={["#173f39"]}
                    />
                  </Box>
                </Box>
              </Stack>
            </Popover>
          </Paper>
        </Grid>
      </Grid>

      {/* Botones de accion */}
      <Box sx={{ display: "flex", justifyContent: "flex-end", mb: 4 }}>
        <Stack direction="row" spacing={2}>
          <Button 
            variant="contained" 
            color="primary" 
            startIcon={<SearchIcon />} 
            onClick={buscar}
            disabled={loadingSearch}
            sx={{ borderRadius: 2, px: 4, py: 1, bgcolor: "#173f39", "&:hover": { bgcolor: "#21534b" } }}
          >
            {t("common.actions.search", "Buscar")}
          </Button>
          <Button 
            variant="contained" 
            color="success" 
            startIcon={<DownloadIcon />} 
            onClick={() => setModalExportOpen(true)}
            sx={{ borderRadius: 2, px: 3 }}
          >
            {t("ordenCompra.actions.generate", "Generar Reporte")}
          </Button>
        </Stack>
      </Box>

      {ordenData ? (
        <>
          <VistaPreviaPDFOrdenCompra orden={ordenData} />
          <Box mt={4}>
            <Typography variant="h6" gutterBottom>{t("ordenCompra.grid.detailsTitle", "Artículos de la Orden")}</Typography>
            <GridArticuloOrdenCompra items={articulos} presentaciones={presentaciones} setSelectedRows={() => {}} setSelectedRow={() => {}} />
          </Box>
        </>
      ) : (
        <Box>
          <AppDataGrid rows={resultados} columns={columns} loading={loadingSearch} containerSx={{ minHeight: 200 }} autoHeight={true} />
        </Box>
      )}

      <Dialog open={modalExportOpen} onClose={() => !exporting && setModalExportOpen(false)} maxWidth="xs" fullWidth PaperProps={{ sx: { borderRadius: 4, p: 1 }}}>
        <DialogTitle sx={{ fontWeight: 700, textAlign: "center", pb: 1 }}>
          {t("ordenCompra.modal.exportTitle", "Generar Reporte")}
        </DialogTitle>
        <DialogContent sx={{ textAlign: "center", pb: 2 }}>
          {exporting ? (
            <Box sx={{ py: 3 }}>
              <Typography variant="body2" sx={{ mb: 2 }}>{t("ordenCompra.modal.exporting", "Generando documento...")}</Typography>
              <LinearProgress color="success" />
            </Box>
          ) : (
            <Box sx={{ py: 2 }}>
              <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
                {t("ordenCompra.modal.exportSub", "Seleccione el formato.")}
              </Typography>
              <Stack direction="row" spacing={2} justifyContent="center">
                <Button variant="outlined" color="error" size="large" onClick={() => generarReporte("PDF")} sx={{ width: 120, height: 100, display: "flex", flexDirection: "column", gap: 1, borderRadius: 3 }}>
                  <PdfIcon fontSize="large" />
                  PDF
                </Button>
                <Button variant="outlined" color="success" size="large" onClick={() => generarReporte("EXCEL")} sx={{ width: 120, height: 100, display: "flex", flexDirection: "column", gap: 1, borderRadius: 3 }}>
                  <ExcelIcon fontSize="large" />
                  Excel
                </Button>
              </Stack>
            </Box>
          )}
        </DialogContent>
        {!exporting && (
          <DialogActions sx={{ justifyContent: "center", pt: 0, pb: 2 }}>
            <Button onClick={() => setModalExportOpen(false)} color="inherit" sx={{ textTransform: "none" }}>{t("common.actions.cancel", "Cancelar")}</Button>
          </DialogActions>
        )}
      </Dialog>

      <Dialog open={previewOpen} onClose={() => setPreviewOpen(false)} fullWidth maxWidth="lg" PaperProps={{ sx: { borderRadius: 4 } }}>
        <DialogTitle sx={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
          {t("ordenCompra.modal.previewTitle", "Vista previa")}
          <IconButton onClick={() => setPreviewOpen(false)}><CloseIcon /></IconButton>
        </DialogTitle>
        <Divider />
        <DialogContent sx={{ p: 0, height: "80vh" }}>
          {previewUrl && <iframe src={previewUrl} width="100%" height="100%" title="PDF" style={{ border: "none" }} />}
        </DialogContent>
      </Dialog>

      <MessageSnackBar message={message} setMessage={setMessage} />
    </Box>
  );
}
