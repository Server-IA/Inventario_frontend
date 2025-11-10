import React, { useState, useEffect, useCallback } from "react";
import axios from "../axiosConfig";
import MessageSnackBar from "../MessageSnackBar";
import FormUnidad from "./FormUnidad";
import GridUnidad from "./GridUnidad";

export default function Unidad() {
  const [selectedRow, setSelectedRow] = useState({ id: 0 });
  const [message, setMessage] = useState({
    open: false,
    severity: "success",
    text: "",
  });

  // catálogo para pintar nombres (tipos de unidad)
  const [tiposUnidad, setTiposUnidad] = useState([]);

  // filas finales que van al grid
  const [unidades, setUnidades] = useState([]);

  // loading para el grid
  const [loading, setLoading] = useState(false);

  /* =========================================================================
     1. Cargar catálogo de tipos de unidad una sola vez
     ========================================================================= */
  useEffect(() => {
    const token = localStorage.getItem("token");
    const headers = { headers: { Authorization: `Bearer ${token}` } };

    axios
      .get("/v1/items/tipo_unidad/0", headers)
      .then((res) => {
        const list = Array.isArray(res.data?.content)
          ? res.data.content
          : Array.isArray(res.data)
          ? res.data
          : [];

        const normalizados = list.map((t) => ({
          id: t.id,
          nombre: t.nombre || t.name || "",
        }));

        setTiposUnidad(normalizados);
      })
      .catch((err) => {
        console.error("Error cargando tipos de unidad", err);
        setMessage({
          open: true,
          severity: "error",
          text: "Error al cargar Tipos de Unidad",
        });
      });
  }, []);

  /* =========================================================================
     2. Definimos reloadData con useCallback (ESTABLE)
        - Forzamos al backend a darnos hasta 1000 filas (page=0&size=1000)
        - Adaptamos cada unidad para que el grid tenga todos los campos listos
     ========================================================================= */
  const reloadData = useCallback(() => {
    setLoading(true);

    axios
      .get("/v1/unidad", {
        params: {
          page: 0,
          size: 1000, // <- fuerza traer TODO en una sola llamada grande
        },
      })
      .then((res) => {
        const data = res.data ?? {};
        const rawList = Array.isArray(data?.content)
          ? data.content
          : Array.isArray(data?.data)
          ? data.data
          : Array.isArray(data)
          ? data
          : [];

        const adaptadas = rawList.map((u) => {
          // detectar el tipo de unidad de la fila
          const tipoIdCrudo =
            u?.tipoUnidadId ??
            u?.tipoUnidad?.id ??
            u?.tipo_unidad_id ??
            "";

          // buscar el nombre legible en el catálogo
          const matchTipo = tiposUnidad.find(
            (tu) => String(tu.id) === String(tipoIdCrudo)
          );

          // armar texto bonito para la columna "Tipo de unidad"
          const tipoNombreBonito =
            matchTipo?.nombre ||
            u?.tipoUnidad?.nombre ||
            u?.tipoUnidad?.name ||
            u?.tipo_unidad_nombre ||
            (tipoIdCrudo !== "" ? String(tipoIdCrudo) : "—");

          // estado legible
          const estadoNombre =
            u?.estado?.nombre ??
            u?.estado?.name ??
            (String(u?.estadoId) === "1" ? "Activo" : "Inactivo");

          return {
            ...u,
            tipoUnidadId: tipoIdCrudo,
            tipoUnidadNombre: tipoNombreBonito,
            estadoId: u?.estado?.id ?? u?.estadoId,
            estadoNombre,
          };
        });

        setUnidades(adaptadas);
        // limpiamos selección para que botones tipo "Actualizar" no queden colgados en una fila vieja
        setSelectedRow({ id: 0 });
      })
      .catch((err) => {
        console.error("Error al cargar unidades", err);
        setMessage({
          open: true,
          severity: "error",
          text: "Error al cargar unidades",
        });
        setUnidades([]);
      })
      .finally(() => {
        setLoading(false);
      });
  }, [tiposUnidad]); // <- depende solo del catálogo. cuando cambie catálogo, esta versión se actualiza

  /* =========================================================================
     3. Cargar unidades cuando ya tengamos el catálogo de tiposUnidad
        y cada vez que el catálogo cambie (por si cambia orden, etc.)
     ========================================================================= */
  useEffect(() => {
    if (tiposUnidad.length > 0) {
      reloadData();
    }
  }, [tiposUnidad, reloadData]);

  /* =========================================================================
     4. Render
     ========================================================================= */
  return (
    <div>
      <h1>Unidades</h1>

      <MessageSnackBar message={message} setMessage={setMessage} />

      <FormUnidad
        selectedRow={selectedRow || { id: 0 }}
        setSelectedRow={setSelectedRow}
        setMessage={setMessage}
        reloadData={reloadData} // <- ahora es una función estable gracias a useCallback
      />

      <GridUnidad
        rows={unidades}
        loading={loading}
        selectedRow={selectedRow}
        setSelectedRow={(row) => {
          setSelectedRow(row);
        }}
      />
    </div>
  );
}
