import React, { useState, useEffect, useCallback, useRef } from "react";
import axios from "../axiosConfig";
import MessageSnackBar from "../MessageSnackBar";
import GridKardex from "./GridKardex";
import ReKardex from "../RKardex/Rkardex";
import { Box, Typography, Button, Dialog, useTheme } from "@mui/material";
import { useKardexAuth } from "./hooks/useKardexAuth";
import { useKardexData } from "./hooks/useKardexData";
import { useKardexFilters } from "./hooks/useKardexFilters";
import { KardexFormsContainer } from "./KardexFormsContainer";
import { KardexArticulosSection } from "./KardexArticulosSection";
import { normalizePageResponse } from "./utils/kardexFormatters";

/**
 * @description Componente principal para la gestión de Kardex
 * Coordina autenticación, datos, filtros y sub-componentes
 */
export default function Kardex() {
    // Autenticación
    const { isAdmin } = useKardexAuth();

    // Datos
    const { kardexesRaw, catalogs, reloadData } = useKardexData();

    // Filtros
    const [kardexPage, setKardexPage] = useState({ page: 0, size: 10 });
    const {
        filters,
        setFilters,
        paginatedRows,
        totalFiltered,
    } = useKardexFilters(kardexesRaw, kardexPage);

    // UI
    const [selectedRow, setSelectedRow] = useState(null);
    const [searchDialogOpen, setSearchDialogOpen] = useState(false);
    const [message, setMessage] = useState({
        open: false,
        severity: "success",
        text: "",
    });

    // Artículos
    const [articuloItems, setArticuloItems] = useState([]);
    const [articuloPaginationModel, setArticuloPaginationModel] = useState({
        page: 0,
        size: 10,
    });
    const [articuloRowCount, setArticuloRowCount] = useState(0);
    const [articuloLoading, setArticuloLoading] = useState(false);
    const [reloadArticulos, setReloadArticulos] = useState(false);

    const theme = useTheme();
    const articulosSectionRef = useRef(null);

    // Cargar artículos del kardex seleccionado
    const loadArticulos = useCallback(async (kardexId) => {
        if (!kardexId) {
            setArticuloItems([]);
            setArticuloRowCount(0);
            return;
        }
        try {
            setArticuloLoading(true);
            const { page, size } = articuloPaginationModel;
            const res = await axios.get(`/v1/articulo-kardex/${kardexId}/articulos`, {
                params: { page, size, sort: "id,desc" },
            });

            const { rows, total } = normalizePageResponse(res);
            setArticuloItems(rows);

            const totalNum = Number(total);
            const effectiveTotal =
                Number.isFinite(totalNum) && totalNum > 0
                    ? totalNum
                    : rows.length < size
                        ? page * size + rows.length
                        : (page + 2) * size;

            setArticuloRowCount(effectiveTotal);

            if (rows.length === 0 && page > 0) {
                setArticuloPaginationModel((p) => ({ ...p, page: p.page - 1 }));
            }
        } catch {
            setArticuloItems([]);
            setArticuloRowCount(0);
        } finally {
            setArticuloLoading(false);
        }
    }, [articuloPaginationModel]);

    // Cargar artículos cuando cambia fila seleccionada
    useEffect(() => {
        if (selectedRow) {
            loadArticulos(selectedRow.id);
        } else {
            setArticuloItems([]);
            setArticuloRowCount(0);
        }
    }, [selectedRow, reloadArticulos, articuloPaginationModel, loadArticulos]);

    const containerKardex = {
        backgroundColor: theme.palette.mode === "dark" ? "#1e2a2c" : "#c9e6fe",
        padding: 3,
        borderRadius: 2,
    };

    return (
        <Box sx={{ p: 2 }}>
            {/* Sección Kardex */}
            <Box sx={{ ...containerKardex, mb: 4 }}>
                <Box
                    mb={2}
                    display="flex"
                    justifyContent="space-between"
                    alignItems="center"
                >
                    <Typography variant="h5">Gestión de Kardex</Typography>
                    <Button
                        variant="contained"
                        onClick={() => setSearchDialogOpen(true)}
                    >
                        Buscar reporte
                    </Button>
                </Box>

                {/* Formularios CRUD */}
                <KardexFormsContainer
                    selectedRow={selectedRow}
                    setSelectedRow={setSelectedRow}
                    reloadData={reloadData}
                    setMessage={setMessage}
                    onViewArticulos={() =>
                        articulosSectionRef.current?.scrollIntoView({
                            behavior: "smooth",
                            block: "start",
                        })
                    }
                />

                {/* Grid de Kardex */}
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
                        loading={false}
                        rowCount={totalFiltered}
                        paginationModel={kardexPage}
                        setPaginationModel={setKardexPage}
                        isAdmin={isAdmin}
                        filters={filters}
                        setFilters={setFilters}
                    />
                </Box>
            </Box>

            {/* Sección Artículos */}
            <KardexArticulosSection
                sectionRef={articulosSectionRef}
                selectedRow={selectedRow}
                onReloadArticulos={() => setReloadArticulos((prev) => !prev)}
                articuloItems={articuloItems}
                articuloLoading={articuloLoading}
                articuloPaginationModel={articuloPaginationModel}
                setArticuloPaginationModel={setArticuloPaginationModel}
                articuloRowCount={articuloRowCount}
                presentaciones={catalogs.presentaciones}
                setMessage={setMessage}
            />

            {/* Dialog Reporte */}
            <Dialog
                open={searchDialogOpen}
                onClose={() => setSearchDialogOpen(false)}
                fullWidth
                maxWidth="lg"
            >
                <ReKardex setOpen={setSearchDialogOpen} />
            </Dialog>

            {/* Mensajes */}
            <MessageSnackBar message={message} setMessage={setMessage} />
        </Box>
    );
}
