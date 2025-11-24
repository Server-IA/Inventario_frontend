import React, { useEffect } from "react";
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Grid,
  Box,
  Button,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  TextField,
  IconButton,
  Typography,
} from "@mui/material";
import CloseIcon from "@mui/icons-material/Close";

// Normaliza a array (por si viene en .content)
const asArray = (x) => (Array.isArray(x) ? x : x?.content ?? []);

export default function UbicacionPedidoFilters({
  // diálogo
  open = false,
  onClose = () => {},
  title = "Filtros (ubicación)",
  onApply = () => {},

  // ubicación
  ubiForm = {},
  ubiData = {},
  handleUbiChange = () => {},
  onUbiReset = () => {},
}) {
  // ====== Listas normalizadas ======
  const paises = asArray(ubiData.paises);
  const departamentos = asArray(ubiData.departamentos);
  const municipios = asArray(ubiData.municipios);
  const sedes = asArray(ubiData.sedes);
  const bloques = asArray(ubiData.bloques);
  const espacios = asArray(ubiData.espacios);
  const almacenes = asArray(ubiData.almacenes);

  // ====== AUTOSELECCIONAR UBICACIÓN (si solo hay 1 opción) ======

  // País
  useEffect(() => {
    if (paises.length === 1 && !ubiForm.pais_id) {
      const p = paises[0];
      handleUbiChange("pais_id")({
        target: { value: String(p.id ?? p.code ?? p.value) },
      });
    }
  }, [paises, ubiForm.pais_id, handleUbiChange]);

  // Departamento
  useEffect(() => {
    if (departamentos.length === 1 && !ubiForm.departamento_id) {
      const d = departamentos[0];
      handleUbiChange("departamento_id")({
        target: { value: String(d.id) },
      });
    }
  }, [departamentos, ubiForm.departamento_id, handleUbiChange]);

  // Municipio
  useEffect(() => {
    if (municipios.length === 1 && !ubiForm.municipio_id) {
      const m = municipios[0];
      handleUbiChange("municipio_id")({
        target: { value: String(m.id) },
      });
    }
  }, [municipios, ubiForm.municipio_id, handleUbiChange]);

  // Sede
  useEffect(() => {
    if (sedes.length === 1 && !ubiForm.sede_id) {
      const s = sedes[0];
      handleUbiChange("sede_id")({
        target: { value: String(s.id) },
      });
    }
  }, [sedes, ubiForm.sede_id, handleUbiChange]);

  // Bloque
  useEffect(() => {
    if (bloques.length === 1 && !ubiForm.bloque_id) {
      const b = bloques[0];
      handleUbiChange("bloque_id")({
        target: { value: String(b.id) },
      });
    }
  }, [bloques, ubiForm.bloque_id, handleUbiChange]);

  // Espacio
  useEffect(() => {
    if (espacios.length === 1 && !ubiForm.espacio_id) {
      const e = espacios[0];
      handleUbiChange("espacio_id")({
        target: { value: String(e.id) },
      });
    }
  }, [espacios, ubiForm.espacio_id, handleUbiChange]);

  // Almacén
  useEffect(() => {
    if (almacenes.length === 1 && !ubiForm.almacen_id) {
      const a = almacenes[0];
      handleUbiChange("almacen_id")({
        target: { value: String(a.id) },
      });
    }
  }, [almacenes, ubiForm.almacen_id, handleUbiChange]);

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="lg">
      <DialogTitle sx={{ pr: 6 }}>
        {title}
        <IconButton
          onClick={onClose}
          sx={{ position: "absolute", right: 8, top: 8 }}
        >
          <CloseIcon />
        </IconButton>
      </DialogTitle>

      <DialogContent dividers>
        {/* ====== Filtros de Ubicación ====== */}
        <Box mb={3}>
          <Typography variant="subtitle1" sx={{ mb: 1 }}>
            Ubicación
          </Typography>
          <Grid container spacing={2}>
            {/* País */}
            {paises.length >= 1 && (
              <Grid item xs={12} md={4}>
                <FormControl fullWidth>
                  <InputLabel>País</InputLabel>
                  <Select
                    name="pais_id"
                    label="País"
                    value={ubiForm.pais_id || ""}
                    onChange={handleUbiChange("pais_id")}
                  >
                    {paises.map((p) => (
                      <MenuItem
                        key={p.id ?? p.code ?? p.value}
                        value={String(p.id ?? p.code ?? p.value)}
                      >
                        {p.nombre ?? p.name ?? p.label ?? `País ${p.id}`}
                      </MenuItem>
                    ))}
                  </Select>
                </FormControl>
              </Grid>
            )}

            {/* Departamento */}
            {departamentos.length >= 1 && (
              <Grid item xs={12} md={4}>
                <FormControl fullWidth>
                  <InputLabel>Departamento</InputLabel>
                  <Select
                    name="departamento_id"
                    label="Departamento"
                    value={ubiForm.departamento_id || ""}
                    onChange={handleUbiChange("departamento_id")}
                  >
                    {departamentos.map((d) => (
                      <MenuItem key={d.id} value={String(d.id)}>
                        {d.nombre ?? d.name}
                      </MenuItem>
                    ))}
                  </Select>
                </FormControl>
              </Grid>
            )}

            {/* Municipio */}
            {municipios.length >= 1 && (
              <Grid item xs={12} md={4}>
                <FormControl fullWidth>
                  <InputLabel>Municipio</InputLabel>
                  <Select
                    name="municipio_id"
                    label="Municipio"
                    value={ubiForm.municipio_id || ""}
                    onChange={handleUbiChange("municipio_id")}
                  >
                    {municipios.map((m) => (
                      <MenuItem key={m.id} value={String(m.id)}>
                        {m.nombre ?? m.name}
                      </MenuItem>
                    ))}
                  </Select>
                </FormControl>
              </Grid>
            )}

            {/* Sede */}
            {sedes.length >= 1 && (
              <Grid item xs={12} md={4}>
                <FormControl fullWidth>
                  <InputLabel>Sede</InputLabel>
                  <Select
                    name="sede_id"
                    label="Sede"
                    value={ubiForm.sede_id || ""}
                    onChange={handleUbiChange("sede_id")}
                  >
                    {sedes.map((s) => (
                      <MenuItem key={s.id} value={String(s.id)}>
                        {s.nombre ?? s.name}
                      </MenuItem>
                    ))}
                  </Select>
                </FormControl>
              </Grid>
            )}

            {/* Bloque */}
            {bloques.length >= 1 && (
              <Grid item xs={12} md={4}>
                <FormControl fullWidth>
                  <InputLabel>Bloque</InputLabel>
                  <Select
                    name="bloque_id"
                    label="Bloque"
                    value={ubiForm.bloque_id || ""}
                    onChange={handleUbiChange("bloque_id")}
                  >
                    {bloques.map((b) => (
                      <MenuItem key={b.id} value={String(b.id)}>
                        {b.nombre ?? b.name}
                      </MenuItem>
                    ))}
                  </Select>
                </FormControl>
              </Grid>
            )}

            {/* Espacio */}
            {espacios.length >= 1 && (
              <Grid item xs={12} md={4}>
                <FormControl fullWidth>
                  <InputLabel>Espacio</InputLabel>
                  <Select
                    name="espacio_id"
                    label="Espacio"
                    value={ubiForm.espacio_id || ""}
                    onChange={handleUbiChange("espacio_id")}
                  >
                    {espacios.map((e) => (
                      <MenuItem key={e.id} value={String(e.id)}>
                        {e.nombre ?? e.name}
                      </MenuItem>
                    ))}
                  </Select>
                </FormControl>
              </Grid>
            )}

            {/* Almacén */}
            {almacenes.length >= 1 && (
              <Grid item xs={12} md={4}>
                <FormControl fullWidth>
                  <InputLabel>Almacén</InputLabel>
                  <Select
                    name="almacen_id"
                    label="Almacén"
                    value={ubiForm.almacen_id || ""}
                    onChange={handleUbiChange("almacen_id")}
                  >
                    {almacenes.map((a) => (
                      <MenuItem key={a.id} value={String(a.id)}>
                        {a.nombre ?? a.name}
                      </MenuItem>
                    ))}
                  </Select>
                </FormControl>
              </Grid>
            )}
          </Grid>

          <Box mt={2}>
            <Button size="small" onClick={onUbiReset}>
              Limpiar ubicación
            </Button>
          </Box>
        </Box>
      </DialogContent>

      <DialogActions>
        <Button onClick={onClose}>Cerrar</Button>
        <Button variant="contained" onClick={onApply}>
          Aplicar
        </Button>
      </DialogActions>
    </Dialog>
  );
}
