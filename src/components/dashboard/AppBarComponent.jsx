/*=============================================================================
 Nombre del archivo : AppBarComponent.jsx
 Descripcion        : Barra superior principal con tema, idioma y acceso al perfil.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |   Fecha    | Versión |      Autor           | Descripción del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2026-05-08 | 0.4.0   | Cesar Medina         | Creación del archivo.       |
 +------------+---------+----------------------+-----------------------------+
=============================================================================*/
/**
 * @module AppBarComponent
 * @description Renderiza la barra superior principal de la aplicación e integra
 * navegación pública, selector de idioma, cambio de tema y menú de perfil.
 */
import React, { useEffect, useState } from "react";
import {
  AppBar,
  Toolbar,
  Button,
  Typography,
  Switch,
  Box,
  Tooltip,
  FormControlLabel,
  useMediaQuery,
  Menu,
  MenuItem,
} from "@mui/material";
import { useLocation } from "react-router-dom";
import { useTheme, alpha } from "@mui/material/styles";
import Brightness7Icon from "@mui/icons-material/Brightness7";
import DarkModeIcon from "@mui/icons-material/DarkMode";
import TranslateIcon from "@mui/icons-material/Translate";
import KeyboardArrowDownIcon from "@mui/icons-material/KeyboardArrowDown";
import CheckIcon from "@mui/icons-material/Check";
import { useTranslation } from "react-i18next";

import Login from "../Login";
import Register from "../Register";
import ProfileMenu from "../ProfileMenu";
import Inicio from "../Inicio.jsx";
import { useThemeToggle } from "./ThemeToggleProvider";

const APPBAR_GREEN = "#114232";
const APPBAR_HEIGHT = 72;

// Rutas donde NO quieres mostrar Login/Register
const HIDE_AUTH_BTNS_ROUTES = [
  "/seguridad/form-registro-persona",
  "/seguridad/form-registro-empresa",
  "/registro/persona",
  "/registro/empresa",
];

// Helper local para leer estado del JWT
const decodeJwt = (jwt = "") => {
  try {
    const [, raw] = jwt.split(".");
    if (!raw) return {};
    const b64 = raw.replace(/-/g, "+").replace(/_/g, "/");
    const pad = b64.length % 4 === 2 ? "==" : b64.length % 4 === 3 ? "=" : "";
    const json = atob(b64 + pad);
    const payload = JSON.parse(json);
    return {
      ...payload,
      estado: payload?.estado != null ? Number(payload.estado) : undefined,
    };
  } catch {
    return {};
  }
};

