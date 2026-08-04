/*=============================================================================
 Nombre del archivo : re_pvn.jsx
 Descripcion        : Módulo de Reportes de Vencimiento de Productos.
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
  Paper, Divider, Popover, LinearProgress, Chip
} from "@mui/material";
import {
  Close as CloseIcon,
  Search as SearchIcon,
  Download as DownloadIcon,
  PictureAsPdf as PdfIcon,
  TableView as ExcelIcon,
  CalendarToday as CalendarIcon,
  Refresh as RefreshIcon,
  Warning as WarningIcon,
  ErrorOutline as ErrorOutlineIcon
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

export default function RE_productoVencimiento() {
  const { t, i18n } = useTranslation();
  const theme = useTheme();
  const isDark = theme.palette.mode === "dark";

  const empresaId = localStorage.getItem("empresaId");
  const token = localStorage.getItem("token");
  const headers = { headers: { Authorization: `Bearer ${token}` } };

  // ===== Utils
  const asArray = (x) => (Array.isArray(x) ? x : x?.content ?? x?.data ?? []);
  // Convert string IDs to integers for API calls (hook stores IDs as strings)
  const toInt = (v) => (v && v !== "" ? Number(v) : null);
  
  const toLocal = (val) => {
    if (!val) return "";
    const d = new Date(val);
    if (isNaN(d.getTime())) return String(val);
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
    // Start 1 year ago to include vencidos from the past
    const oneYearAgo = new Date(today.getFullYear() - 1, today.getMonth(), today.getDate());
    const y1 = oneYearAgo.getFullYear();
    const m1 = String(oneYearAgo.getMonth() + 1).padStart(2, '0');
    const d1 = String(oneYearAgo.getDate()).padStart(2, '0');
    
    // End 1 month from now to include products expiring soon
    const nextMonth = new Date(today.getFullYear(), today.getMonth() + 1, today.getDate());
    const y2 = nextMonth.getFullYear();
    const m2 = String(nextMonth.getMonth() + 1).padStart(2, '0');
    const d2 = String(nextMonth.getDate()).padStart(2, '0');
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
    fetchInitialData: fetchFiltrosIniciales,
    preloadData,
    loading: catLoading,
    error: catError,
  } = useUbicacionFilters({ empresaId, headers, autoselectSingle: true, reportType: "vencimiento-producto" });

  const [formReporte, setFormReporte] = useState({
    ...getInitDates()
  });
  
  const [filtrosProd, setFiltrosProd] = useState({
    categoria_id: "",
    producto_id: "",
    presentacion_id: "",
    estado: ""
  });

  // Datos catálogos producto
  const [categorias, setCategorias] = useState([]);
  const [productos, setProductos] = useState([]);
  const [presentaciones, setPresentaciones] = useState([]);

  useEffect(() => {
    if (preloadData) {
      setCategorias(asArray(preloadData.categorias));
    }
  }, [preloadData]);

  const fetchProductosPorCategoria = async (categoriaId) => {
    if (!categoriaId) { setProductos([]); setPresentaciones([]); return; }
    try {
      const res = await axios.get(`/v2/report/vencimiento-producto/productos`, { ...headers, params: { categoriaId } });
      setProductos(asArray(res.data));
      setPresentaciones([]);
    } catch { setProductos([]); }
  };

  const fetchPresentacionesPorProducto = async (productoId) => {
    if (!productoId) { setPresentaciones([]); return; }
    try {
      const res = await axios.get(`/v2/report/vencimiento-producto/presentaciones`, { ...headers, params: { productoId } });
      setPresentaciones(asArray(res.data));
    } catch { setPresentaciones([]); }
  };



  const handleProdChange = (field) => (e) => {
    const val = e.target.value;
    setFiltrosProd(p => ({ ...p, [field]: val }));
    if (field === 'categoria_id') {
      setFiltrosProd(p => ({ ...p, categoria_id: val, producto_id: '', presentacion_id: '' }));
      fetchProductosPorCategoria(val);
    }
    if (field === 'producto_id') {
      setFiltrosProd(p => ({ ...p, producto_id: val, presentacion_id: '' }));
      fetchPresentacionesPorProducto(val);
    }
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
    const y1 = today.getFullYear();
    const m1 = String(today.getMonth() + 1).padStart(2, '0');
    const d1 = String(today.getDate()).padStart(2, '0');
    fInicio = `${y1}-${m1}-${d1}T00:00`;
    
    let targetDate = new Date(today);
    if (type === 'hoy') {
      targetDate = today;
    } else if (type === 'dias7') {
      targetDate.setDate(today.getDate() + 7);
    } else if (type === 'dias15') {
      targetDate.setDate(today.getDate() + 15);
    } else if (type === 'mes') {
      targetDate.setMonth(today.getMonth() + 1);
    }
    
    const y2 = targetDate.getFullYear();
    const m2 = String(targetDate.getMonth() + 1).padStart(2, '0');
    const d2 = String(targetDate.getDate()).padStart(2, '0');
    fFin = `${y2}-${m2}-${d2}T23:59`;
    
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
    
    // Validar ubicación mínima (Sede o Almacén)
    if (!ubi.sede_id && !ubi.almacen_id) {
      setMessage({ open: true, severity: "warning", text: t("vencimiento.messages.noLocation", "Debe seleccionar al menos un nivel de ubicación (Sede o Almacén).") });
      return false;
    }

    if (!formReporte.fecha_inicio || !formReporte.fecha_fin) {
      setMessage({ open: true, severity: "warning", text: t("vencimiento.messages.emptyDates", "El rango de fechas es obligatorio.") });
      return false;
    }
    const ini = new Date(formReporte.fecha_inicio);
    const fin = new Date(formReporte.fecha_fin);
    if (ini > fin) {
      setErrors({ fechas_rango: true });
      setMessage({ open: true, severity: "warning", text: t("vencimiento.messages.invalidDateRange", "La fecha de inicio no puede ser mayor que la fecha fin.") });
      return false;
    }
    return true;
  };

  const calculateStatus = (fechaVencimiento) => {
    if (!fechaVencimiento) return null;
    const venc = new Date(fechaVencimiento);
    const hoy = new Date();
    hoy.setHours(0, 0, 0, 0);
    venc.setHours(0, 0, 0, 0);
    return venc <= hoy ? "vencido" : "proximo";
  };

  const buscar = async () => {
    setResultados([]);
    if (!validarFiltros()) return;

    setLoadingSearch(true);
    try {
      const payload = {
        paisId:         toInt(ubi.pais_id),
        departamentoId: toInt(ubi.departamento_id),
        municipioId:    toInt(ubi.municipio_id),
        sedeId:         toInt(ubi.sede_id),
        bloqueId:       toInt(ubi.bloque_id),
        espacioId:      toInt(ubi.espacio_id),
        almacenId:      toInt(ubi.almacen_id),
        categoriaId:    toInt(filtrosProd.categoria_id),
        productoId:     toInt(filtrosProd.producto_id),
        presentacionId: toInt(filtrosProd.presentacion_id),
        fechaInicio: toLocal(formReporte.fecha_inicio),
        fechaFin: toLocal(formReporte.fecha_fin),
        estado: filtrosProd.estado || "TODOS"
      };

      const res = await axios.post("/v2/report/vencimiento-producto/buscar", payload, headers);
      const lista = asArray(res.data?.resultados || res.data);
      setResultados(lista);
      if (lista.length === 0) {
        setMessage({ open: true, severity: "info", text: t("vencimiento.messages.noResults", "No se encontraron productos.") });
      } else {
        setMessage({ open: true, severity: "info", text: `${lista.length} ${t("vencimiento.messages.found", "resultado(s) encontrado(s).")}` });
      }
    } catch (error) {
      console.error(error);
      setMessage({
        open: true,
        severity: "error",
        text: t("vencimiento.messages.searchError", "Error al buscar datos de vencimiento.")
      });
    } finally {
      setLoadingSearch(false);
    }
  };

  const [exporting, setExporting] = useState(false);
  const [modalExportOpen, setModalExportOpen] = useState(false);
  const [previewUrl, setPreviewUrl] = useState("");
  const [previewOpen, setPreviewOpen] = useState(false);

  const generarReporte = async (formato) => {
    if (!validarFiltros()) return;
    setExporting(true);
    try {
      const payload = {
        paisId:         toInt(ubi.pais_id),
        departamentoId: toInt(ubi.departamento_id),
        municipioId:    toInt(ubi.municipio_id),
        sedeId:         toInt(ubi.sede_id),
        bloqueId:       toInt(ubi.bloque_id),
        espacioId:      toInt(ubi.espacio_id),
        almacenId:      toInt(ubi.almacen_id),
        categoriaId:    toInt(filtrosProd.categoria_id),
        productoId:     toInt(filtrosProd.producto_id),
        presentacionId: toInt(filtrosProd.presentacion_id),
        fechaInicio: toLocal(formReporte.fecha_inicio),
        fechaFin: toLocal(formReporte.fecha_fin),
        estado: filtrosProd.estado || "TODOS"
      };

      const res = await axios.post("/v2/report/vencimiento-producto/exportar", payload, {
        ...headers,
        params: { formato: formato },
        responseType: "blob"
      });
      
      const blob = new Blob([res.data]);
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `Reporte_Vencimiento_${formato}.${formato.toLowerCase() === 'excel' ? 'xlsx' : 'pdf'}`);
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      setMessage({ open: true, severity: "success", text: t("vencimiento.messages.exportSuccess", "Reporte descargado exitosamente.") });
    } catch (error) {
      console.error(error);
      setMessage({
        open: true,
        severity: "error",
        text: t("vencimiento.messages.exportError", "Error al exportar reporte.")
      });
    } finally {
      setExporting(false);
      setModalExportOpen(false);
    }
  };

  const columns = useMemo(() => [
    { field: 'kardexItemId', headerName: t("vencimiento.grid.id", "ID"), width: 80 },
    { field: 'producto', headerName: t("vencimiento.grid.nombre", "Producto"), flex: 1, minWidth: 200 },
    { field: 'sede', headerName: t("vencimiento.grid.sede", "Sede"), width: 150 },
    { field: 'almacen', headerName: t("vencimiento.grid.almacen", "Almacén"), width: 150 },
    { 
      field: 'fechaVencimiento', 
      headerName: t("vencimiento.grid.fechaVencimiento", "Fecha Vencimiento"), 
      width: 160,
      valueFormatter: (params) => params.value ? toLocal(params.value) : '' 
    },
    { field: 'cantidad', headerName: t("vencimiento.grid.cantidad", "Cantidad"), width: 120, align: 'right', headerAlign: 'right' },
    { 
      field: 'estado', 
      headerName: t("vencimiento.columns.status", "Estado"), 
      width: 180,
      renderCell: (params) => {
        const code = params.row.estadoCodigo;
        if (code === "VENCIDO") {
          return <Chip icon={<ErrorOutlineIcon />} label={params.row.estado || t("vencimiento.statusOptions.vencido", "Vencido")} color="error" size="small" />;
        } else if (code === "PROXIMO_A_VENCER") {
          return <Chip icon={<WarningIcon />} label={params.row.estado || t("vencimiento.statusOptions.proximo", "Próximo a vencer")} color="warning" size="small" />;
        }
        return params.row.estado || '';
      }
    }
  ], [t]);

  const boxStyles = {
    p: 3, 
    borderRadius: 4, 
    bgcolor: isDark ? "rgba(255,255,255,0.02)" : "#fff",
    boxShadow: isDark ? "0 4px 12px rgba(0,0,0,0.2)" : "0 4px 12px rgba(23,63,57,0.06)"
  };
  const titleStyles = { fontWeight: 600, mb: 2, color: isDark ? "#e7f6f7" : "#173f39", display: "flex", alignItems: "center", gap: 1 };

  const productosFiltrados = productos;

  const presentacionesFiltradas = presentaciones;

  return (
    <Box sx={{ width: "100%", p: { xs: 2, md: 4 }, color: "text.primary" }}>
      <Box sx={{ display: "flex", justifyContent: "center", alignItems: "center", mb: 4 }}>
        <Typography variant="h4" sx={{ fontWeight: 700, color: isDark ? "#fff" : "#173f39" }}>
          {t("vencimiento.title", "Reporte de Vencimiento de Producto")}
        </Typography>
      </Box>

      <Grid container spacing={3} mb={3} alignItems="flex-start">
        <Grid item xs={12} md={7}>
          <Paper sx={boxStyles}>
            <Typography variant="h6" sx={titleStyles}>
              {t("vencimiento.sections.location", "Ubicación")}
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
                  <InputLabel>{t("vencimiento.filters.country", "País")}</InputLabel>
                  <Select label={t("vencimiento.filters.country", "País")} value={ubi.pais_id || ""} onChange={handleUbiChange("pais_id")}>
                    <MenuItem value=""><em>{t("common.labels.all", "Todos")}</em></MenuItem>
                    {ubiData?.paises?.map(it => <MenuItem key={it.id} value={String(it.id)}>{it.nombre}</MenuItem>)}
                  </Select>
                </FormControl>
              </Grid>
              <Grid item xs={12} sm={4}>
                <FormControl size="small" fullWidth disabled={ubiLoading || ubiError || !ubi.pais_id}>
                  <InputLabel>{t("vencimiento.filters.department", "Departamento")}</InputLabel>
                  <Select label={t("vencimiento.filters.department", "Departamento")} value={ubi.departamento_id || ""} onChange={handleUbiChange("departamento_id")}>
                    <MenuItem value=""><em>{t("common.labels.all", "Todos")}</em></MenuItem>
                    {ubiData?.departamentos?.map(it => <MenuItem key={it.id} value={String(it.id)}>{it.nombre}</MenuItem>)}
                  </Select>
                </FormControl>
              </Grid>
              <Grid item xs={12} sm={4}>
                <FormControl size="small" fullWidth disabled={ubiLoading || ubiError || !ubi.departamento_id}>
                  <InputLabel>{t("vencimiento.filters.municipality", "Municipio")}</InputLabel>
                  <Select label={t("vencimiento.filters.municipality", "Municipio")} value={ubi.municipio_id || ""} onChange={handleUbiChange("municipio_id")}>
                    <MenuItem value=""><em>{t("common.labels.all", "Todos")}</em></MenuItem>
                    {ubiData?.municipios?.map(it => <MenuItem key={it.id} value={String(it.id)}>{it.nombre}</MenuItem>)}
                  </Select>
                </FormControl>
              </Grid>
              <Grid item xs={12} sm={4}>
                <FormControl size="small" fullWidth disabled={ubiLoading || ubiError}>
                  <InputLabel>{t("vencimiento.filters.headquarters", "Sede")}</InputLabel>
                  <Select label={t("vencimiento.filters.headquarters", "Sede")} value={ubi.sede_id || ""} onChange={handleUbiChange("sede_id")}>
                    <MenuItem value=""><em>{t("common.labels.all", "Todas")}</em></MenuItem>
                    {ubiData?.sedes?.map(it => <MenuItem key={it.id} value={String(it.id)}>{it.nombre}</MenuItem>)}
                  </Select>
                </FormControl>
              </Grid>
              <Grid item xs={12} sm={4}>
                <FormControl size="small" fullWidth disabled={ubiLoading || ubiError || !ubi.sede_id}>
                  <InputLabel>{t("vencimiento.filters.block", "Bloque")}</InputLabel>
                  <Select label={t("vencimiento.filters.block", "Bloque")} value={ubi.bloque_id || ""} onChange={handleUbiChange("bloque_id")}>
                    <MenuItem value=""><em>{t("common.labels.all", "Todos")}</em></MenuItem>
                    {ubiData?.bloques?.map(it => <MenuItem key={it.id} value={String(it.id)}>{it.nombre}</MenuItem>)}
                  </Select>
                </FormControl>
              </Grid>
              <Grid item xs={12} sm={4}>
                <FormControl size="small" fullWidth disabled={ubiLoading || ubiError || !ubi.bloque_id}>
                  <InputLabel>{t("vencimiento.filters.space", "Espacio")}</InputLabel>
                  <Select label={t("vencimiento.filters.space", "Espacio")} value={ubi.espacio_id || ""} onChange={handleUbiChange("espacio_id")}>
                    <MenuItem value=""><em>{t("common.labels.all", "Todos")}</em></MenuItem>
                    {ubiData?.espacios?.map(it => <MenuItem key={it.id} value={String(it.id)}>{it.nombre}</MenuItem>)}
                  </Select>
                </FormControl>
              </Grid>
              <Grid item xs={12} sm={4}>
                <FormControl size="small" fullWidth disabled={ubiLoading || ubiError || !ubi.espacio_id}>
                  <InputLabel>{t("vencimiento.filters.warehouse", "Almacén")}</InputLabel>
                  <Select label={t("vencimiento.filters.warehouse", "Almacén")} value={ubi.almacen_id || ""} onChange={handleUbiChange("almacen_id")}>
                    <MenuItem value=""><em>{t("common.labels.all", "Todos")}</em></MenuItem>
                    {ubiData?.almacenes?.map(it => <MenuItem key={it.id} value={String(it.id)}>{it.nombre}</MenuItem>)}
                  </Select>
                </FormControl>
              </Grid>
            </Grid>
          </Paper>
        </Grid>

        <Grid item xs={12} md={5}>
          <Paper sx={boxStyles}>
            <Typography variant="h6" sx={titleStyles}>
              {t("vencimiento.sections.productData", "Producto / Estado")}
              {catLoading && <LinearProgress sx={{ flexGrow: 1, ml: 2, height: 2 }} />}
              {catError && (
                <IconButton size="small" onClick={fetchFiltrosIniciales} color="error" title={t("common.actions.retry", "Reintentar")}>
                  <RefreshIcon fontSize="small"/>
                </IconButton>
              )}
            </Typography>
            <Grid container spacing={2}>
              <Grid item xs={12} sm={6}>
                <FormControl size="small" fullWidth disabled={catLoading || catError}>
                  <InputLabel>{t("vencimiento.filters.category", "Categoría")}</InputLabel>
                  <Select value={filtrosProd.categoria_id || ""} label={t("vencimiento.filters.category", "Categoría")} onChange={handleProdChange("categoria_id")}>
                    <MenuItem value=""><em>{t("common.labels.all", "Todos")}</em></MenuItem>
                    {categorias.map(c => <MenuItem key={c.id} value={String(c.id)}>{c.nombre ?? c.name}</MenuItem>)}
                  </Select>
                </FormControl>
              </Grid>
              <Grid item xs={12} sm={6}>
                <FormControl size="small" fullWidth disabled={catLoading || catError}>
                  <InputLabel>{t("vencimiento.filters.product", "Producto")}</InputLabel>
                  <Select value={filtrosProd.producto_id || ""} label={t("vencimiento.filters.product", "Producto")} onChange={handleProdChange("producto_id")}>
                    <MenuItem value=""><em>{t("common.labels.all", "Todos")}</em></MenuItem>
                    {productosFiltrados.map(p => <MenuItem key={p.id} value={String(p.id)}>{p.nombre ?? p.name}</MenuItem>)}
                  </Select>
                </FormControl>
              </Grid>
              <Grid item xs={12} sm={6}>
                <FormControl size="small" fullWidth disabled={catLoading || catError || !filtrosProd.producto_id}>
                  <InputLabel>{t("vencimiento.filters.presentation", "Presentación")}</InputLabel>
                  <Select value={filtrosProd.presentacion_id || ""} label={t("vencimiento.filters.presentation", "Presentación")} onChange={handleProdChange("presentacion_id")}>
                    <MenuItem value=""><em>{t("common.labels.all", "Todos")}</em></MenuItem>
                    {presentacionesFiltradas.map(p => <MenuItem key={p.id} value={String(p.id)}>{p.nombre ?? p.name}</MenuItem>)}
                  </Select>
                </FormControl>
              </Grid>
              <Grid item xs={12} sm={6}>
                <FormControl size="small" fullWidth>
                  <InputLabel>{t("vencimiento.filters.status", "Estado")}</InputLabel>
                  <Select value={filtrosProd.estado || ""} label={t("vencimiento.filters.status", "Estado")} onChange={handleProdChange("estado")}>
                    <MenuItem value="TODOS">{t("vencimiento.filters.todos", "Todos")}</MenuItem>
                    <MenuItem value="PROXIMO_A_VENCER">{t("vencimiento.filters.proximo", "Próximo a Vencer")}</MenuItem>
                    <MenuItem value="VENCIDO">{t("vencimiento.filters.vencido", "Vencido")}</MenuItem>
                  </Select>
                </FormControl>
              </Grid>
              <Grid item xs={12}>
                <Box sx={{ display: "flex", alignItems: "center", gap: 2 }}>
                  <Typography variant="subtitle1" sx={{ fontWeight: 600, color: isDark ? "#e7f6f7" : "#173f39", whiteSpace: "nowrap" }}>
                    {t("vencimiento.sections.time", "Fechas de Vencimiento")}
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
                      : t("vencimiento.filters.selectDates", "Seleccionar fechas")}
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
                    {t("vencimiento.filters.quickSelect", "Selección rápida")}
                  </Typography>
                  <Stack spacing={1}>
                    {["hoy", "dias7", "dias15", "mes"].map(type => (
                      <Button key={type} variant="text" size="small" fullWidth onClick={() => setQuickDate(type)} sx={{ justifyContent: "flex-start", textTransform: "none" }}>
                        {t(`vencimiento.filters.quick.${type}`)}
                      </Button>
                    ))}
                  </Stack>
                </Box>
                <Box sx={{ pl: { md: 2 } }}>
                  <Typography variant="subtitle2" sx={{ mb: 2, fontWeight: 600 }}>
                    {t("vencimiento.filters.customRange", "Rango personalizado")}
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
        </Stack>
      </Box>

      <Box>
        <AppDataGrid 
          rows={resultados} 
          columns={columns} 
          loading={loadingSearch} 
          containerSx={{ minHeight: 200 }} 
          autoHeight={true}
          getRowId={(row) => row.kardexItemId ?? row.id ?? Math.random()}
        />
      </Box>

      <MessageSnackBar message={message} setMessage={setMessage} />
    </Box>
  );
}
