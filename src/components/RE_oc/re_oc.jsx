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
 | 2026-07-18 | 0.4.2   | Jeisson Sanchez      | Soporte Dark Mode Picker    |
 +------------+---------+----------------------+-----------------------------+
 | 2026-10-16 | 0.4.0   | Jeisson Sanchez      | [HU-041.1 / Task #229] Integración con contrato V2 tipado de filtros, resumen y exportar PDF/XLSX. |
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

export default function RE_ordenCompra() {
  const { t, i18n } = useTranslation();
  const theme = useTheme();
  const isDark = theme.palette.mode === "dark";

  const empresaId = localStorage.getItem("empresaId");
  const token = localStorage.getItem("token");
  const headers = { headers: { Authorization: `Bearer ${token}` } };

  // ===== Utils
  const asArray = (x) => (Array.isArray(x) ? x : x?.content ?? x?.data ?? []);
  const toLocal = (val) => {
    if (!val) return "";
    const d = new Date(val);
    return isNaN(d.getTime()) ? String(val) : d.toLocaleString();
  };

  const toDateOnly = (val) => {
    if (!val) return null;
    const s = String(val).trim();
    if (s.includes("T")) return s.split("T")[0];
    if (s.includes(" ")) return s.split(" ")[0];
    return s;
  };

  const getInitDates = () => {
    const today = new Date();
    const firstDayLastMonth = new Date(today.getFullYear(), today.getMonth() - 1, 1);
    const y1 = firstDayLastMonth.getFullYear();
    const m1 = String(firstDayLastMonth.getMonth() + 1).padStart(2, "0");
    const d1 = String(firstDayLastMonth.getDate()).padStart(2, "0");
    const y2 = today.getFullYear();
    const m2 = String(today.getMonth() + 1).padStart(2, "0");
    const d2 = String(today.getDate()).padStart(2, "0");
    return {
      fecha_inicio: `${y1}-${m1}-${d1}T00:00`,
      fecha_fin: `${y2}-${m2}-${d2}T23:59`,
    };
  };

  // ===== Hook unificado con filtros V2 del reporte de orden de compra
  const {
    form: ubi,
    handleChange: handleUbiChange,
    data: ubiData,
    loading: ubiLoading,
    error: ubiError,
    fetchInitialData,
    preloadData,
    pedido,
    handlePedidoChange,
    pedidos,
  } = useUbicacionFilters({ empresaId, headers, autoselectSingle: true, reportType: "orden-compra" });

  const [formReporte, setFormReporte] = useState({
    proveedor_id: "",
    ...getInitDates(),
  });

  const [estadosOC, setEstadosOC] = useState([]);
  const [proveedores, setProveedores] = useState([]);

  // Sincronizar catálogo de estados, proveedores y fechas inteligentes del endpoint V2
  useEffect(() => {
    if (preloadData) {
      if (Array.isArray(preloadData.estados)) {
        setEstadosOC(preloadData.estados);
      }
      if (Array.isArray(preloadData.proveedores)) {
        setProveedores(preloadData.proveedores);
      }
      if (preloadData.fechaInicio && preloadData.fechaFin) {
        setFormReporte((prev) => ({
          ...prev,
          fecha_inicio: `${preloadData.fechaInicio}T00:00`,
          fecha_fin: `${preloadData.fechaFin}T23:59`,
        }));
      }
    }
  }, [preloadData]);

  const handleFormReporteChange = (name) => (event) => {
    const value = event?.target ? event.target.value : event;
    setFormReporte((prev) => ({ ...prev, [name]: value }));
  };

  // UI state
  const [resultados, setResultados] = useState([]);
  const [loadingSearch, setLoadingSearch] = useState(false);
  const [paginationModel, setPaginationModel] = useState({ page: 0, pageSize: 10 });
  const [rowCount, setRowCount] = useState(0);

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
    const m2 = String(today.getMonth() + 1).padStart(2, "0");
    const d2 = String(today.getDate()).padStart(2, "0");
    fFin = `${y2}-${m2}-${d2}T23:59`;

    if (type === "hoy") fInicio = `${y2}-${m2}-${d2}T00:00`;
    else if (type === "semana") {
      const past = new Date(today.getTime() - 7 * 24 * 60 * 60 * 1000);
      fInicio = `${past.getFullYear()}-${String(past.getMonth() + 1).padStart(2, "0")}-${String(past.getDate()).padStart(2, "0")}T00:00`;
    } else if (type === "mes") {
      const past = new Date(today.getFullYear(), today.getMonth() - 1, today.getDate());
      fInicio = `${past.getFullYear()}-${String(past.getMonth() + 1).padStart(2, "0")}-${String(past.getDate()).padStart(2, "0")}T00:00`;
    } else if (type === "trimestre") {
      const past = new Date(today.getFullYear(), today.getMonth() - 3, today.getDate());
      fInicio = `${past.getFullYear()}-${String(past.getMonth() + 1).padStart(2, "0")}-${String(past.getDate()).padStart(2, "0")}T00:00`;
    } else if (type === "anio") {
      fInicio = `${y2}-01-01T00:00`;
    }
    setFormReporte((f) => ({ ...f, fecha_inicio: fInicio, fecha_fin: fFin }));
    handleCloseDate();
  };

  const handleSelectDateRange = (item) => {
    const { startDate, endDate } = item.selection;
    const y1 = startDate.getFullYear();
    const m1 = String(startDate.getMonth() + 1).padStart(2, "0");
    const d1 = String(startDate.getDate()).padStart(2, "0");
    const fInicio = `${y1}-${m1}-${d1}T00:00`;

    let fFin = "";
    if (endDate) {
      const y2 = endDate.getFullYear();
      const m2 = String(endDate.getMonth() + 1).padStart(2, "0");
      const d2 = String(endDate.getDate()).padStart(2, "0");
      fFin = `${y2}-${m2}-${d2}T23:59`;
    } else {
      fFin = `${y1}-${m1}-${d1}T23:59`;
    }
    setFormReporte((f) => ({ ...f, fecha_inicio: fInicio, fecha_fin: fFin }));
  };

  const validarRango = () => {
    setErrors({ fechas_rango: false });
    if (!formReporte.fecha_inicio || !formReporte.fecha_fin) {
      setMessage({
        open: true,
        severity: "warning",
        text: t("ordenCompra.messages.emptyDates", "El rango de fechas es obligatorio."),
      });
      return false;
    }
    const ini = new Date(formReporte.fecha_inicio);
    const fin = new Date(formReporte.fecha_fin);
    if (ini > fin) {
      setErrors({ fechas_rango: true });
      setMessage({
        open: true,
        severity: "warning",
        text: t("ordenCompra.messages.invalidDateRange", "La fecha de inicio no puede ser mayor que la fecha fin."),
      });
      return false;
    }
    return true;
  };

  // Construcción del DTO tipado sin SQL ni campos fuera del contrato
  const buildFiltersPayload = () => {
    const payload = {
      fechaInicio: toDateOnly(formReporte.fecha_inicio),
      fechaFin: toDateOnly(formReporte.fecha_fin),
    };

    if (pedido?.pedido_id) payload.pedidoId = Number(pedido.pedido_id);
    if (pedido?.categoria_estado_id) payload.estadoId = Number(pedido.categoria_estado_id);
    if (formReporte?.proveedor_id) payload.proveedorId = Number(formReporte.proveedor_id);

    if (ubi?.pais_id) payload.paisId = Number(ubi.pais_id);
    if (ubi?.departamento_id) payload.departamentoId = Number(ubi.departamento_id);
    if (ubi?.municipio_id) payload.municipioId = Number(ubi.municipio_id);
    if (ubi?.sede_id) payload.sedeId = Number(ubi.sede_id);
    if (ubi?.bloque_id) payload.bloqueId = Number(ubi.bloque_id);
    if (ubi?.espacio_id) payload.espacioId = Number(ubi.espacio_id);
    if (ubi?.almacen_id) payload.almacenId = Number(ubi.almacen_id);

    return payload;
  };

  // Consulta contractual V2 del resumen paginado
  const buscar = async (page = 0, size = paginationModel.pageSize) => {
    if (!validarRango()) return;

    setLoadingSearch(true);
    try {
      const payload = buildFiltersPayload();
      const res = await axios.post(`/v2/report/orden-compra/resumen?page=${page}&size=${size}`, payload);
      const data = res?.data;
      const content = Array.isArray(data?.content) ? data.content : Array.isArray(data) ? data : [];
      const mapped = content.map((row, idx) => ({
        ...row,
        id: row.ordenCompraId ?? row.id ?? `oc-${page}-${idx}`,
      }));

      // El contrato V2 entrega la paginacion en `page` (PaginacionDTO) y no en la raiz.
      const paginacion = data?.page ?? data?.header ?? {};
      const totalElementos = Number(
        paginacion.totalElements ?? data?.totalElements ?? mapped.length
      );

      setResultados(mapped);
      setRowCount(totalElementos);

      if (mapped.length === 0) {
        setMessage({
          open: true,
          severity: "info",
          text: t("ordenCompra.messages.noResultsEmpty", "No se encontraron registros para los filtros seleccionados."),
        });
      } else {
        setMessage({
          open: true,
          severity: "info",
          text: t("ordenCompra.messages.noResults", { count: totalElementos }),
        });
      }
    } catch (err) {
      console.error(err);
      const status = err?.response?.status;
      let text = t("ordenCompra.messages.searchError", "No fue posible cargar las órdenes.");
      if (status === 403) {
        text = t("ordenCompra.messages.forbidden", "No tiene permisos para realizar esta operación.");
      } else if (status === 400) {
        text = err?.response?.data?.detail || t("ordenCompra.messages.badRequest", "La solicitud contiene parámetros inválidos.");
      }
      setMessage({ open: true, severity: "error", text });
      setResultados([]);
      setRowCount(0);
    } finally {
      setLoadingSearch(false);
    }
  };

  const handlePaginationChange = (nextModel) => {
    setPaginationModel(nextModel);
    buscar(nextModel.page, nextModel.pageSize);
  };

  // Exportación contractual V2 (PDF / XLSX)
  const [modalExportOpen, setModalExportOpen] = useState(false);
  const [exporting, setExporting] = useState(false);
  const [previewUrl, setPreviewUrl] = useState("");
  const [previewOpen, setPreviewOpen] = useState(false);

  const generarReporte = async (formato = "PDF") => {
    if (!validarRango()) return;
    setExporting(true);
    try {
      const payload = buildFiltersPayload();
      const queryFormat = formato === "EXCEL" ? "XLSX" : formato;
      const res = await axios.post(`/v2/report/orden-compra/exportar?formato=${queryFormat}`, payload, {
        responseType: "blob",
      });

      const mimeType = queryFormat === "XLSX"
        ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        : "application/pdf";
      const blob = new Blob([res.data], { type: mimeType });
      const objUrl = window.URL.createObjectURL(blob);

      if (queryFormat === "XLSX") {
        const a = document.createElement("a");
        a.href = objUrl;
        a.download = `OrdenCompra_${Date.now()}.xlsx`;
        a.click();
        URL.revokeObjectURL(objUrl);
        setMessage({
          open: true,
          severity: "success",
          text: t("ordenCompra.messages.excelSuccess", "Excel descargado correctamente."),
        });
      } else {
        if (previewUrl) URL.revokeObjectURL(previewUrl);
        setPreviewUrl(objUrl);
        setPreviewOpen(true);
        setMessage({
          open: true,
          severity: "success",
          text: t("ordenCompra.messages.pdfSuccess", "PDF generado correctamente."),
        });
      }
    } catch (err) {
      console.error(err);
      const status = err?.response?.status;
      let text = t("ordenCompra.messages.exportError", "No se pudo generar el reporte.");
      if (status === 403) {
        text = t("ordenCompra.messages.forbidden", "No tiene permisos para realizar esta operación.");
      } else if (status === 400) {
        text = t("ordenCompra.messages.badRequest", "La solicitud contiene parámetros inválidos.");
      }
      setMessage({ open: true, severity: "error", text });
    } finally {
      setExporting(false);
      setModalExportOpen(false);
    }
  };

  // Columnas para AppDataGrid según contrato V2
  const columns = useMemo(() => [
    { field: "ordenCompraId", headerName: t("ordenCompra.columns.id", "ID"), width: 90 },
    {
      field: "fechaHora",
      headerName: t("ordenCompra.columns.fecha", "Fecha/Hora"),
      flex: 1,
      minWidth: 160,
      renderCell: (params) => toLocal(params.row.fechaHora),
    },
    {
      field: "pedidoId",
      headerName: t("ordenCompra.columns.order", "Pedido"),
      width: 100,
      renderCell: (params) => (params.row.pedidoId ? `#${params.row.pedidoId}` : "-"),
    },
    {
      field: "proveedor",
      headerName: t("ordenCompra.columns.supplier", "Proveedor"),
      flex: 1.2,
      minWidth: 150,
      renderCell: (params) => params.row.proveedor || "-",
    },
    {
      field: "sede",
      headerName: t("ordenCompra.columns.headquarters", "Sede"),
      flex: 1.1,
      minWidth: 140,
      renderCell: (params) => params.row.sede || "-",
    },
    {
      field: "almacen",
      headerName: t("ordenCompra.columns.warehouse", "Almacén"),
      flex: 1.1,
      minWidth: 140,
      renderCell: (params) => params.row.almacen || "-",
    },
    {
      field: "estado",
      headerName: t("ordenCompra.columns.status", "Estado"),
      width: 160,
      renderCell: (params) => params.row.estado || "-",
    },
  ], [t]);

  const boxStyles = {
    p: 3,
    borderRadius: 4,
    bgcolor: isDark ? "rgba(255,255,255,0.02)" : "#fff",
    boxShadow: isDark ? "0 4px 12px rgba(0,0,0,0.2)" : "0 4px 12px rgba(23,63,57,0.06)",
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
        {/* Ubicación (Izquierda) */}
        <Grid item xs={12} md={7}>
          <Paper sx={boxStyles}>
            <Typography variant="h6" sx={titleStyles}>
              {t("ordenCompra.sections.location", "Ubicación")}
              {ubiLoading && <LinearProgress sx={{ flexGrow: 1, ml: 2, height: 2 }} />}
              {ubiError && (
                <IconButton size="small" onClick={fetchInitialData} color="error" title={t("common.actions.retry", "Reintentar")}>
                  <RefreshIcon fontSize="small" />
                </IconButton>
              )}
            </Typography>
            <Grid container spacing={2}>
              <Grid item xs={12}>
                <FormControl size="small" fullWidth disabled={ubiLoading || ubiError}>
                  <InputLabel>{t("ordenCompra.filters.country", "País")}</InputLabel>
                  <Select label={t("ordenCompra.filters.country", "País")} value={ubi.pais_id || ""} onChange={handleUbiChange("pais_id")}>
                    <MenuItem value=""><em>{t("common.labels.all", "Todos")}</em></MenuItem>
                    {ubiData?.paises?.map((it) => <MenuItem key={it.id} value={String(it.id)}>{it.nombre}</MenuItem>)}
                  </Select>
                </FormControl>
              </Grid>
              <Grid item xs={12} sm={4}>
                <FormControl size="small" fullWidth disabled={ubiLoading || ubiError || !ubi.pais_id}>
                  <InputLabel>{t("ordenCompra.filters.department", "Departamento")}</InputLabel>
                  <Select label={t("ordenCompra.filters.department", "Departamento")} value={ubi.departamento_id || ""} onChange={handleUbiChange("departamento_id")}>
                    <MenuItem value=""><em>{t("common.labels.all", "Todos")}</em></MenuItem>
                    {ubiData?.departamentos?.map((it) => <MenuItem key={it.id} value={String(it.id)}>{it.nombre}</MenuItem>)}
                  </Select>
                </FormControl>
              </Grid>
              <Grid item xs={12} sm={4}>
                <FormControl size="small" fullWidth disabled={ubiLoading || ubiError || !ubi.departamento_id}>
                  <InputLabel>{t("ordenCompra.filters.municipality", "Municipio")}</InputLabel>
                  <Select label={t("ordenCompra.filters.municipality", "Municipio")} value={ubi.municipio_id || ""} onChange={handleUbiChange("municipio_id")}>
                    <MenuItem value=""><em>{t("common.labels.all", "Todos")}</em></MenuItem>
                    {ubiData?.municipios?.map((it) => <MenuItem key={it.id} value={String(it.id)}>{it.nombre}</MenuItem>)}
                  </Select>
                </FormControl>
              </Grid>
              <Grid item xs={12} sm={4}>
                <FormControl size="small" fullWidth disabled={ubiLoading || ubiError}>
                  <InputLabel>{t("ordenCompra.filters.headquarters", "Sede")}</InputLabel>
                  <Select label={t("ordenCompra.filters.headquarters", "Sede")} value={ubi.sede_id || ""} onChange={handleUbiChange("sede_id")}>
                    <MenuItem value=""><em>{t("common.labels.all", "Todas")}</em></MenuItem>
                    {ubiData?.sedes?.map((it) => <MenuItem key={it.id} value={String(it.id)}>{it.nombre}</MenuItem>)}
                  </Select>
                </FormControl>
              </Grid>
              <Grid item xs={12} sm={4}>
                <FormControl size="small" fullWidth disabled={ubiLoading || ubiError || !ubi.sede_id}>
                  <InputLabel>{t("ordenCompra.filters.block", "Bloque")}</InputLabel>
                  <Select label={t("ordenCompra.filters.block", "Bloque")} value={ubi.bloque_id || ""} onChange={handleUbiChange("bloque_id")}>
                    <MenuItem value=""><em>{t("common.labels.all", "Todos")}</em></MenuItem>
                    {ubiData?.bloques?.map((it) => <MenuItem key={it.id} value={String(it.id)}>{it.nombre}</MenuItem>)}
                  </Select>
                </FormControl>
              </Grid>
              <Grid item xs={12} sm={4}>
                <FormControl size="small" fullWidth disabled={ubiLoading || ubiError || !ubi.bloque_id}>
                  <InputLabel>{t("ordenCompra.filters.space", "Espacio")}</InputLabel>
                  <Select label={t("ordenCompra.filters.space", "Espacio")} value={ubi.espacio_id || ""} onChange={handleUbiChange("espacio_id")}>
                    <MenuItem value=""><em>{t("common.labels.all", "Todos")}</em></MenuItem>
                    {ubiData?.espacios?.map((it) => <MenuItem key={it.id} value={String(it.id)}>{it.nombre}</MenuItem>)}
                  </Select>
                </FormControl>
              </Grid>
              <Grid item xs={12} sm={4}>
                <FormControl size="small" fullWidth disabled={ubiLoading || ubiError || !ubi.espacio_id}>
                  <InputLabel>{t("ordenCompra.filters.warehouse", "Almacén")}</InputLabel>
                  <Select label={t("ordenCompra.filters.warehouse", "Almacén")} value={ubi.almacen_id || ""} onChange={handleUbiChange("almacen_id")}>
                    <MenuItem value=""><em>{t("common.labels.all", "Todos")}</em></MenuItem>
                    {ubiData?.almacenes?.map((it) => <MenuItem key={it.id} value={String(it.id)}>{it.nombre}</MenuItem>)}
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
              {ubiLoading && <LinearProgress sx={{ flexGrow: 1, ml: 2, height: 2 }} />}
              {ubiError && (
                <IconButton size="small" onClick={fetchInitialData} color="error" title={t("common.actions.retry", "Reintentar")}>
                  <RefreshIcon fontSize="small" />
                </IconButton>
              )}
            </Typography>
            <Grid container spacing={2}>
              <Grid item xs={12} sm={6}>
                <FormControl size="small" fullWidth>
                  <InputLabel>{t("ordenCompra.filters.order", "Pedido")}</InputLabel>
                  <Select name="pedido_id" value={pedido.pedido_id || ""} label={t("ordenCompra.filters.order", "Pedido")} onChange={handlePedidoChange("pedido_id")}>
                    <MenuItem value=""><em>{t("common.labels.all", "Todos")}</em></MenuItem>
                    {asArray(pedidos).map((p) => <MenuItem key={p.id} value={String(p.id)}>{`${t("ordenCompra.filters.order", "Pedido")} ${p.id}`}</MenuItem>)}
                  </Select>
                </FormControl>
              </Grid>

              <Grid item xs={12} sm={6}>
                <FormControl size="small" fullWidth>
                  <InputLabel>{t("ordenCompra.filters.provider", "Proveedor")}</InputLabel>
                  <Select name="proveedor_id" value={formReporte.proveedor_id || ""} label={t("ordenCompra.filters.provider", "Proveedor")} onChange={handleFormReporteChange("proveedor_id")}>
                    <MenuItem value=""><em>{t("common.labels.all", "Todos")}</em></MenuItem>
                    {asArray(proveedores).map((pr) => <MenuItem key={pr.id} value={String(pr.id)}>{pr.nombre || pr.name || `Proveedor ${pr.id}`}</MenuItem>)}
                  </Select>
                </FormControl>
              </Grid>

              <Grid item xs={12}>
                <FormControl size="small" fullWidth disabled={ubiLoading || ubiError}>
                  <InputLabel>{t("ordenCompra.filters.status", "Estado de la orden")}</InputLabel>
                  <Select name="categoria_estado_id" value={pedido.categoria_estado_id || ""} label={t("ordenCompra.filters.status", "Estado de la orden")} onChange={handlePedidoChange("categoria_estado_id")}>
                    <MenuItem value=""><em>{t("common.labels.all", "Todos")}</em></MenuItem>
                    {estadosOC.map((e) => <MenuItem key={e.id} value={String(e.id)}>{e.name ?? e.nombre ?? `Estado ${e.id}`}</MenuItem>)}
                  </Select>
                </FormControl>
              </Grid>

              {/* Selector de Rango de Fechas */}
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
                    error={Boolean(errors.fechas_rango)}
                    sx={{
                      justifyContent: "space-between",
                      py: 1,
                      color: errors.fechas_rango ? "error.main" : (isDark ? "#e7f6f7" : "#173f39"),
                      borderColor: errors.fechas_rango ? "error.main" : (isDark ? "rgba(255,255,255,0.2)" : "rgba(0,0,0,0.23)"),
                      textTransform: "none",
                      fontWeight: 500,
                      borderRadius: 2,
                    }}
                  >
                    {formReporte.fecha_inicio && formReporte.fecha_fin
                      ? `${toDateOnly(formReporte.fecha_inicio)} - ${toDateOnly(formReporte.fecha_fin)}`
                      : t("ordenCompra.filters.selectDates", "Seleccionar fechas")}
                  </Button>
                </Box>
                {errors.fechas_rango && (
                  <Typography variant="caption" color="error" sx={{ mt: 0.5, display: "block" }}>
                    {t("ordenCompra.messages.invalidDateRange", "La fecha de inicio no puede ser mayor que la fecha fin.")}
                  </Typography>
                )}
              </Grid>
            </Grid>

            <Popover
              open={openDate}
              anchorEl={anchorElDate}
              onClose={handleCloseDate}
              anchorOrigin={{ vertical: "bottom", horizontal: "left" }}
              transformOrigin={{ vertical: "top", horizontal: "left" }}
              PaperProps={{ sx: { borderRadius: 3, mt: 1, maxWidth: 900 } }}
            >
              <Stack direction={{ xs: "column", md: "row" }} sx={{ p: 2 }}>
                <Box sx={{ minWidth: 200, pr: 2, borderRight: { md: `1px solid ${theme.palette.divider}` }, mb: { xs: 2, md: 0 } }}>
                  <Typography variant="subtitle2" sx={{ mb: 2, fontWeight: 600 }}>
                    {t("ordenCompra.filters.quickSelect", "Selección rápida")}
                  </Typography>
                  <Stack spacing={1}>
                    {["hoy", "semana", "mes", "trimestre", "anio"].map((type) => (
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
                    ".rdrCalendarWrapper": {
                      color: isDark ? "#e7f6f7" : "#000",
                      bgcolor: isDark ? "transparent" : "#fff",
                      borderRadius: 2,
                    },
                    ".rdrDayNumber span": { color: isDark ? "#e7f6f7" : "#1d2429" },
                    ".rdrDayPassive .rdrDayNumber span": { color: isDark ? "rgba(255,255,255,0.3)" : "#d5dce0" },
                    ".rdrMonthAndYearPickers select": { color: isDark ? "#e7f6f7" : "#3e484f" },
                    ".rdrNextPrevButton": { background: isDark ? "rgba(255,255,255,0.1)" : "#eff2f7" },
                    ".rdrWeekDay": { color: isDark ? "rgba(255,255,255,0.7)" : "#849095" },
                    ".rdrDateDisplayWrapper": { backgroundColor: isDark ? "transparent" : "#eff2f7" },
                    ".rdrDateDisplayItem": { backgroundColor: isDark ? "rgba(255,255,255,0.1)" : "#fff", boxShadow: isDark ? "none" : "0 1px 2px 0 rgba(35,57,66,.21)", borderColor: isDark ? "rgba(255,255,255,0.2)" : "transparent" },
                    ".rdrDateDisplayItemActive": { borderColor: isDark ? "#fff" : "transparent" },
                    ".rdrDateDisplayItem input": { color: isDark ? "#fff" : "#333" },
                  }}>
                    <DateRange
                      locale={i18n.language?.startsWith("en") ? enUS : es}
                      editableDateInputs={true}
                      onChange={handleSelectDateRange}
                      moveRangeOnFirstSelection={false}
                      ranges={[{
                        startDate: formReporte.fecha_inicio ? new Date(formReporte.fecha_inicio) : new Date(),
                        endDate: formReporte.fecha_fin ? new Date(formReporte.fecha_fin) : new Date(),
                        key: "selection",
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

      {/* Botones de acción */}
      <Box sx={{ display: "flex", justifyContent: "flex-end", mb: 4 }}>
        <Stack direction="row" spacing={2}>
          <Button
            variant="contained"
            color="primary"
            startIcon={<SearchIcon />}
            onClick={() => buscar(0, paginationModel.pageSize)}
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
            disabled={exporting}
            sx={{ borderRadius: 2, px: 3 }}
          >
            {t("ordenCompra.actions.generate", "Generar Reporte")}
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
          paginationModel={paginationModel}
          onPaginationModelChange={handlePaginationChange}
          rowCount={rowCount}
          pageSizeOptions={[5, 10, 20, 50]}
        />
      </Box>

      {/* Modal de Exportación */}
      <Dialog
        open={modalExportOpen}
        onClose={() => !exporting && setModalExportOpen(false)}
        maxWidth="xs"
        fullWidth
        PaperProps={{ sx: { borderRadius: 4, p: 1 } }}
      >
        <DialogTitle sx={{ fontWeight: 700, textAlign: "center", pb: 1 }}>
          {t("ordenCompra.modal.exportTitle", "Generar Reporte")}
        </DialogTitle>
        <DialogContent sx={{ textAlign: "center", pb: 2 }}>
          {exporting ? (
            <Box sx={{ py: 3 }}>
              <Typography variant="body2" sx={{ mb: 2 }}>
                {t("ordenCompra.modal.exporting", "Generando documento...")}
              </Typography>
              <LinearProgress color="success" />
            </Box>
          ) : (
            <Box sx={{ py: 2 }}>
              <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
                {t("ordenCompra.modal.exportSub", "Seleccione el formato.")}
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

      {/* Visor de Vista previa PDF */}
      <Dialog
        open={previewOpen}
        onClose={() => setPreviewOpen(false)}
        fullWidth
        maxWidth="lg"
        PaperProps={{ sx: { borderRadius: 4 } }}
      >
        <DialogTitle sx={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
          {t("ordenCompra.modal.previewTitle", "Vista previa")}
          <IconButton onClick={() => setPreviewOpen(false)}>
            <CloseIcon />
          </IconButton>
        </DialogTitle>
        <Divider />
        <DialogContent sx={{ p: 0, height: "80vh" }}>
          {previewUrl && (
            <iframe
              src={previewUrl}
              width="100%"
              height="100%"
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
