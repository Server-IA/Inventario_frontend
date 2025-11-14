import * as React from "react";
import {
  Divider,
  List,
  Box,
  ListItem,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Grid,
  Typography,
} from "@mui/material";
import { alpha, useTheme } from "@mui/material/styles";
import DescriptionOutlinedIcon from '@mui/icons-material/DescriptionOutlined';
import SecurityIcon from '@mui/icons-material/Security';
import MenuIcon from "@mui/icons-material/Menu";
import HistoryToggleOffIcon from '@mui/icons-material/HistoryToggleOff';
import CategoryIcon from '@mui/icons-material/Category';
import TimelineIcon from '@mui/icons-material/Timeline';
import VerifiedIcon from '@mui/icons-material/Verified';
import ToggleOnIcon from '@mui/icons-material/ToggleOn';
import FunctionsIcon from '@mui/icons-material/Functions';
import SyncAltIcon from '@mui/icons-material/SyncAlt';
import TuneIcon from '@mui/icons-material/Tune';
import DonutSmallIcon from '@mui/icons-material/DonutSmall';
import AdminPanelSettingsIcon from '@mui/icons-material/AdminPanelSettings';
import AppsIcon from '@mui/icons-material/Apps';
import CurrencyExchangeIcon from '@mui/icons-material/CurrencyExchange';
import FactCheckIcon from '@mui/icons-material/FactCheck';
import BadgeIcon from '@mui/icons-material/Badge';
import ViewModuleIcon from '@mui/icons-material/ViewModule';
import StraightenIcon from '@mui/icons-material/Straighten';
import PersonPinCircleIcon from '@mui/icons-material/PersonPinCircle';
import DevicesOtherIcon from '@mui/icons-material/DevicesOther';
import TimelineOutlinedIcon from '@mui/icons-material/TimelineOutlined';
import CategoryOutlinedIcon from '@mui/icons-material/CategoryOutlined';
import BusinessCenterIcon from '@mui/icons-material/BusinessCenter';
import ReceiptLongIcon from '@mui/icons-material/ReceiptLong';
import RequestQuoteIcon from '@mui/icons-material/RequestQuote';
import ReceiptIcon from '@mui/icons-material/Receipt';
import GroupWorkIcon from '@mui/icons-material/GroupWork';
import SetMealIcon from '@mui/icons-material/SetMeal';
import BrandingWatermarkIcon from '@mui/icons-material/BrandingWatermark';
import NotificationsActiveIcon from '@mui/icons-material/NotificationsActive';
import PublicIcon from '@mui/icons-material/Public';
import LanguageIcon from '@mui/icons-material/Language';
import PersonIcon from '@mui/icons-material/Person';
import SlideshowIcon from '@mui/icons-material/Slideshow';
import CategoryTwoToneIcon from '@mui/icons-material/CategoryTwoTone';
import Inventory2Icon from '@mui/icons-material/Inventory2';
import DashboardCustomizeIcon from '@mui/icons-material/DashboardCustomize';
import ViewQuiltIcon from '@mui/icons-material/ViewQuilt';
import MonetizationOnIcon from '@mui/icons-material/MonetizationOn';
import ViewComfyIcon from '@mui/icons-material/ViewComfy';
import InventoryIcon from '@mui/icons-material/Inventory';
import PrecisionManufacturingIcon from '@mui/icons-material/PrecisionManufacturing';
import NotificationsNoneIcon from '@mui/icons-material/NotificationsNone';
import PrecisionManufacturingOutlinedIcon from '@mui/icons-material/PrecisionManufacturingOutlined';
import DomainAddIcon from '@mui/icons-material/DomainAdd';
import StraightenOutlinedIcon from '@mui/icons-material/StraightenOutlined';
import DevicesFoldIcon from '@mui/icons-material/DevicesFold';
import ChangeCircleIcon from '@mui/icons-material/ChangeCircle';
import EventRepeatIcon from '@mui/icons-material/EventRepeat';
import AutoAwesomeMosaicIcon from '@mui/icons-material/AutoAwesomeMosaic';
import ConstructionIcon from '@mui/icons-material/Construction';
import CalendarMonthIcon from '@mui/icons-material/CalendarMonth';
import WarehouseIcon from '@mui/icons-material/Warehouse';
import AppSettingsAltIcon from '@mui/icons-material/AppSettingsAlt';
import ThunderstormIcon from '@mui/icons-material/Thunderstorm';
import ChecklistIcon from '@mui/icons-material/Checklist';
import VillaIcon from '@mui/icons-material/Villa';
import BusinessIcon from '@mui/icons-material/Business';
import TaskAltIcon from '@mui/icons-material/TaskAlt';
import BusinessCenterOutlinedIcon from '@mui/icons-material/BusinessCenterOutlined';
import ConnectWithoutContactIcon from '@mui/icons-material/ConnectWithoutContact';
import LocationCityIcon from '@mui/icons-material/LocationCity';
import RequestPageIcon from '@mui/icons-material/RequestPage';
import KeyIcon from '@mui/icons-material/Key';
import AutoModeIcon from '@mui/icons-material/AutoMode';
import TrendingUpIcon from '@mui/icons-material/TrendingUp';
import LocalShippingIcon from '@mui/icons-material/LocalShipping';
import AssessmentIcon from '@mui/icons-material/Assessment';
import ViewAgendaIcon from '@mui/icons-material/ViewAgenda';
import ViewStreamIcon from '@mui/icons-material/ViewStream';
import SwipeIcon from '@mui/icons-material/Swipe';
import SchemaIcon from '@mui/icons-material/Schema';
import ThermostatIcon from '@mui/icons-material/Thermostat';
import HistoryIcon from '@mui/icons-material/History';
import SensorsIcon from '@mui/icons-material/Sensors';
import MapIcon from '@mui/icons-material/Map';
import SplitscreenIcon from '@mui/icons-material/Splitscreen';
import SpeedIcon from '@mui/icons-material/Speed';
import WorkHistoryIcon from '@mui/icons-material/WorkHistory';
import ViewWeekIcon from '@mui/icons-material/ViewWeek';
import LockClockIcon from '@mui/icons-material/LockClock';
import ListAltIcon from '@mui/icons-material/ListAlt';
import WaterDropIcon from '@mui/icons-material/WaterDrop';
import PriceChangeIcon from '@mui/icons-material/PriceChange';
import SpaceDashboardIcon from '@mui/icons-material/SpaceDashboard';
import EventSeatIcon from '@mui/icons-material/EventSeat';
import CheckBoxIcon from '@mui/icons-material/CheckBox';
import ListIcon from '@mui/icons-material/List';
import FormatListNumberedIcon from '@mui/icons-material/FormatListNumbered';
import SmartToyIcon from '@mui/icons-material/SmartToy';
import WidgetsIcon from '@mui/icons-material/Widgets';
import NotificationsIcon from '@mui/icons-material/Notifications';
import ShoppingCartCheckoutIcon from '@mui/icons-material/ShoppingCartCheckout';
import PlaylistAddCheckIcon from '@mui/icons-material/PlaylistAddCheck';
import ShoppingBagIcon from '@mui/icons-material/ShoppingBag';
import ReorderIcon from '@mui/icons-material/Reorder';
import ShoppingBasketIcon from '@mui/icons-material/ShoppingBasket';
import MyLocationIcon from '@mui/icons-material/MyLocation';
import ColorizeIcon from '@mui/icons-material/Colorize';
import EventNoteIcon from '@mui/icons-material/EventNote';
import PersonOutlineIcon from '@mui/icons-material/PersonOutline';
import PeopleAltIcon from '@mui/icons-material/PeopleAlt';
import AutoGraphIcon from '@mui/icons-material/AutoGraph';
import StackedLineChartIcon from '@mui/icons-material/StackedLineChart';
import ChangeHistoryIcon from '@mui/icons-material/ChangeHistory';
import LocationOnIcon from '@mui/icons-material/LocationOn';
import ViewCarouselIcon from '@mui/icons-material/ViewCarousel';
import FactoryIcon from '@mui/icons-material/Factory';
import InventoryOutlinedIcon from '@mui/icons-material/InventoryOutlined';


