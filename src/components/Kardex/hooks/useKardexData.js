import { useState, useCallback, useEffect } from "react";
import axios from "../../axiosConfig";
import { toArray, normalizePageResponse } from "../utils/kardexFormatters";
import { MAX_RECORDS } from "../constants/kardexConstants";

/**
 * @description Hook para manejar carga de datos de kardex y catálogos
 * @returns {object} { kardexesRaw, catalogs, loading, reloadData, reloadCatalogs }
 */
export const useKardexData = () => {
    // Kardex
    const [kardexesRaw, setKardexesRaw] = useState([]);
    const [loadingKardex, setLoadingKardex] = useState(false);

    // Catálogos
    const [catalogs, setCatalogs] = useState({
        almacenes: [],
        producciones: [],
        tiposMovimiento: [],
        presentaciones: [],
        pedidos: [],
        ordenesCompra: [],
        empresas: [],
    });
    const [loadingCatalogs, setLoadingCatalogs] = useState(false);

    // Cargar catálogos
    const reloadCatalogs = useCallback(async () => {
        setLoadingCatalogs(true);
        try {
            const [rAlm, rProd, rTmov, rPres, rPed, rOc, rEmp] = await Promise.all([
                axios.get("/v1/items/almacen/0"),
                axios.get("/v1/items/produccion/0"),
                axios.get("/v1/items/tipo_movimiento/0"),
                axios.get("/v1/items/producto_presentacion/0"),
                axios.get("/v1/items/pedido/0"),
                axios.get("/v1/items/orden_compra/0"),
                axios.get("/v1/items/empresa/0"),
            ]);

            setCatalogs({
                almacenes: toArray(rAlm.data),
                producciones: toArray(rProd.data),
                tiposMovimiento: toArray(rTmov.data),
                presentaciones: toArray(rPres.data),
                pedidos: toArray(rPed.data),
                ordenesCompra: toArray(rOc.data),
                empresas: toArray(rEmp.data),
            });
        } catch (e) {
            console.error("Error cargando catálogos:", e);
            setCatalogs({
                almacenes: [],
                producciones: [],
                tiposMovimiento: [],
                presentaciones: [],
                pedidos: [],
                ordenesCompra: [],
                empresas: [],
            });
        } finally {
            setLoadingCatalogs(false);
        }
    }, []);

    // Cargar kardexes
    const reloadData = useCallback(async () => {
        setLoadingKardex(true);
        try {
            const res = await axios.get("/v1/kardex", {
                params: { page: 0, size: MAX_RECORDS },
            });
            const rows = toArray(res.data);
            setKardexesRaw(rows);
        } catch (e) {
            console.error("Error cargando kardexes:", e);
            setKardexesRaw([]);
        } finally {
            setLoadingKardex(false);
        }
    }, []);

    // Cargar al montar el componente
    useEffect(() => {
        reloadCatalogs();
        reloadData();
    }, [reloadCatalogs, reloadData]);

    return {
        kardexesRaw,
        catalogs,
        loading: loadingKardex || loadingCatalogs,
        loadingKardex,
        loadingCatalogs,
        reloadData,
        reloadCatalogs,
    };
};
