import React from "react";
import { Dialog, DialogTitle, DialogContent, DialogActions, Stack, Typography, Button, Avatar, Chip, Card, CardContent, Grid } from "@mui/material";
import { useTranslation } from "react-i18next";
export default function UserDetailDialog({ open, data, onClose }) {
  const { t } = useTranslation();
  const firstName = data?.nombre ?? data?.nombreCompleto?.split(" ")?.[0] ?? "";
  const lastName = data?.apellido ?? data?.nombreCompleto?.split(" ")?.slice(1).join(" ") ?? "";
  const initials = String(firstName).charAt(0).toUpperCase() + String(lastName).charAt(0).toUpperCase();
  const asignaciones = Array.isArray(data?.asignaciones) ? data.asignaciones : [];
  return (
    <Dialog open={open} onClose={onClose} maxWidth="md" fullWidth>
      <DialogTitle>{t("usuario.detail.title")}</DialogTitle>
      <DialogContent dividers>
        <Card elevation={0} sx={{ mb: 2 }}>
          <CardContent>
            <Stack direction="row" spacing={2} alignItems="center">
              <Avatar sx={{ width: 64, height: 64 }}>{initials || "U"}</Avatar>
              <Stack spacing={0.5}>
                <Typography variant="h6">{firstName} {lastName}</Typography>
                <Typography variant="body2" color="text.secondary">{data?.username ?? data?.correo}</Typography>
                <Stack direction="row" spacing={1}>
                  <Chip label={Number(data?.estadoId ?? 1) === 1 ? t("common.labels.active") : t("common.labels.inactive")} color={Number(data?.estadoId ?? 1) === 1 ? "success" : "error"} size="small" />
                  {data?.rolPreferido && <Chip label={data.rolPreferido} size="small" />}
                  {data?.empresaNombre && <Chip label={data.empresaNombre} size="small" />}
                </Stack>
              </Stack>
            </Stack>
          </CardContent>
        </Card>
        <Grid container spacing={3}>
          <Grid item xs={12} md={6}>
            <Typography variant="subtitle1" sx={{ mb: 1 }}>{t("usuario.detail.personalInformation")}</Typography>
            <Stack spacing={1.25}>
              <Typography>{t("usuario.detail.gender")}: {data?.genero ?? ""}</Typography>
              <Typography>{t("usuario.detail.document")}: {data?.documento ?? ""}</Typography>
              <Typography>{t("usuario.detail.birthDate")}: {data?.fechaNacimiento ?? ""}</Typography>
              <Typography>{t("usuario.detail.address")}: {data?.direccion ?? ""}</Typography>
              <Typography>{t("usuario.detail.phone")}: {data?.celular ?? ""}</Typography>
            </Stack>
          </Grid>
          <Grid item xs={12} md={6}>
            <Typography variant="subtitle1" sx={{ mb: 1 }}>{t("usuario.detail.assignments")}</Typography>
            <Stack spacing={1.25}>
              {asignaciones.length === 0 ? (
                <Typography color="text.secondary">{t("common.labels.withoutAssignments")}</Typography>
              ) : (
                asignaciones.map((a, i) => (
                  <Card key={i} variant="outlined">
                    <CardContent sx={{ py: 1.25 }}>
                      <Stack direction={{ xs: "column", sm: "row" }} spacing={1} alignItems="center" justifyContent="space-between">
                        <Stack spacing={0.5}>
                          <Typography>{t("common.labels.role")}: {a.rolNombre}</Typography>
                          <Typography>{t("common.labels.company")}: {a.empresaNombre}</Typography>
                        </Stack>
                        <Stack spacing={0.5} alignItems={{ xs: "flex-start", sm: "flex-end" }}>
                          <Typography>{t("common.labels.start")}: {a.iniciaContratoEn}</Typography>
                          <Typography>{t("common.labels.end")}: {a.finalizaContratoEn || t("common.labels.withoutDate")}</Typography>
                        </Stack>
                        <Chip label={Number(a.estadoId ?? 1) === 1 ? t("common.labels.active") : t("common.labels.inactive")} size="small" />
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
        <Button onClick={onClose}>{t("common.actions.close")}</Button>
      </DialogActions>
    </Dialog>
  );
}
