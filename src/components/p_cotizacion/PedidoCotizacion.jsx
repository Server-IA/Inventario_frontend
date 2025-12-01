import React, { useState, useEffect, useCallback } from "react";
import axios from "../axiosConfig";
import MessageSnackBar from "../MessageSnackBar";
import FormPedidoCotizacion from "./FormPedidoCotizacion.jsx";
import GridPedidoCotizacion from "./GridPedidoCotizacion.jsx";

export default function PedidoCotizacion() {
  const [selectedRow, setSelectedRow] = useState({});
  const [rows, setRows] = useState([]);
  const [formOpen, setFormOpen] = useState(false);

  const [message, setMessage] = useState({
    open: false,
    severity: "success",
    text: "",
  });

  const [pedidos, setPedidos] = useState([]);
  const [proveedores, setProveedores] = useState([]);

  // normaliza a array (por si viene en .content)
  const asArray = (x) => (Array.isArray(x) ? x : x?.content ?? x?.data ?? []);

  // ==== CRUD principal ====
  const reloadData = useCallback(async () => {
    try {
      const res = await axios.get("/v1/pedido-cotizacion", {
        params: { page: 0, size: 1000 },
      });

      const list = asArray(res.data);
      setRows(list);
    } catch (error) {
      console.error(error);
      setMessage({
        open: true,
        severity: "error",
        text: "Error al cargar pedidos cotización",
      });
    }
  }, []);

  // ==== combos ====
  const loadPedidos = useCallback(async () => {
    try {
      // usar /v1/items/pedido/0 como dijiste
      const res = await axios.get("/v1/items/pedido/0");
      setPedidos(asArray(res.data));
    } catch (err) {
      console.error(err);
    }
  }, []);

  const loadProveedores = useCallback(async () => {
    try {
      // asumo mismo patrón de items para proveedor
      const res = await axios.get("/v1/items/proveedor/0");
      setProveedores(asArray(res.data));
    } catch (err) {
      console.error(err);
    }
  }, []);

  useEffect(() => {
    reloadData();
    loadPedidos();
    loadProveedores();
  }, [reloadData, loadPedidos, loadProveedores]);

  return (
    <div>
      <h1>Pedido Cotización</h1>

      <MessageSnackBar message={message} setMessage={setMessage} />

      <FormPedidoCotizacion
        open={formOpen}
        setOpen={setFormOpen}
        selectedRow={selectedRow || {}}
        setSelectedRow={setSelectedRow}
        setMessage={setMessage}
        reloadData={reloadData}
        pedidos={pedidos}
        proveedores={proveedores}
      />

      <GridPedidoCotizacion
        rows={rows}
        setSelectedRow={setSelectedRow}
      />
    </div>
  );
}