import ListAltOutlinedIcon from '@mui/icons-material/ListAltOutlined';


// no tocar el orden de importación de los componentes

import axios from "../axiosConfig.js";
import { useTranslation } from "react-i18next";
import CategoriaEstado from "../categoriaestado/categoria_estado.jsx";
import Persona from "../personas/Persona.jsx";
import Pais from "../pais/Pais";
import Departamento from "../departamento/Departamento";
import Municipio from "../municipio/Municipio";
import Presentacionproducto from "../Presentacionproducto/Presentacionproducto.jsx";
import Produccion from "../produccion/Produccion";
import Empresa from "../empresas/Empresa.jsx";
import Producto from "../producto/Producto.jsx";
import ProductoCategoria from "../producto_categoria/ProductoCategoria.jsx";
import Almacen from "../almacen/Almacen.jsx";
import Espacio from "../espacio/Espacio.jsx";
import Bloque from "../bloque/Bloque.jsx";
import Sede from "../sede/Sede.jsx";
import Marca from "../marca/Marca.jsx";
import Unidad from "../unidad/Unidad.jsx";
import TipoMovimiento from "../tipo_movimiento/TipoMovimiento.jsx";
import TipoProduccion from "../tipo_produccion/TipoProduccion.jsx";
import Presentacion from "../Presentacion/Presentacion.jsx";
import TipoBloque from "../tipo_bloque/Tipobloque.jsx";
import TipoSedes from "../tipo_sede/TipoSede.jsx";
import TipoEspacio from "../tipo_espacio/TipoEspacio.jsx";
import Pedido from "../r_pedido/Pedido.jsx";
import kardex from "../Kardex/kardex.jsx";
import Rol from "../Rol/Rol.jsx";
import TipoIdentificacion from "../TipoIdentificacion/TipoIdentificacion.jsx";
import Proveedor from "../Proveedor/Proveedor.jsx";
import MediaCard from "../MediaCard.jsx";
import OrdenCompra from "../OrdenCompra/OrdenCompra.jsx";
import Ocupacion from "../ocupacion/Ocupacion.jsx";
import EvaluacionItem from "../Evaluacion_item/Evaluacion_item.jsx";
import Grupo from "../Grupo/Grupo.jsx";
import Movimineto from "../Movimiento/Movimiento.jsx";
import Proceso from "../Proceso/Proceso.jsx";
import Ingrediente from "../ingrediente/ingrediente.jsx";
import Seccion from "../seccion/Seccion.jsx";
import Subseccion from "../subseccion/subseccion.jsx";
import TipoInventario from "../tipo_inventario/Tipo_inventario.jsx";
import Inventario from "../Inventario/Inventario.jsx";
import IngredientePresentacionProducto from "../IngredientePP/IngredientePP.jsx";
import TipoEvaluacion from "../tipo-evaluacion/Tipo_evaluacion.jsx";
import Re_pedido from "../RE_pedido/re_pv.jsx";
import RE_kardex from "../RKardex/Rkardex.jsx";
import RE_productoVencimiento from "../RE_pv/re_pvn.jsx";
import RE_ordenCompra from "../RE_oc/re_oc.jsx";
import RE_fc from "../RE_fc/re_fc.jsx";
import TipoUnida from "../tipo_unidad/tipounidad.jsx";

