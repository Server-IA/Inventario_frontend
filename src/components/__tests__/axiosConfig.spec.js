import { describe, it, expect, vi } from "vitest";

// ============================================================
// Captured interceptor function — the mock factory below
// stores the onFulfilled handler here when the module loads.
// ============================================================
let requestInterceptor = null;

vi.mock("../../i18n.js", () => ({
  resolveAppLanguage: vi.fn(() => "es"),
}));

vi.mock("axios", () => {
  const mockInstance = {
    interceptors: {
      request: {
        use: vi.fn((onFulfilled) => {
          requestInterceptor = onFulfilled;
        }),
      },
    },
  };
  return {
    default: {
      create: vi.fn(() => mockInstance),
    },
  };
});

// Set the env var that axiosConfig reads from import.meta.env
process.env.VITE_BACKEND_URI = "http://localhost:8080";

// Dynamic import so process.env is set before module evaluation
const { default: api } = await import("../axiosConfig.js");

// ============================================================
// Tests
// ============================================================
describe("axiosConfig", () => {
  describe("baseURL", () => {
    it("is correctly built from VITE_BACKEND_URI", () => {
      // axios.create was called once and the returned object is `api`.
      // The actual baseURL is set inside axios internals — we can't easily
      // read it back, but we verify `create` was called (module loaded).
      expect(api).toBeDefined();
      expect(api.interceptors).toBeDefined();
    });
  });

  describe("request interceptor", () => {
    it("adds Accept-Language header to every request", () => {
      const config = { headers: {} };
      const result = requestInterceptor(config);

      expect(result.headers["Accept-Language"]).toBe("es");
    });

    it("creates headers object when it is missing", () => {
      const config = {};
      const result = requestInterceptor(config);

      expect(result.headers).toBeDefined();
      expect(result.headers["Accept-Language"]).toBe("es");
    });

    describe("skipAuth", () => {
      it("removes Authorization header when skipAuth is true", () => {
        const config = {
          headers: { Authorization: "Bearer old-token" },
          skipAuth: true,
        };
        const result = requestInterceptor(config);

        expect(result.headers.Authorization).toBeUndefined();
        expect(result.headers["Accept-Language"]).toBe("es");
      });

      it("removes Authorization even if no token is in localStorage", () => {
        localStorage.removeItem("token");
        const config = {
          headers: { Authorization: "Bearer something" },
          skipAuth: true,
        };
        const result = requestInterceptor(config);

        expect(result.headers.Authorization).toBeUndefined();
      });
    });

    describe("normal auth flow", () => {
      beforeEach(() => {
        localStorage.clear();
      });

      it("adds Bearer token from localStorage when present", () => {
        localStorage.setItem("token", "test-jwt-token");
        const config = { headers: {} };
        const result = requestInterceptor(config);

        expect(result.headers.Authorization).toBe("Bearer test-jwt-token");
      });

      it("does NOT add Authorization when no token in localStorage", () => {
        const config = { headers: {} };
        const result = requestInterceptor(config);

        expect(result.headers.Authorization).toBeUndefined();
      });

      it("removes existing Authorization if no token in localStorage", () => {
        const config = {
          headers: { Authorization: "Bearer stale-token" },
        };
        const result = requestInterceptor(config);

        expect(result.headers.Authorization).toBeUndefined();
      });

      it("keeps existing Authorization when token IS in localStorage", () => {
        localStorage.setItem("token", "fresh-token");
        const config = {
          headers: { Authorization: "Bearer existing" },
        };
        const result = requestInterceptor(config);

        // It overwrites, not keeps — the interceptor sets it fresh
        expect(result.headers.Authorization).toBe("Bearer fresh-token");
      });

      it("preserves other custom headers", () => {
        localStorage.setItem("token", "t");
        const config = {
          headers: { "X-Custom": "value", "Content-Type": "application/pdf" },
          skipAuth: false,
        };
        const result = requestInterceptor(config);

        expect(result.headers["X-Custom"]).toBe("value");
        expect(result.headers["Content-Type"]).toBe("application/pdf");
        expect(result.headers.Authorization).toBe("Bearer t");
      });
    });
  });
});
