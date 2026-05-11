/*=============================================================================
 Nombre del archivo : axiosConfig.js
 Descripcion        : Cliente Axios principal para consumir la API base del backend.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |   Fecha    | Versión |      Autor           | Descripción del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2026-05-08 | 0.4.0   | Cesar Medina         | Creación del archivo.       |
 +------------+---------+----------------------+-----------------------------+
=============================================================================*/
/**
 * @module axiosConfig
 * @description Configura el cliente Axios principal con `Authorization` y
 * `Accept-Language` para las rutas servidas desde `/api`.
 */
import axios from "axios";
import { resolveAppLanguage } from "../i18n.js";

const instance = axios.create({
  baseURL: import.meta.env.VITE_BACKEND_URI + "/api",
  headers: { "Content-Type": "application/json" },
});

instance.interceptors.request.use(
  (config) => {
    config.headers = config.headers || {};
    config.headers["Accept-Language"] = resolveAppLanguage();

    if (config.skipAuth) {
      if (config.headers.Authorization) delete config.headers.Authorization;
      config.headers = { ...config.headers, Authorization: undefined };
      return config;
    }

    const token = localStorage.getItem("token");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    } else if (config.headers.Authorization) {
      delete config.headers.Authorization;
    }
    return config;
  },
  (e) => Promise.reject(e)
);

export default instance;

