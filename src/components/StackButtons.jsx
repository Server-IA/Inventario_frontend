import React from "react";
import { Stack, Button } from "@mui/material";
import AddIcon from "@mui/icons-material/Add";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";

export default function StackButtons({
  onCreate,
  onEdit,
  onDelete,
  disableEdit = false,
  disableDelete = false,
}) {
  return (
    <Stack direction="row" spacing={2} sx={{ mb: 2 }}>
      <Button
        variant="contained"
        startIcon={<AddIcon />}
        onClick={onCreate}
        sx={{
          bgcolor: "#1e3d3b",
          "&:hover": { bgcolor: "#16302f" },
        }}
      >
        AGREGAR
      </Button>

      <Button
        variant="contained"
        startIcon={<EditIcon />}
        onClick={onEdit}
        disabled={disableEdit}
        sx={{
          bgcolor: "#dcdcdc",
          color: "#333",
          "&:hover": { bgcolor: "#cfcfcf" },
        }}
      >
        ACTUALIZAR
      </Button>

      <Button
        variant="contained"
        startIcon={<DeleteIcon />}
        onClick={onDelete}
        disabled={disableDelete}
        sx={{
          bgcolor: "#f8d7da",
          color: "#c62828",
          "&:hover": { bgcolor: "#f1b0b7" },
        }}
      >
        ELIMINAR
      </Button>
    </Stack>
  );
}
