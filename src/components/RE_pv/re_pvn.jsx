import React, { useEffect, useState, useCallback } from "react";
import {
  Box, Typography, TextField, Button, Stack, Grid,
  FormControl, InputLabel, Select, MenuItem,
  Dialog, DialogTitle, DialogContent, IconButton,
  Table, TableBody, TableCell, TableHead, TableRow,
  TableContainer, Paper
} from "@mui/material";
import CloseIcon from "@mui/icons-material/Close";
import axios from "../axiosConfig";
import MessageSnackBar from "../MessageSnackBar";

import useUbicacionFilters from "../useUbicacionFilters";
import UbicacionProductoVencimientoFilters from "../UbicacionProductoVencimientoFilters.jsx";

export default function RE_productoVencimiento() {
  // === Auth / empresa
  const empresaId = localStorage.getItem("empresaId");
  const token = localStorage.getItem("token");
  const headers = { headers: { Authorization: `Bearer ${token}` } };

  // === Helpers
  const asArray = (x) => (Array.isArray(x) ? x : x?.content ?? x?.data ?? []);
  const toMin = (val, end = false) => {
    if (!val) return null;
    const [d, t] = String(val).split("T");
    const hhmm = (t || (end ? "23:59" : "00:00")).slice(0, 5);
    return `${d} ${hhmm}`;
  };

  // ALIAS EXACTOS del JRXML
  const ALIAS_EMP = "em.emp_id";
  const ALIAS_VENC = "k.kai_fecha_vencimiento";

  // ===== Hook unificado: ubicación (igual que en RE_pedido) =====
  const {
    form: ubi,
    handleChange: handleUbiChange,
    data: ubiData,
    resetTodo,
    // si tu hook tiene más cosas, se ignoran aquí
  } = useUbicacionFilters({ empresaId, headers, autoselectSingle: true });

  // ===== Filtros de producto / fechas =====
  const [filtro, setFiltro] = useState({
    producto_id: "",
    producto_categoria_id: "",
    fecha_inicio: "",
    fecha_fin: "",
  });

  // ===== Estados adicionales de UI =====
  const [productos, setProductos] = useState([]);
  const [categorias, setCategorias] = useState([]);
  const [resultados, setResultados] = useState([]);
  const [previewUrl, setPreviewUrl] = useState("");
  const [previewOpen, setPreviewOpen] = useState(false);
  const [message, setMessage] = useState({
    open: false,
    severity: "info",
    text: "",
  });
  const [errors, setErrors] = useState({ fechas_rango: false });
  const [openUbi, setOpenUbi] = useState(false);

  // === Cargar catálogos (productos / categorías) ===
  useEffect(() => {
    Promise.all([
      axios.get("/v1/items/producto/0", headers).catch(() => ({ data: [] })),
      axios.get("/v1/items/producto_categoria/0", headers).catch(() => ({ data: [] })),
    ])
      .then(([pr, cat]) => {
        setProductos(asArray(pr.data));
        setCategorias(asArray(cat.data));
      })
      .catch(() =>
        setMessage({
          open: true,
          severity: "error",
          text: "No se pudieron cargar los catálogos.",
        })
      );
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const handleFiltroChange = (field) => (e) => {
    const value = e?.target ? e.target.value : e;
    setFiltro((prev) => ({ ...prev, [field]: value }));
  };

  const validarRango = () => {
    setErrors({ fechas_rango: false });
    if (filtro.fecha_inicio && filtro.fecha_fin) {
      const ini = new Date(filtro.fecha_inicio);
      const fin = new Date(filtro.fecha_fin);
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

  // ===== Buscar (solo UI, igual idea que RE_pedido) =====
  const buscar = useCallback(async () => {
    setResultados([]);
    setErrors({ fechas_rango: false });

    if (!validarRango()) return;

    try {
      const r = await axios.get("/v1/items/producto/0", headers);
      let lista = asArray(r.data);

      // Filtro por categoría
      if (filtro.producto_categoria_id) {
        lista = lista.filter((p) =>
          String(
            p.productoCategoriaId ??
              p.producto_categoria_id ??
              p.categoriaId ??
              ""
          ) === String(filtro.producto_categoria_id)
        );
      }

      // Filtro por producto
      if (filtro.producto_id) {
        lista = lista.filter((p) => String(p.id) === String(filtro.producto_id));
      }

      // (Opcional) aquí podrías aplicar filtros por ubicación (ubi.*) si tu API trae esos datos

      setResultados(lista);
      setMessage({
        open: true,
        severity: "info",
        text: `Mostrando ${lista.length} producto(s).`,
      });
    } catch (err) {
      console.error(err);
      setResultados([]);
      setMessage({
        open: true,
        severity: "error",
        text: "No se pudieron cargar productos.",
      });
    }
  }, [filtro, headers]);

  // ===== WHERE para el reporte (como en RE_pedido, pero con vencimiento) =====
  const buildCondicion = () => {
    const DEF_INI = "1900-01-01 00:00";
    const DEF_FIN = "2099-12-31 23:59";

    const ini = toMin(filtro.fecha_inicio, false);
    const fin = toMin(filtro.fecha_fin, true);

    const out = {};
    let idx = 0;

    // 0) Empresa (obligatoria)
    out[String(idx++)] = `${ALIAS_EMP} = $EMPRESA_ID$`;

    // 1) Producto (opcional)
    if (filtro.producto_id) {
      out[String(idx++)] = `AND p.pro_id = ${Number(filtro.producto_id)}`;
    }

    // 2) Categoría de producto (opcional)
    if (filtro.producto_categoria_id) {
      out[String(idx++)] = `AND p.pro_producto_categoria_id = ${Number(
        filtro.producto_categoria_id
      )}`;
    }

    // 3) Fechas de vencimiento (si no hay ambas, BETWEEN amplio)
    if (ini && fin) {
      out[String(idx++)] = `AND ${ALIAS_VENC} BETWEEN "${ini}" AND "${fin}"`;
    } else {
      out[String(idx++)] = `AND ${ALIAS_VENC} BETWEEN "${DEF_INI}" AND "${DEF_FIN}"`;
    }

    // (Opcional) aquí puedes agregar condiciones por ubicación usando ubi.*
    // Ejemplo:
    // if (ubi.almacen_id) {
    //   out[String(idx++)] = `AND k.kai_almacen_id = ${Number(ubi.almacen_id)}`;
    // }

    return out;
  };

  // ===== POST PDF =====
  const generarPDF = async () => {
    if (!validarRango()) return;

    const condicion = buildCondicion();
    const payload = { condicion, EMPRESA_ID: empresaId };

    try {
      const res = await axios({
        url: "/v2/report/nuevo/producto_vencimiento",
        method: "POST",
        data: payload,
        responseType: "blob",
        ...headers,
      });

      const contentType = res.headers?.["content-type"] || "";
      if (!contentType.includes("pdf")) {
        try {
          const txt = await res.data.text?.();
          throw new Error(txt || "El servidor no devolvió un PDF.");
        } catch {
          throw new Error("El servidor no devolvió un PDF.");
        }
      }

      const blob = new Blob([res.data], { type: "application/pdf" });
      if (previewUrl) URL.revokeObjectURL(previewUrl);
      const url = window.URL.createObjectURL(blob);
      setPreviewUrl(url);
      setPreviewOpen(true);
      setMessage({ open: true, severity: "success", text: "PDF generado." });
    } catch (err) {
      console.error("❌ Error al generar PDF:", err);
      setMessage({
        open: true,
        severity: "error",
        text:
          "No fue posible generar el PDF. Verifica que la plantilla use em.emp_id y k.kai_fecha_vencimiento, y que la API /v2/report/nuevo/producto_vencimiento esté disponible.",
      });
    }
  };

  return (
    <Box sx={{ p: 4 }}>
      <Typography variant="h4" gutterBottom>
        Reporte de Vencimiento de Producto
      </Typography>

      {/* Filtros principales (como en RE_pedido) */}
      <Grid container spacing={2} mb={2}>
        <Grid item xs={12} md={6}>
          <FormControl fullWidth>
            <InputLabel>Producto</InputLabel>
            <Select
              name="producto_id"
              value={filtro.producto_id || ""}
              label="Producto"
              onChange={handleFiltroChange("producto_id")}
            >
              <MenuItem value="">
                <em>Todos</em>
              </MenuItem>
              {asArray(productos).map((p) => (
                <MenuItem key={p.id} value={String(p.id)}>
                  {p.nombre ?? p.name ?? `Producto ${p.id}`}
                </MenuItem>
              ))}
            </Select>
          </FormControl>
        </Grid>

        <Grid item xs={12} md={6}>
          <FormControl fullWidth>
            <InputLabel>Categoría Producto</InputLabel>
            <Select
              name="producto_categoria_id"
              value={filtro.producto_categoria_id || ""}
              label="Categoría Producto"
              onChange={handleFiltroChange("producto_categoria_id")}
            >
              <MenuItem value="">
                <em>Todas</em>
              </MenuItem>
              {asArray(categorias).length ? (
                asArray(categorias).map((c) => (
                  <MenuItem key={c.id} value={String(c.id)}>
                    {c.nombre ?? c.name ?? `Categoría ${c.id}`}
                  </MenuItem>
                ))
              ) : (
                <MenuItem disabled value="">
                  Sin opciones
                </MenuItem>
              )}
            </Select>
          </FormControl>
        </Grid>

        {/* Fechas (opcionales) */}
        <Grid item xs={12} md={6}>
          <TextField
            label="Fecha Inicio"
            name="fecha_inicio"
            type="datetime-local"
            fullWidth
            value={filtro.fecha_inicio || ""}
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
            value={filtro.fecha_fin || ""}
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
        <Button variant="outlined" onClick={generarPDF}>
          Generar Reporte
        </Button>
        <Button variant="text" onClick={() => setOpenUbi(true)}>
          Filtros (ubicación )
        </Button>
      </Stack>

      {/* Diálogo: filtros Ubicación + Producto (igual patrón que UbicacionPedidoFilters) */}
      <UbicacionProductoVencimientoFilters
        variant="dialog"
        title="Filtros (ubicación)"
        open={openUbi}
        onClose={() => setOpenUbi(false)}
        // Ubicación
        ubiForm={ubi}
        ubiData={ubiData}
        handleUbiChange={handleUbiChange}
        onUbiReset={resetTodo}
        // Producto / fechas
        filtro={filtro}
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

      {/* Vista previa PDF */}
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

      {/* Resultados de la búsqueda (demo) */}
      {resultados.length > 0 && (
        <Box mt={4}>
          <Typography variant="h6" gutterBottom>
            Productos encontrados
          </Typography>
          <TableContainer component={Paper} variant="outlined">
            <Table size="small">
              <TableHead>
                <TableRow>
                  <TableCell>ID</TableCell>
                  <TableCell>Nombre</TableCell>
                  <TableCell>Categoría</TableCell>
                  <TableCell>Descripción</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {resultados.map((p) => (
                  <TableRow key={p.id}>
                    <TableCell>{p.id}</TableCell>
                    <TableCell>{p.nombre ?? p.name ?? ""}</TableCell>
                    <TableCell>
                      {p.productoCategoriaId ??
                        p.categoriaId ??
                        p.producto_categoria_id ??
                        ""}
                    </TableCell>
                    <TableCell>{p.descripcion ?? ""}</TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        </Box>
      )}

      <MessageSnackBar message={message} setMessage={setMessage} />
    </Box>
  );
}
