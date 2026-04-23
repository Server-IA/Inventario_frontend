import React from "react";
import { Stack, Typography, Box } from "@mui/material";
import { useTheme, alpha } from "@mui/material/styles";
import { useTranslation } from "react-i18next";

export default function SectionHeader({ title, titleKey, subtitle, subtitleKey, icon, rightNode, titleOptions, subtitleOptions }) {
  const theme = useTheme();
  const { t } = useTranslation();
  const isDark = theme.palette.mode === "dark";
  const dividerColor = isDark ? alpha("#dfeae6", 0.18) : "#9fb4ad";
  const resolvedTitle = titleKey ? t(titleKey, titleOptions) : title;
  const resolvedSubtitle = subtitleKey ? t(subtitleKey, subtitleOptions) : subtitle;

  return (
    <Stack spacing={1.25} sx={{ mb: 2.5, mt: { xs: 1.75, sm: 1.25, md: 1, lg: 0 }, width: "100%" }}>
      <Box
        sx={{
          position: "relative",
          width: "100%",
          minHeight: { xs: "auto", md: 56 },
        }}
      >
        <Stack
          direction={{ xs: "column", md: "row" }}
          spacing={1.5}
          alignItems="center"
          justifyContent="center"
          sx={{
            textAlign: "center",
            position: { xs: "static", md: "absolute" },
            left: { md: "50%" },
            top: { md: "50%" },
            transform: { md: "translate(-50%, -50%)" },
            width: { xs: "100%", md: "auto" },
            pointerEvents: "none",
          }}
        >
          {icon && <Box sx={{ color: "text.primary", display: "grid", placeItems: "center" }}>{icon}</Box>}
          <Stack spacing={0} alignItems="center">
            <Typography
              variant="h4"
              fontWeight={800}
              textAlign="center"
              sx={{
                fontSize: { xs: "1.65rem", sm: "1.9rem", md: "2.1rem", lg: "2.35rem" },
                lineHeight: 1.1,
                letterSpacing: "-0.02em",
              }}
            >
              {resolvedTitle}
            </Typography>
            {resolvedSubtitle && (
              <Typography variant="body2" color="text.secondary" textAlign="center">
                {resolvedSubtitle}
              </Typography>
            )}
          </Stack>
        </Stack>

        {rightNode && (
          <Box
            sx={{
              display: "flex",
              justifyContent: { xs: "center", md: "flex-end" },
              mt: { xs: 1.5, md: 0 },
              width: "100%",
            }}
          >
            {rightNode}
          </Box>
        )}
      </Box>

      <Box
        sx={{
          width: "100%",
          height: "1px",
          bgcolor: dividerColor,
        }}
      />
    </Stack>
  );
}
