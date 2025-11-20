import React, { useState, useEffect, useCallback } from "react";
import axios from "../axiosConfig";
import MessageSnackBar from "../MessageSnackBar";
import FormMarca from "./FormMarca";
import GridMarca from "./GridMarca";

export default function Marca() {
  const [selectedRow, setSelectedRow] = useState({});
  const [message, setMessage] = useState({
    open: false,
    severity: "success",
    text: "",
  });
  const [marcas, setMarcas] = useState([]);
  const [formOpen, setFormOpen] = useState(false);

  // paginación NUEVO estilo
  const [paginationModel, setPaginationModel] = useState({
    page: 0,
    pageSize: 5,          // 👈 arranca en 5 filas
  });
  const [rowCount, setRowCount] = useState(0);
  const [loading, setLoading] = useState(false);

  const reloadData = useCallback(
    async (model = paginationModel) => {
      const page = model.page ?? 0;
      const size = model.pageSize ?? 5;

      try {
        setLoading(true);
        const { data } = await axios.get("/v1/marca", {
          params: { page, size },
        });

        const list = Array.isArray(data)
          ? data
          : Array.isArray(data?.content)
          ? data.content
          : [];

        const filas = list.map((m) => ({
          ...m,
          estadoId: m.estado?.id ?? m.estadoId ?? null,
        }));
        setMarcas(filas);

        // info de paginación del backend
        if (!Array.isArray(data) && data.page) {
          setRowCount(Number(data.page.totalElements ?? filas.length));
        } else {
          setRowCount(filas.length);
        }
      } catch (err) {
        console.error("❌ Error al cargar marcas:", err);
        setMessage({
          open: true,
          severity: "error",
          text: "Error al cargar marcas",
        });
        setMarcas([]);
      } finally {
        setLoading(false);
      }
    },
    [paginationModel]
  );

  // recargar cada vez que cambie page o pageSize
  useEffect(() => {
    reloadData(paginationModel);
  }, [reloadData, paginationModel.page, paginationModel.pageSize]);

  return (
    <div>
      <h1>Marcas</h1>

      <MessageSnackBar message={message} setMessage={setMessage} />

      <FormMarca
        open={formOpen}
        setOpen={setFormOpen}
        selectedRow={selectedRow}
        setSelectedRow={setSelectedRow}
        setMessage={setMessage}
        // cuando guardes, recarga la página actual
        reloadData={() => reloadData(paginationModel)}
      />

      <GridMarca
        rows={marcas}
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
