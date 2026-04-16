import React, { useState, useEffect, useCallback } from "react";
import axios from "../axiosConfig";
import MessageSnackBar from "../MessageSnackBar";
import FormCategoriaEstado from "./FormCategoriaEstado.jsx";
import GridCategoriaEstado from "./GridCategoriaEstado.jsx";
import GridActionBar from "../common/GridActionBar";
import SectionHeader from "../common/SectionHeader";

export default function CategoriaEstado() {
  const [selectedRow, setSelectedRow] = useState(null);
  const [message, setMessage] = useState({
    open: false,
    severity: "success",
    text: "",
  });
  const [rows, setRows] = useState([]);
  const [formOpen, setFormOpen] = useState(false);

  const reloadData = useCallback(async () => {
    try {
      const res = await axios.get("/v1/categoria-estado");
      const data = res?.data ?? [];
      const list = Array.isArray(data) ? data : data.content ?? [];
      setRows(list);
    } catch {
      setMessage({
        open: true,
        severity: "error",
        text: "Error al cargar categorias",
      });
    }
  }, []);

  useEffect(() => {
    reloadData();
  }, [reloadData]);

  const handleOpenCreate = () => {
    setSelectedRow(null);
    setFormOpen(true);
  };

  const handleOpenUpdate = () => {
    if (!selectedRow?.id) {
      setMessage({ open: true, severity: "warning", text: "Selecciona un registro" });
      return;
    }
    setFormOpen(true);
  };

  const handleDelete = async () => {
    if (!selectedRow?.id) {
      setMessage({
        open: true,
        severity: "warning",
        text: "Selecciona un registro para eliminar",
      });
      return;
    }

    if (!window.confirm(`¿Eliminar "${selectedRow.nombre}"?`)) return;

    try {
      await axios.delete(`/v1/categoria-estado/${selectedRow.id}`);
      setMessage({ open: true, severity: "success", text: "Eliminado" });
      setSelectedRow(null);
      reloadData();
    } catch {
      setMessage({ open: true, severity: "error", text: "No se pudo eliminar" });
    }
  };

  return (
    <div>
      <SectionHeader title="Categoria Estado" />

      <MessageSnackBar message={message} setMessage={setMessage} />

      <GridActionBar
        onAdd={handleOpenCreate}
        onUpdate={handleOpenUpdate}
        onDelete={handleDelete}
        canUpdate={Boolean(selectedRow?.id)}
        canDelete={Boolean(selectedRow?.id)}
      />

      <FormCategoriaEstado
        open={formOpen}
        setOpen={setFormOpen}
        selectedRow={selectedRow}
        setSelectedRow={setSelectedRow}
        setMessage={setMessage}
        reloadData={reloadData}
      />

      <GridCategoriaEstado
        rows={rows}
        selectedRow={selectedRow}
        setSelectedRow={setSelectedRow}
      />
    </div>
  );
}
