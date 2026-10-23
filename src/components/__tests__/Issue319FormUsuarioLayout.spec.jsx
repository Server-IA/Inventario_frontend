import { describe, it, expect } from "vitest";
import FormUsuario from "../Usuario/FormUsuario";

describe("Issue #319 - FormUsuario layout y definición de componentes", () => {
  it("exporta FormUsuario como componente funcional React válido con PropTypes definidos", () => {
    expect(typeof FormUsuario).toBe("function");
    expect(FormUsuario.propTypes).toBeDefined();
    expect(FormUsuario.propTypes.open).toBeDefined();
    expect(FormUsuario.propTypes.onClose).toBeDefined();
  });
});
