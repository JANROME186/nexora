export function ConsentCheckbox({
  checked,
  onChange,
  label,
}: {
  checked: boolean;
  onChange: (checked: boolean) => void;
  label: string;
}) {
  return (
    <div className="form-field form-field--checkbox">
      <label>
        <input
          type="checkbox"
          checked={checked}
          onChange={(event) => onChange(event.target.checked)}
          required
        />
        {label}
      </label>
    </div>
  );
}

export function FormMessages({
  validationMessage,
  errorMessage,
}: {
  validationMessage: string;
  errorMessage: string;
}) {
  return (
    <>
      {validationMessage && (
        <p role="alert" className="form-error">
          {validationMessage}
        </p>
      )}
      {errorMessage && (
        <p role="alert" className="form-error">
          {errorMessage}
        </p>
      )}
    </>
  );
}

export function SubmitButtonWithCooldown({
  isSubmitting,
  isCoolingDown,
  remainingSeconds,
  submitLabel,
  submittingLabel,
  rateLimitMessage,
}: {
  isSubmitting: boolean;
  isCoolingDown: boolean;
  remainingSeconds: number;
  submitLabel: string;
  submittingLabel: string;
  rateLimitMessage: string;
}) {
  return (
    <>
      <button type="submit" className="btn btn--primary" disabled={isSubmitting || isCoolingDown}>
        {isSubmitting ? submittingLabel : submitLabel}
      </button>
      {isCoolingDown && (
        <p role="status" className="field-hint">
          {rateLimitMessage} ({remainingSeconds}s)
        </p>
      )}
    </>
  );
}
