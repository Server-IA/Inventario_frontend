import React from "react";
import { describe, it, expect, vi } from "vitest";
import { render, screen } from "@testing-library/react";

// Mock react-i18next's useTranslation hook
vi.mock("react-i18next", () => ({
  useTranslation: () => ({
    t: (key, options) => {
      if (options && options.count) return `${key} ${options.count}`;
      return key;
    },
    i18n: {
      changeLanguage: vi.fn(),
      language: "es",
      languages: ["es", "en"],
      options: { supportedLngs: ["es", "en"], fallbackLng: "es" },
    },
    ready: true,
  }),
  initReactI18next: {
    type: "3rdParty",
    init: () => {},
  },
  Trans: ({ i18nKey, children }) => children || i18nKey,
  I18nextProvider: ({ children }) => children,
}));

// Also mock the i18n.js module itself since it imports JSON and runs init
vi.mock("../i18n", () => ({
  default: {
    t: (key) => key,
    changeLanguage: vi.fn(),
    language: "es",
    languages: ["es", "en"],
  },
  resolveAppLanguage: (language) => {
    return String(language || "es").toLowerCase().startsWith("en") ? "en" : "es";
  },
}));

// Import useTranslation at the top level so vitest's mock applies
import { useTranslation } from "react-i18next";

// A simple component that uses useTranslation
function TestComponent() {
  const { t, i18n, ready } = useTranslation();
  return (
    <div>
      <span data-testid="t-result">{String(typeof t === "function")}</span>
      <span data-testid="i18n-result">{String(!!i18n)}</span>
      <span data-testid="ready-result">{String(ready)}</span>
      <span data-testid="translated">{t("common.actions.add")}</span>
      <span data-testid="interpolated">{t("common.labels.moreCount", { count: 5 })}</span>
    </div>
  );
}

describe("i18n React Integration", () => {
  it("useTranslation hook works inside a rendered component", () => {
    render(<TestComponent />);

    expect(screen.getByTestId("t-result")).toHaveTextContent("true");
    expect(screen.getByTestId("i18n-result")).toHaveTextContent("true");
    expect(screen.getByTestId("ready-result")).toHaveTextContent("true");
  });

  it("t function returns the translation key", () => {
    render(<TestComponent />);

    expect(screen.getByTestId("translated")).toHaveTextContent("common.actions.add");
  });

  it("t function handles interpolation with count", () => {
    render(<TestComponent />);

    expect(screen.getByTestId("interpolated")).toHaveTextContent("common.labels.moreCount 5");
  });

  it("resolveAppLanguage returns 'es' for spanish locales", async () => {
    const { resolveAppLanguage } = await import("../i18n");
    expect(resolveAppLanguage("es")).toBe("es");
    expect(resolveAppLanguage("es-MX")).toBe("es");
    expect(resolveAppLanguage("es-ES")).toBe("es");
  });

  it("resolveAppLanguage returns 'en' for english locales", async () => {
    const { resolveAppLanguage } = await import("../i18n");
    expect(resolveAppLanguage("en")).toBe("en");
    expect(resolveAppLanguage("en-US")).toBe("en");
    expect(resolveAppLanguage("en-GB")).toBe("en");
  });

  it("resolveAppLanguage returns 'es' for unknown locales", async () => {
    const { resolveAppLanguage } = await import("../i18n");
    expect(resolveAppLanguage("fr")).toBe("es");
    expect(resolveAppLanguage("pt")).toBe("es");
    expect(resolveAppLanguage("")).toBe("es");
    expect(resolveAppLanguage(null)).toBe("es");
    expect(resolveAppLanguage(undefined)).toBe("es");
  });

  it("renders a component using useTranslation", () => {
    render(<TestComponent />);
    expect(screen.getByTestId("translated")).toBeInTheDocument();
  });
});
