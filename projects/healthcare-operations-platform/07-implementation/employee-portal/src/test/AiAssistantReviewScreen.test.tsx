import { useEffect, useRef, type ReactNode } from "react";
import { afterEach, describe, expect, it, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { AiAssistantReviewScreen } from "../components/screens/AiAssistantReviewScreen";
import { LocaleProvider } from "../i18n/LocaleContext";
import { AdminScopeProvider, useAdminScope } from "../state/AdminScopeContext";
import * as api from "../api/aiOverlayApi";

function ScopeSetter() {
  const { setTenantId } = useAdminScope();
  const initialized = useRef(false);
  useEffect(() => {
    if (initialized.current) return;
    initialized.current = true;
    setTenantId("tenant-1");
  }, [setTenantId]);
  return null;
}

function Harness({ children }: { children: ReactNode }) {
  return (
    <LocaleProvider>
      <AdminScopeProvider>
        <ScopeSetter />
        {children}
      </AdminScopeProvider>
    </LocaleProvider>
  );
}

const REVIEW_REQUIRED_INTERACTION = {
  sessionId: "ai-session-1",
  tenantId: "tenant-1",
  actorId: "user-1",
  purpose: "result_case_summary",
  sourceContextType: "result",
  sourceContextId: "result-1001",
  draftOutput: "Hemoglobin is within reference range. Follow-up is not urgent.",
  citations: ["result:result-1001", "knowledge_base:range-policy"],
  confidenceBand: "medium",
  safetyDecision: "allowed_with_human_review",
  reviewStatus: "human_review_required",
  modelProviderRef: "local-deterministic",
  modelNameRef: "nexora-local-fixture",
  policyVersion: "AI-POLICY-2026-07",
  lifecycleStatus: "generated",
};

describe("AiAssistantReviewScreen", () => {
  afterEach(() => {
    vi.restoreAllMocks();
    window.localStorage.clear();
  });

  it("requests a cited draft and then records human review", async () => {
    vi.spyOn(api, "requestAssistantDraft").mockResolvedValue(REVIEW_REQUIRED_INTERACTION);
    vi.spyOn(api, "reviewAssistantDraft").mockResolvedValue({
      ...REVIEW_REQUIRED_INTERACTION,
      reviewStatus: "accepted",
      reviewerId: "reviewer-1",
      reviewReason: "Reviewed against citations and source context.",
      lifecycleStatus: "archived",
    });
    const user = userEvent.setup();

    render(
      <Harness>
        <AiAssistantReviewScreen />
      </Harness>,
    );

    await user.click(screen.getByRole("button", { name: "Solicitar borrador" }));

    expect(await screen.findByText("Borrador generado para revisión humana.")).toBeInTheDocument();
    expect(screen.getByText("result:result-1001")).toBeInTheDocument();
    expect(screen.getByRole("button", { name: "Guardar revisión" })).toBeEnabled();

    await user.click(screen.getByRole("button", { name: "Guardar revisión" }));

    expect(await screen.findByText("Revisión guardada.")).toBeInTheDocument();
    expect(api.reviewAssistantDraft).toHaveBeenCalledWith(
      "ai-session-1",
      expect.objectContaining({ decision: "accepted", reviewerId: "reviewer-1" }),
    );
  });

  it("blocks review when the selected draft has no citations", async () => {
    vi.spyOn(api, "requestAssistantDraft").mockResolvedValue({
      ...REVIEW_REQUIRED_INTERACTION,
      citations: [],
    });
    const reviewSpy = vi.spyOn(api, "reviewAssistantDraft");
    const user = userEvent.setup();

    render(
      <Harness>
        <AiAssistantReviewScreen />
      </Harness>,
    );

    await user.click(screen.getByRole("button", { name: "Solicitar borrador" }));

    expect(
      await screen.findByText("La revisión está bloqueada hasta que el borrador tenga citas."),
    ).toBeInTheDocument();
    expect(screen.getByRole("button", { name: "Guardar revisión" })).toBeDisabled();
    expect(reviewSpy).not.toHaveBeenCalled();
  });

  it("loads audit records and allows selecting a prior session", async () => {
    vi.spyOn(api, "listAssistantAuditRecords").mockResolvedValue([REVIEW_REQUIRED_INTERACTION]);
    const user = userEvent.setup();

    render(
      <Harness>
        <AiAssistantReviewScreen />
      </Harness>,
    );

    await user.click(screen.getByRole("button", { name: "Cargar auditoría" }));
    await user.click(await screen.findByRole("button", { name: "ai-session-1" }));

    expect(screen.getByText("Auditoría cargada.")).toBeInTheDocument();
    expect(
      screen.getByText("Hemoglobin is within reference range. Follow-up is not urgent."),
    ).toBeInTheDocument();
  });
});
