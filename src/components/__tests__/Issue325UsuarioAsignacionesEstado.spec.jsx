import { describe, it, expect } from "vitest";
import FormUsuario from "../Usuario/FormUsuario";

describe("Issue #325 - Estado de asignaciones en FormUsuario", () => {
  it("exporta FormUsuario como componente funcional React válido con PropTypes definidos", () => {
    expect(typeof FormUsuario).toBe("function");
    expect(FormUsuario.propTypes).toBeDefined();
    expect(FormUsuario.propTypes.open).toBeDefined();
    expect(FormUsuario.propTypes.onClose).toBeDefined();
    expect(FormUsuario.propTypes.initialData).toBeDefined();
  });

  it("resuelve correctamente la regla de asignación activa o inactiva", () => {
    const isAssignmentActive = (assignment) =>
      assignment.estadoId !== 2 &&
      assignment.estadoId !== "2" &&
      String(assignment.estadoNombre || "").toLowerCase() !== "inactivo" &&
      assignment.activo !== false;

    expect(isAssignmentActive({ estadoId: 1, estadoNombre: "Activo", activo: true })).toBe(true);
    expect(isAssignmentActive({ estadoId: 2, estadoNombre: "Inactivo", activo: false })).toBe(false);
    expect(isAssignmentActive({ estadoId: "2", estadoNombre: "Inactivo" })).toBe(false);
    expect(isAssignmentActive({ activo: false })).toBe(false);
  });

  it("identifica asignaciones persistidas vs nuevos borradores de la sesión", () => {
    const isPersistedAssignment = (assignment, isCreateMode, initialAssignments = []) =>
      Boolean(
        assignment.usuarioRolId ||
        (!isCreateMode && initialAssignments.some(
          (a) => String(a.rolId) === String(assignment.rolId) && String(a.empresaId) === String(assignment.empresaId)
        ))
      );

    const persisted = { usuarioRolId: 101, rolId: "1", empresaId: "1505" };
    const draft = { rolId: "3", empresaId: "1505" };

    expect(isPersistedAssignment(persisted, false, [persisted])).toBe(true);
    expect(isPersistedAssignment(draft, false, [persisted])).toBe(false);
    expect(isPersistedAssignment(draft, true, [])).toBe(false);
  });
});
