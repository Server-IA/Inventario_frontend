/*=============================================================================
 Nombre del archivo : TipoCostoIndirecto.jsx
 Descripcion        : Módulo de Gestión de Tipos de Costos Indirectos (HU-044).
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |   Fecha    | Versión |      Autor           | Descripción del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2026-10-16 | 0.4.0   | Jeisson Sanchez      | Creación del contenedor de listado paginado y filtros para HU-044.2. |
 | 2026-10-16 | 0.4.0   | Jeisson Sanchez      | Integrar diálogo y confirmación de activación/inactivación para HU-044.4. |
 +------------+---------+----------------------+-----------------------------+
=============================================================================*/
import React, { useEffect, useMemo, useState } from "react";
import {
  Box,
  Button,
  Chip,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Stack,
  Typography,
} from "@mui/material";
import { useTranslation } from "react-i18next";
import axios from "../../axiosConfig";
import SectionHeader from "../../common/SectionHeader.jsx";
import GridActionBar from "../../common/GridActionBar.jsx";
import AppDataGrid from "../../common/AppDataGrid.jsx";
import MessageSnackBar from "../../MessageSnackBar.jsx";
import FormTipoCostoIndirecto from "./FormTipoCostoIndirecto.jsx";
import FiltroTipoCostoIndirecto from "./FiltroTipoCostoIndirecto.jsx";
import { filtrosVacios, toParams, parseServerError } from "./tipoCostoIndirectoValidation";

