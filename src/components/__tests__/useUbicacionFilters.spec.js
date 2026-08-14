import { describe, it, expect, vi } from "vitest";

// ============================================================
// Mock axiosConfig so the module loads cleanly (no side effects
// from i18n or axios configuration).
// ============================================================
vi.mock("../axiosConfig", () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    interceptors: { request: { use: vi.fn() } },
  },
}));

// Now safe to import the whole module
import * as mod from "../useUbicacionFilters.js";

// ============================================================
// Helper functions — NOT exported from the source module.
// Copy them here since they are pure and testable.
// ============================================================
const asArray = (x) =>
  Array.isArray(x) ? x : (x && Array.isArray(x.content) ? x.content : []);

const uniqById = (arr) => {
  const map = new Map();
  for (const it of arr ?? []) map.set(String(it.id), it);
  return Array.from(map.values());
};

const __toReportDT = (val) => (val ? String(val).replace("T", " ") : "");

const __toSQLDate = (dt) => (dt ? String(dt).split("T")[0] : null);

const DEFAULT_ALIASES = {
  pais_id: "pa.pais_id",
  departamento_id: "d.dep_id",
  municipio_id: "m.municipio_id",
  sede_id: "se.sede_id",
  bloque_id: "bl.bloque_id",
  espacio_id: "es.espacio_id",
  almacen_id: "a.almacen_id",
};

function __appendUbicacionConds(conds, src, aliases = DEFAULT_ALIASES) {
  if (!src) return;
  const keys = [
    "pais_id",
    "departamento_id",
    "municipio_id",
    "sede_id",
    "bloque_id",
    "espacio_id",
    "almacen_id",
  ];
  for (const k of keys) {
    const v = src[k];
    if (v !== undefined && v !== null && String(v) !== "") {
      const col = aliases[k] || k;
      conds.push(`AND ${col} = ${Number(v)}`);
    }
  }
}

function __appendRangoFecha(conds, colDate, fecha_inicio, fecha_fin) {
  const ini = __toSQLDate(fecha_inicio);
  const fin = __toSQLDate(fecha_fin);
  if (ini && fin)
    conds.push(`AND ${colDate} BETWEEN "${ini}" AND "${fin}"`);
  else if (ini) conds.push(`AND DATE(${colDate}) >= "${ini}"`);
  else if (fin) conds.push(`AND DATE(${colDate}) <= "${fin}"`);
}

