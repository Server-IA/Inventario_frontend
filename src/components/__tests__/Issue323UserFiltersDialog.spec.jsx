import { describe, it, expect } from "vitest";
import UserFiltersDialog from "../Usuario/UserFiltersDialog";

describe("Issue #323 - UserFiltersDialog simplificado", () => {
  it("exporta UserFiltersDialog como componente funcional con PropTypes definidos", () => {
    expect(typeof UserFiltersDialog).toBe("function");
    expect(UserFiltersDialog.propTypes).toBeDefined();
    expect(UserFiltersDialog.propTypes.values).toBeDefined();
    expect(UserFiltersDialog.propTypes.onApply).toBeDefined();
    expect(UserFiltersDialog.propTypes.onClear).toBeDefined();
    expect(UserFiltersDialog.propTypes.onClose).toBeDefined();
  });
});
