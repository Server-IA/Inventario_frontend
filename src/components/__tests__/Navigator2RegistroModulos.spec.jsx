import { describe, expect, it } from "vitest";
import TipoCostoIndirecto from "../Costos/TipoCostoIndirecto/TipoCostoIndirecto.jsx";
import { components } from "../dashboard/Navigator2.jsx";

describe("Navigator2 module registry", () => {
  it("registers the backend menu id for indirect cost types", () => {
    expect(components["tipo-costo-indirecto"]).toBe(TipoCostoIndirecto);
  });
});
