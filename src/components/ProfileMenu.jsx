import React, { useState, useEffect } from "react";
import { Button, Menu, MenuItem, Divider } from "@mui/material";
import AccountCircle from "@mui/icons-material/AccountCircle";
import ChangePasswordDialog from "./ChangePasswordDialog";
import MessageSnackBar from "./MessageSnackBar";
import Login from "./Login";
import RoleSwitcherModal from "./RoleSwitcherModal";
import ChangeLogoDialog from "./ChangeLogoDialog.jsx";
import { useLocation } from "react-router-dom";

/**
 * Extra: si algún día quieres forzar que sólo salga "Cerrar Sesión"
 * desde arriba, puedes pasar la prop optional onlyLogout={true}.
 */
const ProfileMenu = ({
  setCurrentModule,
  setIsAuthenticated,
  onlyLogout = false,
}) => {
  const [anchorEl, setAnchorEl] = useState(null);

  const [passwordDialogOpen, setPasswordDialogOpen] = useState(false);
  const [logoDialogOpen, setLogoDialogOpen] = useState(false);
  const [openRoleModal, setOpenRoleModal] = useState(false);

  const [message, setMessage] = useState({
    open: false,
    severity: "",
    text: "",
  });

  const [nombreUsuario, setNombreUsuario] = useState("Mi Perfil");
  const [isSingleCompanyAndRole, setIsSingleCompanyAndRole] = useState(false);

  const location = useLocation();

  // datos locales
  const rolId = localStorage.getItem("rolId"); // <- viene como string normalmente
  // solo rol 1 y 2 pueden ver "Cambiar logo"
  const puedeCambiarLogo = rolId === "1" || rolId === "2";

  useEffect(() => {
    const empresaNombre = localStorage.getItem("empresaNombre");
    const rolesByCompany = JSON.parse(
      localStorage.getItem("rolesByCompany") || "[]"
    );

    if (empresaNombre) setNombreUsuario(empresaNombre);

    const empresas = [...new Set(rolesByCompany.map((r) => r.empresaId))];
    if (empresas.length === 1 && rolesByCompany.length === 1) {
      setIsSingleCompanyAndRole(true);
    }
  }, []);

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
      >
        {nombreUsuario}
      </Button>

      <Menu
        id="simple-menu"
        anchorEl={anchorEl}
        keepMounted
        open={Boolean(anchorEl)}
        onClose={handleClose}
      >
        {showOnlyLogout ? (
          <MenuItem onClick={handleLogout}>Cerrar Sesión</MenuItem>
        ) : (
          <>
            {/* Cambiar contraseña */}
            <MenuItem
              onClick={() => {
                setPasswordDialogOpen(true);
                handleClose();
              }}
            >
              Cambiar Contraseña
            </MenuItem>

            {/* Cambiar empresa/rol (solo si hay más de una combinación posible) */}
            {!isSingleCompanyAndRole && (
              <MenuItem
                onClick={() => {
                  setOpenRoleModal(true);
                  handleClose();
                }}
              >
                Cambiar empresa/rol
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
                  Cambiar logo
                </MenuItem>
              </>
            )}

            <Divider />

            {/* Cerrar sesión */}
            <MenuItem onClick={handleLogout}>Cerrar Sesión</MenuItem>
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
