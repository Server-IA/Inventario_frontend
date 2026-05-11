/*=============================================================================
 Nombre del archivo : ProfileMenu.jsx
 Descripcion        : Menú desplegable del perfil con acciones del usuario autenticado.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |   Fecha    | Versión |      Autor           | Descripción del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2026-05-08 | 0.4.0   | Cesar Medina         | Creación del archivo.       |
 +------------+---------+----------------------+-----------------------------+
=============================================================================*/
/**
 * @module ProfileMenu
 * @description Muestra las opciones del perfil autenticado como cambio de
 * contraseña, cambio de empresa/rol, cambio de logo y cierre de sesión.
 */
import React, { useState, useEffect } from "react";
import { Button, Menu, MenuItem, Divider, Box, Typography } from "@mui/material";
import AccountCircle from "@mui/icons-material/AccountCircle";
import ChangePasswordDialog from "./ChangePasswordDialog";
import MessageSnackBar from "./MessageSnackBar";
import Login from "./Login";
import RoleSwitcherModal from "./RoleSwitcherModal";
import ChangeLogoDialog from "./ChangeLogoDialog.jsx";
import { useLocation } from "react-router-dom";
import { useTranslation } from "react-i18next";

/**
 * Extra: si algún día quieres forzar que sólo salga "Cerrar Sesión"
 * desde arriba, puedes pasar la prop optional onlyLogout={true}.
 */
const ProfileMenu = ({
  setCurrentModule,
  setIsAuthenticated,
  onlyLogout = false,
  compact = false,
}) => {
  const { t } = useTranslation();
  const [anchorEl, setAnchorEl] = useState(null);

  const [passwordDialogOpen, setPasswordDialogOpen] = useState(false);
  const [logoDialogOpen, setLogoDialogOpen] = useState(false);
  const [openRoleModal, setOpenRoleModal] = useState(false);

  const [message, setMessage] = useState({
    open: false,
    severity: "",
    text: "",
  });

  // 🔹 Ahora este label muestra el nombre de la persona (no la empresa)
  const [nombreUsuario, setNombreUsuario] = useState(t("common.profile.defaultName"));
  const [isSingleCompanyAndRole, setIsSingleCompanyAndRole] = useState(false);

  const location = useLocation();

  // datos locales
  const rolId = localStorage.getItem("rolId"); // <- viene como string normalmente
  // solo rol 1 y 2 pueden ver "Cambiar logo"
  const puedeCambiarLogo = rolId === "1" || rolId === "2";

  useEffect(() => {
    const rolesByCompany = JSON.parse(localStorage.getItem("rolesByCompany") || "[]");

    // 👉 Mostrar el nombre de la persona en el botón del perfil
    const nombrePersona = localStorage.getItem("nombrePersona");
    if (nombrePersona && nombrePersona.trim()) {
      setNombreUsuario(nombrePersona);
    } else {
      setNombreUsuario(t("common.profile.defaultName"));
    }

    const empresas = [...new Set(rolesByCompany.map((r) => r.empresaId))];
    if (empresas.length === 1 && rolesByCompany.length === 1) {
      setIsSingleCompanyAndRole(true);
    }
  }, [t]);

  const handleClick = (e) => setAnchorEl(e.currentTarget);
  const handleClose = () => setAnchorEl(null);

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("accessToken");
    localStorage.removeItem("refreshToken");
    localStorage.removeItem("empresaNombre");
    localStorage.removeItem("empresaId");
    localStorage.removeItem("rolId");
    localStorage.removeItem("rolesByCompany");
    localStorage.removeItem("nombrePersona"); // 🔹 limpiar nombrePersona también

    setIsAuthenticated(false);
    setCurrentModule(
      <Login
        setIsAuthenticated={setIsAuthenticated}
        setCurrentModule={setCurrentModule}
      />
    );
    handleClose();
  };

  const afterSwitch = () => window.location.reload();

  // Detectar rutas "especiales" donde sólo debe salir Cerrar Sesión
  const path = location.pathname || "";
  const showOnlyLogoutByRoute =
    path.includes("/onboarding/persona") ||
    path.includes("/onboarding/empresa") ||
    path.includes("/registro/persona") ||
    path.includes("/registro/empresa") ||
    path.includes("/seguridad/form-registro-persona") ||
    path.includes("/seguridad/form-registro-empresa");

  const showOnlyLogout = Boolean(onlyLogout || showOnlyLogoutByRoute);

  return (
    <>
      <Button
        aria-controls="simple-menu"
        aria-haspopup="true"
        onClick={handleClick}
        color="inherit"
        startIcon={<AccountCircle />}
        sx={{
          minWidth: 44,
          px: compact ? 1 : 1.25,
          "& .MuiButton-startIcon": {
            mr: compact ? 0 : 0.75,
            ml: 0,
          },
        }}
      >
        {!compact ? nombreUsuario : null}
      </Button>

      <Menu
        id="simple-menu"
        anchorEl={anchorEl}
        keepMounted
        open={Boolean(anchorEl)}
        onClose={handleClose}
      >
        <Box sx={{ px: 2, py: 1.25, minWidth: 220 }}>
          <Typography variant="caption" color="text.secondary">
            {t("common.profile.userLabel")}
          </Typography>
          <Typography variant="body2" fontWeight={700}>
            {nombreUsuario}
          </Typography>
        </Box>
        <Divider />
        {showOnlyLogout ? (
          <MenuItem onClick={handleLogout}>{t("common.profile.logout")}</MenuItem>
        ) : (
          <>
            {/* Cambiar contraseña */}
            <MenuItem
              onClick={() => {
                setPasswordDialogOpen(true);
                handleClose();
              }}
            >
              {t("common.profile.changePassword")}
            </MenuItem>

            {/* Cambiar empresa/rol (solo si hay más de una combinación posible) */}
            {!isSingleCompanyAndRole && (
              <MenuItem
                onClick={() => {
                  setOpenRoleModal(true);
                  handleClose();
                }}
              >
                {t("common.profile.switchCompanyRole")}
              </MenuItem>
            )}

            {/* Cambiar logo (solo rol 1 o rol 2) */}
            {puedeCambiarLogo && (
              <>
                <Divider />
                <MenuItem
                  onClick={() => {
                    setLogoDialogOpen(true);
                    handleClose();
                  }}
                >
                  {t("common.profile.changeLogo")}
                </MenuItem>
              </>
            )}

            <Divider />

            {/* Cerrar sesión */}
            <MenuItem onClick={handleLogout}>{t("common.profile.logout")}</MenuItem>
          </>
        )}
      </Menu>

      {/* DIALOG: Cambiar contraseña */}
      <ChangePasswordDialog
        open={passwordDialogOpen}
        setOpen={setPasswordDialogOpen}
        setMessage={setMessage}
      />

      {/* DIALOG: Cambiar logo */}
      <ChangeLogoDialog
        open={logoDialogOpen}
        setOpen={setLogoDialogOpen}
        setMessage={setMessage}
      />

      {/* Snackbar de feedback (SIEMPRE montado) */}
      <MessageSnackBar {...message} setMessage={setMessage} />

      {/* Modal de cambio de empresa/rol */}
      <RoleSwitcherModal
        open={openRoleModal}
        onClose={() => setOpenRoleModal(false)}
        onSwitched={afterSwitch}
      />
    </>
  );
};

export default ProfileMenu;
