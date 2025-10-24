import React, { useState } from "react";
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  Box,
  Typography,
  Alert,
} from "@mui/material";
import axios from "./axiosConfig";

export default function ChangeLogoDialog({
  open = false,
  setOpen = () => {},
}) {
  const [selectedFile, setSelectedFile] = useState(null);
  const [uploading, setUploading] = useState(false);

  // mensaje interno local del modal
  // statusType: "success" | "error" | ""  (para pintar el color)
  const [statusMsg, setStatusMsg] = useState("");
  const [statusType, setStatusType] = useState("");

  const handleClose = () => {
    if (uploading) return; // bloquear cierre si está subiendo
    setSelectedFile(null);
    setStatusMsg("");
    setStatusType("");
    setOpen(false);
  };

  const handleFileChange = (e) => {
    const file = e.target.files?.[0];
    if (!file) return;

    const isPng =
      file.type === "image/png" ||
      file.name.toLowerCase().endsWith(".png");

    if (!isPng) {
      setStatusType("error");
      setStatusMsg("Solo se permiten archivos .png");
      e.target.value = null;
      setSelectedFile(null);
      return;
    }

    setSelectedFile(file);
    setStatusMsg("");      // limpiar mensajes anteriores
    setStatusType("");
  };

  const handleUpload = async () => {
    if (!selectedFile) {
      setStatusType("error");
      setStatusMsg("Selecciona un archivo PNG primero.");
      return;
    }

    try {
      setUploading(true);
      setStatusMsg("");
      setStatusType("");

      const formData = new FormData();
      formData.append("file", selectedFile);

      await axios.post("/v1/empresas/logo", formData, {
        headers: { "Content-Type": "multipart/form-data" },
      });

      // éxito
      setStatusType("success");
      setStatusMsg("✅ El logo se ha guardado exitosamente.");

      // si quieres limpiar el archivo después de subirlo, descomenta:
      // setSelectedFile(null);

    } catch (err) {
      console.error("Error subiendo logo:", err);

      const msgBackend =
        err?.response?.data?.message ||
        err?.response?.data?.error ||
        err?.response?.statusText ||
        "No se pudo subir el logo. Verifica que sea un PNG válido.";

      setStatusType("error");
      setStatusMsg(msgBackend);
    } finally {
      setUploading(false);
    }
  };

  return (
    <Dialog
      open={open}
      fullWidth
      maxWidth="xs"
      // NO dejamos que se cierre por click afuera o ESC
      onClose={(_, reason) => {
        if (uploading) return;
        if (reason === "backdropClick" || reason === "escapeKeyDown") return;
        handleClose();
      }}
    >
      <DialogTitle>Cambiar logo de la empresa</DialogTitle>

      <DialogContent dividers>
        <Box display="flex" flexDirection="column" gap={2}>
          <Typography variant="body2" sx={{ color: "text.secondary" }}>
            Sube el nuevo logo (solo formato PNG).
          </Typography>

          <Button variant="outlined" component="label" disabled={uploading}>
            SELECCIONAR ARCHIVO PNG
            <input
              type="file"
              hidden
              accept="image/png"
              onChange={handleFileChange}
            />
          </Button>

          {selectedFile && (
            <Typography
              variant="caption"
              sx={{
                wordBreak: "break-all",
                color: "text.secondary",
              }}
            >
              Archivo seleccionado: {selectedFile.name}
            </Typography>
          )}

          {/* Mensaje interno */}
          {statusMsg !== "" && (
            <Alert
              severity={statusType === "success" ? "success" : "error"}
              variant="filled"
              sx={{ fontSize: "0.8rem" }}
            >
              {statusMsg}
            </Alert>
          )}
        </Box>
      </DialogContent>

      <DialogActions>
        <Button onClick={handleClose} disabled={uploading}>
          Cerrar
        </Button>
        <Button
          onClick={handleUpload}
          variant="contained"
          disabled={uploading || !selectedFile}
        >
          {uploading ? "Subiendo..." : "Guardar"}
        </Button>
      </DialogActions>
    </Dialog>
  );
}
