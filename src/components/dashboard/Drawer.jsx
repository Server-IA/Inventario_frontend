/**
 * @module Drawer
 */
import * as React from 'react';
import { styled, createTheme, ThemeProvider } from '@mui/material/styles';
import MuiDrawer from '@mui/material/Drawer';
import MuiAppBar from '@mui/material/AppBar';
import { SiteProps } from './SiteProps';

/**
 * Componente Drawer estilizado utilizando Material-UI.
 * Aplica estilos personalizados al componente MuiDrawer.
 * Utiliza un tema de Material-UI para manejar transiciones y diseño responsivo.
 */
export const Drawer = styled(MuiDrawer, { shouldForwardProp: (prop) => prop !== 'open' })(
    ({ theme, open }) => ({
      '& .MuiDrawer-paper': {
        position: 'relative',
        backgroundColor: '#E7F6F7',
        color: '#173f39',
        whiteSpace: 'nowrap',
        width: SiteProps.drawerWidth,
        transition: theme.transitions.create('width', {
          easing: theme.transitions.easing.sharp,
          duration: theme.transitions.duration.enteringScreen,
        }),
        boxSizing: 'border-box',
        // vínculos/iconos dentro del drawer
        '& .MuiListItemButton-root': {
          color: '#173f39',
          '&:hover': { backgroundColor: 'rgba(23,63,57,0.08)' },
        },
        '& .MuiListItemIcon-root': {
          color: '#173f39',
        },
        ...(!open && {
          overflowX: 'hidden',
          transition: theme.transitions.create('width', {
            easing: theme.transitions.easing.sharp,
            duration: theme.transitions.duration.leavingScreen,
          }),
          width: theme.spacing(7),
          [theme.breakpoints.up('sm')]: {
            width: theme.spacing(9),
          },
        }),
      },
    }),
);


  
  
