/*=============================================================================
 Nombre del archivo : i18n.js
 Descripcion        : Inicializa la internacionalización y resuelve el idioma activo.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |   Fecha    | Versión |      Autor           | Descripción del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2026-05-08 | 0.4.0   | Cesar Medina         | Creación del archivo.       |
 +------------+---------+----------------------+-----------------------------+
 | 2026-06-06 | 0.4.0   | Jeisson Sanchez      | Traducciones geográficas.   |
 +------------+---------+----------------------+-----------------------------+
 | 2026-06-26 | 0.4.0   | Jeisson Sanchez      | Traducciones Kardex.        |
 +------------+---------+----------------------+-----------------------------+
=============================================================================*/
/**
 * @module i18n
 * @description Inicializa `i18next`, registra recursos por idioma/módulo y
 * expone una utilidad para resolver el idioma actual de la aplicación.
 */
import i18n from "i18next";
import { initReactI18next } from "react-i18next";
import LanguageDetector from "i18next-browser-languagedetector";
import esCommon from "./locales/es/common.json";
import esAuth from "./locales/es/auth.json";
import esUsuario from "./locales/es/usuario.json";
import esAlmacen from "./locales/es/almacen.json";
import esEmpresaRol from "./locales/es/empresaRol.json";
import esLocalizacionGeografica from "./locales/es/localizacionGeografica.json";
import esVencimiento from "./locales/es/vencimiento.json";
import enCommon from "./locales/en/common.json";
import enAuth from "./locales/en/auth.json";
import enUsuario from "./locales/en/usuario.json";
import enAlmacen from "./locales/en/almacen.json";
import enEmpresaRol from "./locales/en/empresaRol.json";
import enLocalizacionGeografica from "./locales/en/localizacionGeografica.json";
import enVencimiento from "./locales/en/vencimiento.json";

const normalizeLanguage = (language) =>
  String(language || "")
    .toLowerCase()
    .startsWith("en")
    ? "en"
    : "es";

export const resolveAppLanguage = (language) =>
  normalizeLanguage(
    language ||
      localStorage.getItem("preferredLanguage") ||
      localStorage.getItem("i18nextLng") ||
      navigator.language
  );

i18n
  .use(LanguageDetector)
  .use(initReactI18next)
  .init({
    resources: {
      es: {
        translation: {
          common: esCommon,
          auth: esAuth,
          usuario: esUsuario,
          almacen: esAlmacen,
          empresaRol: esEmpresaRol,
          localizacionGeografica: esLocalizacionGeografica,
          vencimiento: esVencimiento,
        },
      },
      en: {
        translation: {
          common: enCommon,
          auth: enAuth,
          usuario: enUsuario,
          almacen: enAlmacen,
          empresaRol: enEmpresaRol,
          localizacionGeografica: enLocalizacionGeografica,
          vencimiento: enVencimiento,
        },
      },
    },
    fallbackLng: "es",
    supportedLngs: ["es", "en"],
    load: "languageOnly",
    detection: {
      order: ["localStorage", "navigator", "htmlTag"],
      caches: ["localStorage"],
      lookupLocalStorage: "preferredLanguage",
    },
    interpolation: {
      escapeValue: false,
    },
    returnNull: false,
  });

export default i18n;
