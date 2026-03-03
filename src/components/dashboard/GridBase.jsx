import { DataGrid } from "@mui/x-data-grid";
import { styled } from "@mui/material/styles";

const StyledDataGrid = styled(DataGrid)(({ theme }) => {
  const isDark = theme.palette.mode === "dark";

  const CONTAINER = isDark ? "#1e1e1e" : "#dfe3e8";
  const CONTENT = isDark ? "#2a2a2a" : "#eef1f4";
  const HOVER = isDark ? "#333333" : "#e3e8ee";
  const SELECTED = isDark ? "#2f3b52" : "#dde6f5";

  return {
    border: "none",
    borderRadius: 20,
    backgroundColor: CONTAINER,
    overflow: "hidden",

    "& .MuiDataGrid-columnHeaders": {
      backgroundColor: CONTAINER,
      borderBottom: "none",
      fontWeight: 600,
    },

    "& .MuiDataGrid-virtualScroller": {
      backgroundColor: CONTENT,
    },

    "& .MuiDataGrid-row": {
      backgroundColor: CONTENT,
    },

    "& .MuiDataGrid-row:hover": {
      backgroundColor: HOVER,
    },

    "& .MuiDataGrid-footerContainer": {
      borderTop: "none",
      backgroundColor: CONTAINER,
    },

    "& .MuiDataGrid-columnSeparator": {
      display: "none",
    },

    "& .MuiDataGrid-cell": {
      borderBottom: "none",
    },

    "& .MuiDataGrid-cell:focus, & .MuiDataGrid-cell:focus-within": {
      outline: "none",
    },

    "& .MuiDataGrid-row.Mui-selected": {
      backgroundColor: `${SELECTED} !important`,
      borderLeft: "4px solid #2563eb",
    },
  };
});

export default function GridBase(props) {
  return (
    <StyledDataGrid
      {...props}
      disableColumnMenu
      disableRowSelectionOnClick
      hideFooterSelectedRowCount
    />
  );
}