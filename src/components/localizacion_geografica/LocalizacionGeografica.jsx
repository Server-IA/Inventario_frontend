/*=============================================================================
 Nombre del archivo : LocalizacionGeografica.jsx
 Descripcion        : Módulo principal unificado para Gestión de Localización Geográfica.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |   Fecha    | Versión |      Autor           | Descripción del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2026-05-23 | 1.0.0   | Jeisson Sanchez      | Creación del archivo.       |
 +------------+---------+----------------------+-----------------------------+
 | 2026-06-06 | 0.4.0   | Jeisson Sanchez      | Ajuste i18n y estilos.      |
 +------------+---------+----------------------+-----------------------------+
=============================================================================*/

import React, { useMemo, useState } from "react";
import { Box, Button, MenuItem, Select, Tooltip } from "@mui/material";
import { Add, Check, Close, Edit } from "@mui/icons-material";
import { alpha, useTheme } from "@mui/material/styles";
import { useTranslation } from "react-i18next";

import AppDataGrid from "../common/AppDataGrid.jsx";
import GridActionBar from "../common/GridActionBar.jsx";
import SectionHeader from "../common/SectionHeader.jsx";
import ConfirmInactivateModal from "./ConfirmInactivateModal";
import DepartamentoModal from "./DepartamentoModal";
import FiltroModal from "./FiltroModal";
import MunicipioModal from "./MunicipioModal";
import MunicipiosListModal from "./MunicipiosListModal";
import PaisModal from "./PaisModal";

const ACTIVE_STATUS = "Activo";
const INACTIVE_STATUS = "Inactivo";
const ALL_STATUS = "Todos";

const initialPaises = [
  { id: 1, nombre: "Colombia", codigo: "170", acronimo: "COL", estado: ACTIVE_STATUS },
  { id: 2, nombre: "Argentina", codigo: "032", acronimo: "ARG", estado: ACTIVE_STATUS },
];

const initialDepartamentos = [
  { id: 1, paisId: 1, nombre: "Huila", codigo: "41", acronimo: "HUI", estado: ACTIVE_STATUS },
  { id: 2, paisId: 1, nombre: "Antioquia", codigo: "05", acronimo: "ANT", estado: ACTIVE_STATUS },
];

const initialMunicipios = [
  { id: 1, departamentoId: 1, nombre: "Neiva", codigo: "41001", acronimo: "NEI", estado: ACTIVE_STATUS },
  { id: 2, departamentoId: 1, nombre: "Pitalito", codigo: "41551", acronimo: "PIT", estado: ACTIVE_STATUS },
];

