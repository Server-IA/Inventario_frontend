/*=============================================================================
 Nombre del archivo : Empresa.jsx
 Descripcion        : Componente principal del módulo de empresas (HU-043.1).
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |   Fecha    | Versión |      Autor           | Descripción del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2026-08-16 | 0.5.0   | Jeisson Sanchez      | HU-043.1 Registrar empresa  |
 +------------+---------+----------------------+-----------------------------+
=============================================================================*/
/**
 * @file Empresa.jsx
 * @module Empresa
 * @description Componente principal para la gestión de empresas.
 * @author Jeisson Sanchez
 */

import * as React from "react";
import axios from "../axiosConfig";
import { useTranslation } from "react-i18next";
import MessageSnackBar from "../MessageSnackBar";
import GridActionBar from "../common/GridActionBar";
import FormEmpresa from "./FormEmpresa";
import GridEmpresa from "./GridEmpresa";

/**
 * @typedef {Object} SnackbarMessage
 * @property {boolean} open - Si el mensaje está visible
 * @property {string} severity - Nivel de severidad ("success", "error", etc.)
 * @property {string} text - Texto del mensaje
 */

/**
 * Componente principal para la gestión de empresas.
 *
 * @returns {JSX.Element} El módulo de gestión de empresas
 */
export default function Empresa() {
  const { t } = useTranslation();
  const [message, setMessage] = React.useState(
    /** @type {SnackbarMessage} */ ({
      open: false,
      severity: "success",
      text: "",
    })
  );
  const [personas, setPersonas] = React.useState([]);
  const [tiposIdentificacion, setTiposIdentificacion] = React.useState([]);
  const [openForm, setOpenForm] = React.useState(false);

  /**
   * Carga las personas y los tipos de identificación usados por el formulario.
   */
  const reloadData = React.useCallback(() => {
    axios
      .get("/v1/persona")
      .then((res) => setPersonas(res.data.content || []))
      .catch((err) => console.error("Error al cargar personas:", err));

    axios
      .get("/v1/tipo_identificacion")
      .then((res) => setTiposIdentificacion(res.data || []))
      .catch((err) => console.error("Error al cargar tipos de identificación:", err));
  }, []);

  React.useEffect(() => {
    reloadData();
  }, [reloadData]);

  /**
   * Abre el formulario de registro de una nueva empresa.
   */
  const handleAdd = () => {
    setOpenForm(true);
  };

  return (
    <div style={{ height: "100%", width: "100%" }}>
      <h1>{t("empresa.title", "Gestión de Empresas")}</h1>

      <MessageSnackBar message={message} setMessage={setMessage} />

      <GridActionBar onAdd={handleAdd} />

      <FormEmpresa
        personas={personas}
        tiposIdentificacion={tiposIdentificacion}
        setMessage={setMessage}
        reloadData={reloadData}
        open={openForm}
        setOpen={setOpenForm}
      />

      <GridEmpresa />
    </div>
  );
}