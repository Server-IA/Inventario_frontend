import { describe, it, expect } from "vitest";
import { resolveBackendMessage } from "../Usuario/Usuario";

describe("Issue #321 - Interpretación de invalid_params en errores de asignaciones", () => {
  it("Criterio QA 1: interpreta invalid_params como objeto y muestra el mensaje funcional de rol y empresa", () => {
    const errorResponse = {
      detail: "La solicitud contiene datos inválidos o acciones no permitidas.",
      invalid_params: {
        "asignaciones[2].rolId": "El rol 'GERENTE' no está activo para la empresa 'Empresa Norte'.",
      },
    };

    const message = resolveBackendMessage(errorResponse);
    expect(message).toContain("La solicitud contiene datos inválidos o acciones no permitidas.");
    expect(message).toContain("El rol 'GERENTE' no está activo para la empresa 'Empresa Norte'.");
    // No debe exponer el índice técnico como información principal
    expect(message).not.toContain("(asignaciones[2].rolId)");
  });

  it("Criterio QA 2: cuando hay varias asignaciones inválidas en el objeto, muestra todos los mensajes de forma separada", () => {
    const errorResponse = {
      detail: "La solicitud contiene datos inválidos o acciones no permitidas.",
      invalid_params: {
        "asignaciones[1].rolId": "El rol 'OPERADOR' no está activo para la empresa 'Empresa Sur'.",
        "asignaciones[2].rolId": "El rol 'GERENTE' no está activo para la empresa 'Empresa Norte'.",
        "asignaciones[3].rolId": "La vigencia de la asignación está vencida.",
      },
    };

    const message = resolveBackendMessage(errorResponse);
    expect(message).toContain("La solicitud contiene datos inválidos o acciones no permitidas.");
    expect(message).toContain("El rol 'OPERADOR' no está activo para la empresa 'Empresa Sur'.");
    expect(message).toContain("El rol 'GERENTE' no está activo para la empresa 'Empresa Norte'.");
    expect(message).toContain("La vigencia de la asignación está vencida.");
  });

  it("compatibilidad con formato array en invalid_params", () => {
    const errorResponse = {
      detail: "Datos inválidos",
      invalid_params: [
        {
          name: "asignaciones[0].rolId",
          reason: "El rol 'ADMIN' no pertenece a la empresa.",
        },
      ],
    };

    const message = resolveBackendMessage(errorResponse);
    expect(message).toContain("Datos inválidos");
    expect(message).toContain("El rol 'ADMIN' no pertenece a la empresa.");
  });

  it("si no hay invalid_params, devuelve el mensaje de detail normal", () => {
    const normalResponse = {
      detail: "Error general del servidor.",
    };

    const message = resolveBackendMessage(normalResponse);
    expect(message).toBe("Error general del servidor.");
  });
});