export default function TipoCostoIndirecto() {
  const { t } = useTranslation();
  const [formOpen, setFormOpen] = useState(false);
  const [selectedRow, setSelectedRow] = useState(null);
  const [confirmOpen, setConfirmOpen] = useState(false);
  const [toggling, setToggling] = useState(false);
  const [filtersOpen, setFiltersOpen] = useState(false);
  const [filtros, setFiltros] = useState(filtrosVacios);
  const [paginationModel, setPaginationModel] = useState({ page: 0, pageSize: 10 });
  const [rows, setRows] = useState([]);
  const [rowCount, setRowCount] = useState(0);
  const [loading, setLoading] = useState(false);
  const [refreshKey, setRefreshKey] = useState(0);
  const [message, setMessage] = useState({ open: false, severity: "success", text: "" });

  const queryParams = useMemo(() => toParams(filtros), [filtros]);

  useEffect(() => {
    let cancelled = false;
    setLoading(true);

    axios
      .get("/v1/tipos-costos-indirectos", {
        params: {
          page: paginationModel.page,
          size: paginationModel.pageSize,
          sortBy: "id,asc",
          ...queryParams,
        },
      })
      .then(({ data }) => {
        if (cancelled) return;
        const items = Array.isArray(data?.data) ? data.data : [];
        const total = data?.header?.totalElements ?? items.length;
        setRows(items);
        setRowCount(total);
      })
      .catch((error) => {
        if (cancelled) return;
        setRows([]);
        setRowCount(0);
        const { detail, messageKey } = parseServerError(error);
        const fallbackMessage = t("costos.tipoCostoIndirecto.messages.listError");
        setMessage({
          open: true,
          severity: "error",
          text: messageKey
            ? t(`costos.tipoCostoIndirecto.messages.${messageKey}`)
            : detail || fallbackMessage,
        });
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });

    return () => {
      cancelled = true;
    };
  }, [paginationModel, queryParams, refreshKey, t]);

  const applyFilters = (nuevosFiltros) => {
    setFiltros(nuevosFiltros);
    setPaginationModel((prev) => ({ ...prev, page: 0 }));
    setFiltersOpen(false);
  };

  const handleClearFilters = () => {
    applyFilters(filtrosVacios);
  };

  const columns = useMemo(
    () => [
      {
        field: "nombre",
        headerKey: "costos.tipoCostoIndirecto.columns.nombre",
        headerName: "Nombre",
        flex: 1.2,
        minWidth: 160,
      },
      {
        field: "descripcion",
        headerKey: "costos.tipoCostoIndirecto.columns.descripcion",
        headerName: "Descripción",
        flex: 2,
        minWidth: 220,
        valueGetter: (params) => params.value || "-",
      },
      {
        field: "empresaNombre",
        headerKey: "costos.tipoCostoIndirecto.columns.empresa",
        headerName: "Empresa",
        flex: 1,
        minWidth: 140,
        valueGetter: (params) => params.value || "-",
      },
      {
        field: "estadoNombre",
        headerKey: "costos.tipoCostoIndirecto.columns.estado",
        headerName: "Estado",
        statusChip: true,
        width: 120,
      },
    ],
    []
  );

  const localeText = useMemo(
    () => ({
      noRowsLabel: t("costos.tipoCostoIndirecto.grid.empty", "No hay tipos de costos indirectos para mostrar."),
    }),
    [t]
  );

  // Chips de filtros activos
  const activeChips = useMemo(() => {
    const list = [];
    if (filtros.nombre && filtros.nombre.trim()) {
      list.push({
        key: "nombre",
        label: `${t("costos.tipoCostoIndirecto.filters.nombre", "Nombre")}: ${filtros.nombre.trim()}`,
        onDelete: () => applyFilters({ ...filtros, nombre: "" }),
      });
    }
    if (filtros.estadoId) {
      const estadoTexto =
        String(filtros.estadoId) === "1"
          ? t("common.labels.active", "Activo")
          : t("common.labels.inactive", "Inactivo");
      list.push({
        key: "estadoId",
        label: `${t("costos.tipoCostoIndirecto.filters.estado", "Estado")}: ${estadoTexto}`,
        onDelete: () => applyFilters({ ...filtros, estadoId: "" }),
      });
    }
    return list;
  }, [filtros, t]);

  const hasActiveFilters = Object.keys(queryParams).length > 0;

  const handleCreate = () => {
    setSelectedRow(null);
    setFormOpen(true);
  };

  const handleUpdate = () => {
    if (!selectedRow?.id) return;
    setFormOpen(true);
  };

  const isSelectedActive = Number(selectedRow?.estadoId) === 1;
  const deleteActionLabel = isSelectedActive
    ? t("common.actions.inactivate", "Inactivar")
    : t("common.actions.activate", "Activar");

  const handleDeleteIntent = () => {
    if (!selectedRow?.id) return;
    setConfirmOpen(true);
  };

  const handleConfirmToggleEstado = async () => {
    if (!selectedRow?.id) return;

    setToggling(true);
    const nuevoEstadoActivo = !isSelectedActive;

    try {
      const { data } = await axios.patch(
        `/v1/tipos-costos-indirectos/${selectedRow.id}/estado`,
        { activo: nuevoEstadoActivo }
      );

      const successText =
        data?.mensaje ||
        t(
          "costos.tipoCostoIndirecto.messages.stateChanged",
          "El estado del tipo de costo indirecto fue actualizado exitosamente."
        );

      setMessage({
        open: true,
        severity: "success",
        text: successText,
      });

      setSelectedRow(null);
      setRefreshKey((k) => k + 1);
      setConfirmOpen(false);
    } catch (error) {
      const { detail, status } = parseServerError(error);
      const errorText =
        status === 409
          ? detail ||
            t(
              "costos.tipoCostoIndirecto.messages.hasActiveCosts",
              "No se puede inactivar porque tiene costos indirectos activos asociados."
            )
          : detail ||
            t(
              "costos.tipoCostoIndirecto.messages.stateError",
              "No se pudo cambiar el estado del tipo de costo indirecto."
            );

      setMessage({
        open: true,
        severity: "error",
        text: errorText,
      });
      setConfirmOpen(false);
    } finally {
      setToggling(false);
    }
  };

  return (
    <Box p={2}>
      <SectionHeader titleKey="costos.tipoCostoIndirecto.title" />

      <MessageSnackBar message={message} setMessage={setMessage} />

      <GridActionBar
        onAdd={handleCreate}
        onUpdate={handleUpdate}
        onDelete={handleDeleteIntent}
        canAdd={true}
        canUpdate={Boolean(selectedRow)}
        canDelete={Boolean(selectedRow)}
        showDelete={true}
        labels={{ delete: deleteActionLabel }}
        onFilters={() => setFiltersOpen(true)}
        onClearFilters={handleClearFilters}
        hasActiveFilters={hasActiveFilters}
      />

      {activeChips.length > 0 && (
        <Stack direction="row" flexWrap="wrap" useFlexGap spacing={1} sx={{ mb: 2 }}>
          {activeChips.map((chip) => (
            <Chip
              key={chip.key}
              size="small"
              label={chip.label}
              onDelete={chip.onDelete}
              color="primary"
              variant="outlined"
            />
          ))}
        </Stack>
      )}

      <FormTipoCostoIndirecto
        open={formOpen}
        setOpen={setFormOpen}
        selectedRow={selectedRow}
        setMessage={setMessage}
        onCreated={() => {
          setRefreshKey((k) => k + 1);
          setSelectedRow(null);
        }}
      />

      <FiltroTipoCostoIndirecto
        open={filtersOpen}
        filtros={filtros}
        onApply={applyFilters}
        onClose={() => setFiltersOpen(false)}
      />

      <AppDataGrid
        columns={columns}
        rows={rows}
        loading={loading}
        rowCount={rowCount}
        paginationModel={paginationModel}
        setPaginationModel={setPaginationModel}
        pageSizeOptions={[5, 10, 20, 50]}
        localeText={localeText}
        containerSx={{ borderRadius: 4 }}
        selectedRow={selectedRow}
        setSelectedRow={setSelectedRow}
      />

      <Dialog
        open={confirmOpen}
        onClose={() => !toggling && setConfirmOpen(false)}
        maxWidth="xs"
        fullWidth
      >
        <DialogTitle>
          {t(
            isSelectedActive
              ? "costos.tipoCostoIndirecto.confirmState.inactivateTitle"
              : "costos.tipoCostoIndirecto.confirmState.activateTitle",
            isSelectedActive
              ? "Inactivar tipo de costo indirecto"
              : "Activar tipo de costo indirecto"
          )}
        </DialogTitle>
        <DialogContent dividers>
          <Typography variant="body1">
            {t(
              isSelectedActive
                ? "costos.tipoCostoIndirecto.confirmState.inactivateText"
                : "costos.tipoCostoIndirecto.confirmState.activateText",
              {
                nombre: selectedRow?.nombre || "",
                defaultValue: isSelectedActive
                  ? `¿Deseas inactivar el tipo de costo indirecto "${selectedRow?.nombre || ""}"? Dejará de estar disponible para registrar nuevos costos indirectos.`
                  : `¿Deseas activar el tipo de costo indirecto "${selectedRow?.nombre || ""}"? Volverá a estar disponible para la parametrización de costos.`,
              }
            )}
          </Typography>
        </DialogContent>
        <DialogActions sx={{ px: 2.5, py: 1.5 }}>
          <Button
            onClick={() => setConfirmOpen(false)}
            disabled={toggling}
            color="inherit"
          >
            {t("common.actions.cancel", "Cancelar")}
          </Button>
          <Button
            onClick={handleConfirmToggleEstado}
            variant="contained"
            color={isSelectedActive ? "error" : "success"}
            disabled={toggling}
          >
            {toggling
              ? t("common.actions.saving", "Guardando...")
              : deleteActionLabel}
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}
