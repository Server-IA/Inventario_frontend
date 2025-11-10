import React, { useEffect, useState } from "react";
import {
  AppBar,
  Toolbar,
  Button,
  Typography,
  Switch,
  Box,
  FormControlLabel,
} from "@mui/material";
import { useLocation } from "react-router-dom";
import { useTheme, alpha } from "@mui/material/styles";
import Brightness7Icon from "@mui/icons-material/Brightness7";
import DarkModeIcon from "@mui/icons-material/DarkMode";

import Login from "../Login";
import Register from "../Register";
import ProfileMenu from "../ProfileMenu";
import Inicio from "../Inicio.jsx";
import { useThemeToggle } from "./ThemeToggleProvider";

const APPBAR_GREEN = "#114232";

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
  const location = useLocation();
  const { toggleTheme, darkMode } = useThemeToggle();
  const theme = useTheme();

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
      <Toolbar>
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
          <Box sx={{ mr: "auto", display: "flex", alignItems: "center", gap: 1.25 }}>
            <Box
              component="img"
              src={LOGO_SRC}
              alt="Inventario Usco"
              sx={{ width: 28, height: 28, objectFit: "contain" }}
            />
            <Typography variant="h6" sx={{ fontWeight: 700, lineHeight: 1 }} component="span">
              Inventario Usco
            </Typography>

            {/* Sufijo con el nombre de la empresa SOLO si estado === 4 y hay nombre */}
            {estadoActivo && empresaNombre && (
              <Typography
                variant="h6"
                component="span"
                sx={{ fontWeight: 700, lineHeight: 1, ml: 1 }}
              >
                — {empresaNombre}
              </Typography>
            )}
          </Box>
        )}

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
              icon={<Brightness7Icon sx={{ fontSize: 20 }} />}     // ☀️
              checkedIcon={<DarkModeIcon sx={{ fontSize: 20 }} />} // 🌙
            />
          }
          label={darkMode ? "Modo oscuro" : "Modo claro"}
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

        {!showProfileUI ? (
          <>
            <Button color="inherit" onClick={handleLogin}>Login</Button>
            <Button color="inherit" onClick={handleRegister}>Register</Button>
          </>
        ) : (
          <ProfileMenu
            setCurrentModule={setCurrentModule}
            setIsAuthenticated={setIsAuthenticated}
            onLogout={onLogout}
          />
        )}
      </Toolbar>
    </AppBar>
  );
}
