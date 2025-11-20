import React, { useState, useEffect, useCallback } from "react";
import axios from "../axiosConfig";
import MessageSnackBar from "../MessageSnackBar";
import FormIngrediente from "./FormIngrediente";
import GridIngrediente from "./GridIngrediente";

export default function Ingrediente() {
  const [selectedRow, setSelectedRow] = useState({});
  const [message, setMessage] = useState({
    open: false,
    severity: "success",
    text: "",
  });

  const [ingredientes, setIngredientes] = useState([]);
  const [loading, setLoading] = useState(false);

  // Dialog
  const [formOpen, setFormOpen] = useState(false);

  // Paginación server-side (SIEMPRE con page + pageSize)
  const [paginationModel, setPaginationModel] = useState({
    page: 0,
    pageSize: 10,
  });
  const [rowCount, setRowCount] = useState(0);

  const reloadData = useCallback(async () => {
    const { page, pageSize } = paginationModel;

    try {
      setLoading(true);

      const res = await axios.get("/v1/ingrediente", {
        params: { page, size: pageSize },
      });

      const data = res?.data ?? {};
      const list = Array.isArray(data) ? data : data.content ?? [];

      const filas = list.map((it) => ({
        ...it,
        estadoId: it.estado?.id ?? it.estadoId ?? null,
      }));

      setIngredientes(filas);

      if (!Array.isArray(data)) {
        setRowCount(Number(data.page?.totalElements ?? filas.length));
      } else {
        setRowCount(filas.length);
      }
    } catch (err) {
      setMessage({
        open: true,
        severity: "error",
        text: "Error al cargar ingredientes",
      });
    } finally {
      setLoading(false);
    }
  }, [paginationModel.page, paginationModel.pageSize]);

  // Cargar al inicio y cada vez que cambie page/pageSize
  useEffect(() => {
    reloadData();
  }, [reloadData]);

  return (
    <div>
      <h1>Ingredientes</h1>

      <MessageSnackBar message={message} setMessage={setMessage} />

      <FormIngrediente
        open={formOpen}
        setOpen={setFormOpen}
        selectedRow={selectedRow || {}}
        setSelectedRow={setSelectedRow}
        setMessage={setMessage}
        reloadData={reloadData}
      />

      <GridIngrediente
        rows={ingredientes}
        selectedRow={selectedRow}
        setSelectedRow={setSelectedRow}
        loading={loading}
        paginationModel={paginationModel}
        setPaginationModel={setPaginationModel}
        rowCount={rowCount}
      />
    </div>
  );
}