// Imágenes
import img1 from "/images/cards/1.jpg";
import img2 from "/images/cards/2.jpg";
import img3 from "/images/cards/3.jpg";
import img4 from "/images/cards/4.jpg";
import img5 from "/images/cards/5.jpg";
import img6 from "/images/cards/6.jpg";
import img7 from "/images/cards/7.jpg";
import img8 from "/images/cards/8.jpg";
import img9 from "/images/cards/9.jpg";
import img10 from "/images/cards/10.jpg";

// seguridad
import persona from "/images/cards/persona.webp";
import rol from "/images/cards/rol.webp";
import tipo_identificacion from "/images/cards/tipo_identificacion.webp";
import empresa from "/images/cards/empresa.webp";
import tipo_evaluacion from "/images/cards/tipo_evaluacion.webp";
// parametrización
import pais from "/images/cards/pais.webp";
import departamento from "/images/cards/departamento.webp";
import municipio from "/images/cards/municipio.webp";
import tipo_sede from "/images/cards/tipo_sede.webp";
import sede from "/images/cards/sede.webp";
import tipo_bloque from "/images/cards/tipo_bloque.webp";
import bloque from "/images/cards/bloque.webp";
import grupo from "/images/cards/grupo.webp";
import tipo_espacio from "/images/cards/tipo_espacio.webp";
import espacio from "/images/cards/espacio.webp";
import almacen from "/images/cards/almacen.webp";
import seccion from "/images/cards/seccion.webp";
import subseccion from "/images/cards/subseccion.webp";
import tip_inventario from "/images/cards/tip_inventario.webp";
import inventario from "/images/cards/inventario.webp";
// inventario
import ingredienteproductopresentacion from "/images/cards/ingredienteproductopresentacion.webp";
import presentacion_producto from "/images/cards/presentacion_producto.webp";
import presentacion from "/images/cards/presentacion.webp";
import producto_categoria from "/images/cards/producto_categoria.webp";
import marca from "/images/cards/marca.webp";
import ingrediente from "/images/cards/ingrediente.webp";
import tipo_produccion from "/images/cards/tipo_produccion.webp";
import produccion from "/images/cards/produccion.webp";
import proceso from "/images/cards/proceso.webp";
import tipo_movimiento from "/images/cards/tipo_movimiento.webp";
import movimiento from "/images/cards/movimiento.webp";
import unidad from "/images/cards/unidad.webp";
import productos from "/images/cards/productos.webp";
import proveedor from "/images/cards/proveedor.webp";
import pedido from "/images/cards/pedido.webp";
import orde_compra from "/images/cards/orde_compra.webp";
import kardexx from "/images/cards/kardexx.webp";
import ocupacion from "/images/cards/ocupacion.webp";
// reportes
import r_pedidoo from "/images/cards/r_pedidoo.webp";
import r_kardexx from "/images/cards/r_kardexx.webp";
import r_factura from "/images/cards/r_factura.webp";
import prodcutos_vencidos from "/images/cards/prodcutos_vencidos.webp";
import r_orden_compra from "/images/cards/r_orden_compra.webp";

