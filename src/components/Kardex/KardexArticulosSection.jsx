import React, { useState } from "react";
import { Box, Typography } from "@mui/material";
import GridArticuloKardex from "./GridArticuloKardex";
import FormArticuloKardex from "./FormArticuloKardex";

/**
 * @description Sección de artículos del kardex seleccionado
 */
export function KardexArticulosSection({
    selectedRow,
    articuloItems,
    articuloLoading,
    articuloPaginationModel,
    setArticuloPaginationModel,
    articuloRowCount,
    presentaciones,
    setMessage,
}) {
    const [selectedArticulo, setSelectedArticulo] = useState({});
    const [reloadArticulos, setReloadArticulos] = useState(false);

    if (!selectedRow) return null;

    return (
        <Box
            sx={{
                backgroundColor: (theme) =>
                    theme.palette.mode === "dark" ? "#2c383b" : "#caddf3",
                padding: 2,
                borderRadius: 2,
                mt: 4,
            }}
        >
            <Box
                display="flex"
                justifyContent="space-between"
                alignItems="center"
                mb={2}
            >
                <Typography variant="h6">
                    Artículos del Kardex seleccionado
                </Typography>
                <Box display="flex" gap={2}>
                    <FormArticuloKardex
                        selectedRow={selectedArticulo}
                        kardexId={selectedRow?.id || ""}
                        setSelectedRow={setSelectedArticulo}
                        setMessage={setMessage}
                        reloadData={() => setReloadArticulos((prev) => !prev)}
                    />
                </Box>
            </Box>

            <GridArticuloKardex
                items={articuloItems}
                setSelectedRow={setSelectedArticulo}
                loading={articuloLoading}
                paginationModel={articuloPaginationModel}
                setPaginationModel={setArticuloPaginationModel}
                rowCount={articuloRowCount}
                presentaciones={presentaciones}
                kardexId={selectedRow?.id}
            />
        </Box>
    );
}
