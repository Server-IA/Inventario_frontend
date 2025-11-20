// src/components/Producto/Producto.jsx
import React, { useState, useEffect, useCallback } from "react";
import axios from "../axiosConfig";
import MessageSnackBar from "../MessageSnackBar";
import FormProducto from "./FormProducto";
import GridProducto from "./GridProducto";

import { Box, Stack, Button } from "@mui/material";
import AddRounded from "@mui/icons-material/AddRounded";
import EditRounded from "@mui/icons-material/EditRounded";
import DeleteRounded from "@mui/icons-material/DeleteRounded";

export default function Producto() {
  const [selectedRow, setSelectedRow] = useState({});
  const [message, setMessage] = useState({
    open: false,
    severity: "success",
    text: "",
  });

  const [productos, setProductos] = useState([]);
  const [loading, setLoading] = useState(false);

  const [formOpen, setFormOpen] = useState(false);
  const [editing, setEditing] = useState(null);

  // catálogos para selects del formulario
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
        text: `Error cargando catálogos: ${
          e?.response?.data?.message ?? e.message
        }`,
      });
    }
  }, []);

  const loadProductos = useCallback(async () => {
    try {
      setLoading(true);

      // pedimos hasta 1000 registros para tener todo y paginar en cliente
      const res = await axios.get("/v2/productos", {
        params: { size: 1000 },
      });

      const data = res?.data ?? [];
      let list;

      if (Array.isArray(data)) {
        list = data;
      } else if (Array.isArray(data.content)) {
        list = data.content;
      } else if (Array.isArray(data.data)) {
        list = data.data;
      } else {
        list = [];
      }

      const filas = list.map((it) => ({
        ...it,
        productoCategoriaNombre: it.productoCategoriaNombre ?? "",
        estadoNombre: it.estadoNombre ?? "",
        unidadMinimaNombre: it.unidadMinimaNombre ?? "",
      }));

      setProductos(filas);
    } catch (e) {
      setMessage({
        open: true,
        severity: "error",
        text: `Error al cargar productos: ${
          e?.response?.data?.message ?? e.message
        }`,
      });
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadCatalogos();
    loadProductos();
  }, [loadCatalogos, loadProductos]);

  // === acciones ===
  const handleCreate = () => {
    setEditing(null);
    setFormOpen(true);
  };

  const handleEdit = () => {
    if (!selectedRow?.id) {
      setMessage({
        open: true,
        severity: "warning",
        text: "Selecciona un producto para editar.",
      });
      return;
    }
    setEditing(selectedRow);
    setFormOpen(true);
  };

  const handleDelete = async () => {
    if (!selectedRow?.id) return;
    try {
      await axios.delete(`/v2/productos/${selectedRow.id}`);
      setMessage({
        open: true,
        severity: "success",
        text: "Producto eliminado",
      });
      setSelectedRow({});
      loadProductos();
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
        setMessage({
          open: true,
          severity: "success",
          text: "Producto actualizado",
        });
      } else {
        await axios.post(`/v2/productos`, payload);
        setMessage({
          open: true,
          severity: "success",
          text: "Producto creado",
        });
      }
      setFormOpen(false);
      setSelectedRow({});
      loadProductos();
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
      {/* ===== TÍTULO + BOTONERA ===== */}
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
