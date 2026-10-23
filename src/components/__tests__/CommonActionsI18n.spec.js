import { describe, expect, it } from "vitest";
import i18next from "i18next";
import enCommon from "../../locales/en/common.json";
import esCommon from "../../locales/es/common.json";

const createI18n = async (language, common) => {
  const instance = i18next.createInstance();
  await instance.init({
    lng: language,
    fallbackLng: false,
    resources: { [language]: { translation: { common } } },
    interpolation: { escapeValue: false },
  });
  return instance;
};

describe("common action translations", () => {
  it("resolves activate and inactivate actions through i18n keys in English", async () => {
    const i18n = await createI18n("en", enCommon);

    expect(i18n.t("common.actions.activate")).toBe("Activate");
    expect(i18n.t("common.actions.inactivate")).toBe("Inactivate");
  });

  it("resolves activate and inactivate actions through i18n keys in Spanish", async () => {
    const i18n = await createI18n("es", esCommon);

    expect(i18n.t("common.actions.activate")).toBe("Activar");
    expect(i18n.t("common.actions.inactivate")).toBe("Inactivar");
  });
});
