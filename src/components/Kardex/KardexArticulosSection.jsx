import React, { useState } from "react";
import { Box, Typography } from "@mui/material";
import GridArticuloKardex from "./GridArticuloKardex";
import FormArticuloKardex from "./FormArticuloKardex";
import { resolveKardexId } from "./utils/kardexFormatters";

export function KardexArticulosSection({
    sectionRef,
    selectedRow,
    onReloadArticulos,
    articuloItems,
    articuloLoading,
    articuloPaginationModel,
    setArticuloPaginationModel,
    articuloRowCount,
    presentaciones,
    setMessage,
}) {
    const [selectedArticulo, setSelectedArticulo] = useState(null);
    const kardexId = resolveKardexId(selectedRow);

    if (!selectedRow || !kardexId) return null;

    return (
        <Box
            ref={sectionRef}
            sx={{
                backgroundColor: (theme) =>
                    theme.palette.mode === "dark" ? "#2c383b" : "#caddf3",
                padding: 2,
                borderRadius: 2,
                mt: 4,
            }}
        >
            <Typography variant="h6" mb={1}>
                Articulos del Kardex seleccionado
            </Typography>
            <FormArticuloKardex
                selectedRow={selectedArticulo}
                kardexId={kardexId}
                setSelectedRow={setSelectedArticulo}
                setMessage={setMessage}
                reloadData={onReloadArticulos}
            />

            <GridArticuloKardex
                items={articuloItems}
                setSelectedRow={setSelectedArticulo}
                loading={articuloLoading}
                paginationModel={articuloPaginationModel}
                onPaginationModelChange={setArticuloPaginationModel}
                rowCount={articuloRowCount}
                presentaciones={presentaciones}
                kardexId={kardexId}
                selectedRow={selectedArticulo}
            />
        </Box>
    );
}
