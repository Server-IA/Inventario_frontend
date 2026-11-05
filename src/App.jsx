/*=============================================================================
Nombre del archivo : App.jsx
Descripcion        : Reestructuracion de paises
===============================================================================
CONTROL DE CAMBIOS
+------------+---------+----------------------+-----------------------------+
|   Fecha    | Version |      Autor           | Descripcion del cambio      |
+------------+---------+----------------------+-----------------------------+
| 2026-06-03 | 0.4.0   | Jeisson Sanchez      |  Reestructuracion de paises.       |
| 2026-10-16 | 0.4.0   | Jeisson Sanchez      | Registrar módulo TipoCostoIndirecto (HU-044). |
+------------+---------+----------------------+-----------------------------+
=============================================================================*/
import React, { useEffect, useState } from "react";
import { Box, CssBaseline, Container, Toolbar } from "@mui/material";

import { useThemeToggle } from "./components/dashboard/ThemeToggleProvider";
import { useTranslation } from "react-i18next";
import "./i18n.js";
import "./index.css";
import { useLocation } from "react-router-dom";

import AppBarComponent from "./components/dashboard/AppBarComponent.jsx";
import Copyright from "./components/dashboard/Copyright";
import Inicio from "./components/Inicio.jsx";
import Contenido from "./components/dashboard/Contenido.jsx";
import Navigator2 from "./components/dashboard/Navigator2.jsx";

import Verify from "./components/Verify.jsx";
import FormRegistroPersona from "./components/seguridad/FormRegistroPersona";
import FormRegistroEmpresa from "./components/seguridad/FormRegistroEmpresa.jsx";
import ChangePasswordInitial from "./components/seguridad/ChangePasswordInitial.jsx";

const APPBAR_GREEN = "#0F2327";
const APPBAR_HEIGHT = 72;

