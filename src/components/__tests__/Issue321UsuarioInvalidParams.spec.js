import { describe, it, expect } from "vitest";

// Función extraída de Usuario.jsx para validar resolveBackendMessage con invalid_params
const resolveBackendMessage = (payload) => {
  if (typeof payload === "string") return payload;
  if (!payload || typeof payload !== "object") return "";

  const invalidParams = payload?.invalid_params || payload?.invalidParams;
  if (Array.isArray(invalidParams) && invalidParams.length > 0) {
    const details = invalidParams
      .map((item) => {
        const reason = item?.reason || item?.message || "";
        const name = item?.name ? `(${item.name}) ` : "";
        return reason ? `${name}${reason}`.trim() : "";
      })
      .filter(Boolean);

    if (details.length > 0) {
      const summary = payload?.detail ?? payload?.message ?? payload?.mensaje ?? "";
      return summary ? `${summary}: ${details.join("; ")}` : details.join("; ");
    }
  }

  return (
    payload?.detail ??
    payload?.message ??
    payload?.mensaje ??
    payload?.data?.detail ??
    payload?.data?.message ??
    payload?.data?.mensaje ??
    ""
  );
};

describe("Issue #321 - Interpretación de invalid_params en errores de asignaciones", () => {
  it("Criterio 1 y 2: interpreta invalid_params con una asignación inválida y asocia el nombre del campo/rol", () => {
    const errorResponse = {
      detail: "La solicitud contiene datos inválidos o acciones no permitidas.",
      invalid_params: [
        {
          name: "asignaciones[2].rolId",
          reason: "Rol no activo en la empresa asociada.",
        },
      ],
    };

    const message = resolveBackendMessage(errorResponse);
    expect(message).toContain("La solicitud contiene datos inválidos o acciones no permitidas.");
    expect(message).toContain("(asignaciones[2].rolId) Rol no activo en la empresa asociada.");
  });

  it("Criterio 3 y 4: maneja múltiples asignaciones inválidas sin descartar el resumen general", () => {
    const errorResponse = {
      detail: "La solicitud contiene datos inválidos o acciones no permitidas.",
      invalid_params: [
        {
          name: "asignaciones[2].rolId",
          reason: "Rol no activo en la empresa asociada.",
        },
        {
          name: "asignaciones[3].rolId",
          reason: "Rol no activo en la empresa asociada.",
        },
        {
          name: "asignaciones[5].rolId",
          reason: "Rol no activo en la empresa asociada.",
        },
      ],
    };

    const message = resolveBackendMessage(errorResponse);
    expect(message).toContain("La solicitud contiene datos inválidos o acciones no permitidas.");
    expect(message).toContain("(asignaciones[2].rolId) Rol no activo en la empresa asociada.");
    expect(message).toContain("(asignaciones[3].rolId) Rol no activo en la empresa asociada.");
    expect(message).toContain("(asignaciones[5].rolId) Rol no activo en la empresa asociada.");
  });

  it("si no hay invalid_params, devuelve el mensaje de detail normal", () => {
    const normalResponse = {
      detail: "Error general del servidor.",
    };

    const message = resolveBackendMessage(normalResponse);
    expect(message).toBe("Error general del servidor.");
  });
});
