import React, { useState, useEffect } from "react";
import { Button, Menu, MenuItem } from "@mui/material";
import AccountCircle from "@mui/icons-material/AccountCircle";
import ChangePasswordDialog from "./ChangePasswordDialog";
import MessageSnackBar from "./MessageSnackBar";
import Login from "./Login";
import RoleSwitcherModal from "./RoleSwitcherModal";
import { useLocation } from "react-router-dom";

/**
 * Extra: si algún día quieres forzar que sólo salga "Cerrar Sesión"
 * desde arriba, puedes pasar la prop optional onlyLogout={true}.
 */
const ProfileMenu = ({ setCurrentModule, setIsAuthenticated, onlyLogout = false }) => {
  const [anchorEl, setAnchorEl] = useState(null);
  const [passwordDialogOpen, setPasswordDialogOpen] = useState(false);
  const [message, setMessage] = useState({ open: false, severity: "", text: "" });
  const [nombreUsuario, setNombreUsuario] = useState("Mi Perfil");
  const [openRoleModal, setOpenRoleModal] = useState(false);
  const [isSingleCompanyAndRole, setIsSingleCompanyAndRole] = useState(false);

  const location = useLocation();

  useEffect(() => {
    const empresaNombre = localStorage.getItem("empresaNombre");
    const rolesByCompany = JSON.parse(localStorage.getItem("rolesByCompany") || "[]");
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
      <Login setIsAuthenticated={setIsAuthenticated} setCurrentModule={setCurrentModule} />
    );
    handleClose();
  };

  const afterSwitch = () => window.location.reload();

  // --- AQUÍ ESTÁ LA CLAVE ---
  // En tu screenshot la ruta es: /coagronet/coagronet/onboarding/persona
  // Para que funcione con prefijos, usamos "includes" en lugar de igualdad estricta.
  const path = location.pathname || "";

  const showOnlyLogoutByRoute =
    path.includes("/onboarding/persona") ||
    path.includes("/onboarding/empresa") ||
    path.includes("/registro/persona") ||
    path.includes("/registro/empresa") ||
    path.includes("/seguridad/form-registro-persona") ||
    path.includes("/seguridad/form-registro-empresa");

  // Si viene la prop onlyLogout o la ruta coincide, mostramos solo "Cerrar Sesión".
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
            <MenuItem onClick={() => { setPasswordDialogOpen(true); handleClose(); }}>
              Cambiar Contraseña
            </MenuItem>

            {!isSingleCompanyAndRole && (
              <MenuItem onClick={() => { setOpenRoleModal(true); handleClose(); }}>
                Cambiar empresa/rol
              </MenuItem>
            )}

            <MenuItem onClick={handleLogout}>Cerrar Sesión</MenuItem>
          </>
        )}
      </Menu>

      <ChangePasswordDialog
        open={passwordDialogOpen}
        setOpen={setPasswordDialogOpen}
        setMessage={setMessage}
      />
      {message.open && <MessageSnackBar {...message} setMessage={setMessage} />}

      <RoleSwitcherModal
        open={openRoleModal}
        onClose={() => setOpenRoleModal(false)}
        onSwitched={afterSwitch}
      />
    </>
  );
};

export default ProfileMenu;
