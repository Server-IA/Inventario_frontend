import React from "react";
import {
  GridToolbarContainer,
  GridToolbarQuickFilter,
} from "@mui/x-data-grid";
import { Button, Box } from "@mui/material";
import RestartAltIcon from "@mui/icons-material/RestartAlt";

export function KardexToolbar({ onResetColumns }) {
  return (
    <GridToolbarContainer
      sx={{
        p: 1,
        gap: 1,
        justifyContent: "flex-end",
        flexWrap: "wrap",
      }}
    >
      <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
        <GridToolbarQuickFilter debounceMs={300} />
        <Button variant="outlined" size="small" startIcon={<RestartAltIcon />} onClick={onResetColumns}>
          Restablecer columnas
        </Button>
      </Box>
    </GridToolbarContainer>
  );
}
