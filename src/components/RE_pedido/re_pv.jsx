/*=============================================================================
 Nombre del archivo : re_pv.jsx (Reporte de Pedido)
 Descripcion        : Módulo de Reportes de Pedidos Internos.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |   Fecha    | Versión |      Autor           | Descripción del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2026-07-09 | 0.4.0   | Jeisson Sanchez      | Refactor UI/UX filtros, HU  |
 +------------+---------+----------------------+-----------------------------+
 | 2026-07-11 | 0.4.1   | Jeisson Sanchez      | Rediseño alineación filtros |
 +------------+---------+----------------------+-----------------------------+
 | 2026-07-18 | 0.4.2   | Jeisson Sanchez      | Soporte Dark Mode Picker    |
 +------------+---------+----------------------+-----------------------------+
 | 2026-07-27 | 0.5.0   | Jeisson Sanchez      | Integración endpoints API   |
 +------------+---------+----------------------+-----------------------------+
=============================================================================*/
import React, { useEffect, useState, useMemo } from "react";
import {
  Box, Typography, Button, Stack, Grid,
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

export default function RE_pedido() {
  const { t, i18n } = useTranslation();
  const theme = useTheme();
  const isDark = theme.palette.mode === "dark";

  const empresaId = localStorage.getItem("empresaId");
  const token = localStorage.getItem("token");
  const headers = { headers: { Authorization: `Bearer ${token}` } };

  const asArray = (x) => (Array.isArray(x) ? x : x?.content ?? x?.data ?? []);
  // Convert string IDs to integers for API calls (hook stores IDs as strings)
  const toInt = (v) => (v && v !== "" ? Number(v) : null);
  
  const getFecha = (p) => p?.fechaHora ?? p?.pedFechaHora ?? p?.fecha ?? p?.createdAt ?? p?.ped_fecha_hora ?? null;

  const toLocal = (val) => {
    if (!val) return "";
    const d = new Date(val);
    if (isNaN(d.getTime())) return String(val);
    const y = d.getFullYear();
    const m = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    const h = String(d.getHours()).padStart(2, '0');
    const min = String(d.getMinutes()).padStart(2, '0');
    return `${y}-${m}-${day} ${h}:${min}`;
  };

  const toDateStr = (val) => {
    if (!val) return "";
    const d = new Date(val);
    if (isNaN(d.getTime())) return "";
    const y = d.getFullYear();
    const m = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${y}-${m}-${day}`;
  };

  const toDateTimeStr = (val, end = false) => {
    if (!val) return null;
    const [d, t = "00:00"] = String(val).split("T");
    const time = end ? "23:59:59" : `${t}:00`;
    return `${d} ${time}`;
  };

  const getInitDates = () => {
    const today = new Date();
    const y1 = today.getFullYear();
    const m1 = String(today.getMonth() + 1).padStart(2, '0');
    const d1 = String(today.getDate()).padStart(2, '0');
    return {
      fecha_inicio: `${y1}-${m1}-${d1}T00:00`,
      fecha_fin: `${y1}-${m1}-${d1}T23:59`,
    };
  };

  // ===== Hook unificado
  const {
    form: ubi,
    handleChange: handleUbiChange,
    data: ubiData,
    loading: ubiLoading,
    error: ubiError,
    fetchFiltrosIniciales: fetchCatalogos,
    preloadData,
    loading: catLoading,
    error: catError,
  } = useUbicacionFilters({ empresaId, headers, autoselectSingle: true, reportType: "pedido" });

  const [formReporte, setFormReporte] = useState({
    ...getInitDates()
  });
  
  const [pedidoFiltros, setPedidoFiltros] = useState({
    pedido_id: "",
    pedido_estado_id: ""
  });

  // Datos catálogos pedido
  const [pedidosBase, setPedidosBase] = useState([]);
  const [pedidoEstados, setPedidoEstados] = useState([]);

  useEffect(() => {
    if (preloadData) {
      setPedidosBase(asArray(preloadData.pedidos));
      setPedidoEstados(asArray(preloadData.estados));
    }
  }, [preloadData]);

  const handlePedidoChange = (field) => (e) => {
    setPedidoFiltros(p => ({ ...p, [field]: e.target.value }));
  };

  // UI state
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
    
    let targetDate = new Date(today);
    if (type === 'hoy') {
      targetDate = today;
    } else if (type === 'semana') {
      targetDate.setDate(today.getDate() - 7);
    } else if (type === 'quincena') {
      targetDate.setDate(today.getDate() - 15);
    } else if (type === 'mes') {
      targetDate.setMonth(today.getMonth() - 1);
    }
    
    const y1 = targetDate.getFullYear();
    const m1 = String(targetDate.getMonth() + 1).padStart(2, '0');
    const d1 = String(targetDate.getDate()).padStart(2, '0');
    fInicio = `${y1}-${m1}-${d1}T00:00`;
    
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

  const validarFiltros = () => {
    setErrors({ fechas_rango: false });
    
    if (formReporte.fecha_inicio && formReporte.fecha_fin) {
      const ini = new Date(formReporte.fecha_inicio);
      const fin = new Date(formReporte.fecha_fin);
      if (ini > fin) {
        setErrors({ fechas_rango: true });
        setMessage({ open: true, severity: "warning", text: t("pedido.messages.invalidDates", "La fecha de inicio no puede ser mayor que la fin.") });
        return false;
      }
    }
    return true;
  };

  const buscar = async () => {
    setResultados([]);
    if (!validarFiltros()) return;

    setLoadingSearch(true);
    try {
      const payload = {
        pedidoIds: pedidoFiltros.pedido_id ? [parseInt(pedidoFiltros.pedido_id, 10)] : [],
        estadoId: toInt(pedidoFiltros.pedido_estado_id),
        paisId:         toInt(ubi.pais_id),
        departamentoId: toInt(ubi.departamento_id),
        municipioId:    toInt(ubi.municipio_id),
        sedeId:         toInt(ubi.sede_id),
        bloqueId:       toInt(ubi.bloque_id),
        espacioId:      toInt(ubi.espacio_id),
        almacenId:      toInt(ubi.almacen_id),
        fechaInicio: formReporte.fecha_inicio ? toDateStr(formReporte.fecha_inicio) : null,
        fechaFin: formReporte.fecha_fin ? toDateStr(formReporte.fecha_fin) : null
      };

      const res = await axios.post("/v2/report/pedido/resumen", payload, headers);
      const lista = asArray(res.data?.pedidos || res.data);
      setResultados(lista);
      
      if (lista.length === 0) {
        setMessage({ open: true, severity: "info", text: t("pedido.messages.noOrders", "No se encontraron pedidos.") });
      } else {
        setMessage({ open: true, severity: "info", text: t("pedido.messages.loaded", { count: lista.length }, `Mostrando ${lista.length} pedido(s).`) });
      }
    } catch (err) {
      console.error(err);
      setMessage({ open: true, severity: "error", text: t("pedido.messages.errorFetching", "Error al buscar.") });
    } finally {
      setLoadingSearch(false);
    }
  };

  // Export
  const [modalExportOpen, setModalExportOpen] = useState(false);
  const [exporting, setExporting] = useState(false);
  const [previewUrl, setPreviewUrl] = useState("");
  const [previewOpen, setPreviewOpen] = useState(false);

  const generarReporte = async (formato = "PDF") => {
    if (!validarFiltros()) return;
    setExporting(true);
    try {
      const payload = {
        pedidoIds: pedidoFiltros.pedido_id ? [parseInt(pedidoFiltros.pedido_id, 10)] : [],
        estadoId: toInt(pedidoFiltros.pedido_estado_id),
        paisId:         toInt(ubi.pais_id),
        departamentoId: toInt(ubi.departamento_id),
        municipioId:    toInt(ubi.municipio_id),
        sedeId:         toInt(ubi.sede_id),
        bloqueId:       toInt(ubi.bloque_id),
        espacioId:      toInt(ubi.espacio_id),
        almacenId:      toInt(ubi.almacen_id),
        fechaInicio: formReporte.fecha_inicio ? toDateStr(formReporte.fecha_inicio) : null,
        fechaFin: formReporte.fecha_fin ? toDateStr(formReporte.fecha_fin) : null
      };
      
      const res = await axios({
        url: "/v2/report/pedido/exportar",
        method: "POST",
        data: payload,
        responseType: "blob",
        ...headers,
        params: { formato: formato }
      });
      
      const mimeType = formato === "EXCEL" ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" : "application/pdf";
      const blob = new Blob([res.data], { type: mimeType });
      const objUrl = window.URL.createObjectURL(blob);
      
      if (formato === "EXCEL") {
        const a = document.createElement("a");
        a.href = objUrl;
        a.download = `Pedidos_${new Date().getTime()}.xlsx`;
        a.click();
        URL.revokeObjectURL(objUrl);
        setMessage({ open: true, severity: "success", text: t("pedido.messages.excelSuccess", "Excel descargado.") });
      } else {
        if (previewUrl) URL.revokeObjectURL(previewUrl);
        setPreviewUrl(objUrl);
        setPreviewOpen(true);
        setMessage({ open: true, severity: "success", text: t("pedido.messages.pdfSuccess", "PDF generado.") });
      }
    } catch (err) {
      console.error(err);
      setMessage({ open: true, severity: "error", text: t("pedido.messages.exportError", "Error al exportar.") });
    } finally {
      setExporting(false);
      setModalExportOpen(false);
    }
  };

  // Columns for AppDataGrid
  const columns = useMemo(() => [
    { field: "pedidoId", headerName: t("pedido.grid.id", "ID Pedido"), width: 90 },
    { field: "fechaPedido", headerName: t("pedido.grid.fecha", "Fecha"), width: 180, valueFormatter: (params) => toLocal(params.value) },
    { field: "estado", headerName: t("pedido.grid.estado", "Estado"), width: 140 },
    { field: "almacen", headerName: t("pedido.grid.almacen", "Almacén"), flex: 1, minWidth: 200 },
    { field: "cantidadProductos", headerName: t("pedido.grid.totalProducts", "Variedad Prods"), width: 150 },
    { field: "totalCantidad", headerName: t("pedido.grid.totalUnits", "Total Unidades"), width: 150, align: "right", headerAlign: "right" }
  ], [t]);

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
          {t("pedido.title", "Reporte de Pedido")}
        </Typography>
      </Box>

      <Grid container spacing={3} mb={3} alignItems="flex-start">
        {/* Ubicacion (Izquierda) */}
        <Grid item xs={12} md={7}>
          <Paper sx={boxStyles}>
            <Typography variant="h6" sx={titleStyles}>
              {t("pedido.sections.location", "Ubicación")}
              {ubiLoading && <LinearProgress sx={{ flexGrow: 1, ml: 2, height: 2 }} />}
              {ubiError && (
                <IconButton size="small" onClick={fetchFiltrosIniciales} color="error" title={t("common.actions.retry", "Reintentar")}>
                  <RefreshIcon fontSize="small"/>
                </IconButton>
              )}
            </Typography>
            <Grid container spacing={2}>
              <Grid item xs={12}>
                <FormControl size="small" fullWidth disabled={ubiLoading || ubiError}>
                  <InputLabel>{t("pedido.filters.country", "País")}</InputLabel>
                  <Select label={t("pedido.filters.country", "País")} value={ubi.pais_id || ""} onChange={handleUbiChange("pais_id")}>
                    <MenuItem value=""><em>{t("common.labels.all", "Todos")}</em></MenuItem>
                    {ubiData?.paises?.map(it => <MenuItem key={it.id} value={String(it.id)}>{it.nombre}</MenuItem>)}
                  </Select>
                </FormControl>
              </Grid>
              <Grid item xs={12} sm={4}>
                <FormControl size="small" fullWidth disabled={ubiLoading || ubiError || !ubi.pais_id}>
                  <InputLabel>{t("pedido.filters.department", "Departamento")}</InputLabel>
                  <Select label={t("pedido.filters.department", "Departamento")} value={ubi.departamento_id || ""} onChange={handleUbiChange("departamento_id")}>
                    <MenuItem value=""><em>{t("common.labels.all", "Todos")}</em></MenuItem>
                    {ubiData?.departamentos?.map(it => <MenuItem key={it.id} value={String(it.id)}>{it.nombre}</MenuItem>)}
                  </Select>
                </FormControl>
              </Grid>
              <Grid item xs={12} sm={4}>
                <FormControl size="small" fullWidth disabled={ubiLoading || ubiError || !ubi.departamento_id}>
                  <InputLabel>{t("pedido.filters.municipality", "Municipio")}</InputLabel>
                  <Select label={t("pedido.filters.municipality", "Municipio")} value={ubi.municipio_id || ""} onChange={handleUbiChange("municipio_id")}>
                    <MenuItem value=""><em>{t("common.labels.all", "Todos")}</em></MenuItem>
                    {ubiData?.municipios?.map(it => <MenuItem key={it.id} value={String(it.id)}>{it.nombre}</MenuItem>)}
                  </Select>
                </FormControl>
              </Grid>
              <Grid item xs={12} sm={4}>
                <FormControl size="small" fullWidth disabled={ubiLoading || ubiError}>
                  <InputLabel>{t("pedido.filters.headquarters", "Sede")}</InputLabel>
                  <Select label={t("pedido.filters.headquarters", "Sede")} value={ubi.sede_id || ""} onChange={handleUbiChange("sede_id")}>
                    <MenuItem value=""><em>{t("common.labels.all", "Todas")}</em></MenuItem>
                    {ubiData?.sedes?.map(it => <MenuItem key={it.id} value={String(it.id)}>{it.nombre}</MenuItem>)}
                  </Select>
                </FormControl>
              </Grid>
              <Grid item xs={12} sm={4}>
                <FormControl size="small" fullWidth disabled={ubiLoading || ubiError || !ubi.sede_id}>
                  <InputLabel>{t("pedido.filters.block", "Bloque")}</InputLabel>
                  <Select label={t("pedido.filters.block", "Bloque")} value={ubi.bloque_id || ""} onChange={handleUbiChange("bloque_id")}>
                    <MenuItem value=""><em>{t("common.labels.all", "Todos")}</em></MenuItem>
                    {ubiData?.bloques?.map(it => <MenuItem key={it.id} value={String(it.id)}>{it.nombre}</MenuItem>)}
                  </Select>
                </FormControl>
              </Grid>
              <Grid item xs={12} sm={4}>
                <FormControl size="small" fullWidth disabled={ubiLoading || ubiError || !ubi.bloque_id}>
                  <InputLabel>{t("pedido.filters.space", "Espacio")}</InputLabel>
                  <Select label={t("pedido.filters.space", "Espacio")} value={ubi.espacio_id || ""} onChange={handleUbiChange("espacio_id")}>
                    <MenuItem value=""><em>{t("common.labels.all", "Todos")}</em></MenuItem>
                    {ubiData?.espacios?.map(it => <MenuItem key={it.id} value={String(it.id)}>{it.nombre}</MenuItem>)}
                  </Select>
                </FormControl>
              </Grid>
              <Grid item xs={12} sm={4}>
                <FormControl size="small" fullWidth disabled={ubiLoading || ubiError || !ubi.espacio_id}>
                  <InputLabel>{t("pedido.filters.warehouse", "Almacén")}</InputLabel>
                  <Select label={t("pedido.filters.warehouse", "Almacén")} value={ubi.almacen_id || ""} onChange={handleUbiChange("almacen_id")}>
                    <MenuItem value=""><em>{t("common.labels.all", "Todos")}</em></MenuItem>
                    {ubiData?.almacenes?.map(it => <MenuItem key={it.id} value={String(it.id)}>{it.nombre}</MenuItem>)}
                  </Select>
                </FormControl>
              </Grid>
            </Grid>
          </Paper>
        </Grid>

        {/* Derecha: Datos Pedido + Tiempo */}
        <Grid item xs={12} md={5}>
          <Paper sx={boxStyles}>
            <Typography variant="h6" sx={titleStyles}>
              {t("pedido.sections.orderData", "Datos del Pedido")}
              {catLoading && <LinearProgress sx={{ flexGrow: 1, ml: 2, height: 2 }} />}
              {catError && (
                <IconButton size="small" onClick={fetchCatalogos} color="error" title={t("common.actions.retry", "Reintentar")}>
                  <RefreshIcon fontSize="small"/>
                </IconButton>
              )}
            </Typography>
            <Grid container spacing={2}>
              <Grid item xs={12}>
                <FormControl size="small" fullWidth disabled={catLoading || catError}>
                  <InputLabel>{t("pedido.filters.orderId", "Pedido")}</InputLabel>
                  <Select value={pedidoFiltros.pedido_id || ""} label={t("pedido.filters.orderId", "Pedido")} onChange={handlePedidoChange("pedido_id")}>
                    <MenuItem value=""><em>{t("common.labels.all", "Todos")}</em></MenuItem>
                    {pedidosBase.map(p => <MenuItem key={p.id} value={String(p.id)}>{`Pedido ${p.id}`}</MenuItem>)}
                  </Select>
                </FormControl>
              </Grid>
              <Grid item xs={12}>
                <FormControl size="small" fullWidth disabled={catLoading || catError}>
                  <InputLabel>{t("pedido.filters.orderStatus", "Estado del Pedido")}</InputLabel>
                  <Select value={pedidoFiltros.pedido_estado_id || ""} label={t("pedido.filters.orderStatus", "Estado del Pedido")} onChange={handlePedidoChange("pedido_estado_id")}>
                    <MenuItem value=""><em>{t("common.labels.all", "Todos")}</em></MenuItem>
                    {pedidoEstados.map(e => <MenuItem key={e.id} value={String(e.id)}>{e.nombre || e.name}</MenuItem>)}
                  </Select>
                </FormControl>
              </Grid>
              {/* Tiempo — tercera fila, alineada con Bloque/Espacio/Almacén */}
              <Grid item xs={12}>
                <Box sx={{ display: "flex", alignItems: "center", gap: 2 }}>
                  <Typography variant="subtitle1" sx={{ fontWeight: 600, color: isDark ? "#e7f6f7" : "#173f39", whiteSpace: "nowrap" }}>
                    {t("pedido.sections.time", "Tiempo")}
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
                      : t("pedido.filters.selectDates", "Seleccionar fechas")}
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
                    {t("pedido.filters.quickSelect", "Selección rápida")}
                  </Typography>
                  <Stack spacing={1}>
                    {["hoy", "semana", "quincena", "mes"].map(type => (
                      <Button key={type} variant="text" size="small" fullWidth onClick={() => setQuickDate(type)} sx={{ justifyContent: "flex-start", textTransform: "none" }}>
                        {t(`pedido.filters.quick.${type}`)}
                      </Button>
                    ))}
                  </Stack>
                </Box>
                <Box sx={{ pl: { md: 2 } }}>
                  <Typography variant="subtitle2" sx={{ mb: 2, fontWeight: 600 }}>
                    {t("pedido.filters.customRange", "Rango personalizado")}
                  </Typography>
                  <Box sx={{ 
                    '.rdrCalendarWrapper': { 
                      color: isDark ? '#e7f6f7' : '#000',
                      bgcolor: isDark ? 'transparent' : '#fff',
                      borderRadius: 2
                    },
                    '.rdrDayNumber span': { color: isDark ? '#e7f6f7' : '#1d2429' },
                    '.rdrDayPassive .rdrDayNumber span': { color: isDark ? 'rgba(255,255,255,0.3)' : '#d5dce0' },
                    '.rdrMonthAndYearPickers select': { color: isDark ? '#e7f6f7' : '#3e484f' },
                    '.rdrNextPrevButton': { background: isDark ? 'rgba(255,255,255,0.1)' : '#eff2f7' },
                    '.rdrWeekDay': { color: isDark ? 'rgba(255,255,255,0.7)' : '#849095' },
                    '.rdrDateDisplayWrapper': { backgroundColor: isDark ? 'transparent' : '#eff2f7' },
                    '.rdrDateDisplayItem': { backgroundColor: isDark ? 'rgba(255,255,255,0.1)' : '#fff', boxShadow: isDark ? 'none' : '0 1px 2px 0 rgba(35,57,66,.21)', borderColor: isDark ? 'rgba(255,255,255,0.2)' : 'transparent' },
                    '.rdrDateDisplayItemActive': { borderColor: isDark ? '#fff' : 'transparent' },
                    '.rdrDateDisplayItem input': { color: isDark ? '#fff' : '#333' }
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
            {t("pedido.actions.generate", "Generar Reporte")}
          </Button>
        </Stack>
      </Box>

      <Box>
        <AppDataGrid 
          rows={resultados} 
          columns={columns} 
          loading={loadingSearch} 
          containerSx={{ minHeight: 200 }} 
          autoHeight={true}
          getRowId={(row) => row.pedidoId ?? row.id}
        />
      </Box>

      <Dialog open={modalExportOpen} onClose={() => !exporting && setModalExportOpen(false)} maxWidth="xs" fullWidth PaperProps={{ sx: { borderRadius: 4, p: 1 }}}>
        <DialogTitle sx={{ fontWeight: 700, textAlign: "center", pb: 1 }}>
          {t("pedido.modal.exportTitle", "Generar Reporte")}
        </DialogTitle>
        <DialogContent sx={{ textAlign: "center", pb: 2 }}>
          {exporting ? (
            <Box sx={{ py: 3 }}>
              <Typography variant="body2" sx={{ mb: 2 }}>{t("pedido.modal.exporting", "Generando documento...")}</Typography>
              <LinearProgress color="success" />
            </Box>
          ) : (
            <Box sx={{ py: 2 }}>
              <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
                {t("pedido.modal.exportSub", "Seleccione el formato.")}
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
          {t("pedido.modal.previewTitle", "Vista previa")}
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
