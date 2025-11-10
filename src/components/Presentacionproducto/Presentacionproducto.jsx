// src/components/Presentacionproducto.jsx
import React, { useEffect, useState, useCallback } from "react";
import axios from "../axiosConfig";
import MessageSnackBar from "../MessageSnackBar";
import FormPresentacionproducto from "./FormPresentacionproducto";
import GridPresentacionproducto from "./GridPresentacionproducto";

// Convierte respuesta en array: [], {content:[]}, {data:[]}
const toList = (data) =>
  Array.isArray(data)
    ? data
    : Array.isArray(data?.content)
    ? data.content
    : Array.isArray(data?.data)
    ? data.data
    : [];

// Obtiene un label robusto (nombre | name | descripcion | #id)
const labelOf = (obj) =>
  obj?.nombre ?? obj?.name ?? obj?.descripcion ?? `#${obj?.id ?? ""}`;

export default function Presentacionproducto() {
  const [selectedRow, setSelectedRow] = useState({});
  const [message, setMessage] = useState({ open: false, severity: "success", text: "" });
  const [rows, setRows] = useState([]);

  const [paginationModel, setPaginationModel] = useState({ page: 0, pageSize: 5 });
  const [sortModel, setSortModel] = useState([]);
  const [filterModel, setFilterModel] = useState({ items: [] });
  const [rowCount, setRowCount] = useState(0);
  const [loading, setLoading] = useState(false);

  const reloadData = useCallback(
    async (p = paginationModel.page, s = paginationModel.pageSize) => {
      try {
        setLoading(true);

        const [
          resListado,
          resProductos,
          resUnidades,
          resMarcas,
          resPresentaciones
        ] = await Promise.all([
          axios.get("/v1/producto_presentacion", { params: { page: p, size: s } }),
          axios.get("/v1/items/producto/0"),
          axios.get("/v1/items/unidad/0"),
          axios.get("/v1/items/marca/0"),
          axios.get("/v1/items/presentacion/0"),
        ]);

        const payloadListado  = resListado?.data ?? resListado;
        const listPresent     = toList(payloadListado);
        const listProductos   = toList(resProductos?.data ?? resProductos);
        const listUnidades    = toList(resUnidades?.data ?? resUnidades);
        const listMarcas      = toList(resMarcas?.data ?? resMarcas);
        const listTipoPres    = toList(resPresentaciones?.data ?? resPresentaciones);

        // Mapas id -> etiqueta robusta
        const productoMap = Object.fromEntries(listProductos.map(p => [p.id, labelOf(p)]));
        const unidadMap   = Object.fromEntries(listUnidades.map(u => [u.id, labelOf(u)]));
        const marcaMap    = Object.fromEntries(listMarcas.map(m => [m.id, labelOf(m)]));
        const tipoPresMap = Object.fromEntries(listTipoPres.map(t => [t.id, labelOf(t)]));

        // Enriquecer filas con nombres
        const filas = listPresent.map(item => ({
          ...item,
          productoNombre:     productoMap[item.productoId]     ?? "—",
          unidadNombre:       unidadMap[item.unidadId]         ?? "—",
          marcaNombre:        marcaMap[item.marcaId]           ?? "—",
          presentacionNombre: tipoPresMap[item.presentacionId] ?? "—",
        }));

        setRows(filas);

        // Total para paginación
        const total =
          payloadListado?.page?.totalElements ??
          payloadListado?.totalElements ??
          filas.length;

        setRowCount(Number(total));
      } catch (err) {
        console.error("❌ Error al cargar Producto–Presentación:", err);
        setMessage({ open: true, severity: "error", text: "Error al cargar Producto–Presentación." });
        setRows([]);
        setRowCount(0);
      } finally {
        setLoading(false);
      }
    },
    [paginationModel.page, paginationModel.pageSize]
  );

  useEffect(() => {
    reloadData(paginationModel.page, paginationModel.pageSize);
  }, [paginationModel.page, paginationModel.pageSize]);

  const handlePaginationChange = (newModel) => {
    setPaginationModel({
      page: newModel.page,
      pageSize: newModel.pageSize,
    });
  };

  return (
    <div>
      <h1>Producto Presentación</h1>

      <MessageSnackBar message={message} setMessage={setMessage} />

      <FormPresentacionproducto
        selectedRow={selectedRow}
        setSelectedRow={setSelectedRow}
        setMessage={setMessage}
        reloadData={() => reloadData(paginationModel.page, paginationModel.pageSize)}
      />

      <GridPresentacionproducto
        rows={rows}
        selectedRow={selectedRow}
        setSelectedRow={setSelectedRow}
        loading={loading}
        paginationModel={paginationModel}
        onPaginationModelChange={handlePaginationChange}
        sortModel={sortModel}
        setSortModel={setSortModel}
        filterModel={filterModel}
        setFilterModel={setFilterModel}
        rowCount={rowCount}
      />
    </div>
  );
}
