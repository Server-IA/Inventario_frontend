/*=============================================================================
 Nombre del archivo : CostoIndirecto.jsx
 Descripcion        : Módulo Gestión de Costos Indirectos del subsistema Costos.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |   Fecha    | Versión |      Autor           | Descripción del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2026-09-25 | 0.4.0   | Arekkazu             | Creación del archivo para   |
 |            |         |                      | la HU-045.1 (solo registro).|
 +------------+---------+----------------------+-----------------------------+
=============================================================================*/
/**
 * @module CostoIndirecto
 * @description Contenedor del módulo: encabezado, barra de acciones, grilla y
 * modal de registro. El listado se conectará cuando exista el GET del backend.
 */
import React, { useMemo, useState } from "react";
import { Box } from "@mui/material";
import { useTranslation } from "react-i18next";
import SectionHeader from "../../common/SectionHeader.jsx";
import GridActionBar from "../../common/GridActionBar.jsx";
import AppDataGrid from "../../common/AppDataGrid.jsx";
import MessageSnackBar from "../../MessageSnackBar.jsx";
import FormCostoIndirecto from "./FormCostoIndirecto.jsx";

export default function CostoIndirecto() {
  const { t } = useTranslation();
  const [formOpen, setFormOpen] = useState(false);
  const [message, setMessage] = useState({ open: false, severity: "success", text: "" });

  const columns = useMemo(
    () => [
      { field: "tipoCostoIndirectoNombre", headerKey: "costos.costoIndirecto.columns.tipo", flex: 1, minWidth: 220 },
      { field: "espacioNombre", headerKey: "costos.costoIndirecto.columns.espacio", flex: 1, minWidth: 180 },
      { field: "fechaInicio", headerKey: "costos.costoIndirecto.columns.fechaInicio", width: 160 },
      { field: "fechaFin", headerKey: "costos.costoIndirecto.columns.fechaFin", width: 160 },
    ],
    []
  );
  const localeText = useMemo(() => ({ noRowsLabel: t("costos.costoIndirecto.grid.empty") }), [t]);

  return (
    <Box p={2}>
      <SectionHeader titleKey="costos.costoIndirecto.title" />

      <MessageSnackBar message={message} setMessage={setMessage} />

      {/* ponytail: Actualizar/Eliminar llegan con HU-045.3 y siguientes; por ahora solo se registra. */}
      <GridActionBar onAdd={() => setFormOpen(true)} canUpdate={false} showDelete={false} />

      <FormCostoIndirecto open={formOpen} setOpen={setFormOpen} setMessage={setMessage} />

      <AppDataGrid columns={columns} rows={[]} localeText={localeText} containerSx={{ borderRadius: 4 }} />
    </Box>
  );
}
