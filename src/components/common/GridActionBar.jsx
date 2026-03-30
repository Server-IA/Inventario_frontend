import React from "react";
import { Stack, Button } from "@mui/material";
import AddIcon from "@mui/icons-material/Add";
import ModeEditOutlineIcon from "@mui/icons-material/ModeEditOutline";
import DeleteOutlineIcon from "@mui/icons-material/DeleteOutline";
import VisibilityIcon from "@mui/icons-material/Visibility";

export default function GridActionBar({
  onAdd,
  onUpdate,
  onDelete,
  canUpdate = false,
  canDelete = false,
  extraActions,
}) {
  return (
    <Stack direction="row" spacing={2} sx={{ mb: 2 }}>
      <Button
        onClick={onAdd}
        startIcon={<AddIcon />}
        sx={{
          bgcolor: "#173f39",
          color: "#fff",
          px: 2.5,
          py: 1,
          borderRadius: 2,
          textTransform: "uppercase",
          fontWeight: 700,
          fontSize: "0.75rem",
          boxShadow: "0 6px 16px rgba(23,63,57,0.25)",
          "&:hover": { bgcolor: "#0f2a26" },
          "& .MuiButton-startIcon svg": { fontSize: 16 },
        }}
      >
        Agregar
      </Button>
      <Button
        onClick={onUpdate}
        startIcon={<ModeEditOutlineIcon />}
        disabled={!canUpdate}
        sx={{
          bgcolor: "#f4f5f7",
          color: "#2a2e35",
          px: 2.5,
          py: 1,
          borderRadius: 2,
          textTransform: "uppercase",
          fontWeight: 700,
          fontSize: "0.75rem",
          boxShadow: "0 6px 16px rgba(0,0,0,0.08)",
          "&:hover": { bgcolor: "#e9eaee" },
          "&.Mui-disabled": { color: "#9aa0a6" },
          "& .MuiButton-startIcon svg": { fontSize: 16 },
        }}
      >
        Actualizar
      </Button>
      {extraActions}
      <Button
        onClick={onDelete}
        startIcon={<DeleteOutlineIcon />}
        disabled={!canDelete}
        sx={{
          bgcolor: "#fff0f0",
          color: "#d32f2f",
          px: 2.5,
          py: 1,
          borderRadius: 2,
          textTransform: "uppercase",
          fontWeight: 700,
          fontSize: "0.75rem",
          boxShadow: "0 6px 16px rgba(211,47,47,0.12)",
          "&:hover": { bgcolor: "#ffe5e5" },
          "&.Mui-disabled": { color: "#f19999" },
          "& .MuiButton-startIcon svg": { fontSize: 16 },
        }}
      >
        Eliminar
      </Button>
    </Stack>
  );
}
