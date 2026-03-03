import * as React from 'react';
import Stack from '@mui/material/Stack';
import Button from '@mui/material/Button';

import AddIcon from '@mui/icons-material/Add';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';

function StackButtons(props) {
  const handleClickCreate = () => props.methods.create();
  const handleClickUpdate = () => props.methods.update();
  const handleClickDelete = () => props.methods.deleteRow();

  return (
    <Stack
      direction="row"
      justifyContent="flex-start"
      alignItems="center"
      spacing={2}
      sx={{ mt: 2 }}
    >
      {/* AGREGAR */}
<Button
  startIcon={<AddIcon sx={{ fontSize: 18 }} />}
  onClick={handleClickCreate}
  variant="contained"
  sx={(theme) => ({
    backgroundColor: '#1f3f3d',
    borderRadius: '6px',
    padding: '6px 16px',
    fontSize: '13px',
    fontWeight: 600,
    textTransform: 'uppercase',
    boxShadow: 'none',
    color: theme.palette.mode === 'dark' ? '#fff' : '#fff', 
    '&:hover': {
      backgroundColor: '#173231',
      boxShadow: 'none',
    },
  })}
>
  Agregar
</Button>

      {/* ACTUALIZAR */}
      <Button
        startIcon={<EditIcon sx={{ fontSize: 18 }} />}
        onClick={handleClickUpdate}
        variant="contained"
        sx={{
          backgroundColor: '#d9d9d9',
          color: '#333',
          borderRadius: '6px',
          padding: '6px 16px',
          fontSize: '13px',
          fontWeight: 600,
          textTransform: 'uppercase',
          boxShadow: 'none',
          '&:hover': {
            backgroundColor: '#c9c9c9',
            boxShadow: 'none',
          },
        }}
      >
        Actualizar
      </Button>

      {/* ELIMINAR */}
      <Button
        startIcon={<DeleteIcon sx={{ fontSize: 18 }} />}
        onClick={handleClickDelete}
        variant="contained"
        sx={{
          backgroundColor: '#f3d6d6',
          color: '#c62828',
          borderRadius: '6px',
          padding: '6px 16px',
          fontSize: '13px',
          fontWeight: 600,
          textTransform: 'uppercase',
          boxShadow: 'none',
          '&:hover': {
            backgroundColor: '#eec5c5',
            boxShadow: 'none',
          },
        }}
      >
        Eliminar
      </Button>
    </Stack>
  );
}

export default StackButtons;