import React from "react";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { ThemeProvider, createTheme } from "@mui/material/styles";
import axios from "../axiosConfig";

vi.mock("../axiosConfig", () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
  },
}));

vi.mock("react-i18next", () => ({
  useTranslation: () => ({
    t: (key, fallback) => (typeof fallback === "string" ? fallback : key),
  }),
}));

import FormSede from "../sede/FormSede.jsx";

const theme = createTheme({ palette: { mode: "light" } });

const renderFormSede = () =>
  render(
    <ThemeProvider theme={theme}>
      <FormSede
        open
        grupos={[]}
        tiposSede={[]}
        authHeaders={{ headers: { Authorization: "Bearer test" } }}
      />
    </ThemeProvider>
  );

const chooseSelectOption = async (user, name, optionName) => {
  const input = document.querySelector(`input[name="${name}"]`);
  const combobox = input?.closest(".MuiInputBase-root")?.querySelector('[role="combobox"]');

  expect(combobox).not.toBeNull();
  await user.click(combobox);
  await user.click(await screen.findByRole("option", { name: optionName }));
};

describe("Issue Sede - cascada geográfica", () => {
  beforeEach(() => {
    vi.clearAllMocks();
    axios.get.mockImplementation((url) => {
      if (url === "/v1/items/pais/0") {
        return Promise.resolve({ data: [{ id: 57, name: "Colombia" }] });
      }
      if (url === "/v1/items/departamento/57") {
        return Promise.resolve({ data: [{ id: 41, name: "Huila" }] });
      }
      if (url === "/v1/items/municipio/41") {
        return Promise.resolve({ data: [{ id: 41001, name: "Neiva" }] });
      }
      return Promise.resolve({ data: [] });
    });
  });

  it("no marca un departamento válido como ajeno al país seleccionado", async () => {
    const user = userEvent.setup();
    renderFormSede();

    await waitFor(() => {
      expect(axios.get).toHaveBeenCalledWith("/v1/items/pais/0", expect.any(Object));
    });

    await chooseSelectOption(user, "paisId", "Colombia");

    await waitFor(() => {
      expect(axios.get).toHaveBeenCalledWith(
        "/v1/items/departamento/57",
        expect.any(Object)
      );
    });

    await chooseSelectOption(user, "deptoId", "Huila");
    await user.click(screen.getByRole("button", { name: "common.actions.save" }));

    await waitFor(() => {
      expect(
        screen.queryByText("sede.form.validation.departmentMismatch")
      ).not.toBeInTheDocument();
    });
  });
});