// ============================================================
// Tests: helper functions
// ============================================================
describe("useUbicacionFilters — helpers", () => {
  // ---------- asArray ----------
  describe("asArray", () => {
    it("returns the input if it is already an array", () => {
      const arr = [1, 2, 3];
      expect(asArray(arr)).toBe(arr); // same reference
      expect(asArray([])).toEqual([]);
    });

    it("unwraps { content: [...] } objects", () => {
      const obj = { content: [{ id: 1 }, { id: 2 }] };
      expect(asArray(obj)).toEqual([{ id: 1 }, { id: 2 }]);
    });

    it("returns empty array for null / undefined", () => {
      expect(asArray(null)).toEqual([]);
      expect(asArray(undefined)).toEqual([]);
    });

    it("returns empty array for non-array, non-content objects", () => {
      expect(asArray({})).toEqual([]);
      expect(asArray({ content: "not-an-array" })).toEqual([]);
      expect(asArray(42)).toEqual([]);
      expect(asArray("string")).toEqual([]);
    });
  });

  // ---------- uniqById ----------
  describe("uniqById", () => {
    it("deduplicates by id (last write wins)", () => {
      const input = [
        { id: 1, name: "a" },
        { id: 2, name: "b" },
        { id: 1, name: "c" }, // duplicate id=1
      ];
      const result = uniqById(input);
      expect(result).toHaveLength(2);
      expect(result.find((x) => x.id === 1).name).toBe("c");
    });

    it("returns empty array for empty input", () => {
      expect(uniqById([])).toEqual([]);
    });

    it("handles null / undefined input gracefully", () => {
      expect(uniqById(null)).toEqual([]);
      expect(uniqById(undefined)).toEqual([]);
    });

    it("works with string ids", () => {
      const input = [
        { id: "a", v: 1 },
        { id: "b", v: 2 },
        { id: "a", v: 3 },
      ];
      expect(uniqById(input)).toHaveLength(2);
    });
  });

  // ---------- __toReportDT ----------
  describe("__toReportDT", () => {
    it("replaces T with space in date strings", () => {
      expect(__toReportDT("2024-01-15T10:30:00")).toBe("2024-01-15 10:30:00");
    });

    it("returns empty string for null / undefined / empty", () => {
      expect(__toReportDT(null)).toBe("");
      expect(__toReportDT(undefined)).toBe("");
      expect(__toReportDT("")).toBe("");
    });

    it("passes through strings without T unchanged", () => {
      expect(__toReportDT("2024-01-15")).toBe("2024-01-15");
      expect(__toReportDT("2024-01-15 10:30:00")).toBe("2024-01-15 10:30:00");
    });
  });

  // ---------- __toSQLDate ----------
  describe("__toSQLDate", () => {
    it("extracts YYYY-MM-DD from ISO datetime strings", () => {
      expect(__toSQLDate("2024-01-15T10:30:00")).toBe("2024-01-15");
      expect(__toSQLDate("2024-01-15")).toBe("2024-01-15");
    });

    it("returns null for null / undefined / empty", () => {
      expect(__toSQLDate(null)).toBeNull();
      expect(__toSQLDate(undefined)).toBeNull();
      expect(__toSQLDate("")).toBeNull();
    });
  });

  // ---------- __appendUbicacionConds ----------
  describe("__appendUbicacionConds", () => {
    it("does nothing when src is null/undefined", () => {
      const conds = [];
      __appendUbicacionConds(conds, null);
      expect(conds).toHaveLength(0);
    });

    it("appends conditions for non-empty location ids", () => {
      const conds = [];
      __appendUbicacionConds(conds, { pais_id: 1, municipio_id: 5 });
      expect(conds).toEqual([
        "AND pa.pais_id = 1",
        "AND m.municipio_id = 5",
      ]);
    });

    it("skips keys with empty string values, but includes numeric 0", () => {
      const conds = [];
      __appendUbicacionConds(conds, { pais_id: "", departamento_id: 0 });
      // "" is skipped (empty string), 0 is kept ("0" !== "")
      expect(conds).toEqual(["AND d.dep_id = 0"]);
    });

    it("skips null/undefined values but includes 0 and false", () => {
      const conds = [];
      __appendUbicacionConds(conds, {
        pais_id: 1,
        departamento_id: null,
        municipio_id: undefined,
      });
      expect(conds).toEqual(["AND pa.pais_id = 1"]);
    });

    it("uses custom aliases when provided", () => {
      const conds = [];
      const customAliases = { pais_id: "custom.pais" };
      __appendUbicacionConds(conds, { pais_id: 42 }, customAliases);
      expect(conds).toEqual(["AND custom.pais = 42"]);
    });
  });

  // ---------- __appendRangoFecha ----------
  describe("__appendRangoFecha", () => {
    it("generates BETWEEN when both dates are provided", () => {
      const conds = [];
      __appendRangoFecha(conds, "t.fecha", "2024-01-01", "2024-12-31");
      expect(conds).toEqual([
        'AND t.fecha BETWEEN "2024-01-01" AND "2024-12-31"',
      ]);
    });

    it("generates >= when only fecha_inicio is provided", () => {
      const conds = [];
      __appendRangoFecha(conds, "t.fecha", "2024-01-01", null);
      expect(conds).toEqual(['AND DATE(t.fecha) >= "2024-01-01"']);
    });

    it("generates <= when only fecha_fin is provided", () => {
      const conds = [];
      __appendRangoFecha(conds, "t.fecha", null, "2024-12-31");
      expect(conds).toEqual(['AND DATE(t.fecha) <= "2024-12-31"']);
    });

    it("does nothing when both dates are null/undefined", () => {
      const conds = [];
      __appendRangoFecha(conds, "t.fecha", null, null);
      __appendRangoFecha(conds, "t.fecha", undefined, undefined);
      expect(conds).toHaveLength(0);
    });

    it("extracts date part from datetime strings", () => {
      const conds = [];
      __appendRangoFecha(
        conds,
        "t.fecha",
        "2024-01-15T10:30:00",
        "2024-12-31T23:59:59"
      );
      expect(conds).toEqual([
        'AND t.fecha BETWEEN "2024-01-15" AND "2024-12-31"',
      ]);
    });
  });

  // ---------- DEFAULT_ALIASES ----------
  describe("DEFAULT_ALIASES", () => {
    it("is exported and has the expected mapping", () => {
      expect(mod.DEFAULT_ALIASES).toBeDefined();
      expect(mod.DEFAULT_ALIASES.pais_id).toBe("pa.pais_id");
      expect(mod.DEFAULT_ALIASES.departamento_id).toBe("d.dep_id");
      expect(mod.DEFAULT_ALIASES.municipio_id).toBe("m.municipio_id");
      expect(mod.DEFAULT_ALIASES.sede_id).toBe("se.sede_id");
      expect(mod.DEFAULT_ALIASES.bloque_id).toBe("bl.bloque_id");
      expect(mod.DEFAULT_ALIASES.espacio_id).toBe("es.espacio_id");
      expect(mod.DEFAULT_ALIASES.almacen_id).toBe("a.almacen_id");
    });
  });
});

