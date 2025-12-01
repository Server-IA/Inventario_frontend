import React, { useState, useEffect, useCallback } from "react";
import axios from "../axiosConfig";
import MessageSnackBar from "../MessageSnackBar";
import FormUsuarioRol from "./FormUsuarioRol.jsx";
import GridUsuarioRol from "./GridUsuarioRol.jsx";

const ESTADOS_USUARIO_ROL = [
  { id: 1, nombre: "Activo" },
  { id: 2, nombre: "Inactivo" },
];

export default function UsuarioRol() {
  const [selectedRow, setSelectedRow] = useState({});
  const [message, setMessage] = useState({
    open: false,
    severity: "success",
    text: "",
  });
  const [rows, setRows] = useState([]);
  const [formOpen, setFormOpen] = useState(false);

  // Cargar usuario-roles
  const reloadData = useCallback(async () => {
    try {
      const res = await axios.get("/v1/admin/usuario-roles", {
        params: { page: 0, size: 1000 },
      });

      const data = res?.data ?? [];
      const list = Array.isArray(data) ? data : data.content ?? [];

      console.log("Usuario-Roles recibidos:", list.length);
      setRows(list);
    } catch (error) {
      console.error(error);
      setMessage({
        open: true,
        severity: "error",
        text: "Error al cargar usuario-roles",
      });
    }
  }, []);

  useEffect(() => {
    reloadData();
  }, [reloadData]);

  return (
    <div>
      <h1>Gestión de Usuario-Rol</h1>

      <MessageSnackBar message={message} setMessage={setMessage} />

      <FormUsuarioRol
        open={formOpen}
        setOpen={setFormOpen}
        selectedRow={selectedRow || {}}
        setSelectedRow={setSelectedRow}
        setMessage={setMessage}
        reloadData={reloadData}
        estados={ESTADOS_USUARIO_ROL}
        // Si más adelante tienes combos de usuarios/empresas/roles,
        // aquí los puedes pasar como props: usuarios, empresas, roles
      />

      <GridUsuarioRol rows={rows} setSelectedRow={setSelectedRow} />
    </div>
  );
}
