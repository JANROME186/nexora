import { useState, type FormEvent } from "react";
import { submitQuotationRequest } from "../api/careDeliveryApi";
import { ApiError } from "../api/httpClient";
import { BranchSelect } from "../components/common/BranchSelect";
import {
  CatalogItemLinesField,
  type CatalogItemLine,
} from "../components/common/CatalogItemLinesField";
import { ContactFields } from "../components/common/ContactFields";
import {
  ConsentCheckbox,
  FormMessages,
  SubmitButtonWithCooldown,
} from "../components/common/FormSubmitControls";
import { resolveErrorMessage } from "../components/common/resolveErrorMessage";
import { siteConfig } from "../config/siteConfig";
import { useLocale } from "../i18n/LocaleContext";
import { usePageMeta } from "../seo/usePageMeta";
import { useAsyncAction } from "../state/useAsyncAction";
import { useCatalogItemOptions } from "../state/useCatalogItemOptions";
import { useRateLimitCooldown } from "../state/useRateLimitCooldown";

export function QuotationRequestPage() {
  const { t, locale } = useLocale();
  usePageMeta("quotationTitle", "quotationDescription");
  const { options } = useCatalogItemOptions(locale);
  const cooldown = useRateLimitCooldown();
  const submitAction = useAsyncAction(submitQuotationRequest);
  const f = t.quotationForm;

  const [fullName, setFullName] = useState("");
  const [phone, setPhone] = useState("");
  const [email, setEmail] = useState("");
  const [branchId, setBranchId] = useState(siteConfig.branches[0]?.branchId ?? "");
  const [lines, setLines] = useState<CatalogItemLine[]>([]);
  const [consent, setConsent] = useState(false);
  const [validationMessage, setValidationMessage] = useState("");

  const resetForm = () => {
    setFullName("");
    setPhone("");
    setEmail("");
    setLines([]);
    setConsent(false);
    setValidationMessage("");
    submitAction.reset();
  };

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setValidationMessage("");

    if (!phone.trim() && !email.trim()) {
      setValidationMessage(t.errors.validationContactRequired);
      return;
    }
    if (lines.length === 0) {
      setValidationMessage(t.errors.validationSelectAtLeastOneItem);
      return;
    }
    if (!consent) {
      setValidationMessage(t.errors.validationRequired);
      return;
    }

    const result = await submitAction.run({
      tenantId: siteConfig.tenantId,
      laboratoryId: siteConfig.laboratoryId,
      branchId,
      prospectiveFullName: fullName || undefined,
      prospectivePhone: phone || undefined,
      prospectiveEmail: email || undefined,
      lines: lines.map((line) => ({
        testDefinitionId: line.testDefinitionId,
        catalogItemKind: line.catalogItemKind,
        quantity: line.quantity,
      })),
    });

    if (!result.ok) {
      const { error } = result;
      if (error instanceof ApiError && error.isRateLimited) {
        cooldown.start();
      }
    }
  };

  if (submitAction.status === "success") {
    return (
      <section aria-labelledby="quotation-success-heading" className="form-success">
        <h1 id="quotation-success-heading">{f.successTitle}</h1>
        <p>{f.successBody}</p>
        <button type="button" className="btn btn--primary" onClick={resetForm}>
          {f.submitAnother}
        </button>
      </section>
    );
  }

  const isSubmitting = submitAction.status === "loading";
  const errorMessage =
    submitAction.status === "error" ? resolveErrorMessage(submitAction.error, t.errors) : "";

  return (
    <section aria-labelledby="quotation-heading">
      <h1 id="quotation-heading">{f.title}</h1>
      <p>{f.intro}</p>
      <form onSubmit={handleSubmit} noValidate>
        <ContactFields
          idPrefix="quotation"
          fullName={fullName}
          onFullNameChange={setFullName}
          fullNameLabel={f.fullNameLabel}
          phone={phone}
          onPhoneChange={setPhone}
          phoneLabel={f.phoneLabel}
          email={email}
          onEmailChange={setEmail}
          emailLabel={f.emailLabel}
          contactRequiredHint={f.contactRequiredHint}
        />
        <BranchSelect
          id="quotation-branch"
          label={f.branchLabel}
          locale={locale}
          value={branchId}
          onChange={setBranchId}
        />
        <fieldset className="form-field">
          <legend>{f.linesLabel}</legend>
          <p className="field-hint">{f.linesHelp}</p>
          <CatalogItemLinesField
            fieldId="quotation-item-picker"
            options={options}
            lines={lines}
            onChange={setLines}
            showQuantity
            addLabel={f.addLine}
            removeLabel={f.removeLine}
            selectPlaceholder={t.itemPicker.selectPlaceholder}
            quantityLabel={f.quantityLabel}
          />
        </fieldset>
        <ConsentCheckbox checked={consent} onChange={setConsent} label={f.consentLabel} />
        <FormMessages validationMessage={validationMessage} errorMessage={errorMessage} />
        <SubmitButtonWithCooldown
          isSubmitting={isSubmitting}
          isCoolingDown={cooldown.isActive}
          remainingSeconds={cooldown.remainingSeconds}
          submitLabel={f.submit}
          submittingLabel={f.submitting}
          rateLimitMessage={t.errors.PUBLIC_RATE_LIMIT_EXCEEDED}
        />
      </form>
    </section>
  );
}