const App = () => {
  useTranslation();
  useThemeToggle();
  const location = useLocation();
  const [currentModule, setCurrentModule] = useState(null);
  const hasValidToken = () => {
    const token = localStorage.getItem("token");
    const exp = Number(localStorage.getItem("token_expiration"));
    return Boolean(token && exp && Date.now() < exp);
  };

  const [isAuthenticated, setIsAuthenticated] = useState(hasValidToken());
  const [menuOpen, setMenuOpen] = useState(() => {
    const saved = localStorage.getItem("sidebarOpen");
    return saved ? JSON.parse(saved) : true;
  });
  useEffect(() => {
    const timers = [50, 180, 320].map((ms) =>
      setTimeout(() => window.dispatchEvent(new Event("resize")), ms),
    );
    return () => timers.forEach(clearTimeout);
  }, [menuOpen]);
  useEffect(() => {
    const hasValidToken = () => {
      const token = localStorage.getItem("token");
      const exp = Number(localStorage.getItem("token_expiration"));
      return Boolean(token && exp && Date.now() < exp);
    };

    // ðŸ”¹ VERIFY (PÃšBLICO)
    if (/\/auth\/verify(?:\/|$)/.test(location.pathname)) {
      setIsAuthenticated(false);
      setCurrentModule(<Verify key={location.search} />);
      return;
    }

    // ðŸ”¹ SIN TOKEN
    if (!hasValidToken()) {
      localStorage.removeItem("token");
      localStorage.removeItem("token_expiration");
      localStorage.removeItem("activeModule");

      setIsAuthenticated(false);
      setCurrentModule(<Inicio setCurrentModule={setCurrentModule} />);
      return;
    }

    setIsAuthenticated(true);

    // ðŸ”¥ ONBOARDING PERSONA
    if (location.pathname === "/inventario/onboarding/persona") {
      setCurrentModule(
        <FormRegistroPersona setCurrentModule={setCurrentModule} />,
      );
      return;
    }

    // ðŸ”¥ ONBOARDING EMPRESA
    if (location.pathname === "/inventario/onboarding/empresa") {
      setCurrentModule(
        <FormRegistroEmpresa setCurrentModule={setCurrentModule} />,
      );
      return;
    }

    // ðŸ”¥ CAMBIO PASSWORD INICIAL
    if (location.pathname === "/inventario/auth/change-password-initial") {
      setCurrentModule(<ChangePasswordInitial />);
      return;
    }

    // ðŸ”¹ DASHBOARD PRINCIPAL
    if (location.pathname === "/" || location.pathname === "/inventario") {
      setCurrentModule(<Contenido setCurrentModule={setCurrentModule} />);
    }
  }, [location.pathname]);

  const isOnboarding =
    location.pathname.startsWith("/inventario/onboarding") ||
    location.pathname.startsWith("/inventario/auth");

  const isPublic = !isAuthenticated || isOnboarding;

  //const isPublic = !isAuthenticated;

  return (
    <Box sx={{ display: "flex", minHeight: "100vh" }}>
      <CssBaseline />

      {/* MenÃº solo si estÃ¡ autenticado */}
      {isAuthenticated && !isOnboarding && (
        <Navigator2
          setCurrentModuleItem={setCurrentModule}
          setMenuOpen={setMenuOpen}
          isAuthenticated={isAuthenticated}
        />
      )}

      <Box
        id="app-main"
        component="main"
        sx={{
          flexGrow: 1,
          width: isAuthenticated
            ? {
                xs: "100%",
                sm: menuOpen ? "calc(100% - 220px)" : "calc(100% - 70px)",
                md: menuOpen ? "calc(100% - 250px)" : "calc(100% - 70px)",
              }
            : "100%",
          minWidth: 0,
          display: "flex",
          flexDirection: "column",
          ml: isAuthenticated
            ? {
                xs: 0,
                sm: menuOpen ? "220px" : "70px",
                md: menuOpen ? "250px" : "70px",
              }
            : 0,
          transition:
            "margin-left 0.42s cubic-bezier(0.22, 1, 0.36, 1), width 0.42s cubic-bezier(0.22, 1, 0.36, 1)",
          bgcolor: "transparent",
        }}
      >
        <AppBarComponent
          key={isAuthenticated}
          setCurrentModule={setCurrentModule}
          setIsAuthenticated={setIsAuthenticated}
          isAuthenticated={isAuthenticated}
        />

        <Toolbar
          disableGutters
          sx={{
            minHeight: `${APPBAR_HEIGHT}px`,
            bgcolor: APPBAR_GREEN,
            boxShadow: "none",
            border: 0,
          }}
        />

        {isPublic ? (
          <Box sx={{ flex: 1, display: "flex", flexDirection: "column" }}>
            {currentModule}
            <Box sx={{ py: 2 }}>
              <Copyright />
            </Box>
          </Box>
        ) : (
          <Container
            maxWidth={false}
            disableGutters
            sx={{
              flex: 1,
              width: "100%",
              maxWidth: "100% !important",
              pt: { xs: 2, sm: 2.25, md: 1.5 },
              pb: { xs: 9.5, sm: 3, md: 2 },
              px: { xs: 1, sm: 2, md: 3 },
              display: "flex",
              flexDirection: "column",
              bgcolor: "transparent",
            }}
          >
            <Box
              sx={{
                flex: { xs: "0 0 auto", sm: 1 },
                width: "100%",
                minWidth: 0,
                minHeight: {
                  xs: "auto",
                  sm: "calc(100vh - 185px)",
                  md: "calc(100vh - 170px)",
                },
                overflow: { xs: "visible", sm: "auto" },
                bgcolor: "transparent",
              }}
            >
              {currentModule}
            </Box>
            <Box
              sx={{ pt: { xs: 1.5, sm: 2.25, md: 2 }, pb: { xs: 1.25, sm: 0 } }}
            >
              <Copyright />
            </Box>
          </Container>
        )}
      </Box>
    </Box>
  );
};

export default App;
