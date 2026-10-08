import { describe, it, expect } from "vitest";
import Rkardex from "../RKardex/Rkardex";

describe("HU-039.2 & HU-039.3 - Reportes Kardex (Previsualización y Descarga)", () => {
  it("exporta el componente Rkardex funcionalmente", () => {
    expect(typeof Rkardex).toBe("function");
  });
});
