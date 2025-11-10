import React, { useState, useEffect, useMemo, useCallback } from "react";
import axios from "../axiosConfig";
import MessageSnackBar from "../MessageSnackBar";
import FormProceso from "./FromProceso";
import GridProceso from "./GridProceso";

/* ------------------------ Helpers robustos ------------------------ */
const toList = (payload) => {
  if (!payload) return [];
  if (Array.isArray(payload)) return payload;
  if (Array.isArray(payload?.content)) return payload.content; // Page<>
  if (Array.isArray(payload?.data)) return payload.data;       // { data: [...] }
  if (typeof payload === "string") { try { return JSON.parse(payload); } catch { return []; } }
  return [];
};

const toMap = (payload, key = "id", label = "name") => {
  const list = toList(payload);
  const map = {};
  for (const it of list) {
    const k = it?.[key];
    if (k != null) map[String(k)] = it?.[label];
  }
  return map;
};
/* ----------------------------------------------------------------- */

export default function Proceso() {
  const [selectedRow, setSelectedRow] = useState(null);
  const [message, setMessage] = useState({
    open: false,
    severity: "success",
    text: "",
  });

  // catálogo tipoProduccion
  const [tipoProduccionMap, setTipoProduccionMap] = useState({}); // id -> name

  // data principal
  const [procesosRaw, setProcesosRaw] = useState([]);

  // ---- paginación del grid (server-side) ----
  const [page, setPage] = useState(0);      // DataGrid usa 0-based
  const [size, setSize] = useState(10);     // rowsPerPage actual
  const [totalElements, setTotalElements] = useState(0);
  const [loading, setLoading] = useState(false);

  // carga catálogo tipo_produccion
  const loadTipoProduccion = useCallback(async () => {
    try {
      const res = await axios.get("/v1/items/tipo_produccion/0"); // devuelve [{id, name}, ...]
      setTipoProduccionMap(toMap(res.data, "id", "name"));
    } catch (e) {
      console.error("No se pudo cargar tipo_produccion:", e);
      setTipoProduccionMap({});
      setMessage({
        open: true,
        severity: "error",
        text: "No se pudo cargar el catálogo de Tipo de Producción.",
      });
    }
  }, []);

  // carga procesos paginados
  const loadProcesos = useCallback(
    async (pageArg = page, sizeArg = size) => {
      try {
        setLoading(true);

        const safePage = Math.max(0, Number.isFinite(+pageArg) ? +pageArg : 0);
        const safeSize = Math.max(1, Number.isFinite(+sizeArg) ? +sizeArg : 10);

        // Ejemplo backend tipo Spring Page<Proceso>
        const res = await axios.get("/v1/proceso", {
          params: { page: safePage, size: safeSize },
        });

        const dataPage = res?.data ?? {};
        const lista = Array.isArray(dataPage?.content)
          ? dataPage.content
          : toList(dataPage);

        setProcesosRaw(lista);

        // metadata de paginación
        const total = Number.isFinite(dataPage?.totalElements)
          ? dataPage.totalElements
          : lista.length;

        setTotalElements(total);
        setPage(
          Number.isFinite(dataPage?.number) ? dataPage.number : safePage
        );
        setSize(
          Number.isFinite(dataPage?.size) ? dataPage.size : safeSize
        );
      } catch (err) {
        console.error("Error al cargar procesos:", err);
        setProcesosRaw([]);
        setMessage({
          open: true,
          severity: "error",
          text: "Error al cargar procesos.",
        });
      } finally {
        setLoading(false);
      }
    },
    [page, size]
  );

  // API pública para el formulario (crear/editar/eliminar)
  const reloadData = useCallback(async () => {
    await loadTipoProduccion();
    await loadProcesos(0, size); // cuando recargo después de crear, me voy a la página 0
    setPage(0);
    setMessage({
      open: true,
      severity: "success",
      text: "Datos actualizados.",
    });
  }, [loadTipoProduccion, loadProcesos, size]);

  // carga inicial
  useEffect(() => {
    (async () => {
      await loadTipoProduccion();
      await loadProcesos(page, size);
    })();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  // Enriquecer procesos con nombre del tipoProduccion
  const procesos = useMemo(() => {
    return (procesosRaw ?? []).map((p) => {
      const nameDirecto = p?.tipoProduccion?.name;
      const namePorMapa = tipoProduccionMap[String(p?.tipoProduccionId)];
      return {
        ...p,
        id: p?.id,
        tipoProduccionNombre: nameDirecto ?? namePorMapa ?? "",
      };
    });
  }, [procesosRaw, tipoProduccionMap]);

  // ---- handlers que usará el DataGrid para paginar ----
  const handleChangePage = (_evt, nextPage) => {
    setPage(nextPage);
    loadProcesos(nextPage, size);
  };

  const handleChangeRowsPerPage = (evt) => {
    const nextSize = parseInt(evt?.target?.value ?? evt, 10) || 10;
    setSize(nextSize);
    setPage(0);
    loadProcesos(0, nextSize);
  };

  return (
    <div>
      <h1>Procesos</h1>

      <MessageSnackBar message={message} setMessage={setMessage} />

      <FormProceso
        selectedRow={selectedRow}
        setSelectedRow={setSelectedRow}
        setMessage={setMessage}
        reloadData={reloadData}
      />

      <GridProceso
        // datos
        procesos={procesos}
        loading={loading}
        selectedRow={selectedRow}
        setSelectedRow={setSelectedRow}

        // paginación server-side (igual patrón que usamos en Producto.jsx / GridProducto.jsx estilo B)
        page={page}
        rowsPerPage={size}
        totalElements={totalElements}
        onPageChange={handleChangePage}
        onRowsPerPageChange={handleChangeRowsPerPage}
      />
    </div>
  );
}