import tipo_unidad from "/images/cards/tipo_unidad.webp";
import categoria_estado from "/images/cards/categoria_estado.webp";

const icons = {
  HistoryToggleOff: <HistoryToggleOffIcon />,
  Category: <CategoryIcon />,
  Timeline: <TimelineIcon />,
  Verified: <VerifiedIcon />,
  ToggleOn: <ToggleOnIcon />,
  Functions: <FunctionsIcon />,
  SyncAlt: <SyncAltIcon />,
  Tune: <TuneIcon />,
  DonutSmall: <DonutSmallIcon />,
  AdminPanelSettings: <AdminPanelSettingsIcon />,
  Apps: <AppsIcon />,
  CurrencyExchange: <CurrencyExchangeIcon />,
  FactCheck: <FactCheckIcon />,
  Badge: <BadgeIcon />,
  ViewModule: <ViewModuleIcon />,
  Straighten: <StraightenIcon />,
  PersonPinCircle: <PersonPinCircleIcon />,
  DevicesOther: <DevicesOtherIcon />,
  TimelineOutlined: <TimelineOutlinedIcon />,
  CategoryOutlined: <CategoryOutlinedIcon />,
  BusinessCenter: <BusinessCenterIcon />,
  ReceiptLong: <ReceiptLongIcon />,
  RequestQuote: <RequestQuoteIcon />,
  Receipt: <ReceiptIcon />,
  GroupWork: <GroupWorkIcon />,
  SetMeal: <SetMealIcon />,
  BrandingWatermark: <BrandingWatermarkIcon />,
  NotificationsActive: <NotificationsActiveIcon />,
  Public: <PublicIcon />,
  Language: <LanguageIcon />,
  Person: <PersonIcon />,
  Slideshow: <SlideshowIcon />,
  CategoryTwoTone: <CategoryTwoToneIcon />,
  Inventory2: <Inventory2Icon />,
  DashboardCustomize: <DashboardCustomizeIcon />,
  ViewQuilt: <ViewQuiltIcon />,
  MonetizationOn: <MonetizationOnIcon />,
  ViewComfy: <ViewComfyIcon />,
  Inventory: <InventoryIcon />,
  PrecisionManufacturing: <PrecisionManufacturingIcon />,
  NotificationsNone: <NotificationsNoneIcon />,
  PrecisionManufacturingOutlined: <PrecisionManufacturingOutlinedIcon />,
  DomainAdd: <DomainAddIcon />,
  StraightenOutlined: <StraightenOutlinedIcon />,
  DevicesFold: <DevicesFoldIcon />,
  ChangeCircle: <ChangeCircleIcon />,
  EventRepeat: <EventRepeatIcon />,
  AutoAwesomeMosaic: <AutoAwesomeMosaicIcon />,
  Construction: <ConstructionIcon />,
  CalendarMonth: <CalendarMonthIcon />,
  Warehouse: <WarehouseIcon />,
  AppSettingsAlt: <AppSettingsAltIcon />,
  Thunderstorm: <ThunderstormIcon />,
  Checklist: <ChecklistIcon />,
  Villa: <VillaIcon />,
  Business: <BusinessIcon />,
  TaskAlt: <TaskAltIcon />,
  BusinessCenterOutlined: <BusinessCenterOutlinedIcon />,
  ConnectWithoutContact: <ConnectWithoutContactIcon />,
  LocationCity: <LocationCityIcon />,
  RequestPage: <RequestPageIcon />,
  Key: <KeyIcon />,
  AutoMode: <AutoModeIcon />,
  TrendingUp: <TrendingUpIcon />,
  LocalShipping: <LocalShippingIcon />,
  Assessment: <AssessmentIcon />,
  ViewAgenda: <ViewAgendaIcon />,
  ViewStream: <ViewStreamIcon />,
  Swipe: <SwipeIcon />,
  Schema: <SchemaIcon />,
  Thermostat: <ThermostatIcon />,
  History: <HistoryIcon />,
  Sensors: <SensorsIcon />,
  Map: <MapIcon />,
  Splitscreen: <SplitscreenIcon />,
  Speed: <SpeedIcon />,
  WorkHistory: <WorkHistoryIcon />,
  ViewWeek: <ViewWeekIcon />,
  LockClock: <LockClockIcon />,
  ListAlt: <ListAltIcon />,
  WaterDrop: <WaterDropIcon />,
  PriceChange: <PriceChangeIcon />,
  SpaceDashboard: <SpaceDashboardIcon />,
  EventSeat: <EventSeatIcon />,
  CheckBox: <CheckBoxIcon />,
  List: <ListIcon />,
  FormatListNumbered: <FormatListNumberedIcon />,
  SmartToy: <SmartToyIcon />,
  Widgets: <WidgetsIcon />,
  Notifications: <NotificationsIcon />,
  ShoppingCartCheckout: <ShoppingCartCheckoutIcon />,
  PlaylistAddCheck: <PlaylistAddCheckIcon />,
  ShoppingBag: <ShoppingBagIcon />,
  Reorder: <ReorderIcon />,
  ShoppingBasket: <ShoppingBasketIcon />,
  MyLocation: <MyLocationIcon />,
  Colorize: <ColorizeIcon />,
  EventNote: <EventNoteIcon />,
  PersonOutline: <PersonOutlineIcon />,
  PeopleAlt: <PeopleAltIcon />,
  AutoGraph: <AutoGraphIcon />,
  StackedLineChart: <StackedLineChartIcon />,
  ChangeHistory: <ChangeHistoryIcon />,
  LocationOn: <LocationOnIcon />,
  ViewCarousel: <ViewCarouselIcon />,
  Factory: <FactoryIcon />,
  InventoryOutlined: <InventoryOutlinedIcon />,
  ListAltOutlined: <ListAltOutlinedIcon />,
  Security: <SecurityIcon />,   
  Description: <DescriptionOutlinedIcon />,
};

