/*=============================================================================
 Nombre del archivo : FiltroCostoIndirecto.jsx
 Descripcion        : Diálogo de filtros del listado de costos indirectos
                      (tipo, ubicación en cascada, estado y periodo).
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |   Fecha    | Versión |      Autor           | Descripción del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2026-09-26 | 0.4.0   | Arekkazu             | Creación del archivo para   |
 |            |         |                      | la HU-045.2.                |
 +------------+---------+----------------------+-----------------------------+
=============================================================================*/
/**
 * @module FiltroCostoIndirecto
 * @description Filtros de `GET /v1/costos-indirectos`. Reutiliza los
 * selectores y catálogos activos del formulario de registro (HU-045.1).
 */
import React, { useEffect, useState } from "react";
import PropTypes from "prop-types";
import { Box, Button, Dialog, DialogActions, DialogContent, DialogTitle, MenuItem, TextField } from "@mui/material";
import { useTranslation } from "react-i18next";
import { Selector, useOpciones } from "./FormCostoIndirecto.jsx";
import { seleccionarNivel } from "./costoIndirectoValidation";
import { getTipos, getSedes, getBloques, getEspacios, getAlmacenes } from "./costoIndirectoCatalogos";

export const filtrosVacios = {
  tipo: null,
  sede: null,
  bloque: null,
  espacio: null,
  almacen: null,
  estadoId: "",
  fechaInicio: "",
  fechaFin: "",
};

/**
 * Convierte los filtros en query params del backend; omite los vacíos.
 * @param {Object} filtros Filtros aplicados.
 * @returns {Object}
 */
export const toParams = ({ tipo, sede, bloque, espacio, almacen, estadoId, fechaInicio, fechaFin }) =>
  Object.fromEntries(
    Object.entries({
      tipoCostoIndirectoId: tipo?.id,
      sedeId: sede?.id,
      bloqueId: bloque?.id,
      espacioId: espacio?.id,
      almacenId: almacen?.id,
      estadoId,
      fechaInicio,
      fechaFin,
    }).filter(([, valor]) => valor != null && valor !== "")
  );

const FULL_ROW = { gridColumn: "1 / -1" };

export default function FiltroCostoIndirecto({ open, filtros, onApply, onClose }) {
  const { t } = useTranslation();
  const [draft, setDraft] = useState(filtros);

  useEffect(() => {
    if (open) setDraft(filtros);
  }, [open, filtros]);

  const tipos = useOpciones(getTipos, undefined, open);
  const sedes = useOpciones(getSedes, undefined, open);
  const bloques = useOpciones(getBloques, draft.sede?.id, open && Boolean(draft.sede));
  const espacios = useOpciones(getEspacios, draft.bloque?.id, open && Boolean(draft.bloque));
  const almacenes = useOpciones(getAlmacenes, draft.espacio?.id, open && Boolean(draft.espacio));

  const t2 = (key) => t(`costos.costoIndirecto.${key}`);
  const fieldLabel = (campo) => t2(`form.fields.${campo}`);
  const noOptionsText = t2("form.helpers.noOptions");

  const handleSelect = (campo, valor) => setDraft((prev) => seleccionarNivel(prev, campo, valor));
  const handleChange = (event) => {
    const { name, value } = event.target;
    setDraft((prev) => ({ ...prev, [name]: value }));
  };

  const helper = (catalogo, padre, selectFirstKey) => {
    if (catalogo.error) return t2("form.helpers.catalogError");
    return padre === undefined || padre ? undefined : t2(selectFirstKey);
  };

  const selector = (campo, catalogo, padre, selectFirstKey) => (
    <Selector
      name={campo}
      label={fieldLabel(campo)}
      value={draft[campo]}
      catalogo={catalogo}
      disabled={padre !== undefined && !padre}
      helperText={helper(catalogo, padre, selectFirstKey)}
      onChange={handleSelect}
      noOptionsText={noOptionsText}
    />
  );

  return (
    <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
      <DialogTitle>{t2("filters.title")}</DialogTitle>
      <DialogContent>
        <Box sx={{ display: "grid", gridTemplateColumns: { xs: "1fr", sm: "1fr 1fr" }, gap: 2, pt: 1 }}>
          <Box sx={FULL_ROW}>{selector("tipo", tipos)}</Box>
          <Box sx={FULL_ROW}>{selector("sede", sedes)}</Box>
          {selector("bloque", bloques, draft.sede, "form.helpers.selectSedeFirst")}
          {selector("espacio", espacios, draft.bloque, "form.helpers.selectBloqueFirst")}
          <Box sx={FULL_ROW}>{selector("almacen", almacenes, draft.espacio, "form.helpers.selectEspacioFirst")}</Box>
          <TextField
            sx={FULL_ROW}
            select
            name="estadoId"
            label={t2("columns.estado")}
            value={draft.estadoId}
            onChange={handleChange}
          >
            <MenuItem value="">{t("common.labels.all")}</MenuItem>
            <MenuItem value={1}>{t("common.labels.active")}</MenuItem>
            <MenuItem value={2}>{t("common.labels.inactive")}</MenuItem>
          </TextField>
          <TextField
            name="fechaInicio"
            type="date"
            label={fieldLabel("fechaInicio")}
            value={draft.fechaInicio}
            onChange={handleChange}
            InputLabelProps={{ shrink: true }}
          />
          <TextField
            name="fechaFin"
            type="date"
            label={fieldLabel("fechaFin")}
            value={draft.fechaFin}
            onChange={handleChange}
            InputLabelProps={{ shrink: true }}
            inputProps={{ min: draft.fechaInicio || undefined }}
          />
        </Box>
      </DialogContent>
      <DialogActions>
        <Button onClick={() => onApply(filtrosVacios)}>{t("common.actions.clear")}</Button>
        <Button variant="contained" onClick={() => onApply(draft)}>
          {t("common.actions.apply")}
        </Button>
      </DialogActions>
    </Dialog>
  );
}

FiltroCostoIndirecto.propTypes = {
  open: PropTypes.bool.isRequired,
  filtros: PropTypes.object.isRequired,
  onApply: PropTypes.func.isRequired,
  onClose: PropTypes.func.isRequired,
};
