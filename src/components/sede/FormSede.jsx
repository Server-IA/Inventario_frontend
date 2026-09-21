/*=============================================================================
 Nombre del archivo : FormSede.jsx
 Descripcion        : Formulario de creacion y edicion de sedes.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |   Fecha    | Versión |      Autor           | Descripción del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2026-09-21 | 0.4.0   | Cesar Medina         | Agrega cascada geografica   |
 |            |         |                      | pais/departamento/municipio |
 |            |         |                      | y validaciones de coherencia|
 |            |         |                      | con i18n.                   |
 +------------+---------+----------------------+-----------------------------+
=============================================================================*/

import React, { useEffect, useMemo, useState } from "react";
import PropTypes from "prop-types";
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Button,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  FormHelperText,
} from "@mui/material";
import axios from "../axiosConfig";
import { useTranslation } from "react-i18next";
import { validateCamposBase } from "../utils/validations";

const asArray = (payload) =>
  Array.isArray(payload) ? payload : payload?.content ?? [];

export default function FormSede({
  open = false,
  setOpen = () => {},
  formMode = "create",
  selectedRow = null,
  initialPaisId = "",
  initialDeptoId = "",
  initialMunicipioId = "",
  grupos = [],
  tiposSede = [],
  reloadData = () => {},
  setMessage = () => {},
  authHeaders = {},
}) {
  const { t } = useTranslation();
  const initialData = {
    id: null,
    paisId: initialPaisId || "",
    deptoId: initialDeptoId || "",
    grupoId: "",
    tipoSedeId: "",
    nombre: "",
    municipioId: initialMunicipioId || "",
    geolocalizacion: "",
    coordenadas: "",
    area: "",
    comuna: "",
    descripcion: "",
    estadoId: 1,
  };

  const [formData, setFormData] = useState(initialData);
  const [errors, setErrors] = useState({});
  const [paisesOpts, setPaisesOpts] = useState([]);
  const [departamentosCatalog, setDepartamentosCatalog] = useState([]);
  const [municipiosCatalog, setMunicipiosCatalog] = useState([]);
  const [departamentosOpts, setDepartamentosOpts] = useState([]);
  const [municipiosOpts, setMunicipiosOpts] = useState([]);

  const invalidCharsRegex = /[<>/"'`;(){}[\]\\]/g;
  const sqliWordsRegex =
    /\b(select|insert|update|delete|drop|union|exec|xp_|information_schema)\b|--|\/\*|\*\//i;

  const cleanText = (v = "") => v.replace(invalidCharsRegex, "");

  const toNum = (v) =>
    v === null || v === undefined || v === "" ? "" : Number(v);

  const getSafeId = (...values) => {
    for (const v of values) {
      const n = Number(v);
      if (!isNaN(n) && n > 0) return n;
    }
    return "";
  };

  const departamentosById = useMemo(
    () =>
      new Map(
        departamentosCatalog.map((departamento) => [Number(departamento.id), departamento])
      ),
    [departamentosCatalog]
  );
  const municipiosById = useMemo(
    () =>
      new Map(
        municipiosCatalog.map((municipio) => [Number(municipio.id), municipio])
      ),
    [municipiosCatalog]
  );

  const resolveInitialLocation = ({
    row,
    municipios,
    departamentos,
  }) => {
    const municipioId = getSafeId(
      row?.municipioId,
      row?.municipio?.id,
      initialMunicipioId
    );
    const municipio = municipios.find(
      (item) => Number(item.id) === Number(municipioId)
    );
    const deptoId = getSafeId(
      row?.deptoId,
      row?.departamentoId,
      row?.departamento?.id,
      municipio?.departamentoId,
      initialDeptoId
    );
    const departamento = departamentos.find(
      (item) => Number(item.id) === Number(deptoId)
    );
    const paisId = getSafeId(
      row?.paisId,
      row?.pais?.id,
      departamento?.paisId,
      initialPaisId
    );

    return {
      paisId,
      deptoId,
      municipioId,
    };
  };

  useEffect(() => {
    if (!open) return;

    let active = true;

    const loadLocationCatalogs = async () => {
      try {
        const [paisesResult, departamentosResult, municipiosResult] =
          await Promise.allSettled([
            axios.get("/v1/pais", {
              ...authHeaders,
              params: { page: 0, size: 1000 },
            }),
            axios.get("/v1/departamento", {
              ...authHeaders,
              params: { page: 0, size: 1000 },
            }),
            axios.get("/v1/municipio", {
              ...authHeaders,
              params: { page: 0, size: 5000 },
            }),
          ]);

        if (!active) return;

        const paises =
          paisesResult.status === "fulfilled"
            ? asArray(paisesResult.value.data).map((pais) => ({
                id: Number(pais.id),
                nombre: pais.nombre ?? pais.name ?? String(pais.id),
              }))
            : [];
        const departamentos =
          departamentosResult.status === "fulfilled"
            ? asArray(departamentosResult.value.data).map((departamento) => ({
                id: Number(departamento.id),
                paisId: Number(departamento.paisId),
                nombre:
                  departamento.nombre ??
                  departamento.name ??
                  String(departamento.id),
              }))
            : [];
        const municipios =
          municipiosResult.status === "fulfilled"
            ? asArray(municipiosResult.value.data).map((municipio) => ({
                id: Number(municipio.id),
                departamentoId: Number(municipio.departamentoId),
                nombre: municipio.nombre ?? municipio.name ?? String(municipio.id),
              }))
            : [];

        const location = resolveInitialLocation({
          row: formMode === "edit" ? selectedRow : null,
          municipios,
          departamentos,
        });

        setPaisesOpts(paises);
        setDepartamentosCatalog(departamentos);
        setMunicipiosCatalog(municipios);
        setDepartamentosOpts(
          location.paisId
            ? departamentos.filter(
                (departamento) =>
                  Number(departamento.paisId) === Number(location.paisId)
              )
            : []
        );
        setFormData({
          id:
            formMode === "edit" && selectedRow
              ? toNum(selectedRow.id)
              : initialData.id,
          paisId: location.paisId,
          deptoId: location.deptoId,
          grupoId:
            formMode === "edit" && selectedRow
              ? getSafeId(selectedRow.grupoId, selectedRow?.grupo?.id)
              : initialData.grupoId,
          tipoSedeId:
            formMode === "edit" && selectedRow
              ? getSafeId(selectedRow.tipoSedeId, selectedRow?.tipoSede?.id)
              : initialData.tipoSedeId,
          nombre:
            formMode === "edit" && selectedRow ? selectedRow.nombre ?? "" : "",
          municipioId: location.municipioId,
          geolocalizacion:
            formMode === "edit" && selectedRow
              ? selectedRow.geolocalizacion ?? ""
              : "",
          coordenadas:
            formMode === "edit" && selectedRow ? selectedRow.coordenadas ?? "" : "",
          area: formMode === "edit" && selectedRow ? selectedRow.area ?? "" : "",
          comuna:
            formMode === "edit" && selectedRow ? selectedRow.comuna ?? "" : "",
          descripcion:
            formMode === "edit" && selectedRow ? selectedRow.descripcion ?? "" : "",
          estadoId:
            formMode === "edit" && selectedRow
              ? toNum(selectedRow.estadoId) || 1
              : 1,
        });
        setErrors({});
      } catch {
        if (!active) return;
        setPaisesOpts([]);
        setDepartamentosCatalog([]);
        setMunicipiosCatalog([]);
        setDepartamentosOpts([]);
        setMunicipiosOpts([]);
      }
    };

    loadLocationCatalogs();

    return () => {
      active = false;
    };
  }, [
    open,
    formMode,
    selectedRow,
    initialPaisId,
    initialDeptoId,
    initialMunicipioId,
  ]);

  useEffect(() => {
    if (!open) return;

    if (!formData.paisId) {
      setDepartamentosOpts([]);
      setMunicipiosOpts([]);
      return;
    }

    const nextDepartamentos = departamentosCatalog.filter(
      (departamento) =>
        Number(departamento.paisId) === Number(formData.paisId)
    );
    setDepartamentosOpts(nextDepartamentos);

    if (!formData.deptoId) {
      setMunicipiosOpts([]);
      return;
    }

    let active = true;

    const loadMunicipiosByDepartment = async () => {
      try {
        const response = await axios.get("/v1/municipio", {
          ...authHeaders,
          params: { departamentoId: Number(formData.deptoId), page: 0, size: 1000 },
        });
        if (!active) return;

        const municipios = asArray(response.data).map((municipio) => ({
          id: Number(municipio.id),
          nombre: municipio.nombre ?? municipio.name ?? String(municipio.id),
        }));
        setMunicipiosOpts(municipios);
        if (
          formData.municipioId &&
          !municipios.some(
            (municipio) => Number(municipio.id) === Number(formData.municipioId)
          )
        ) {
          setFormData((current) => ({ ...current, municipioId: "" }));
        }
      } catch {
        if (!active) return;
        setMunicipiosOpts([]);
      }
    };

    loadMunicipiosByDepartment();

    return () => {
      active = false;
    };
  }, [
    departamentosCatalog,
    formData.deptoId,
    formData.municipioId,
    formData.paisId,
    open,
  ]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    let v = value;

    // Limpiar textos de caracteres especiales peligrosos
    if (["nombre", "descripcion"].includes(name)) {
      v = cleanText(v);
      if (sqliWordsRegex.test(v)) {
        // Por si quieres marcar algún error de seguridad:
        // aquí solo limpiamos, no seteamos error directo
      }
    }

    // Geolocalización: SOLO NÚMEROS (no negativos, sin signos)
    if (name === "geolocalizacion") {
      v = value.replace(/[^0-9]/g, "");
    }

    // Coordenadas: permitir dígitos, punto, coma, espacios y signo -
    if (name === "coordenadas") {
      v = value.replace(/[^0-9.,\s-]/g, "");
    }

    // Área: números y punto, sin signos
    if (name === "area") {
      v = value.replace(/[^0-9.]/g, "");
    }

    // COMUNA como select numérico (1-10)
    if (name === "comuna") {
      v = Number(v);
    }

    if (name === "paisId") {
      setFormData((prev) => ({
        ...prev,
        paisId: toNum(v),
        deptoId: "",
        municipioId: "",
      }));
      return;
    }

    if (name === "deptoId") {
      setFormData((prev) => ({
        ...prev,
        deptoId: toNum(v),
        municipioId: "",
      }));
      return;
    }

    const newVal =
      [
        "estadoId",
        "grupoId",
        "tipoSedeId",
        "municipioId",
        "paisId",
        "deptoId",
      ].includes(name)
        ? toNum(v)
        : v;

    setFormData((prev) => ({ ...prev, [name]: newVal }));
  };

  const parseCoordinates = (value = "") => {
    if (!value.trim()) return null;
    // Separamos por coma o espacio
    const parts = value.split(/[,\s]+/).filter(Boolean);
    if (parts.length !== 2) return null;

    const lat = Number(parts[0].replace(",", "."));
    const lon = Number(parts[1].replace(",", "."));

    if (Number.isNaN(lat) || Number.isNaN(lon)) return null;
    return { lat, lon };
  };

  const validate = () => {
    const e = {};

    const baseErrors = validateCamposBase({
      nombre: formData.nombre,
      descripcion: formData.descripcion,
      estado: formData.estadoId,
    });

    if (baseErrors.nombre) e.nombre = baseErrors.nombre;
    if (baseErrors.descripcion) e.descripcion = baseErrors.descripcion;
    if (baseErrors.estado) e.estadoId = baseErrors.estado;
    if (baseErrors._security) e._security = baseErrors._security;

    // Nombre: obligatorio + NO solo números
    if (!formData.nombre.trim()) {
      e.nombre = t("sede.form.validation.nameRequired");
    } else if (/^\d+$/.test(formData.nombre.trim())) {
      e.nombre = t("sede.form.validation.nameOnlyNumbers");
    }

    // Grupo / Tipo Sede / Municipio obligatorios
    if (!Number(formData.grupoId))
      e.grupoId = t("sede.form.validation.groupRequired");
    if (!Number(formData.tipoSedeId))
      e.tipoSedeId = t("sede.form.validation.typeRequired");
    if (!Number(formData.paisId))
      e.paisId = t("sede.form.validation.countryRequired");
    if (!Number(formData.deptoId))
      e.deptoId = t("sede.form.validation.departmentRequired");
    if (!Number(formData.municipioId))
      e.municipioId = t("sede.form.validation.municipalityRequired");

    const selectedMunicipio = municipiosById.get(Number(formData.municipioId));
    const selectedDepartamento = departamentosById.get(Number(formData.deptoId));

    if (
      Number(formData.municipioId) &&
      (!selectedMunicipio ||
        Number(selectedMunicipio.departamentoId) !== Number(formData.deptoId))
    ) {
      e.municipioId = t("sede.form.validation.municipalityMismatch");
    }

    if (
      Number(formData.deptoId) &&
      (!selectedDepartamento ||
        Number(selectedDepartamento.paisId) !== Number(formData.paisId))
    ) {
      e.deptoId = t("sede.form.validation.departmentMismatch");
    }

    // Área: numérica y no negativa
    if (formData.area !== "") {
      const n = Number(formData.area);
      if (isNaN(n)) e.area = t("sede.form.validation.areaNumeric");
      else if (n < 0) e.area = t("sede.form.validation.areaNonNegative");
    }

    // Geolocalización: solo números (ya limpiada en handleChange)
    if (formData.geolocalizacion && !/^[0-9]+$/.test(formData.geolocalizacion)) {
      e.geolocalizacion = t("sede.form.validation.geolocationNumeric");
    }

    // Coordenadas: formato numérico y rangos de lat/long
    if (formData.coordenadas) {
      const coords = parseCoordinates(formData.coordenadas);
      if (!coords) {
        e.coordenadas = t("sede.form.validation.coordinatesFormat");
      } else {
        const { lat, lon } = coords;
        if (lat < -90 || lat > 90) {
          e.coordenadas = t("sede.form.validation.latitudeRange");
        } else if (lon < -180 || lon > 180) {
          e.coordenadas = t("sede.form.validation.longitudeRange");
        }
      }
    }

    // COMUNA → OBLIGATORIA + SOLO 1–10
    if (!formData.comuna) {
      e.comuna = t("sede.form.validation.communeRequired");
    } else if (formData.comuna < 1 || formData.comuna > 10) {
      e.comuna = t("sede.form.validation.communeRange");
    }

    // Descripción: no solo números
    if (formData.descripcion && /^\d+$/.test(formData.descripcion.trim())) {
      e.descripcion = t("sede.form.validation.descriptionOnlyNumbers");
    }

    setErrors(e);
    return Object.keys(e).length === 0;
  };

  const handleSubmit = async () => {
    if (!validate()) return;

    const payload = {
      grupoId: Number(formData.grupoId),
      tipoSedeId: Number(formData.tipoSedeId),
      nombre: formData.nombre.trim(),
      municipioId: Number(formData.municipioId),
      geolocalizacion: formData.geolocalizacion?.trim() || null,
      coordenadas: formData.coordenadas?.trim() || null,
      area: formData.area === "" ? null : Number(formData.area),
      comuna: Number(formData.comuna),
      descripcion: formData.descripcion?.trim() || null,
      estadoId: Number(formData.estadoId),
    };

    try {
      if (formMode === "edit" && formData.id) {
        await axios.put(
          `/v1/sede/${formData.id}`,
          { id: Number(formData.id), ...payload },
          authHeaders
        );
        setMessage({
          open: true,
          severity: "success",
          text: t("sede.messages.updateSuccess"),
        });
      } else {
        await axios.post("/v1/sede", payload, authHeaders);
        setMessage({
          open: true,
          severity: "success",
          text: t("sede.messages.createSuccess"),
        });
      }
      setOpen(false);
      reloadData();
    } catch (err) {
      const api = err.response?.data || {};
      const txt =
        api.message ||
        api.error ||
        (err.response?.status === 409
          ? t("sede.messages.saveConflict")
          : t("sede.messages.saveError"));
      setMessage({ open: true, severity: "error", text: txt });
    }
  };

  return (
    <Dialog open={open} onClose={() => setOpen(false)} maxWidth="sm" fullWidth>
      <DialogTitle>
        {formMode === "edit"
          ? t("sede.form.editTitle")
          : t("sede.form.createTitle")}
      </DialogTitle>

      <DialogContent>
        {/* Nombre */}
        <TextField
          fullWidth
          margin="normal"
          label={t("sede.form.fields.name")}
          name="nombre"
          value={formData.nombre}
          onChange={handleChange}
          error={!!errors.nombre}
          helperText={errors.nombre}
        />

        <FormControl fullWidth margin="normal" error={!!errors.paisId}>
          <InputLabel>{t("sede.form.fields.country")}</InputLabel>
          <Select
            name="paisId"
            value={formData.paisId}
            onChange={handleChange}
            label={t("sede.form.fields.country")}
          >
            {paisesOpts.map((pais) => (
              <MenuItem key={pais.id} value={pais.id}>
                {pais.nombre}
              </MenuItem>
            ))}
          </Select>
          <FormHelperText>{errors.paisId}</FormHelperText>
        </FormControl>

        <FormControl
          fullWidth
          margin="normal"
          error={!!errors.deptoId}
          disabled={!formData.paisId}
        >
          <InputLabel>{t("sede.form.fields.department")}</InputLabel>
          <Select
            name="deptoId"
            value={formData.deptoId}
            onChange={handleChange}
            label={t("sede.form.fields.department")}
          >
            {departamentosOpts.map((departamento) => (
              <MenuItem key={departamento.id} value={departamento.id}>
                {departamento.nombre}
              </MenuItem>
            ))}
          </Select>
          <FormHelperText>{errors.deptoId}</FormHelperText>
        </FormControl>

        {/* Grupo */}
        <FormControl fullWidth margin="normal" error={!!errors.grupoId}>
          <InputLabel>{t("sede.form.fields.group")}</InputLabel>
          <Select
            name="grupoId"
            value={formData.grupoId}
            onChange={handleChange}
            label={t("sede.form.fields.group")}
          >
            {grupos.map((g) => (
              <MenuItem key={g.id} value={g.id}>
                {g.nombre}
              </MenuItem>
            ))}
          </Select>
          <FormHelperText>{errors.grupoId}</FormHelperText>
        </FormControl>

        {/* Tipo de sede */}
        <FormControl fullWidth margin="normal" error={!!errors.tipoSedeId}>
          <InputLabel>{t("sede.form.fields.type")}</InputLabel>
          <Select
            name="tipoSedeId"
            value={formData.tipoSedeId}
            onChange={handleChange}
            label={t("sede.form.fields.type")}
          >
            {tiposSede.map((t) => (
              <MenuItem key={t.id} value={t.id}>
                {t.nombre}
              </MenuItem>
            ))}
          </Select>
          <FormHelperText>{errors.tipoSedeId}</FormHelperText>
        </FormControl>

        {/* Municipio */}
        <FormControl
          fullWidth
          margin="normal"
          error={!!errors.municipioId}
          disabled={!formData.deptoId}
        >
          <InputLabel>{t("sede.form.fields.municipality")}</InputLabel>
          <Select
            name="municipioId"
            value={formData.municipioId}
            onChange={handleChange}
            label={t("sede.form.fields.municipality")}
          >
            {municipiosOpts.map((m) => (
              <MenuItem key={m.id} value={m.id}>
                {m.nombre}
              </MenuItem>
            ))}
          </Select>
          <FormHelperText>{errors.municipioId}</FormHelperText>
        </FormControl>

        {/* Geolocalización */}
        <TextField
          fullWidth
          margin="normal"
          label={t("sede.form.fields.geolocation")}
          name="geolocalizacion"
          value={formData.geolocalizacion}
          onChange={handleChange}
          error={!!errors.geolocalizacion}
          helperText={errors.geolocalizacion}
        />

        {/* Coordenadas */}
        <TextField
          fullWidth
          margin="normal"
          label={t("sede.form.fields.coordinates")}
          name="coordenadas"
          value={formData.coordenadas}
          onChange={handleChange}
          error={!!errors.coordenadas}
          helperText={
            errors.coordenadas ||
            t("sede.form.helpers.coordinatesExample")
          }
        />

        {/* Área */}
        <TextField
          fullWidth
          margin="normal"
          label={t("sede.form.fields.area")}
          name="area"
          value={formData.area}
          onChange={handleChange}
          error={!!errors.area}
          helperText={errors.area}
        />

        {/* Comuna */}
        <FormControl fullWidth margin="normal" error={!!errors.comuna}>
          <InputLabel>{t("sede.form.fields.commune")}</InputLabel>
          <Select
            name="comuna"
            value={formData.comuna}
            onChange={handleChange}
            label={t("sede.form.fields.commune")}
          >
            {[1, 2, 3, 4, 5, 6, 7, 8, 9, 10].map((c) => (
              <MenuItem key={c} value={c}>
                {c}
              </MenuItem>
            ))}
          </Select>
          <FormHelperText>{errors.comuna}</FormHelperText>
        </FormControl>

        {/* Descripción */}
        <TextField
          fullWidth
          margin="normal"
          label={t("sede.form.fields.description")}
          name="descripcion"
          value={formData.descripcion}
          onChange={handleChange}
          error={!!errors.descripcion}
          helperText={errors.descripcion}
          multiline
          minRows={2}
        />

        {/* Estado */}
        <FormControl fullWidth margin="normal" error={!!errors.estadoId}>
          <InputLabel>{t("sede.form.fields.status")}</InputLabel>
          <Select
            name="estadoId"
            value={formData.estadoId}
            onChange={handleChange}
            label={t("sede.form.fields.status")}
          >
            <MenuItem value={1}>{t("common.labels.active")}</MenuItem>
            <MenuItem value={2}>{t("common.labels.inactive")}</MenuItem>
          </Select>
          <FormHelperText>{errors.estadoId}</FormHelperText>
        </FormControl>
      </DialogContent>

      <DialogActions>
        <Button onClick={() => setOpen(false)}>
          {t("common.actions.cancel")}
        </Button>
        <Button variant="contained" onClick={handleSubmit}>
          {t("common.actions.save")}
        </Button>
      </DialogActions>
    </Dialog>
  );
}

FormSede.propTypes = {
  open: PropTypes.bool,
  setOpen: PropTypes.func,
  formMode: PropTypes.string,
  selectedRow: PropTypes.object,
  initialPaisId: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  initialDeptoId: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  initialMunicipioId: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  grupos: PropTypes.array,
  tiposSede: PropTypes.array,
  reloadData: PropTypes.func,
  setMessage: PropTypes.func,
  authHeaders: PropTypes.object,
};
