import { useState, type FormEvent } from "react";
import { submitAppointmentRequest } from "../api/careDeliveryApi";
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
import { ScheduledDateTimeFields } from "../components/common/ScheduledDateTimeFields";
import { siteConfig } from "../config/siteConfig";
import { useLocale } from "../i18n/LocaleContext";
import { usePageMeta } from "../seo/usePageMeta";
import { useAsyncAction } from "../state/useAsyncAction";
import { useCatalogItemOptions } from "../state/useCatalogItemOptions";
import { useRateLimitCooldown } from "../state/useRateLimitCooldown";

export function AppointmentRequestPage() {
  const { t, locale } = useLocale();
  usePageMeta("appointmentTitle", "appointmentDescription");
  const { options } = useCatalogItemOptions(locale);
  const cooldown = useRateLimitCooldown();
  const submitAction = useAsyncAction(submitAppointmentRequest);
  const f = t.appointmentForm;

  const [fullName, setFullName] = useState("");
  const [phone, setPhone] = useState("");
  const [email, setEmail] = useState("");
  const [branchId, setBranchId] = useState(siteConfig.branches[0]?.branchId ?? "");
  const [scheduledStart, setScheduledStart] = useState("");
  const [scheduledEnd, setScheduledEnd] = useState("");
  const [items, setItems] = useState<CatalogItemLine[]>([]);
  const [consent, setConsent] = useState(false);
  const [validationMessage, setValidationMessage] = useState("");

  const resetForm = () => {
    setFullName("");
    setPhone("");
    setEmail("");
    setScheduledStart("");
    setScheduledEnd("");
    setItems([]);
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
    if (items.length === 0) {
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
      scheduledStart: new Date(scheduledStart).toISOString(),
      scheduledEnd: new Date(scheduledEnd).toISOString(),
      requestedItems: items.map((item) => ({
        testDefinitionId: item.testDefinitionId,
        catalogItemKind: item.catalogItemKind,
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
      <section aria-labelledby="appointment-success-heading" className="form-success">
        <h1 id="appointment-success-heading">{f.successTitle}</h1>
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
    <section aria-labelledby="appointment-heading">
      <h1 id="appointment-heading">{f.title}</h1>
      <p>{f.intro}</p>
      <form onSubmit={handleSubmit} noValidate>
        <ContactFields
          idPrefix="appointment"
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
          id="appointment-branch"
          label={f.branchLabel}
          locale={locale}
          value={branchId}
          onChange={setBranchId}
        />
        <ScheduledDateTimeFields
          start={scheduledStart}
          onStartChange={setScheduledStart}
          startLabel={f.scheduledStartLabel}
          end={scheduledEnd}
          onEndChange={setScheduledEnd}
          endLabel={f.scheduledEndLabel}
        />
        <fieldset className="form-field">
          <legend>{f.itemsLabel}</legend>
          <p className="field-hint">{f.itemsHelp}</p>
          <CatalogItemLinesField
            fieldId="appointment-item-picker"
            options={options}
            lines={items}
            onChange={setItems}
            addLabel={f.addItem}
            removeLabel={f.removeItem}
            selectPlaceholder={t.itemPicker.selectPlaceholder}
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
