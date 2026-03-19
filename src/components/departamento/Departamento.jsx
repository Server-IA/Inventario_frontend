import React, { useEffect, useState, useRef } from "react";
import axios from "../axiosConfig";
import GridDepartamento from "./GridDepartamento";
import FormDepartamento from "./FormDepartamento";
import MessageSnackBar from "../MessageSnackBar";
import { Box, Typography, Button, Stack } from "@mui/material";
import StackButtons from "../StackButtons";

// Modal filtros
import CrudFilterModal from "../common/CrudFilterModal";
import { makeLoaders, unwrap as unwrapPage } from "../common/filtersLoaders";

export default function Departamento() {
  // ===========================
  // ESTADO
  // ===========================
  const [filters, setFilters] = useState({ paisId: "" });
  const [openFilters, setOpenFilters] = useState(false);

  const [paisesItems, setPaisesItems] = useState([]);
  const [departamentos, setDepartamentos] = useState([]);

  const [selectedRow, setSelectedRow] = useState(null);
  const [message, setMessage] = useState({
    open: false,
    severity: "success",
    text: "",
  });

  const formMethodsRef = useRef({});

  // ===========================
  // CONFIG
  // ===========================
  const token = localStorage.getItem("token");
  const headers = { headers: { Authorization: `Bearer ${token}` } };

  const { getPaises } = makeLoaders(headers);

  const paisesForm = paisesItems.map((p) => ({
    id: p.id,
    nombre: p.name,
  }));

  const fieldsDepartamento = [
    { name: "paisId", label: "País", getOptions: getPaises },
  ];

  // ===========================
  // LOAD PAISES
  // ===========================
  useEffect(() => {
    (async () => {
      try {
        const r = await axios.get("/v1/items/pais/0");
        const arr = Array.isArray(r.data) ? r.data : [];
        if (arr.length) {
          setPaisesItems(arr);
          return;
        }
        throw new Error();
      } catch {
        try {
          const { data } = await axios.get("/v1/pais", {
            ...headers,
            params: { page: 0, size: 1000 },
          });

          const list = (Array.isArray(data) ? data : data?.content ?? []).map((p) => ({
            id: p.id,
            name: p.nombre,
          }));

          setPaisesItems(list);
        } catch {
          setMessage({
            open: true,
            severity: "error",
            text: "Error al cargar países",
          });
        }
      }
    })();
  }, []);

  // ===========================
  // LOAD DATA
  // ===========================
  const reloadData = () => {
    const req = filters.paisId
      ? axios.get("/v1/departamento", {
          ...headers,
          params: { paisId: Number(filters.paisId), page: 0, size: 1000 },
        })
      : axios.get("/v1/departamento", {
          ...headers,
          params: { page: 0, size: 1000 },
        });

    req
      .then((res) => {
        const lista = unwrapPage(res.data);

        const normalizadas = lista.map((d) => {
          const paisIdNum = d.paisId ?? d.pais?.id ?? "";
          const pais = paisesItems.find(
            (p) => Number(p.id) === Number(paisIdNum)
          );

          return {
            ...d,
            paisId: Number(paisIdNum) || "",
            paisNombre: pais?.name ?? "",
            name: d.nombre, // FIX IMPORTANTE
          };
        });

        setDepartamentos(normalizadas);
        setSelectedRow(null);
      })
      .catch(() =>
        setMessage({
          open: true,
          severity: "error",
          text: "Error al cargar departamentos.",
        })
      );
  };

  useEffect(() => { reloadData(); }, []);
  useEffect(() => { reloadData(); }, [filters.paisId]);
  useEffect(() => {
    if (paisesItems.length) reloadData();
  }, [paisesItems]);

  // ===========================
  // FILTROS
  // ===========================
  const handleFiltersChange = ({ name, value }) =>
    setFilters((f) => ({ ...f, [name]: value }));

  const handleFiltersClear = () =>
    setFilters({ paisId: "" });

  const handleFiltersApply = () => {
    setOpenFilters(false);
    reloadData();
  };

  // ===========================
  // RENDER
  // ===========================
  return (
    <Box sx={{ p: 2 }}>
      {/* HEADER */}
      <Stack direction="row" justifyContent="space-between" alignItems="center">
        <Typography variant="h5">Gestión de Departamento</Typography>

        <Stack direction="row" spacing={1}>
          <Button onClick={() => setOpenFilters(true)}>
            Mostrar filtros
          </Button>

          {filters.paisId && (
            <Button onClick={handleFiltersClear}>
              Limpiar filtros
            </Button>
          )}
        </Stack>
      </Stack>

      {/* BOTONES */}
      <Box sx={{ mt: 2, mb: 3 }}>
        <StackButtons methods={formMethodsRef.current} />
      </Box>

      {/* GRID */}
      <GridDepartamento
        departamentos={departamentos}
        setSelectedRow={setSelectedRow}
      />

      {/* FORM */}
      <FormDepartamento
        selectedRow={selectedRow}
        setSelectedRow={setSelectedRow}
        setMessage={setMessage}
        reloadData={reloadData}
        paisId={filters.paisId || ""}
        paises={paisesForm}
        externalMethods={formMethodsRef} //  FIX IMPORTANTE
      />

      {/* SNACKBAR */}
      <MessageSnackBar message={message} setMessage={setMessage} />

      {/* FILTROS */}
      <CrudFilterModal
        open={openFilters}
        onClose={() => setOpenFilters(false)}
        title="Filtros de Departamento"
        fields={fieldsDepartamento}
        values={filters}
        onChange={handleFiltersChange}
        onClear={handleFiltersClear}
        onApply={handleFiltersApply}
      />
    </Box>
  );
}