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
}) {
    const [formOpen, setFormOpen] = useState(false);
    const [formMode, setFormMode] = useState("create");

    const handleDelete = async () => {
        if (!selectedRow) return;
        if (!window.confirm("¿Eliminar el Kardex seleccionado?")) return;

        try {
            const axios = (await import("../axiosConfig")).default;
            await axios.delete(`/v1/kardex/${selectedRow.id}`);
            setMessage({
                open: true,
                severity: "success",
                text: "Kardex eliminado correctamente.",
            });
            setSelectedRow(null);
            reloadData();
        } catch {
            setMessage({
                open: true,
                severity: "error",
                text: "Error al eliminar el Kardex.",
            });
        }
    };

    const handleInactivate = async () => {
        if (!selectedRow) return;

        const confirmMessage =
            "¿Inactivar este movimiento de Kardex?\n\n" +
            "⚠️ Se revertirán los cambios de stock asociados.\n" +
            "Si el stock resultante es negativo, la operación será bloqueada.";

        if (!window.confirm(confirmMessage)) return;

        try {
            const axios = (await import("../axiosConfig")).default;
            const response = await axios.put(`/v1/kardex/${selectedRow.id}/inactivate`);

            setMessage({
                open: true,
                severity: "success",
                text: response?.data?.message || "Kardex inactivado correctamente. Stock revertido.",
            });
            setSelectedRow(null);
            reloadData();
        } catch (err) {
            const errorMsg = err?.response?.data?.message || "Error al inactivar el Kardex.";
            setMessage({
                open: true,
                severity: "error",
                text: errorMsg,
            });
        }
    };

    return (
        <>
            <Box sx={{ mb: 2, display: "flex", gap: 2 }}>
                <Button
                    variant="contained"
                    color="primary"
                    onClick={() => {
                        setFormMode("create");
                        setFormOpen(true);
                    }}
                >
                    + Agregar
                </Button>
                <Button
                    variant="outlined"
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
                    disabled={!selectedRow}
                    onClick={handleInactivate}
                >
                    Inactivar
                </Button>
                <Button
                    variant="outlined"
                    color="error"
                    disabled={!selectedRow}
                    onClick={handleDelete}
                >
                    Eliminar
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
