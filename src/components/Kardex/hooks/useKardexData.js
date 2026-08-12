import { useState, useCallback, useEffect } from "react";
import axios from "../../axiosConfig";
import { toArray } from "../utils/kardexFormatters";
import { MAX_RECORDS } from "../constants/kardexConstants";

export const useKardexData = () => {
    const [kardexesRaw, setKardexesRaw] = useState([]);
    const [loadingKardex, setLoadingKardex] = useState(false);

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

    const reloadCatalogs = useCallback(async () => {
        setLoadingCatalogs(true);
        try {
            const reqs = await Promise.allSettled([
                axios.get("/v1/items/almacen/0"),
                axios.get("/v1/items/produccion/0"),
                axios.get("/v1/items/tipo_movimiento/0"),
                axios.get("/v1/items/producto_presentacion/0"),
                axios.get("/v1/items/pedido/0"),
                axios.get("/v1/items/orden_compra/0"),
                axios.get("/v1/items/empresa/0"),
                axios.get("/v1/items/proveedor/0"),
            ]);

            const pick = (idx) =>
                reqs[idx]?.status === "fulfilled" ? reqs[idx].value?.data : [];

            const empresasBase = toArray(pick(6));
            const proveedoresBase = toArray(pick(7));
            const empresasMerged = [...empresasBase];
            const seen = new Set(empresasBase.map((e) => String(e?.id)));
            for (const prov of proveedoresBase) {
                const id = prov?.id;
                if (id == null || seen.has(String(id))) continue;
                empresasMerged.push(prov);
                seen.add(String(id));
            }

            setCatalogs({
                almacenes: toArray(pick(0)),
                producciones: toArray(pick(1)),
                tiposMovimiento: toArray(pick(2)),
                presentaciones: toArray(pick(3)),
                pedidos: toArray(pick(4)),
                ordenesCompra: toArray(pick(5)),
                empresas: empresasMerged,
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

    const reloadData = useCallback(async () => {
        setLoadingKardex(true);
        try {
            const res = await axios.get("/v1/kardex", {
                params: { page: 0, size: MAX_RECORDS, sort: "id,desc" },
            });
            setKardexesRaw(toArray(res.data));
        } catch (e) {
            console.error("Error cargando kardexes:", e);
            setKardexesRaw([]);
        } finally {
            setLoadingKardex(false);
        }
    }, []);

    useEffect(() => {
        reloadCatalogs();
        reloadData();
    }, [reloadCatalogs, reloadData]);

    return {
        kardexesRaw,
        catalogs,
        loading: loadingKardex || loadingCatalogs,
        reloadData,
        reloadCatalogs,
    };
};
