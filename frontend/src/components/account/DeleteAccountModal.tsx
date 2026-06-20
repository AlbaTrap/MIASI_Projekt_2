import { AppButton } from '../ui/AppButton';

type DeleteAccountModalProps = {
  open: boolean;
  error: string;
  isSubmitting: boolean;
  onCancel: () => void;
  onConfirm: () => void;
};

export function DeleteAccountModal({
  open,
  error,
  isSubmitting,
  onCancel,
  onConfirm,
}: DeleteAccountModalProps) {
  if (!open) {
    return null;
  }

  return (
    <div className="modal-backdrop" role="presentation">
      <section className="modal-panel">
        <h2>Usuń konto</h2>
        <p>
          Konto zostanie oznaczone jako usunięte. Po tej operacji nie będzie można
          zalogować się na to konto.
        </p>
        {error ? <p className="form-error">{error}</p> : null}
        <div className="button-row">
          <AppButton disabled={isSubmitting} onClick={onCancel} variant="secondary">
            Anuluj
          </AppButton>
          <AppButton disabled={isSubmitting} onClick={onConfirm} variant="danger">
            Potwierdź usunięcie
          </AppButton>
        </div>
      </section>
    </div>
  );
}
