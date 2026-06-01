/*=============================================================================
 Nombre del archivo : LocalizacionGeografica.jsx
 Descripcion        : Módulo principal unificado para Gestión de Localización Geográfica
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |   Fecha    | Versión |      Autor           | Descripción del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2026-05-23 | 1.0.0   | Jeisson Sanchez      | Creación del archivo.       |
 +------------+---------+----------------------+-----------------------------+
=============================================================================*/

import React, { useState, useMemo } from "react";
import {
  Box,
  Typography,
  Select,
  MenuItem,
  Button,
  IconButton,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
} from "@mui/material";
import { Add, Edit, Close, FilterList } from "@mui/icons-material";

// Import Common Components
import SectionHeader from "../common/SectionHeader.jsx";
import GridActionBar from "../common/GridActionBar.jsx";
import AppDataGrid from "../common/AppDataGrid.jsx";

// Import Modals
import PaisModal from "./PaisModal";
import DepartamentoModal from "./DepartamentoModal";
import MunicipioModal from "./MunicipioModal";
import MunicipiosListModal from "./MunicipiosListModal";
import FiltroModal from "./FiltroModal";
import ConfirmInactivateModal from "./ConfirmInactivateModal";

// Initial Mock Data
const initialPaises = [
  { id: 1, nombre: "Colombia", codigo: "170", acronimo: "COL", estado: "Activo" },
  { id: 2, nombre: "Argentina", codigo: "032", acronimo: "ARG", estado: "Activo" },
];
const initialDepartamentos = [
  { id: 1, paisId: 1, nombre: "Huila", codigo: "41", acronimo: "HUI", estado: "Activo" },
  { id: 2, paisId: 1, nombre: "Antioquia", codigo: "05", acronimo: "ANT", estado: "Activo" },
];
const initialMunicipios = [
  { id: 1, departamentoId: 1, nombre: "Neiva", codigo: "41001", acronimo: "NEI", estado: "Activo" },
  { id: 2, departamentoId: 1, nombre: "Pitalito", codigo: "41551", acronimo: "PIT", estado: "Activo" },
];

