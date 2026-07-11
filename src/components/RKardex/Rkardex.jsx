/*=============================================================================
 Nombre del archivo : Rkardex.jsx
 Descripcion        : Módulo de Reportes de Kardex.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |   Fecha    | Versión |      Autor           | Descripción del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2026-06-17 | 0.4.0   | Jeisson Sanchez      | Refactor UI/UX filtros, HU  |
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

export default function Rkardex() {
  const { t, i18n } = useTranslation();
  const theme = useTheme();
  const isDark = theme.palette.mode === "dark";

  const empresaId = localStorage.getItem("empresaId");
  const token = localStorage.getItem("token");
  const headers = { headers: { Authorization: `Bearer ${token}` } };

  // Utils
  const asArray = (x) => (Array.isArray(x) ? x : x?.content ?? x?.data ?? []);
  const getFechaKdx = (o) => o?.karFechaHora ?? o?.fechaHora ?? o?.fecha ?? o?.createdAt ?? null;
  const toLocal = (val) => {
    if (!val) return "";
    const d = new Date(val);
    return isNaN(d.getTime()) ? String(val) : d.toLocaleString();
  };

  // init date (last month)
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

  const toDateStr = (val, end = false) => {
    if (!val) return null;
    const [d, t_part] = String(val).split("T");
    if (!d) return null;
    const hhmm = t_part ? t_part.slice(0, 5) : end ? "23:59" : "00:00";
    return `${d} ${hhmm}`;
  };

  // Ubi hook
  const {
    form: ubi,
    handleChange: handleUbiChange,
    data: ubiData,
    loading: ubiLoading,
    error: ubiError,
    fetchInitialData
  } = useUbicacionFilters({ empresaId, headers, autoselectSingle: true });

  // Kardex filters
  const [kdxFiltro, setKdxFiltro] = useState({
    producto_categoria_id: "",
    producto_id: "",
    produccion_id: "",
    producto_presentacion_id: "",
    ...getInitDates()
  });

  const handleFiltroChange = (name) => (e) => {
    const value = e?.target ? e.target.value : e;
    setKdxFiltro((f) => ({ ...f, [name]: value }));
  };

  // Catalogs
  const [categorias, setCategorias] = useState([]);
  const [productos, setProductos] = useState([]);
  const [producciones, setProducciones] = useState([]);
  const [presentaciones, setPresentaciones] = useState([]);
  const [catalogsLoading, setCatalogsLoading] = useState(false);
  const [catalogsError, setCatalogsError] = useState(false);

  const fetchCatalogs = () => {
    setCatalogsLoading(true);
    setCatalogsError(false);
    Promise.all([
      axios.get("/v1/items/producto_categoria/0", headers).catch(() => ({ data: [] })),
      axios.get("/v1/items/producto/0", headers).catch(() => ({ data: [] })),
      axios.get("/v1/items/produccion/0", headers).catch(() => ({ data: [] })),
      axios.get("/v1/items/producto_presentacion/0", headers).catch(() => ({ data: [] })),
    ])
    .then(([cat, pro, prod, pres]) => {
      setCategorias(asArray(cat.data));
      setProductos(asArray(pro.data));
      setProducciones(asArray(prod.data));
      setPresentaciones(asArray(pres.data));
      setCatalogsLoading(false);
    })
    .catch(() => {
      setCatalogsError(true);
      setCatalogsLoading(false);
      setMessage({
        open: true,
        severity: "error",
        text: t("kardex.messages.catalogsError", "Error al cargar catálogos. Por favor, reintente."),
      });
    });
  };

  useEffect(() => {
    fetchCatalogs();
    // eslint-disable-next-line
  }, []);

  // UI state
  const [resultados, setResultados] = useState([]);
  const [loadingSearch, setLoadingSearch] = useState(false);
  const [message, setMessage] = useState({ open: false, severity: "info", text: "" });
  const [errors, setErrors] = useState({ fechas_rango: false, fechas_vacias: false });

  // Date Range Popover
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

    if (type === 'hoy') {
      fInicio = `${y2}-${m2}-${d2}T00:00`;
    } else if (type === 'semana') {
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

    setKdxFiltro(f => ({ ...f, fecha_inicio: fInicio, fecha_fin: fFin }));
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
    setKdxFiltro(f => ({ ...f, fecha_inicio: fInicio, fecha_fin: fFin }));
  };

  const validarFiltros = () => {
    let err = false;
    setErrors({ fechas_rango: false, fechas_vacias: false });
    if (!kdxFiltro.fecha_inicio || !kdxFiltro.fecha_fin) {
      setErrors(e => ({ ...e, fechas_vacias: true }));
      setMessage({ open: true, severity: "warning", text: t("kardex.messages.emptyDates", "El rango de fechas es obligatorio.") });
      return false;
    }
    const ini = new Date(kdxFiltro.fecha_inicio);
    const fin = new Date(kdxFiltro.fecha_fin);
    if (ini > fin) {
      setErrors(e => ({ ...e, fechas_rango: true }));
      setMessage({ open: true, severity: "warning", text: t("kardex.messages.invalidDateRange", "La fecha de inicio no puede ser mayor que la fecha fin.") });
      return false;
    }
    return true;
  };

  const fetchAllKardex = async () => {
    const CANDIDATES = ["/v1/kardex"];
    const size = 200;
    for (const basePath of CANDIDATES) {
      try {
        const r0 = await axios.get(basePath, headers);
        const list0 = asArray(r0.data);
        if (list0.length) return list0;
        let page = 0, acc = [];
        for (let i = 0; i < 15; i++) {
          const r = await axios.get(basePath, { params: { page, size }, ...headers });
          const list = asArray(r.data);
          if (!list.length) break;
          acc = acc.concat(list);
          page += 1;
        }
        if (acc.length) return acc;
      } catch (err) {
        if (import.meta.env.DEV) console.debug("[fetchAllKardex] fallo", basePath, err?.response?.status);
      }
    }
    const e = new Error("No se encontró endpoint para listar kardex.");
    e.code = "NO_KARDEX_ENDPOINT";
    throw e;
  };

  const buscar = async () => {
    setResultados([]);
    if (!validarFiltros()) return;

    setLoadingSearch(true);
    try {
      const all = await fetchAllKardex();
      const ini = new Date(kdxFiltro.fecha_inicio);
      const fin = new Date(kdxFiltro.fecha_fin);

      const lista = all.filter((row) => {
        const f = getFechaKdx(row);
        if (!f) return true;
        const d = new Date(f);
        if (isNaN(d.getTime())) return true;
        if (d < ini) return false;
        if (d > fin) return false;
        
        if (ubi.sede_id && row.sede?.id !== Number(ubi.sede_id)) return false;
        if (kdxFiltro.producto_id && row.producto?.id !== Number(kdxFiltro.producto_id)) return false;

        return true;
      });

      setResultados(lista);
      if (lista.length === 0) {
        setMessage({ open: true, severity: "info", text: t("kardex.messages.noResults", "No se encontraron registros para los filtros seleccionados.") });
      }
    } catch (err) {
      const msg = err?.code === "NO_KARDEX_ENDPOINT" ? "Endpoint 404" : t("kardex.messages.searchError", "Error al realizar la búsqueda.");
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
    const userIni = toDateStr(kdxFiltro.fecha_inicio, false);
    const userFin = toDateStr(kdxFiltro.fecha_fin, true);

    const c = {};
    c["0"] = `e.emp_id = $EMPRESA_ID$`;
    if (ubi.municipio_id) c["1"] = `AND m.mun_id = ${Number(ubi.municipio_id)}`;
    if (ubi.sede_id) c["2"] = `AND s.sed_id = ${Number(ubi.sede_id)}`;
    if (ubi.bloque_id) c["3"] = `AND blo.blo_id = ${Number(ubi.bloque_id)}`;
    if (ubi.espacio_id) c["4"] = `AND esp.esp_id = ${Number(ubi.espacio_id)}`;
    if (ubi.almacen_id) c["5"] = `AND al.alm_id = ${Number(ubi.almacen_id)}`;
    if (kdxFiltro.producto_id) c["6"] = `AND p.pro_id = ${Number(kdxFiltro.producto_id)}`;
    if (kdxFiltro.producto_categoria_id) c["7"] = `AND p.pro_producto_categoria_id = ${Number(kdxFiltro.producto_categoria_id)}`;
    if (kdxFiltro.produccion_id) c["9"] = `AND pr.pro_id = ${Number(kdxFiltro.produccion_id)}`;
    if (kdxFiltro.producto_presentacion_id) c["10"] = `AND pp.prp_id = ${Number(kdxFiltro.producto_presentacion_id)}`;
    c["8"] = `AND k.kar_fecha_hora BETWEEN '${userIni}' AND '${userFin}'`;
    return c;
  };

  const getReportPath = () => {
    if (kdxFiltro.produccion_id) return "/v2/report/nuevo/kardex_produccion";
    if (kdxFiltro.producto_presentacion_id) return "/v2/report/nuevo/kardex_producto_presentacion";
    return "/v2/report/nuevo/kardex";
  };

  const generarReporte = async (formato = "PDF") => {
    if (!validarFiltros()) {
      setModalExportOpen(false);
      return;
    }
    setExporting(true);
    try {
      const condicion = buildCondicion();
      const url = getReportPath();
      const payload = { condicion, EMPRESA_ID: empresaId, formato };

      let res;
      try {
        res = await axios({ url, method: "POST", data: payload, responseType: "blob", ...headers });
      } catch (e1) {
        if (e1?.response?.status === 404 && url !== "/v2/report/nuevo/kardex") {
          res = await axios({ url: "/v2/report/nuevo/kardex", method: "POST", data: payload, responseType: "blob", ...headers });
        } else {
          throw e1;
        }
      }

      const mimeType = formato === "EXCEL" ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" : "application/pdf";
      const blob = new Blob([res.data], { type: mimeType });
      const objUrl = window.URL.createObjectURL(blob);
      
      if (formato === "EXCEL") {
        const a = document.createElement("a");
        a.href = objUrl;
        a.download = `Kardex_${new Date().getTime()}.xlsx`;
        a.click();
        URL.revokeObjectURL(objUrl);
        setMessage({ open: true, severity: "success", text: t("kardex.messages.excelSuccess", "Excel descargado correctamente.") });
      } else {
        if (previewUrl) URL.revokeObjectURL(previewUrl);
        setPreviewUrl(objUrl);
        setPreviewOpen(true);
        setMessage({ open: true, severity: "success", text: t("kardex.messages.pdfSuccess", "PDF generado correctamente.") });
      }
    } catch (err) {
      console.error(err);
      setMessage({ open: true, severity: "error", text: t("kardex.messages.exportError", "No se pudo generar el reporte.") });
    } finally {
      setExporting(false);
      setModalExportOpen(false);
    }
  };

  // Columns for grid
  const columns = useMemo(() => [
    { field: "id", headerName: "ID", width: 80 },
    { 
      field: "fecha", 
      headerName: t("kardex.columns.fecha", "Fecha/Hora"), 
      flex: 1, minWidth: 150,
      renderCell: (params) => toLocal(getFechaKdx(params.row))
    },
    { 
      field: "sede", 
      headerName: t("kardex.columns.sede", "Sede"), 
      flex: 1, minWidth: 120,
      renderCell: (params) => params.row?.sede?.nombre || ""
    },
    { 
      field: "almacen", 
      headerName: t("kardex.columns.almacen", "Almacén"), 
      flex: 1, minWidth: 120,
      renderCell: (params) => params.row?.almacen?.nombre || ""
    },
    { 
      field: "produccion", 
      headerName: t("kardex.columns.produccion", "Producción"), 
      flex: 1, minWidth: 120,
      renderCell: (params) => params.row?.produccion?.nombre || ""
    },
    { 
      field: "movimiento", 
      headerName: t("kardex.columns.movimiento", "Tipo de Movimiento"), 
      flex: 1, minWidth: 150,
      renderCell: (params) => params.row?.tipoMovimiento?.nombre || ""
    },
    { 
      field: "producto", 
      headerName: t("kardex.columns.producto", "Producto"), 
      flex: 1, minWidth: 150,
      renderCell: (params) => params.row?.producto?.nombre || ""
    },
    { 
      field: "presentacion", 
      headerName: t("kardex.columns.presentacion", "Presentación"), 
      flex: 1, minWidth: 120,
      renderCell: (params) => params.row?.presentacion?.nombre || ""
    },
    { field: "cantidad", headerName: t("kardex.columns.cantidad", "Cantidad"), width: 100, align: "right" },
    { field: "saldo", headerName: t("kardex.columns.saldo", "Saldo"), width: 100, align: "right" }
  ], [t]);

  const boxStyles = {
    p: 3, 
    borderRadius: 4, 
    bgcolor: isDark ? "rgba(255,255,255,0.02)" : "#fff",
    boxShadow: isDark ? "0 4px 12px rgba(0,0,0,0.2)" : "0 4px 12px rgba(23,63,57,0.06)"
  };

  const titleStyles = {
    fontWeight: 600,
    mb: 2,
    color: isDark ? "#e7f6f7" : "#173f39",
    display: "flex",
    alignItems: "center",
    gap: 1
  };

  return (
    <Box sx={{ width: "100%", p: { xs: 2, md: 4 }, color: "text.primary" }}>
      <Box sx={{ display: "flex", justifyContent: "center", alignItems: "center", mb: 4 }}>
        <Typography variant="h4" sx={{ fontWeight: 700, color: isDark ? "#fff" : "#173f39" }}>
          {t("kardex.title", "Reportes de Kardex")}
        </Typography>
      </Box>
           {/* FILTROS GROUPED LAYOUT */}
      <Grid container spacing={3} mb={3} alignItems="flex-start">
        
        {/* Ubicacion (Izquierda) */}
        <Grid item xs={12} md={7}>
          <Paper sx={boxStyles}>
            <Typography variant="h6" sx={titleStyles}>
              {t("kardex.sections.location", "Ubicación")}
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
                  <InputLabel>{t("kardex.filters.country", "País")}</InputLabel>
                  <Select label={t("kardex.filters.country", "País")} value={ubi.pais_id || ""} onChange={handleUbiChange("pais_id")}>
                    <MenuItem value=""><em>{t("common.labels.all", "Todos")}</em></MenuItem>
                    {ubiData?.paises?.map(it => <MenuItem key={it.id} value={String(it.id)}>{it.nombre}</MenuItem>)}
                  </Select>
                </FormControl>
              </Grid>
              <Grid item xs={12} sm={4}>
                <FormControl size="small" fullWidth disabled={ubiLoading || ubiError || !ubi.pais_id}>
                  <InputLabel>{t("kardex.filters.department", "Departamento")}</InputLabel>
                  <Select label={t("kardex.filters.department", "Departamento")} value={ubi.departamento_id || ""} onChange={handleUbiChange("departamento_id")}>
                    <MenuItem value=""><em>{t("common.labels.all", "Todos")}</em></MenuItem>
                    {ubiData?.departamentos?.map(it => <MenuItem key={it.id} value={String(it.id)}>{it.nombre}</MenuItem>)}
                  </Select>
                </FormControl>
              </Grid>
              <Grid item xs={12} sm={4}>
                <FormControl size="small" fullWidth disabled={ubiLoading || ubiError || !ubi.departamento_id}>
                  <InputLabel>{t("kardex.filters.municipality", "Municipio")}</InputLabel>
                  <Select label={t("kardex.filters.municipality", "Municipio")} value={ubi.municipio_id || ""} onChange={handleUbiChange("municipio_id")}>
                    <MenuItem value=""><em>{t("common.labels.all", "Todos")}</em></MenuItem>
                    {ubiData?.municipios?.map(it => <MenuItem key={it.id} value={String(it.id)}>{it.nombre}</MenuItem>)}
                  </Select>
                </FormControl>
              </Grid>
              <Grid item xs={12} sm={4}>
                <FormControl size="small" fullWidth disabled={ubiLoading || ubiError}>
                  <InputLabel>{t("kardex.filters.headquarters", "Sede")}</InputLabel>
                  <Select label={t("kardex.filters.headquarters", "Sede")} value={ubi.sede_id || ""} onChange={handleUbiChange("sede_id")}>
                    <MenuItem value=""><em>{t("common.labels.all", "Todas")}</em></MenuItem>
                    {ubiData?.sedes?.map(it => <MenuItem key={it.id} value={String(it.id)}>{it.nombre}</MenuItem>)}
                  </Select>
                </FormControl>
              </Grid>
              <Grid item xs={12} sm={4}>
                <FormControl size="small" fullWidth disabled={ubiLoading || ubiError || !ubi.sede_id}>
                  <InputLabel>{t("kardex.filters.block", "Bloque")}</InputLabel>
                  <Select label={t("kardex.filters.block", "Bloque")} value={ubi.bloque_id || ""} onChange={handleUbiChange("bloque_id")}>
                    <MenuItem value=""><em>{t("common.labels.all", "Todos")}</em></MenuItem>
                    {ubiData?.bloques?.map(it => <MenuItem key={it.id} value={String(it.id)}>{it.nombre}</MenuItem>)}
                  </Select>
                </FormControl>
              </Grid>
              <Grid item xs={12} sm={4}>
                <FormControl size="small" fullWidth disabled={ubiLoading || ubiError || !ubi.bloque_id}>
                  <InputLabel>{t("kardex.filters.space", "Espacio")}</InputLabel>
                  <Select label={t("kardex.filters.space", "Espacio")} value={ubi.espacio_id || ""} onChange={handleUbiChange("espacio_id")}>
                    <MenuItem value=""><em>{t("common.labels.all", "Todos")}</em></MenuItem>
                    {ubiData?.espacios?.map(it => <MenuItem key={it.id} value={String(it.id)}>{it.nombre}</MenuItem>)}
                  </Select>
                </FormControl>
              </Grid>
              <Grid item xs={12} sm={4}>
                <FormControl size="small" fullWidth disabled={ubiLoading || ubiError || !ubi.espacio_id}>
                  <InputLabel>{t("kardex.filters.warehouse", "Almacén")}</InputLabel>
                  <Select label={t("kardex.filters.warehouse", "Almacén")} value={ubi.almacen_id || ""} onChange={handleUbiChange("almacen_id")}>
                    <MenuItem value=""><em>{t("common.labels.all", "Todos")}</em></MenuItem>
                    {ubiData?.almacenes?.map(it => <MenuItem key={it.id} value={String(it.id)}>{it.nombre}</MenuItem>)}
                  </Select>
                </FormControl>
              </Grid>
            </Grid>
          </Paper>
        </Grid>

        {/* Derecha: Producto + Tiempo */}
        <Grid item xs={12} md={5}>
          {/* Producto */}
          <Paper sx={boxStyles}>
            <Typography variant="h6" sx={titleStyles}>
              {t("kardex.sections.product", "Producto")}
              {catalogsLoading && <LinearProgress sx={{ flexGrow: 1, ml: 2, height: 2 }} />}
              {catalogsError && (
                <IconButton size="small" onClick={fetchCatalogs} color="error" title={t("common.actions.retry", "Reintentar")}>
                  <RefreshIcon fontSize="small"/>
                </IconButton>
              )}
            </Typography>
            <Grid container spacing={2}>
              <Grid item xs={12} sm={6}>
                <FormControl size="small" fullWidth disabled={catalogsLoading || catalogsError}>
                  <InputLabel>{t("kardex.filters.category", "Categoría")}</InputLabel>
                  <Select label={t("kardex.filters.category", "Categoría")} value={kdxFiltro.producto_categoria_id || ""} onChange={handleFiltroChange("producto_categoria_id")}>
                    <MenuItem value=""><em>{t("common.labels.all", "Todas")}</em></MenuItem>
                    {categorias.map(it => <MenuItem key={it.id} value={String(it.id)}>{it.nombre || `#${it.id}`}</MenuItem>)}
                  </Select>
                </FormControl>
              </Grid>
              <Grid item xs={12} sm={6}>
                <FormControl size="small" fullWidth disabled={catalogsLoading || catalogsError}>
                  <InputLabel>{t("kardex.filters.product", "Producto")}</InputLabel>
                  <Select label={t("kardex.filters.product", "Producto")} value={kdxFiltro.producto_id || ""} onChange={handleFiltroChange("producto_id")}>
                    <MenuItem value=""><em>{t("common.labels.all", "Todos")}</em></MenuItem>
                    {productos.map(it => <MenuItem key={it.id} value={String(it.id)}>{it.nombre || `#${it.id}`}</MenuItem>)}
                  </Select>
                </FormControl>
              </Grid>
              <Grid item xs={12} sm={6}>
                <FormControl size="small" fullWidth disabled={catalogsLoading || catalogsError}>
                  <InputLabel>{t("kardex.filters.presentation", "Presentación")}</InputLabel>
                  <Select label={t("kardex.filters.presentation", "Presentación")} value={kdxFiltro.producto_presentacion_id || ""} onChange={handleFiltroChange("producto_presentacion_id")}>
                    <MenuItem value=""><em>{t("common.labels.all", "Todas")}</em></MenuItem>
                    {presentaciones.map(it => <MenuItem key={it.id} value={String(it.id)}>{it.nombre || `#${it.id}`}</MenuItem>)}
                  </Select>
                </FormControl>
              </Grid>
              <Grid item xs={12} sm={6}>
                <FormControl size="small" fullWidth disabled={catalogsLoading || catalogsError}>
                  <InputLabel>{t("kardex.filters.production", "Producción")}</InputLabel>
                  <Select label={t("kardex.filters.production", "Producción")} value={kdxFiltro.produccion_id || ""} onChange={handleFiltroChange("produccion_id")}>
                    <MenuItem value=""><em>{t("common.labels.all", "Todas")}</em></MenuItem>
                    {producciones.map(it => <MenuItem key={it.id} value={String(it.id)}>{it.nombre || `#${it.id}`}</MenuItem>)}
                  </Select>
                </FormControl>
              </Grid>
              {/* Tiempo — tercera fila, alineada con Bloque/Espacio/Almacén */}
              <Grid item xs={12}>
                <Box sx={{ display: "flex", alignItems: "center", gap: 2 }}>
                  <Typography variant="subtitle1" sx={{ fontWeight: 600, color: isDark ? "#e7f6f7" : "#173f39", whiteSpace: "nowrap" }}>
                    {t("kardex.sections.time", "Tiempo")}
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
                    {kdxFiltro.fecha_inicio && kdxFiltro.fecha_fin 
                      ? `${toDateStr(kdxFiltro.fecha_inicio)} - ${toDateStr(kdxFiltro.fecha_fin, true)}`
                      : t("kardex.filters.selectDates", "Seleccionar fechas")}
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
                    {t("kardex.filters.quickSelect", "Selección rápida")}
                  </Typography>
                  <Stack spacing={1}>
                    {["hoy", "semana", "mes", "trimestre", "anio"].map(type => {
                      const labels = {
                        hoy: "Hoy", semana: "Última Semana", mes: "Último Mes", trimestre: "Último Trimestre", anio: "Este Año"
                      };
                      return (
                        <Button key={type} variant="text" size="small" fullWidth onClick={() => setQuickDate(type)} sx={{ justifyContent: "flex-start", textTransform: "none" }}>
                          {t(`kardex.filters.quick.${type}`, labels[type])}
                        </Button>
                      )
                    })}
                  </Stack>
                </Box>
                <Box sx={{ pl: { md: 2 } }}>
                  <Typography variant="subtitle2" sx={{ mb: 2, fontWeight: 600 }}>
                    {t("kardex.filters.customRange", "Rango personalizado")}
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
                        startDate: kdxFiltro.fecha_inicio ? new Date(kdxFiltro.fecha_inicio) : new Date(),
                        endDate: kdxFiltro.fecha_fin ? new Date(kdxFiltro.fecha_fin) : new Date(),
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
            {t("kardex.actions.generate", "Generar Reporte")}
          </Button>
        </Stack>
      </Box>

      {/* Resultados Grilla */}
      <Box>
        <AppDataGrid
          rows={resultados}
          columns={columns}
          loading={loadingSearch}
          containerSx={{ minHeight: 200 }}
          autoHeight={true}
        />
      </Box>

      {/* Export Modal */}
      <Dialog open={modalExportOpen} onClose={() => !exporting && setModalExportOpen(false)} maxWidth="xs" fullWidth PaperProps={{ sx: { borderRadius: 4, p: 1 }}}>
        <DialogTitle sx={{ fontWeight: 700, textAlign: "center", pb: 1 }}>
          {t("kardex.modal.exportTitle", "Generar Reporte Kardex")}
        </DialogTitle>
        <DialogContent sx={{ textAlign: "center", pb: 2 }}>
          {exporting ? (
            <Box sx={{ py: 3 }}>
              <Typography variant="body2" sx={{ mb: 2 }}>
                {t("kardex.modal.exporting", "Generando documento, por favor espere...")}
              </Typography>
              <LinearProgress color="success" />
            </Box>
          ) : (
            <Box sx={{ py: 2 }}>
              <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
                {t("kardex.modal.exportSub", "Seleccione el formato en el que desea descargar su reporte de Kardex.")}
              </Typography>
              <Stack direction="row" spacing={2} justifyContent="center">
                <Button 
                  variant="outlined" 
                  color="error" 
                  size="large"
                  onClick={() => generarReporte("PDF")}
                  sx={{ width: 120, height: 100, display: "flex", flexDirection: "column", gap: 1, borderRadius: 3 }}
                >
                  <PdfIcon fontSize="large" />
                  PDF
                </Button>
                <Button 
                  variant="outlined" 
                  color="success" 
                  size="large"
                  onClick={() => generarReporte("EXCEL")}
                  sx={{ width: 120, height: 100, display: "flex", flexDirection: "column", gap: 1, borderRadius: 3 }}
                >
                  <ExcelIcon fontSize="large" />
                  Excel
                </Button>
              </Stack>
            </Box>
          )}
        </DialogContent>
        {!exporting && (
          <DialogActions sx={{ justifyContent: "center", pt: 0, pb: 2 }}>
            <Button onClick={() => setModalExportOpen(false)} color="inherit" sx={{ textTransform: "none" }}>
              {t("common.actions.cancel", "Cancelar")}
            </Button>
          </DialogActions>
        )}
      </Dialog>

      {/* Preview PDF */}
      <Dialog open={previewOpen} onClose={() => setPreviewOpen(false)} fullWidth maxWidth="lg" PaperProps={{ sx: { borderRadius: 4 } }}>
        <DialogTitle sx={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
          {t("kardex.modal.previewTitle", "Vista previa del Reporte")}
          <IconButton onClick={() => setPreviewOpen(false)}>
            <CloseIcon />
          </IconButton>
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
