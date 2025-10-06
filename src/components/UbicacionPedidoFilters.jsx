// src/components/UbicacionPedidoFilters.jsx
import React from "react";
import {
  Dialog, DialogTitle, DialogContent, DialogActions,
  Grid, Box, Button, FormControl, InputLabel, Select, MenuItem,
  TextField, IconButton, Typography
} from "@mui/material";
import CloseIcon from "@mui/icons-material/Close";

const asArray = (x) => (Array.isArray(x) ? x : x?.content ?? []);

export default function UbicacionPedidoFilters({
  // diálogo
  open = false,
  onClose = () => {},
  title = "Filtros",
  onApply = () => {},

  // ubicación
  ubiForm = {},
  ubiData = {},
  handleUbiChange = () => {},
  onUbiReset = () => {},

  // pedido
  pedido = {},
  pedidos = [],
  pedidoEstados = [],         // <<--- IMPORTANTE: ahora se llama pedidoEstados
  handlePedidoChange = () => {},
  fechasError = false,
}) {
  const pedidosList = asArray(pedidos);
  const estadosList = Array.isArray(pedidoEstados) ? pedidoEstados : []; // evita undefined.length

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="lg">
      <DialogTitle sx={{ pr: 6 }}>
        {title}
        <IconButton onClick={onClose} sx={{ position: "absolute", right: 8, top: 8 }}>
          <CloseIcon />
        </IconButton>
      </DialogTitle>

      <DialogContent dividers>
        {/* ====== Filtros de Ubicación (opcional, ajusta a tus campos reales) ====== */}
        <Box mb={3}>
          <Typography variant="subtitle1" sx={{ mb: 1 }}>Ubicación</Typography>
          <Grid container spacing={2}>
            <Grid item xs={12} md={4}>
              <FormControl fullWidth>
                <InputLabel>País</InputLabel>
                <Select
                  name="pais_id"
                  label="País"
                  value={ubiForm.pais_id || ""}
                  onChange={handleUbiChange("pais_id")}
                >
                  {asArray(ubiData.paises).map(p => (
                    <MenuItem key={p.id ?? p.code ?? p.value} value={String(p.id ?? p.code ?? p.value)}>
                      {p.nombre ?? p.name ?? p.label ?? `País ${p.id}`}
                    </MenuItem>
                  ))}
                  {!asArray(ubiData.paises).length && <MenuItem disabled value="">Sin opciones</MenuItem>}
                </Select>
              </FormControl>
            </Grid>

            <Grid item xs={12} md={4}>
              <FormControl fullWidth>
                <InputLabel>Departamento</InputLabel>
                <Select
                  name="departamento_id"
                  label="Departamento"
                  value={ubiForm.departamento_id || ""}
                  onChange={handleUbiChange("departamento_id")}
                >
                  {asArray(ubiData.departamentos).map(d => (
                    <MenuItem key={d.id} value={String(d.id)}>
                      {d.nombre ?? d.name}
                    </MenuItem>
                  ))}
                  {!asArray(ubiData.departamentos).length && <MenuItem disabled value="">Sin opciones</MenuItem>}
                </Select>
              </FormControl>
            </Grid>

            <Grid item xs={12} md={4}>
              <FormControl fullWidth>
                <InputLabel>Municipio</InputLabel>
                <Select
                  name="municipio_id"
                  label="Municipio"
                  value={ubiForm.municipio_id || ""}
                  onChange={handleUbiChange("municipio_id")}
                >
                  {asArray(ubiData.municipios).map(m => (
                    <MenuItem key={m.id} value={String(m.id)}>
                      {m.nombre ?? m.name}
                    </MenuItem>
                  ))}
                  {!asArray(ubiData.municipios).length && <MenuItem disabled value="">Sin opciones</MenuItem>}
                </Select>
              </FormControl>
            </Grid>
          </Grid>

          <Box mt={2}>
            <Button size="small" onClick={onUbiReset}>Limpiar ubicación</Button>
          </Box>
        </Box>

        {/* ====== Filtros de Pedido ====== */}
        <Box>
          <Typography variant="subtitle1" sx={{ mb: 1 }}>Pedido</Typography>
          <Grid container spacing={2}>
            <Grid item xs={12} md={6}>
              <FormControl fullWidth>
                <InputLabel>Pedido</InputLabel>
                <Select
                  name="pedido_id"
                  label="Pedido"
                  value={pedido.pedido_id || ""}
                  onChange={handlePedidoChange("pedido_id")}
                >
                  {pedidosList.map(p => (
                    <MenuItem key={p.id} value={String(p.id)}>{`Pedido ${p.id}`}</MenuItem>
                  ))}
                  {!pedidosList.length && <MenuItem disabled value="">Sin opciones</MenuItem>}
                </Select>
              </FormControl>
            </Grid>

            <Grid item xs={12} md={6}>
              <FormControl fullWidth>
                <InputLabel>Estado del pedido</InputLabel>
                <Select
                  name="pedido_estado_id"
                  label="Estado del pedido"
                  value={pedido.pedido_estado_id || ""}
                  onChange={handlePedidoChange("pedido_estado_id")}
                >
                  {estadosList.map(e => (
                    <MenuItem key={e.id} value={String(e.id)}>
                      {e.name /* el endpoint /v1/items/pedido_estado/0 trae 'name' */}
                    </MenuItem>
                  ))}
                  {!estadosList.length && <MenuItem disabled value="">Sin opciones</MenuItem>}
                </Select>
              </FormControl>
            </Grid>

            <Grid item xs={12} md={6}>
              <TextField
                label="Fecha inicio"
                type="datetime-local"
                name="fecha_inicio"
                value={pedido.fecha_inicio || ""}
                onChange={handlePedidoChange("fecha_inicio")}
                InputLabelProps={{ shrink: true }}
                fullWidth
                error={!!fechasError}
                helperText={fechasError ? "Inicio no puede ser mayor que fin." : ""}
              />
            </Grid>

            <Grid item xs={12} md={6}>
              <TextField
                label="Fecha fin"
                type="datetime-local"
                name="fecha_fin"
                value={pedido.fecha_fin || ""}
                onChange={handlePedidoChange("fecha_fin")}
                InputLabelProps={{ shrink: true }}
                fullWidth
                error={!!fechasError}
                helperText={fechasError ? "Fin debe ser >= Inicio." : ""}
              />
            </Grid>
          </Grid>
        </Box>
      </DialogContent>

      <DialogActions>
        <Button onClick={onClose}>Cerrar</Button>
        <Button variant="contained" onClick={onApply}>Aplicar</Button>
      </DialogActions>
    </Dialog>
  );
}
