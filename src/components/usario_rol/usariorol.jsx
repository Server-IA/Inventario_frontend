import React, { useState, useEffect } from "react";
import axios from "../axiosConfig";
import MessageSnackBar from "../MessageSnackBar";
import FormUsuarioRol from "./FormUsuarioRol";
import GridUsuarioRol from "./GridUsuarioRol";
import { Paper, Typography } from "@mui/material";

export default function UsuarioRol() {
  const [selectedRow, setSelectedRow] = useState({});
  const [message, setMessage] = useState({ open: false, severity: "success", text: "" });

  const [rows, setRows] = useState([]);
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [rowCount, setRowCount] = useState(0);
  const [loading, setLoading] = useState(false);

  const reloadData = async (pageArg = page, sizeArg = pageSize) => {
    const token = localStorage.getItem("token");
    const headers = { headers: { Authorization: `Bearer ${token}` } };

    try {
      setLoading(true);
      const { data } = await axios.get("/v1/usuario-roles", {
        ...headers,
        params: {
          page: pageArg,
          size: sizeArg,
          sort: "id,desc", 
        },
      });

      const content = data.content || [];
      const meta = data.page || {};
      const total = data.totalElements ?? meta.totalElements ?? content.length;

      setRows(content);
      setRowCount(total);
      setPage(data.number ?? meta.number ?? pageArg);
      setPageSize(data.size ?? meta.size ?? sizeArg);

    } catch (err) {
      console.error("❌ Error API:", err);
      setMessage({ open: true, severity: "error", text: "Error de conexión con el servidor" });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    reloadData(0, pageSize);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const handlePaginationModelChange = (model) => {
    if (model.size !== pageSize) {
      setPageSize(model.size);
      setPage(0);
      reloadData(0, model.size);
    } else {
      setPage(model.page);
      reloadData(model.page, pageSize);
    }
  };

  return (
    <div style={{ padding: '20px' }}>
      <Typography variant="h5" color="primary" sx={{ mb: 2, fontWeight: 'bold' }}>
        Gestión de Usuarios y Roles
      </Typography>

      <MessageSnackBar message={message} setMessage={setMessage} />

      <FormUsuarioRol
        selectedRow={selectedRow}
        setSelectedRow={setSelectedRow}
        setMessage={setMessage}
        reloadData={() => reloadData(page, pageSize)}
      />

      <Paper elevation={2} sx={{ mt: 2 }}>
        <GridUsuarioRol
          rows={rows}
          selectedRow={selectedRow}
          setSelectedRow={setSelectedRow}
          paginationModel={{ page, pageSize }}
          setPaginationModel={handlePaginationModelChange}
          rowCount={rowCount}
          loading={loading}
        />
      </Paper>
    </div>
  );
}