/*=============================================================================
 Nombre del archivo : Sede.jsx
 Descripcion        : Modulo principal para la gestion de sedes.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |   Fecha    | Versión |      Autor           | Descripción del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2026-09-21 | 0.4.0   | Cesar Medina         | Estandariza la vista,       |
 |            |         |                      | agrega cascada geografica   |
 |            |         |                      | y filtros por pais,         |
 |            |         |                      | departamento y municipio.   |
 +------------+---------+----------------------+-----------------------------+
=============================================================================*/

import React, { useEffect, useMemo, useState } from "react";
import { Box } from "@mui/material";
import { alpha, useTheme } from "@mui/material/styles";
import { useTranslation } from "react-i18next";
import FilterListIcon from "@mui/icons-material/FilterList";

import axios from "../axiosConfig";
import MessageSnackBar from "../MessageSnackBar";
import CrudFilterModal from "../common/CrudFilterModal";
import { makeLoaders, unwrap as unwrapPage } from "../common/filtersLoaders";
import GridActionBar from "../common/GridActionBar";
import SectionHeader from "../common/SectionHeader";
import FormSede from "./FormSede";
import GridSede from "./GridSede";

const EMPTY_FILTERS = {
  paisId: "",
  deptoId: "",
  municipioId: "",
};

const asItemsArray = (data) => (Array.isArray(data) ? data : []);

const getDialogUi = (theme) => {
  const isDark = theme.palette.mode === "dark";
  const darkGreen = isDark ? "#E7F6F7" : "#173f39";
  const green = isDark ? "#2b6b60" : "#173f39";
  const surface = isDark ? "#10211f" : theme.palette.common.white;
  const sectionSurface = isDark ? "#142b28" : theme.palette.common.white;
  const summarySurface = isDark ? alpha("#2b6b60", 0.28) : "#dfeae6";
  const subtleBorder = alpha(green, 0.14);
  const softShadow = `0 10px 30px ${alpha(darkGreen, isDark ? 0.18 : 0.08)}`;
  const sectionShadow = `0 4px 14px ${alpha(darkGreen, isDark ? 0.14 : 0.05)}`;

  return {
    darkGreen,
    paperSx: {
      borderRadius: 3,
      overflow: "hidden",
      backgroundColor: surface,
      boxShadow: softShadow,
    },
    titleSx: {
      px: { xs: 2.25, sm: 3 },
      py: 2.15,
      backgroundColor: surface,
      borderTop: `3px solid ${darkGreen}`,
      borderBottom: `1px solid ${subtleBorder}`,
      fontSize: "1.12rem",
      fontWeight: 700,
      color: darkGreen,
    },
    contentSx: {
      px: { xs: 2.25, sm: 3 },
      pt: 6,
      pb: 2.5,
      backgroundColor: surface,
    },
    actionsSx: {
      px: { xs: 2.25, sm: 3 },
      py: 2.25,
      backgroundColor: surface,
      borderTop: `1px solid ${subtleBorder}`,
    },
    summaryCardSx: {
      borderRadius: 2,
      border: `1px solid ${subtleBorder}`,
      boxShadow: sectionShadow,
      backgroundColor: summarySurface,
    },
    formCardSx: {
      borderRadius: 2,
      border: `1px solid ${subtleBorder}`,
      boxShadow: sectionShadow,
      backgroundColor: sectionSurface,
    },
    bodySx: {
      mt: 0.25,
      width: "100%",
    },
    closeButtonSx: {
      color: darkGreen,
    },
    secondaryButtonSx: {
      borderRadius: 2,
      px: 2.5,
      textTransform: "none",
      fontWeight: 700,
      color: darkGreen,
      border: `1px solid ${subtleBorder}`,
    },
    primaryButtonSx: {
      borderRadius: 2,
      px: 2.5,
      textTransform: "none",
      fontWeight: 700,
      backgroundColor: isDark ? "#173f39" : "#1d4d45",
      boxShadow: "none",
      "&:hover": {
        backgroundColor: isDark ? "#21534b" : "#173f39",
        boxShadow: "none",
      },
    },
  };
};

