/*=============================================================================
Nombre del archivo : AppDataGrid.jsx
Descripción        : Componente reutilizable para la grilla de datos.
===============================================================================
CONTROL DE CAMBIOS
+------------+---------+----------------------+-----------------------------+
|   Fecha    | Versión |      Autor           | Descripción del cambio      |
+------------+---------+----------------------+-----------------------------+
| 2026-05-06 | 0.4.0   | Cesar Medina         | Creación del archivo.       |
+------------+---------+----------------------+-----------------------------+
=============================================================================*/
/**
 * Componente reusable de grilla para módulos de gestión.
 * @module AppDataGrid
 */
import React, { useEffect, useMemo, useState } from "react";
import PropTypes from "prop-types";
import { Box, Paper, Stack, Chip } from "@mui/material";
import { DataGrid, GridToolbarContainer, GridToolbarColumnsButton, GridToolbarDensitySelector, GridToolbarExport } from "@mui/x-data-grid";
import { useTheme, alpha } from "@mui/material/styles";
import { useTranslation } from "react-i18next";

/**
 * Carga desde localStorage el modelo de visibilidad de columnas.
 * @param {string} storageKey Clave usada para persistir la preferencia.
 * @returns {Object|undefined} Modelo serializado o `undefined` cuando no existe.
 */
const loadStoredVisibilityModel = (storageKey) => {
  if (!storageKey) return undefined;

  try {
    const raw = localStorage.getItem(storageKey);
    return raw ? JSON.parse(raw) : undefined;
  } catch {
    return undefined;
  }
};

/**
 * Grilla reusable basada en MUI DataGrid con soporte para i18n, tipado explícito
 * de columnas y persistencia opcional de visibilidad.
 *
 * El componente permite que cada módulo describa sus columnas usando `type` y
 * `headerKey`, mientras la grilla resuelve formato, alineación y render visual
 * consistente para tipos como `number`, `date`, `boolean`, `status` y `actions`.
 *
 * @param {Object} props Propiedades del componente.
 * @param {Array<Object>} [props.rows=[]] Filas a renderizar.
 * @param {Array<Object>} props.columns Definición declarativa de columnas.
 * @param {function} [props.getRowId] Función para resolver el id de una fila.
 * @param {boolean} [props.loading=false] Indica estado de carga.
 * @param {boolean} [props.autoHeight=true] Ajusta automáticamente la altura a las filas visibles.
 * @param {boolean} [props.highlightOnHover=true] Activa resaltado visual al pasar el cursor.
 * @param {boolean} [props.selectOnClick=true] Permite selección de filas al hacer clic.
 * @param {Object|null} [props.selectedRow=null] Fila seleccionada externamente.
 * @param {function} [props.setSelectedRow] Setter para selección externa.
 * @param {Object} [props.paginationModel] Modelo de paginación server-side.
 * @param {number} [props.paginationModel.page] Página actual.
 * @param {number} [props.paginationModel.pageSize] Tamaño actual de página.
 * @param {number} [props.paginationModel.size] Alias de tamaño usado por algunos módulos.
 * @param {function} [props.setPaginationModel] Setter del modelo server-side.
 * @param {number} [props.rowCount] Total de filas cuando se pagina contra backend.
 * @param {number[]} [props.pageSizeOptions=[5,10,20,50]] Tamaños permitidos por página.
 * @param {boolean} [props.quickFilter=false] Muestra toolbar compacta con herramientas de grid.
 * @param {string} [props.columnVisibilityKey] Clave de localStorage para persistir visibilidad de columnas.
 * @param {Object} [props.columnVisibilityModel] Modelo controlado de visibilidad de columnas.
 * @param {function} [props.onColumnVisibilityModelChange] Callback cuando cambia la visibilidad.
 * @param {Object} [props.localeText] Sobrescritura parcial de textos internos de DataGrid.
 * @param {Object} [props.sx] Estilos extra para el DataGrid.
 * @param {React.ReactNode} [props.leftActions] Acciones renderizadas encima de la grilla, alineadas a la izquierda.
 * @param {React.ReactNode} [props.rightActions] Acciones renderizadas encima de la grilla, alineadas a la derecha.
 * @param {Object} [props.containerSx] Estilos extra para el contenedor `Paper`.
 * @param {function} [props.onEscape] Callback ejecutado al presionar Escape.
 * @returns {JSX.Element}
 */
