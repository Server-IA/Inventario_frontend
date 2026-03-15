import React, { useEffect, useState, useMemo } from "react";
import axios from "../axiosConfig";
import MessageSnackBar from "../MessageSnackBar";
import FormKardex from "./FromKardex";
import GridKardex from "./GridKardex";
import GridArticuloKardex from "./GridArticuloKardex";
import FormArticuloKardex from "./FormArticuloKardex";
import ReKardex from "../RKardex/Rkardex";
import { Box, Typography, Button, Dialog, useTheme } from "@mui/material";

/* ===== Helper para decodificar JWT ===== */
const decodeJwt = (jwt = "") => {
  try {
    const [, raw] = jwt.split(".");
    if (!raw) return {};
    const b64 = raw.replace(/-/g, "+").replace(/_/g, "/");
    const pad = b64.length % 4 === 2 ? "==" : b64.length % 4 === 3 ? "=" : "";
    const json = atob(b64 + pad);
    return JSON.parse(json);
  } catch {
    return {};
  }
};

export default function Kardex() {
  /* ===== Detectar admin desde JWT ===== */
  const token = localStorage.getItem("token");
  const decoded = useMemo(() => decodeJwt(token), [token]);
  const rolId = decoded?.rolId; // 1 = ADMINISTRADOR_SISTEMA, 2 = ADMINISTRADOR_EMPRESA
  const isAdmin = rolId === 1; // Solo ADMINISTRADOR_SISTEMA ve todas las empresas

  const [kardexes, setKardexes] = useState([]);
  const [kardexesRaw, setKardexesRaw] = useState([]); // Todos los datos sin filtrar
  const [selectedRow, setSelectedRow] = useState(null);

  const [formOpen, setFormOpen] = useState(false);
  const [formMode, setFormMode] = useState("create");

  /* ===== Filtros frontend ===== */
  const [filters, setFilters] = useState({
    fechaDesde: "",
    fechaHasta: "",
    tipoMovimientoId: "",
    estadoId: "",
  });

  const [message, setMessage] = useState({
    open: false,
    severity: "success",
    text: "",
  });
  const [searchDialogOpen, setSearchDialogOpen] = useState(false);

  // Artículos
  const [articuloItems, setArticuloItems] = useState([]);
  const [selectedArticulo, setSelectedArticulo] = useState({});
  const [reloadArticulos, setReloadArticulos] = useState(false);

  // Catálogos
  const [almacenes, setAlmacenes] = useState([]);
  const [producciones, setProducciones] = useState([]);
  const [tiposMovimiento, setTiposMovimiento] = useState([]);
  const [presentaciones, setPresentaciones] = useState([]);

  // 🔹 NUEVOS catálogos para mostrar nombres en GridKardex
  const [pedidos, setPedidos] = useState([]);
  const [ordenesCompra, setOrdenesCompra] = useState([]);
  const [empresas, setEmpresas] = useState([]);

  const theme = useTheme();

  // Paginación Kardex
  const [kardexPage, setKardexPage] = useState({ page: 0, size: 10 });
  const [totalKardex, setTotalKardex] = useState(0);
  const [loadingKardex, setLoadingKardex] = useState(false);

  // Paginación Artículos (mismo patrón que en Pedido)
  const [articuloPaginationModel, setArticuloPaginationModel] = useState({
    page: 0,
    size: 10,
  });
  const [articuloRowCount, setArticuloRowCount] = useState(0);
  const [articuloLoading, setArticuloLoading] = useState(false);

  // Normalizador base
  const toArray = (d) =>
    Array.isArray(d)
      ? d
      : d?.content ??
      d?.items ??
      d?.data ??
      d?.results ??
      [];

  // Normalizador de página (igual que en Pedido)
  const normalizePageResponse = (res) => {
    const data = res?.data ?? {};
    const rows = toArray(data);
    const total =
      data?.totalElements ??
      data?.page?.totalElements ??
      data?.total ??
      rows.length;
    return { rows, total };
  };

  // ------- Cargar catálogos una vez -------
  useEffect(() => {
    const loadCatalogs = async () => {
      try {
        const [
          rAlm,
          rProd,
          rTmov,
          rPres,
          rPed,
          rOc,
          rEmp,
        ] = await Promise.all([
          axios.get("/v1/items/almacen/0"),
          axios.get("/v1/items/produccion/0"),
          axios.get("/v1/items/tipo_movimiento/0"),
          axios.get("/v1/items/producto_presentacion/0"),
          axios.get("/v1/items/pedido/0"),
          axios.get("/v1/items/orden_compra/0"),
          axios.get("/v1/items/empresa/0"),
        ]);

        setAlmacenes(toArray(rAlm.data));
        setProducciones(toArray(rProd.data));
        setTiposMovimiento(toArray(rTmov.data));
        setPresentaciones(toArray(rPres.data));

        setPedidos(toArray(rPed.data));
        setOrdenesCompra(toArray(rOc.data));
        setEmpresas(toArray(rEmp.data));
      } catch (e) {
        setAlmacenes([]);
        setProducciones([]);
        setTiposMovimiento([]);
        setPresentaciones([]);
        setPedidos([]);
        setOrdenesCompra([]);
        setEmpresas([]);
      }
    };
    loadCatalogs();
  }, []);

  // ------- Cargar lista de kardex (SIN paginación, trae todo y filtra frontend) -------
  const reloadData = () => {
    setLoadingKardex(true);
    axios
      .get("/v1/kardex", { params: { page: 0, size: 1000 } }) // Trae máximo 1000
      .then((res) => {
        const rows = toArray(res.data);
        setKardexesRaw(rows); // Guardar todos los datos sin filtrar
      })
      .catch(() => {
        setMessage({
          open: true,
          severity: "error",
          text: "Error al cargar kardexes",
        });
        setKardexesRaw([]);
      })
      .finally(() => setLoadingKardex(false));
  };

  /* ===== Filtrar kardexes en frontend ===== */
  useEffect(() => {
    let filtered = kardexesRaw;

    // Filtro por fecha desde
    if (filters.fechaDesde) {
      const dateDesde = new Date(filters.fechaDesde);
      filtered = filtered.filter(
        (k) => new Date(k.fechaHora) >= dateDesde
      );
    }

    // Filtro por fecha hasta
    if (filters.fechaHasta) {
      const dateHasta = new Date(filters.fechaHasta);
      filtered = filtered.filter(
        (k) => new Date(k.fechaHora) <= dateHasta
      );
    }

    // Filtro por tipo de movimiento
    if (filters.tipoMovimientoId) {
      const tipoId = Number(filters.tipoMovimientoId);
      filtered = filtered.filter((k) => k.tipoMovimientoId === tipoId);
    }

    // Filtro por estado
    if (filters.estadoId !== "") {
      const estId = Number(filters.estadoId);
      filtered = filtered.filter((k) => k.estadoId === estId);
    }

    // Aplicar paginación al filtrado
    const { page, size } = kardexPage;
    const startIdx = page * size;
    const paginatedRows = filtered.slice(startIdx, startIdx + size);

    setKardexes(paginatedRows);
    setTotalKardex(filtered.length);

    if (paginatedRows.length > 0 && !selectedRow) setSelectedRow(paginatedRows[0]);
    if (paginatedRows.length === 0 && page > 0) {
      setKardexPage((p) => ({ ...p, page: 0 }));
    }
  }, [kardexesRaw, filters, kardexPage.page, kardexPage.size]);

  // ------- Cargar artículos del kardex -------
  const loadArticulos = async (kardexId) => {
    if (!kardexId) {
      setArticuloItems([]);
      setArticuloRowCount(0);
      return;
    }
    try {
      setArticuloLoading(true);
      const size = articuloPaginationModel.size;
      const page = articuloPaginationModel.page;
      const params = { page, size, sort: "id,desc" };

      // Endpoint adaptado para kardex
      const res = await axios.get(
        `/v1/articulo-kardex/${kardexId}/articulos`,
        { params }
      );

      const { rows, total } = normalizePageResponse(res);

      setArticuloItems(rows);

      const totalNum = Number(total);
      let effectiveTotal;
      if (Number.isFinite(totalNum) && totalNum > 0) {
        effectiveTotal = totalNum;
      } else {
        effectiveTotal =
          rows.length < size
            ? page * size + rows.length
            : (page + 2) * size;
      }
      setArticuloRowCount(effectiveTotal);

      if (rows.length === 0 && page > 0) {
        setArticuloPaginationModel((p) => ({ ...p, page: p.page - 1 }));
      }
    } catch (e) {
      setArticuloItems([]);
      setArticuloRowCount(0);
    } finally {
      setArticuloLoading(false);
    }
  };

  // Effects
  useEffect(() => {
    reloadData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  useEffect(() => {
    if (selectedRow) {
      loadArticulos(selectedRow.id);
    } else {
      setArticuloItems([]);
      setArticuloRowCount(0);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [
    selectedRow,
    reloadArticulos,
    articuloPaginationModel.page,
    articuloPaginationModel.size,
  ]);

  // Estilos
  const containerKardex = {
    backgroundColor: theme.palette.mode === "dark" ? "#1e2a2c" : "#c9e6fe",
    padding: 3,
    borderRadius: 2,
  };
  const containerArticulos = {
    backgroundColor: theme.palette.mode === "dark" ? "#2c383b" : "#caddf3",
    padding: 2,
    borderRadius: 2,
  };

  return (
    <Box sx={{ p: 2 }}>
      {/* KARDEX */}
      <Box sx={{ ...containerKardex, mb: 4 }}>
        <Box
          mb={2}
          display="flex"
          justifyContent="space-between"
          alignItems="center"
        >
          <Typography variant="h5">Gestión de Kardex</Typography>
          <Button
            variant="contained"
            onClick={() => setSearchDialogOpen(true)}
          >
            Buscar reporte
          </Button>
        </Box>

        <Box sx={{ mb: 2, display: "flex", gap: 2 }}>
          <Button
            variant="contained"
            color="primary"
            onClick={() => {
              setFormMode("create");
              setFormOpen(true);
            }}
          >
            + Agregar
          </Button>
          <Button
            variant="outlined"
            onClick={() => {
              setFormMode("edit");
              setFormOpen(true);
            }}
            disabled={!selectedRow}
          >
            Actualizar
          </Button>
          <Button
            variant="outlined"
            color="error"
            disabled={!selectedRow}
            onClick={async () => {
              if (!selectedRow) return;
              if (!window.confirm("¿Eliminar el Kardex seleccionado?")) return;
              try {
                await axios.delete(`/v1/kardex/${selectedRow.id}`);
                setMessage({
                  open: true,
                  severity: "success",
                  text: "Kardex eliminado correctamente.",
                });
                setSelectedRow(null);
                reloadData();
              } catch {
                setMessage({
                  open: true,
                  severity: "error",
                  text: "Error al eliminar el Kardex.",
                });
              }
            }}
          >
            Eliminar
          </Button>
        </Box>

        <FormKardex
          open={formOpen}
          setOpen={setFormOpen}
          formMode={formMode}
          selectedRow={selectedRow}
          setSelectedRow={setSelectedRow}
          reloadData={reloadData}
          setMessage={setMessage}
        />

        <Box sx={{ mt: 2 }}>
          <Typography variant="h6" gutterBottom>
            Lista de Kardex
          </Typography>
          <GridKardex
            kardexes={kardexes}
            almacenes={almacenes}
            producciones={producciones}
            tiposMovimiento={tiposMovimiento}
            pedidos={pedidos}
            ordenesCompra={ordenesCompra}
            empresas={empresas}
            selectedRow={selectedRow}
            setSelectedRow={setSelectedRow}
            loading={loadingKardex}
            rowCount={totalKardex}
            paginationModel={kardexPage}
            setPaginationModel={setKardexPage}
            isAdmin={isAdmin}
            filters={filters}
            setFilters={setFilters}
          />
        </Box>
      </Box>

      {/* ARTÍCULOS */}
      {selectedRow && (
        <Box sx={{ ...containerArticulos, mt: 4 }}>
          <Box
            display="flex"
            justifyContent="space-between"
            alignItems="center"
            mb={2}
          >
            <Typography variant="h6">
              Artículos del Kardex seleccionado
            </Typography>
            <Box display="flex" gap={2}>
              <FormArticuloKardex
                selectedRow={selectedArticulo}
                kardexId={selectedRow?.id || ""}
                setSelectedRow={setSelectedArticulo}
                setMessage={setMessage}
                reloadData={() => setReloadArticulos((prev) => !prev)}
              />
            </Box>
          </Box>

          <GridArticuloKardex
            items={articuloItems}
            setSelectedRow={setSelectedArticulo}
            loading={articuloLoading}
            paginationModel={articuloPaginationModel}
            setPaginationModel={setArticuloPaginationModel}
            rowCount={articuloRowCount}
            presentaciones={presentaciones}
            kardexId={selectedRow?.id}
          />
        </Box>
      )}

      <Dialog
        open={searchDialogOpen}
        onClose={() => setSearchDialogOpen(false)}
        fullWidth
        maxWidth="lg"
      >
        <ReKardex setOpen={setSearchDialogOpen} />
      </Dialog>

      <MessageSnackBar message={message} setMessage={setMessage} />
    </Box>
  );
}
