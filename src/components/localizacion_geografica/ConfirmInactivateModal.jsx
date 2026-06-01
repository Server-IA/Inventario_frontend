/*=============================================================================
 Nombre del archivo : ConfirmInactivateModal.jsx
 Descripcion        : Modal de confirmación para inactivar o activar un registro geográfico.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |   Fecha    | Versión |      Autor           | Descripción del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2026-05-23 | 1.0.0   | Jeisson Sanchez      | Creación del archivo.       |
 +------------+---------+----------------------+-----------------------------+
=============================================================================*/

import React from "react";
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  Typography,
  Box,
} from "@mui/material";

export default function ConfirmInactivateModal({
  open,
  onClose,
  onConfirm,
  title,
  itemName,
  isActivating = false,
  impactMessage = [],
}) {
  return (
    <Dialog
      open={open}
      onClose={onClose}
      PaperProps={{
        sx: {
          backgroundColor: "#1e1e1e",
          color: "#fff",
          minWidth: 400,
          border: isActivating ? "1px solid #4caf50" : "1px solid #f44336",
          borderRadius: 2,
        },
      }}
    >
      <DialogTitle sx={{ textAlign: "center", pb: 1 }}>
        {title || `Activar/Inactivar ${itemName}`}
      </DialogTitle>
      <DialogContent>
        <Typography variant="body1" align="center" sx={{ mb: 2 }}>
          ¿Está seguro que desea {isActivating ? "activar" : "inactivar"} este registro?
        </Typography>
        {!isActivating && impactMessage && impactMessage.length > 0 && (
          <Box sx={{ mt: 2 }}>
            <Typography variant="body2" sx={{ mb: 1 }}>
              Esta acción afectará los siguientes registros asociados:
            </Typography>
            <Box
              sx={{
                border: "1px solid #555",
                borderRadius: 1,
                p: 2,
                backgroundColor: "#2a2a2a",
              }}
            >
              <ul style={{ margin: 0, paddingLeft: 20 }}>
                {impactMessage.map((msg, idx) => (
                  <li key={idx}>
                    <Typography variant="body2">{msg}</Typography>
                  </li>
                ))}
              </ul>
              <Typography variant="body2" sx={{ mt: 1 }}>
                No aparecerán en los selectores del sistema.
              </Typography>
            </Box>
          </Box>
        )}
      </DialogContent>
      <DialogActions sx={{ justifyContent: "center", pb: 3 }}>
        <Button
          onClick={onClose}
          variant="outlined"
          sx={{
            color: "#fff",
            borderColor: "#fff",
            "&:hover": { borderColor: "#ccc", backgroundColor: "rgba(255,255,255,0.1)" },
          }}
        >
          Cancelar
        </Button>
        <Button
          onClick={onConfirm}
          variant="contained"
          sx={{
            backgroundColor: isActivating ? "#2e7d32" : "#d32f2f",
            "&:hover": { backgroundColor: isActivating ? "#1b5e20" : "#b71c1c" },
          }}
        >
          {isActivating ? "Activar" : "Inactivar"}
        </Button>
      </DialogActions>
    </Dialog>
  );
}
