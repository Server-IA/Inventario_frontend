import React, { useState, useEffect, useCallback, useMemo } from "react";
import axios from "../axiosConfig";
import MessageSnackBar from "../MessageSnackBar";
import FormUsuarioRol from "./FormUsuarioRol.jsx";
import GridUsuarioRol from "./GridUsuarioRol.jsx";

const ESTADOS_USUARIO_ROL = [
  { id: 1, nombre: "Activo" },
  { id: 2, nombre: "Inactivo" },
];

// ===== Helpers robustos =====
const looksLikeEmail = (v) =>
  typeof v === "string" && v.includes("@") && v.includes(".");

const pickEmail = (obj) => {
  if (!obj || typeof obj !== "object") return "";
  const byKey =
    obj.email ??
    obj.usuarioEmail ??
    obj.correo ??
    obj.correoElectronico ??
    obj.usuario_email ??
    obj.mail ??
    "";
  if (looksLikeEmail(byKey)) return String(byKey).trim();
  const found = Object.values(obj).find((v) => looksLikeEmail(v));
  return found ? String(found).trim() : "";
};

const pickUsuarioEmpresa = (obj) => {
  if (!obj || typeof obj !== "object") return "";
  return String(
    obj.usuario_empresa ??
      obj.usuarioEmpresa ??
      obj.usuarioempresa ??
      obj.userEmpresa ??
      obj.nombre ??
      ""
  ).trim();
};

const pickRolId = (obj) => {
  const v = obj?.id ?? obj?.rolId ?? obj?.rol_id ?? obj?.codigo ?? null;
  if (v === null || v === undefined || v === "") return null;
  const n = Number(v);
  return Number.isNaN(n) ? null : n;
};

const pickRolEmpresa = (obj) => {
  if (!obj || typeof obj !== "object") return "";
  return String(
    obj.rol_empresa ??
      obj.rolEmpresa ??
      obj.rolNombre ??
      obj.nombre ??
      obj.descripcion ??
      ""
  ).trim();
};

export default function UsuarioRol() {
  const [selectedRow, setSelectedRow] = useState({});
  const [message, setMessage] = useState({
    open: false,
    severity: "success",
    text: "",
  });

  const [rawRows, setRawRows] = useState([]);
  const [usuariosItems, setUsuariosItems] = useState([]); // /items/usuario_empresa/0
  const [rolesItems, setRolesItems] = useState([]); // /items/rol_empresa/0

  const [formOpen, setFormOpen] = useState(false);
  const [loading, setLoading] = useState(false);

  // ✅ Mapa email -> usuario_empresa
  const usuarioEmpresaByEmail = useMemo(() => {
    const map = {};
    (Array.isArray(usuariosItems) ? usuariosItems : []).forEach((u) => {
      const email = pickEmail(u);
      const usuarioEmpresa = pickUsuarioEmpresa(u);
      if (email) map[email] = usuarioEmpresa || email;
    });
    return map;
  }, [usuariosItems]);

  // ✅ Mapa rolId -> rol_empresa
  const rolEmpresaById = useMemo(() => {
    const map = {};
    (Array.isArray(rolesItems) ? rolesItems : []).forEach((r) => {
      const id = pickRolId(r);
      const rolEmpresa = pickRolEmpresa(r);
      if (id !== null) map[id] = rolEmpresa || String(id);
    });
    return map;
  }, [rolesItems]);

  // ✅ Enriquecer rows para que el Grid muestre nombres
  const rows = useMemo(() => {
    return (Array.isArray(rawRows) ? rawRows : []).map((r) => {
      const email = String(r?.usuarioEmail ?? "").trim();
      const rolId = r?.rolId ?? r?.rol_id ?? r?.rol ?? null;
      const rolIdNum = rolId !== null ? Number(rolId) : null;

      return {
        ...r,
        usuarioNombre: usuarioEmpresaByEmail[email] || email,
        // si el backend ya trae rolNombre bonito, lo respetamos; si no, usamos items
        rolNombre:
          r?.rolNombre ||
          (rolIdNum !== null && !Number.isNaN(rolIdNum)
            ? rolEmpresaById[rolIdNum]
            : "") ||
          r?.rolNombre ||
          "",
      };
    });
  }, [rawRows, usuarioEmpresaByEmail, rolEmpresaById]);

  const reloadData = useCallback(async () => {
    try {
      setLoading(true);
      const res = await axios.get("v1/usuario-roles", {
        params: { page: 0, size: 1000 },
      });
      const list = res?.data?.content ?? res?.data ?? [];
      setRawRows(Array.isArray(list) ? list : []);
    } catch (error) {
      console.error(error);
      setMessage({
        open: true,
        severity: "error",
        text: "Error al cargar usuario-roles",
      });
    } finally {
      setLoading(false);
    }
  }, []);

  const loadUsuariosItems = useCallback(async () => {
    try {
      const res = await axios.get("/v1/items/usuario_empresa/0");
      const list = res?.data?.content ?? res?.data ?? [];
      setUsuariosItems(Array.isArray(list) ? list : []);
    } catch (error) {
      console.error(error);
      setMessage({
        open: true,
        severity: "error",
        text: "Error al cargar items de usuario_empresa",
      });
      setUsuariosItems([]);
    }
  }, []);

  const loadRolesItems = useCallback(async () => {
    try {
      const res = await axios.get("/v1/items/rol_empresa/0");
      const list = res?.data?.content ?? res?.data ?? [];
      setRolesItems(Array.isArray(list) ? list : []);
    } catch (error) {
      console.error(error);
      setMessage({
        open: true,
        severity: "error",
        text: "Error al cargar items de rol_empresa",
      });
      setRolesItems([]);
    }
  }, []);

  useEffect(() => {
    reloadData();
    loadUsuariosItems();
    loadRolesItems();
  }, [reloadData, loadUsuariosItems, loadRolesItems]);

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
        // ✅ USANDO LOS ITEMS
        usuarios={usuariosItems}
        roles={rolesItems}
      />

      <GridUsuarioRol
        rows={rows}
        loading={loading}
        selectedRow={selectedRow}
        setSelectedRow={setSelectedRow}
      />
    </div>
  );
}
