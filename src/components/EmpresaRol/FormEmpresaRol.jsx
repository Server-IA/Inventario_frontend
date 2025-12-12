import React, { useEffect, useState } from "react";
import PropTypes from "prop-types";
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Box,
} from "@mui/material";
import AddRounded from "@mui/icons-material/AddRounded";

export default function FormEmpresaRol({ open, onClose, roles = [], onSubmit }) {
  const [rolId, setRolId] = useState("");

  useEffect(() => {
    if (open) {
      setRolId("");
    }
  }, [open]);

  const handleChange = (event) => {
    setRolId(event.target.value);
  };

  const handleSubmit = (event) => {
    event.preventDefault();
    onSubmit(rolId);
  };

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
      <DialogTitle>Asignar rol a la empresa</DialogTitle>
      <DialogContent dividers>
        <Box component="form" onSubmit={handleSubmit} sx={{ mt: 1 }}>
          <FormControl fullWidth margin="normal">
            <InputLabel id="rolId-label">Rol</InputLabel>
            <Select
              labelId="rolId-label"
              label="Rol"
              value={rolId}
              onChange={handleChange}
            >
              {roles.map((rol) => (
                <MenuItem key={rol.id} value={rol.id}>
                  {rol.nombre || rol.name || rol.rolNombre}
                </MenuItem>
              ))}
            </Select>
          </FormControl>
        </Box>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>Cancelar</Button>
        <Button
          variant="contained"
          startIcon={<AddRounded />}
          type="submit"
          form=":r" // opcional si quieres, pero no es necesario si es el único form del Dialog
        >
          Guardar
        </Button>
      </DialogActions>
    </Dialog>
  );
}

FormEmpresaRol.propTypes = {
  open: PropTypes.bool.isRequired,
  onClose: PropTypes.func.isRequired,
  roles: PropTypes.array,
  onSubmit: PropTypes.func.isRequired,
};