const components = {
  tipoidentificacion: TipoIdentificacion,
  roll: Rol,
  proveedor: Proveedor,
  pais: Pais,
  departamento: Departamento,
  municipio: Municipio,
  almacen: Almacen,
  espacio: Espacio,
  tipo_espacio: TipoEspacio,
  bloque: Bloque,
  tipo_bloque: TipoBloque,
  tipo_sede: TipoSedes,
  sede: Sede,
  presentacion_producto: Presentacionproducto,
  presentacion: Presentacion,
  producto_categoria: ProductoCategoria,
  producto: Producto,
  produccion: Produccion,
  marca: Marca,
  unidad: Unidad,
  tipo_evaluacion: TipoEvaluacion,
  tipo_movimiento: TipoMovimiento,
  tipo_produccion: TipoProduccion,
  persona: Persona,
  pedido: Pedido,
  empresa: Empresa,
  kardex: kardex,
  media_card: MediaCard,
  evaluacion_item: EvaluacionItem,
  OrdenCompra: OrdenCompra,
  Ocupacion: Ocupacion,
  grupo: Grupo,
  movimiento: Movimineto,
  Proceso: Proceso,
  ingrediente: Ingrediente,
  seccion: Seccion,
  subseccion: Subseccion,
  tipo_inventario: TipoInventario,
  Inventario: Inventario,
  IngredientePresentacionProducto: IngredientePresentacionProducto,
  RE_pedido: Re_pedido,
  RE_kardex: RE_kardex,
  RE_pv: RE_productoVencimiento,
  RE_oc: RE_ordenCompra,
  RE_fc: RE_fc,
 tipo_unidad: TipoUnida,
 CategoriaEstado: CategoriaEstado,
 rol: Rol,

};

