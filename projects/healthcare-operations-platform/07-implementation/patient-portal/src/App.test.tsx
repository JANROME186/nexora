import { render, screen } from "@testing-library/react";
import { describe, it, expect, vi } from "vitest";
import App from "./App";

vi.mock("./api/patientResultHistoryApi", () => ({
  getPatientHistory: vi.fn().mockResolvedValue({
    patientId: "test",
    entries: [],
  }),
}));

describe("Patient Portal App", () => {
  it("renders the app correctly", () => {
    render(<App />);
    expect(screen.getByText("HOP Patient Portal")).toBeInTheDocument();
  });
});
