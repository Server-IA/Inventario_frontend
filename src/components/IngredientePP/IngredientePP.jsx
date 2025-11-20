import React, { useState, useEffect, useMemo, useCallback } from "react";
import axios from "../axiosConfig";
import MessageSnackBar from "../MessageSnackBar.jsx";
import FormIngredientePresentacionP from "./FormIngredientePresentacionP.jsx";
import GridIngredientePresentacionP from "./GridIngredientePresentacionP.jsx";
import { Box, Typography, Button } from "@mui/material";
import AddIcon from "@mui/icons-material/Add";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";

export default function IngredientePresentacionProducto() {
  const [selectedRow, setSelectedRow] = useState(null);
  const [message, setMessage] = useState({
    open: false,
    severity: "success",
    text: "",
  });

  // filas crudas que vienen del backend (content)
  const [datos, setDatos] = useState([]);

  const [formOpen, setFormOpen] = useState(false);

  // paginación (según el changelog: ?page=0&size=10)
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);
  const [totalElements, setTotalElements] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(false);

  // ================== RELOAD DATA (según doc) ==================
  const reloadData = useCallback(
    async (pageArg = page, sizeArg = size) => {
      try {
        setLoading(true);

        const pageQ = Math.max(0, Number.isFinite(+pageArg) ? +pageArg : 0);
        const sizeQ = Math.max(1, Number.isFinite(+sizeArg) ? +sizeArg : 10);

        // 👉 según documento: /api/v1/ingrediente-presentacion-producto?page=&size=
        const res = await axios.get(
          "/v1/ingrediente-presentacion-producto",
          { params: { page: pageQ, size: sizeQ } }
        );

        const pagePayload = res?.data ?? {};
        const lista = Array.isArray(pagePayload.content)
          ? pagePayload.content
          : [];

        setDatos(lista);

        const totalElems = Number.isFinite(+pagePayload.totalElements)
          ? +pagePayload.totalElements
          : lista.length;

        const totalPgs = Number.isFinite(+pagePayload.totalPages)
          ? +pagePayload.totalPages
          : Math.max(1, Math.ceil(totalElems / Math.max(1, sizeQ)));

        setPage(Number.isFinite(+pagePayload.number) ? +pagePayload.number : pageQ);
        setSize(sizeQ);
        setTotalElements(totalElems);
        setTotalPages(totalPgs);
      } catch (err) {
        const status = err?.response?.status;
        const body = err?.response?.data;
        try {
          console.error(
            "Error /api/v1/ingrediente-presentacion-producto",
            status,
            JSON.stringify(body)
          );
        } catch {
          console.error(
            "Error /api/v1/ingrediente-presentacion-producto",
            status,
            body
          );
        }
        setMessage({
          open: true,
          severity: "error",
          text: `Error al cargar datos${
            status ? ` (HTTP ${status})` : ""
          }`,
        });
        setDatos([]);
      } finally {
        setLoading(false);
      }
    },
    [page, size]
  );

  useEffect(() => {
    // carga inicial respetando la paginación del doc
    reloadData(0, size);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [size]);

  // ================== NORMALIZAR FILAS (según DTO del doc) ==================
  // El doc dice que cada item viene así:
  // {
  //   "idIngredientePresentacionProducto": 12,
  //   "nombreProducto": "Leche Entera",
  //   "idPresentacionProducto": 5,
  //   "nombrePresentacionProducto": "Caja 1L",
  //   "ingrediente": {
  //     "idIngrediente": 3,
  //     "nombreIngrediente": "Leche",
  //     "cantidad": 1.0,
  //     "idUnidad": 2,
  //     "nombreUnidad": "Litro",
  //     "idEstado": 1,
  //     "nombreEstado": "Activo"
  //   }
  // }
  //
  // De aquí sacamos todos los campos que el grid y el form necesitan.
  const rowsConJoin = useMemo(() => {
    return (datos || []).map((r) => {
      const ing = r.ingrediente ?? {};

      const idIngredientePresentacionProducto =
        r.idIngredientePresentacionProducto ?? r.id ?? null;

      const ingredienteId =
        r.ingredienteId ??
        ing.idIngrediente ??
        null;

      const presentacionProductoId =
        r.presentacionProductoId ??
        r.idPresentacionProducto ??
        null;

      const unidadId =
        r.unidadId ??
        ing.idUnidad ??
        null;

      const estadoId =
        r.estadoId ??
        ing.idEstado ??
        null;

      const cantidad =
        r.cantidad ??
        ing.cantidad ??
        null;

      const ingredienteNombre =
        r.ingredienteNombre ??
        ing.nombreIngrediente ??
        "";

      const presentacionNombre =
        r.presentacionNombre ??
        r.nombrePresentacionProducto ??
        "";

      const unidadNombre =
        r.unidadNombre ??
        ing.nombreUnidad ??
        "";

      const estadoNombre =
        r.estadoNombre ??
        ing.nombreEstado ??
        (String(estadoId) === "1" ? "Activo" : "Inactivo");

      const nombreProducto =
        r.nombreProducto ??
        r.productoNombre ??
        "";

      return {
        ...r,
        // id normalizado para DataGrid
        id: idIngredientePresentacionProducto,
        idIngredientePresentacionProducto,

        // para el form
        ingredienteId,
        presentacionProductoId,
        unidadId,
        estadoId,
        cantidad,

        // para el grid (nombres legibles)
        ingredienteNombre,
        presentacionNombre,
        unidadNombre,
        estadoNombre,
        nombreProducto,
      };
    });
  }, [datos]);

  // ================== Handlers de paginación (server-side) ==================
  const handleChangePage = (_evt, nextPage) => {
    setPage(nextPage);
    reloadData(nextPage, size);
  };

  const handleChangeRowsPerPage = (evt) => {
    const nextSize = parseInt(evt?.target?.value ?? evt, 10) || 10;
    setSize(nextSize);
    setPage(0);
    reloadData(0, nextSize);
  };

  // ================== Botones CRUD ==================
  const openCreate = () => {
    setSelectedRow(null);
    setFormOpen(true);
  };

  const openEdit = () => {
    if (!selectedRow) return;
    setFormOpen(true);
  };

  const handleDelete = async () => {
    if (!selectedRow) return;
    const id =
      selectedRow.idIngredientePresentacionProducto ?? selectedRow.id;
    if (!id) return;

    try {
      await axios.delete(
        `/v1/ingrediente-presentacion-producto/${id}`
      );
      setMessage({
        open: true,
        severity: "success",
        text: "Registro eliminado correctamente (estado INACTIVO)",
      });
      reloadData(page, size);
      setSelectedRow(null);
    } catch (err) {
      console.error("Error al eliminar Ingrediente–Presentación:", err);
      setMessage({
        open: true,
        severity: "error",
        text: "Error al eliminar el registro",
      });
    }
  };

  return (
    <Box sx={{ p: 2 }}>
      <Typography variant="h4" gutterBottom>
        Ingrediente – Presentación de Producto
      </Typography>

      {/* Botones arriba igual que Producción */}
      <Box sx={{ mb: 2, display: "flex", gap: 1, justifyContent: "flex-end" }}>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={openCreate}
        >
          Crear
        </Button>
        <Button
          variant="outlined"
          startIcon={<EditIcon />}
          disabled={!selectedRow}
          onClick={openEdit}
        >
          Editar
        </Button>
        <Button
          variant="outlined"
          color="error"
          startIcon={<DeleteIcon />}
          disabled={!selectedRow}
          onClick={handleDelete}
        >
          Eliminar
        </Button>
      </Box>

      <GridIngredientePresentacionP
        rows={rowsConJoin}
        selectedRow={selectedRow}
        setSelectedRow={setSelectedRow}
        loading={loading}
        // paginación estilo B (igual que GridProduccion)
        page={page}
        rowsPerPage={size}
        totalElements={totalElements}
        onPageChange={handleChangePage}
        onRowsPerPageChange={handleChangeRowsPerPage}
      />

      <FormIngredientePresentacionP
        open={formOpen}
        setOpen={setFormOpen}
        selectedRow={selectedRow}
        setSelectedRow={setSelectedRow}
        setMessage={setMessage}
        // importante: recargar usando la API paginada del doc
        reloadData={() => reloadData(page, size)}
      />

      <MessageSnackBar message={message} setMessage={setMessage} />
    </Box>
  );
}
