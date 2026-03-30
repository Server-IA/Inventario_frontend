import React from "react";
import { Stack, Typography, Box } from "@mui/material";
export default function SectionHeader({ title, subtitle, icon, rightNode }) {
  return (
    <Stack direction="row" alignItems="center" justifyContent="space-between" sx={{ mb: 2 }}>
      <Stack direction="row" spacing={1.5} alignItems="center">
        {icon && <Box sx={{ color: "text.primary" }}>{icon}</Box>}
        <Stack spacing={0}>
          <Typography variant="h6" fontWeight={700}>{title}</Typography>
          {subtitle && <Typography variant="body2" color="text.secondary">{subtitle}</Typography>}
        </Stack>
      </Stack>
      {rightNode && <Box>{rightNode}</Box>}
    </Stack>
  );
}
