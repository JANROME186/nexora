export function ScheduledDateTimeFields({
  start,
  onStartChange,
  startLabel,
  end,
  onEndChange,
  endLabel,
}: {
  start: string;
  onStartChange: (value: string) => void;
  startLabel: string;
  end: string;
  onEndChange: (value: string) => void;
  endLabel: string;
}) {
  return (
    <>
      <div className="form-field">
        <label htmlFor="appointment-start">{startLabel}</label>
        <input
          id="appointment-start"
          type="datetime-local"
          value={start}
          onChange={(event) => onStartChange(event.target.value)}
          required
        />
      </div>
      <div className="form-field">
        <label htmlFor="appointment-end">{endLabel}</label>
        <input
          id="appointment-end"
          type="datetime-local"
          value={end}
          onChange={(event) => onEndChange(event.target.value)}
          required
        />
      </div>
    </>
  );
}
