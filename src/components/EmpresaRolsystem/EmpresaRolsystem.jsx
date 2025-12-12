// src/components/empresaRolSystem/EmpresaRolsystem.jsx
import React, { useState, useEffect, useCallback } from "react";
import axios from "../axiosConfig.js";
import MessageSnackBar from "../MessageSnackBar.jsx";
import FormEmpresaRol from "./FormEmpresaRolsystem.jsx";
import GridEmpresaRol from "./GridEmpresaRolsystem.jsx";

export default function EmpresaRolsystem() {
  const [selectedRow, setSelectedRow] = useState(null);

  const [message, setMessage] = useState({
    open: false,
    severity: "success",
    text: "",
  });

  const [rows, setRows] = useState([]);
  const [formOpen, setFormOpen] = useState(false);
  const [loading, setLoading] = useState(false);

  // combos
  const [empresas, setEmpresas] = useState([]);
  const [roles, setRoles] = useState([]);

  /** Carga de la tabla principal */
  const reloadData = useCallback(async () => {
    try {
      setLoading(true);
      const res = await axios.get("/v1/system/empresa-rol");
      const list = Array.isArray(res?.data) ? res.data : [];
      setRows(list);
    } catch (error) {
      console.error(error);
      setMessage({
        open: true,
        severity: "error",
        text: "Error al cargar Empresa-Rol",
      });
    } finally {
      setLoading(false);
    }
  }, []);

  /** Carga de combos (items empresa / rol) */
  const loadItems = useCallback(async () => {
    try {
      const [empRes, rolRes] = await Promise.all([
        axios.get("/v1/items/empresa/0"),
        axios.get("/v1/items/rol/0"),
      ]);

      setEmpresas(Array.isArray(empRes?.data) ? empRes.data : []);
      setRoles(Array.isArray(rolRes?.data) ? rolRes.data : []);
    } catch (err) {
      console.error(err);
      setMessage((prev) => ({
        ...prev,
        open: true,
        severity: "error",
        text: "Error al cargar listas de empresa y rol",
      }));
    }
  }, []);

  useEffect(() => {
    reloadData();
    loadItems();
  }, [reloadData, loadItems]);

  return (
    <div>
      <h1>Gestión Empresa-Rol (System)</h1>

      <MessageSnackBar message={message} setMessage={setMessage} />

      <FormEmpresaRol
        open={formOpen}
        setOpen={setFormOpen}
        selectedRow={selectedRow}
        setSelectedRow={setSelectedRow}
        setMessage={setMessage}
        reloadData={reloadData}
        empresas={empresas}
        roles={roles}
      />

      <GridEmpresaRol
        rows={rows}
        loading={loading}
        selectedRow={selectedRow}
        setSelectedRow={setSelectedRow}
      />
    </div>
  );
}
