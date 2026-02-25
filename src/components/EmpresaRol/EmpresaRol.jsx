import React, { useState, useEffect, useCallback } from "react";
import axios from "../axiosConfig";
import { Box, Button } from "@mui/material";
import MessageSnackBar from "../MessageSnackBar.jsx";
import FormEmpresaRol from "./FormEmpresaRol.jsx";
import GridEmpresaRol from "./GridEmpresaRol.jsx";
import ModalVerPermisos from "./ModalVerPermisos";
import StackButtons from "../StackButtons";

export default function EmpresaRol() {
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

 const reloadData = useCallback(async () => {
  try {
    setLoading(true);

    //  Cargar empresa-rol
    const resEmpresaRol = await axios.get("/v1/empresa-rol");
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

          return (
            <div>
              <h1>Roles de Empresa</h1>

              <MessageSnackBar message={message} setMessage={setMessage} />
              <Box
          sx={{
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
            mb: 2,
          }}
        >

          {/* IZQUIERDA (si quieres dejar vacío o título secundario) */}
          <Box />

          {/* DERECHA: Botones */}
          <Box sx={{ display: "flex", gap: 2 }}>

            <Button
              variant="outlined"
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
            >
              Ver permisos
            </Button>

            <StackButtons
              methods={{
                create: () => {
                  setSelectedRow(null);
                  setFormOpen(true);
                },
                update: () => {
                  if (!selectedRow?.id)
                    return setMessage({
                      open: true,
                      severity: "warning",
                      text: "Selecciona una fila",
                    });

                  setFormOpen(true);
                },
              deleteRow: async () => {
                if (!selectedRow?.id)
                  return setMessage({
                    open: true,
                    severity: "warning",
                    text: "Selecciona una fila",
                  });

                if (!window.confirm("¿Eliminar este rol y todos sus permisos?")) return;

                try {
                  setLoading(true);

                  //  Obtener rolId real
                  const resRoles = await axios.get("/v1/items/rol/0");
                  const rolBase = resRoles.data.find(
                    r => r.name === selectedRow.rolNombre
                  );

                  if (!rolBase) throw new Error("Rol base no encontrado");

                  const rolId = rolBase.id;

                  //  Obtener permisos actuales
                  const permisosRes = await axios.get(
                    `/v1/empresa-rol-permisos/rol/${rolId}/permisos`
                  );

                  const permisos = permisosRes.data || [];

                  //  Extraer permisosId
                  const permisosIds = permisos.map(p => p.id);

                  //  Eliminar permisos individuales si existen
                  if (permisosIds.length > 0) {
                    await axios.delete(
                      `/v1/empresa-rol-permisos/rol/${rolId}/permisos/quitar`,
                      {
                        data: { permisosId: permisosIds }
                      }
                    );
                  }

                  //  Ahora eliminar empresa-rol
                  await axios.delete(`/v1/empresa-rol/${selectedRow.id}`);

                  setMessage({
                    open: true,
                    severity: "success",
                    text: "Rol y permisos eliminados correctamente",
                  });

                  reloadData();

                } catch (error) {

                  console.error(error);

                  setMessage({
                    open: true,
                    severity: "error",
                    text: "Error al eliminar. Revisa dependencias o permisos.",
                  });

                } finally {
                  setLoading(false);
                }
              }
              }}
            />

          </Box>
        </Box>
          
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

      <GridEmpresaRol
        rows={rows}
        loading={loading}
        selectedRow={selectedRow}
        setSelectedRow={setSelectedRow}
      />

    </div>
  );
}