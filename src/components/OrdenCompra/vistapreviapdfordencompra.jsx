import React from "react";
import PropTypes from "prop-types";
import {
  Box, Typography, Card, CardContent, Grid, Divider
} from "@mui/material";

export default function VistaPreviaPDFOrdenCompra({ orden, articulos = [] }) {
  // HU-041.2: Calcular totales (unidades pedidas y valor total de la orden)
  const items = Array.isArray(articulos) && articulos.length > 0 
    ? articulos 
    : Array.isArray(orden?.articulosCargados) 
      ? orden.articulosCargados 
      : [];

  const totalUnidades = items.reduce((sum, item) => sum + (Number(item.cantidad) || 0), 0);
  const valorTotal = items.reduce((sum, item) => {
    const cant = Number(item.cantidad) || 0;
    const precio = Number(item.precioUnitario) || Number(item.precio) || 0;
    return sum + (cant * precio);
  }, 0);

  return (
    <Box mt={4}>
      <Card elevation={3} sx={{ maxWidth: 550, backgroundColor: "#1e1e1e", borderRadius: 3, alignSelf: "flex-start" }}>
        <CardContent>
          <Typography variant="h6" gutterBottom sx={{ color: "#fff" }}>
            Resumen de la Orden de Compra
          </Typography>

          <Grid container spacing={1}>
            <Grid item xs={6}>
              <Typography sx={{ color: "#bbb" }}><strong>ID:</strong></Typography>
            </Grid>
            <Grid item xs={6}>
              <Typography sx={{ color: "#fff" }}>{orden.id}</Typography>
            </Grid>

            <Grid item xs={6}>
              <Typography sx={{ color: "#bbb" }}><strong>Descripción:</strong></Typography>
            </Grid>
            <Grid item xs={6}>
              <Typography sx={{ color: "#fff" }}>{orden.descripcion}</Typography>
            </Grid>

            <Grid item xs={6}>
              <Typography sx={{ color: "#bbb" }}><strong>Fecha:</strong></Typography>
            </Grid>
            <Grid item xs={6}>
              <Typography sx={{ color: "#fff" }}>{orden.fechaHora}</Typography>
            </Grid>

            <Grid item xs={6}>
              <Typography sx={{ color: "#bbb" }}><strong>Pedido ID:</strong></Typography>
            </Grid>
            <Grid item xs={6}>
              <Typography sx={{ color: "#fff" }}>{orden.pedidoId}</Typography>
            </Grid>

            <Grid item xs={6}>
              <Typography sx={{ color: "#bbb" }}><strong>Proveedor ID:</strong></Typography>
            </Grid>
            <Grid item xs={6}>
              <Typography sx={{ color: "#fff" }}>{orden.proveedorId}</Typography>
            </Grid>

            <Grid item xs={6}>
              <Typography sx={{ color: "#bbb" }}><strong>Estado:</strong></Typography>
            </Grid>
            <Grid item xs={6}>
              <Typography sx={{ color: "#fff" }}>{orden.estadoId === 1 ? "Activo" : "Inactivo"}</Typography>
            </Grid>
          </Grid>

          <Divider sx={{ my: 2, borderColor: "rgba(255,255,255,0.15)" }} />

          {/* HU-041.2: Resumen de totales de la orden */}
          <Typography variant="subtitle2" sx={{ color: "#66bb6a", fontWeight: 700, mb: 1 }}>
            Totales de la Compra
          </Typography>
          <Grid container spacing={1}>
            <Grid item xs={6}>
              <Typography sx={{ color: "#bbb" }}><strong>Total Unidades:</strong></Typography>
            </Grid>
            <Grid item xs={6}>
              <Typography sx={{ color: "#fff", fontWeight: 600 }}>{totalUnidades}</Typography>
            </Grid>

            <Grid item xs={6}>
              <Typography sx={{ color: "#bbb" }}><strong>Valor Total:</strong></Typography>
            </Grid>
            <Grid item xs={6}>
              <Typography sx={{ color: "#fff", fontWeight: 700, fontSize: "1.05rem" }}>
                ${valorTotal.toLocaleString("es-CO", { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
              </Typography>
            </Grid>
          </Grid>
        </CardContent>
      </Card>
    </Box>
  );
}

VistaPreviaPDFOrdenCompra.propTypes = {
  orden: PropTypes.object.isRequired,
  articulos: PropTypes.array,
};