import React from "react";
import { Box, Typography, Link } from "@mui/material";
import { useTheme, alpha } from "@mui/material/styles";
import { useTranslation } from "react-i18next";
import packageInfo from "../../../package.json";
import { SiteProps } from "./SiteProps";

export default function Copyright({
  fullWidth = true,
  seamlessAbove = true,
  ...props
}) {
  const { t } = useTranslation();
  const theme = useTheme();
  const isDark = theme.palette.mode === "dark";
  const appVersion = packageInfo.version || "0.4.0";

  const softBg = isDark ? alpha(theme.palette.primary.light, 0.08) : "#ffffff";

  return (
    <Box
      component="footer"
      sx={{
        position: "relative",
        width: fullWidth ? "100svw" : "100%",
        ml: fullWidth ? "calc(50% - 50svw)" : 0,
        mr: fullWidth ? "calc(50% - 50svw)" : 0,
        "@supports not (width: 100svw)": {
          width: fullWidth ? "100vw" : "100%",
          ml: fullWidth ? "calc(50% - 50vw)" : 0,
          mr: fullWidth ? "calc(50% - 50vw)" : 0,
        },
        bgcolor: softBg,
        py: 3,
        borderTop: "none !important",
        mt: seamlessAbove ? "-1px" : 0,
        display: "flex",
        flexDirection: "column",
        alignItems: "center", // esto centra todo horizontalmente
      }}
    >
      <Typography variant="body2" color="text.secondary" align="center" {...props}>
        {t("common.footer.copyrightPrefix")}
        {" "}
        <Link
          color="inherit"
          href="https://inmero.co/"
          target="_blank"
          rel="noopener"
          sx={{
            fontWeight: 600,
            textDecoration: "none",
            "&:hover": { textDecoration: "underline" },
          }}
        >
          {SiteProps.appName}
        </Link>{" "}
        {new Date().getFullYear()}
        {"."}
      </Typography>

      <Typography variant="body2" color="text.secondary" align="center" gutterBottom>
        {t("common.footer.buildVersion", { version: appVersion })}
      </Typography>
    </Box>
  );
}
