import { describe, expect, it } from "vitest";
import { canSaveKardex, resolveMovementTypeId, shouldResetEditLoading } from "./kardexEditSession";

describe("kardex edit session safeguards", () => {
  it("blocks saving while an edit request is still loading", () => {
    expect(canSaveKardex({ savingKardex: false, loadingEditData: true })).toBe(false);
    expect(canSaveKardex({ savingKardex: true, loadingEditData: false })).toBe(false);
    expect(canSaveKardex({ savingKardex: false, loadingEditData: false })).toBe(true);
  });

  it("resets edit loading only after both dialogs are closed", () => {
    expect(shouldResetEditLoading({ open: false, articleModalOpen: false })).toBe(true);
    expect(shouldResetEditLoading({ open: true, articleModalOpen: false })).toBe(false);
    expect(shouldResetEditLoading({ open: false, articleModalOpen: true })).toBe(false);
  });

  it("resolves a movement type by name once its catalog arrives", () => {
    const movementTypes = [
      { id: 1, nombre: "Entrada" },
      { id: 2, nombre: "Traslado" },
    ];

    expect(resolveMovementTypeId(movementTypes, " traslado ")).toBe(2);
    expect(resolveMovementTypeId([], "Entrada")).toBe("");
  });
});
