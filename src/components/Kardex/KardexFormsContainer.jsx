import React, { useState } from "react";
import { Button, Stack } from "@mui/material";
import FormKardex from "./FormKardex";
import axios from "../axiosConfig";
import { resolveKardexId } from "./utils/kardexFormatters";
import GridActionBar from "../common/GridActionBar";

export function KardexFormsContainer({
  selectedRow,
  setSelectedRow,
  reloadData,
  setMessage,
  onViewArticulos,
  onOpenReportes,
  onOpenFilters,
  onClearFilters,
  hasActiveFilters = false,
}) {
  const [formOpen, setFormOpen] = useState(false);
  const [formMode, setFormMode] = useState("create");
  const [startInArticles, setStartInArticles] = useState(false);

  const handleInactivate = async () => {
    const kardexId = resolveKardexId(selectedRow);
    if (!kardexId) return;

    const confirmMessage =
      "¿Anular este movimiento de Kardex?\n\n" +
      "Se revertiran los cambios de stock asociados.\n" +
      "Si el stock resultante es negativo, la operacion sera bloqueada.";

    if (!window.confirm(confirmMessage)) return;

    try {
      const response = await axios.delete(`/v1/kardex/${kardexId}`);

      setMessage({
        open: true,
        severity: "success",
        text: response?.data?.message || "Kardex anulado correctamente. Stock revertido.",
      });
      setSelectedRow(null);
      reloadData();
    } catch (err) {
      const d = err?.response?.data;
      const errorMsg =
        (typeof d === "string" && d) || d?.message || d?.detail || d?.title || "Error al anular el Kardex.";
      setMessage({
        open: true,
        severity: "error",
        text: errorMsg,
      });
    }
  };

  const handleViewArticulos = () => {
    if (!selectedRow) return;
    if (typeof onViewArticulos === "function") {
      onViewArticulos();
      return;
    }
    setFormMode("edit");
    setStartInArticles(true);
    setFormOpen(true);
  };

  return (
    <>
      <GridActionBar
        onAdd={() => {
          setFormMode("create");
          setStartInArticles(false);
          setFormOpen(true);
        }}
        onUpdate={() => {
          setFormMode("edit");
          setStartInArticles(false);
          setFormOpen(true);
        }}
        onDelete={handleInactivate}
        canUpdate={!!selectedRow}
        canDelete={!!selectedRow}
        labels={{ add: "Agregar", update: "Actualizar", delete: "Anular" }}
        onFilters={onOpenFilters}
        onClearFilters={onClearFilters}
        hasActiveFilters={hasActiveFilters}
        extraActions={
          <Stack direction="row" spacing={1}>
            <Button variant="contained" onClick={onOpenReportes}>
              Reportes
            </Button>
            <Button variant="contained" onClick={handleViewArticulos} disabled={!selectedRow}>
              Ver Articulos
            </Button>
          </Stack>
        }
      />

      <FormKardex
        open={formOpen}
        setOpen={setFormOpen}
        formMode={formMode}
        startInArticles={startInArticles}
        selectedRow={selectedRow}
        setSelectedRow={setSelectedRow}
        reloadData={reloadData}
        setMessage={setMessage}
      />
    </>
  );
}
