import React, { useEffect, useMemo, useState } from "react";
import { Box, Chip, Button } from "@mui/material";
import VisibilityIcon from "@mui/icons-material/Visibility";
import GridActionBar from "../common/GridActionBar.jsx";
import SectionHeader from "../common/SectionHeader.jsx";
import AppDataGrid from "../common/AppDataGrid.jsx";
import FormUsuario from "./FormUsuario.jsx";
import UserDetailDialog from "./UserDetailDialog.jsx";
import MessageSnackBar from "../MessageSnackBar";

const emptyRow = {
  id: null,
  username: "",
  nombre: "",
  apellido: "",
  genero: "",
  tipoDocumentoIdentidadId: "",
  codigoIdentificacion: "",
  fechaNacimiento: "",
  estrato: "",
  direccion: "",
  celular: "",
  rolPreferido: "",
  empresaId: "",
  empresaNombre: "",
  estadoId: 1,
  asignaciones: [],
};

const mockUsers = [
  { id: 1, username: "admin@inmero.co", nombre: "Cesar", apellido: "Medina", correo: "admin@inmero.co", celular: "3001112222", rolPreferido: "Administrador", empresaId: 1, empresaNombre: "Inmero S.A.", genero: "Masculino", documento: "CC 12345678", fechaNacimiento: "1985-01-20", direccion: "Cra 10 # 12-34", estadoId: 1, asignaciones: [{ rolNombre: "Administrador", empresaNombre: "Inmero S.A.", iniciaContratoEn: "2020-01-01", finalizaContratoEn: "", estadoId: 1, preferido: true }] },
  { id: 2, username: "jlopez@inmero.co", nombre: "Juan", apellido: "Lopez", correo: "jlopez@inmero.co", celular: "3002223333", rolPreferido: "Gerente", empresaId: 1, empresaNombre: "Inmero S.A.", genero: "Masculino", documento: "CC 87654321", fechaNacimiento: "1990-04-15", direccion: "Calle 80 # 45-10", estadoId: 1, asignaciones: [{ rolNombre: "Gerente", empresaNombre: "Inmero S.A.", iniciaContratoEn: "2021-02-01", finalizaContratoEn: "", estadoId: 1, preferido: true }] },
  { id: 3, username: "maria.suarez@agrotech.co", nombre: "Maria", apellido: "Suarez", correo: "maria.suarez@agrotech.co", celular: "3015556666", rolPreferido: "Operario", empresaId: 2, empresaNombre: "Agrotech Ltda.", genero: "Femenino", documento: "CC 11223344", fechaNacimiento: "1995-08-10", direccion: "Av 68 # 90-12", estadoId: 2, asignaciones: [{ rolNombre: "Operario", empresaNombre: "Agrotech Ltda.", iniciaContratoEn: "2022-03-01", finalizaContratoEn: "2025-03-01", estadoId: 2, preferido: true }] },
  { id: 4, username: "pedro.mtz@inmero.co", nombre: "Pedro", apellido: "Martinez", correo: "pedro.mtz@inmero.co", celular: "3023334444", rolPreferido: "Supervisor", empresaId: 1, empresaNombre: "Inmero S.A.", genero: "Masculino", documento: "CC 99887766", fechaNacimiento: "1988-12-05", direccion: "Transv 25 # 12-05", estadoId: 1, asignaciones: [{ rolNombre: "Supervisor", empresaNombre: "Inmero S.A.", iniciaContratoEn: "2023-05-15", finalizaContratoEn: "", estadoId: 1, preferido: true }] },
  { id: 5, username: "ana.castro@agrotech.co", nombre: "Ana", apellido: "Castro", correo: "ana.castro@agrotech.co", celular: "3037778888", rolPreferido: "Analista", empresaId: 2, empresaNombre: "Agrotech Ltda.", genero: "Femenino", documento: "CC 55667788", fechaNacimiento: "1992-03-22", direccion: "Calle 26 # 30-20", estadoId: 1, asignaciones: [{ rolNombre: "Analista", empresaNombre: "Agrotech Ltda.", iniciaContratoEn: "2024-01-01", finalizaContratoEn: "", estadoId: 1, preferido: true }] },
];