const moduleImages = {
  persona,
  rol,
  tipoidentificacion: tipo_identificacion,
  tipo_evaluacion,
  empresa,
  pais,
  departamento,
  municipio,
  grupo,
  tipo_sede,
  sede,
  tipo_bloque,
  bloque,
  tipo_espacio,
  espacio,
  almacen,
  seccion: seccion,
  subseccion: subseccion,
  tipo_inventario: tip_inventario,
  Inventario: inventario,
  IngredientePresentacionProducto: ingredienteproductopresentacion,
  presentacion_producto,
  presentacion,
  producto_categoria,
  marca,
  ingrediente: ingrediente,
  tipo_produccion,
  produccion,
  Proceso: proceso,
  tipo_movimiento,
  movimiento: movimiento,
  unidad,
  producto: productos,
  proveedor,
  r_pedido: pedido,
  OrdenCompra: orde_compra,
  kardex: kardexx,
  Ocupacion: ocupacion,
  RE_pedido: r_pedidoo,
  RE_kardex: r_kardexx,
  RE_pv: prodcutos_vencidos,
  RE_oc: r_orden_compra,
  RE_fc: r_factura,
  media_card: img7,
  evaluacion_item: img2,
  pedido: pedido,
  tipo_unidad: tipo_unidad,
  CategoriaEstado: categoria_estado,
};



