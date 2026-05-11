/*=============================================================================
 Nombre del archivo : axiosConfig2.js
 Descripcion        : Cliente Axios para endpoints versionados bajo /api/v2.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |   Fecha    | Versión |      Autor           | Descripción del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2026-05-08 | 0.4.0   | Cesar Medina         | Creación del archivo.       |
 +------------+---------+----------------------+-----------------------------+
=============================================================================*/
/**
 * @module axiosConfig2
 * @description Configura el cliente Axios secundario para la API `v2`,
 * conservando autenticación y encabezado de idioma.
 */
import axios from 'axios';
import { resolveAppLanguage } from "../i18n.js";

const axiosV2 = axios.create({
  baseURL: import.meta.env.VITE_BACKEND_URI+'/api/v2',
  headers: { 'Content-Type': 'application/json' },
});

axiosV2.interceptors.request.use(config => {
  config.headers = config.headers || {};
  config.headers["Accept-Language"] = resolveAppLanguage();
  const token = localStorage.getItem('token');
  if (token) config.headers['Authorization'] = `Bearer ${token}`;
  return config;
}, error => Promise.reject(error));

export default axiosV2;
