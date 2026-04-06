import React, { useState } from "react";
import { Box, Button } from "@mui/material";
import FormKardex from "./FromKardex";

/**
 * @description Contenedor de formularios y botones CRUD de Kardex
 */
export function KardexFormsContainer({
    selectedRow,
    setSelectedRow,
    reloadData,
    setMessage,
    onViewArticulos,
}) {
    const [formOpen, setFormOpen] = useState(false);
    const [formMode, setFormMode] = useState("create");

    const handleInactivate = async () => {
        if (!selectedRow) return;

        const confirmMessage =
            "¿Anular este movimiento de Kardex?\n\n" +
            "Se revertirán los cambios de stock asociados.\n" +
            "Si el stock resultante es negativo, la operación será bloqueada.";

        if (!window.confirm(confirmMessage)) return;

        try {
            const axios = (await import("../axiosConfig")).default;
            const response = await axios.put(`/v1/kardex/${selectedRow.id}/inactivate`);

            setMessage({
                open: true,
                severity: "success",
                text: response?.data?.message || "Kardex anulado correctamente. Stock revertido.",
            });
            setSelectedRow(null);
            reloadData();
        } catch (err) {
            const errorMsg = err?.response?.data?.message || "Error al anular el Kardex.";
            setMessage({
                open: true,
                severity: "error",
                text: errorMsg,
            });
        }
    };

    return (
        <>
            <Box sx={{ mb: 2, display: "flex", gap: 2, flexWrap: "wrap" }}>
                <Button
                    variant="contained"
                    color="primary"
                    sx={{ textTransform: "none" }}
                    onClick={() => {
                        setFormMode("create");
                        setFormOpen(true);
                    }}
                >
                    + Agregar
                </Button>
                <Button
                    variant="outlined"
                    sx={{ textTransform: "none" }}
                    onClick={() => {
                        setFormMode("edit");
                        setFormOpen(true);
                    }}
                    disabled={!selectedRow}
                >
                    Actualizar
                </Button>
                <Button
                    variant="outlined"
                    color="warning"
                    sx={{ textTransform: "none" }}
                    disabled={!selectedRow}
                    onClick={handleInactivate}
                >
                    Anular
                </Button>
                <Button
                    variant="outlined"
                    sx={{ textTransform: "none" }}
                    disabled={!selectedRow}
                    onClick={() => onViewArticulos?.()}
                >
                    Ver Articulos
                </Button>
            </Box>

            <FormKardex
                open={formOpen}
                setOpen={setFormOpen}
                formMode={formMode}
                selectedRow={selectedRow}
                setSelectedRow={setSelectedRow}
                reloadData={reloadData}
                setMessage={setMessage}
            />
        </>
    );
}

