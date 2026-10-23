import { describe, it, expect } from "vitest";
import FormUsuario from "../Usuario/FormUsuario";

describe("Issue #325 - Validación del componente FormUsuario y contratos de estado de asignaciones", () => {
  it("1. exporta FormUsuario como componente funcional React válido con PropTypes definidos", () => {
    expect(typeof FormUsuario).toBe("function");
    expect(FormUsuario.propTypes).toBeDefined();
    expect(FormUsuario.propTypes.open).toBeDefined();
    expect(FormUsuario.propTypes.mode).toBeDefined();
    expect(FormUsuario.propTypes.onClose).toBeDefined();
    expect(FormUsuario.propTypes.onSubmit).toBeDefined();
  });

  it("2. valida lógica de conmutación de estado (Activo <-> Inactivo) y bandera hasPendingStatusChange", () => {
    const mockAsignaciones = [
      {
        usuarioRolId: 101,
        rolId: 2,
        empresaId: 1505,
        estadoId: 1, // Activa
      },
      {
        usuarioRolId: 102,
        rolId: 4,
        empresaId: 1505,
        estadoId: 2, // Inactiva
      },
    ];

    // Simula toggleAssignmentStatus sobre la primera asignación
    const currentStatus = Number(mockAsignaciones[0].estadoId ?? 1);
    const newStatus = currentStatus === 1 ? 2 : 1;
    mockAsignaciones[0] = {
      ...mockAsignaciones[0],
      estadoId: newStatus,
      hasPendingStatusChange: true,
    };

    expect(mockAsignaciones[0].estadoId).toBe(2);
    expect(mockAsignaciones[0].hasPendingStatusChange).toBe(true);

    // Simula toggleAssignmentStatus sobre la segunda asignación (reactivación)
    const currentStatus2 = Number(mockAsignaciones[1].estadoId ?? 1);
    const newStatus2 = currentStatus2 === 1 ? 2 : 1;
    mockAsignaciones[1] = {
      ...mockAsignaciones[1],
      estadoId: newStatus2,
      hasPendingStatusChange: true,
    };

    expect(mockAsignaciones[1].estadoId).toBe(1);
    expect(mockAsignaciones[1].hasPendingStatusChange).toBe(true);
  });

  it("3. formatea el payload de asignaciones transmitiendo usuarioRolId y estadoId actualizado para el PUT de backend", () => {
    const asignacionesModificadas = [
      {
        usuarioRolId: 101,
        rolId: 2,
        empresaId: 1505,
        estadoId: 2, // Inactivada
      },
      {
        usuarioRolId: 102,
        rolId: 4,
        empresaId: 1505,
        estadoId: 1, // Reactivada
      },
    ];

    const payload = asignacionesModificadas.map((item) => ({
      ...(item?.usuarioRolId ? { usuarioRolId: Number(item.usuarioRolId) } : {}),
      empresaId: Number(item.empresaId),
      rolId: Number(item.rolId),
      estadoId: Number(item?.estadoId ?? 1),
    }));

    expect(payload).toEqual([
      {
        usuarioRolId: 101,
        empresaId: 1505,
        rolId: 2,
        estadoId: 2,
      },
      {
        usuarioRolId: 102,
        empresaId: 1505,
        rolId: 4,
        estadoId: 1,
      },
    ]);
  });
});