export default function AppDataGrid({
  rows = [],
  columns = [],
  getRowId = (row) => row.id,
  loading = false,
  autoHeight = true,
  highlightOnHover = true,
  selectOnClick = true,
  selectedRow = null,
  setSelectedRow,
  paginationModel,
  setPaginationModel,
  rowCount,
  pageSizeOptions = [5, 10, 20, 50],
  quickFilter = false,
  columnVisibilityKey,
  columnVisibilityModel,
  onColumnVisibilityModelChange,
  localeText,
  sx,
  leftActions,
  rightActions,
  containerSx,
  onEscape,
  slots,
  slotProps,
  checkboxSelection = false,
  rowSelectionModel,
  onRowSelectionModelChange,
  disableRowSelectionOnClick,
  hideFooterSelectedRowCount = true,
}) {
  const theme = useTheme();
  const { t, i18n } = useTranslation();
  const isDark = theme.palette.mode === "dark";
  const [internalVisibilityModel, setInternalVisibilityModel] = useState(() =>
    loadStoredVisibilityModel(columnVisibilityKey)
  );
  const headerBg = isDark ? "#1a2a28" : "#dfeae6";
  const footerBg = isDark ? "#152422" : "#ecf3f0";
  const bodyBg = isDark ? "#0f1b1a" : "#f6fbf9";
  const paperShadow = isDark
    ? "0 8px 22px rgba(0,0,0,0.35), 0 2px 8px rgba(0,0,0,0.25)"
    : "0 6px 18px rgba(23,63,57,0.08), 0 2px 6px rgba(0,0,0,0.06)";
  const serverPagination = Boolean(paginationModel && setPaginationModel && typeof rowCount === "number");
  const effectiveColumnVisibilityModel =
    columnVisibilityModel ?? internalVisibilityModel;

  useEffect(() => {
    if (!columnVisibilityKey || columnVisibilityModel) return;
    setInternalVisibilityModel(loadStoredVisibilityModel(columnVisibilityKey));
  }, [columnVisibilityKey, columnVisibilityModel]);

  const gridLocaleText = useMemo(
    () => ({
      toolbarColumns: t("common.grid.toolbar.columns"),
      toolbarFilters: t("common.grid.toolbar.filters"),
      toolbarDensity: t("common.grid.toolbar.density"),
      toolbarExport: t("common.grid.toolbar.export"),
      noRowsLabel: t("common.grid.noRowsLabel"),
      noResultsOverlayLabel: t("common.grid.noResultsOverlayLabel"),
      columnsPanelTextFieldLabel: t("common.grid.columnsPanelTextFieldLabel"),
      columnsPanelTextFieldPlaceholder: t("common.grid.columnsPanelTextFieldPlaceholder"),
      columnsManagementShowHideAllText: t("common.grid.columnsManagementShowHideAllText"),
      columnsManagementReset: t("common.grid.columnsManagementReset"),
      toolbarQuickFilterPlaceholder: t("common.grid.toolbarQuickFilterPlaceholder"),
      MuiTablePagination: {
        labelRowsPerPage: t("common.grid.MuiTablePagination.labelRowsPerPage"),
      },
      ...localeText,
    }),
    [localeText, t, i18n.language]
  );

  const formatNumber = (value, column) => {
    if (value === null || value === undefined || value === "") return "";
    return new Intl.NumberFormat(i18n.language, column?.numberFormatOptions).format(Number(value));
  };

  const formatDate = (value, column) => {
    if (!value) return "";
    const date = value instanceof Date ? value : new Date(value);
    if (Number.isNaN(date.getTime())) return String(value);
    return new Intl.DateTimeFormat(i18n.language, column?.dateFormatOptions ?? (column?.type === "datetime"
      ? { dateStyle: "medium", timeStyle: "short" }
      : { dateStyle: "medium" })).format(date);
  };

  const resolveStatusMeta = (params, column) => {
    const raw =
      params?.value ??
      params?.row?.[column.field] ??
      params?.row?.estadoNombre ??
      params?.row?.estadoId;

    const normalized = String(raw ?? "").trim().toLowerCase();
    const statusMap = column?.statusMap ?? {};
    const mapped = statusMap[raw] ?? statusMap[String(raw)] ?? statusMap[normalized];
    if (mapped) {
      return {
        label: mapped.labelKey ? t(mapped.labelKey, mapped.labelOptions) : mapped.label,
        color: mapped.color ?? "default",
      };
    }
    const isActive =
      raw === 1 ||
      raw === "1" ||
      normalized === "activo" ||
      normalized === "active" ||
      normalized === "activa";

    return {
      label: isActive ? t("common.labels.active") : t("common.labels.inactive"),
      color: isActive ? "success" : "error",
    };
  };

  const resolveBooleanMeta = (params, column) => {
    const raw = params?.value ?? params?.row?.[column.field];
    const normalized = String(raw ?? "").trim().toLowerCase();
    const value = typeof raw === "boolean"
      ? raw
      : raw === 1 ||
        raw === "1" ||
        normalized === "true" ||
        normalized === "si" ||
        normalized === "sí" ||
        normalized === "yes";

    return {
      label: value ? t("common.labels.yes") : t("common.labels.no"),
      color: value ? "success" : "default",
    };
  };

  const columnsMemo = useMemo(
    () =>
      (Array.isArray(columns) ? columns : []).map((column) => {
        const columnType = column?.statusChip ? "status" : column?.type ?? "text";
        const baseColumn = {
          ...column,
          type: columnType === "custom" ? undefined : column.type,
          headerName: column.headerKey ? t(column.headerKey, column.headerOptions) : column.headerName,
        };

        if (columnType === "status") {
          return {
            ...baseColumn,
            headerClassName: [column.headerClassName, "col-estado"].filter(Boolean).join(" "),
            cellClassName: [column.cellClassName, "col-estado"].filter(Boolean).join(" "),
            sortable: column.sortable ?? true,
            align: column.align ?? "left",
            headerAlign: column.headerAlign ?? "left",
            renderCell: column.renderCell ?? ((params) => {
              const meta = resolveStatusMeta(params, column);
              return (
                <Box sx={{ display: "flex", justifyContent: "flex-start", width: "100%" }}>
                  <Chip label={meta.label} color={meta.color} size="small" />
                </Box>
              );
            }),
          };
        }

        if (columnType === "boolean") {
          return {
            ...baseColumn,
            align: column.align ?? "center",
            headerAlign: column.headerAlign ?? "center",
            renderCell: column.renderCell ?? ((params) => {
              const meta = resolveBooleanMeta(params, column);
              return <Chip label={meta.label} color={meta.color} size="small" variant="outlined" />;
            }),
          };
        }

        if (columnType === "number") {
          return {
            ...baseColumn,
            align: column.align ?? "right",
            headerAlign: column.headerAlign ?? "right",
            valueFormatter: column.valueFormatter ?? ((params) => formatNumber(params?.value, column)),
          };
        }

        if (columnType === "date" || columnType === "datetime") {
          return {
            ...baseColumn,
            valueFormatter: column.valueFormatter ?? ((params) => formatDate(params?.value, column)),
          };
        }

        if (columnType === "actions") {
          return {
            ...baseColumn,
            sortable: column.sortable ?? false,
            filterable: column.filterable ?? false,
            disableColumnMenu: column.disableColumnMenu ?? true,
            align: column.align ?? "center",
            headerAlign: column.headerAlign ?? "center",
            renderCell:
              column.renderCell ??
              ((params) => (
                <Stack direction="row" spacing={1} alignItems="center">
                  {typeof column.getActions === "function" ? column.getActions(params) : null}
                </Stack>
              )),
          };
        }

        return baseColumn;
      }),
    [columns, i18n.language, t]
  );

  const Toolbar = quickFilter
    ? function Toolbar() {
        return (
          <GridToolbarContainer>
            <GridToolbarColumnsButton />
            <GridToolbarDensitySelector />
            <GridToolbarExport />
          </GridToolbarContainer>
        );
      }
    : undefined;

  const mergedSx = {
    border: 0,
    bgcolor: "#fff",
    borderRadius: 4,
    overflow: "hidden",
    "& .MuiDataGrid-columnSeparator": { display: "none" },
    "& .MuiDataGrid-columnHeaders": {
      bgcolor: headerBg,
      borderTopLeftRadius: 4,
      borderTopRightRadius: 4,
      px: 0,
      boxShadow: isDark
        ? "inset 0 -1px 0 rgba(255,255,255,0.08)"
        : "inset 0 -1px 0 rgba(23,63,57,0.12)",
    },
    "& .MuiDataGrid-columnHeader": {
      pl: "12px !important",
      pr: "12px !important",
    },
    "& .MuiDataGrid-columnHeaderTitleContainer": {
      pl: "12px !important",
      pr: "12px !important",
      justifyContent: "flex-start",
    },
    "& .MuiDataGrid-columnHeaderTitle": {
      fontWeight: 700,
      textAlign: "left",
      color: isDark ? "#dfeae6" : undefined,
    },
    "& .MuiDataGrid-footerContainer": {
      borderTop: "none",
      px: 2,
      bgcolor: footerBg,
      borderBottomLeftRadius: 4,
      borderBottomRightRadius: 4,
    },
    "& .MuiDataGrid-cell": {
      py: 1.25,
      pl: "12px !important",
      pr: "12px !important",
      borderBottom: "none",
    },
    "& .MuiDataGrid-cellContent": {
      pl: "12px !important",
      pr: "12px !important",
    },
    "& .MuiDataGrid-row": {
      borderBottom: "none",
      position: "relative",
    },
    "& .MuiDataGrid-cell:focus, & .MuiDataGrid-cell:focus-within": {
      outline: "none",
    },
    ...(isDark
      ? {}
      : {
          "& .MuiDataGrid-row.Mui-selected": {
            outline: "none",
            boxShadow: "none",
            bgcolor: "rgba(23,63,57,0.08) !important",
          },
        }),
    "& .MuiDataGrid-row.Mui-selected::before": {
      content: '""',
      position: "absolute",
      left: 0,
      top: 0,
      bottom: 0,
      width: "4px",
      bgcolor: "#173f39",
      borderTopLeftRadius: "4px",
      borderBottomLeftRadius: "4px",
    },
    "& .MuiDataGrid-virtualScroller": {
      bgcolor: bodyBg,
    },
    ...(highlightOnHover
      ? {
          "& .MuiDataGrid-row:hover": {
            bgcolor: isDark ? "rgba(255,255,255,0.04)" : "action.hover",
          },
        }
      : {}),
    ...sx,
  };


  const handleVisibilityChange = (model) => {
    if (!columnVisibilityModel) {
      setInternalVisibilityModel(model);
    }
    onColumnVisibilityModelChange?.(model);
    if (columnVisibilityKey) {
      try {
        localStorage.setItem(columnVisibilityKey, JSON.stringify(model));
      } catch {
        // ignore localStorage errors (quota/private mode)
      }
    }
  };

  const resolvedSlots = slots ?? (quickFilter ? { toolbar: Toolbar } : undefined);
  const resolvedDisableRowSelectionOnClick =
    typeof disableRowSelectionOnClick === "boolean"
      ? disableRowSelectionOnClick
      : !selectOnClick;
  const internalRowSelectionModel =
    rowSelectionModel ??
    (selectOnClick && (selectedRow && getRowId ? [getRowId(selectedRow)] : selectedRow?.id ? [selectedRow.id] : []));

  return (
    <Paper sx={{ p: 0, width: "100%", maxWidth: "100%", overflowX: "auto", borderRadius: 6, boxShadow: paperShadow, bgcolor: "transparent", ...containerSx }}>
      {(leftActions || rightActions) && (
        <Stack direction="row" alignItems="center" justifyContent="space-between" sx={{ mb: 2 }}>
          <Box sx={{ display: "flex", gap: 1 }}>{leftActions}</Box>
          <Box sx={{ display: "flex", gap: 2 }}>{rightActions}</Box>
        </Stack>
      )}
      <DataGrid
        onCellKeyDown={(params, e) => {
          if (e?.key === "Escape") {
            e.preventDefault();
            e.stopPropagation();
            setSelectedRow?.(null);
            onEscape?.();
            try {
              const active = document.activeElement;
              if (active && typeof active.blur === "function") active.blur();
            } catch {
              // ignore focus handling errors
            }
          }
        }}
        rows={Array.isArray(rows) ? rows : []}
        columns={columnsMemo}
        getRowId={getRowId}
        loading={loading}
        autoHeight={autoHeight}
        pageSizeOptions={pageSizeOptions}
        hideFooterSelectedRowCount={hideFooterSelectedRowCount}
        checkboxSelection={checkboxSelection}
        paginationMode={serverPagination ? "server" : "client"}
        {...(serverPagination
          ? {
              paginationModel: {
                page: paginationModel.page ?? 0,
                pageSize: paginationModel.pageSize ?? paginationModel.size ?? pageSizeOptions[0] ?? 10,
              },
              onPaginationModelChange: (model) => {
                const next = {
                  page: model.page ?? 0,
                  size: model.pageSize ?? model.size ?? pageSizeOptions[0] ?? 10,
                };
                setPaginationModel?.(next);
              },
              rowCount,
            }
          : {
              initialState: {
                pagination: { paginationModel: { page: 0, pageSize: pageSizeOptions[0] ?? 5 } },
              },
            })}
        onRowClick={selectOnClick ? (params) => setSelectedRow?.(params.row) : undefined}
        rowSelectionModel={internalRowSelectionModel}
        onRowSelectionModelChange={(selection) => {
          onRowSelectionModelChange?.(selection);
          if (!selectOnClick || onRowSelectionModelChange) return;
          const id = Array.isArray(selection) && selection.length ? selection[0] : null;
          if (id == null) {
            setSelectedRow?.(null);
            return;
          }
          const row = (Array.isArray(rows) ? rows : []).find((r) => (getRowId ? getRowId(r) === id : r?.id === id)) || null;
          setSelectedRow?.(row);
        }}
        disableRowSelectionOnClick={!selectOnClick}
        slots={quickFilter ? { toolbar: Toolbar } : undefined}
        columnVisibilityModel={effectiveColumnVisibilityModel}
        onColumnVisibilityModelChange={handleVisibilityChange}
        localeText={gridLocaleText}
        autosizeOnMount={false}
        sx={mergedSx}
      />
    </Paper>
  );
}