export default function AppBarComponent({
  setCurrentModule,
  onLogout,
  isAuthenticated,
  setIsAuthenticated,
}) {
  const { t, i18n } = useTranslation();
  const location = useLocation();
  const { toggleTheme, darkMode } = useThemeToggle();
  const theme = useTheme();
  const isCompact = useMediaQuery(theme.breakpoints.down("md"));
  const [languageAnchorEl, setLanguageAnchorEl] = useState(null);
  const currentLanguage = String(i18n.resolvedLanguage || i18n.language || "es")
    .toLowerCase()
    .startsWith("en")
    ? "en"
    : "es";

  const BASE_PATH =
    (typeof import.meta !== "undefined" && import.meta.env && import.meta.env.BASE_URL) ||
    process.env.PUBLIC_URL ||
    "/";
  const LOGO_SRC = `${BASE_PATH.replace(/\/+$/, "")}/images/Icono.webp`;

  const handleGoHome = () => {
    if (typeof setCurrentModule === "function") {
      setCurrentModule(<Inicio setCurrentModule={setCurrentModule} />);
    }
  };

  const handleLogin = () => {
    if (typeof setCurrentModule === "function") {
      setCurrentModule(
        <Login setIsAuthenticated={setIsAuthenticated} setCurrentModule={setCurrentModule} />
      );
    }
  };

  const handleRegister = () => {
    if (typeof setCurrentModule === "function") {
      setCurrentModule(<Register setCurrentModule={setCurrentModule} />);
    }
  };

  const openLanguageMenu = (event) => {
    setLanguageAnchorEl(event.currentTarget);
  };

  const closeLanguageMenu = () => {
    setLanguageAnchorEl(null);
  };

  const handleLanguageChange = (language) => {
    localStorage.setItem("preferredLanguage", language);
    i18n.changeLanguage(language);
    closeLanguageMenu();
  };

  useEffect(() => {
    if (location.pathname === "/login") handleLogin();
    if (location.pathname === "/register") handleRegister();
  }, [location.pathname]);

  // Mostrar UI de perfil si:
  // - está autenticado
  // - o hay token en localStorage (por recarga)
  // - o está en un formulario de registro (para ocultar Login/Register)
  const hasAnyToken =
    !!localStorage.getItem("accessToken") ||
    !!localStorage.getItem("token");

  const showProfileUI =
    isAuthenticated || hasAnyToken || HIDE_AUTH_BTNS_ROUTES.includes(location.pathname);

  // ===== Estado para el título dinámico =====
  const [empresaNombre, setEmpresaNombre] = useState(
    () => localStorage.getItem("empresaNombre") || ""
  );
  const [estadoActivo, setEstadoActivo] = useState(false);

  // Refresca empresa y estado desde localStorage/JWT
  const refreshTitleState = () => {
    const name = localStorage.getItem("empresaNombre") || "";
    setEmpresaNombre(name);

    const token = localStorage.getItem("token") || "";
    const { estado } = decodeJwt(token);
    setEstadoActivo(Number(estado) === 4);
  };

  useEffect(() => {
    // Inicial
    refreshTitleState();

    // Actualiza cuando cambie algo relevante
    const onStorage = () => refreshTitleState();
    const onAuthUpdated = () => refreshTitleState();
    const onFocus = () => refreshTitleState();

    window.addEventListener("storage", onStorage);
    window.addEventListener("auth:updated", onAuthUpdated); // emítelo cuando hagas login/cambio de rol
    window.addEventListener("focus", onFocus);

    return () => {
      window.removeEventListener("storage", onStorage);
      window.removeEventListener("auth:updated", onAuthUpdated);
      window.removeEventListener("focus", onFocus);
    };
  }, []);

  return (
    <AppBar
      position="fixed"
      sx={{
        width: "100%",
        backgroundColor: APPBAR_GREEN,
        borderRadius: 0,
      }}
    >
      <Toolbar sx={{ minHeight: `${APPBAR_HEIGHT}px`, gap: { xs: 1, sm: 1.5 } }}>
        {/* Logo / Título */}
        {!showProfileUI ? (
          <Button
            onClick={handleGoHome}
            color="inherit"
            startIcon={
              <Box
                component="img"
                src={LOGO_SRC}
                alt="Inventario Usco"
                sx={{ width: 28, height: 28, objectFit: "contain" }}
              />
            }
            sx={{
              mr: "auto",
              textTransform: "none",
              fontSize: 22,
              fontWeight: 700,
              px: 1,
              gap: 1,
            }}
          >
            Inventario Usco
          </Button>
        ) : (
          <Box
            sx={{
              mr: "auto",
              minWidth: 0,
              display: "flex",
              alignItems: "center",
              gap: 1.25,
            }}
          >
            <Box
              component="img"
              src={LOGO_SRC}
              alt="Inventario Usco"
              sx={{ width: 28, height: 28, objectFit: "contain" }}
            />
            {isCompact ? (
              <Box sx={{ minWidth: 0, display: "flex", flexDirection: "column" }}>
                <Typography
                  variant="h6"
                  sx={{
                    fontWeight: 700,
                    lineHeight: 1.05,
                    fontSize: { xs: "1rem", sm: "1.05rem" },
                  }}
                  component="span"
                >
                  Inventario Usco
                </Typography>
                {estadoActivo && empresaNombre && (
                  <Typography
                    variant="body2"
                    component="span"
                    sx={{
                      fontWeight: 600,
                      lineHeight: 1.1,
                      mt: 0.15,
                      color: alpha(theme.palette.common.white, 0.84),
                      fontSize: { xs: "0.72rem", sm: "0.78rem" },
                      whiteSpace: "nowrap",
                      overflow: "hidden",
                      textOverflow: "ellipsis",
                      maxWidth: { xs: 150, sm: 220 },
                    }}
                  >
                    {empresaNombre}
                  </Typography>
                )}
              </Box>
            ) : (
              <Box sx={{ minWidth: 0, display: "flex", alignItems: "baseline", gap: 1 }}>
                <Typography
                  variant="h6"
                  sx={{
                    fontWeight: 700,
                    lineHeight: 1.05,
                    fontSize: { md: "1.2rem", lg: "1.25rem" },
                    whiteSpace: "nowrap",
                  }}
                  component="span"
                >
                  Inventario Usco
                </Typography>
                {estadoActivo && empresaNombre && (
                  <Typography
                    variant="body2"
                    component="span"
                    sx={{
                      fontWeight: 600,
                      color: alpha(theme.palette.common.white, 0.84),
                      fontSize: { md: "0.84rem", lg: "0.9rem" },
                      whiteSpace: "nowrap",
                      overflow: "hidden",
                      textOverflow: "ellipsis",
                      maxWidth: { md: 260, lg: 420 },
                    }}
                  >
                    — {empresaNombre}
                  </Typography>
                )}
              </Box>
            )}
          </Box>
        )}

        {isCompact ? (
          <Tooltip title={darkMode ? t("common.theme.dark") : t("common.theme.light")}>
            <Switch
              checked={darkMode}
              onChange={toggleTheme}
              sx={{
                ml: { xs: 0.5, sm: 1 },
                mr: { xs: 0.5, sm: 1.5 },
                width: 44,
                height: 24,
                padding: 0,
                "& .MuiSwitch-switchBase": {
                  padding: 0.3,
                  "&.Mui-checked": {
                    transform: "translateX(20px)",
                    color: "#fff",
                    "& + .MuiSwitch-track": {
                      backgroundColor: theme.palette.primary.main,
                      opacity: 1,
                    },
                  },
                },
                "& .MuiSwitch-thumb": {
                  width: 18,
                  height: 18,
                  borderRadius: "50%",
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "center",
                  fontSize: 12,
                },
                "& .MuiSwitch-track": {
                  borderRadius: 20,
                  backgroundColor: alpha(theme.palette.common.white, 0.5),
                  opacity: 1,
                },
              }}
              icon={<Brightness7Icon sx={{ fontSize: 20 }} />}
              checkedIcon={<DarkModeIcon sx={{ fontSize: 20 }} />}
            />
          </Tooltip>
        ) : (
          <FormControlLabel
            control={
              <Switch
                checked={darkMode}
                onChange={toggleTheme}
                sx={{
                  width: 44,
                  height: 24,
                  padding: 0,
                  "& .MuiSwitch-switchBase": {
                    padding: 0.3,
                    "&.Mui-checked": {
                      transform: "translateX(20px)",
                      color: "#fff",
                      "& + .MuiSwitch-track": {
                        backgroundColor: theme.palette.primary.main,
                        opacity: 1,
                      },
                    },
                  },
                  "& .MuiSwitch-thumb": {
                    width: 18,
                    height: 18,
                    borderRadius: "50%",
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "center",
                    fontSize: 12,
                  },
                  "& .MuiSwitch-track": {
                    borderRadius: 20,
                    backgroundColor: alpha(theme.palette.common.white, 0.5),
                    opacity: 1,
                  },
                }}
                icon={<Brightness7Icon sx={{ fontSize: 20 }} />}
                checkedIcon={<DarkModeIcon sx={{ fontSize: 20 }} />}
              />
            }
            label={darkMode ? t("common.theme.dark") : t("common.theme.light")}
            labelPlacement="start"
            sx={{
              ml: 2,
              mr: 2,
              color: "inherit",
              display: "flex",
              alignItems: "center",
              gap: 1.2,
              "& .MuiFormControlLabel-label": { fontWeight: 600 },
            }}
          />
        )}

        <Button
          color="inherit"
          onClick={openLanguageMenu}
          startIcon={isCompact ? null : <TranslateIcon />}
          endIcon={isCompact ? null : <KeyboardArrowDownIcon />}
          aria-controls={languageAnchorEl ? "language-menu" : undefined}
          aria-haspopup="true"
          aria-expanded={languageAnchorEl ? "true" : undefined}
          sx={{
            mr: showProfileUI ? 1 : 0,
            minWidth: { xs: 34, md: 68 },
            px: { xs: 0.6, md: 1.25 },
            py: 0.45,
            textTransform: "none",
            fontWeight: 600,
            letterSpacing: 0.2,
            borderRadius: 999,
            border: `1px solid ${alpha(theme.palette.common.white, 0.12)}`,
            backgroundColor: alpha(theme.palette.common.white, 0.035),
            color: alpha(theme.palette.common.white, 0.92),
            boxShadow: "none",
            transition: theme.transitions.create(
              ["background-color", "border-color", "color"],
              {
                duration: theme.transitions.duration.shorter,
              }
            ),
            "&:hover": {
              backgroundColor: alpha(theme.palette.common.white, 0.08),
              borderColor: alpha(theme.palette.common.white, 0.18),
              boxShadow: "none",
            },
            "& .MuiButton-startIcon": {
              mr: isCompact ? 0 : 0.55,
              "& svg": {
                fontSize: isCompact ? 18 : 17,
                opacity: 0.82,
              },
            },
            "& .MuiButton-endIcon": {
              ml: 0.2,
              "& svg": {
                fontSize: 18,
                opacity: 0.72,
              },
            },
          }}
        >
          {currentLanguage.toUpperCase()}
        </Button>
        <Menu
          id="language-menu"
          anchorEl={languageAnchorEl}
          open={Boolean(languageAnchorEl)}
          onClose={closeLanguageMenu}
        >
          <MenuItem
            selected={currentLanguage === "es"}
            onClick={() => handleLanguageChange("es")}
            sx={{ minWidth: 180, display: "flex", justifyContent: "space-between", gap: 2 }}
          >
            <span>{`${t("common.language.spanish")} (ES)`}</span>
            {currentLanguage === "es" ? <CheckIcon fontSize="small" /> : null}
          </MenuItem>
          <MenuItem
            selected={currentLanguage === "en"}
            onClick={() => handleLanguageChange("en")}
            sx={{ minWidth: 180, display: "flex", justifyContent: "space-between", gap: 2 }}
          >
            <span>{`${t("common.language.english")} (EN)`}</span>
            {currentLanguage === "en" ? <CheckIcon fontSize="small" /> : null}
          </MenuItem>
        </Menu>

        {!showProfileUI ? (
          <>
            {!isCompact && (
              <Button color="inherit" onClick={handleLogin}>
                {t("auth.actions.login")}
              </Button>
            )}
            {!isCompact && (
              <Button color="inherit" onClick={handleRegister}>
                {t("auth.actions.register")}
              </Button>
            )}
          </>
        ) : (
          <ProfileMenu
            setCurrentModule={setCurrentModule}
            setIsAuthenticated={setIsAuthenticated}
            onLogout={onLogout}
            compact={isCompact}
          />
        )}
      </Toolbar>
    </AppBar>
  );
}
