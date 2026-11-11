/*=============================================================================
 Nombre del archivo : vistapreviapdfordencompra.jsx
 Descripcion        : Tarjeta de resumen de totales y detalles de Orden de Compra.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |   Fecha    | Versión |      Autor           | Descripción del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2026-07-01 | 0.4.0   | Cesar Medina         | Creación del componente.    |
 | 2026-10-16 | 0.4.0   | Jeisson Sanchez      | [HU-041.2 / Task #231] Consumir totalUnidades y valorTotal directamente del backend, soporte de unidades heterogéneas y estado de error. |
| 2026-10-23 | 0.4.0   | Jeisson Sanchez      | [HU-041.2 / Task #231] Reemplazar colores hardcodeados por tokens de tema (background.paper, text.primary/secondary, divider) y eliminar título duplicado. |
 +------------+---------+----------------------+-----------------------------+
=============================================================================*/
import React from "react";
import PropTypes from "prop-types";
import {
  Box, Typography, Card, CardContent, Grid, Divider, Alert
} from "@mui/material";
import { useTranslation } from "react-i18next";

export default function VistaPreviaPDFOrdenCompra({ orden }) {
  const { t, i18n } = useTranslation();

  if (!orden) return null;

  if (orden.error) {
    return (
      <Box mt={4}>
        <Alert severity="error" sx={{ maxWidth: 550, borderRadius: 3 }}>
          {orden.errorMessage || t("ordenCompra.messages.searchError", "Error al cargar los datos de la orden.")}
        </Alert>
      </Box>
    );
  }

  // HU-041.2: Consumir valores calculados directamente por el backend contractual
  const idOrden = orden.ordenCompraId ?? orden.id ?? "-";
  const pedidoId = orden.pedidoId ? `#${orden.pedidoId}` : "-";
  const fecha = orden.fechaHora ? String(orden.fechaHora).replace("T", " ").slice(0, 19) : "-";
  const proveedor = orden.proveedor || orden.proveedorNombre || "-";
  const sede = orden.sede || orden.sedeNombre || "-";
  const almacen = orden.almacen || orden.almacenNombre || "-";
  const estado = orden.estado || (orden.estadoId === 1 ? "Activo" : "Inactivo");

  const totalUnidadesDisplay = orden.totalUnidades !== null && orden.totalUnidades !== undefined
    ? Number(orden.totalUnidades)
    : t("ordenCompra.columns.heterogeneousUnits", "No aplica: unidades heterogéneas");

  const valorTotalNumber = Number(orden.valorTotal) || 0;
  const valorTotalDisplay = new Intl.NumberFormat(i18n.language || "es", {
    style: "currency",
    currency: "COP",
    minimumFractionDigits: 2,
  }).format(valorTotalNumber);

  return (
    <Box mt={1}>
      <Card
        elevation={0}
        sx={{
          maxWidth: 550,
          bgcolor: "background.paper",
          borderRadius: 2,
          border: (theme) => `1px solid ${theme.palette.divider}`,
          alignSelf: "flex-start",
        }}
      >
        <CardContent sx={{ p: 2, "&:last-child": { pb: 2 } }}>
          <Grid container spacing={1.5}>
            <Grid item xs={6}>
              <Typography variant="body2" color="text.secondary">
                <strong>ID:</strong>
              </Typography>
            </Grid>
            <Grid item xs={6}>
              <Typography variant="body2" color="text.primary">
                {idOrden}
              </Typography>
            </Grid>

            <Grid item xs={6}>
              <Typography variant="body2" color="text.secondary">
                <strong>{t("ordenCompra.columns.order", "Pedido")}:</strong>
              </Typography>
            </Grid>
            <Grid item xs={6}>
              <Typography variant="body2" color="text.primary">
                {pedidoId}
              </Typography>
            </Grid>

            <Grid item xs={6}>
              <Typography variant="body2" color="text.secondary">
                <strong>{t("ordenCompra.columns.fecha", "Fecha")}:</strong>
              </Typography>
            </Grid>
            <Grid item xs={6}>
              <Typography variant="body2" color="text.primary">
                {fecha}
              </Typography>
            </Grid>

            <Grid item xs={6}>
              <Typography variant="body2" color="text.secondary">
                <strong>{t("ordenCompra.columns.supplier", "Proveedor")}:</strong>
              </Typography>
            </Grid>
            <Grid item xs={6}>
              <Typography variant="body2" color="text.primary">
                {proveedor}
              </Typography>
            </Grid>

            <Grid item xs={6}>
              <Typography variant="body2" color="text.secondary">
                <strong>{t("ordenCompra.columns.headquarters", "Sede")}:</strong>
              </Typography>
            </Grid>
            <Grid item xs={6}>
              <Typography variant="body2" color="text.primary">
                {sede}
              </Typography>
            </Grid>

            <Grid item xs={6}>
              <Typography variant="body2" color="text.secondary">
                <strong>{t("ordenCompra.columns.warehouse", "Almacén")}:</strong>
              </Typography>
            </Grid>
            <Grid item xs={6}>
              <Typography variant="body2" color="text.primary">
                {almacen}
              </Typography>
            </Grid>

            <Grid item xs={6}>
              <Typography variant="body2" color="text.secondary">
                <strong>{t("ordenCompra.columns.status", "Estado")}:</strong>
              </Typography>
            </Grid>
            <Grid item xs={6}>
              <Typography variant="body2" color="text.primary">
                {estado}
              </Typography>
            </Grid>
          </Grid>

          <Divider sx={{ my: 2 }} />

          {/* HU-041.2: Resumen económico y total de unidades según backend */}
          <Typography variant="subtitle2" color="primary.main" sx={{ fontWeight: 700, mb: 1 }}>
            {t("ordenCompra.sections.totals", "Totales de la Compra")}
          </Typography>
          <Grid container spacing={1.5}>
            <Grid item xs={6}>
              <Typography variant="body2" color="text.secondary">
                <strong>{t("ordenCompra.columns.totalUnits", "Total Unidades")}:</strong>
              </Typography>
            </Grid>
            <Grid item xs={6}>
              <Typography variant="body2" color="text.primary" sx={{ fontWeight: 600 }}>
                {totalUnidadesDisplay}
              </Typography>
            </Grid>

            <Grid item xs={6}>
              <Typography variant="body2" color="text.secondary">
                <strong>{t("ordenCompra.columns.totalValue", "Valor Total")}:</strong>
              </Typography>
            </Grid>
            <Grid item xs={6}>
              <Typography variant="body2" color="text.primary" sx={{ fontWeight: 700, fontSize: "1.05rem" }}>
                {valorTotalDisplay}
              </Typography>
            </Grid>
          </Grid>
        </CardContent>
      </Card>
    </Box>
  );
}

VistaPreviaPDFOrdenCompra.propTypes = {
  orden: PropTypes.shape({
    id: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
    ordenCompraId: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
    pedidoId: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
    fechaHora: PropTypes.string,
    proveedor: PropTypes.string,
    proveedorNombre: PropTypes.string,
    sede: PropTypes.string,
    sedeNombre: PropTypes.string,
    almacen: PropTypes.string,
    almacenNombre: PropTypes.string,
    estado: PropTypes.string,
    estadoId: PropTypes.number,
    totalUnidades: PropTypes.number,
    valorTotal: PropTypes.number,
    error: PropTypes.bool,
    errorMessage: PropTypes.string,
  }),
};
