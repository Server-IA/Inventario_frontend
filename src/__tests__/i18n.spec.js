import { describe, it, expect, beforeEach, vi } from "vitest";

// ============================================================
// Mock i18next + plugins BEFORE importing the module under test
// so that i18n.init() is a no-op and doesn't try to load JSON
// locale files or touch the DOM / localStorage at import time.
// ============================================================
vi.mock("i18next", () => ({
  default: {
    use: vi.fn().mockReturnThis(),
    init: vi.fn(),
  },
}));

vi.mock("react-i18next", () => ({
  initReactI18next: {},
}));

vi.mock("i18next-browser-languagedetector", () => ({
  default: {},
}));

// ------------------------------------------------------------------
// Now safe to import — resolveAppLanguage is a pure function that
// only touches localStorage / navigator, both fine to mock.
// ------------------------------------------------------------------
import { resolveAppLanguage } from "../i18n.js";

// ------------------------------------------------------------------
// normalizeLanguage is NOT exported from i18n.js, but it is a pure
// function — copy it here so we can test it directly.
// ------------------------------------------------------------------
const normalizeLanguage = (language) =>
  String(language || "")
    .toLowerCase()
    .startsWith("en")
    ? "en"
    : "es";

// ============================================================
// Tests
// ============================================================
describe("i18n", () => {
  // ---------- normalizeLanguage ----------
  describe("normalizeLanguage", () => {
    it("returns 'en' for any English language code", () => {
      expect(normalizeLanguage("en")).toBe("en");
      expect(normalizeLanguage("EN")).toBe("en");
      expect(normalizeLanguage("en-US")).toBe("en");
      expect(normalizeLanguage("en-GB")).toBe("en");
      expect(normalizeLanguage("english")).toBe("en");
    });

    it("returns 'es' for any non-English language code", () => {
      expect(normalizeLanguage("es")).toBe("es");
      expect(normalizeLanguage("ES")).toBe("es");
      expect(normalizeLanguage("es-CO")).toBe("es");
      expect(normalizeLanguage("fr")).toBe("es");
      expect(normalizeLanguage("de")).toBe("es");
      expect(normalizeLanguage("pt-BR")).toBe("es");
    });

    it("returns 'es' for empty / null / undefined input", () => {
      expect(normalizeLanguage("")).toBe("es");
      expect(normalizeLanguage(null)).toBe("es");
      expect(normalizeLanguage(undefined)).toBe("es");
    });

    it("returns 'es' when called with no arguments", () => {
      expect(normalizeLanguage()).toBe("es");
    });

    it("returns 'es' for numbers or other non-string coercible values", () => {
      expect(normalizeLanguage(123)).toBe("es"); // "123" does not start with "en"
      expect(normalizeLanguage(0)).toBe("es");   // "0"
    });
  });

  // ---------- resolveAppLanguage ----------
  describe("resolveAppLanguage", () => {
    beforeEach(() => {
      localStorage.clear();
      vi.stubGlobal("navigator", { language: "es-CO" });
    });

    it("uses the explicit argument when provided", () => {
      expect(resolveAppLanguage("en-US")).toBe("en");
      expect(resolveAppLanguage("es-MX")).toBe("es");
      expect(resolveAppLanguage("fr")).toBe("es");
    });

    it("falls back to localStorage preferredLanguage", () => {
      localStorage.setItem("preferredLanguage", "en");
      expect(resolveAppLanguage()).toBe("en");

      localStorage.setItem("preferredLanguage", "es-CO");
      expect(resolveAppLanguage()).toBe("es");
    });

    it("falls back to localStorage i18nextLng when preferredLanguage is absent", () => {
      localStorage.setItem("i18nextLng", "en-US");
      expect(resolveAppLanguage()).toBe("en");
    });

    it("prefers preferredLanguage over i18nextLng", () => {
      localStorage.setItem("preferredLanguage", "en");
      localStorage.setItem("i18nextLng", "es");
      expect(resolveAppLanguage()).toBe("en");
    });

    it("falls back to navigator.language when nothing is in localStorage", () => {
      vi.stubGlobal("navigator", { language: "en-GB" });
      expect(resolveAppLanguage()).toBe("en");

      vi.stubGlobal("navigator", { language: "es-ES" });
      expect(resolveAppLanguage()).toBe("es");
    });

    it("uses explicit arg even when localStorage and navigator are set", () => {
      localStorage.setItem("preferredLanguage", "es");
      localStorage.setItem("i18nextLng", "es");
      vi.stubGlobal("navigator", { language: "es" });
      expect(resolveAppLanguage("en")).toBe("en");
    });

    it("handles empty localStorage values gracefully", () => {
      localStorage.setItem("preferredLanguage", "");
      expect(resolveAppLanguage()).toBe("es"); // "" → normalize → "es"
    });
  });
});
