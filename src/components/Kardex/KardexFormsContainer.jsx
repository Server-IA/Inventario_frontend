import React, { useState } from "react";
import { Box, Button } from "@mui/material";
import FormKardex from "./FromKardex";
import axios from "../axiosConfig";
import { resolveKardexId } from "./utils/kardexFormatters";

export function KardexFormsContainer({
  selectedRow,
  setSelectedRow,
  reloadData,
  setMessage,
  onViewArticulos,
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
      <Box sx={{ mb: 2, display: "flex", gap: 2, flexWrap: "wrap" }}>
        <Button
          variant="contained"
          color="primary"
          sx={{ textTransform: "none" }}
          onClick={() => {
            setFormMode("create");
            setStartInArticles(false);
            setFormOpen(true);
          }}
        >
          + Agregar
        </Button>
        <Button
          variant="outlined"
          sx={{ textTransform: "none" }}
          onClick={() => {
            setFormMode("edit");
            setStartInArticles(false);
            setFormOpen(true);
          }}
          disabled={!selectedRow}
        >
          Actualizar
        </Button>
        <Button
          variant="outlined"
          color="warning"
          sx={{ textTransform: "none" }}
          disabled={!selectedRow}
          onClick={handleInactivate}
        >
          Anular
        </Button>
        <Button
          variant="outlined"
          sx={{ textTransform: "none" }}
          disabled={!selectedRow}
          onClick={handleViewArticulos}
        >
          Ver Articulos
        </Button>
      </Box>

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
