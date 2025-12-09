import React, { useState, useEffect, useCallback } from "react";
import axios from "../axiosConfig.js";
import MessageSnackBar from "../MessageSnackBar.jsx";
import FormEmpresaRol from "./FormEmpresaRolsystem.jsx";
import GridEmpresaRol from "./GridEmpresaRolsystem.jsx";

export default function EmpresaRolsystem() {
  const [selectedRow, setSelectedRow] = useState({});
  const [message, setMessage] = useState({
    open: false,
    severity: "success",
    text: "",
  });
  const [rows, setRows] = useState([]);
  const [formOpen, setFormOpen] = useState(false);
  const [loading, setLoading] = useState(false);

  // Si más adelante quieres combos de empresa y rol,
  // aquí podrías cargarlos y pasarlos como props al formulario.
  const [empresas, setEmpresas] = useState([]);
  const [roles, setRoles] = useState([]);

  const reloadData = useCallback(async () => {
    try {
      setLoading(true);

      // GET empresas-rol
      const res = await axios.get("v1/system/empresa-rol");
      const list = Array.isArray(res?.data) ? res.data : [];

      console.log("Empresa-Rol recibidos:", list.length);
      setRows(list);
    } catch (error) {
      console.error(error);
      setMessage({
        open: true,
        severity: "error",
        text: "Error al cargar empresa-rol",
      });
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    reloadData();
  }, [reloadData]);

  return (
    <div>
      <h1>Gestión Empresa-Rol</h1>

      <MessageSnackBar message={message} setMessage={setMessage} />

      <FormEmpresaRol
        open={formOpen}
        setOpen={setFormOpen}
        selectedRow={selectedRow || {}}
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
