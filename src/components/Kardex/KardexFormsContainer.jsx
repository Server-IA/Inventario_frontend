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
