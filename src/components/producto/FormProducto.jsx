import React, { useEffect, useState } from "react";
import {
  Dialog, DialogTitle, DialogContent, DialogActions,
  Grid, TextField, FormControlLabel, Checkbox, Button,
  FormControl, InputLabel, Select, MenuItem, FormHelperText, Stack
} from "@mui/material";
import AddRounded from "@mui/icons-material/AddRounded";
import SaveRounded from "@mui/icons-material/SaveRounded";
import CloseRounded from "@mui/icons-material/CloseRounded";

const emptyForm = {
  id: null,
  nombre: "",
  descripcion: "",
  productoCategoriaId: "",
  estadoId: "",
  unidadMinimaId: "",
  cantidadMinima: "",
  esOrganico: false,
};

export default function FormProducto({
  open,
  onClose,
  initialData = emptyForm,
  onSubmit,
  categorias = [],
  unidades = [],
}) {
  const [formData, setFormData] = useState(emptyForm);
  const [errors, setErrors] = useState({});

  useEffect(() => {
    setFormData({ ...emptyForm, ...(initialData || {}) });
    setErrors({});
  }, [initialData]);

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: type === "checkbox" ? checked : value,
    }));
    setErrors((prev) => ({ ...prev, [name]: "" }));
  };

  const validate = () => {
    const errs = {};
    if (!formData.nombre) errs.nombre = "Campo requerido";
    if (!formData.productoCategoriaId) errs.productoCategoriaId = "Campo requerido";
    if (!formData.estadoId) errs.estadoId = "Campo requerido";
    if (!formData.unidadMinimaId) errs.unidadMinimaId = "Campo requerido";
    setErrors(errs);
    return Object.keys(errs).length === 0;
  };

  const doSubmit = () => {
    if (!validate()) return;
    const payload = {
      nombre: formData.nombre,
      productoCategoriaId: Number(formData.productoCategoriaId),
      descripcion: formData.descripcion || "",
      estadoId: Number(formData.estadoId),
      unidadMinimaId: Number(formData.unidadMinimaId),
      cantidadMinima: formData.cantidadMinima === "" ? null : Number(formData.cantidadMinima),
      esOrganico: Boolean(formData.esOrganico),
      ...(formData.id ? { id: formData.id } : {}),
    };
    onSubmit(payload);
  };

  const isEdit = Boolean(formData.id);

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
      <DialogTitle>{isEdit ? "Editar producto" : "Nuevo producto"}</DialogTitle>

      {/* permite enviar con Enter */}
      <form
        onSubmit={(e) => {
          e.preventDefault();
          doSubmit();
        }}
      >
        <DialogContent dividers>
          <Grid container spacing={2} sx={{ mt: 0.5 }}>
            <Grid item xs={12}>
              <TextField
                label="Nombre"
                fullWidth
                required
                name="nombre"
                value={formData.nombre}
                onChange={handleChange}
                error={!!errors.nombre}
                helperText={errors.nombre}
              />
            </Grid>

            <Grid item xs={12}>
              <TextField
                label="Descripción"
                fullWidth
                multiline
                minRows={2}
                name="descripcion"
                value={formData.descripcion}
                onChange={handleChange}
              />
            </Grid>

            {/* Selector de Categoría */}
            <Grid item xs={12} sm={6}>
              <FormControl fullWidth margin="normal" error={!!errors.productoCategoriaId}>
                <InputLabel>Categoría</InputLabel>
                <Select
                  name="productoCategoriaId"
                  value={formData.productoCategoriaId}
                  onChange={handleChange}
                  label="Categoría"
                >
                  {categorias.map((c) => (
                    <MenuItem key={c.id} value={c.id}>
                      {c.nombre ?? c.name}
                    </MenuItem>
                  ))}
                </Select>
                {errors.productoCategoriaId && (
                  <FormHelperText>{errors.productoCategoriaId}</FormHelperText>
                )}
              </FormControl>
            </Grid>

            {/* Selector de Estado fijo (Activo/Inactivo) */}
            <Grid item xs={12} sm={6}>
              <FormControl fullWidth margin="normal" error={!!errors.estadoId}>
                <InputLabel>Estado</InputLabel>
                <Select
                  name="estadoId"
                  value={formData.estadoId}
                  onChange={handleChange}
                  label="Estado"
                >
                  <MenuItem value={1}>Activo</MenuItem>
                  <MenuItem value={2}>Inactivo</MenuItem>
                </Select>
                {errors.estadoId && <FormHelperText>{errors.estadoId}</FormHelperText>}
              </FormControl>
            </Grid>

            {/* Selector de Unidad mínima */}
            <Grid item xs={12} sm={6}>
              <FormControl fullWidth margin="normal" error={!!errors.unidadMinimaId}>
                <InputLabel>Unidad mínima</InputLabel>
                <Select
                  name="unidadMinimaId"
                  value={formData.unidadMinimaId}
                  onChange={handleChange}
                  label="Unidad mínima"
                >
                  {unidades.map((u) => (
                    <MenuItem key={u.id} value={u.id}>
                      {u.nombre ?? u.name}
                    </MenuItem>
                  ))}
                </Select>
                {errors.unidadMinimaId && (
                  <FormHelperText>{errors.unidadMinimaId}</FormHelperText>
                )}
              </FormControl>
            </Grid>

            {/* Campo cantidad mínima */}
            <Grid item xs={12} sm={6}>
              <TextField
                label="Cantidad mínima"
                type="number"
                fullWidth
                name="cantidadMinima"
                value={formData.cantidadMinima}
                onChange={handleChange}
              />
            </Grid>

            {/* Checkbox de orgánico */}
            <Grid item xs={12}>
              <FormControlLabel
                control={
                  <Checkbox
                    name="esOrganico"
                    checked={!!formData.esOrganico}
                    onChange={handleChange}
                  />
                }
                label="Es orgánico"
              />
            </Grid>
          </Grid>
        </DialogContent>

        {/* ==== Botonera estilo “Producción” ==== */}
        <DialogActions sx={{ px: 3, py: 2 }}>
          <Stack direction="row" spacing={1} sx={{ width: "100%" }} justifyContent="flex-end">
            {/* Cancelar (outlined) */}
            <Button
              type="button"
              onClick={onClose}
              startIcon={<CloseRounded />}
              sx={(t) => ({
                borderRadius: 999,
                px: 2,
                py: 0.9,
                fontWeight: 800,
                textTransform: "uppercase",
                border: `1px solid ${t.palette.divider}`,
                color: t.palette.text.secondary,
              })}
            >
              Cancelar
            </Button>

            {/* Crear / Guardar (cápsula blanca con sombra) */}
            <Button
              type="submit"
              startIcon={isEdit ? <SaveRounded /> : <AddRounded />}
              sx={(t) => ({
                borderRadius: 999,
                px: 2.2,
                py: 0.9,
                fontWeight: 800,
                letterSpacing: 0.5,
                textTransform: "uppercase",
                bgcolor: t.palette.background.paper,
                color: t.palette.text.primary,
                boxShadow: 2,
                border: `1px solid ${t.palette.divider}`,
                "&:hover": { boxShadow: 3, bgcolor: t.palette.background.paper },
              })}
            >
              {isEdit ? "Guardar" : "Crear"}
            </Button>
          </Stack>
        </DialogActions>
      </form>
    </Dialog>
  );
}
