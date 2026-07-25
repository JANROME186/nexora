import type { ReactNode } from "react";
import { describe, expect, it, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { MarketplaceOffersScreen } from "../components/screens/MarketplaceOffersScreen";
import { LocaleProvider } from "../i18n/LocaleContext";
import { AdminScopeProvider } from "../state/AdminScopeContext";
import * as api from "../api/marketplaceApi";

function Harness({ children }: { children: ReactNode }) {
  return (
    <LocaleProvider>
      <AdminScopeProvider>{children}</AdminScopeProvider>
    </LocaleProvider>
  );
}

const OFFER = {
  offerId: "offer-1",
  packageId: "pkg-1",
  packageVersion: "1.0.0",
  offerCode: "STANDARD-TIER",
  offerType: "subscription",
  lifecycleStatus: "published",
  tierCodes: ["standard"],
  trialPeriodDays: 14,
  effectiveVersion: 1,
};

describe("MarketplaceOffersScreen", () => {
  it("publishes an offer and loads the offers list", async () => {
    vi.spyOn(api, "publishOffer").mockResolvedValue(OFFER);
    vi.spyOn(api, "listOffers").mockResolvedValue([OFFER]);
    const user = userEvent.setup();

    render(
      <Harness>
        <MarketplaceOffersScreen />
      </Harness>,
    );

    await user.type(screen.getByLabelText("ID del paquete"), "pkg-1");
    await user.type(screen.getByLabelText("Versión del paquete"), "1.0.0");
    await user.type(screen.getByLabelText("Código de oferta"), "STANDARD-TIER");
    await user.type(screen.getByLabelText("Tipo de oferta"), "subscription");
    await user.type(screen.getByLabelText("Códigos de nivel (separados por coma)"), "standard");
    await user.type(
      screen.getByLabelText("ID del actor", { selector: "#mkt-offer-publish-actor" }),
      "admin-1",
    );
    await user.click(screen.getByRole("button", { name: "Publicar oferta" }));

    expect(await screen.findByText("Oferta publicada.")).toBeInTheDocument();
    expect(api.publishOffer).toHaveBeenCalledWith(
      expect.objectContaining({
        packageId: "pkg-1",
        offerCode: "STANDARD-TIER",
        tierCodes: ["standard"],
      }),
    );

    await user.click(screen.getByRole("button", { name: "Cargar ofertas" }));
    expect(await screen.findByText("STANDARD-TIER")).toBeInTheDocument();
  });

  it("accepts a selected offer for a tenant", async () => {
    vi.spyOn(api, "listOffers").mockResolvedValue([OFFER]);
    vi.spyOn(api, "acceptOffer").mockResolvedValue({
      offerId: "offer-1",
      tenantId: "tenant-1",
      entitlementId: "ent-1",
      grantedAt: "2026-07-25T00:00:00Z",
    });
    const user = userEvent.setup();

    render(
      <Harness>
        <MarketplaceOffersScreen />
      </Harness>,
    );

    await user.click(screen.getByRole("button", { name: "Cargar ofertas" }));
    await user.click(await screen.findByRole("button", { name: "STANDARD-TIER" }));

    await user.type(screen.getByLabelText("ID de tenant"), "tenant-1");
    await user.type(
      screen.getByLabelText("ID del actor", { selector: "#mkt-offer-accept-actor" }),
      "admin-1",
    );
    await user.click(screen.getByRole("button", { name: "Aceptar oferta" }));

    expect(await screen.findByText("Oferta aceptada.")).toBeInTheDocument();
    expect(api.acceptOffer).toHaveBeenCalledWith(
      "offer-1",
      expect.objectContaining({ tenantId: "tenant-1", actorId: "admin-1" }),
    );
  });
});
