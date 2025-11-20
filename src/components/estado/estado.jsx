import React, { useState, useEffect, useCallback } from "react";
import axios from "../axiosConfig";
import MessageSnackBar from "../MessageSnackBar";
import FormEstado from "./FormEstado.jsx";
import GridEstado from "./GridEstado.jsx";

export default function Estado() {
  const [selectedRow, setSelectedRow] = useState({});
  const [message, setMessage] = useState({
    open: false,
    severity: "success",
    text: "",
  });

  const [rows, setRows] = useState([]);
  const [formOpen, setFormOpen] = useState(false);
  const [categorias, setCategorias] = useState([]);

  // 🔹 Traer TODOS los estados (sin /api, solo /v1/estado)
  const reloadData = useCallback(async () => {
    try {
      // Si el backend NO usa paginación, ignora estos params.
      // Si SÍ usa paginación (size=20 por defecto), con size grande traes todo.
      const res = await axios.get("/v1/estado", {
        params: { page: 0, size: 1000 },
      });

      const data = res?.data ?? [];
      const list = Array.isArray(data) ? data : data.content ?? [];

      console.log("Estados recibidos:", list.length);
      setRows(list);
    } catch (error) {
      console.error(error);
      setMessage({
        open: true,
        severity: "error",
        text: "Error al cargar estados",
      });
    }
  }, []);

  // 🔹 Categorías para el combo del formulario
  const loadCategorias = useCallback(async () => {
    try {
      const res = await axios.get("/v1/categoria-estado");
      const data = res?.data ?? [];
      const list = Array.isArray(data) ? data : data.content ?? [];
      setCategorias(list);
    } catch (error) {
      console.error(error);
    }
  }, []);

  useEffect(() => {
    reloadData();
    loadCategorias();
  }, [reloadData, loadCategorias]);

  return (
    <div>
      <h1>Estado</h1>

      <MessageSnackBar message={message} setMessage={setMessage} />

      <FormEstado
        open={formOpen}
        setOpen={setFormOpen}
        selectedRow={selectedRow || {}}
        setSelectedRow={setSelectedRow}
        setMessage={setMessage}
        reloadData={reloadData}
        categorias={categorias}
      />

      <GridEstado
        rows={rows}
        setSelectedRow={setSelectedRow}
      />
    </div>
  );
}
