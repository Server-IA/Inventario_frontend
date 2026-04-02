import React, { useState, useEffect, useCallback } from "react";
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
} from "@mui/material";
import { useTheme, alpha } from "@mui/material/styles";
import axios from "../axiosConfig";
import MessageSnackBar from "../MessageSnackBar.jsx";
import FormEmpresaRol from "./FormEmpresaRol.jsx";
import GridEmpresaRol from "./GridEmpresaRol.jsx";
import ModalVerPermisos from "./ModalVerPermisos";
import SectionHeader from "../common/SectionHeader.jsx";
import GridActionBar from "../common/GridActionBar.jsx";
import VisibilityIcon from "@mui/icons-material/Visibility";

const SYSTEM_ROLE_REGEX = /(ROLE_ADMINISTRADOR_SISTEMA|ADMINISTRADOR[_\s-]*SISTEMA|ADMIN\s*SISTEMA)/i;

const parseRolesByCompany = () => {
  try {
    return JSON.parse(localStorage.getItem("rolesByCompany") || "[]");
  } catch {
    return [];
  }
};

const resolveCurrentRoleName = () => {
  const empresaId = Number(localStorage.getItem("empresaId"));
  const rolId = Number(localStorage.getItem("rolId"));
  const rolesByCompany = parseRolesByCompany();

  const byContext = rolesByCompany.find(
    (r) => Number(r?.empresaId) === empresaId && Number(r?.rolId) === rolId
  );

  return (
    byContext?.rolNombre ||
    localStorage.getItem("rolNombre") ||
    ""
  );
};

