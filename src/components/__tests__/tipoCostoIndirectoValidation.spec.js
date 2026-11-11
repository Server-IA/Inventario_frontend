import { describe, expect, it } from "vitest";
import {
  getServerErrorMessageKey,
  parseServerError,
} from "../Costos/TipoCostoIndirecto/tipoCostoIndirectoValidation";

describe("tipoCostoIndirectoValidation", () => {
  it("maps known HTTP statuses to translatable message keys", () => {
    expect(getServerErrorMessageKey(403)).toBe("forbidden");
    expect(getServerErrorMessageKey(404)).toBe("notFound");
    expect(getServerErrorMessageKey(409)).toBe("conflict");
    expect(getServerErrorMessageKey(500)).toBeNull();
  });

  it("does not hardcode a Spanish fallback while parsing a server error", () => {
    const parsed = parseServerError({
      response: {
        status: 403,
        data: {
          detail: "Detalle devuelto por el backend",
          invalid_params: [{ name: "nombre" }],
        },
      },
    });

    expect(parsed).toMatchObject({
      status: 403,
      detail: "Detalle devuelto por el backend",
      messageKey: "forbidden",
      fieldErrors: { nombre: "invalidValue" },
    });
  });
});
