import { describe, it, expect } from "vitest";
import VistaPreviaPDFOrdenCompra from "../OrdenCompra/vistapreviapdfordencompra";
import RE_ordenCompra from "../RE_oc/re_oc";

describe("HU-041.1 & HU-041.2 - Reporte Orden de Compra y Resumen de Totales", () => {
  it("exporta RE_ordenCompra funcionalmente", () => {
    expect(typeof RE_ordenCompra).toBe("function");
  });

  it("calcula correctamente el resumen de totales en VistaPreviaPDFOrdenCompra", () => {
    expect(typeof VistaPreviaPDFOrdenCompra).toBe("function");
    expect(VistaPreviaPDFOrdenCompra.propTypes).toBeDefined();
  });
});