export default function Navigator2({
  setCurrentModuleItem,
  setMenuOpen,
  isAuthenticated,
}) {
  const { t } = useTranslation();
  const theme = useTheme();
  const isDark = theme.palette.mode === "dark";

  // ===== TOKENS VISUALES =====
  const sidebarBg = isDark
    ? alpha(theme.palette.background.paper, 0.98)
    : theme.palette.background.paper;
  const dividerColor = isDark ? alpha("#fff", 0.08) : alpha("#000", 0.08);
  const hoverBg = isDark
    ? alpha("#FFFFFF", 0.06)
    : alpha(theme.palette.primary.main, 0.06);
  const selectedBg = isDark
    ? alpha("#FFFFFF", 0.15)
    : alpha(theme.palette.primary.main, 0.12);
  const selectedColor = isDark ? "#FFFFFF" : theme.palette.primary.dark;
  const selectedBar = theme.palette.primary.main;

  const tileBorder = isDark
    ? alpha("#FFFFFF", 0.12)
    : alpha(theme.palette.primary.main, 0.28);
  const tileOverlayFrom = isDark ? alpha("#000", 0.05) : alpha("#000", 0.0);
  const tileOverlayTo = isDark ? alpha("#000", 0.65) : alpha("#000", 0.55);
  const tileTitleColor = isDark
    ? alpha("#fff", 0.95)
    : theme.palette.common.white;
  const tileTitleShadow = isDark
    ? "0 1px 2px rgba(0,0,0,.85)"
    : "0 1px 2px rgba(0,0,0,.45)";
  const tileIconBg = isDark
    ? alpha(theme.palette.primary.light, 0.22)
    : alpha("#fff", 0.75);
  const tileIconFg = isDark ? "#fff" : theme.palette.primary.dark;

  // ===== HELPERS =====
  const toKey = (x) => String(x ?? "").trim();
  const normalizeTipo = (t) => (t === "movil" ? "movil" : "web");

  // ===== ESTADOS =====
  const [menuItems, setMenuItems] = React.useState([]);
  const [selectedMenu, setSelectedMenu] = React.useState(null);
  const [tipoAplicacion, setTipoAplicacion] = React.useState(() =>
    normalizeTipo(localStorage.getItem("tipoAplicacion") || "web")
  );
  const [open, setOpen] = React.useState(() => {
    const stored = localStorage.getItem("sidebarOpen");
    return stored ? JSON.parse(stored) : true;
  });

  // ===== EFECTOS =====
  React.useEffect(() => {
    setMenuOpen?.(open);
  }, [open]);

  React.useEffect(() => {
    if (!isAuthenticated) return;

    const tipo = normalizeTipo(tipoAplicacion);
    localStorage.setItem("tipoAplicacion", tipo);

    axios
      // Endpoint correcto con query param
      .get("/v2/menu", { params: { tipoAplicacion: tipo } })
      .then(({ data }) => {
        // Adaptar la respuesta (SubSistemaResponseDTO)
        const subsistemas = Array.isArray(data) ? data : [];
        const adapted = subsistemas.map((ss) => ({
          id: ss.nombre, // clave del subsistema
          text: ss.nombre,
          icon: ss.icono,
          children: (ss.modulos ?? []).map((m) => ({
            id: m.id,
            text: m.nombre,
            icon: m.icono,
            url: m.url,
          })),
        }));
        setMenuItems(adapted);

        // Restaurar módulo activo si existe
        const saved = localStorage.getItem("activeModule");
        if (saved && components[saved]) {
          setCurrentModuleItem(React.createElement(components[saved]));
          setSelectedMenu(toKey(saved));
          return;
        }

        // Si no hay guardado, seleccionar el primer subsistema
        const firstMenu = adapted[0];
        if (!firstMenu) {
          setSelectedMenu(null);
          setCurrentModuleItem(null);
          return;
        }

        if (firstMenu?.children?.length) {
          setSelectedMenu(toKey(firstMenu.id));
          setCurrentModuleItem(renderSubmenu(firstMenu.children, firstMenu.id));
        } else {
          const k = toKey(firstMenu.id);
          const Comp = components[k];
          setSelectedMenu(k);
          if (Comp) {
            localStorage.setItem("activeModule", k);
            setCurrentModuleItem(<Comp />);
          } else {
            setCurrentModuleItem(null);
          }
        }
      })
      .catch((err) => {
        console.error("[Navigator2] Error cargando menú:", {
          status: err?.response?.status,
          data: err?.response?.data,
        });
      });
  }, [isAuthenticated, tipoAplicacion]);

  // ===== HANDLERS =====
  const toggleDrawer = () => {
    const newOpen = !open;
    setOpen(newOpen);
    setMenuOpen?.(newOpen);
    localStorage.setItem("sidebarOpen", JSON.stringify(newOpen));
  };

  const handleMenuClick = (menuId) => {
    const key = toKey(menuId);
    setSelectedMenu(key);

    const menu = menuItems.find((item) => toKey(item.id) === key);
    if (menu?.children?.length) {
      setCurrentModuleItem(renderSubmenu(menu.children, menuId));
    } else {
      localStorage.setItem("activeModule", key);
      const Component = components[key];
      setCurrentModuleItem(Component ? <Component /> : null);
    }
  };

  const handleSubMenuClick = (subMenuId, parentMenuId) => {
    const key = toKey(subMenuId);
    localStorage.setItem("activeModule", key);
    setSelectedMenu(toKey(parentMenuId));
    const Component = components[key];
    setCurrentModuleItem(Component ? <Component /> : null);
  };

  // ===== RENDER DE SUBMENÚ =====
  const renderSubmenu = (children, parentMenuId) => (
    <Box
      key={theme.palette.mode}
      sx={{
        bgcolor: (t) =>
          t.palette.mode === "dark" ? "transparent" : "#e7f6f7",
        borderRadius: 3,
        p: { xs: 2, md: 3 },
      }}
    >
      <Grid container spacing={2.5}>
        {children.map(({ id, text, icon }) => (
          <Grid
            item
            xs={12}
            sm={6}
            md={4}
            lg={3}
            key={`submenu:${toKey(parentMenuId)}:${toKey(id)}`}
          >
            <Box
              onClick={() => handleSubMenuClick(id, parentMenuId)}
              sx={{
                position: "relative",
                height: 190,
                borderRadius: 3,
                overflow: "hidden",
                cursor: "pointer",
                transition: "transform .2s ease",
                "&:hover": { transform: "translateY(-2px)" },
                "&:before": {
                  content: '""',
                  position: "absolute",
                  inset: 0,
                  borderRadius: 3,
                  border: `2px solid ${tileBorder}`,
                  pointerEvents: "none",
                },
              }}
            >
              {/* Imagen */}
              <Box
                component="img"
                src={moduleImages[id] || img1}
                alt={id}
                sx={{
                  position: "absolute",
                  inset: 0,
                  width: "100%",
                  height: "100%",
                  objectFit: "cover",
                  objectPosition: "50% 35%",
                }}
              />
              {/* Overlay */}
              <Box
                sx={{
                  position: "absolute",
                  inset: 0,
                  background: `linear-gradient(180deg, ${tileOverlayFrom} 45%, ${tileOverlayTo} 100%)`,
                }}
              />
              {/* Icono + título */}
              <Box
                sx={{
                  position: "absolute",
                  left: 12,
                  right: 12,
                  bottom: 12,
                  display: "flex",
                  alignItems: "center",
                  gap: 1,
                }}
              >
                <Box
                  sx={{
                    width: 36,
                    height: 36,
                    borderRadius: "50%",
                    display: "grid",
                    placeItems: "center",
                    bgcolor: tileIconBg,
                    color: tileIconFg,
                    flex: "0 0 auto",
                  }}
                >
                  {icons[icon]}
                </Box>
                <Typography
                  variant="subtitle1"
                  title={t(text)}
                  sx={{
                    fontWeight: 800,
                    lineHeight: 1.1,
                    color: tileTitleColor,
                    textShadow: tileTitleShadow,
                    overflow: "hidden",
                    whiteSpace: "nowrap",
                    textOverflow: "ellipsis",
                  }}
                >
                  {t(text)}
                </Typography>
              </Box>
            </Box>
          </Grid>
        ))}
      </Grid>
    </Box>
  );

  // ===== RENDER PRINCIPAL =====
  if (!isAuthenticated) return null;

  return (
    <Box
      sx={{
        position: "fixed",
        top: 65,
        left: 0,
        width: {
          xs: open ? "200px" : "60px",
          sm: open ? "220px" : "70px",
          md: open ? "250px" : "70px",
        },
        height: "calc(100vh - 65px)",
        bgcolor: sidebarBg,
        color: theme.palette.text.primary,
        borderRight: `1px solid ${dividerColor}`,
        transition: "width 0.25s ease",
        zIndex: 1200,
        display: "flex",
        flexDirection: "column",
      }}
    >
      {/* Header del sidebar */}
      <Box
        onClick={toggleDrawer}
        sx={{
          display: "flex",
          alignItems: "center",
          gap: 1,
          px: 2,
          py: 1.5,
          borderBottom: `1px solid ${dividerColor}`,
          cursor: "pointer",
        }}
      >
        <MenuIcon />
        {open && (
          <Typography variant="h6" fontWeight={800}>
            {t("Menú")}
          </Typography>
        )}
      </Box>

      {/* Lista de subsistemas */}
      <List sx={{ px: 1, py: 1, flex: 1, overflowY: "auto" }}>
        {menuItems.map(({ id, text, icon }) => (
          <ListItem
            key={`menu:${toKey(id)}`}
            disablePadding
            onClick={() => handleMenuClick(id)}
          >
            <ListItemButton
              selected={selectedMenu === toKey(id)}
              sx={{
                borderRadius: 2,
                mx: 1,
                my: 0.5,
                justifyContent: open ? "flex-start" : "center",
                "&:hover": { bgcolor: hoverBg },
                "&.Mui-selected": {
                  bgcolor: selectedBg,
                  color: selectedColor,
                  "& .MuiListItemIcon-root": { color: selectedColor },
                  "& .MuiListItemText-primary": {
                    color: selectedColor,
                    fontWeight: 800,
                  },
                  "&:hover": { bgcolor: selectedBg },
                  position: "relative",
                  boxShadow: isDark
                    ? `inset 0 0 0 1px ${alpha(
                        theme.palette.primary.main,
                        0.25
                      )}`
                    : "none",
                  "&::before": {
                    content: '""',
                    position: "absolute",
                    left: 0,
                    top: 6,
                    bottom: 6,
                    width: 3.5,
                    borderRadius: "0 3px 3px 0",
                    backgroundColor: selectedBar,
                  },
                },
              }}
            >
              <ListItemIcon
                sx={{
                  color: theme.palette.text.primary,
                  minWidth: 0,
                  mr: open ? 2 : 0,
                }}
              >
                {icons[icon]}
              </ListItemIcon>
              {open && (
                <ListItemText
                  primary={t(text)}
                  primaryTypographyProps={{ fontWeight: 600 }}
                />
              )}
            </ListItemButton>
          </ListItem>
        ))}
        <Divider sx={{ my: 1.5, borderColor: dividerColor }} />
      </List>
    </Box>
  );
}