export default function EmpresaRol() {
  const theme = useTheme();
  const isDark = theme.palette.mode === "dark";
  const [selectedRow, setSelectedRow] = useState(null);
  const [rows, setRows] = useState([]);
  const [formOpen, setFormOpen] = useState(false);
  const [modalPermisosOpen, setModalPermisosOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const [roles, setRoles] = useState([]);

  const [message, setMessage] = useState({
    open: false,
    severity: "success",
    text: "",
  });

  const empresaId = Number(localStorage.getItem("empresaId"));
  const currentRoleName = resolveCurrentRoleName();
  const isSystemAdmin = SYSTEM_ROLE_REGEX.test(currentRoleName);

  const permisosLegacyParams = (targetEmpresaId) =>
    isSystemAdmin ? { params: { empresaId: Number(targetEmpresaId) } } : undefined;

  const [confirmOpen, setConfirmOpen] = useState(false);
 const reloadData = useCallback(async () => {
  try {
    setLoading(true);

    //  Cargar empresa-rol
    const resEmpresaRol = await axios.get(
      isSystemAdmin ? "/v1/system/empresa-rol" : "/v1/empresa-rol"
    );
    const empresaRoles = resEmpresaRol.data;

    //  Cargar catálogo roles
    const resRoles = await axios.get("/v1/items/rol/0");
    const rolesCatalogo = resRoles.data;

    const enriched = await Promise.all(
      empresaRoles.map(async (empresaRol) => {

        //  Buscar rolId por nombre
        const rolBase = rolesCatalogo.find(
          r => r.name === empresaRol.rolNombre
        );

        if (!rolBase) {
          return { ...empresaRol, permisos: [] };
        }

        try {
          const permisosRes = await axios.get(
            `/v1/empresa-rol-permisos/rol/${rolBase.id}/permisos`,
            permisosLegacyParams(empresaRol.empresaId ?? empresaId)
          );

          return {
            ...empresaRol,
            permisos: permisosRes.data || [],
          };

        } catch {
          return {
            ...empresaRol,
            permisos: [],
          };
        }
      })
    );

    setRows(enriched);

  } catch (error) {
    console.error(error);
    setRows([]);
  } finally {
    setLoading(false);
  }
}, [isSystemAdmin, empresaId]);

  // CARGAR CATÁLOGO DE ROLES (solo para el formulario)
          const loadRoles = useCallback(async () => {
            try {
              const rolRes = await axios.get("/v1/items/rol/0");

              const list = Array.isArray(rolRes?.data)
                ? rolRes.data
                : Array.isArray(rolRes?.data?.content)
                ? rolRes.data.content
                : [];

              setRoles(list);
            } catch (err) {
              console.error("Error cargando roles catálogo:", err);
              setRoles([]);
            }
          }, []);

          useEffect(() => {
            reloadData();
            loadRoles();
          }, [reloadData, loadRoles]);
          
const confirmarEliminacion = async () => {
  try {
    setLoading(true);

    const resRoles = await axios.get("/v1/items/rol/0");
    const rolBase = resRoles.data.find(
      r => r.name === selectedRow.rolNombre
    );

    if (!rolBase) throw new Error("Rol base no encontrado");

    const rolId = rolBase.id;

    const permisosRes = await axios.get(
      `/v1/empresa-rol-permisos/rol/${rolId}/permisos`,
      permisosLegacyParams(selectedRow?.empresaId ?? empresaId)
    );

    const permisos = permisosRes.data || [];
    const permisosIds = permisos.map(p => p.id);

    if (permisosIds.length > 0) {
      await axios.delete(
        `/v1/empresa-rol-permisos/rol/${rolId}/permisos/quitar`,
        {
          data: { permisosId: permisosIds }
        }
      );
    }

    await axios.delete(
      isSystemAdmin
        ? `/v1/system/empresa-rol/${selectedRow.id}`
        : `/v1/empresa-rol/${selectedRow.id}`
    );

    setMessage({
      open: true,
      severity: "success",
      text: "Rol y permisos eliminados correctamente",
    });

    reloadData();

  } catch (error) {
    setMessage({
      open: true,
      severity: "error",
      text: "Error al eliminar. Revisa dependencias o permisos.",
    });
  } finally {
    setLoading(false);
    setConfirmOpen(false);
  }
};

const extraActions = (
  <Button
    variant="outlined"
    startIcon={<VisibilityIcon />}
    onClick={() => {
      if (!selectedRow?.id) {
        return setMessage({
          open: true,
          severity: "warning",
          text: "Selecciona un rol primero",
        });
      }
      setModalPermisosOpen(true);
    }}
    disabled={!selectedRow?.id}
    sx={{
      px: 2.5,
      py: 1,
      borderRadius: 2,
      textTransform: "uppercase",
      fontWeight: 700,
      fontSize: "0.75rem",
      borderColor: theme.palette.divider,
      color: theme.palette.text.primary,
      backgroundColor: isDark ? alpha(theme.palette.common.white, 0.12) : theme.palette.grey[100],
      boxShadow: `0 6px 16px ${alpha(theme.palette.common.black, isDark ? 0.35 : 0.1)}`,
      "&:hover": {
        borderColor: theme.palette.text.secondary,
        backgroundColor: isDark ? alpha(theme.palette.common.white, 0.18) : theme.palette.grey[200],
      },
      "&.Mui-disabled": {
        color: theme.palette.text.disabled,
        borderColor: theme.palette.action.disabledBackground,
      },
      "& .MuiButton-startIcon svg": { fontSize: 16 },
    }}
  >
    Ver permisos
  </Button>
);

          return (
            <div>
              <SectionHeader
                title="Roles de Empresa"
              />

              <MessageSnackBar message={message} setMessage={setMessage} />
              <GridActionBar
                onAdd={() => {
                  setSelectedRow(null);
                  setFormOpen(true);
                }}
                onUpdate={() => {
                  if (!selectedRow?.id)
                    return setMessage({
                      open: true,
                      severity: "warning",
                      text: "Selecciona una fila",
                    });

                  setFormOpen(true);
                }}
                onDelete={() => {
                  if (!selectedRow?.id)
                    return setMessage({
                      open: true,
                      severity: "warning",
                      text: "Selecciona una fila",
                    });

                  setConfirmOpen(true);
                }}
                canUpdate={Boolean(selectedRow?.id)}
                canDelete={Boolean(selectedRow?.id)}
                extraActions={extraActions}
              />
          
      <FormEmpresaRol
        open={formOpen}
        setOpen={setFormOpen}
        selectedRow={selectedRow}
        setSelectedRow={setSelectedRow}
        setMessage={setMessage}
        reloadData={reloadData}
        roles={roles}
        empresaId={empresaId}
        isSystemAdmin={isSystemAdmin}
      />

      <ModalVerPermisos
        open={modalPermisosOpen}
        onClose={() => setModalPermisosOpen(false)}
        permisos={selectedRow?.permisos || []}
        rolNombre={selectedRow?.rolNombre}
      />

      <GridEmpresaRol
        rows={rows}
        loading={loading}
        selectedRow={selectedRow}
        setSelectedRow={setSelectedRow}
        isSystemAdmin={isSystemAdmin}
      />
<Dialog open={confirmOpen} onClose={() => setConfirmOpen(false)}>
  <DialogTitle>Confirmar eliminación</DialogTitle>
  <DialogContent>
    ¿Está seguro que desea eliminar este rol y todos sus permisos?
  </DialogContent>
  <DialogActions>
    <Button onClick={() => setConfirmOpen(false)}>
      Cancelar
    </Button>
    <Button
      color="error"
      variant="contained"
      onClick={confirmarEliminacion}
    >
      Eliminar
    </Button>
  </DialogActions>
</Dialog>
    </div>
  );
}
