import React, { useState, useEffect, useCallback, useMemo } from "react";
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Box,
  Button,
  Chip,
} from "@mui/material";
import VisibilityIcon from "@mui/icons-material/Visibility";
import { useTheme, alpha } from "@mui/material/styles";
import axios from "../axiosConfig";
import MessageSnackBar from "../MessageSnackBar.jsx";
import FormEmpresaRol from "./FormEmpresaRol.jsx";
import ModalVerPermisos from "./ModalVerPermisos";
import SectionHeader from "../common/SectionHeader.jsx";
import GridActionBar from "../common/GridActionBar.jsx";
import AppDataGrid from "../common/AppDataGrid.jsx";

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
  const [confirmOpen, setConfirmOpen] = useState(false);
  const reloadData = useCallback(async () => {
    try {
      setLoading(true);

      const resEmpresaRol = await axios.get("/v1/empresa-rol");
      const empresaRoles = resEmpresaRol.data;

      const resRoles = await axios.get("/v1/items/rol/0");
      const rolesCatalogo = resRoles.data;

      const enriched = await Promise.all(
        empresaRoles.map(async (empresaRol) => {
          const rolBase = rolesCatalogo.find((r) => r.name === empresaRol.rolNombre);

          if (!rolBase) {
            return { ...empresaRol, permisos: [] };
          }

          try {
            const permisosRes = await axios.get(
              `/v1/empresa-rol-permisos/rol/${rolBase.id}/permisos`
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
  }, []);

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

  const handleCreate = () => {
    setSelectedRow(null);
    setFormOpen(true);
  };

  const handleUpdate = () => {
    if (!selectedRow?.id) {
      return setMessage({
        open: true,
        severity: "warning",
        text: "Selecciona una fila",
      });
    }
    setFormOpen(true);
  };

  const handleViewPermisos = () => {
    if (!selectedRow?.id) {
      return setMessage({
        open: true,
        severity: "warning",
        text: "Selecciona un rol primero",
      });
    }
    setModalPermisosOpen(true);
  };

  const handleDeleteIntent = () => {
    if (!selectedRow?.id) {
      return setMessage({
        open: true,
        severity: "warning",
        text: "Selecciona una fila",
      });
    }
    setConfirmOpen(true);
  };

  const confirmarEliminacion = async () => {
    try {
      setLoading(true);

      const resRoles = await axios.get("/v1/items/rol/0");
      const rolBase = resRoles.data.find((r) => r.name === selectedRow.rolNombre);

      if (!rolBase) throw new Error("Rol base no encontrado");

      const rolId = rolBase.id;

      const permisosRes = await axios.get(
        `/v1/empresa-rol-permisos/rol/${rolId}/permisos`
      );

      const permisos = permisosRes.data || [];
      const permisosIds = permisos.map((p) => p.id);

      if (permisosIds.length > 0) {
        await axios.delete(`/v1/empresa-rol-permisos/rol/${rolId}/permisos/quitar`, {
          data: { permisosId: permisosIds },
        });
      }

      await axios.delete(`/v1/empresa-rol/${selectedRow.id}`);

      setMessage({
        open: true,
        severity: "success",
        text: "Rol y permisos eliminados correctamente",
      });

      reloadData();
      setSelectedRow(null);
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

  const columns = useMemo(
    () => [
      { field: "id", headerName: "ID", width: 90, type: "number" },
      { field: "rolNombre", headerName: "Rol", flex: 1, minWidth: 220 },
      {
        field: "permisos",
        headerName: "Permisos",
        flex: 1.8,
        minWidth: 320,
        sortable: false,
        renderCell: (params) => {
          const permisos = Array.isArray(params.row.permisos) ? params.row.permisos : [];
          if (permisos.length === 0) {
            return (
              <Box sx={{ color: "text.secondary", fontStyle: "italic" }}>
                Sin permisos
              </Box>
            );
          }

          const visibles = permisos.slice(0, 3);
          return (
            <Box sx={{ display: "flex", flexWrap: "wrap", gap: 0.75, py: 0.5 }}>
              {visibles.map((permiso) => (
                <Chip
                  key={permiso.id}
                  label={permiso.nombre}
                  size="small"
                  sx={{
                    fontSize: "11px",
                    fontWeight: 500,
                    bgcolor: isDark
                      ? alpha(theme.palette.primary.light, 0.22)
                      : alpha(theme.palette.primary.main, 0.12),
                    color: isDark
                      ? theme.palette.primary.light
                      : theme.palette.primary.dark,
                    border: `1px solid ${
                      isDark ? alpha(theme.palette.primary.light, 0.4) : alpha(theme.palette.primary.main, 0.24)
                    }`,
                  }}
                />
              ))}
              {permisos.length > 3 && (
                <Box sx={{ fontSize: "11px", color: "text.secondary", alignSelf: "center" }}>
                  +{permisos.length - 3} mas
                </Box>
              )}
            </Box>
          );
        },
      },
      {
        field: "estadoNombre",
        headerName: "Estado",
        flex: 0.7,
        minWidth: 140,
        statusChip: true,
        valueGetter: (params) =>
          params.row.estadoNombre ?? params.row.estado?.nombre ?? params.row.estadoId ?? "",
      },
    ],
    [isDark, theme]
  );

  return (
    <Box p={2}>
      <SectionHeader title="Roles de Empresa" />

      <MessageSnackBar message={message} setMessage={setMessage} />

      <GridActionBar
        onAdd={handleCreate}
        onUpdate={handleUpdate}
        onDelete={handleDeleteIntent}
        canUpdate={Boolean(selectedRow)}
        canDelete={Boolean(selectedRow)}
        onFilters={() =>
          setMessage({
            open: true,
            severity: "info",
            text: "Filtros disponibles proximamente",
          })
        }
        extraActions={
          <Button
            onClick={handleViewPermisos}
            startIcon={<VisibilityIcon />}
            disabled={!selectedRow}
          >
            Ver permisos
          </Button>
        }
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
      />

      <ModalVerPermisos
        open={modalPermisosOpen}
        onClose={() => setModalPermisosOpen(false)}
        permisos={selectedRow?.permisos || []}
        rolNombre={selectedRow?.rolNombre}
      />

      <AppDataGrid
        rows={rows}
        columns={columns}
        loading={loading}
        selectedRow={selectedRow}
        setSelectedRow={setSelectedRow}
        containerSx={{ borderRadius: 4 }}
        onEscape={() => {
          setFormOpen(false);
          setModalPermisosOpen(false);
          setConfirmOpen(false);
        }}
      />

      <Dialog open={confirmOpen} onClose={() => setConfirmOpen(false)}>
        <DialogTitle>Confirmar eliminacion</DialogTitle>
        <DialogContent>
          ¿Esta seguro que desea eliminar este rol y todos sus permisos?
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
    </Box>
  );
}