AppDataGrid.propTypes = {
  rows: PropTypes.array,
  columns: PropTypes.array.isRequired,
  getRowId: PropTypes.func,
  loading: PropTypes.bool,
  autoHeight: PropTypes.bool,
  highlightOnHover: PropTypes.bool,
  selectOnClick: PropTypes.bool,
  selectedRow: PropTypes.object,
  setSelectedRow: PropTypes.func,
  paginationModel: PropTypes.shape({
    page: PropTypes.number,
    pageSize: PropTypes.number,
    size: PropTypes.number,
  }),
  setPaginationModel: PropTypes.func,
  rowCount: PropTypes.number,
  pageSizeOptions: PropTypes.array,
  quickFilter: PropTypes.bool,
  columnVisibilityKey: PropTypes.string,
  columnVisibilityModel: PropTypes.object,
  onColumnVisibilityModelChange: PropTypes.func,
  localeText: PropTypes.object,
  sx: PropTypes.object,
  leftActions: PropTypes.node,
  rightActions: PropTypes.node,
  containerSx: PropTypes.object,
  onEscape: PropTypes.func,
  slots: PropTypes.object,
  slotProps: PropTypes.object,
  checkboxSelection: PropTypes.bool,
  rowSelectionModel: PropTypes.array,
  onRowSelectionModelChange: PropTypes.func,
  disableRowSelectionOnClick: PropTypes.bool,
  hideFooterSelectedRowCount: PropTypes.bool,
};
