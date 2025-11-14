import React, { useState, useEffect, useCallback } from "react";
import axios from "../axiosConfig";
import MessageSnackBar from "../MessageSnackBar";
import FormTipounida from "./Formtipounida.jsx";
import GridTipounida from "./Gridtipounida.jsx";

export default function TipoUnida() {
  const [selectedRow, setSelectedRow] = useState({});
  const [message, setMessage] = useState({ open: false, severity: "success", text: "" });

  // filas
  const [rows, setRows] = useState([]);

  // paginación / carga
  const [rowCount, setRowCount] = useState(0);
  const [loading, setLoading] = useState(false);
  const [paginationModel, setPaginationModel] = useState({ page: 0, pageSize: 5 });

  // si el backend devuelve un array plano -> usamos modo cliente
  const [clientMode, setClientMode] = useState(false);

  // dialog del form
  const [formOpen, setFormOpen] = useState(false);

  const reloadData = useCallback(
    async (p = paginationModel.page, s = paginationModel.pageSize) => {
      try {
        setLoading(true);
        const { data, headers } = await axios.get("/v1/tipo-unidad", {
          params: { page: p, size: s },
        });

        // 1) normaliza lista
        const isArray = Array.isArray(data);
        const list = isArray
          ? data
          : Array.isArray(data?.content)
          ? data.content
          : Array.isArray(data?.data)
          ? data.data
          : [];

        const filas = list.map((m) => ({
          ...m,
          id: m?.id ?? m?.tipoUnidadId ?? m?.tipounidadId,
          estadoId: m?.estado?.id ?? m?.estadoId ?? null,
        }));
        setRows(filas);

        // 2) decide modo (cliente vs server) y total
        if (isArray) {
          setClientMode(true);
          setRowCount(filas.length); // el grid cortará en cliente
        } else {
          setClientMode(false);
          let total =
            data?.page?.totalElements ??
            data?.totalElements ??
            data?.total ??
            Number(headers?.["x-total-count"]) ??
            filas.length;
          if (total < filas.length) total = filas.length;
          setRowCount(Number(total));
        }
      } catch (err) {
        console.error("❌ /v1/tipo-unidad:", err);
        setMessage({ open: true, severity: "error", text: "Error al cargar Tipo de unidad" });
        setRows([]);
        setRowCount(0);
      } finally {
        setLoading(false);
      }
    },
    [paginationModel.page, paginationModel.pageSize]
  );

  // carga inicial
  useEffect(() => {
    reloadData(0, paginationModel.pageSize);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  // llamado cuando cambias página/tamaño (solo server)
  const handleSetPaginationModel = (next) => {
    const nextPage = Number(next.page ?? 0);
    const nextSize = Number(next.size ?? next.pageSize ?? 5);
    setPaginationModel({ page: nextPage, pageSize: nextSize });
    reloadData(nextPage, nextSize);
  };

  return (
    <div>
      <h1>Tipo de unidad</h1>

      <MessageSnackBar message={message} setMessage={setMessage} />

      <FormTipounida
        open={formOpen}
        setOpen={setFormOpen}
        selectedRow={selectedRow}
        setSelectedRow={setSelectedRow}
        setMessage={setMessage}
        reloadData={() => reloadData(paginationModel.page, paginationModel.pageSize)}
      />

      <GridTipounida
        rows={rows}
        selectedRow={selectedRow}
        setSelectedRow={setSelectedRow}
        loading={loading}
        {...(!clientMode
          ? {
              rowCount,
              paginationModel,
              setPaginationModel: handleSetPaginationModel,
            }
          : {})}
      />
    </div>
  );
}