// ============================================================
// Tests: exported builder functions
// ============================================================
describe("useUbicacionFilters — builders", () => {
  // ---------- buildPedidoCondicionEmpresa ----------
  describe("buildPedidoCondicionEmpresa", () => {
    it("builds conditions with only empresaId (mandatory)", () => {
      const result = mod.buildPedidoCondicionEmpresa({ empresaId: 100 });
      expect(result.condicion).toEqual({
        "0": "p.ped_empresa_id = 100",
      });
    });

    it("includes pedido_id and categoria_estado_id when provided", () => {
      const result = mod.buildPedidoCondicionEmpresa({
        empresaId: 100,
        pedido_id: 42,
        categoria_estado_id: 7,
      });
      const vals = Object.values(result.condicion);
      expect(vals).toContain("p.ped_empresa_id = 100");
      expect(vals).toContain("AND p.ped_id = 42");
      expect(vals).toContain("AND est.est_estado_categoria_id = 7");
    });

    it("includes date range conditions", () => {
      const result = mod.buildPedidoCondicionEmpresa({
        empresaId: 100,
        fecha_inicio: "2024-01-01",
        fecha_fin: "2024-12-31",
      });
      const vals = Object.values(result.condicion);
      expect(vals).toContain(
        'AND p.ped_fecha_hora BETWEEN "2024-01-01" AND "2024-12-31"'
      );
    });

    it("includes ubicacion conditions", () => {
      const result = mod.buildPedidoCondicionEmpresa({
        empresaId: 100,
        ubicacion: { pais_id: 1, sede_id: 3 },
      });
      const vals = Object.values(result.condicion);
      expect(vals).toContain("AND pa.pais_id = 1");
      expect(vals).toContain("AND se.sede_id = 3");
    });

    it("allows overriding column alias", () => {
      const result = mod.buildPedidoCondicionEmpresa({
        empresaId: 100,
        colFecha: "custom.fecha",
        fecha_inicio: "2024-06-01",
        fecha_fin: "2024-06-30",
      });
      const vals = Object.values(result.condicion);
      expect(vals).toContain(
        'AND custom.fecha BETWEEN "2024-06-01" AND "2024-06-30"'
      );
    });
  });

  // ---------- buildPedidoReporteEspecifico ----------
  describe("buildPedidoReporteEspecifico", () => {
    it("builds the report payload with mandatory fields", () => {
      const result = mod.buildPedidoReporteEspecifico({
        empresaId: 100,
        pedido_id: 42,
        categoria_estado_id: 7,
        logoPath: "/logo.png",
      });
      expect(result).toEqual({
        emp_id: 100,
        ped_id: 42,
        categoria_estado_id: 7,
        logo_empresa: "/logo.png",
      });
    });

    it("includes fecha_inicio/fecha_fin when provided", () => {
      const result = mod.buildPedidoReporteEspecifico({
        empresaId: 100,
        pedido_id: 42,
        categoria_estado_id: 7,
        fecha_inicio: "2024-01-15T10:30:00",
        fecha_fin: "2024-12-31T23:59:59",
      });
      expect(result.fecha_inicio).toBe("2024-01-15 10:30:00");
      expect(result.fecha_fin).toBe("2024-12-31 23:59:59");
    });

    it("omits fecha keys when not provided", () => {
      const result = mod.buildPedidoReporteEspecifico({
        empresaId: 100,
        pedido_id: 42,
        categoria_estado_id: 7,
      });
      expect(result).not.toHaveProperty("fecha_inicio");
      expect(result).not.toHaveProperty("fecha_fin");
    });
  });

  // ---------- buildFacturaCondicionEmpresa ----------
  describe("buildFacturaCondicionEmpresa", () => {
    it("builds conditions with empresaId and optional fields", () => {
      const result = mod.buildFacturaCondicionEmpresa({
        empresaId: 200,
        producto_id: 10,
        producto_categoria_id: 3,
      });
      const vals = Object.values(result.condicion);
      expect(vals).toContain("f.emp_id = 200");
      expect(vals).toContain("AND f.prod_id = 10");
      expect(vals).toContain("AND f.prod_cat_id = 3");
    });

    it("includes date range with default colFecha", () => {
      const result = mod.buildFacturaCondicionEmpresa({
        empresaId: 200,
        fecha_inicio: "2024-03-01",
        fecha_fin: "2024-03-31",
      });
      const vals = Object.values(result.condicion);
      expect(vals).toContain(
        'AND f.fact_fecha BETWEEN "2024-03-01" AND "2024-03-31"'
      );
    });
  });

  // ---------- buildKardexCondicionEmpresa ----------
  describe("buildKardexCondicionEmpresa", () => {
    it("builds conditions with empresaId (mandatory)", () => {
      const result = mod.buildKardexCondicionEmpresa({ empresaId: 300 });
      const vals = Object.values(result.condicion);
      expect(vals).toContain("k.emp_id = 300");
    });

    it("includes all optional fields", () => {
      const result = mod.buildKardexCondicionEmpresa({
        empresaId: 300,
        fecha_inicio: "2024-01-01",
        fecha_fin: "2024-06-30",
        categoria_estado_id: 5,
        pedido_id: 99,
        ubicacion: { almacen_id: 7 },
      });
      const vals = Object.values(result.condicion);
      // 0: empresa
      // 1: categoria_estado
      // 2: pedido
      // 3: fecha range
      // 4: almacen ubicacion
      expect(vals).toContain("k.emp_id = 300");
      expect(vals).toContain("AND k.est_categoria_id = 5");
      expect(vals).toContain("AND k.ped_id = 99");
      expect(vals).toContain(
        'AND k.kdx_fecha BETWEEN "2024-01-01" AND "2024-06-30"'
      );
      expect(vals).toContain("AND a.almacen_id = 7");
    });
  });

  // ---------- buildOrdenCompraCondicionEmpresa ----------
  describe("buildOrdenCompraCondicionEmpresa", () => {
    it("builds conditions with empresaId (mandatory)", () => {
      const result = mod.buildOrdenCompraCondicionEmpresa({ empresaId: 400 });
      const vals = Object.values(result.condicion);
      expect(vals).toContain("oc.emp_id = 400");
    });

    it("uses the default colFecha oc.oc_fecha", () => {
      const result = mod.buildOrdenCompraCondicionEmpresa({
        empresaId: 400,
        fecha_inicio: "2024-05-01",
      });
      const vals = Object.values(result.condicion);
      expect(vals).toContain('AND DATE(oc.oc_fecha) >= "2024-05-01"');
    });
  });

  // ---------- buildProductoVencidoCondicionEmpresa ----------
  describe("buildProductoVencidoCondicionEmpresa", () => {
    it("builds conditions with empresaId (mandatory)", () => {
      const result = mod.buildProductoVencidoCondicionEmpresa({ empresaId: 500 });
      const vals = Object.values(result.condicion);
      expect(vals).toContain("pv.emp_id = 500");
    });

    it("uses the default colFecha pv.venc_fecha", () => {
      const result = mod.buildProductoVencidoCondicionEmpresa({
        empresaId: 500,
        fecha_fin: "2024-12-31",
      });
      const vals = Object.values(result.condicion);
      expect(vals).toContain('AND DATE(pv.venc_fecha) <= "2024-12-31"');
    });
  });

  // ---------- Cross-builder consistency ----------
  describe("cross-builder consistency", () => {
    it("all builders return { condicion: {...} } shape", () => {
      const builders = [
        mod.buildPedidoCondicionEmpresa,
        mod.buildFacturaCondicionEmpresa,
        mod.buildKardexCondicionEmpresa,
        mod.buildOrdenCompraCondicionEmpresa,
        mod.buildProductoVencidoCondicionEmpresa,
      ];
      for (const builder of builders) {
        const result = builder({ empresaId: 1 });
        expect(result).toHaveProperty("condicion");
        expect(typeof result.condicion).toBe("object");
      }
    });

    it("buildPedidoReporteEspecifico returns flat object (no condicion wrapper)", () => {
      const result = mod.buildPedidoReporteEspecifico({
        empresaId: 1,
        pedido_id: 1,
        categoria_estado_id: 1,
      });
      expect(result).not.toHaveProperty("condicion");
      expect(result).toHaveProperty("emp_id");
    });
  });
});
