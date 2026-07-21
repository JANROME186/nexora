interface ContactFieldsProps {
  idPrefix: string;
  fullName: string;
  onFullNameChange: (value: string) => void;
  fullNameLabel: string;
  phone: string;
  onPhoneChange: (value: string) => void;
  phoneLabel: string;
  email: string;
  onEmailChange: (value: string) => void;
  emailLabel: string;
  contactRequiredHint: string;
}

/** Shared full name / phone / email fields for the appointment and quotation request forms. */
export function ContactFields({
  idPrefix,
  fullName,
  onFullNameChange,
  fullNameLabel,
  phone,
  onPhoneChange,
  phoneLabel,
  email,
  onEmailChange,
  emailLabel,
  contactRequiredHint,
}: ContactFieldsProps) {
  const hintId = `${idPrefix}-contact-hint`;

  return (
    <>
      <div className="form-field">
        <label htmlFor={`${idPrefix}-full-name`}>{fullNameLabel}</label>
        <input
          id={`${idPrefix}-full-name`}
          type="text"
          value={fullName}
          onChange={(event) => onFullNameChange(event.target.value)}
          required
        />
      </div>
      <div className="form-field">
        <label htmlFor={`${idPrefix}-phone`}>{phoneLabel}</label>
        <input
          id={`${idPrefix}-phone`}
          type="tel"
          value={phone}
          onChange={(event) => onPhoneChange(event.target.value)}
          aria-describedby={hintId}
        />
      </div>
      <div className="form-field">
        <label htmlFor={`${idPrefix}-email`}>{emailLabel}</label>
        <input
          id={`${idPrefix}-email`}
          type="email"
          value={email}
          onChange={(event) => onEmailChange(event.target.value)}
          aria-describedby={hintId}
        />
      </div>
      <p id={hintId} className="field-hint">
        {contactRequiredHint}
      </p>
    </>
  );
}
