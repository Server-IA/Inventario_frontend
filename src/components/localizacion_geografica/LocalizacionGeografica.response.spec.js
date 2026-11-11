import { describe, expect, it } from "vitest";
import { asLocationList } from "./LocalizacionGeografica";

describe("asLocationList", () => {
  it("accepts plain arrays returned by legacy location endpoints", () => {
    const rows = [{ id: 1, nombre: "Colombia" }];

    expect(asLocationList(rows)).toEqual(rows);
  });

  it("unwraps paginated responses instead of silently rendering an empty grid", () => {
    const rows = [{ id: 1, nombre: "Huila" }];

    expect(asLocationList({ content: rows, totalElements: 1 })).toEqual(rows);
  });

  it("returns an empty list only for unsupported payloads", () => {
    expect(asLocationList(null)).toEqual([]);
    expect(asLocationList({ content: "invalid" })).toEqual([]);
  });
});
