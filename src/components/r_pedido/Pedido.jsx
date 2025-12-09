import React, { useEffect, useMemo, useState } from "react";
import axios from "../axiosConfig";
import MessageSnackBar from "../MessageSnackBar";
import FormPedido from "./FormPedido";
import GridPedido from "./GridPedido";
import GridArticuloPedido from "./GridArticuloPedido";
import FormArticuloPedido from "./FormArticuloPedido";
import RePV from "../RE_pedido/re_pv";
import { Box, Typography, Button, Dialog, useTheme } from "@mui/material";

export default function Pedido() {
  const theme = useTheme();

  // -------------------- Estado principal --------------------
  const [pedidos, setPedidos] = useState([]);
  const [selectedRow, setSelectedRow] = useState(null);

  const [formOpen, setFormOpen] = useState(false);
  const [formMode, setFormMode] = useState("create");
  const [message, setMessage] = useState({ open: false, severity: "success", text: "" });
  const [searchDialogOpen, setSearchDialogOpen] = useState(false);

  // Artículos
  const [articuloItems, setArticuloItems] = useState([]);
  const [selectedArticulo, setSelectedArticulo] = useState({});
  const [reloadArticulos, setReloadArticulos] = useState(false);
  const [presentaciones, setPresentaciones] = useState([]);

  // Lookups
  const [producciones, setProducciones] = useState([]);
  const [almacenes, setAlmacenes] = useState([]);
  const [estados, setEstados] = useState([]);

  // -------------------- Paginación (patrón Espacio) --------------------
  // Pedidos
  const [pedidoPaginationModel, setPedidoPaginationModel] = useState({ page: 0, size: 5 });
  const [pedidoRowCount, setPedidoRowCount] = useState(0);
  const [pedidoLoading, setPedidoLoading] = useState(false);

  // Artículos
  const [articuloPaginationModel, setArticuloPaginationModel] = useState({ page: 0, size: 5 });
  const [articuloRowCount, setArticuloRowCount] = useState(0);
  const [articuloLoading, setArticuloLoading] = useState(false);

  // -------------------- Utils --------------------
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

  const toArray = (d) => (Array.isArray(d) ? d : (d?.content ?? d?.items ?? d?.data ?? d?.results ?? []));

  // -------------------- Cargas iniciales --------------------
  useEffect(() => {
    axios.get("/v1/items/almacen/0")
      .then((r) => setAlmacenes(toArray(r.data)))
      .catch(() => setAlmacenes([]));

    axios.get("/v1/items/produccion/0")
      .then((r) => setProducciones(toArray(r.data)))
      .catch(() => setProducciones([]));

    axios.get("/v1/items/pedido_estado/0")
      .then((r) => setEstados(toArray(r.data)))
      .catch(() => setEstados([]));

    axios.get("/v1/items/producto_presentacion/0")
      .then((r) => setPresentaciones(toArray(r.data)))
      .catch(() => setPresentaciones([]));
  }, []);

  // -------------------- Pedidos: listar con paginación --------------------
  const reloadPedidos = async () => {
  try {
    setPedidoLoading(true);
    const size = pedidoPaginationModel.size;
    const page = pedidoPaginationModel.page; // 0-based (si tu API es 1-based usa page+1)
    const params = { page, size, sort: "id,desc" };

    const res = await axios.get("/v1/pedido", { params });
    const { rows, total } = normalizePageResponse(res);

    setPedidos(rows);

    // ----- Cálculo de rowCount efectivo -----
    const totalNum = Number(total);
    let effectiveTotal;

    if (Number.isFinite(totalNum) && totalNum > 0) {
      // Backend nos dio un total correcto
      effectiveTotal = totalNum;
    } else {
      // No hay total fiable. Estimamos:
      if (rows.length < size) {
        // Última página: total exacto = filas mostradas hasta aquí
        effectiveTotal = page * size + rows.length;
      } else {
        // Aún puede haber más páginas: anuncia al menos 1 página más
        effectiveTotal = (page + 2) * size; // “2” => página actual + una más
      }
    }

    setPedidoRowCount(effectiveTotal);

    // Selección inicial
    if (rows.length > 0 && !selectedRow) {
      setSelectedRow(rows[0]);
    }

    // Si la página quedó vacía y no es la primera, retrocede
    if (rows.length === 0 && page > 0) {
      setPedidoPaginationModel((p) => ({ ...p, page: p.page - 1 }));
    }
  } catch (e) {
    setMessage({ open: true, severity: "error", text: "Error al cargar pedidos" });
    setPedidos([]);
    setPedidoRowCount(0);
  } finally {
    setPedidoLoading(false);
  }
};

  // Montaje y cuando cambia la paginación de pedidos
  useEffect(() => { reloadPedidos(); /* eslint-disable-next-line */ }, [pedidoPaginationModel]);

  // -------------------- Artículos: listar con paginación --------------------
const loadArticulos = async (pedidoId) => {
  if (!pedidoId) {
    setArticuloItems([]);
    setArticuloRowCount(0);
    return;
  }
  try {
    setArticuloLoading(true);
    const size = articuloPaginationModel.size;
    const page = articuloPaginationModel.page;
    const params = { page, size, sort: "id,desc" };

    const res = await axios.get(`/v1/pedido/${pedidoId}/articulos`, { params });
    const { rows, total } = normalizePageResponse(res);

    setArticuloItems(rows);

    const totalNum = Number(total);
    let effectiveTotal;
    if (Number.isFinite(totalNum) && totalNum > 0) {
      effectiveTotal = totalNum;
    } else {
      effectiveTotal = rows.length < size ? page * size + rows.length : (page + 2) * size;
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


  // Cuando cambia pedido seleccionado, paginación de artículos o recarga
  useEffect(() => {
    if (selectedRow?.id) loadArticulos(selectedRow.id);
    else {
      setArticuloItems([]);
      setArticuloRowCount(0);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [selectedRow, articuloPaginationModel, reloadArticulos]);

  // -------------------- Handlers de paginación (patrón Espacio) --------------------
  // El grid emite { page, pageSize } → convertimos a { page, size }
  const onPedidoGridPageChange = (model) => {
    setPedidoPaginationModel((prev) => {
      const nextSize = model.pageSize ?? model.size ?? prev.size ?? 5;
      const sizeChanged = (prev.size ?? prev.pageSize) !== nextSize;
      return {
        page: sizeChanged ? 0 : (model.page ?? 0),
        size: nextSize,
      };
    });
  };

  const onArticuloGridPageChange = (model) => {
    setArticuloPaginationModel((prev) => {
      const nextSize = model.pageSize ?? model.size ?? prev.size ?? 5;
      const sizeChanged = (prev.size ?? prev.pageSize) !== nextSize;
      return {
        page: sizeChanged ? 0 : (model.page ?? 0),
        size: nextSize,
      };
    });
  };

  // -------------------- Estilos --------------------
  const containerPedidos = useMemo(() => ({
    backgroundColor: theme.palette.mode === "dark" ? "#1e2a2c" : "#c9e6fe",
    padding: 3,
    borderRadius: 2,
  }), [theme.palette.mode]);

  const containerArticulos = useMemo(() => ({
    backgroundColor: theme.palette.mode === "dark" ? "#2c383b" : "#caddf3",
    padding: 2,
    borderRadius: 2,
  }), [theme.palette.mode]);

  // -------------------- UI --------------------
  return (
    <Box sx={{ p: 2 }}>
      {/* Contenedor principal de Pedidos */}
      <Box sx={{ ...containerPedidos, mb: 4 }}>
        <Box mb={2} display="flex" justifyContent="space-between" alignItems="center">
          <Typography variant="h5">Gestión de Pedido</Typography>
          <Button variant="contained" onClick={() => setSearchDialogOpen(true)}>
            Generar reporte de pedido
          </Button>
        </Box>

        <Box sx={{ mb: 2, display: "flex", gap: 2 }}>
          <Button
            variant="contained"
            color="primary"
            onClick={() => { setFormMode("create"); setFormOpen(true); }}
          >
            + Crear
          </Button>
          <Button
            variant="outlined"
            onClick={() => { setFormMode("edit"); setFormOpen(true); }}
            disabled={!selectedRow}
          >
            Actualizar
          </Button>
          <Button
            variant="outlined"
            color="error"
            onClick={async () => {
              if (!selectedRow) return;
              if (window.confirm("¿Eliminar el pedido seleccionado?")) {
                try {
                  await axios.delete(`/v1/pedido/${selectedRow.id}`);
                  setMessage({ open: true, severity: "success", text: "Pedido eliminado correctamente." });
                  setSelectedRow(null);
                  reloadPedidos();
                } catch {
                  setMessage({ open: true, severity: "error", text: "Error al eliminar el pedido." });
                }
              }
            }}
            disabled={!selectedRow}
          >
            Eliminar
          </Button>
        </Box>

        <FormPedido
          open={formOpen}
          setOpen={setFormOpen}
          formMode={formMode}
          selectedRow={selectedRow}
          setSelectedRow={setSelectedRow}
          reloadData={reloadPedidos}
          setMessage={setMessage}
          almacenId={selectedRow?.almacenId || ""}
        />

        <Box sx={{ mt: 2 }}>
          <Typography variant="h6" gutterBottom>Lista de Pedidos</Typography>
          <GridPedido
            pedidos={pedidos}
            selectedRow={selectedRow}
            setSelectedRow={setSelectedRow}

            // Server-side
            loading={pedidoLoading}
            rowCount={pedidoRowCount}
            // El grid espera { page, pageSize }; convertimos nuestro { page, size }
            paginationModel={{ page: pedidoPaginationModel.page, pageSize: pedidoPaginationModel.size }}
            onPaginationModelChange={onPedidoGridPageChange}

            // Extras
            producciones={producciones}
            almacenes={almacenes}
            estados={estados}

            onAnularPedido={(pedido) => anularPedido(pedido)}         // PUT /pedido/anular
            onCompletarPedido={(pedido) => completarPedido(pedido)}   // PUT /pedido/completar
          />
        </Box>
      </Box>

      {/* Contenedor de Artículos */}
      {selectedRow && (
        <Box sx={{ ...containerArticulos, mt: 4 }}>
          <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
            <Typography variant="h6">Artículos del Pedido seleccionado</Typography>
            <Box display="flex" gap={2}>
              <FormArticuloPedido
                selectedRow={selectedArticulo}
                pedidoId={selectedRow?.id || ""}
                setSelectedRow={setSelectedArticulo}
                setMessage={setMessage}
                reloadData={() => setReloadArticulos((v) => !v)}
              />
            </Box>
          </Box>

          <GridArticuloPedido
            items={articuloItems}
            setSelectedRow={setSelectedArticulo}
            presentaciones={presentaciones}

            // Server-side
            loading={articuloLoading}
            rowCount={articuloRowCount}
            paginationModel={{ page: articuloPaginationModel.page, pageSize: articuloPaginationModel.size }}
            onPaginationModelChange={onArticuloGridPageChange}
          />
        </Box>
      )}

      <Dialog open={searchDialogOpen} onClose={() => setSearchDialogOpen(false)} fullWidth maxWidth="lg">
        <RePV setOpen={setSearchDialogOpen} />
      </Dialog>

      <MessageSnackBar message={message} setMessage={setMessage} />
    </Box>
  );
}
