import React from "react";
import { Dialog, DialogTitle, DialogContent, DialogActions, Stack, Typography, Button, Avatar, Chip, Card, CardContent, Grid } from "@mui/material";
export default function UserDetailDialog({ open, data, onClose }) {
  const initials = String(data?.nombre ?? "").charAt(0).toUpperCase() + String(data?.apellido ?? "").charAt(0).toUpperCase();
  const asignaciones = Array.isArray(data?.asignaciones) ? data.asignaciones : [];
  return (
    <Dialog open={open} onClose={onClose} maxWidth="md" fullWidth>
      <DialogTitle>Detalle de usuario</DialogTitle>
      <DialogContent dividers>
        <Card elevation={0} sx={{ mb: 2 }}>
          <CardContent>
            <Stack direction="row" spacing={2} alignItems="center">
              <Avatar sx={{ width: 64, height: 64 }}>{initials || "U"}</Avatar>
              <Stack spacing={0.5}>
                <Typography variant="h6">{data?.nombre} {data?.apellido}</Typography>
                <Typography variant="body2" color="text.secondary">{data?.username ?? data?.correo}</Typography>
                <Stack direction="row" spacing={1}>
                  <Chip label={Number(data?.estadoId ?? 1) === 1 ? "Activo" : "Inactivo"} color={Number(data?.estadoId ?? 1) === 1 ? "success" : "error"} size="small" />
                  {data?.rolPreferido && <Chip label={data.rolPreferido} size="small" />}
                  {data?.empresaNombre && <Chip label={data.empresaNombre} size="small" />}
                </Stack>
              </Stack>
            </Stack>
          </CardContent>
        </Card>
        <Grid container spacing={3}>
          <Grid item xs={12} md={6}>
            <Typography variant="subtitle1" sx={{ mb: 1 }}>Información personal</Typography>
            <Stack spacing={1.25}>
              <Typography>Género: {data?.genero ?? ""}</Typography>
              <Typography>Documento: {data?.documento ?? ""}</Typography>
              <Typography>Fecha nacimiento: {data?.fechaNacimiento ?? ""}</Typography>
              <Typography>Dirección: {data?.direccion ?? ""}</Typography>
              <Typography>Celular: {data?.celular ?? ""}</Typography>
            </Stack>
          </Grid>
          <Grid item xs={12} md={6}>
            <Typography variant="subtitle1" sx={{ mb: 1 }}>Asignaciones</Typography>
            <Stack spacing={1.25}>
              {asignaciones.length === 0 ? (
                <Typography color="text.secondary">Sin asignaciones</Typography>
              ) : (
                asignaciones.map((a, i) => (
                  <Card key={i} variant="outlined">
                    <CardContent sx={{ py: 1.25 }}>
                      <Stack direction={{ xs: "column", sm: "row" }} spacing={1} alignItems="center" justifyContent="space-between">
                        <Stack spacing={0.5}>
                          <Typography>Rol: {a.rolNombre}</Typography>
                          <Typography>Empresa: {a.empresaNombre}</Typography>
                        </Stack>
                        <Stack spacing={0.5} alignItems={{ xs: "flex-start", sm: "flex-end" }}>
                          <Typography>Inicio: {a.iniciaContratoEn}</Typography>
                          <Typography>Fin: {a.finalizaContratoEn || "Sin fecha"}</Typography>
                        </Stack>
                        <Chip label={Number(a.estadoId ?? 1) === 1 ? "Activo" : "Inactivo"} size="small" />
                      </Stack>
                    </CardContent>
                  </Card>
                ))
              )}
            </Stack>
          </Grid>
        </Grid>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>Cerrar</Button>
      </DialogActions>
    </Dialog>
  );
}