export default function LocalizacionGeografica() {
  const theme = useTheme();
  const { t } = useTranslation();
  const isDark = theme.palette.mode === "dark";
  const [paises, setPaises] = useState(initialPaises);
  const [departamentos, setDepartamentos] = useState(initialDepartamentos);
  const [municipios, setMunicipios] = useState(initialMunicipios);
  const [selectedPaisId, setSelectedPaisId] = useState(paises[0]?.id || "");
  const [selectedDeptoId, setSelectedDeptoId] = useState(null);
  const [deptoFilters, setDeptoFilters] = useState(null);
  const [munFilters, setMunFilters] = useState(null);
  const [modalState, setModalState] = useState({
    pais: false,
    depto: false,
    municipioList: false,
    municipioForm: false,
    filtroDepto: false,
    filtroMun: false,
    confirm: false,
  });
  const [actionContext, setActionContext] = useState({
    type: null,
    item: null,
    isActivating: false,
  });

  const selectedPais = paises.find((pais) => pais.id === selectedPaisId);
  const selectedDepto = departamentos.find((depto) => depto.id === selectedDeptoId);

  const deptosByPais = useMemo(() => {
    let list = departamentos.filter((depto) => depto.paisId === selectedPaisId);
    if (deptoFilters) {
      if (deptoFilters.nombre) {
        list = list.filter((depto) => depto.nombre.toLowerCase().includes(deptoFilters.nombre.toLowerCase()));
      }
      if (deptoFilters.codigo) list = list.filter((depto) => depto.codigo.includes(deptoFilters.codigo));
      if (deptoFilters.acronimo) {
        list = list.filter((depto) => depto.acronimo.toLowerCase().includes(deptoFilters.acronimo.toLowerCase()));
      }
      if (deptoFilters.estado !== ALL_STATUS) list = list.filter((depto) => depto.estado === deptoFilters.estado);
    }
    return list;
  }, [departamentos, selectedPaisId, deptoFilters]);

  const municipiosByDepto = useMemo(() => {
    if (!selectedDeptoId) return [];
    let list = municipios.filter((municipio) => municipio.departamentoId === selectedDeptoId);
    if (munFilters) {
      if (munFilters.nombre) {
        list = list.filter((municipio) => municipio.nombre.toLowerCase().includes(munFilters.nombre.toLowerCase()));
      }
      if (munFilters.codigo) list = list.filter((municipio) => municipio.codigo.includes(munFilters.codigo));
      if (munFilters.acronimo) {
        list = list.filter((municipio) => municipio.acronimo.toLowerCase().includes(munFilters.acronimo.toLowerCase()));
      }
      if (munFilters.estado !== ALL_STATUS) list = list.filter((municipio) => municipio.estado === munFilters.estado);
    }
    return list;
  }, [municipios, selectedDeptoId, munFilters]);

  const openModal = (name, context = {}) => {
    setActionContext((prev) => ({ ...prev, ...context }));
    setModalState((prev) => ({ ...prev, [name]: true }));
  };

  const closeModal = (name) => {
    setModalState((prev) => ({ ...prev, [name]: false }));
  };

  const handleSavePais = (data) => {
    if (actionContext.item) {
      setPaises((prev) => prev.map((pais) => (pais.id === actionContext.item.id ? { ...pais, ...data } : pais)));
      return;
    }

    const newId = paises.length ? Math.max(...paises.map((pais) => pais.id)) + 1 : 1;
    setPaises((prev) => [...prev, { id: newId, ...data, estado: ACTIVE_STATUS }]);
    setSelectedPaisId(newId);
  };

  const handleSaveDepto = (data) => {
    if (actionContext.item) {
      setDepartamentos((prev) => prev.map((depto) => (depto.id === actionContext.item.id ? { ...depto, ...data } : depto)));
      return;
    }

    const newId = departamentos.length ? Math.max(...departamentos.map((depto) => depto.id)) + 1 : 1;
    setDepartamentos((prev) => [...prev, { id: newId, paisId: selectedPaisId, ...data, estado: ACTIVE_STATUS }]);
  };

  const handleSaveMunicipio = (data) => {
    if (actionContext.item) {
      setMunicipios((prev) => prev.map((municipio) => (municipio.id === actionContext.item.id ? { ...municipio, ...data } : municipio)));
      return;
    }

    const newId = municipios.length ? Math.max(...municipios.map((municipio) => municipio.id)) + 1 : 1;
    setMunicipios((prev) => [...prev, { id: newId, departamentoId: selectedDeptoId, ...data, estado: ACTIVE_STATUS }]);
  };

  const handleConfirmAction = () => {
    const { type, item, isActivating } = actionContext;
    const nextStatus = isActivating ? ACTIVE_STATUS : INACTIVE_STATUS;

    if (type === "pais") {
      setPaises((prev) => prev.map((pais) => (pais.id === item.id ? { ...pais, estado: nextStatus } : pais)));
      if (!isActivating) {
        const deptoIds = departamentos.filter((depto) => depto.paisId === item.id).map((depto) => depto.id);
        setDepartamentos((prev) => prev.map((depto) => (depto.paisId === item.id ? { ...depto, estado: INACTIVE_STATUS } : depto)));
        setMunicipios((prev) => prev.map((municipio) => (deptoIds.includes(municipio.departamentoId) ? { ...municipio, estado: INACTIVE_STATUS } : municipio)));
      }
    } else if (type === "depto") {
      setDepartamentos((prev) => prev.map((depto) => (depto.id === item.id ? { ...depto, estado: nextStatus } : depto)));
      if (!isActivating) {
        setMunicipios((prev) => prev.map((municipio) => (municipio.departamentoId === item.id ? { ...municipio, estado: INACTIVE_STATUS } : municipio)));
      }
    } else if (type === "municipio") {
      setMunicipios((prev) => prev.map((municipio) => (municipio.id === item.id ? { ...municipio, estado: nextStatus } : municipio)));
    }

    closeModal("confirm");
  };

  const getImpactMessage = () => {
    const { type, item } = actionContext;
    if (!item) return [];
    if (type === "pais") {
      const deptoCount = departamentos.filter((depto) => depto.paisId === item.id).length;
      const deptoIds = departamentos.filter((depto) => depto.paisId === item.id).map((depto) => depto.id);
      const munCount = municipios.filter((municipio) => deptoIds.includes(municipio.departamentoId)).length;
      return [
        t("localizacionGeografica.confirm.departmentsAffected", { count: deptoCount }),
        t("localizacionGeografica.confirm.municipalitiesAffected", { count: munCount }),
      ];
    }
    if (type === "depto") {
      const munCount = municipios.filter((municipio) => municipio.departamentoId === item.id).length;
      return [t("localizacionGeografica.confirm.municipalitiesAffected", { count: munCount })];
    }
    return [];
  };

  const countryButtonBaseSx = {
    minWidth: 44,
    width: 44,
    height: 40,
    p: 0,
    borderRadius: 2,
    boxShadow: isDark
      ? "0 8px 20px rgba(0,0,0,0.24), 0 2px 8px rgba(0,0,0,0.16)"
      : "0 6px 18px rgba(23,63,57,0.08), 0 2px 6px rgba(0,0,0,0.06)",
  };
  const countryAddSx = {
    ...countryButtonBaseSx,
    bgcolor: isDark ? "#173f39" : "#1d4d45",
    color: "#fff",
    "&:hover": { bgcolor: isDark ? "#21534b" : "#173f39" },
  };
  const countryEditSx = {
    ...countryButtonBaseSx,
    bgcolor: isDark ? alpha("#2b6b60", 0.28) : "#d9e9e3",
    color: isDark ? "#e7f6f7" : "#173f39",
    "&:hover": { bgcolor: isDark ? alpha("#2b6b60", 0.4) : "#cfe1da" },
    "&.Mui-disabled": {
      color: isDark ? alpha("#e7f6f7", 0.38) : "#7f9790",
      bgcolor: isDark ? alpha("#2b6b60", 0.12) : "#edf3f0",
    },
  };
  const countryDangerSx = {
    ...countryButtonBaseSx,
    bgcolor: isDark ? alpha("#ffb4ab", 0.12) : "#fff0f0",
    color: isDark ? "#ffb4ab" : "#d32f2f",
    "&:hover": { bgcolor: isDark ? alpha("#ffb4ab", 0.18) : "#ffdede" },
    "&.Mui-disabled": {
      color: isDark ? alpha("#ffb4ab", 0.4) : "#f19999",
      bgcolor: isDark ? alpha("#ffb4ab", 0.08) : "#fff6f6",
    },
  };

  const deptoColumns = useMemo(() => [
    { field: "codigo", headerKey: "localizacionGeografica.columns.code", flex: 1, minWidth: 100 },
    { field: "nombre", headerKey: "localizacionGeografica.columns.name", flex: 2, minWidth: 200 },
    { field: "acronimo", headerKey: "localizacionGeografica.columns.acronym", flex: 1, minWidth: 100 },
    { field: "estado", headerKey: "localizacionGeografica.columns.status", type: "status", flex: 1, minWidth: 100 },
  ], []);

  return (
    <Box sx={{ width: "100%", p: 3, color: "text.primary", minHeight: "80vh" }}>
      <SectionHeader titleKey="localizacionGeografica.title" />

      <Box sx={{ display: "flex", justifyContent: "center", alignItems: "center", gap: 2, mb: 4 }}>
        <Select
          value={selectedPaisId}
          onChange={(event) => {
            setSelectedPaisId(event.target.value);
            setSelectedDeptoId(null);
            setDeptoFilters(null);
          }}
          size="small"
          sx={{ minWidth: 250 }}
        >
          {paises.map((pais) => (
            <MenuItem key={pais.id} value={pais.id}>
              {pais.nombre} ({pais.acronimo}) {pais.estado === INACTIVE_STATUS ? t("localizacionGeografica.labels.inactiveSuffix") : ""}
            </MenuItem>
          ))}
        </Select>
        <Tooltip title={t("common.actions.add")}>
          <Button sx={countryAddSx} onClick={() => openModal("pais", { type: "pais", item: null })}>
            <Add />
          </Button>
        </Tooltip>
        <Tooltip title={t("common.actions.update")}>
          <Button disabled={!selectedPais} sx={countryEditSx} onClick={() => openModal("pais", { type: "pais", item: selectedPais })}>
            <Edit />
          </Button>
        </Tooltip>
        <Tooltip title={selectedPais?.estado === INACTIVE_STATUS ? t("localizacionGeografica.actions.activate") : t("localizacionGeografica.actions.inactivate")}>
          <Button
            disabled={!selectedPais}
            sx={countryDangerSx}
            onClick={() => openModal("confirm", { type: "pais", item: selectedPais, isActivating: selectedPais?.estado === INACTIVE_STATUS })}
          >
            {selectedPais?.estado === INACTIVE_STATUS ? <Check /> : <Close />}
          </Button>
        </Tooltip>
      </Box>

      <GridActionBar
        onAdd={() => openModal("depto", { type: "depto", item: null })}
        onUpdate={() => openModal("depto", { type: "depto", item: selectedDepto })}
        onDelete={() => openModal("confirm", { type: "depto", item: selectedDepto, isActivating: selectedDepto?.estado === INACTIVE_STATUS })}
        canAdd={Boolean(selectedPais && selectedPais.estado !== INACTIVE_STATUS)}
        canUpdate={Boolean(selectedDepto)}
        canDelete={Boolean(selectedDepto)}
        onFilters={() => setModalState((prev) => ({ ...prev, filtroDepto: true }))}
        labels={{
          delete: selectedDepto?.estado === INACTIVE_STATUS
            ? t("localizacionGeografica.actions.activate")
            : t("localizacionGeografica.actions.inactivate"),
        }}
        extraActions={
          <Button disabled={!selectedDepto} onClick={() => setModalState((prev) => ({ ...prev, municipioList: true }))}>
            {t("localizacionGeografica.actions.viewMunicipalities")}
          </Button>
        }
      />

      <AppDataGrid
        rows={deptosByPais}
        columns={deptoColumns}
        selectedRow={selectedDepto}
        setSelectedRow={(row) => setSelectedDeptoId(row?.id || null)}
        containerSx={{ borderRadius: 4 }}
      />

      <PaisModal
        open={modalState.pais}
        onClose={() => closeModal("pais")}
        onSave={handleSavePais}
        paisToEdit={actionContext.item}
      />
      <DepartamentoModal
        open={modalState.depto}
        onClose={() => closeModal("depto")}
        onSave={handleSaveDepto}
        deptoToEdit={actionContext.item}
        paisContext={selectedPais}
      />
      <ConfirmInactivateModal
        open={modalState.confirm}
        onClose={() => closeModal("confirm")}
        onConfirm={handleConfirmAction}
        title={t("localizacionGeografica.confirm.title", { type: actionContext.type })}
        itemName={actionContext.item?.nombre}
        isActivating={actionContext.isActivating}
        impactMessage={getImpactMessage()}
      />
      <FiltroModal
        open={modalState.filtroDepto}
        onClose={() => closeModal("filtroDepto")}
        currentFilters={deptoFilters}
        onFilter={setDeptoFilters}
      />
      <FiltroModal
        open={modalState.filtroMun}
        onClose={() => closeModal("filtroMun")}
        currentFilters={munFilters}
        onFilter={setMunFilters}
      />
      <MunicipiosListModal
        open={modalState.municipioList}
        onClose={() => closeModal("municipioList")}
        paisContext={selectedPais}
        deptoContext={selectedDepto}
        municipios={municipiosByDepto}
        onAdd={() => openModal("municipioForm", { type: "municipio", item: null })}
        onEdit={(municipio) => openModal("municipioForm", { type: "municipio", item: municipio })}
        onInactivate={(municipio) => openModal("confirm", { type: "municipio", item: municipio, isActivating: municipio.estado === INACTIVE_STATUS })}
        onOpenFilter={() => setModalState((prev) => ({ ...prev, filtroMun: true }))}
      />
      <MunicipioModal
        open={modalState.municipioForm}
        onClose={() => closeModal("municipioForm")}
        onSave={handleSaveMunicipio}
        municipioToEdit={actionContext.item}
        paisContext={selectedPais}
        deptoContext={selectedDepto}
      />
    </Box>
  );
}