export default function LocalizacionGeografica() {
  const [paises, setPaises] = useState(initialPaises);
  const [departamentos, setDepartamentos] = useState(initialDepartamentos);
  const [municipios, setMunicipios] = useState(initialMunicipios);

  // Selection state
  const [selectedPaisId, setSelectedPaisId] = useState(paises[0]?.id || "");
  const [selectedDeptoId, setSelectedDeptoId] = useState(null);

  // Filters state
  const [deptoFilters, setDeptoFilters] = useState(null);
  const [munFilters, setMunFilters] = useState(null);

  // Modals visibility state
  const [modalState, setModalState] = useState({
    pais: false,
    depto: false,
    municipioList: false,
    municipioForm: false,
    filtroDepto: false,
    filtroMun: false,
    confirm: false,
  });

  // Edit / Action contextual state
  const [actionContext, setActionContext] = useState({
    type: null, // "pais", "depto", "municipio"
    item: null, // item to edit or inactivate
    isActivating: false,
  });

  const selectedPais = paises.find((p) => p.id === selectedPaisId);
  const selectedDepto = departamentos.find((d) => d.id === selectedDeptoId);

  // Computed data
  const deptosByPais = useMemo(() => {
    let list = departamentos.filter((d) => d.paisId === selectedPaisId);
    if (deptoFilters) {
      if (deptoFilters.nombre) list = list.filter((d) => d.nombre.toLowerCase().includes(deptoFilters.nombre.toLowerCase()));
      if (deptoFilters.codigo) list = list.filter((d) => d.codigo.includes(deptoFilters.codigo));
      if (deptoFilters.acronimo) list = list.filter((d) => d.acronimo.toLowerCase().includes(deptoFilters.acronimo.toLowerCase()));
      if (deptoFilters.estado !== "Todos") list = list.filter((d) => d.estado === deptoFilters.estado);
    }
    return list;
  }, [departamentos, selectedPaisId, deptoFilters]);

  const municipiosByDepto = useMemo(() => {
    if (!selectedDeptoId) return [];
    let list = municipios.filter((m) => m.departamentoId === selectedDeptoId);
    if (munFilters) {
      if (munFilters.nombre) list = list.filter((m) => m.nombre.toLowerCase().includes(munFilters.nombre.toLowerCase()));
      if (munFilters.codigo) list = list.filter((m) => m.codigo.includes(munFilters.codigo));
      if (munFilters.acronimo) list = list.filter((m) => m.acronimo.toLowerCase().includes(munFilters.acronimo.toLowerCase()));
      if (munFilters.estado !== "Todos") list = list.filter((m) => m.estado === munFilters.estado);
    }
    return list;
  }, [municipios, selectedDeptoId, munFilters]);

  // General Modal Toggler
  const openModal = (name, ctx = {}) => {
    setActionContext((prev) => ({ ...prev, ...ctx }));
    setModalState((prev) => ({ ...prev, [name]: true }));
  };

  const closeModal = (name) => {
    setModalState((prev) => ({ ...prev, [name]: false }));
  };

  // ---------------- Handlers: PAIS ----------------
  const handleSavePais = (data) => {
    if (actionContext.item) {
      // Update
      setPaises(paises.map((p) => (p.id === actionContext.item.id ? { ...p, ...data } : p)));
    } else {
      // Create
      const newId = paises.length ? Math.max(...paises.map((p) => p.id)) + 1 : 1;
      const newPais = { id: newId, ...data, estado: "Activo" };
      setPaises([...paises, newPais]);
      setSelectedPaisId(newId);
    }
  };

  const handleConfirmAction = () => {
    const { type, item, isActivating } = actionContext;
    const nuevoEstado = isActivating ? "Activo" : "Inactivo";

    if (type === "pais") {
      setPaises(paises.map((p) => (p.id === item.id ? { ...p, estado: nuevoEstado } : p)));
      if (!isActivating) {
        // Cascade to deptos and municipios
        const deptosToInactivate = departamentos.filter((d) => d.paisId === item.id).map((d) => d.id);
        setDepartamentos(departamentos.map((d) => (d.paisId === item.id ? { ...d, estado: "Inactivo" } : d)));
        setMunicipios(municipios.map((m) => (deptosToInactivate.includes(m.departamentoId) ? { ...m, estado: "Inactivo" } : m)));
      }
    } else if (type === "depto") {
      setDepartamentos(departamentos.map((d) => (d.id === item.id ? { ...d, estado: nuevoEstado } : d)));
      if (!isActivating) {
        setMunicipios(municipios.map((m) => (m.departamentoId === item.id ? { ...m, estado: "Inactivo" } : m)));
      }
    } else if (type === "municipio") {
      setMunicipios(municipios.map((m) => (m.id === item.id ? { ...m, estado: nuevoEstado } : m)));
    }
    closeModal("confirm");
  };

  // ---------------- Handlers: DEPARTAMENTO ----------------
  const handleSaveDepto = (data) => {
    if (actionContext.item) {
      setDepartamentos(departamentos.map((d) => (d.id === actionContext.item.id ? { ...d, ...data } : d)));
    } else {
      const newId = departamentos.length ? Math.max(...departamentos.map((d) => d.id)) + 1 : 1;
      setDepartamentos([...departamentos, { id: newId, paisId: selectedPaisId, ...data, estado: "Activo" }]);
    }
  };

  const handleDeptoRowClick = (depto) => {
    if (selectedDeptoId === depto.id) setSelectedDeptoId(null);
    else setSelectedDeptoId(depto.id);
  };

  // ---------------- Handlers: MUNICIPIO ----------------
  const handleSaveMunicipio = (data) => {
    if (actionContext.item) {
      setMunicipios(municipios.map((m) => (m.id === actionContext.item.id ? { ...m, ...data } : m)));
    } else {
      const newId = municipios.length ? Math.max(...municipios.map((m) => m.id)) + 1 : 1;
      setMunicipios([...municipios, { id: newId, departamentoId: selectedDeptoId, ...data, estado: "Activo" }]);
    }
  };

  // ---------------- Render Helpers ----------------
  const getImpactMessage = () => {
    const { type, item } = actionContext;
    if (!item) return [];
    if (type === "pais") {
      const deptoCount = departamentos.filter((d) => d.paisId === item.id).length;
      const deptoIds = departamentos.filter((d) => d.paisId === item.id).map((d) => d.id);
      const munCount = municipios.filter((m) => deptoIds.includes(m.departamentoId)).length;
      return [
        `${deptoCount} Departamentos pasarán a estado Inactivo`,
        `${munCount} Municipios pasarán a estado Inactivo`,
      ];
    }
    if (type === "depto") {
      const munCount = municipios.filter((m) => m.departamentoId === item.id).length;
      return [`${munCount} Municipios pasarán a estado Inactivo`];
    }
    return [];
  };

  const deptoColumns = useMemo(() => [
    { field: "codigo", headerName: "Código", flex: 1, minWidth: 100 },
    { field: "nombre", headerName: "Nombre", flex: 2, minWidth: 200 },
    { field: "acronimo", headerName: "Acrónimo", flex: 1, minWidth: 100 },
    { field: "estado", headerName: "Estado", type: "status", flex: 1, minWidth: 100 },
  ], []);

  return (
    <Box sx={{ width: "100%", p: 3, color: "text.primary", minHeight: "80vh" }}>
      <SectionHeader title="Gestión de Localización Geográfica" />

      <Box sx={{ display: "flex", justifyContent: "center", alignItems: "center", gap: 2, mb: 4 }}>
        <Select
          value={selectedPaisId}
          onChange={(e) => {
            setSelectedPaisId(e.target.value);
            setSelectedDeptoId(null);
            setDeptoFilters(null);
          }}
          size="small"
          sx={{ minWidth: 250 }}
        >
          {paises.map((p) => (
            <MenuItem key={p.id} value={p.id}>
              {p.nombre} ({p.acronimo}) {p.estado === "Inactivo" ? "[Inactivo]" : ""}
            </MenuItem>
          ))}
        </Select>
        <Button
          variant="contained"
          sx={{ minWidth: "40px", p: "6px" }}
          onClick={() => openModal("pais", { type: "pais", item: null })}
        >
          <Add />
        </Button>
        <Button
          variant="outlined"
          disabled={!selectedPais}
          sx={{ minWidth: "40px", p: "6px" }}
          onClick={() => openModal("pais", { type: "pais", item: selectedPais })}
        >
          <Edit />
        </Button>
        <Button
          variant="contained"
          disabled={!selectedPais}
          sx={{ minWidth: "40px", p: "6px" }}
          color={selectedPais?.estado === "Activo" ? "error" : "warning"}
          onClick={() =>
            openModal("confirm", { type: "pais", item: selectedPais, isActivating: selectedPais?.estado === "Inactivo" })
          }
        >
          {selectedPais?.estado === "Activo" ? <Close /> : "Activar"}
        </Button>
      </Box>

      <GridActionBar
        onAdd={() => openModal("depto", { type: "depto", item: null })}
        onUpdate={() => openModal("depto", { type: "depto", item: selectedDepto })}
        onDelete={() => openModal("confirm", { type: "depto", item: selectedDepto, isActivating: selectedDepto?.estado === "Inactivo" })}
        canAdd={!!selectedPais && selectedPais.estado !== "Inactivo"}
        canUpdate={!!selectedDepto}
        canDelete={!!selectedDepto}
        onFilters={() => setModalState({ ...modalState, filtroDepto: true })}
        labels={{ delete: selectedDepto?.estado === "Inactivo" ? "Activar" : "Inactivar" }}
        extraActions={
          <Button
            variant="contained"
            disabled={!selectedDepto}
            onClick={() => setModalState({ ...modalState, municipioList: true })}
          >
            Ver Municipios
          </Button>
        }
      />

      <AppDataGrid
        rows={deptosByPais}
        columns={deptoColumns}
        selectedRow={selectedDepto}
        setSelectedRow={(row) => setSelectedDeptoId(row?.id || null)}
      />

      {/* Render Modals */}
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
        title={`Activar/Inactivar ${actionContext.type}`}
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
        onEdit={(mun) => openModal("municipioForm", { type: "municipio", item: mun })}
        onInactivate={(mun) =>
          openModal("confirm", { type: "municipio", item: mun, isActivating: mun.estado === "Inactivo" })
        }
        onOpenFilter={() => setModalState({ ...modalState, filtroMun: true })}
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
