import React, { useEffect, useState, useMemo } from "react";
import axios from "../axiosConfig";
import MessageSnackBar from "../MessageSnackBar";
import FormOrdenCompra from "./FormOrdenCompra";
import GridOrdenCompra from "./GridOrdenCompra";
import FormArticuloOrdenCompra from "./FormArticuloOrdenCompra";
import GridArticuloOrdenCompra from "./GridArticuloOrdenCompra";
import ReOC from "../RE_oc/re_oc";
import { Box, Typography, Button, Dialog, useTheme } from "@mui/material";

export default function OrdenCompra() {
  const theme = useTheme();

  // -------------------- Estado principal --------------------
  const [ordenes, setOrdenes] = useState([]);
  const [selectedRow, setSelectedRow] = useState({});
  const [message, setMessage] = useState({ open: false, severity: "success", text: "" });
  const [searchDialogOpen, setSearchDialogOpen] = useState(false);

  // Artículos
  const [articuloItems, setArticuloItems] = useState([]);
  const [selectedArticulo, setSelectedArticulo] = useState({});
  const [reloadArticulos, setReloadArticulos] = useState(false);

  // -------------------- Paginación (principal) --------------------
  // Nota: normalizamos para aceptar {pageSize} o {size}
  const [paginationModel, setPaginationModel] = useState({ page: 0, size: 10, pageSize: 10 });
  const [sortModel, setSortModel] = useState([]);
  const [filterModel, setFilterModel] = useState({ items: [] });
  const [loading, setLoading] = useState(false);
  const [rowCount, setRowCount] = useState(0);

  // -------------------- Paginación (artículos) --------------------
  const [articuloPaginationModel, setArticuloPaginationModel] = useState({ page: 0, size: 5 });
  const [articuloRowCount, setArticuloRowCount] = useState(0);
  const [articuloLoading, setArticuloLoading] = useState(false);

  // Proveedores y Presentaciones (lookups)
  const [proveedores, setProveedores] = useState([]);
  const [presentaciones, setPresentaciones] = useState([]);

  // Estilos
  const containerOrdenes = {
    backgroundColor: theme.palette.mode === "dark" ? "#1e2a2c" : "#c9e6fe",
    padding: 3,
    borderRadius: 2,
  };
  const containerArticulos = {
    backgroundColor: theme.palette.mode === "dark" ? "#2c383b" : "#caddf3",
    padding: 2,
    borderRadius: 2,
  };

  // -------------------- Helpers --------------------
  const normalizePageResponse = (res) => {
    const d = res?.data;
    const isPage = d && typeof d === "object" && Array.isArray(d.content);
    if (isPage) {
      return {
        rows: d.content,
        total: Number(d.totalElements ?? d.total ?? 0),
      };
    }
    const headerTotal = Number(res?.headers?.["x-total-count"]);
    return {
      rows: Array.isArray(d) ? d : [],
      total: Number.isFinite(headerTotal) ? headerTotal : (Array.isArray(d) ? d.length : 0),
    };
  };

  // Normalizador para cambios de paginación del grid principal ({pageSize} o {size})
  const onOcGridPaginationChange = (next) => {
    setPaginationModel((prev) => {
      const nextSize = next.size ?? next.pageSize ?? prev.size ?? prev.pageSize ?? 10;
      const sizeChanged = (prev.size ?? prev.pageSize) !== nextSize;
      return {
        page: sizeChanged ? 0 : (next.page ?? prev.page ?? 0),
        size: nextSize,
        pageSize: nextSize, // mantenemos ambos para evitar incompatibilidades
      };
    });
  };

  // params para backend (principal)
  const queryParams = useMemo(() => {
    const size = paginationModel.size ?? paginationModel.pageSize ?? 10;
    const params = {
      page: paginationModel.page ?? 0, // 0-based; si tu API es 1-based usa (page + 1)
      size,
    };
    if (Array.isArray(sortModel) && sortModel.length > 0) {
      const { field, sort } = sortModel[0] || {};
      if (field && sort) params.sort = `${field},${sort}`;
    }
    const quickItem = filterModel?.items?.find((it) => it.value && !it.operator);
    if (quickItem?.value) params.q = String(quickItem.value);
    return params;
  }, [paginationModel, sortModel, filterModel]);

  // -------------------- Cargas iniciales lookups --------------------
  useEffect(() => {
    axios.get("/v1/items/proveedor/0")
      .then((res) => setProveedores(Array.isArray(res.data) ? res.data : []))
      .catch(() => setProveedores([]));

    axios.get("/v1/items/producto_presentacion/0")
      .then((res) => setPresentaciones(Array.isArray(res.data) ? res.data : []))
      .catch(() => setPresentaciones([]));
  }, []);

  const proveedoresMap = useMemo(
    () => Object.fromEntries((proveedores || []).map((p) => [Number(p.id), p.name ?? p.nombre])),
    [proveedores]
  );

  const presentacionesMap = useMemo(
    () => Object.fromEntries((presentaciones || []).map((pp) => [Number(pp.id), pp.name ?? pp.nombre])),
    [presentaciones]
  );

  // -------------------- Cargar órdenes con paginación --------------------
  const reloadData = async () => {
    try {
      setLoading(true);
      const res = await axios.get("/v1/orden-compra", { params: queryParams });
      const { rows, total } = normalizePageResponse(res);

      setOrdenes(rows);

      // rowCount efectivo (habilita flechas aunque el backend no mande total)
      const totalNum = Number(total);
      const size = queryParams.size;
      const page = queryParams.page;
      const effectiveTotal =
        Number.isFinite(totalNum) && totalNum > 0
          ? totalNum
          : rows.length < size
          ? page * size + rows.length
          : (page + 2) * size;

      setRowCount(effectiveTotal);

      // Mantener selección
      if (rows.length > 0) {
        const still = rows.find((r) => r.id === selectedRow?.id);
        setSelectedRow(still || rows[0]);
      } else {
        setSelectedRow({});
      }
    } catch (e) {
      setMessage({ open: true, severity: "error", text: "Error al cargar órdenes de compra" });
      setOrdenes([]);
      setRowCount(0);
      setSelectedRow({});
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    reloadData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [queryParams]);

  // -------------------- Artículos con paginación --------------------
  const reloadArticulosPage = async (ordenId) => {
    if (!ordenId) {
      setArticuloItems([]);
      setArticuloRowCount(0);
      return;
    }
    try {
      setArticuloLoading(true);
      const { page, size } = articuloPaginationModel;
      const params = { page, size, sort: "id,desc" }; // ajusta sort si necesitas

      const res = await axios.get(`/v1/orden-compra/${ordenId}/articulos`, { params });
      const { rows, total } = normalizePageResponse(res);
      setArticuloItems(rows);

      const totalNum = Number(total);
      const effectiveTotal =
        Number.isFinite(totalNum) && totalNum > 0
          ? totalNum
          : rows.length < size
          ? page * size + rows.length
          : (page + 2) * size;

      setArticuloRowCount(effectiveTotal);

      // Si la página queda vacía y no es la primera, retrocede
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

  // Cargar artículos al cambiar selección / paginación o cuando se pide recargar
  useEffect(() => {
    if (selectedRow?.id) reloadArticulosPage(selectedRow.id);
    else {
      setArticuloItems([]);
      setArticuloRowCount(0);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [selectedRow, articuloPaginationModel, reloadArticulos]);

  // -------------------- UI --------------------
  return (
    <Box sx={{ p: 2 }}>
      <Box sx={{ ...containerOrdenes, mb: 4 }}>
        <Box mb={2} display="flex" justifyContent="space-between" alignItems="center">
          <Typography variant="h5">Gestión de Órdenes de Compra</Typography>
          <Button variant="contained" onClick={() => setSearchDialogOpen(true)}>
            Buscar reporte
          </Button>
        </Box>

        <FormOrdenCompra
          selectedRow={selectedRow}
          setSelectedRow={setSelectedRow}
          reloadData={reloadData}
          setMessage={setMessage}
        />

        <Box sx={{ mt: 4 }}>
          <Typography variant="h6" gutterBottom>
            Lista de Órdenes de Compra
          </Typography>
          <GridOrdenCompra
  ordenes={ordenes}
  rowCount={rowCount}
  loading={loading}
  paginationModel={paginationModel}
  setPaginationModel={setPaginationModel}
  sortModel={sortModel}
  setSortModel={setSortModel}
  setFilterModel={setFilterModel}
  setSelectedRow={setSelectedRow}
  proveedoresMap={proveedoresMap}
  setMessage={setMessage}
  reloadData={reloadData} 
/>

        </Box>
      </Box>

      {selectedRow?.id && (
        <Box sx={{ ...containerArticulos, mt: 4 }}>
          <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
            <Typography variant="h6">Artículos de la Orden de Compra seleccionada</Typography>
            <Box display="flex" gap={2}>
              <FormArticuloOrdenCompra
                selectedRow={selectedArticulo}
                ordenCompraId={selectedRow.id}
                setSelectedRow={setSelectedArticulo}
                setMessage={setMessage}
                reloadData={() => setReloadArticulos((prev) => !prev)}
              />
            </Box>
          </Box>

          <GridArticuloOrdenCompra
            items={articuloItems}
            setSelectedRow={setSelectedArticulo}
            presentacionesMap={presentacionesMap}
            paginationModel={articuloPaginationModel}
            setPaginationModel={setArticuloPaginationModel}
            rowCount={articuloRowCount}
            loading={articuloLoading}
          />
        </Box>
      )}

      <Dialog
        open={searchDialogOpen}
        onClose={() => setSearchDialogOpen(false)}
        fullWidth
        maxWidth="lg"
      >
        <ReOC setOpen={setSearchDialogOpen} />
      </Dialog>

      <MessageSnackBar message={message} setMessage={setMessage} />
    </Box>
  );
}
