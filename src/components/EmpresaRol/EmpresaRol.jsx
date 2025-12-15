// src/components/EmpresaRol/EmpresaRol.jsx
import React, { useState, useEffect, useCallback } from "react";
import axios from "../axiosConfig";
import MessageSnackBar from "../MessageSnackBar.jsx";
import FormEmpresaRol from "./FormEmpresaRol.jsx";
import GridEmpresaRol from "./GridEmpresaRol.jsx";

export default function EmpresaRol() {
  const [selectedRow, setSelectedRow] = useState(null);

  const [message, setMessage] = useState({
    open: false,
    severity: "success",
    text: "",
  });

  const [rows, setRows] = useState([]);
  const [formOpen, setFormOpen] = useState(false);
  const [loading, setLoading] = useState(false);

  // combo roles
  const [roles, setRoles] = useState([]);

  /** Carga de la tabla principal */
  const reloadData = useCallback(async () => {
    try {
      setLoading(true);
      const res = await axios.get("/v1/empresa-rol", {
        params: { page: 0, size: 1000 },
      });

      const list = Array.isArray(res?.data)
        ? res.data
        : Array.isArray(res?.data?.content)
        ? res.data.content
        : [];

      setRows(list);
    } catch (error) {
      console.error(error);
      setRows([]);
      setMessage({
        open: true,
        severity: "error",
        text: "Error al cargar Empresa-Rol",
      });
    } finally {
      setLoading(false);
    }
  }, []);

  /** Carga de combos (items rol) */
  const loadItems = useCallback(async () => {
    try {
      const rolRes = await axios.get("/v1/items/rol/0");
      setRoles(Array.isArray(rolRes?.data) ? rolRes.data : []);
    } catch (err) {
      console.error(err);
      setRoles([]);
      setMessage((prev) => ({
        ...prev,
        open: true,
        severity: "error",
        text: "Error al cargar lista de roles",
      }));
    }
  }, []);

  useEffect(() => {
    reloadData();
    loadItems();
  }, [reloadData, loadItems]);

  return (
    <div>
      <h1>Gestión Rol </h1>
      <MessageSnackBar message={message} setMessage={setMessage} />

      <FormEmpresaRol
        open={formOpen}
        setOpen={setFormOpen}
        selectedRow={selectedRow}
        setSelectedRow={setSelectedRow}
        setMessage={setMessage}
        reloadData={reloadData}
        roles={roles}
      />

      <GridEmpresaRol
        rows={rows}
        loading={loading}
        selectedRow={selectedRow}
        setSelectedRow={setSelectedRow}
        setOpenForm={setFormOpen}
        setMessage={setMessage}
        reloadData={reloadData}
      />
    </div>
  );
}