export default function Usuario() {
  const [rows, setRows] = useState([]);
  const [selectedRow, setSelectedRow] = useState(null);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState({ open: false, severity: "", text: "" });
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [openForm, setOpenForm] = useState(false);
  const [formMode, setFormMode] = useState("create");
  const [formData, setFormData] = useState(emptyRow);
  const [openDetail, setOpenDetail] = useState(false);
  const [detail, setDetail] = useState(null);

  const isAdmin = String(localStorage.getItem("rolId")) === "1";
  const empresaIdOwn = Number(localStorage.getItem("empresaId"));

  const empresasList = useMemo(() => {
    const set = new Map();
    mockUsers.forEach((u) => {
      if (!set.has(u.empresaId)) set.set(u.empresaId, { id: u.empresaId, nombre: u.empresaNombre });
    });
    return Array.from(set.values());
  }, []);

  const rolesList = useMemo(() => {
    const s = new Set();
    mockUsers.forEach((u) => { if (u.rolPreferido) s.add(u.rolPreferido); });
    return Array.from(s.values());
  }, []);

  const baseRows = useMemo(() => {
    if (isAdmin) return mockUsers;
    if (!Number.isFinite(empresaIdOwn)) return mockUsers;
    const byCompany = mockUsers.filter((u) => Number(u.empresaId) === Number(empresaIdOwn));
    return byCompany.length ? byCompany : mockUsers;
  }, [isAdmin, empresaIdOwn]);

  const columns = useMemo(() => {
    const cols = [
      { field: "nombre", headerName: "Nombre", width: 180 },
      { field: "apellido", headerName: "Apellido", width: 180 },
      { field: "correo", headerName: "Correo", width: 240 },
      { field: "celular", headerName: "Teléfono", width: 160 },
      { field: "rolPreferido", headerName: "Rol", width: 200 },
      { field: "estadoId", headerName: "Estado", width: 140, align: "left", headerAlign: "left", headerClassName: "col-estado", cellClassName: "col-estado", renderCell: (p) => (<Box sx={{ display: "flex", justifyContent: "flex-start" }}><Chip label={Number(p.row.estadoId) === 1 ? "Activo" : "Inactivo"} color={Number(p.row.estadoId) === 1 ? "success" : "error"} size="small" /></Box>) },
      ...(isAdmin ? [{ field: "empresaNombre", headerName: "Empresa", width: 220 }] : []),
    ];
    return cols;
  }, [isAdmin]);

  const loadData = () => {
    setLoading(true);
    setTimeout(() => {
      setRows(baseRows);
      setLoading(false);
    }, 120);
  };

  useEffect(() => { loadData(); }, [baseRows]);

  const handlePaginationModelChange = (model) => {
    if (model.size !== pageSize) {
      setPageSize(model.size);
      setPage(0);
    } else {
      setPage(model.page);
    }
  };

  const handleCreate = () => {
    setFormMode("create");
    setFormData(emptyRow);
    setOpenForm(true);
  };
  const handleEdit = () => {
    if (!selectedRow) return;
    setFormMode("edit");
    setFormData({ ...emptyRow, ...selectedRow });
    setOpenForm(true);
  };
  const handleView = () => {
    if (!selectedRow) return;
    setDetail(selectedRow);
    setOpenDetail(true);
  };
  const handleToggleActive = () => {
    if (!selectedRow) return;
    const inactive = Number(selectedRow.estadoId) !== 1;
    setRows((prev) => (Array.isArray(prev) ? prev : []).map((r) => r.id === selectedRow.id ? { ...r, estadoId: inactive ? 1 : 2 } : r));
    setSelectedRow((prev) => prev ? { ...prev, estadoId: inactive ? 1 : 2 } : prev);
    setMessage({ open: true, severity: inactive ? "success" : "info", text: inactive ? "Usuario activado" : "Usuario inactivado" });
  };
  const handleDelete = () => {
    if (!selectedRow) return;
    setRows((prev) => (Array.isArray(prev) ? prev : []).filter((r) => r.id !== selectedRow.id));
    setSelectedRow(null);
    setMessage({ open: true, severity: "info", text: "Usuario eliminado" });
  };

  const handleSubmit = async (dataFromForm) => {
    const payload = { ...(dataFromForm || formData) };
    const pref = Array.isArray(payload.asignaciones) ? payload.asignaciones.find((a) => Boolean(a.preferido)) : null;
    if (pref) {
      payload.rolPreferido = pref.rolNombre || payload.rolPreferido || "";
      payload.empresaId = pref.empresaId ?? payload.empresaId;
      payload.empresaNombre = pref.empresaNombre || payload.empresaNombre || "";
    }
    if (formMode === "create") {
      const nextId = (rows.reduce((mx, r) => Math.max(mx, Number(r.id)), 0) || 0) + 1;
      payload.id = nextId;
      setRows((prev) => [payload, ...(Array.isArray(prev) ? prev : [])]);
    } else {
      setRows((prev) => (Array.isArray(prev) ? prev : []).map((r) => (r.id === payload.id ? { ...r, ...payload } : r)));
    }
    setOpenForm(false);
    setMessage({ open: true, severity: "success", text: "Usuario guardado correctamente" });
  };

  return (
    <Box p={2}>
      <SectionHeader title="Usuarios" />

      <GridActionBar
        onAdd={handleCreate}
        onUpdate={handleEdit}
        onDelete={handleDelete}
        canUpdate={Boolean(selectedRow)}
        canDelete={Boolean(selectedRow)}
        extraActions={
          <Button
            onClick={handleView}
            startIcon={<VisibilityIcon />}
            disabled={!selectedRow}
            sx={{
              bgcolor: "#f4f5f7",
              color: "#2a2e35",
              px: 2.5,
              py: 1,
              borderRadius: 2,
              textTransform: "uppercase",
              fontWeight: 700,
              fontSize: "0.75rem",
              boxShadow: "0 6px 16px rgba(0,0,0,0.08)",
              "&:hover": { bgcolor: "#e9eaee" },
              "&.Mui-disabled": { color: "#9aa0a6" },
              "& .MuiButton-startIcon svg": { fontSize: 16 },
            }}
          >
            Ver detalle
          </Button>
        }
      />

      <AppDataGrid
        rows={rows}
        columns={columns}
        loading={loading}
        selectedRow={selectedRow}
        setSelectedRow={setSelectedRow}
        paginationModel={{ page, pageSize }}
        setPaginationModel={handlePaginationModelChange}
        rowCount={rows.length}
        containerSx={{ borderRadius: 4 }}
      />

      <FormUsuario
        open={openForm}
        onClose={() => setOpenForm(false)}
        mode={formMode}
        initialData={formData}
        onSubmit={handleSubmit}
        roles={rolesList}
        empresas={empresasList}
        isAdmin={isAdmin}
      />

      <UserDetailDialog
        open={openDetail}
        data={detail ?? selectedRow}
        onClose={() => setOpenDetail(false)}
      />

      <MessageSnackBar message={message} setMessage={setMessage} />
    </Box>
  );
}
