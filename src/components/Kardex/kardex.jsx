import React, { useState } from "react";
import MessageSnackBar from "../MessageSnackBar";
import GridKardex from "./GridKardex";
import ReKardex from "../RKardex/Rkardex";
import { Box, Typography, Button, Dialog, useTheme } from "@mui/material";
import { useKardexAuth } from "./hooks/useKardexAuth";
import { useKardexData } from "./hooks/useKardexData";
import { useKardexFilters } from "./hooks/useKardexFilters";
import { KardexFormsContainer } from "./KardexFormsContainer";

export default function Kardex() {
  const { isAdmin } = useKardexAuth();
  const { kardexesRaw, catalogs, reloadData, loading } = useKardexData();

  const [kardexPage, setKardexPage] = useState({ page: 0, size: 10 });
  const { filters, setFilters, paginatedRows, totalFiltered } = useKardexFilters(
    kardexesRaw,
    kardexPage,
    catalogs.tiposMovimiento
  );

  const [selectedRow, setSelectedRow] = useState(null);
  const [searchDialogOpen, setSearchDialogOpen] = useState(false);
  const [message, setMessage] = useState({ open: false, severity: "success", text: "" });

  const theme = useTheme();

  const containerKardex = {
    backgroundColor: theme.palette.mode === "dark" ? "#1e2a2c" : "#c9e6fe",
    padding: 3,
    borderRadius: 2,
  };

  return (
    <Box sx={{ p: 2 }}>
      <Box sx={{ ...containerKardex, mb: 4 }}>
        <Box mb={2} display="flex" justifyContent="space-between" alignItems="center">
          <Typography variant="h5">Gestion de Kardex</Typography>
          <Button variant="contained" onClick={() => setSearchDialogOpen(true)}>
            Buscar reporte
          </Button>
        </Box>

        <KardexFormsContainer
          selectedRow={selectedRow}
          setSelectedRow={setSelectedRow}
          reloadData={reloadData}
          setMessage={setMessage}
        />

        <Box sx={{ mt: 2 }}>
          <Typography variant="h6" gutterBottom>
            Lista de Kardex
          </Typography>
          <GridKardex
            kardexes={paginatedRows}
            almacenes={catalogs.almacenes}
            producciones={catalogs.producciones}
            tiposMovimiento={catalogs.tiposMovimiento}
            pedidos={catalogs.pedidos}
            ordenesCompra={catalogs.ordenesCompra}
            empresas={catalogs.empresas}
            selectedRow={selectedRow}
            setSelectedRow={setSelectedRow}
            loading={loading}
            rowCount={totalFiltered}
            paginationModel={kardexPage}
            setPaginationModel={setKardexPage}
            isAdmin={isAdmin}
            filters={filters}
            setFilters={setFilters}
          />
        </Box>
      </Box>

      <Dialog open={searchDialogOpen} onClose={() => setSearchDialogOpen(false)} fullWidth maxWidth="lg">
        <ReKardex setOpen={setSearchDialogOpen} />
      </Dialog>

      <MessageSnackBar message={message} setMessage={setMessage} />
    </Box>
  );
}
