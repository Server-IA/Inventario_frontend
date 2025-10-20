import React, { useState, useEffect, useCallback } from "react";
import axios from "../axiosConfig";
import MessageSnackBar from "../MessageSnackBar";
import FormUnidad from "./FormUnidad";
import GridUnidad from "./GridUnidad";

export default function Unidad() {
  const [selectedRow, setSelectedRow] = useState(null);
  const [message, setMessage] = useState({ open: false, severity: "success", text: "" });
  const [rows, setRows] = useState([]);

  const [paginationModel, setPaginationModel] = useState({ page: 0, pageSize: 10 });
  const [rowCount, setRowCount] = useState(0);
  const [loading, setLoading] = useState(false);

  const fetchPage = useCallback(async (pageArg = paginationModel.page, sizeArg = paginationModel.pageSize) => {
    try {
      setLoading(true);
      const { data } = await axios.get("/v1/unidad", {
        params: { page: pageArg, size: sizeArg },
      });

      const contenido = Array.isArray(data?.content) ? data.content : [];
      const filas = contenido.map((item) => ({
        ...item,
        estadoId: item?.estado?.id ?? item?.estadoId,
      }));

      setRows(filas);
      setRowCount(data?.page?.totalElements ?? filas.length);
    } catch (err) {
      setMessage({ open: true, severity: "error", text: "Error al cargar unidades" });
    } finally {
      setLoading(false);
    }
  }, [paginationModel]);

  useEffect(() => {
    fetchPage();
  }, [fetchPage, paginationModel]);

  const reloadData = () => fetchPage(0, paginationModel.pageSize);

  return (
    <div>
      <h1>Unidades</h1>

      <MessageSnackBar message={message} setMessage={setMessage} />

      <FormUnidad
        selectedRow={selectedRow}
        setSelectedRow={setSelectedRow}
        setMessage={setMessage}
        reloadData={reloadData}
      />

      <GridUnidad
        rows={rows}
        selectedRow={selectedRow}
        setSelectedRow={setSelectedRow}
        paginationModel={paginationModel}
        setPaginationModel={setPaginationModel}
        rowCount={rowCount}
        loading={loading}
      />
    </div>
  );
}
