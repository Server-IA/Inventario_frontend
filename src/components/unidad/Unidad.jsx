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
     2. reloadData:
        - Forzamos al backend a darnos hasta 1000 filas
        - Adaptamos cada unidad para que el grid tenga todos los campos listos
        - Si el backend SOLO manda un string tipoUnidad ("peso"), lo usamos
     ========================================================================= */
  const reloadData = useCallback(() => {
    setLoading(true);

    axios
      .get("/v1/unidad", {
        params: {
          page: 0,
          size: 1000,
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
          /* --------- 1) ID crudo (si lo hay) --------- */
          let tipoIdCrudo =
            u?.tipoUnidadId ??
            u?.tipoUnidad?.id ??
            u?.tipo_unidad_id ??
            "";

          /* --------- 2) Nombre crudo (casos posibles) --------- */
          let tipoNombreCrudo = "";

          if (typeof u?.tipoUnidad === "string") {
            // caso: tipoUnidad: "peso"
            tipoNombreCrudo = u.tipoUnidad;
          } else {
            tipoNombreCrudo =
              u?.tipoUnidadNombre ??
              u?.tipoUnidad?.nombre ??
              u?.tipoUnidad?.name ??
              u?.tipo_unidad_nombre ??
              "";
          }

          /* --------- 3) Si no vino ID pero sí nombre, lo buscamo en el catálogo --------- */
          if (!tipoIdCrudo && tipoNombreCrudo && tiposUnidad.length > 0) {
            const matchPorNombre = tiposUnidad.find(
              (tu) =>
                String(tu.nombre || "").toLowerCase().trim() ===
                String(tipoNombreCrudo).toLowerCase().trim()
            );
            if (matchPorNombre) {
              tipoIdCrudo = matchPorNombre.id;
            }
          }

          /* --------- 4) Nombre bonito para mostrar en la grid --------- */
          const matchTipoPorId =
            tipoIdCrudo &&
            tiposUnidad.find((tu) => String(tu.id) === String(tipoIdCrudo));

          const tipoNombreBonito =
            matchTipoPorId?.nombre ||
            tipoNombreCrudo || // si el backend solo manda el string, usamos ese
            (tipoIdCrudo !== "" ? String(tipoIdCrudo) : "—");

          /* --------- 5) Estado legible --------- */
          const estadoIdCrudo = u?.estadoId ?? u?.estado?.id ?? "";
          const estadoNombre =
            u?.estadoNombre ??
            u?.estado?.nombre ??
            u?.estado?.name ??
            (String(estadoIdCrudo) === "1" ? "Activo" : "Inactivo");

          /* --------- 6) Objeto final que verá el grid y el formulario --------- */
          return {
            ...u,
            tipoUnidadId: tipoIdCrudo,          // para el <Select> al actualizar
            tipoUnidadNombre: tipoNombreBonito, // para la columna "Tipo de unidad"
            estadoId: estadoIdCrudo,
            estadoNombre,
          };
        });

        setUnidades(adaptadas);
        // limpiamos selección
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
  }, [tiposUnidad]);

  /* =========================================================================
     3. Cargar unidades cuando ya tengamos el catálogo
     ========================================================================= */
  useEffect(() => {
    // si por alguna razón el backend NO depende del catálogo,
    // igual podrías llamar reloadData incluso con lista vacía
    reloadData();
  }, [reloadData]);

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
        reloadData={reloadData}
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
