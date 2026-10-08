import { describe, it, expect } from "vitest";
import VistaPreviaPDFOrdenCompra from "../OrdenCompra/vistapreviapdfordencompra";
import RE_ordenCompra from "../RE_oc/re_oc";

describe("HU-041.1 - Reporte Orden de Compra", () => {
  it("exporta RE_ordenCompra funcionalmente", () => {
    expect(typeof RE_ordenCompra).toBe("function");
  });

  it("exporta VistaPreviaPDFOrdenCompra funcionalmente", () => {
    expect(typeof VistaPreviaPDFOrdenCompra).toBe("function");
  });
});
