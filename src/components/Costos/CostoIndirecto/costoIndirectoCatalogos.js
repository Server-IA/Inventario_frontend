/*=============================================================================
 Nombre del archivo : costoIndirectoCatalogos.js
 Descripcion        : Carga de catálogos activos (tipo de costo y ubicación en
                      cascada) para el formulario de costo indirecto.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |   Fecha    | Versión |      Autor           | Descripción del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2026-09-25 | 0.4.0   | Arekkazu             | Creación del archivo para   |
 |            |         |                      | la HU-045.1.                |
 +------------+---------+----------------------+-----------------------------+
=============================================================================*/
/**
 * @module costoIndirectoCatalogos
 * @description Loaders que devuelven opciones `{ id, label }` solo con
 * registros activos (estadoId 1), como exige la HU-045.1.
 */
import axios from "../../axiosConfig";
import { unwrap } from "../../common/filtersLoaders";

const ACTIVO = 1;

const activos = (lista) => lista.filter((item) => Number(item.estadoId) === ACTIVO);
const toOption = (item) => ({ id: item.id, label: item.nombre });

// ponytail: el backend no filtra por padre y devuelve páginas de 10; se pide
// una página grande y se filtra aquí, igual que common/filtersLoaders.js.
// Cambiar por filtros de backend cuando existan.
const listar = async (ruta, params) => {
  const { data } = await axios.get(ruta, { params: { page: 0, size: 2000, ...params } });
  return unwrap(data);
};

/** Tipos de costo indirecto activos. La respuesta viene como `{ header, data }`. */
export const getTipos = async () => {
  const { data } = await axios.get("/v1/tipos-costos-indirectos", {
    params: { estadoId: ACTIVO, size: 100 },
  });
  return activos(data?.data ?? []).map(toOption);
};

/** Sedes activas de la empresa de la sesión (el backend ya filtra por empresa). */
export const getSedes = async () => activos(await listar("/v1/sede")).map(toOption);

const hijosActivos = (ruta, campoPadre) => async (padreId) => {
  const hijos = await listar(ruta, { [campoPadre]: padreId });
  return activos(hijos)
    .filter((hijo) => String(hijo[campoPadre]) === String(padreId))
    .map(toOption);
};

export const getBloques = hijosActivos("/v1/bloque", "sedeId");
export const getEspacios = hijosActivos("/v1/espacio", "bloqueId");
export const getAlmacenes = hijosActivos("/v1/almacen", "espacioId");
