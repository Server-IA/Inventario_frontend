import React, { useEffect, useState } from "react";
import axios from "../axiosConfig";
import MessageSnackBar from "../MessageSnackBar";
import FormModulo from "./FormModulo";
import GridModulo from "./GridModulo";
import ModuloDetalleDialog from "./ModuloDetalleDialog";
import ModuloDisponiblesMenu from "./ModuloDisponiblesMenu.jsx";

import {
  Box,
  Typography,
  Button,
  Tooltip
} from "@mui/material";

import AddIcon from "@mui/icons-material/Add";
import EditIcon from "@mui/icons-material/Edit";
import VisibilityIcon from "@mui/icons-material/Visibility";

export default function Modulo() {

  const [modulos, setModulos] = useState([]);
  const [selectedRow, setSelectedRow] = useState(null);

  const [formOpen, setFormOpen] = useState(false);
  const [formMode, setFormMode] = useState("create");

  const [openDetalle, setOpenDetalle] = useState(false);
  const [openDisponibles, setOpenDisponibles] = useState(false);

  const [message, setMessage] = useState({
    open: false,
    severity: "success",
    text: "",
  });

  const token = localStorage.getItem("token");

  const authHeaders = {
    headers: {
      Authorization: `Bearer ${token}`,
      "Content-Type": "application/json",
      "Accept-Language": "es",
    },
  };

  const unwrap = (data) =>
    Array.isArray(data) ? data : data?.content ?? [];

  // =============================
  // CARGAR LISTADO GENERAL
  // =============================
  const reloadData = async () => {
    try {
      const res = await axios.get("/v2/modulos", authHeaders);
      setModulos(unwrap(res.data));
    } catch {
      setMessage({
        open: true,
        severity: "error",
        text: "Error cargando módulos.",
      });
    }
  };

  useEffect(() => {
    reloadData();
  }, []);

  return (
    <Box sx={{ p: 2 }}>
      <Typography variant="h5" sx={{ mb: 2 }}>
        Gestión de Módulos
      </Typography>

      {/* =============================
          BOTONES
      ============================== */}
      <Box sx={{ mb: 2, display: "flex", gap: 2 }}>

        <Tooltip title="Crear">
          <Button
            variant="contained"
            startIcon={<AddIcon />}
            onClick={() => {
              setFormMode("create");
              setSelectedRow(null);
              setFormOpen(true);
            }}
          >
            Agregar
          </Button>
        </Tooltip>

        <Tooltip title="Editar">
          <span>
            <Button
              variant="outlined"
              startIcon={<EditIcon />}
              disabled={!selectedRow}
              onClick={() => {
                setFormMode("edit");
                setFormOpen(true);
              }}
            >
              Editar
            </Button>
          </span>
        </Tooltip>

        <Tooltip title="Ver Detalle">
          <span>
            <Button
              variant="outlined"
              startIcon={<VisibilityIcon />}
              disabled={!selectedRow}
              onClick={() => setOpenDetalle(true)}
            >
              Ver Detalle
            </Button>
          </span>
        </Tooltip>

        <Button
          variant="outlined"
          onClick={() => setOpenDisponibles(true)}
        >
          Buscar disponibles para menú
        </Button>

      </Box>

      {/* =============================
          GRID
      ============================== */}
      <GridModulo
        modulos={modulos}
        setSelectedRow={setSelectedRow}
      />

      {/* =============================
          FORMULARIO
      ============================== */}
      <FormModulo
        open={formOpen}
        setOpen={setFormOpen}
        formMode={formMode}
        selectedRow={selectedRow}
        reloadData={reloadData}
        setMessage={setMessage}
        authHeaders={authHeaders}
      />

      {/* =============================
          MODAL DETALLE
      ============================== */}
      <ModuloDetalleDialog
        open={openDetalle}
        onClose={() => setOpenDetalle(false)}
        moduloId={selectedRow?.id}
        setMessage={setMessage}
      />

      {/* =============================
          MODAL DISPONIBLES
      ============================== */}
      <ModuloDisponiblesMenu
        open={openDisponibles}
        onClose={() => setOpenDisponibles(false)}
        setMessage={setMessage}
      />

      <MessageSnackBar
        message={message}
        setMessage={setMessage}
      />
    </Box>
  );
}