export default function Sede() {
  const { t } = useTranslation();
  const theme = useTheme();
  const dialogUi = getDialogUi(theme);
  const [filters, setFilters] = useState(EMPTY_FILTERS);
  const [openFilters, setOpenFilters] = useState(false);
  const [gruposItems, setGruposItems] = useState([]);
  const [tiposSedeItems, setTiposSedeItems] = useState([]);
  const [paisesCatalog, setPaisesCatalog] = useState([]);
  const [departamentosCatalog, setDepartamentosCatalog] = useState([]);
  const [municipiosCatalog, setMunicipiosCatalog] = useState([]);
  const [sedes, setSedes] = useState([]);
  const [loading, setLoading] = useState(false);
  const [selectedRow, setSelectedRow] = useState(null);
  const [formOpen, setFormOpen] = useState(false);
  const [formMode, setFormMode] = useState("create");
  const [message, setMessage] = useState({
    open: false,
    severity: "success",
    text: "",
  });

  const token = localStorage.getItem("token");
  const headers = { headers: { Authorization: `Bearer ${token}` } };
  const { getPaises, getDepartamentos, getMunicipios } = makeLoaders(headers);

  const fieldsSede = useMemo(
    () => [
      {
        name: "paisId",
        labelKey: "sede.filters.country",
        getOptions: getPaises,
        clearChildren: ["deptoId", "municipioId"],
      },
      {
        name: "deptoId",
        labelKey: "sede.filters.department",
        getOptions: getDepartamentos,
        dependsOn: ["paisId"],
        disabled: (values) => !values.paisId,
        clearChildren: ["municipioId"],
      },
      {
        name: "municipioId",
        labelKey: "sede.filters.municipality",
        getOptions: getMunicipios,
        dependsOn: ["deptoId"],
        disabled: (values) => !values.deptoId,
      },
    ],
    [getDepartamentos, getMunicipios, getPaises]
  );

  const gruposForm = useMemo(
    () => gruposItems.map((grupo) => ({ id: Number(grupo.id), nombre: grupo.name })),
    [gruposItems]
  );
  const tiposSedeForm = useMemo(
    () => tiposSedeItems.map((tipo) => ({ id: Number(tipo.id), nombre: tipo.name })),
    [tiposSedeItems]
  );

  const paisesById = useMemo(
    () => new Map(paisesCatalog.map((pais) => [Number(pais.id), pais])),
    [paisesCatalog]
  );
  const departamentosById = useMemo(
    () => new Map(departamentosCatalog.map((depto) => [Number(depto.id), depto])),
    [departamentosCatalog]
  );
  const municipiosById = useMemo(
    () => new Map(municipiosCatalog.map((municipio) => [Number(municipio.id), municipio])),
    [municipiosCatalog]
  );
  const gruposById = useMemo(
    () => new Map(gruposItems.map((grupo) => [Number(grupo.id), grupo])),
    [gruposItems]
  );
  const tiposSedeById = useMemo(
    () => new Map(tiposSedeItems.map((tipo) => [Number(tipo.id), tipo])),
    [tiposSedeItems]
  );

  const normalizedSedes = useMemo(
    () =>
      sedes.map((sede) => {
        const municipioId = Number(
          sede.municipioId ?? sede.municipio?.id ?? sede.municipio_id ?? 0
        );
        const grupoId = Number(
          sede.grupoId ?? sede.grupo?.id ?? sede.grupo_id ?? 0
        );
        const tipoSedeId = Number(
          sede.tipoSedeId ?? sede.tipoSede?.id ?? sede.tipo_sede_id ?? 0
        );
        const municipio = municipiosById.get(municipioId);
        const deptoId = Number(
          sede.departamentoId ??
            sede.departamento?.id ??
            municipio?.departamentoId ??
            municipio?.departamento?.id ??
            0
        );
        const departamento = departamentosById.get(deptoId);
        const paisId = Number(
          sede.paisId ??
            sede.pais?.id ??
            departamento?.paisId ??
            departamento?.pais?.id ??
            0
        );
        const pais = paisesById.get(paisId);
        const grupo = gruposById.get(grupoId);
        const tipoSede = tiposSedeById.get(tipoSedeId);

        return {
          ...sede,
          municipioId: municipioId || "",
          deptoId: deptoId || "",
          paisId: paisId || "",
          grupoId: grupoId || "",
          tipoSedeId: tipoSedeId || "",
          paisNombre: sede.pais?.nombre ?? pais?.nombre ?? "",
          departamentoNombre:
            sede.departamento?.nombre ?? departamento?.nombre ?? "",
          municipioNombre:
            sede.municipio?.nombre ??
            sede.municipio?.name ??
            municipio?.nombre ??
            municipio?.name ??
            "",
          grupoNombre:
            sede.grupo?.nombre ?? sede.grupo?.name ?? grupo?.name ?? "",
          tipoSedeNombre:
            sede.tipoSede?.nombre ??
            sede.tipoSede?.name ??
            tipoSede?.name ??
            "",
        };
      }),
    [
      departamentosById,
      gruposById,
      municipiosById,
      paisesById,
      sedes,
      tiposSedeById,
    ]
  );

  const filteredSedes = useMemo(
    () =>
      normalizedSedes.filter((sede) => {
        if (filters.paisId && Number(sede.paisId) !== Number(filters.paisId)) {
          return false;
        }
        if (
          filters.deptoId &&
          Number(sede.deptoId) !== Number(filters.deptoId)
        ) {
          return false;
        }
        if (
          filters.municipioId &&
          Number(sede.municipioId) !== Number(filters.municipioId)
        ) {
          return false;
        }
        return true;
      }),
    [filters.deptoId, filters.municipioId, filters.paisId, normalizedSedes]
  );

  const hasActiveFilters = Boolean(
    filters.paisId || filters.deptoId || filters.municipioId
  );

  const loadCatalogs = async () => {
    try {
      const [
        paisesResponse,
        departamentosResponse,
        municipiosResponse,
        gruposResponse,
        tiposSedeResponse,
      ] = await Promise.all([
        axios.get("/v1/pais", {
          ...headers,
          params: { page: 0, size: 1000 },
        }),
        axios.get("/v1/departamento", {
          ...headers,
          params: { page: 0, size: 1000 },
        }),
        axios.get("/v1/municipio", {
          ...headers,
          params: { page: 0, size: 5000 },
        }),
        axios.get("/v1/items/grupo/0", headers),
        axios.get("/v1/items/tipo_sede/0", headers),
      ]);

      setPaisesCatalog(unwrapPage(paisesResponse.data));
      setDepartamentosCatalog(unwrapPage(departamentosResponse.data));
      setMunicipiosCatalog(unwrapPage(municipiosResponse.data));
      setGruposItems(asItemsArray(gruposResponse.data));
      setTiposSedeItems(asItemsArray(tiposSedeResponse.data));
    } catch {
      setPaisesCatalog([]);
      setDepartamentosCatalog([]);
      setMunicipiosCatalog([]);
      setGruposItems([]);
      setTiposSedeItems([]);
    }
  };

  const reloadData = async () => {
    setLoading(true);
    try {
      const response = await axios.get("/v1/sede", {
        ...headers,
        params: { page: 0, size: 2000 },
      });
      setSedes(unwrapPage(response.data));
    } catch {
      setMessage({
        open: true,
        severity: "error",
        text: t("sede.messages.loadError"),
      });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadCatalogs();
    reloadData();
  }, []);

  useEffect(() => {
    if (
      selectedRow &&
      !filteredSedes.some((row) => Number(row.id) === Number(selectedRow.id))
    ) {
      setSelectedRow(null);
    }
  }, [filteredSedes, selectedRow]);

  const handleDelete = async () => {
    if (!selectedRow) return;
    if (!window.confirm(t("sede.messages.confirmDelete", { name: selectedRow.nombre }))) {
      return;
    }

    try {
      await axios.delete(`/v1/sede/${selectedRow.id}`, headers);
      setMessage({
        open: true,
        severity: "success",
        text: t("sede.messages.deleteSuccess"),
      });
      setSelectedRow(null);
      reloadData();
    } catch {
      setMessage({
        open: true,
        severity: "error",
        text: t("sede.messages.deleteError"),
      });
    }
  };

  const handleFiltersChange = ({ name, value }) => {
    setFilters((current) => ({ ...current, [name]: value }));
  };

  const handleFiltersClear = () => {
    setFilters(EMPTY_FILTERS);
    setOpenFilters(false);
  };

  const handleFiltersApply = () => {
    setOpenFilters(false);
  };

  return (
    <Box sx={{ p: 2 }}>
      <SectionHeader titleKey="sede.title" />

      <GridActionBar
        onAdd={() => {
          setFormMode("create");
          setSelectedRow(null);
          setFormOpen(true);
        }}
        onUpdate={() => {
          setFormMode("edit");
          setFormOpen(true);
        }}
        onDelete={handleDelete}
        canUpdate={Boolean(selectedRow)}
        canDelete={Boolean(selectedRow)}
        onFilters={() => setOpenFilters(true)}
        onClearFilters={handleFiltersClear}
        hasActiveFilters={hasActiveFilters}
      />

      <GridSede
        sedes={filteredSedes}
        selectedRow={selectedRow}
        setSelectedRow={setSelectedRow}
        loading={loading}
      />

      <FormSede
        open={formOpen}
        setOpen={setFormOpen}
        formMode={formMode}
        selectedRow={selectedRow}
        reloadData={reloadData}
        setMessage={setMessage}
        initialPaisId={filters.paisId || ""}
        initialDeptoId={filters.deptoId || ""}
        initialMunicipioId={filters.municipioId || ""}
        grupos={gruposForm}
        tiposSede={tiposSedeForm}
        authHeaders={headers}
      />

      <MessageSnackBar message={message} setMessage={setMessage} />

      <CrudFilterModal
        open={openFilters}
        onClose={() => setOpenFilters(false)}
        titleKey="sede.filters.title"
        titleIcon={
          <Box
            sx={{
              width: 40,
              height: 40,
              borderRadius: 1.5,
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              color: dialogUi.darkGreen,
              backgroundColor:
                theme.palette.mode === "dark" ? alpha("#2b6b60", 0.24) : "#dfeae6",
            }}
          >
            <FilterListIcon fontSize="small" />
          </Box>
        }
        fields={fieldsSede}
        values={filters}
        onChange={handleFiltersChange}
        onClear={handleFiltersClear}
        onApply={handleFiltersApply}
        paperSx={dialogUi.paperSx}
        titleSx={dialogUi.titleSx}
        contentSx={dialogUi.contentSx}
        summaryCardSx={dialogUi.summaryCardSx}
        formCardSx={dialogUi.formCardSx}
        bodySx={dialogUi.bodySx}
        actionsSx={dialogUi.actionsSx}
        primaryButtonSx={dialogUi.primaryButtonSx}
        secondaryButtonSx={dialogUi.secondaryButtonSx}
        closeButtonSx={dialogUi.closeButtonSx}
      />
    </Box>
  );
}
