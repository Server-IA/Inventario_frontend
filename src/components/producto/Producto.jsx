import React, { useState, useEffect, useCallback } from "react";
import axios from "../axiosConfig";
import MessageSnackBar from "../MessageSnackBar";
import FormProducto from "./FormProducto";
import GridProducto from "./GridProducto";

import { Box, Stack, Typography, Button } from "@mui/material";
import AddRounded from "@mui/icons-material/AddRounded";
import EditRounded from "@mui/icons-material/EditRounded";
import DeleteRounded from "@mui/icons-material/DeleteRounded";

export default function Producto() {
  const [selectedRow, setSelectedRow] = useState({});
  const [message, setMessage] = useState({ open: false, severity: "success", text: "" });

  const [productos, setProductos] = useState([]);
  const [loading, setLoading] = useState(false);

  const [formOpen, setFormOpen] = useState(false);
  const [editing, setEditing] = useState(null);

  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [rowCount, setRowCount] = useState(0);

  // === catálogos para los selects del form ===
  const [categorias, setCategorias] = useState([]);
  const [unidades, setUnidades] = useState([]);

  const loadCatalogos = useCallback(async () => {
    try {
      const [cat, uni] = await Promise.all([
        axios.get("/v1/items/producto_categoria/0"),
        axios.get("/v1/items/unidad/0"),
      ]);
      setCategorias(cat.data ?? []);
      setUnidades(uni.data ?? []);
    } catch (e) {
      setMessage({
        open: true,
        severity: "error",
        text: `Error cargando catálogos: ${e?.response?.data?.message ?? e.message}`,
      });
    }
  }, []);

  const reloadData = useCallback(
    async (p = page, s = pageSize) => {
      try {
        setLoading(true);
        const res = await axios.get("/v2/productos", {
          params: { page: p, size: s, sort: "id,desc" },
        });
        const data = res?.data ?? {};
        const list = Array.isArray(data) ? data : data.content ?? [];

        const filas = list.map((it) => ({
          ...it,
          productoCategoriaNombre: it.productoCategoriaNombre ?? "",
          estadoNombre: it.estadoNombre ?? "",
          unidadMinimaNombre: it.unidadMinimaNombre ?? "",
        }));
        setProductos(filas);

        if (!Array.isArray(data)) {
          setRowCount(Number(data.totalElements ?? filas.length));
          setPage(Number(data.number ?? p));
          setPageSize(Number(data.size ?? s));
        } else {
          setRowCount(filas.length);
        }
      } catch (e) {
        setMessage({
          open: true,
          severity: "error",
          text: `Error al cargar productos: ${e?.response?.data?.message ?? e.message}`,
        });
      } finally {
        setLoading(false);
      }
    },
    [page, pageSize]
  );

  useEffect(() => {
    loadCatalogos();
    reloadData(0, pageSize);
  }, [loadCatalogos, reloadData, pageSize]);

  // === acciones ===
  const handleCreate = () => {
    setEditing(null);
    setFormOpen(true);
  };

  const handleEdit = () => {
    if (!selectedRow?.id) {
      setMessage({ open: true, severity: "warning", text: "Selecciona un producto para editar." });
      return;
    }
    setEditing(selectedRow);
    setFormOpen(true);
  };

  const handleDelete = async () => {
    if (!selectedRow?.id) return;
    try {
      await axios.delete(`/v2/productos/${selectedRow.id}`);
      setMessage({ open: true, severity: "success", text: "Producto eliminado" });
      setSelectedRow({});
      reloadData(page, pageSize);
    } catch (e) {
      setMessage({
        open: true,
        severity: "error",
        text: e?.response?.data?.message ?? e.message,
      });
    }
  };

  const submitForm = async (payload) => {
    try {
      if (payload.id) {
        await axios.put(`/v2/productos/${payload.id}`, payload);
        setMessage({ open: true, severity: "success", text: "Producto actualizado" });
      } else {
        await axios.post(`/v2/productos`, payload);
        setMessage({ open: true, severity: "success", text: "Producto creado" });
      }
      setFormOpen(false);
      setSelectedRow({});
      reloadData(page, pageSize);
    } catch (e) {
      setMessage({
        open: true,
        severity: "error",
        text: e?.response?.data?.message ?? e.message,
      });
    }
  };

  return (
    <Box>
      {/* ===== TÍTULO + BOTONERA (como Producción) ===== */}
      <Stack direction="row" alignItems="center" sx={{ mb: 2 }}>
        <h1>Productos</h1>
        <Box sx={{ flex: 1 }} />
        <Button
          onClick={handleCreate}
          startIcon={<AddRounded />}
          sx={(t) => ({
            borderRadius: 999,
            px: 2.2,
            py: 0.9,
            fontWeight: 800,
            textTransform: "uppercase",
            bgcolor: t.palette.background.paper,
            color: t.palette.text.primary,
            boxShadow: 2,
            border: `1px solid ${t.palette.divider}`,
            "&:hover": { boxShadow: 3, bgcolor: t.palette.background.paper },
            mr: 1,
          })}
        >
          Crear
        </Button>
        <Button
          onClick={handleEdit}
          startIcon={<EditRounded />}
          disabled={!selectedRow?.id}
          sx={(t) => ({
            borderRadius: 999,
            px: 2,
            py: 0.9,
            fontWeight: 800,
            textTransform: "uppercase",
            border: `1px solid ${t.palette.divider}`,
            color: t.palette.text.secondary,
            mr: 1,
            "&.Mui-disabled": { opacity: 0.4 },
          })}
        >
          Editar
        </Button>
        <Button
          onClick={handleDelete}
          startIcon={<DeleteRounded />}
          disabled={!selectedRow?.id}
          sx={(t) => ({
            borderRadius: 999,
            px: 2,
            py: 0.9,
            fontWeight: 800,
            textTransform: "uppercase",
            border: `1px solid ${t.palette.divider}`,
            color: t.palette.text.secondary,
            "&.Mui-disabled": { opacity: 0.4 },
          })}
        >
          Eliminar
        </Button>
      </Stack>

      {/* ===== GRID ===== */}
      <GridProducto
        rows={productos}
        selectedRow={selectedRow}
        setSelectedRow={setSelectedRow}
        loading={loading}
        paginationModel={{ page, pageSize }}
        setPaginationModel={({ page: p, size: s }) => {
          setPage(p);
          setPageSize(s);
          reloadData(p, s);
        }}
        rowCount={rowCount}
      />

      {/* ===== FORM ===== */}
      <FormProducto
        open={formOpen}
        onClose={() => setFormOpen(false)}
        initialData={editing ?? undefined}
        onSubmit={submitForm}
        categorias={categorias}
        unidades={unidades}
      />

      <MessageSnackBar message={message} setMessage={setMessage} />
    </Box>
  );
}
