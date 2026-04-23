// CREADO: MARIA
// FECHA DE CREACION: 15/08
// FECHA DE MODIFICACION: 
import i18n from "i18next";
import { initReactI18next } from "react-i18next";
import LanguageDetector from "i18next-browser-languagedetector";
import enTranslation from "./locales/en/translation.json";
import esTranslation from "./locales/es/translation.json";

i18n
  .use(LanguageDetector) // Detecta el idioma del navegador
  .use(initReactI18next) // Pasa i18n a react-i18next
  .init({
    resources: {
      en: {
        translation: enTranslation,
      },
      es: {
        translation: esTranslation,
      }
    },
    fallbackLng: "es", // Idioma por defecto
    interpolation: {
      escapeValue: false // React ya escapa por defecto
    }
  });

export default i18n;
